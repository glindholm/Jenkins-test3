/*
 *  Copyright 2010 Kevin Hunter
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

package net.sourceforge.wsup.email;

import java.util.List;

import net.sourceforge.wsup.core.Ensure;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;

/**
 * Implementation of the {@link EmailSender} interface.
 * This implementation uses the Apache Commons Email as the underlying
 * sending mechanism.
 *
 */
public class EmailSenderImpl extends EmailSenderBase implements EmailSender
{
    /**
     * Constructor.
     */
    public EmailSenderImpl()
    {
    }

    @Override
    public void send() throws EmailException
    {
        List<Address> to = getTo();

        Ensure.isNotNull("'to' is required", to);

        Email email = buildEmail();

        String host = StringUtils.trimToNull(getMailHost());

        if (host != null)
        {
            int colonPos = host.indexOf(':');
            if (colonPos >= 0)
            {
                String hostName = host.substring(0, colonPos);
                int portNumber = Integer.parseInt(host.substring(colonPos + 1));

                email.setHostName(hostName);
                email.setSmtpPort(portNumber);
            }
            else
            {
                email.setHostName(host);
            }
        }

        if (getPort() != null)
        {
            email.setSmtpPort(getPort().intValue());
        }

        if (getTls() != null)
        {
            email.setStartTLSEnabled(getTls().booleanValue());
        }

        String authUser = StringUtils.trimToNull(getAuthUser());
        String authPassword = StringUtils.trimToNull(getAuthPassword());

        if (authUser != null)
        {
            email.setAuthentication(authUser, authPassword);
        }

        for (Address a : to)
        {
            email.addTo(a.getEmailAddress(), a.getName());
        }

        List<Address> cc = getCC();
        if (cc != null)
        {
            for (Address a : cc)
            {
                email.addTo(a.getEmailAddress(), a.getName());
            }
        }

        List<Address> bcc = getBCC();
        if (bcc != null)
        {
            for (Address a : bcc)
            {
                email.addTo(a.getEmailAddress(), a.getName());
            }
        }

        Address from = getFrom();
        if (from != null)
        {
            email.setFrom(from.getEmailAddress(), from.getName());
        }

        Address replyTo = getReplyTo();
        if (replyTo != null)
        {
            email.addReplyTo(replyTo.getEmailAddress(), replyTo.getName());
        }

        String bounceAddress = getBounceAddress();
        if (bounceAddress != null)
        {
            email.setBounceAddress(bounceAddress);
        }

        String subject = getSubject();
        if (subject != null)
        {
            email.setSubject(subject);
        }

        email.send();
    }

    private Email buildEmail() throws EmailException
    {
        String htmlContents = getHtmlContents();
        String textContents = getTextContents();
        List<EmbeddedFile> embeddedFiles = getEmbeddedFiles();
        List<Attachment> attachments = getAttachments();

        if (htmlContents != null)
        {
            HtmlEmail email = _newHtmlEmail();
            email.setHtmlMsg(htmlContents);
            if (textContents != null)
            {
                email.setTextMsg(textContents);
            }

            if (embeddedFiles != null)
            {
                for (EmbeddedFile file : embeddedFiles)
                {
                    email.embed(file.getDataSource(), file.getName(), file.getCid());
                }
            }

            if (attachments != null)
            {
                for (Attachment file : attachments)
                {
                    email.attach(file.getDataSource(), file.getName(), file.getDescription());
                }
            }

            return email;
        }

        Ensure.isNotNull("text message must be non-null in a non-HTML email", textContents);

        if (attachments != null)
        {
            MultiPartEmail email = _newMultiPartEmail();
            email.setMsg(textContents);
            for (Attachment file : attachments)
            {
                email.attach(file.getDataSource(), file.getName(), file.getDescription());
            }

            return email;
        }

        SimpleEmail email = _newSimpleEmail();
        email.setMsg(textContents);
        return email;
    }

    /**
     * Construct a new SimpleEmail. (Override for testing)
     * 
     * @return a newly constructed SimpleEmail
     */
    protected SimpleEmail _newSimpleEmail()
    {
        return new SimpleEmail();
    }

    /**
     * Construct a new MultiPartEmail. (Override for testing)
     * 
     * @return a newly constructed MultiPartEmail
     */
    protected MultiPartEmail _newMultiPartEmail()
    {
        return new MultiPartEmail();
    }

    /**
     * Construct a new HtmlEmail. (Override for testing)
     * 
     * @return a newly constructed HtmlEmail
     */
    protected HtmlEmail _newHtmlEmail()
    {
        return new HtmlEmail();
    }
}
