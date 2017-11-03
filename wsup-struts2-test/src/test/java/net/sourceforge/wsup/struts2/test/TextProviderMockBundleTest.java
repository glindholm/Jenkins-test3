/*
 *  Copyright (c) 2011 Kevin Hunter
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

package net.sourceforge.wsup.struts2.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.junit.Before;
import org.junit.Test;

import com.opensymphony.xwork2.util.ValueStack;

public class TextProviderMockBundleTest
{
    private TextProviderMockBundle obj;
    
    public TextProviderMockBundleTest()
    {
    }
    
    @Before
    public void setup()
    {
        ResourceBundle bundle = ResourceBundle.getBundle("TestBundle");
        obj = new TextProviderMockBundle(bundle);
    }
    
    @Test
    public void testAccesses()
    {
        assertTrue(obj.hasKey("one"));
        assertFalse(obj.hasKey("nope"));
        
        assertEquals("one", obj.getText("one"));
        assertEquals("one", obj.getText("one", "other"));
        assertEquals("other", obj.getText("three", "other"));
    }
    
    @Test
    public void testFillin()
    {
        List<Object> list = new ArrayList<Object>();
        list.add("a");
        String[] array = new String[1];
        array[0] = "a";
               
        assertEquals("two a", obj.getText("two", array));
        assertEquals("two a", obj.getText("two", list));
        assertEquals("two a", obj.getText("two", "default", array));
        assertEquals("two a", obj.getText("two", "default", list));
        assertEquals("two a", obj.getText("two", "default", "a"));
    }
    
    @Test
    public void testBundle()
    {
        assertNotNull(obj.getTexts());
        assertNotNull(obj.getTexts("TestBundle"));
    }
    
    @Test(expected=RuntimeException.class)
    public void coverNotImplemented1()
    {
        obj.getText("one", "default", new ArrayList<Object>(), (ValueStack)null);
    }
    
    @Test(expected=RuntimeException.class)
    public void coverNotImplemented2()
    {
        obj.getText("one", "default", new String[0], (ValueStack)null);
    }
}
