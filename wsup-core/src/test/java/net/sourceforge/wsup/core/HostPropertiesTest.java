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

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.UnknownHostException;
import java.util.Properties;

import org.junit.Test;

public class HostPropertiesTest
{
    public HostPropertiesTest()
    {
    }

    @Test
    public void testResourcePath() throws Exception
    {
        Properties p;

        // force machine name "a"
        p = HostProperties
            .loadHostProperties("/net/sourceforge/wsup/core/HostPropertiesTest.properties", "a");
        assertEquals("aaa", p.getProperty("key"));

        // force machine name "b"
        p = HostProperties
            .loadHostProperties("/net/sourceforge/wsup/core/HostPropertiesTest.properties", "b");
        assertEquals("bbb", p.getProperty("key"));

        // use local machine name
        p = HostProperties
            .loadHostProperties("/net/sourceforge/wsup/core/HostPropertiesTest.properties");
        assertEquals("ccc", p.getProperty("key"));
    }

    private static final String SIMULATED_FILE = "HOST.a.key=aaa\nHOST.b.key=bbb\nkey=ccc";

    @Test
    public void testInputStream() throws Exception
    {
        Properties p;

        // force machine name "a"
        p = HostProperties.loadHostProperties(createInputStream(SIMULATED_FILE), "a");
        assertEquals("aaa", p.getProperty("key"));

        // force machine name "b"
        p = HostProperties.loadHostProperties(createInputStream(SIMULATED_FILE), "b");
        assertEquals("bbb", p.getProperty("key"));

        // use local machine name
        p = HostProperties.loadHostProperties(createInputStream(SIMULATED_FILE));
        assertEquals("ccc", p.getProperty("key"));
    }

    @Test
    public void testReader() throws Exception
    {
        Properties p;

        // force machine name "a"
        p = HostProperties.loadHostProperties(createReader(SIMULATED_FILE), "a");
        assertEquals("aaa", p.getProperty("key"));

        // force machine name "b"
        p = HostProperties.loadHostProperties(createReader(SIMULATED_FILE), "b");
        assertEquals("bbb", p.getProperty("key"));

        // use local machine name
        p = HostProperties.loadHostProperties(createReader(SIMULATED_FILE));
        assertEquals("ccc", p.getProperty("key"));
    }

    @Test(expected = IOException.class)
    public void testResourcePathNotFound() throws Exception
    {
        HostProperties.loadHostProperties("/no/such/path.properties");
    }

    @Test(expected = RuntimeException.class)
    public void testNameBlowup() throws Exception
    {
        StringReader reader = new StringReader("a=b");
        new NameThrowingHostProperties().load(reader);
    }

    @Test
    public void testGetType()
    {
        HostProperties p = new HostProperties();

        p.setProperty("key", "1");
        assertEquals(Integer.valueOf(1), p.getIntegerProperty("key", null));

        p.setProperty("key", "1");
        assertEquals(1, p.getIntProperty("key", 2));

        p.setProperty("key", "1");
        assertEquals(Long.valueOf(1), p.getLongProperty("key", null));

        p.setProperty("key", "true");
        assertEquals(Boolean.TRUE, p.getBooleanProperty("key", null));
    }

    private InputStream createInputStream(String simulatedFile)
    {
        return new ByteArrayInputStream(simulatedFile.getBytes());
    }

    private Reader createReader(String simulatedFile)
    {
        return new StringReader(simulatedFile);
    }

    /*
     * Class that simulates the problem of not being able to get the current host name
     */
    private static class NameThrowingHostProperties extends HostProperties
    {
        private static final long serialVersionUID = -1025890804744937950L;

        public NameThrowingHostProperties()
        {
        }

        @Override
        public String getHostName() throws UnknownHostException
        {
            throw new UnknownHostException("Boom!");
        }
    }
}
