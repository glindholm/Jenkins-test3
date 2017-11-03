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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.store.PersistenceAdapter;
import org.junit.Test;

public class BaseEmbeddedMessageBrokerTest
{
	private static final String BROKER_NAME = "TheBrokerName";
	
	public BaseEmbeddedMessageBrokerTest()
	{
	}
	
	@Test
	public void testName() throws Exception
	{
		TestBroker broker = new TestBroker();
		assertEquals(BROKER_NAME, broker.getBrokerName());
	}
	
	@Test
	public void testStartStop() throws Exception
	{
		TestBroker broker = new TestBroker();
		assertFalse(broker.isRunning());
		broker.startBroker(true);
		assertTrue(broker.isRunning());
		
		assertEquals(BROKER_NAME, broker.getBrokerService().getBrokerName());
		
		broker.stopBroker(true);
		assertFalse(broker.isRunning());
	}
	
	@Test
	public void testDoubleStartStopBenign() throws Exception
	{
		TestBroker broker = new TestBroker();
		assertFalse(broker.isRunning());
		broker.startBroker(true);
		assertTrue(broker.isRunning());
		broker.startBroker(true);
		assertTrue(broker.isRunning());
		
		broker.stopBroker(true);
		assertFalse(broker.isRunning());
		broker.stopBroker(true);
		assertFalse(broker.isRunning());
	}
	
	@Test
	public void testStartStopNoWait() throws Exception
	{
		TestBroker broker = new TestBroker();
		assertFalse(broker.isRunning());
		broker.startBroker(false);
		assertTrue(broker.isRunning());

		BrokerService internalService = broker.getBrokerService();
		assertTrue(internalService.waitUntilStarted());
		
		broker.stopBroker(false);
		assertFalse(broker.isRunning());
		
		internalService.waitUntilStopped();
		assertFalse(internalService.isStarted());
	}
	
	@Test
	public void testExceptionDuringStartup() throws Exception
	{
		TestBroker broker = new TestBroker();
		broker.setStartException(new NullPointerException());
		try
		{
			broker.startBroker(true);
			fail("Didn't throw");
		}
		catch(NullPointerException e)
		{
		}
		
		assertNull(broker.getBrokerService());
		assertFalse(broker.isRunning());
	}
	
	@Test
	public void testCanSendAndReceive() throws Exception
	{
		ActiveMQQueue destination = new ActiveMQQueue("testQueue");

		TestBroker broker = new TestBroker();
		assertFalse(broker.isRunning());
		broker.startBroker(true);
		assertTrue(broker.isRunning());
		
		// create a connection factor, preventing it from automatically creating
		// its own broker
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(broker.getConnectors()[0]+"?create=false");
		Connection connection = factory.createConnection();	// note - this line can be slow
		connection.start();
		
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		
		MessageProducer producer = session.createProducer(destination);
		producer.send(session.createTextMessage("abc"));
		
		MessageConsumer consumer = session.createConsumer(destination);
		Message message = consumer.receive();
		
		assertTrue(message instanceof TextMessage);
		TextMessage tm = (TextMessage)message;
		assertEquals("abc", tm.getText());
		
		session.close();
		connection.close();
		
		broker.stopBroker(true);
		assertFalse(broker.isRunning());
	}
	
	private static class TestBroker extends BaseEmbeddedMessageBroker
	{
		private Exception startException;
		
		public TestBroker()
		{
			super(BROKER_NAME);
		}
		
		public void setStartException(Exception startException)
		{
			this.startException = startException;
		}
		
		public BrokerService getBrokerService()
		{
			return super.getBrokerService();
		}

		@Override
		protected PersistenceAdapter createPersistenceAdapter()
				throws Exception
		{
			if (startException != null)
			{
				throw startException;
			}
			
			return null;
		}

		@Override
		public String[] getConnectors()
		{
			return new String[] { "vm://" + BROKER_NAME };
		}
		
		@Override
		protected void preStart(BrokerService service) throws Exception
		{
			// disable JMX
			service.setUseJmx(false);
			
            service.getSystemUsage().getMemoryUsage().setLimit(1024 * 1024 * 20); // 20 MB
            service.getSystemUsage().getStoreUsage().setLimit(1024 * 1024 * 100); // 100 MB
            service.getSystemUsage().getTempUsage().setLimit(1024 * 1024 * 100); // 100 MB

			super.preStart(service);
		}
	}
}
