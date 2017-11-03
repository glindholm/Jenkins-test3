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

import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.JMSSecurityException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;

/**
 * This is an implementation of an embedded message broker that uses the
 * <code>VM</code> transport for handling messages within the context of a
 * single Virtual Machine.
 * <p>
 * This class does not support persistence.  If message persistence is desired,
 * use {@link VMMessageBrokerKaha}.
 * </p>
 * <p>
 * Although the "configuration" methods do not, the <code>ConnectionFactory</code>
 * methods on this class support concurrent use.
 * </p>
 */
public class VMMessageBroker extends BaseEmbeddedMessageBroker implements ConnectionFactory
{
	private final String connectorURI;

	private final String brokerURI;

	private final ActiveMQConnectionFactory connectionFactory;

    /**
     * Create the broker instance.
     * 
     * @param brokerName
     *            Name of the broker. Must not be <code>null</code> or contain characters not
     *            permitted in a URI.
     */
    public VMMessageBroker(String brokerName)
    {
        this(brokerName, null);
    }
    
	/**
	 * Create the broker instance with additional parameters.
	 * 
	 * @param brokerName Name of the broker.
	 * @param params optional map of additional parameters, the key is the parameter name the value 
	 * is the parameter value. 
	 */
	public VMMessageBroker(String brokerName, Map<String, String> params)
	{
		super(brokerName);

		connectorURI = "vm://" + brokerName;

        StringBuilder brokerUriBuilder = new StringBuilder();
        brokerUriBuilder.append(connectorURI).append("?create=false");
		if (params != null)
		{
		    for(Map.Entry<String, String> param: params.entrySet())
		    {
		        brokerUriBuilder.append('&').append(param.getKey()).append('=').append(param.getValue());
		    }
		}
		brokerURI = brokerUriBuilder.toString();

		connectionFactory = new ActiveMQConnectionFactory(brokerURI);
	}

	/**
	 * Return the URI that <code>ActiveMQConnectionFactory</code> instances
	 * should use to connect to this broker.
	 * 
	 * @return Broker URI String
	 */
	public String getBrokerURI()
	{
		return brokerURI;
	}

	/**
	 * Create a <code>Connection</code> to this broker with the default user
	 * identity. Note that a <code>ConnectionFactory</code> supports concurrent
	 * use, so this method may be called in a mult-threaded manner. The
	 * connection is created in stopped mode. No messages will be delivered
	 * until the {@link Connection#start()} method is explicitly called.
	 * <p>
	 * This method supports concurrent use.
	 * </p>
	 * 
	 * @return a newly created connection
	 * @throws JMSException
	 *             if the JMS provider fails to create the connection due to
	 *             some internal error. In particular, the connection creation
	 *             will fail if the broker is not running.
	 * @throws JMSSecurityException
	 *             if client authentication fails due to an invalid user name or
	 *             password.
	 */
	public Connection createConnection() throws JMSException
	{
		return connectionFactory.createConnection();
	}

	/**
	 * Create a <code>Connection</code> to this broker with the specified user
	 * identity. Note that a <code>ConnectionFactory</code> supports concurrent
	 * use, so this method may be called in a mult-threaded manner. The
	 * connection is created in stopped mode. No messages will be delivered
	 * until the {@link Connection#start()} method is explicitly called.
	 * <p>
	 * This method supports concurrent use.
	 * </p>
	 * 
	 * @return a newly created connection
	 * @throws JMSException
	 *             if the JMS provider fails to create the connection due to
	 *             some internal error. In particular, the connection creation
	 *             will fail if the broker is not running.
	 * @throws JMSSecurityException
	 *             if client authentication fails due to an invalid user name or
	 *             password.
	 */
	public Connection createConnection(String userName, String password)
			throws JMSException
	{
		return connectionFactory.createConnection(userName, password);
	}

	/**
	 * Returns a single VM connector named for the broker.
	 * 
	 * @return String array containing the single connector
	 *         <code>vm://brokerName</code>.
	 * @see BaseEmbeddedMessageBroker#getConnectors()
	 */
	@Override
	protected String[] getConnectors()
	{
		return new String[]
		{
			connectorURI
		};
	}

	/**
	 * @see BaseEmbeddedMessageBroker#preStart(BrokerService)
	 */
	@Override
	protected void preStart(BrokerService service) throws Exception
	{
	    super.preStart(service);
	    
		// disable JMX
		service.setUseJmx(false);
	}
}
