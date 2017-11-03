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

package net.sourceforge.wsup.messaging.activemq;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.store.PersistenceAdapter;
import org.junit.Test;

public class VMMessageBrokerTest
{
    @Test
    public void testConnectorsAndBrokerURI()
    {
        TestBroker broker = new TestBroker("VMMessageBrokerTest");
        assertEquals("VMMessageBrokerTest", broker.getBrokerName());
        String[] connectors = broker.getConnectors();
        assertEquals(1, connectors.length);
        assertEquals("vm://VMMessageBrokerTest", connectors[0]);
        assertEquals("vm://VMMessageBrokerTest?create=false", broker.getBrokerURI());
    }
    
    @Test
    public void testPersistence() throws Exception
    {
        TestBroker broker = new TestBroker("test");
        assertNull(broker.createPersistenceAdapter());
    }

    @Test
    public void testSendReceive() throws Exception
    {
        ActiveMQQueue destination = new ActiveMQQueue("testQueue");
        TestBroker broker = new TestBroker("test");
        broker.startBroker(true);
        
        Connection connection = broker.createConnection();
        connection.start();
        
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        
        MessageProducer producer = session.createProducer(destination);
        producer.send(session.createTextMessage("abc"));
        
        MessageConsumer consumer = session.createConsumer(destination);
        Message message = consumer.receive();
        
        assertTrue(message instanceof TextMessage);
        TextMessage tm = (TextMessage)message;
        assertEquals("abc", tm.getText());
        
        session.close();
        connection.close();
        
        broker.stopBroker(true);
    }
    
    @Test
    public void testSendReceiveWithAuthentication() throws Exception
    {
        ActiveMQQueue destination = new ActiveMQQueue("testQueue");
        
        TestBroker broker = new TestBroker("test");
        broker.startBroker(true);
        
        Connection connection = broker.createConnection("user", "pass");
        connection.start();
        
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        
        MessageProducer producer = session.createProducer(destination);
        producer.send(session.createTextMessage("abc"));
        
        MessageConsumer consumer = session.createConsumer(destination);
        Message message = consumer.receive();
        
        assertTrue(message instanceof TextMessage);
        TextMessage tm = (TextMessage)message;
        assertEquals("abc", tm.getText());
        
        session.close();
        connection.close();
        
        broker.stopBroker(true);
    }
    
    /*
     * Class to allow access to some protected methods
     */
    private static class TestBroker extends VMMessageBroker
    {
        public TestBroker(String brokerName)
        {
            super(brokerName);
        }

        @Override
        public PersistenceAdapter createPersistenceAdapter() throws Exception
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

