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

package net.sourceforge.wsup.hibernate4.database;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.wsup.hibernate4.interceptors.DateAwareInterceptor;
import net.sourceforge.wsup.hibernate4.interceptors.MultiplexInterceptor;

import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.cfg.Configuration;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class derives from <code>BaseDatabase</code> and provides
 * an implementation based on an in-memory HQLDB. This particular class is without verification SQL
 * so that we can test some of the fall-throughs. The <code>TestDatabase</code> derived class
 * provides a "complete" test database.
 * 
 * @author Kevin Hunter
 * 
 */
public class TestDatabaseWithoutVerificationSql extends BaseDatabase
{
    private Properties properties;

    public TestDatabaseWithoutVerificationSql()
    {
    }

    @Override
    protected Configuration createConfiguration() throws HibernateException
    {
        Configuration configuration = new Configuration();

        try
        {
            configuration.configure(loadConfigDocument());
            configuration.addProperties(loadProperties());
        }
        catch (Exception e)
        {
            throw new HibernateException("Exception building test database", e);
        }

        return configuration;
    }

    private Document loadConfigDocument()
        throws ParserConfigurationException, SAXException, IOException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        StringReader reader = new StringReader(HIBERNATE_CFG_XML);
        InputSource source = new InputSource(reader);
        return builder.parse(source);
    }

    private Properties loadProperties() throws IOException
    {
        if (properties == null)
        {
            StringReader reader = new StringReader(HIBERNATE_PROPERTIES);
            properties = new Properties();
            properties.load(reader);
        }

        return properties;
    }

    public void initializeTables() throws Exception
    {
        Connection c = getConnection();
        Statement st = c.createStatement();
        for (int i = 0; i < DB_SETUP_SQL.length; i++)
        {
            st.executeUpdate(DB_SETUP_SQL[i]);
        }
        c.close();
    }

    public void shutdown() throws Exception
    {
        Connection c = getConnection();
        Statement st = c.createStatement();
        st.execute("SHUTDOWN");
        c.close();
    }

    @Override
    protected Interceptor getHibernateSessionInterceptor()
    {
        super.getHibernateSessionInterceptor(); // just for code coverage

        DateAwareInterceptor dateAwareInterceptor = new DateAwareInterceptor();

        return new MultiplexInterceptor(dateAwareInterceptor);
    }

    /**
     * Get a JDBC <code>Connection</code>. Note that this goes directly
     * through the <code>DriverManager</code> path, bypassing anything
     * Hibernate-ish. This is important for testing dead database connections
     * in the <code>getVerifiedSession</code> logic.
     * 
     * @return JDBC <code>Connection</code>.
     * @throws Exception
     */
    private Connection getConnection() throws Exception
    {
        Properties properties = loadProperties();

        String driverClass = properties.getProperty("hibernate.connection.driver_class");
        String dbUrl = properties.getProperty("hibernate.connection.url");
        String dbUser = properties.getProperty("hibernate.connection.username");
        String dbPass = properties.getProperty("hibernate.connection.password");

        Class.forName(driverClass).newInstance();
        return DriverManager.getConnection(dbUrl, dbUser, dbPass);
    }

    /*
     * Contents of the hibernate.cfg.xml file that would correspond to our test database.
     * The DOCTYPE is commented out so that it won't have validation issues if the
     * site holding the DTD happens to be offline.
     */
    private static final String   HIBERNATE_CFG_XML    = "<?xml version='1.0' encoding='UTF-8'?>" +
                                                       //      "<!DOCTYPE hibernate-configuration PUBLIC"+
                                                       //      "   '-//Hibernate/Hibernate Configuration DTD 3.0//EN'"+
                                                       //      "   'http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd'>"+
                                                         "<hibernate-configuration>"
                                                         + "	<session-factory>"
                                                         + "		<mapping class='net.sourceforge.wsup.hibernate4.database.TestBean' />"
                                                         + "		<mapping class='net.sourceforge.wsup.hibernate4.database.TestDateBean' />"
                                                         + "	</session-factory>"
                                                         + "</hibernate-configuration>";

    /*
     * Contents of the hibernate.properties that specify our access to the test database.
     */
    private static final String   HIBERNATE_PROPERTIES = "hibernate.dialect=org.hibernate.dialect.HSQLDialect\n"
                                                         + "hibernate.connection.driver_class=org.hsqldb.jdbcDriver\n"
                                                         + "hibernate.connection.url=jdbc:hsqldb:mem:testdb\n"
                                                         + "hibernate.connection.username=sa\n"
                                                         + "hibernate.connection.password=\n"
                                                         + "hibernate.connection.provider_class=org.hibernate.service.jdbc.connections.internal.C3P0ConnectionProvider\n"
                                                         + "hibernate.query.substitutions true 1, false 0, yes 'Y', no 'N'\n"
                                                         + "c3p0.minPoolSize=2\n"
                                                         + "c3p0.maxPoolSize=10\n"
                                                         + "c3p0.acquireIncrement=2\n"
                                                         + "c3p0.initialPoolSize=3\n"
                                                         + "c3p0.maxIdleTime=5000\n"
                                                         + "c3p0.maxStatements=100\n"
                                                         + "c3p0.checkoutTimeout=5000\n";

    /*
     * List of SQL statements that will initialize the tables in the test database.
     */
    private static final String[] DB_SETUP_SQL         = {
        "drop table dateTable if exists;",
        "drop table testTable if exists;",

        "create table dateTable ("
            + "    id bigint generated by default as identity (start with 1),"
            + "    contents varchar(255) not null,"
            + "    other varchar(255) not null,"
            + "    created timestamp not null,"
            + "    updated timestamp not null,"
            + "    version integer not null,"
            + "    primary key (id)"
            + ");",

        "create table testTable ("
            + "    id bigint generated by default as identity (start with 1),"
            + "    contents varchar(255),"
            + "    version integer not null,"
            + "    primary key (id)"
            + ");",                                   };
}
