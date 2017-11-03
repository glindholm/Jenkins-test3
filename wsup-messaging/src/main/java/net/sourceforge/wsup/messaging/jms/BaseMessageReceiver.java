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

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

/**
 * This is a base class for JMS message receivers. This class manages a
 * {@link Session} and a {@link MessageConsumer}.
 */
public abstract class BaseMessageReceiver extends CloseOnFinalize
{
	private final Connection connection;

	private final Destination destination;

	private final boolean transacted;

	private final int acknowledgeMode;

	private transient Session session;

	private transient MessageConsumer consumer;

	/**
	 * Create an BaseMessageSender.
	 * 
	 * @param connection
	 *            <code>Connection</code> to use.
	 * @param destination
	 *            <code>Destination</code> to use.
	 * @param acknowledgeMode
	 *            Acknowledgement mode that will be used. One of the following:
	 *            <ul>
	 *            <li><code>Session.SESSION_TRANSACTED</code> - transactional
	 *            processing. The client is responsible for calling
	 *            <code>commit</code> on the <code>Session</code> at the
	 *            appropriate point in order to acknowledge messages previously
	 *            sent.</li>
	 *            <li><code>Session.AUTO_ACKNOWLEDGE</code> - each message will
	 *            be treated as having been acknowledged when
	 *            <code>handleMessage</code> returns.</li>
	 *            <li><code>Session.CLIENT_ACKNOWLEDGE</code> - the client is
	 *            responsible for calling <code>Message.acknowledge</code> at
	 *            the appropriate point.</li>
	 *            <li><code>Session.DUPS_OK_ACKNOWLEDGE</code> - the client is
	 *            responsible for calling <code>Message.acknowledge</code> at
	 *            the appropriate point, however the <code>Session</code> will
	 *            lazily acknowledge messages. This is higher performance than
	 *            <code>Session.CLIENT_ACKNOWLEDGE</code>, however there is the
	 *            possibility of duplicate message delivery.</li>
	 *            </ul>
	 */
	protected BaseMessageReceiver(Connection connection, Destination destination,
			int acknowledgeMode)
	{
		this.connection = connection;
		this.destination = destination;
		if (acknowledgeMode == Session.SESSION_TRANSACTED)
		{
			transacted = true;
		}
		else
		{
			transacted = false;
		}
		this.acknowledgeMode = acknowledgeMode;
	}

	/**
	 * Indicates whether this instance operates using transactions.
	 * 
	 * @return <code>true</code> if the underlying <code>Session</code> was
	 *         created to be transacted.
	 */
	public boolean isTransacted()
	{
		return transacted;
	}

	/**
	 * Returns the acknowledgement mode with which this object was created.
	 * 
	 * @return Acknowledgement mode
	 */
	public int getAcknowledgeMode()
	{
		return acknowledgeMode;
	}

	/**
	 * Start the operation of this object.
	 * <p>
	 * Calling this method on an object that has already been started is benign.
	 * </p>
	 * 
	 * @throws JMSException
	 *             If the JMS <code>Session</code> or
	 *             <code>MessageConsumer</code> cannot be created.
	 */
	protected synchronized void start() throws JMSException
	{
		if (session != null)
		{
			return;
		}

		try
		{
			session = connection.createSession(transacted, acknowledgeMode);
			consumer = session.createConsumer(destination);
		}
		catch (JMSException e)
		{
			session = null;
			consumer = null;
			throw e;
		}
		catch (RuntimeException e)
		{
			session = null;
			consumer = null;
			throw e;
		}
	}

	/**
	 * Shut down the handler. Message delivery will cease before the completion
	 * of this method. This method will not return until any message being
	 * processed by <code>handleMessage</code> has been completed.
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
			consumer = null;
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
	
	protected MessageConsumer getConsumer()
	{
		return consumer;
	}
	
	protected Session getSession()
	{
		return session;
	}
}
