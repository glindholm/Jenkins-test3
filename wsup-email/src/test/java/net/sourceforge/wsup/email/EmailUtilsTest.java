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

package net.sourceforge.wsup.email;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit tests for the {@link EmailUtils} class.
 * 
 * @author Kevin Hunter
 * 
 */
public class EmailUtilsTest
{
    public EmailUtilsTest()
    {
    }
    
    private static final String[] INVALID_ADDRESSES =
    {
         "joe",                     // no domain name
         "joe@",                    // no domain name
         "@foo.com",                // no user name
         "joe@foo@com",             // too many @ signs
         "j>e@foo.com",               // invalid character
    };
    
    @Test
    public void testInvalid()
    {
        for (int i = 0; i < INVALID_ADDRESSES.length; i++)
        {
            String testName = "INVALID_ADDRESSES[" + i + "]";
            
            assertFalse(testName, EmailUtils.isValidEmailAddress(INVALID_ADDRESSES[i]));
        }
    }
    
    @Test
    public void testValid()
    {
        assertTrue(EmailUtils.isValidEmailAddress("joe@foo.com"));
    }
    
    @Test
    public void coverConstructor()
    {
        EmailUtils.testCoverage();
    }
}
