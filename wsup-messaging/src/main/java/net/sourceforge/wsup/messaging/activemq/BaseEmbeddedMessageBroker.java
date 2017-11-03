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

package net.sourceforge.wsup.messaging.activemq;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.store.PersistenceAdapter;

/**
 * This is the base class for message brokers that exist within another
 * application. Using the classes derived from this one, applications can create
 * and launch an ActiveMQ-based message broker that will handle the transport of
 * JMS messages.
 */
public abstract class BaseEmbeddedMessageBroker
{
	private final String brokerName;

	private transient BrokerService brokerService;

	/**
	 * Create the broker instance.
	 * 
	 * @param brokerName
	 *            Name of the broker. Must not be <code>null</code>.
	 */
	public BaseEmbeddedMessageBroker(String brokerName)
	{
		this.brokerName = brokerName;
	}

	/**
	 * Returns the name of the broker.
	 * 
	 * @return Name of the broker.
	 */
	public String getBrokerName()
	{
		return brokerName;
	}

	/**
	 * Starts the broker service. This method is benign if called on a broker
	 * that has already been started.
	 * <p>
	 * This method, {@link #stopBroker(boolean)} and {@link #isRunning()}
	 * support concurrent use.
	 * </p>
	 * 
	 * @param wait
	 *            If <code>true</code>, this method will block until the broker
	 *            has completed its startup processing.
	 * 
	 * @throws Exception
	 *             If something goes wrong.
	 */
	public synchronized void startBroker(boolean wait) throws Exception
	{
		if (brokerService != null)
		{
			return;
		}

		try
		{
			brokerService = new BrokerService();

			brokerService.setBrokerName(brokerName);

			PersistenceAdapter adapter = createPersistenceAdapter();
			if (adapter == null)
			{
				brokerService.setPersistent(false);
			}
			else
			{
				adapter.setBrokerName(brokerName);
				brokerService.setPersistent(true);
				brokerService.setPersistenceAdapter(adapter);
			}

			String[] connectors = getConnectors();
			for (String connector : connectors)
			{
				brokerService.addConnector(connector);
			}

			preStart(brokerService);

			brokerService.start();

			if (wait)
			{
				brokerService.waitUntilStarted();
			}
		}
		catch (Exception e)
		{
			brokerService = null;
			throw e;
		}
	}

	/**
	 * Stops the broker service. This method is benign if called on a broker
	 * that has already been stopped, or has not been started.
	 * <p>
	 * This method, {@link #startBroker(boolean)} and {@link #isRunning()}
	 * support concurrent use.
	 * </p>
	 * 
	 * @param wait
	 *            If <code>true</code>, this method will block until the broker
	 *            has completed its startup processing.
	 * 
	 * @throws Exception
	 *             If something goes wrong.
	 */
	public synchronized void stopBroker(boolean wait) throws Exception
	{
		if (brokerService == null)
		{
			return;
		}

		try
		{
			brokerService.stop();
			if (wait)
			{
				brokerService.waitUntilStopped();
			}
		}
		finally
		{
			brokerService = null;
		}
	}

	/**
	 * Indicates whether or not this broker has been started.
	 * <p>
	 * This method, {@link #startBroker(boolean)} and
	 * {@link #startBroker(boolean)} support concurrent use.
	 * </p>
	 * 
	 * @return <code>true</code> if the broker is running,
	 *         <code>false</code> if the broker has not been started, or has
	 *         been stopped.
	 */
	public synchronized boolean isRunning()
	{
		return brokerService != null;
	}

	/**
	 * Allow derived classes access to the core service.
	 * 
	 * @return <code>BrokerService</code> instance if running, otherwise
	 *         <code>null</code>.
	 */
	protected BrokerService getBrokerService()
	{
		return brokerService;
	}

	/**
	 * Overridable method called just before the broker is started. Derived
	 * classes may use this to do additional configuration.
	 * 
	 * @param service
	 *            The internal BrokerService
	 * @throws Exception
	 */
	protected void preStart(BrokerService service) throws Exception
	{
	}

	/**
	 * Abstract method that should return an array containing one or more
	 * <code>Connector</code> URL's.
	 * 
	 * @return Array of <code>String</code>s, each entry in which is the URI for
	 *         a <code>Connector</code> that the broker should establish. Must
	 *         not be <code>null</code>, and must contain at least one entry.
	 */
	protected abstract String[] getConnectors();

	/**
	 * Overrideable method that returns the <code>PersistenceAdapter</code> that
	 * this broker should use to persist messages until they are delivered.
	 * 
	 * @return <code>PersistenceAdapter</code> if persistence is to be used,
	 *         <code>null</code> if the broker is to be non-persistent.
	 */
	protected PersistenceAdapter createPersistenceAdapter()
        throws Exception
	{
	    return null;
	}
}
