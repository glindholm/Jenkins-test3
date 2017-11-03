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
import java.util.Map;

import org.apache.activemq.broker.BrokerService;

/**
 * This is an abstract implementation of {@link VMMessageBroker} that 
 * supports a <code>PersistenceAdapter</code>.
 */
public abstract class VMMessageBrokerPersistent extends VMMessageBroker
{
	private File persistenceRootDir;

	private long maxDataFileLength;

	/**
	 * Create the broker instance.
	 * 
	 * @param brokerName
	 *            Name of the broker. Must not be <code>null</code>.
	 */
	public VMMessageBrokerPersistent(String brokerName)
	{
		super(brokerName);
	}
	
    /**
     * Create the broker instance with additional parameters.
     * 
     * @param brokerName Name of the broker.
     * @param params optional map of additional parameters, the key is the parameter name the value 
     * is the parameter value. 
     */
    public VMMessageBrokerPersistent(String brokerName, Map<String, String> params)
    {
        super(brokerName, params);
    }

	/**
	 * Gets the directory in which the <code>KahaPersistenceAdapter</code>
	 * should store its files.
	 * 
	 * @return <code>File</code> instance.
	 */
	public File getPersistenceRootDir()
	{
		return persistenceRootDir;
	}

	/**
	 * Sets the directory in which the <code>PersistenceAdapter</code>
	 * should store its files.
	 * 
	 * @param persistenceRootDir
	 *            <code>File</code> instance, or <code>null</code> to disable
	 *            persistence. Defaults to <code>null</code>.
	 */
	public void setPersistenceRootDir(File persistenceRootDir)
	{
		this.persistenceRootDir = persistenceRootDir;
	}

	/**
	 * Get the hint for the maximum data file length that the
	 * <code>PersistenceAdapter</code> should use.
	 * 
	 * @return Suggested maximum length of data files.
	 */
	public long getMaxDataFileLength()
	{
		return maxDataFileLength;
	}

	/**
	 * Set a hint for the maximum data file length that the
	 * <code>KahaPersistenceAdapter</code> should use.
	 * 
	 * @param maxDataFileLength
	 *            If > 0, maximum length of data files. If <= 0, the
	 *            <code>PersistenceAdapter</code> default value will be
	 *            used. Defaults to 0.
	 */
	public void setMaxDataFileLength(long maxDataFileLength)
	{
		this.maxDataFileLength = maxDataFileLength;
	}

    @Override
    protected void preStart(BrokerService service) throws Exception
    {
        super.preStart(service);
        
        if (maxDataFileLength > 0)
        {
            service.getSystemUsage().getStoreUsage().setLimit(maxDataFileLength);
        }
    }
	
	
}
