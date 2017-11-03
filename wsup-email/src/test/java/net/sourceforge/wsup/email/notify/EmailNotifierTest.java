/*
 *  Copyright (c) 2010 Kevin Hunter
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

package net.sourceforge.wsup.email.notify;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Properties;

import net.sourceforge.wsup.core.HostProperties;
import net.sourceforge.wsup.core.InvalidPropertyException;
import net.sourceforge.wsup.core.notify.MockNotifier;
import net.sourceforge.wsup.core.notify.Notifier;
import net.sourceforge.wsup.email.EmailSender;
import net.sourceforge.wsup.email.EmailSenderImpl;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.junit.Test;

public class EmailNotifierTest
{
    public EmailNotifierTest()
    {
    }

    @Test
    public void testProperties() throws Exception
    {
        MockEmailNotifier notifier = new MockEmailNotifier();
        assertTrue(notifier.notify(this, "test", "subject", "test", null));
        MockEmailSender sender = notifier.sender;

        assertEquals("host", sender.host);
        assertEquals("user", sender.user);
        assertEquals("pass", sender.password);
        assertEquals("from", sender.from);
        assertEquals("to", sender.to);
    }

    @Test
    public void testProperties2()
    {
        Properties properties = new Properties();

        properties.setProperty(EmailNotifier.PROP_BASE + EmailNotifier.PROP_MAIL_HOST, "host");
        properties.setProperty(EmailNotifier.PROP_BASE + EmailNotifier.PROP_MAIL_USER, "user");
        properties.setProperty(EmailNotifier.PROP_BASE + EmailNotifier.PROP_MAIL_PASSWORD, "pass");
        properties.setProperty(EmailNotifier.PROP_BASE + EmailNotifier.PROP_MAIL_FROM, "from");
        properties.setProperty(EmailNotifier.PROP_BASE + EmailNotifier.PROP_MAIL_TO, "to");
        properties.setProperty(EmailNotifier.PROP_BASE + EmailNotifier.PROP_MAIL_PORT, "26");
        properties.setProperty(EmailNotifier.PROP_BASE + EmailNotifier.PROP_MAIL_TLS, "true");

        MockEmailNotifier notifier = new MockEmailNotifier(properties, null);
        assertTrue(notifier.notify(this, "test", "subject", "test", null));
        MockEmailSender sender = notifier.sender;

        assertEquals("host", sender.host);
        assertEquals("user", sender.user);
        assertEquals("pass", sender.password);
        assertEquals("from", sender.from);
        assertEquals("to", sender.to);
        assertEquals(Integer.valueOf(26), sender.port);
        assertEquals(Boolean.TRUE, sender.tls);
    }

    @Test
    public void testPropertiesPrefix()
    {
        String prefix = "prefix.";
        Properties properties = new Properties();

        properties.setProperty(prefix + EmailNotifier.PROP_MAIL_HOST, "host");
        properties.setProperty(prefix + EmailNotifier.PROP_MAIL_USER, "user");
        properties.setProperty(prefix + EmailNotifier.PROP_MAIL_PASSWORD, "pass");
        properties.setProperty(prefix + EmailNotifier.PROP_MAIL_FROM, "from");
        properties.setProperty(prefix + EmailNotifier.PROP_MAIL_TO, "to");
        properties.setProperty(prefix + EmailNotifier.PROP_MAIL_PORT, "26");
        properties.setProperty(prefix + EmailNotifier.PROP_MAIL_TLS, "true");

        EmailNotifier notifier = new EmailNotifier(properties, prefix, null);

        assertEquals("host", notifier.getEmailConfig().getHost());
        assertEquals("user", notifier.getEmailConfig().getUser());
        assertEquals("pass", notifier.getEmailConfig().getPassword());
        assertEquals("from", notifier.getMailFrom());
        assertEquals("to", notifier.getMailTo());
        assertEquals(Integer.valueOf(26), notifier.getEmailConfig().getPort());
        assertEquals(Boolean.TRUE, notifier.getEmailConfig().getTls());
    }

    @Test
    public void testFailFast()
    {
        String prefix = "prefix.";

        {
            Properties properties = new Properties();
            properties.setProperty(prefix + EmailNotifier.PROP_MAIL_TO, "to");
            new EmailNotifier(properties, prefix, null);
        }

        // mailTo is required
        try
        {
            Properties properties = new Properties();
            //properties.setProperty(prefix + EmailNotifier.PROP_MAIL_TO, "to");
            new EmailNotifier(properties, prefix, null);
            fail("mailTo is required");
        }
        catch (NullPointerException e)
        {
            // success
        }

        // mailPort is invalid
        try
        {
            Properties properties = new Properties();
            properties.setProperty(prefix + EmailNotifier.PROP_MAIL_HOST, "host");
            properties.setProperty(prefix + EmailNotifier.PROP_MAIL_TO, "to");
            properties.setProperty(prefix + EmailNotifier.PROP_MAIL_PORT, "x");
            new EmailNotifier(properties, prefix, null);
            fail("mailPort is invalid");
        }
        catch (InvalidPropertyException e)
        {
            // success
        }

        // mailTls is invalid
        try
        {
            Properties properties = new Properties();
            properties.setProperty(prefix + EmailNotifier.PROP_MAIL_HOST, "host");
            properties.setProperty(prefix + EmailNotifier.PROP_MAIL_TO, "to");
            properties.setProperty(prefix + EmailNotifier.PROP_MAIL_TLS, "x");
            new EmailNotifier(properties, prefix, null);
            fail("mailTls is invalid");
        }
        catch (InvalidPropertyException e)
        {
            // success
        }

    }

    @Test
    public void testContents1() throws Exception
    {
        String message = "This is the message";

        MockEmailNotifier notifier = new MockEmailNotifier();
        assertTrue(notifier.notify(this, "test", "subject", message, null));
        MockEmailSender sender = notifier.sender;

        assertEquals("subject", sender.subject);
        assertTrue(sender.contents.indexOf(message) >= 0);
    }

    @Test
    public void testContents2() throws Exception
    {
        MockEmailNotifier notifier = new MockEmailNotifier();
        assertTrue(notifier.notify(this, "test", "subject", null, new NullPointerException()));
        MockEmailSender sender = notifier.sender;

        assertEquals("subject", sender.subject);
        assertTrue(sender.contents.indexOf("NullPointerException") >= 0);
    }

    @Test
    public void testFailoverSuccess() throws Exception
    {
        MockEmailNotifier notifier = new MockEmailNotifier(new EmailException(), new MockNotifier());
        assertTrue(notifier.notify(this, "test", "subject", "message", null));
    }

    @Test
    public void testFailoverFailure() throws Exception
    {
        MockEmailNotifier notifier = new MockEmailNotifier(new EmailException(), null);
        assertFalse(notifier.notify(this, "test", "subject", "message", null));
    }

    @Test
    public void testReal() throws Exception
    {
        HostProperties properties = HostProperties.loadHostProperties("/Test.properties");
        EmailNotifier notifier = new EmailNotifier(properties, null) {

            @Override
            protected EmailSender createSender()
            {
                return new EmailSenderImpl() {

                    @Override
                    protected SimpleEmail _newSimpleEmail()
                    {
                        return new SimpleEmail()
                        {
                            @Override
                            public String send() throws EmailException
                            {
                                return null; // Don't actually send
                            }

                        };
                    }
                    
                };
            }
            
        };

        try
        {
            throw new NullPointerException();
        }
        catch (Exception e)
        {
            assertTrue(notifier.notify(this,
                                       "test",
                                       "EmailNotifierTest.testReal",
                                       "Unit test result catching NullPointerException",
                                       e));
        }
    }

    private static class MockEmailNotifier extends EmailNotifier
    {
        public MockEmailSender sender;

        public MockEmailNotifier() throws IOException
        {
            sender = new MockEmailSender();
        }

        public MockEmailNotifier(Properties properties, Notifier failover)
        {
            super(properties, failover);
            sender = new MockEmailSender();
        }

        public MockEmailNotifier(EmailException sendException, Notifier failover)
            throws IOException
        {
            super(failover);
            sender = new MockEmailSender(sendException);
        }

        @Override
        protected EmailSender createSender()
        {
            super.createSender(); // cover that function
            return sender;
        }
    }

    private static class MockEmailSender extends EmailSenderImpl
    {
        private EmailException sendException;
        public String          to;
        public String          from;
        public String          host;
        public String          user;
        public String          password;
        public String          subject;
        public String          contents;
        public Integer         port;
        public Boolean         tls;

        public MockEmailSender()
        {
        }

        public MockEmailSender(EmailException sendException)
        {
            this.sendException = sendException;
        }

        @Override
        public void send() throws EmailException
        {
            if (sendException != null)
            {
                throw sendException;
            }
        }

        @Override
        public void addTo(String emailAddress)
        {
            to = emailAddress;
        }

        @Override
        public void setFrom(String emailAddress)
        {
            from = emailAddress;
        }

        @Override
        public void setMailHost(String host)
        {
            this.host = host;
        }

        @Override
        public void setMailHostAuthentication(String user, String password)
        {
            this.user = user;
            this.password = password;
        }

        @Override
        public void setSubject(String subject)
        {
            this.subject = subject;
        }

        @Override
        public void setTextContents(String textContents)
        {
            contents = textContents;
        }

        @Override
        public void setTls(Boolean tls)
        {
            this.tls = tls;
        }

        @Override
        public void setPort(Integer port)
        {
            this.port = port;
        }

    }
}
