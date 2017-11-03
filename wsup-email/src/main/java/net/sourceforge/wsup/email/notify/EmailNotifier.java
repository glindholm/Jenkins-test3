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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

import net.sourceforge.wsup.core.HostProperties;
import net.sourceforge.wsup.core.PropertiesUtils;
import net.sourceforge.wsup.core.notify.Notifier;
import net.sourceforge.wsup.email.EmailConfig;
import net.sourceforge.wsup.email.EmailSender;
import net.sourceforge.wsup.email.EmailSenderImpl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.EmailException;

/**
 * <code>Notifier</code> class that sends notifications via email.
 *
 * @author Kevin Hunter
 * @author Greg Lindholm
 *
 */
public class EmailNotifier implements Notifier
{
    /**
     * Base for all the properties for this object. This string is prepended
     * to the other property names before the property is retrieved.
     */
    public static final String PROP_BASE          = "net.sourceforge.wsup.email.notify.EmailNotifier.";
    /**
     * Property identifying the system to which the mail should be sent,
     * or through which it should be relayed. Formats:
     *
     * <pre>
     * host.com
     * host.com:port
     * </pre>
     */
    public static final String PROP_MAIL_HOST     = "mailHost";

    /**
     * Identified the mail port (integer).
     */
    public static final String PROP_MAIL_PORT     = "mailPort";

    /**
     * Identified if TLS (Transport Layer Security) is to be used.
     */
    public static final String PROP_MAIL_TLS      = "mailTls";

    /**
     * Property identifying the user name (if any) that will be used to
     * authenticate with the mail host.
     */
    public static final String PROP_MAIL_USER     = "mailUser";
    /**
     * Property identifying the password (if any) that will be used to
     * authenticate with the mail host.
     */
    public static final String PROP_MAIL_PASSWORD = "mailPassword";
    /**
     * Property identifying the email address to which notifications
     * should be sent.
     */
    public static final String PROP_MAIL_TO       = "mailTo";
    /**
     * Property identifying the email address that should be listed
     * as the sender of emails.
     */
    public static final String PROP_MAIL_FROM     = "mailFrom";

    private final Notifier     failoverNotifier;
    private final EmailConfig  emailConfig;
    private final String       mailTo;
    private final String       mailFrom;

    /**
     * Constructor that will take configuration properties from a resource
     * in the classpath named <code>/EmailNotifier.properties</code>.
     *
     * @throws IOException If the properties can't be loaded
     */
    public EmailNotifier() throws IOException
    {
        this(null);
    }

    /**
     * Constructor that will take configuration properties from a resource
     * in the classpath named <code>/EmailNotifier.properties</code>, and which
     * will fail over to another notifier if emails can't be sent.
     *
     * @param failoverNotifier Backup <code>Notifier</code> to be used if
     *            emails can't be sent.
     * @throws IOException If the properties can't be loaded
     */
    public EmailNotifier(Notifier failoverNotifier) throws IOException
    {
        this(HostProperties.loadHostProperties("/EmailNotifier.properties"),
             PROP_BASE,
             failoverNotifier);
    }

    /**
     * Constructor that configures the object from the specified <code>Properties</code> object.
     *
     * @param properties Configuration <code>Properties</code>
     * @param failoverNotifier Backup <code>Notifier</code> to be used if
     *            emails can't be sent.
     */
    public EmailNotifier(Properties properties, Notifier failoverNotifier)
    {
        this(properties, PROP_BASE, failoverNotifier);
    }

    /**
     * Constructor that configures the object from the specified <code>Properties</code> object
     * using using <code>prefix</code> as the properties key prefix.
     *
     * @param properties Configuration <code>Properties</code>
     * @param prefix the properties key prefix
     * @param failoverNotifier Backup <code>Notifier</code> to be used if
     *            emails can't be sent.
     */
    public EmailNotifier(Properties properties, String prefix, Notifier failoverNotifier)
    {
        this.failoverNotifier = failoverNotifier;

        emailConfig = buildEmailConfig(properties, prefix);
        mailTo = StringUtils.trimToNull(properties.getProperty(prefix + PROP_MAIL_TO));
        mailFrom = StringUtils.trimToNull(properties.getProperty(prefix + PROP_MAIL_FROM));

        // fail fast
        if (mailTo == null)
        {
            throw new NullPointerException("Required property is null: " + prefix + PROP_MAIL_TO);
        }
    }

    private static EmailConfig buildEmailConfig(Properties p, String prefix)
    {
        return new EmailConfig(StringUtils.trimToNull(p.getProperty(prefix + PROP_MAIL_HOST)),
                               PropertiesUtils.getIntegerProperty(p, prefix + PROP_MAIL_PORT, null),
                               PropertiesUtils.getBooleanProperty(p, prefix + PROP_MAIL_TLS, null),
                               StringUtils.trimToNull(p.getProperty(prefix + PROP_MAIL_USER)),
                               StringUtils.trimToNull(p.getProperty(prefix + PROP_MAIL_PASSWORD)));
    }

    /**
     * @see Notifier#notify(java.lang.Object, java.lang.Object, java.lang.String, java.lang.String,
     *      java.lang.Throwable)
     */
    @Override
    public boolean notify(Object sender,
                          Object code,
                          String subject,
                          String message,
                          Throwable throwable)
    {
        EmailSender emailSender = createSender();

        configureSender(emailSender);

        emailSender.setSubject(subject);
        emailSender.setTextContents(buildContents(message, throwable));

        try
        {
            emailSender.send();
        }
        catch (EmailException e)
        {
            if (failoverNotifier != null)
            {
                return failoverNotifier.notify(emailSender, code, subject, message, throwable);
            }

            return false;
        }

        return true;
    }

    /**
     * Overrideable member that creates an <code>EmailSender</code> object.
     * The default implementation uses <code>EmailSenderImpl</code>
     *
     * @return <code>EmailSender</code> object
     * @see EmailSender
     * @see EmailSenderImpl
     */
    protected EmailSender createSender()
    {
        return new EmailSenderImpl();
    }

    /**
     * Overrideable member that configures the <code>EmailSender</code> object.
     * The default implementation sets the various attributes from the
     * configuration <code>Properties</code> object.
     *
     * @param sender <code>EmailSender</code> to be configured
     * @see EmailSender
     */
    protected void configureSender(EmailSender sender)
    {
        sender.setMailHost(emailConfig.getHost());
        sender.setPort(emailConfig.getPort());
        sender.setTls(emailConfig.getTls());
        sender.setMailHostAuthentication(emailConfig.getUser(), emailConfig.getPassword());

        sender.addTo(mailTo);
        sender.setFrom(mailFrom);
    }

    /**
     * Overrideable member that builds the contents of the email message
     * to be sent.
     *
     * @param message <code>String</code> message passed to <code>Notifier</code>
     * @param throwable <code>Throwable</code> passed to <code>Notifier</code>
     * @return Formatted body of the email message.
     */
    protected String buildContents(String message, Throwable throwable)
    {
        StringWriter writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);

        if (message != null)
        {
            pw.println(message);
        }

        if (throwable != null)
        {
            pw.println("");
            throwable.printStackTrace(pw);
        }

        pw.close();
        return writer.toString();
    }

    public EmailConfig getEmailConfig()
    {
        return emailConfig;
    }

    public String getMailTo()
    {
        return mailTo;
    }

    public String getMailFrom()
    {
        return mailFrom;
    }

}
