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

import java.io.File;

import javax.jms.ConnectionFactory;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.store.PersistenceAdapter;
import org.apache.activemq.store.amq.AMQPersistenceAdapter;

/**
 * This is an implementation of {@link VMMessageBroker} that 
 * supports the <code>AMQ Message Store</code> <code>PersistenceAdapter</code>.
 */
public class VMMessageBrokerAMQ extends VMMessageBrokerPersistent implements ConnectionFactory
{
	/**
	 * Create the broker instance.
	 * 
	 * @param brokerName
	 *            Name of the broker. Must not be <code>null</code>.
	 */
	public VMMessageBrokerAMQ(String brokerName)
	{
		super(brokerName);
	}

	/**
	 * Creates a <code>AMQPersistenceAdapter</code> if a directory has been
	 * provided via {@link #setPersistenceRootDir(File)}.
	 * 
	 * @return AMQPersistenceAdapter, if a directory has been provided via
	 *         {@link #setPersistenceRootDir(File)}. Otherwise returns
	 *         <code>null</code> to disable persistence.
	 * @see BaseEmbeddedMessageBroker#createPersistenceAdapter()
	 */
	@Override
	protected PersistenceAdapter createPersistenceAdapter()
	{
		File directory = getPersistenceRootDir();
		if (directory == null)
		{
			return null;
		}

		AMQPersistenceAdapter adapter = new AMQPersistenceAdapter();

		adapter.setDirectory(directory);

		long maxLength = getMaxDataFileLength();
		if (maxLength > 0 && maxLength <= Integer.MAX_VALUE)
		{
			adapter.setMaxFileLength((int)maxLength);
		}

		return adapter;
	}

	/**
	 * @see BaseEmbeddedMessageBroker#preStart(BrokerService)
	 */
	@Override
	protected void preStart(BrokerService service) throws Exception
	{
		// disable JMX
		service.setUseJmx(false);
	}
}
