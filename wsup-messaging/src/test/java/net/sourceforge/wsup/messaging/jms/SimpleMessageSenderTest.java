/*
 * Copyright (c) 2011 Kevin Hunter
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package net.sourceforge.wsup.messaging.jms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionConsumer;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.StreamMessage;
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

public class SimpleMessageSenderTest
{
	private static ActiveMQQueue destination;

	private static VMMessageBrokerKaha broker;

	private static Connection connection;

	private Session consumerSession;

	private MessageConsumer consumer;

	public SimpleMessageSenderTest()
	{
	}

	@BeforeClass
	public static void oneTimeSetup() throws Exception
	{
		destination = new ActiveMQQueue("MessageSenderTest");

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
		consumerSession = connection.createSession(false,
				Session.AUTO_ACKNOWLEDGE);
		consumer = consumerSession.createConsumer(destination);
	}

	@After
	public void tearDown() throws Exception
	{
		consumerSession.close();
	}

	@Test
	public void testDoubleStartDoubleCloseBenign() throws Exception
	{
		SimpleMessageSender sender = new SimpleMessageSender(connection, destination);
		assertFalse(sender.isStarted());
		sender.start();
		assertTrue(sender.isStarted());
		sender.start();
		assertTrue(sender.isStarted());
		sender.close();
		assertFalse(sender.isStarted());
		sender.close();
		assertFalse(sender.isStarted());
	}

	@Test
	public void testSendString() throws Exception
	{
		SimpleMessageSender sender = new SimpleMessageSender(connection, destination);
		sender.start();
		sender.sendTextMessage("abc");
		sender.close();

		TextMessage received = (TextMessage) consumer.receive(100L);
		assertEquals("abc", received.getText());
	}

	@Test
	public void testSendObject() throws Exception
	{
		SimpleMessageSender sender = new SimpleMessageSender(connection, destination);
		sender.start();
		sender.sendObjectMessage(new Long(1));
		sender.close();

		ObjectMessage received = (ObjectMessage) consumer.receive(100L);
		Object payload = received.getObject();
		assertTrue(payload instanceof Long);
		assertEquals(new Long(1), payload);
	}

	@Test
	public void testSendBytes() throws Exception
	{
		SimpleMessageSender sender = new SimpleMessageSender(connection, destination);
		sender.start();
		BytesMessage sent = sender.createBytesMessage();
		sent.writeByte((byte) 1);
		sent.writeByte((byte) 3);
		sender.sendMessage(sent, null);
		sender.close();

		BytesMessage received = (BytesMessage) consumer.receive(100L);
		assertEquals(2, received.getBodyLength());
		assertEquals((byte) 1, received.readByte());
		assertEquals((byte) 3, received.readByte());
	}

	@Test
	public void testSendMap() throws Exception
	{
		SimpleMessageSender sender = new SimpleMessageSender(connection, destination);
		sender.start();
		MapMessage sent = sender.createMapMessage();
		sent.setLong("long", new Long(1));
		sent.setChar("char", 'a');
		sender.sendMessage(sent, null);
		sender.close();

		MapMessage received = (MapMessage) consumer.receive(100L);
		assertEquals(1, received.getLong("long"));
		assertEquals('a', received.getChar("char"));
	}

	@Test
	public void testSendStream() throws Exception
	{
		SimpleMessageSender sender = new SimpleMessageSender(connection, destination);
		sender.start();
		StreamMessage sent = sender.createStreamMessage();
		sent.writeBytes(new byte[]
		{
			3, 4
		});
		sender.sendMessage(sent, null);
		sender.close();

		StreamMessage received = (StreamMessage) consumer.receive(100L);
		byte[] output = new byte[2];
		received.readBytes(output);
		assertEquals(3, output[0]);
		assertEquals(4, output[1]);
	}

	@Test
	public void testSendWithProperties() throws Exception
	{
		SimpleMessageSender sender = new SimpleMessageSender(connection, destination);
		sender.start();
		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put("long", new Long(3));
		properties.put("int", new Integer(5));
		TextMessage sent = sender.createTextMessage("abc");
		sender.sendMessage(sent, properties);
		sender.close();

		TextMessage received = (TextMessage) consumer.receive(100L);
		assertEquals("abc", received.getText());
		assertEquals(3, received.getLongProperty("long"));
		assertEquals(5, received.getIntProperty("int"));
	}

	@Test
	public void testJmsExceptionDuringStart() throws Exception
	{
		SimpleMessageSender sender = new SimpleMessageSender(new ExceptionConnection(
				new JMSException("boom")), destination);
		try
		{
			sender.start();
			fail("didn't throw");
		}
		catch (JMSException e)
		{
		}
		assertFalse(sender.isStarted());
	}

	@Test
	public void testRuntimeExceptionDuringStart() throws Exception
	{
		SimpleMessageSender sender = new SimpleMessageSender(new ExceptionConnection(
				new RuntimeException("boom")), destination);
		try
		{
			sender.start();
			fail("didn't throw");
		}
		catch (RuntimeException e)
		{
		}
		assertFalse(sender.isStarted());
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
