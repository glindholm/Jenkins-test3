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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionConsumer;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import net.sourceforge.wsup.messaging.activemq.VMMessageBrokerKaha;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.store.PersistenceAdapter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SimpleMessageReceiverTest
{
	private static ActiveMQQueue destination;

	private static VMMessageBrokerKaha broker;

	private static Connection connection;

	private SimpleMessageSender sender;

	public SimpleMessageReceiverTest()
	{
	}

	@BeforeClass
	public static void oneTimeSetup() throws Exception
	{
		destination = new ActiveMQQueue("SimpleMessageReceiverTest");

		broker = new TestBroker("test");
		broker.startBroker(true);
		connection = broker.createConnection();
		connection.start();
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception
	{
		connection.close();
		broker.stopBroker(true);
	}

	@Before
	public void setup() throws Exception
	{
		sender = new SimpleMessageSender(connection, destination);
		sender.start();
	}

	@After
	public void tearDown() throws Exception
	{
		sender.close();
	}

	@Test
	public void testConstruction1()
	{
		SimpleMessageReceiver obj = new SimpleMessageReceiver(connection, destination, Session.SESSION_TRANSACTED);
		assertTrue(obj.isTransacted());
	}
	
	@Test
	public void testConstruction2()
	{
		SimpleMessageReceiver obj = new SimpleMessageReceiver(connection, destination, Session.CLIENT_ACKNOWLEDGE);
		assertFalse(obj.isTransacted());
		assertEquals(Session.CLIENT_ACKNOWLEDGE, obj.getAcknowledgeMode());
	}
	
	@Test
	public void testConstruction3()
	{
		SimpleMessageReceiver obj = new SimpleMessageReceiver(connection, destination, Session.DUPS_OK_ACKNOWLEDGE);
		assertFalse(obj.isTransacted());
		assertEquals(Session.DUPS_OK_ACKNOWLEDGE, obj.getAcknowledgeMode());
	}
	
	@Test
	public void testConstruction4()
	{
		SimpleMessageReceiver obj = new SimpleMessageReceiver(connection, destination, Session.AUTO_ACKNOWLEDGE);
		assertFalse(obj.isTransacted());
		assertEquals(Session.AUTO_ACKNOWLEDGE, obj.getAcknowledgeMode());
	}
	
	@Test
	public void testConstruction5()
	{
		SimpleMessageReceiver obj = new SimpleMessageReceiver(connection, destination);
		assertFalse(obj.isTransacted());
		assertEquals(Session.AUTO_ACKNOWLEDGE, obj.getAcknowledgeMode());
	}
	
	@Test
	public void testDoubleStartDoubleCloseBenign() throws Exception
	{
		SimpleMessageReceiver receiver = new SimpleMessageReceiver(connection, destination);
		assertFalse(receiver.isStarted());
		receiver.start();
		assertTrue(receiver.isStarted());
		receiver.start();
		assertTrue(receiver.isStarted());
		receiver.close();
		assertFalse(receiver.isStarted());
		receiver.close();
		assertFalse(receiver.isStarted());
	}
	
	@Test
	public void testReceive() throws Exception
	{
		sender.sendTextMessage("testReceive");
		
		SimpleMessageReceiver receiver = new SimpleMessageReceiver(connection, destination);
		receiver.start();
		TextMessage message = (TextMessage)receiver.receive();
		assertEquals("testReceive", message.getText());
		receiver.close();
	}
	
	@Test
	public void testReceiveTimeout() throws Exception
	{
		sender.sendTextMessage("testReceiveTimeout");
		SimpleMessageReceiver receiver = new SimpleMessageReceiver(connection, destination);
		receiver.start();
		TextMessage message = (TextMessage)receiver.receive(100L);
		assertEquals("testReceiveTimeout", message.getText());
		assertNull(receiver.receive(100L));
		receiver.close();
	}
	
	@Test
	public void testReceiveNoWait() throws Exception
	{
		SimpleMessageReceiver receiver = new SimpleMessageReceiver(connection, destination);
		receiver.start();
		assertNull(receiver.receiveNoWait());
		receiver.close();
	}

	@Test
	public void testJmsExceptionDuringStart() throws Exception
	{
		SimpleMessageReceiver receiver = new SimpleMessageReceiver(new ExceptionConnection(
				new JMSException("boom")), destination);
		try
		{
			receiver.start();
			fail("didn't throw");
		}
		catch (JMSException e)
		{
		}
		assertFalse(receiver.isStarted());
	}

	@Test
	public void testRuntimeExceptionDuringStart() throws Exception
	{
		SimpleMessageReceiver receiver = new SimpleMessageReceiver(new ExceptionConnection(
				new RuntimeException("boom")), destination);
		try
		{
			receiver.start();
			fail("didn't throw");
		}
		catch (RuntimeException e)
		{
		}
		assertFalse(receiver.isStarted());
	}

	private static class ExceptionConnection implements Connection
	{
		private JMSException je;

		private RuntimeException re;

		public ExceptionConnection(JMSException je)
		{
			this.je = je;
		}

		public ExceptionConnection(RuntimeException re)
		{
			this.re = re;
		}

		@Override
		public void close() throws JMSException
		{
		}

		@Override
		public ConnectionConsumer createConnectionConsumer(Destination arg0,
				String arg1, ServerSessionPool arg2, int arg3)
				throws JMSException
		{
			return null;
		}

		@Override
		public ConnectionConsumer createDurableConnectionConsumer(Topic arg0,
				String arg1, String arg2, ServerSessionPool arg3, int arg4)
				throws JMSException
		{
			return null;
		}

		@Override
		public Session createSession(boolean arg0, int arg1)
				throws JMSException
		{
			if (re != null)
			{
				throw re;
			}

			if (je != null)
			{
				throw je;
			}

			return null;
		}

		@Override
		public String getClientID() throws JMSException
		{
			return null;
		}

		@Override
		public ExceptionListener getExceptionListener() throws JMSException
		{
			return null;
		}

		@Override
		public ConnectionMetaData getMetaData() throws JMSException
		{
			return null;
		}

		@Override
		public void setClientID(String arg0) throws JMSException
		{
		}

		@Override
		public void setExceptionListener(ExceptionListener arg0)
				throws JMSException
		{
		}

		@Override
		public void start() throws JMSException
		{
		}

		@Override
		public void stop() throws JMSException
		{
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

