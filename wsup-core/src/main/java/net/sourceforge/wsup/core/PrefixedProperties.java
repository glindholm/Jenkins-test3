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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Enumeration;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

/**
 * This class extends <code>java.util.Properties</code> by allowing a single
 * file to provide multiple versions of a particular property which will
 * be selected according to a two-part prefix.  Properties that match the
 * entire prefix will be included, with the prefix stripped off.  Properties
 * that match the first part of the prefix but not the second part will be
 * omitted.
 * <p>
 * The first part of the prefix identifies the entire set of "redundant properties,"
 * while the second part of the prefix identifies the particular subset that should
 * be loaded.
 * </p>
 * <p>
 * Thus, for example, if a file contains:
 * <pre>
 * prop1=123
 * machine.abc.prop2=456
 * machine.def.prop2=789
 * </pre>
 * and the primary prefix is <code>machine.</code> and the secondary prefix is
 * <code>def.</code>, the file will be loaded as if it read
 * <pre>
 * prop1=123
 * prop2=789
 * </pre>
 * <code>prop1</code> does not begin with the primary prefix, so it is passed
 * through unaltered.  <code>machine.abc.prop2</code> and <code>machine.def.prop2</code>
 * both begin with the primary prefix, so are subject to filtering.
 * <code>machine.def.prop2</code> begins with the primary prefix followed by the
 * secondary prefix, so it is loaded, with both prefixes stripped off.
 * <code>machine.abc.prop2</code> begins with the primary but not the secondary
 * prefix, so it is omitted.
 * </p>
 *
 * @author Kevin Hunter
 *
 */
public abstract class PrefixedProperties extends Properties
{
	private static final long serialVersionUID = 5793381285746160303L;

	/**
	 * Base constructor.
	 */
	protected PrefixedProperties()
	{
	}

	/**
	 * Derived classes must override this method in order to provide the primary prefix.
	 * @return	Primary prefix string.
	 */
	public abstract String getPrimaryPrefix();

	/**
	 * Derived classes must override this method in order to provide the secondary prefix.
	 * @return	Secondary prefix string.
	 */
	public abstract String getSecondaryPrefix();

    /**
     * Load this instance from another <code>Properties</code> instance, handling
     * prefixed properties.
     *
     * @param rawProperties	Properties instance, possibly containing
     * 						prefixed properties.
     */
    public void loadFrom(Properties rawProperties)
    {
    	clear();

    	String primaryPrefix = getPrimaryPrefix();
    	String filteringPrefix = getPrimaryPrefix() + getSecondaryPrefix();
    	int filteringPrefixLength = filteringPrefix.length();

    	/*
    	 * Load any properties that don't start with any prefix.
    	 */
    	Enumeration<?> names = rawProperties.propertyNames();
    	while(names.hasMoreElements())
    	{
    		String propertyName = (String)names.nextElement();
    		if (!propertyName.startsWith(primaryPrefix))
    		{
    			setProperty(propertyName, rawProperties.getProperty(propertyName));
    		}
    	}

    	/*
    	 * Load those properties that start with our system prefix,
    	 * stripping off the system prefix.
    	 */
    	names = rawProperties.propertyNames();
    	while(names.hasMoreElements())
    	{
    		String propertyName = (String)names.nextElement();
    		if (propertyName.startsWith(filteringPrefix))
    		{
    			setProperty(propertyName.substring(filteringPrefixLength), rawProperties.getProperty(propertyName));
    		}
    	}
    }

    /**
     * @see Properties#load(InputStream)
     */
	@Override
	public synchronized void load(InputStream inStream) throws IOException
	{
		Properties rawProperties = new Properties();
		rawProperties.load(inStream);
		loadFrom(rawProperties);
	}

    /**
     * @see Properties#load(Reader)
     */
	@Override
	public synchronized void load(Reader reader) throws IOException
	{
		Properties rawProperties = new Properties();
		rawProperties.load(reader);
		loadFrom(rawProperties);
	}

    /**
     * @see Properties#loadFromXML(InputStream)
     */
	@Override
	public synchronized void loadFromXML(InputStream in) throws IOException, InvalidPropertiesFormatException
	{
		Properties rawProperties = new Properties();
		rawProperties.loadFromXML(in);
		loadFrom(rawProperties);
	}
}
