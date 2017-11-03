/*
 *  Copyright (c) 2011 Kevin Hunter
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 *  you may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at 
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0 
 *  
 *  Unless required by applicable law or agreed to in writing, software 
 *  distributed under the License is distributed on an "AS IS" BASIS, 
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 *  See the License for the specific language governing permissions and 
 *  limitations under the License. 
 */

package net.sourceforge.wsup.messaging.activemq;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.store.PersistenceAdapter;
import org.apache.activemq.store.amq.AMQPersistenceAdapter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class VMMessageBrokerAMQTest
{
	private static final String PERSIST_PATH = "target/VMMessageBrokerAMQTest";
	
	public VMMessageBrokerAMQTest()
	{
	}
	
	@Before
	public void setup()
	{
		File dir = new File(PERSIST_PATH);
		if (dir.exists())
		{
			recursiveDeleteDirectory(dir);
		}
	}
	
	@After
	public void tearDown()
	{
		File dir = new File(PERSIST_PATH);
		if (dir.exists())
		{
			recursiveDeleteDirectory(dir);
		}
	}
	
	private void recursiveDeleteDirectory(File dir)
	{
		File[] children = dir.listFiles();
		for (File child : children)
		{
			if (child.isDirectory())
			{
				recursiveDeleteDirectory(child);
			}
			else
			{
				child.delete();
			}
		}
		
		dir.delete();
	}
	
	@Test
	public void testConnectorsAndBrokerURI()
	{
		TestBroker broker = new TestBroker("VMMessageBrokerAMQTest");
		assertEquals("VMMessageBrokerAMQTest", broker.getBrokerName());
		String[] connectors = broker.getConnectors();
		assertEquals(1, connectors.length);
		assertEquals("vm://VMMessageBrokerAMQTest", connectors[0]);
		assertEquals("vm://VMMessageBrokerAMQTest?create=false", broker.getBrokerURI());
	}
	
	@Test
	public void testNoFileNoPersistence()
	{
		TestBroker broker = new TestBroker("test");
		assertNull(broker.createPersistenceAdapter());
	}
	
	@Test
	public void testYesFileYesPersistence()
	{
		TestBroker broker = new TestBroker("test");
		File target = new File(PERSIST_PATH);
		broker.setPersistenceRootDir(target);
		
		AMQPersistenceAdapter adapter = (AMQPersistenceAdapter)broker.createPersistenceAdapter();
		assertNotNull(adapter);
		assertEquals(target, adapter.getDirectory());
	}
	
    @Test
    public void testPositiveMessageLengthGetsSet()
    {
        TestBroker broker = new TestBroker("test");
        File target = new File(PERSIST_PATH);
        broker.setPersistenceRootDir(target);
        broker.setMaxDataFileLength(987654);
        
        AMQPersistenceAdapter adapter = (AMQPersistenceAdapter)broker.createPersistenceAdapter();
        assertNotNull(adapter);
        assertEquals(target, adapter.getDirectory());
        assertEquals(987654, adapter.getMaxFileLength());
    }
    
    @Test
    public void testNegativeMessageLengthDoesntGetSet()
    {
        TestBroker broker = new TestBroker("test");
        File target = new File(PERSIST_PATH);
        broker.setPersistenceRootDir(target);
        broker.setMaxDataFileLength(-987654);
        
        AMQPersistenceAdapter adapter = (AMQPersistenceAdapter)broker.createPersistenceAdapter();
        assertNotNull(adapter);
        assertEquals(target, adapter.getDirectory());
        assertTrue(adapter.getMaxFileLength() >= 0);
    }
    
    @Test
    public void testTooBigMessageLengthDoesntGetSet()
    {
        TestBroker broker = new TestBroker("test");
        File target = new File(PERSIST_PATH);
        broker.setPersistenceRootDir(target);
        broker.setMaxDataFileLength((long)Integer.MAX_VALUE + 2);
        
        AMQPersistenceAdapter adapter = (AMQPersistenceAdapter)broker.createPersistenceAdapter();
        assertNotNull(adapter);
        assertEquals(target, adapter.getDirectory());
        assertTrue(adapter.getMaxFileLength() >= 0);
    }
    
	@Test
	public void testPersistenceDirectoryGetsCreated() throws Exception
	{
	    VMMessageBrokerAMQ broker = new TestBroker("test");
		File target = new File(PERSIST_PATH);
		broker.setPersistenceRootDir(target);
		
		assertFalse(target.exists());
		
		broker.startBroker(true);
		broker.stopBroker(true);
		
		assertTrue(target.exists());
		assertTrue(target.isDirectory());
	}
	
	@Test
	public void testPersistenceWorks() throws Exception
	{
        File target = new File(PERSIST_PATH);
        ActiveMQQueue destination = new ActiveMQQueue("testQueue");

        VMMessageBrokerAMQ broker = new TestBroker("test");
        broker.setPersistenceRootDir(target);
        broker.startBroker(true);
        
        Connection connection = broker.createConnection();
        connection.start();
        
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        
        MessageProducer producer = session.createProducer(destination);
        producer.send(session.createTextMessage("abc"));
        
        session.close();
        connection.close();
        broker.stopBroker(true);
        
        /*
         * Never received the message - at this point, it should have been
         * persisted so that we can pick it up when we restart a broker.
         */
        
        broker = new TestBroker("test");
        broker.setPersistenceRootDir(target);
        broker.startBroker(true);
        
        connection = broker.createConnection();
        connection.start();
        
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer(destination);
        Message message = consumer.receive();
        
        assertTrue(message instanceof TextMessage);
        TextMessage tm = (TextMessage)message;
        assertEquals("abc", tm.getText());
        
        session.close();
        connection.close();
        broker.stopBroker(true);
	}

	/*
     * Class to allow access to some protected methods
     */
	private static class TestBroker extends VMMessageBrokerAMQ
	{
		public TestBroker(String brokerName)
		{
			super(brokerName);
		}

		@Override
		public PersistenceAdapter createPersistenceAdapter()
		{
			return super.createPersistenceAdapter();
		}

		@Override
		public String[] getConnectors()
		{
			return super.getConnectors();
		}

        @Override
        protected void preStart(BrokerService service) throws Exception
        {
            super.preStart(service);
            
            service.getSystemUsage().getMemoryUsage().setLimit(1024 * 1024 * 20); // 20 MB
            service.getSystemUsage().getStoreUsage().setLimit(1024 * 1024 * 100); // 100 MB
            service.getSystemUsage().getTempUsage().setLimit(1024 * 1024 * 100); // 100 MB

        }
		
	}
}

