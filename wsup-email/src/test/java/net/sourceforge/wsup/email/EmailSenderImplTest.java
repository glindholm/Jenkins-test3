package net.sourceforge.wsup.email;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import junit.framework.TestCase;
import net.sourceforge.wsup.core.HostProperties;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;

public class EmailSenderImplTest extends TestCase
{
	public EmailSenderImplTest()
	{
	}
	
	public void testAddTo()
	{
		EmailSenderImpl cmd = new EmailSenderImpl();
		
		assertNull(cmd.getTo());
		cmd.addTo("a@b.com");
		assertEquals(1, cmd.getTo().size());
		assertEquals("a@b.com", cmd.getTo().get(0).getEmailAddress());
		cmd.addTo("b@b.com", "b");
		assertEquals(2, cmd.getTo().size());
		assertEquals("b@b.com", cmd.getTo().get(1).getEmailAddress());
		assertEquals("b", cmd.getTo().get(1).getName());
	}
	
	public void testAddCC()
	{
		EmailSenderImpl cmd = new EmailSenderImpl();
		
		assertNull(cmd.getCC());
		cmd.addCC("a@b.com");
		assertEquals(1, cmd.getCC().size());
		assertEquals("a@b.com", cmd.getCC().get(0).getEmailAddress());
		cmd.addCC("b@b.com", "b");
		assertEquals(2, cmd.getCC().size());
		assertEquals("b@b.com", cmd.getCC().get(1).getEmailAddress());
		assertEquals("b", cmd.getCC().get(1).getName());
	}
	
	public void testAddBCC()
	{
		EmailSenderImpl cmd = new EmailSenderImpl();
		
		assertNull(cmd.getBCC());
		cmd.addBCC("a@b.com");
		assertEquals(1, cmd.getBCC().size());
		assertEquals("a@b.com", cmd.getBCC().get(0).getEmailAddress());
		cmd.addBCC("b@b.com", "b");
		assertEquals(2, cmd.getBCC().size());
		assertEquals("b@b.com", cmd.getBCC().get(1).getEmailAddress());
		assertEquals("b", cmd.getBCC().get(1).getName());
	}
	
	public void testAttachments()
	{
		EmailSenderImpl cmd = new EmailSenderImpl();
		
		assertNull(cmd.getAttachments());
		URL url = getClass().getResource("/Test.properties");
		cmd.addAttachment(url, "Test.properties", "properties file");
		assertEquals(1, cmd.getAttachments().size());
		cmd.addAttachment(new File("src/test/resources/email/testPhoto.jpg"), "testPhoto.jpg", "description");
		assertEquals(2, cmd.getAttachments().size());
	}
	
	public void testEmbed()
	{
		EmailSenderImpl cmd = new EmailSenderImpl();
		
		assertNull(cmd.getAttachments());
		URL url = getClass().getResource("/Test.properties");
		cmd.addEmbeddedFile("cid1", "name", url);
		assertEquals(1, cmd.getEmbeddedFiles().size());
		cmd.addEmbeddedFile("cid2", "name", new File("src/test/resources/email/testPhoto.jpg"));
		assertEquals(2, cmd.getEmbeddedFiles().size());
	}
	
	public void testOtherStuff()
	{
		EmailSenderImpl cmd = new EmailSenderImpl();
		
		cmd.setFrom("from@sourceforge.net");
		cmd.setMailHost("mail.sourceforge.net");
		cmd.setMailHostAuthentication("user", "password");
		cmd.setReplyTo("nowhere@sourceforge.net");
		cmd.setBounceAddress("bounce@sourceforge.net");
		cmd.setSubject("subject");
		cmd.setTextContents("text");
		cmd.setHtmlContents("html");
		
		assertEquals("from@sourceforge.net", cmd.getFrom().getEmailAddress());
		assertEquals("mail.sourceforge.net", cmd.getMailHost());
		assertEquals("user", cmd.getAuthUser());
		assertEquals("password", cmd.getAuthPassword());
		assertEquals("nowhere@sourceforge.net", cmd.getReplyTo().getEmailAddress());
		assertEquals("bounce@sourceforge.net", cmd.getBounceAddress());
		assertEquals("subject", cmd.getSubject());
		assertEquals("text", cmd.getTextContents());
		assertEquals("html", cmd.getHtmlContents());
	}
	
	private Properties getTestProperties() throws Exception
	{
		HostProperties props = new HostProperties();
		InputStream stream = getClass().getResourceAsStream("/Test.properties");
		assertNotNull(stream);
		props.load(stream);
		stream.close();
		return props;
	}
	
	private static final String HTML =
		"<html><head></head><body><p>This is a test with an image and an attached properties file<br><img src='cid:IMAGE'></p></body></html>";
	public void testSendHtml() throws Exception
	{
		Properties props = getTestProperties();
        EmailSenderImpl cmd = new EmailSenderImpl()
        {

            @Override
            protected HtmlEmail _newHtmlEmail()
            {
                return new HtmlEmail()
                {

                    @Override
                    public String send() throws EmailException
                    {
                        return null; // Don't actually send
                    }

                };
            }

        };
		
		String host = props.getProperty("EmailSenderTest.host");
		
		assertNotNull("Need to define computer-specific test properties", host);
		
		cmd.setMailHost(host);
		
		String mailHostUser = props.getProperty("EmailSenderTest.user");
		String mailHostPass = props.getProperty("EmailSenderTest.password");
		if (mailHostUser != null && mailHostPass != null)
		{
			cmd.setMailHostAuthentication(mailHostUser,mailHostPass);
		}
		cmd.addTo(props.getProperty("EmailSenderTest.to"));
		cmd.setFrom(props.getProperty("EmailSenderTest.from"));
		cmd.setSubject("EmailSenderTest testSendHtml");
		cmd.setHtmlContents(HTML);
		cmd.setTextContents("Substitute text contents");
		cmd.addEmbeddedFile("IMAGE", "the portrait", new File("src/test/resources/email/testPhoto.jpg"));
		URL url = getClass().getResource("/Test.properties");
		cmd.addAttachment(url, "Test.properties", "properties file");
		
		cmd.send();
	}
	
	public void testSendTextWithAttachment() throws Exception
	{
		Properties props = getTestProperties();
        EmailSenderImpl cmd = new EmailSenderImpl()
        {

            @Override
            protected MultiPartEmail _newMultiPartEmail()
            {
                return new MultiPartEmail()
                {

                    @Override
                    public String send() throws EmailException
                    {
                        return null; // Don't actually send
                    }

                };
            }

        };
		
		cmd.setMailHost(props.getProperty("EmailSenderTest.host"));
		
		String mailHostUser = props.getProperty("EmailSenderTest.user");
		String mailHostPass = props.getProperty("EmailSenderTest.password");
		if (mailHostUser != null && mailHostPass != null)
		{
			cmd.setMailHostAuthentication(mailHostUser,mailHostPass);
		}
		cmd.addTo(props.getProperty("EmailSenderTest.to"));
		cmd.setFrom(props.getProperty("EmailSenderTest.from"));
		cmd.setSubject("EmailSenderTest testSendTextWithAttachment");
		cmd.setTextContents("Text email with attachment");
		cmd.addAttachment(new File("src/test/resources/email/testPhoto.jpg"), "testPhoto.jpeg", "description");
		
		cmd.send();
	}
	
	public void testSendPlainText() throws Exception
	{
		Properties props = getTestProperties();
        EmailSenderImpl cmd = new EmailSenderImpl()
        {

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
		
		cmd.setMailHost(props.getProperty("EmailSenderTest.host"));
		
		String mailHostUser = props.getProperty("EmailSenderTest.user");
		String mailHostPass = props.getProperty("EmailSenderTest.password");
		if (mailHostUser != null && mailHostPass != null)
		{
			cmd.setMailHostAuthentication(mailHostUser,mailHostPass);
		}
		cmd.addTo(props.getProperty("EmailSenderTest.to"));
		cmd.setFrom(props.getProperty("EmailSenderTest.from"));
		cmd.setSubject("EmailSenderTest testSendPlainText");
		cmd.setTextContents("Plain text email");
		
		cmd.send();
	}
}
