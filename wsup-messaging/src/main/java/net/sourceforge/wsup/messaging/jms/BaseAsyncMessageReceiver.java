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
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

/**
 * This is a base class for asynchronous JMS message receivers. When created and
 * started, messages will be sequentially delivered to the
 * {@link #handleMessage(Session, Message)} method. Each instance of this class
 * manages its own JMS {@link Session} and {@link MessageConsumer}.
 * <p>
 * This class can be created and started before the corresponding
 * {@link Connection} is started. Per the JMS specification, messages will not
 * begin being delivered until the <code>Connection</code> is started. This
 * allows system configuration to be completed prior to message delivery, if
 * desired.
 * </p>
 * <p>
 * With respect to thread safety:
 * </p>
 * <ul>
 * <li>
 * The JMS specification requires that messages be delivered sequentially, so
 * <code>handleMessage</code> will not be called in a concurrent manner. This
 * method will, however, be called from a thread internal to the messaging
 * system.</li>
 * <li>
 * The {@link #close()} method may be called from any thread.</li>
 * <li>
 * The internal {@link #onClose()} method will be called from the thread that
 * calls <code>close</code>. It is called <u>after</u> the JMS
 * <code>Session</code> is closed, and this operation will not complete until
 * any call to <code>handleMessage</code> has returned. As a result,
 * <code>onClose</code> and <code>handleMessage</code> will not execute
 * concurrently.</li>
 * <li>
 * The internal {@link #onStart(Session)} method will be called from the thread
 * that calls <code>start</code>. The <code>onStart</code> method is called
 * after the <code>Session</code> is created, but before the
 * <code>MessageListener</code> is set up, so message delivery will not start
 * before <code>onStart</code> returns. Message delivery <u>may</u> start before
 * <code>start</code> returns, however.</li>
 * </ul>
 */
public abstract class BaseAsyncMessageReceiver extends BaseMessageReceiver
{
	/**
	 * Create an BaseAsyncMessageReceiver.
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
	protected BaseAsyncMessageReceiver(Connection connection,
			Destination destination, int acknowledgeMode)
	{
		super(connection, destination, acknowledgeMode);
	}

	/**
	 * Start the operation of this object. Message delivery may begin before
	 * this message returns.
	 * <p>
	 * Calling this method on an object that has already been started is benign.
	 * </p>
	 * 
	 * @throws JMSException
	 *             If the JMS <code>Session</code> or
	 *             <code>MessageConsumer</code> cannot be created.
	 */
	public synchronized void start() throws JMSException
	{
		if (isStarted())
		{
			return;
		}
		
		super.start();

		try
		{
			onStart(getSession());
			getConsumer().setMessageListener(new ListenerHelper(getSession()));
		}
		catch (JMSException e)
		{
			close();
			throw e;
		}
		catch (RuntimeException e)
		{
			close();
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
		if (!isStarted())
		{
			return;
		}
		
		super.close();
		onClose();
	}

	/**
	 * This is an overridable method that is called from within
	 * <code>start</code> after the JMS <code>Session</code> has been created,
	 * but before message delivery begins. It can be used to set up other
	 * resources related to the session, such as <code>MessageProducer</code>s
	 * required to send responses to messages.
	 * 
	 * @param session
	 *            The JMS <code>Session</code>
	 * 
	 * @see #start()
	 */
	protected void onStart(Session session) throws JMSException
	{
	}

	/**
	 * This is an overrideable that is called from within <code>close</code>
	 * after the JMS <code>Session</code> has been closed. Because the process
	 * of closing the <code>Session</code> will not complete while a message is
	 * being processed by <code>handleMessage</code>, this method and
	 * <code>handleMessage</code> will not be called concurrently.
	 * <p>
	 * Note that it is not necessary to close any <code>Session</code>-derived
	 * objects (such as <code>MessageProducer</code>s) that may have been
	 * created during <code>onStart</code> since closing the
	 * <code>Session</code> automatically closes those objects.
	 * </p>
	 */
	protected void onClose()
	{
	}

	/**
	 * This method will be called for each delivered message. Note that even
	 * though some of the <code>Session</code> and <code>Message</code> methods
	 * can throw a <code>JMSException</code>, it is considered a programming
	 * error for this method to throw such an exception.
	 * 
	 * @param session
	 *            JMS <code>Session</code> object.
	 * @param message
	 *            JMS <code>Message</code> being delivered.
	 */
	protected abstract void handleMessage(Session session, Message message);

	/*
	 * Internal "helper" class. This is the actual <code>MessageListener</code>
	 * that is installed into the <code>MessageConsumer</code>. (Having this in
	 * a separate class keeps the <code>onMessage</code> method out of the main
	 * class's public interface.)
	 */
	private class ListenerHelper implements MessageListener
	{
		private final Session session;

		public ListenerHelper(Session session)
		{
			this.session = session;
		}

		@Override
		public void onMessage(Message message)
		{
			BaseAsyncMessageReceiver.this.handleMessage(session, message);
		}
	}
}
