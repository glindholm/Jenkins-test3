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

import java.io.File;
import java.net.URL;

import javax.activation.DataSource;

import org.apache.commons.mail.EmailException;

/**
 * This interface specifies a class that can be used to send emails in
 * either HTML, text or mixed formats. It supports embedding files for
 * HTML emails, as well as attachments for either HTML or text emails.
 *
 * @author Kevin
 *
 */
public interface EmailSender
{
    /**
     * Set the host name (and optional port) of the SMTP through which the email should be relayed.
     *
     * @param host SMTP server name and optional port (e.g. "myhost:25")
     */
    void setMailHost(String host);

    /**
     * Sets the SMTP port (default is 25). The port can also be set by {@link #setMailHost(String)},
     * if both methods
     * are used this one takes priority.
     *
     * @param port the port number
     */
    void setPort(Integer port);

    /**
     * Set TLS (transport Layer Security)
     *
     * @param tls true is TLS is to be used
     */
    void setTls(Boolean tls);

    /**
     * Set the authentication for the SMTP server.
     *
     * @param user Username
     * @param password Passowrd
     */
    void setMailHostAuthentication(String user, String password);

    /**
     * Add a destination address to the email
     *
     * @param emailAddress Email address
     */
    void addTo(String emailAddress);

    /**
     * Add a destination address to the email
     *
     * @param emailAddress Email address
     * @param name Optional name
     */
    void addTo(String emailAddress, String name);

    /**
     * Add a "carbon copy" address to the email
     *
     * @param emailAddress Email address
     */
    void addCC(String emailAddress);

    /**
     * Add a "carbon copy" address to the email
     *
     * @param emailAddress Email address
     * @param name Optional name
     */
    void addCC(String emailAddress, String name);

    /**
     * Add a "blind carbon copy" address to the email
     *
     * @param emailAddress Email address
     */
    void addBCC(String emailAddress);

    /**
     * Add a "blind carbon copy" address to the email
     *
     * @param emailAddress Email address
     * @param name Optional name
     */
    void addBCC(String emailAddress, String name);

    /**
     * Set the "from" email address
     *
     * @param emailAddress Email address
     */
    void setFrom(String emailAddress);

    /**
     * Set the "from" email address
     *
     * @param emailAddress Email address
     * @param name Optional name
     */
    void setFrom(String emailAddress, String name);

    /**
     * Set the "reply to" email address
     *
     * @param emailAddress Email address
     */
    void setReplyTo(String emailAddress);

    /**
     * Set the "reply to" email address
     *
     * @param emailAddress Email address
     * @param name Optional name
     */
    void setReplyTo(String emailAddress, String name);

    /**
     * Set the "bounce" address - the address to which undeliverable mail will be sent
     *
     * @param emailAddress Email address
     */
    void setBounceAddress(String emailAddress);

    /**
     * Set the subject for the email
     *
     * @param subject Subject
     */
    void setSubject(String subject);

    /**
     * Set the plain text contents of the email. This will be the content
     * if {@link #setHtmlContents(String)} is not also called. If HTML
     * is also provided, this is the "backup" for email readers that don't
     * support HTML.
     *
     * @param textContents Plain text contents
     */
    void setTextContents(String textContents);

    /**
     * Set the HTML contents of the email.
     *
     * @param htmlContents HTML contents
     */
    void setHtmlContents(String htmlContents);

    /**
     * Add an embedded file to an HTML email.
     *
     * @param cid This is the "URL" via which the embedded file is referenced within
     *            the HTML. For example, to embed an image, one would include in
     *            the HTML a tag like this: <code>&lt;img src='cid:SOME_STRING'&gt;</code> and pass
     *            "SOME_STRING" as this parameter.
     * @param name Name of the embedded file.
     * @param dataSource <code>DataSource</code> for the file data.
     */
    void addEmbeddedFile(String cid, String name, DataSource dataSource);

    /**
     * Convenience method to embed a file given its URL.
     *
     * @param cid See {@link #addEmbeddedFile(String, String, DataSource)}
     * @param name Name of the embedded file
     * @param source <code>URL</code> for the contents.
     */
    void addEmbeddedFile(String cid, String name, URL source);

    /**
     * Convenience method to embed a <code>File</code>.
     *
     * @param cid See {@link #addEmbeddedFile(String, String, DataSource)}
     * @param name Name of the embedded file
     * @param source <code>File</code> to be embedded
     */
    void addEmbeddedFile(String cid, String name, File source);

    /**
     * Add an attachment to the email
     *
     * @param dataSource <code>DataSource</code> for the file
     * @param name Name of the file
     * @param description Description of the file
     */
    void addAttachment(DataSource dataSource, String name, String description);

    /**
     * Add an attachment to the email
     *
     * @param source <code>URL</code> for the file cotents
     * @param name Name of the attachment file
     * @param description Description of the file
     */
    void addAttachment(URL source, String name, String description);

    /**
     * Add an attachment to the email
     *
     * @param source <code>File</code> to be attached
     * @param name Name of the file
     * @param description Description of the file
     */
    void addAttachment(File source, String name, String description);

    /**
     * Send the email.
     *
     * @throws EmailException
     */
    void send() throws EmailException;

}
