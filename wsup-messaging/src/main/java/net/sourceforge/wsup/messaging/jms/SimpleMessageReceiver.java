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
import javax.jms.Session;

/**
 * Convenience class that encapsulates much of the work required to
 * synchronously receive messages via JMS.
 * <p>
 * This class manages a {@link Session} and a {@link MessageConsumer} and
 * provides methods to receive messages. It also synchronizes accesses to the
 * <code>Session</code> and <code>MessageConsumer</code>, allowing instances of
 * this class to be used in a concurrent manner, even though the
 * <code>Session</code> and <code>MessageConsumer</code> classes are not
 * designed for concurrent access.
 * </p>
 */
public class SimpleMessageReceiver extends BaseMessageReceiver
{
	/**
	 * Create an SimpleMessageReceiver.
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
	public SimpleMessageReceiver(Connection connection,
			Destination destination, int acknowledgeMode)
	{
		super(connection, destination, acknowledgeMode);
	}

	/**
	 * Create an AbstractAsyncMessageHandler that uses the
	 * <code>Session.AUTO_ACKNOWLEDGE</code> acknowledgment method.
	 * 
	 * @param connection
	 *            <code>Connection</code> to use.
	 * @param destination
	 *            <code>Destination</code> to use.
	 */
	protected SimpleMessageReceiver(Connection connection,
			Destination destination)
	{
		super(connection, destination, Session.AUTO_ACKNOWLEDGE);
	}

	/**
	 * Receives the next message produced for this message consumer.
	 * <p>
	 * This call blocks indefinitely until a message is produced or until this
	 * message consumer is closed.
	 * </p>
	 * <p>
	 * If this <code>receive</code> is done within a transaction, the consumer
	 * retains the message until the transaction commits.
	 * </p>
	 * 
	 * @return the next message produced for this message consumer
	 * @throws JMSException
	 *             if the JMS provider fails to receive the next message due to
	 *             some internal error.
	 */
	public synchronized Message receive() throws JMSException
	{
		return getConsumer().receive();
	}

	/**
	 * Receives the next message that arrives within the specified timeout
	 * interval.
	 * <p>
	 * This call blocks until a message arrives, the timeout expires, or this
	 * message consumer is closed. A <code>timeout</code> of zero never expires,
	 * and the call blocks indefinitely.
	 * </p>
	 * 
	 * @param timeout
	 *            the timeout value (in milliseconds)
	 * @return the next message produced for this message consumer, or null if
	 *         the timeout expires
	 * @throws JMSException
	 *             if the JMS provider fails to receive the next message due to
	 *             some internal error.
	 */
	public synchronized Message receive(long timeout) throws JMSException
	{
		return getConsumer().receive(timeout);
	}

	/**
	 * Receives the next message if one is immediately available.
	 * 
	 * @return the next message produced for this message consumer, or null if
	 *         one is not available
	 * @throws JMSException
	 *             if the JMS provider fails to receive the next message due to
	 *             some internal error.
	 */
	public synchronized Message receiveNoWait() throws JMSException
	{
		return getConsumer().receiveNoWait();
	}
}
