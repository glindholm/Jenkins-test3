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

package net.sourceforge.wsup.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * This class extends the {@link PrefixedProperties} class to allow
 * host-specific properties. A <code>Properties</code> file may be coded using
 * the following format:
 *
 * <pre>
 * HOST.machine1.property=value if executed on machine1
 * HOST.machine2.property=value if executed on machine2
 * property=default value if executed on a machine without an override
 * etc
 * </pre>
 *
 * @author Kevin Hunter
 *
 */
public class HostProperties extends PrefixedProperties
{
    private static final long   serialVersionUID = -2180861214773029504L;

    private static final String HOST_PREFIX      = "HOST.";

    private final String        hostName;

    /**
     * Default constructor. The machine name will be determined using
     * <code>InetAddress.getLocalHost().getHostName()</code> unless {@link #getHostName()} is
     * overridden.
     */
    public HostProperties()
    {
        this(null);
    }

    /**
     * Constructor that allows a machine name to be specified "manually."
     *
     * @param hostName
     *            Machine name to use.
     */
    public HostProperties(String hostName)
    {
        this.hostName = hostName;
    }

    /**
     * This routine returns the machine name that should be used. The default
     * implementation returns the <code>hostName</code> from the {@link #HostProperties(String)}
     * constructor (if non-<code>null</code>),
     * or uses <code>InetAddress.getLocalHost().getHostName().toLowerCase()</code> to
     * determine the machine name if no host name was provided during
     * construction.
     *
     * @return String used to identify this particular host.
     * @throws UnknownHostException
     *             If the call to <code>InetAddress.getLocalHost().getHostName()</code> throws
     */
    public String getHostName() throws UnknownHostException
    {
        if (hostName != null)
        {
            return hostName;
        }

        return getLocalHostName().toLowerCase();
    }

    /**
     * @see net.sourceforge.wsup.core.PrefixedProperties#getPrimaryPrefix()
     */
    @Override
    public String getPrimaryPrefix()
    {
        return HOST_PREFIX;
    }

    /**
     * @see net.sourceforge.wsup.core.PrefixedProperties#getSecondaryPrefix()
     */
    @Override
    public String getSecondaryPrefix()
    {
        try
        {
            return getHostName() + ".";
        }
        catch (UnknownHostException e)
        {
            throw new RuntimeException("Can't get local host name", e);
        }
    }

    /**
     * Utility method to load host-specific properties from an <code>InputStream</code> using the
     * current host's name.
     *
     * @param stream
     *            <code>InputStream</code> from which to load properties.
     * @return <code>HostProperties</code> instance
     * @throws IOException
     */
    public static HostProperties loadHostProperties(InputStream stream) throws IOException
    {
        return loadHostProperties(stream, null);
    }

    /**
     * Utility method to load host-specific properties from an <code>InputStream</code>
     *
     * @param stream
     *            <code>InputStream</code> from which to load properties.
     * @param hostName
     *            Host name to use while loading. (May be <code>null</code> to
     *            use current host's name)
     * @return <code>HostProperties</code> instance
     * @throws IOException
     */
    public static HostProperties loadHostProperties(InputStream stream, String hostName)
        throws IOException
    {
        HostProperties properties = new HostProperties(hostName);
        properties.load(stream);
        return properties;
    }

    /**
     * Utility method to load host-specific properties from an <code>Reader</code> using the current
     * host's name.
     *
     * @param reader
     *            <code>Reader</code> from which to load properties.
     * @return <code>HostProperties</code> instance
     * @throws IOException
     */
    public static HostProperties loadHostProperties(Reader reader) throws IOException
    {
        return loadHostProperties(reader, null);
    }

    /**
     * Utility method to load host-specific properties from an <code>Reader</code>
     *
     * @param reader
     *            <code>Reader</code> from which to load properties.
     * @param hostName
     *            Host name to use while loading. (May be <code>null</code> to
     *            use current host's name)
     * @return <code>HostProperties</code> instance
     * @throws IOException
     */
    public static HostProperties loadHostProperties(Reader reader, String hostName)
        throws IOException
    {
        HostProperties properties = new HostProperties(hostName);
        properties.load(reader);
        return properties;
    }

    /**
     * Utility method to load host-specific properties from a a resource path in
     * the application. <code>getResourceAsStream</code> is used to find the
     * appropriate object in the classpath, and the current machine's host name
     * is used to filter the properties.
     *
     * @param resourcePath
     *            <code>String</code> specifying a resource path to be used (via
     *            <code>getResourceAsStream</code>) to load the properties file.
     * @return <code>HostProperties</code> instance
     * @throws IOException
     */
    public static HostProperties loadHostProperties(String resourcePath) throws IOException
    {
        return loadHostProperties(resourcePath, null);
    }

    /**
     * Utility method to load host-specific properties from a a resource path in
     * the application. <code>getResourceAsStream</code> is used to find the
     * appropriate object in the classpath.
     *
     * @param resourcePath
     *            <code>String</code> specifying a resource path to be used (via
     *            <code>getResourceAsStream</code>) to load the properties file.
     * @param hostName
     *            Host name to use while loading. (May be <code>null</code> to
     *            use current host's name)
     * @return <code>HostProperties</code> instance
     * @throws IOException
     */
    public static HostProperties loadHostProperties(String resourcePath, String hostName)
        throws IOException
    {
        InputStream stream = HostProperties.class.getResourceAsStream(resourcePath);
        if (stream == null)
        {
            throw new FileNotFoundException(resourcePath);
        }

        HostProperties properties;

        try
        {
            properties = loadHostProperties(stream, hostName);
        }
        finally
        {
            stream.close();
        }

        return properties;
    }

    /**
     * Utility method that returns the name of the host computer.
     *
     * @return name of the host computer
     * @throws UnknownHostException
     */
    public static String getLocalHostName() throws UnknownHostException
    {
        return InetAddress.getLocalHost().getHostName();
    }

    /**
     * Gets the property value for <code>key</code> as an <code>Integer</code>. If the property
     * value is missing or blank the <code>defaultValue</code> is returned.
     *
     * @param key the key
     * @param defaultValue the default value
     * @return the property value or <code>defaultValue</code> if missing or blank
     * @throws InvalidPropertyException if the value does not convert to an Integer
     */
    public Integer getIntegerProperty(String key, Integer defaultValue)
    {
        return PropertiesUtils.getIntegerProperty(this, key, defaultValue);
    }

    /**
     * Gets the property value for <code>key</code> as an <code>int</code>. If the property value is
     * missing or blank the <code>defaultValue</code> is returned.
     *
     * @param key the key
     * @param defaultValue the default value
     * @return the property value or <code>defaultValue</code> if missing or blank
     * @throws InvalidPropertyException if the value does not convert to an int
     */
    public int getIntProperty(String key, int defaultValue)
    {
        return PropertiesUtils.getIntProperty(this, key, defaultValue);
    }

    /**
     * Gets the property value for <code>key</code> as a <code>Long</code>. If the property
     * value is missing or blank the <code>defaultValue</code> is returned.
     *
     * @param key the key
     * @param defaultValue the default value
     * @return the property value or <code>defaultValue</code> if missing or blank
     * @throws InvalidPropertyException if the value does not convert to a Long
     */
    public Long getLongProperty(String key, Long defaultValue)
    {
        return PropertiesUtils.getLongProperty(this, key, defaultValue);
    }

    /**
     * Gets the property value for <code>key</code> as a <code>Boolean</code>. If the property
     * value is missing or blank the <code>defaultValue</code> is returned. Valid values are case
     * insensitive <code>"true"</code> or <code>"false"</code>.
     *
     * @param key the key
     * @param defaultValue the default value
     * @return the property value or <code>defaultValue</code> if missing or blank
     * @throws InvalidPropertyException if the value does not convert to a Boolean
     */
    public Boolean getBooleanProperty(String key, Boolean defaultValue)
    {
        return PropertiesUtils.getBooleanProperty(this, key, defaultValue);
    }
}
