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

package net.sourceforge.wsup.messaging.jms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import net.sourceforge.wsup.messaging.activemq.VMMessageBrokerKaha;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.store.PersistenceAdapter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class BaseAsyncMessageReceiverTest
{
	private static ActiveMQQueue destination;
	private static VMMessageBrokerKaha broker;
	
	private Connection connection;
	
	public BaseAsyncMessageReceiverTest()
	{
	}
	
	@BeforeClass
	public static void oneTimeSetup() throws Exception
	{
		destination = new ActiveMQQueue("testQueue");
		
		broker = new TestBroker("test");
		
		broker.startBroker(true);
	}
	
	@AfterClass
	public static void oneTimeTearDown() throws Exception
	{
		broker.stopBroker(true);
	}
	
	@Before
	public void setup() throws Exception
	{
		connection = broker.createConnection();
	}
	
	@After
	public void tearDown() throws Exception
	{
		connection.close();
	}
	
	@Test
	public void testConstruction1()
	{
		TestClass obj = new TestClass(connection, destination, Session.SESSION_TRANSACTED);
		assertTrue(obj.isTransacted());
	}
	
	@Test
	public void testConstruction2()
	{
		TestClass obj = new TestClass(connection, destination, Session.CLIENT_ACKNOWLEDGE);
		assertFalse(obj.isTransacted());
		assertEquals(Session.CLIENT_ACKNOWLEDGE, obj.getAcknowledgeMode());
	}
	
	@Test
	public void testConstruction3()
	{
		TestClass obj = new TestClass(connection, destination, Session.DUPS_OK_ACKNOWLEDGE);
		assertFalse(obj.isTransacted());
		assertEquals(Session.DUPS_OK_ACKNOWLEDGE, obj.getAcknowledgeMode());
	}
	
	@Test
	public void testConstruction4()
	{
		TestClass obj = new TestClass(connection, destination, Session.AUTO_ACKNOWLEDGE);
		assertFalse(obj.isTransacted());
		assertEquals(Session.AUTO_ACKNOWLEDGE, obj.getAcknowledgeMode());
	}
	
	@Test
	public void testReceivesMessages() throws Exception
	{
		connection.start();
		
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageProducer producer = session.createProducer(destination);
		producer.send(session.createTextMessage("abc"));
		producer.close();
		session.close();
		
		TestClass test = new TestClass(connection, destination);
		assertFalse(test.isStarted());
		test.start();
		assertTrue(test.isStarted());
		
		long start = System.currentTimeMillis();
		for(;;)
		{
			Thread.yield();
			if (test.getReceived().size() > 0)
			{
				break;
			}
			
			long now = System.currentTimeMillis();
			if (now - start > 5000L)
			{
				fail("Didn't deliver in 5 seconds");
			}
		}
		
		test.close();
		assertFalse(test.isStarted());
		
		assertEquals(1, test.getReceived().size());
		assertEquals("abc", test.getReceived().get(0));
	}
	
	@Test
	public void testDoubleStartDoubleCloseBenign() throws Exception
	{
		TestClass test = new TestClass(connection, destination);
		assertFalse(test.isStarted());
		test.start();
		assertTrue(test.isStarted());
		test.start();
		assertTrue(test.isStarted());
		test.close();
		assertFalse(test.isStarted());
		test.close();
		assertFalse(test.isStarted());
	}
	
	@Test
	public void testJmsExceptionOnStart() throws Exception
	{
		ExceptionClass obj = new ExceptionClass(connection, destination);
		obj.setJmse(new JMSException("boom"));
		try
		{
			obj.start();
			fail("didn't throw");
		}
		catch(JMSException e)
		{
		}
		assertFalse(obj.isStarted());
	}
	
	@Test
	public void testRuntimeExceptionOnStart() throws Exception
	{
		ExceptionClass obj = new ExceptionClass(connection, destination);
		obj.setRe(new NullPointerException("boom"));
		try
		{
			obj.start();
			fail("didn't throw");
		}
		catch(NullPointerException e)
		{
		}
		assertFalse(obj.isStarted());
	}
	
	private static class TestClass extends BaseAsyncMessageReceiver
	{
		private List<String> received = Collections.synchronizedList(new ArrayList<String>());
		
		public TestClass(Connection connection, Destination destination)
		{
			super(connection, destination, Session.AUTO_ACKNOWLEDGE);
		}

		public TestClass(Connection connection, Destination destination, int ackMode)
		{
			super(connection, destination, ackMode);
		}

		@Override
		protected void handleMessage(Session session, Message message)
		{
			try
			{
				received.add(((TextMessage)message).getText());
			}
			catch(Exception e)
			{
				fail(e.toString());
			}
		}
		
		public List<String> getReceived()
		{
			return received;
		}
	}
	
	private static class ExceptionClass extends BaseAsyncMessageReceiver
	{
		private RuntimeException re;
		private JMSException jmse;
		
		public ExceptionClass(Connection connection, Destination destination)
		{
			super(connection, destination, Session.AUTO_ACKNOWLEDGE);
		}

		public void setRe(RuntimeException re)
		{
			this.re = re;
		}

		public void setJmse(JMSException jmse)
		{
			this.jmse = jmse;
		}

		@Override
		protected void handleMessage(Session session, Message message)
		{
		}

		@Override
		protected void onStart(Session session) throws JMSException
		{
			if (re != null)
			{
				throw re;
			}
			
			if (jmse != null)
			{
				throw jmse;
			}
		}
	}
	   private static class TestBroker extends VMMessageBrokerKaha
	    {
	        public TestBroker(String brokerName)
	        {
	            super(brokerName);
	        }
	        public TestBroker(String brokerName,  Map<String, String> params)
	        {
	            super(brokerName, params);
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

