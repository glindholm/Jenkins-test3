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

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

/**
 * Convenience class that encapsulates much of the work required to send
 * messages via JMS.
 * <p>
 * This class manages a {@link Session} and a {@link MessageProducer} and
 * provides methods to create and send messages. It also synchronizes accesses
 * to the <code>Session</code> and <code>MessageProducer</code>, allowing
 * instances of this class to be used in a concurrent manner, even though the
 * <code>Session</code> and <code>MessageProducer</code> classes are not
 * designed for concurrent access.
 * </p>
 */
public class SimpleMessageSender extends CloseOnFinalize
{
	private final Connection connection;

	private final Destination destination;

	private transient Session session;

	private transient MessageProducer producer;

	/**
	 * Constructs a MessageSender
	 * 
	 * @param connection
	 *            <code>Connection</code> to use.
	 * @param destination
	 *            <code>Destination</code> to use.
	 */
	public SimpleMessageSender(Connection connection, Destination destination)
	{
		this.connection = connection;
		this.destination = destination;
	}

	/**
	 * Start the operation of this object. Message delivery may begin after this
	 * message returns.
	 * <p>
	 * Calling this method on an object that has already been started is benign.
	 * </p>
	 * 
	 * @throws JMSException
	 *             If the JMS <code>Session</code> or
	 *             <code>MessageProducer</code> cannot be created.
	 */
	public synchronized void start() throws JMSException
	{
		if (session != null)
		{
			return;
		}

		try
		{
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			producer = session.createProducer(destination);
		}
		catch (JMSException e)
		{
			session = null;
			producer = null;
			throw e;
		}
		catch (RuntimeException e)
		{
			session = null;
			producer = null;
			throw e;
		}
	}

	/**
	 * Close down the object, releasing its resources.
	 * <p>
	 * Calling this method on an object that has not been started or has already
	 * been closed is benign.
	 * </p>
	 */
	@Override
	public synchronized void close() throws JMSException
	{
		try
		{
			if (session != null)
			{
				session.close();
			}
		}
		finally
		{
			producer = null;
			session = null;
		}
	}

	/**
	 * Indicates whether or not this object has been started.
	 * 
	 * @return <code>true</code> after <code>start</code> returns and before
	 *         <code>close</code> is called.
	 */
	public synchronized boolean isStarted()
	{
		return session != null;
	}

	/**
	 * Create a {@link BytesMessage} object.
	 * 
	 * @return <code>BytesMessage</code> instance
	 * @throws JMSException
	 */
	public synchronized BytesMessage createBytesMessage() throws JMSException
	{
		return session.createBytesMessage();
	}

	/**
	 * Create a {@link MapMessage} object.
	 * 
	 * @return <code>MapMessage</code> instance
	 * @throws JMSException
	 */
	public synchronized MapMessage createMapMessage() throws JMSException
	{
		return session.createMapMessage();
	}

	/**
	 * Create a {@link StreamMessage} object.
	 * 
	 * @return <code>StreamMessage</code> instance
	 * @throws JMSException
	 */
	public synchronized StreamMessage createStreamMessage() throws JMSException
	{
		return session.createStreamMessage();
	}

	/**
	 * Creates an {@link ObjectMessage} encapsulating the provided
	 * <code>Object</code>.
	 * 
	 * @param object
	 *            <code>Serializable</code> object to be sent.
	 * @return <code>ObjectMessage</code> instance.
	 * @throws JMSException
	 */
	public synchronized ObjectMessage createObjectMessage(Serializable object)
			throws JMSException
	{
		return session.createObjectMessage(object);
	}

	/**
	 * Creates an {@link TextMessage} encapsulating the provided
	 * <code>String</code>.
	 * 
	 * @param message
	 *            <code>String</code> to be sent.
	 * @return <code>TextMessage</code> instance.
	 * @throws JMSException
	 */
	public synchronized TextMessage createTextMessage(String message)
			throws JMSException
	{
		return session.createTextMessage(message);
	}

	/**
	 * Convenience method to create and send an {@link ObjectMessage}
	 * encapsulating the provided <code>Object</code>.
	 * 
	 * @param object
	 *            <code>Serializable</code> object to be sent.
	 * @throws JMSException
	 */
	public void sendObjectMessage(Serializable object) throws JMSException
	{
		sendMessage(createObjectMessage(object));
	}

	/**
	 * Convenience method to create and send a {@link TextMessage} encapsulating
	 * the provided <code>String</code>.
	 * 
	 * @param message
	 *            <code>String</code> to be sent.
	 * @throws JMSException
	 */
	public void sendTextMessage(String message) throws JMSException
	{
		sendMessage(createTextMessage(message));
	}

	/**
	 * Send a <code>Message</code>, applying the specified message properties.
	 * 
	 * @param message
	 *            <code>Message</code> to be sent.
	 * @param properties
	 *            <code>Map</code> containing the properties to set on the
	 *            message. May be <code>null</code>.
	 * @throws JMSException
	 */
	public void sendMessage(Message message, Map<String, Object> properties)
			throws JMSException
	{
		if (properties != null)
		{
			for (Entry<String, Object> property : properties.entrySet())
			{
				message.setObjectProperty(property.getKey(), property
						.getValue());
			}
		}

		sendMessage(message);
	}

	/**
	 * Send a <code>Message</code> through the internal
	 * <code>MessageProvider</code>.
	 * 
	 * @param message
	 *            <code>Message</code> to be sent.
	 * @throws JMSException
	 */
	public synchronized void sendMessage(Message message) throws JMSException
	{
		producer.send(message);
	}
}
