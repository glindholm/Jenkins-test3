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
import java.util.ArrayList;
import java.util.List;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;

/**
 * Base implementation for the <code>EmailSender</code> classes.
 *
 */
public abstract class EmailSenderBase implements EmailSender
{
    private String             host;
    private String             authUser;
    private String             authPassword;
    private Boolean            tls;
    private Integer            port;
    private List<Address>      to;
    private List<Address>      cc;
    private List<Address>      bcc;
    private Address            from;
    private Address            replyTo;
    private String             bounceAddress;
    private String             subject;
    private String             textContents;
    private String             htmlContents;
    private List<EmbeddedFile> embeddedFiles;
    private List<Attachment>   attachments;

    /**
     * Constructor.
     */
    public EmailSenderBase()
    {
    }

    @Override
    public void setMailHost(String host)
    {
        this.host = host;
    }

    public String getMailHost()
    {
        return host;
    }

    public Boolean getTls()
    {
        return tls;
    }

    @Override
    public void setTls(Boolean tls)
    {
        this.tls = tls;
    }

    public Integer getPort()
    {
        return port;
    }

    @Override
    public void setPort(Integer port)
    {
        this.port = port;
    }

    @Override
    public void setMailHostAuthentication(String user, String password)
    {
        this.authUser = user;
        this.authPassword = password;
    }

    public String getAuthUser()
    {
        return authUser;
    }

    public String getAuthPassword()
    {
        return authPassword;
    }

    @Override
    public void addTo(String emailAddress)
    {
        addTo(emailAddress, null);
    }

    @Override
    public void addTo(String emailAddress, String name)
    {
        if (to == null)
        {
            to = new ArrayList<Address>();
        }

        to.add(new Address(emailAddress, name));
    }

    public List<Address> getTo()
    {
        return to;
    }

    @Override
    public void addCC(String emailAddress)
    {
        addCC(emailAddress, null);
    }

    @Override
    public void addCC(String emailAddress, String name)
    {
        if (cc == null)
        {
            cc = new ArrayList<Address>();
        }

        cc.add(new Address(emailAddress, name));
    }

    public List<Address> getCC()
    {
        return cc;
    }

    @Override
    public void addBCC(String emailAddress)
    {
        addBCC(emailAddress, null);
    }

    @Override
    public void addBCC(String emailAddress, String name)
    {
        if (bcc == null)
        {
            bcc = new ArrayList<Address>();
        }

        bcc.add(new Address(emailAddress, name));
    }

    public List<Address> getBCC()
    {
        return bcc;
    }

    @Override
    public void setFrom(String emailAddress)
    {
        setFrom(emailAddress, null);
    }

    @Override
    public void setFrom(String emailAddress, String name)
    {
        from = new Address(emailAddress, name);
    }

    public Address getFrom()
    {
        return from;
    }

    @Override
    public void setReplyTo(String emailAddress)
    {
        setReplyTo(emailAddress, null);
    }

    @Override
    public void setReplyTo(String emailAddress, String name)
    {
        replyTo = new Address(emailAddress, name);
    }

    public Address getReplyTo()
    {
        return replyTo;
    }

    @Override
    public void setBounceAddress(String bounceAddress)
    {
        this.bounceAddress = bounceAddress;
    }

    public String getBounceAddress()
    {
        return bounceAddress;
    }

    @Override
    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getSubject()
    {
        return subject;
    }

    @Override
    public void setTextContents(String textContents)
    {
        this.textContents = textContents;
    }

    public String getTextContents()
    {
        return textContents;
    }

    @Override
    public void setHtmlContents(String htmlContents)
    {
        this.htmlContents = htmlContents;
    }

    public String getHtmlContents()
    {
        return htmlContents;
    }

    @Override
    public void addEmbeddedFile(String cid, String name, DataSource dataSource)
    {
        if (embeddedFiles == null)
        {
            embeddedFiles = new ArrayList<EmbeddedFile>();
        }
        embeddedFiles.add(new EmbeddedFile(cid, name, dataSource));
    }

    @Override
    public void addEmbeddedFile(String cid, String name, URL source)
    {
        URLDataSource dataSource = new URLDataSource(source);
        addEmbeddedFile(cid, name, dataSource);
    }

    @Override
    public void addEmbeddedFile(String cid, String name, File source)
    {
        FileDataSource dataSource = new FileDataSource(source);
        addEmbeddedFile(cid, name, dataSource);
    }

    public List<EmbeddedFile> getEmbeddedFiles()
    {
        return embeddedFiles;
    }

    @Override
    public void addAttachment(DataSource dataSource, String name, String description)
    {
        if (attachments == null)
        {
            attachments = new ArrayList<Attachment>();
        }

        attachments.add(new Attachment(dataSource, name, description));
    }

    @Override
    public void addAttachment(URL source, String name, String description)
    {
        URLDataSource dataSource = new URLDataSource(source);
        addAttachment(dataSource, name, description);
    }

    @Override
    public void addAttachment(File source, String name, String description)
    {
        FileDataSource dataSource = new FileDataSource(source);
        addAttachment(dataSource, name, description);
    }

    public List<Attachment> getAttachments()
    {
        return attachments;
    }

    public static class Address
    {
        private String emailAddress;
        private String name;

        public Address(String emailAddress, String name)
        {
            this.emailAddress = emailAddress;
            this.name = name;
        }

        public String getEmailAddress()
        {
            return emailAddress;
        }

        public String getName()
        {
            return name;
        }
    }

    public static class EmbeddedFile
    {
        private String     cid;
        private String     name;
        private DataSource dataSource;

        public EmbeddedFile(String cid, String name, DataSource dataSource)
        {
            this.cid = cid;
            this.name = name;
            this.dataSource = dataSource;
        }

        public String getCid()
        {
            return cid;
        }

        public String getName()
        {
            return name;
        }

        public DataSource getDataSource()
        {
            return dataSource;
        }
    }

    public static class Attachment
    {
        private String     description;
        private String     name;
        private DataSource dataSource;

        public Attachment(DataSource dataSource, String name, String description)
        {
            this.description = description;
            this.name = name;
            this.dataSource = dataSource;
        }

        public String getDescription()
        {
            return description;
        }

        public String getName()
        {
            return name;
        }

        public DataSource getDataSource()
        {
            return dataSource;
        }
    }
}
