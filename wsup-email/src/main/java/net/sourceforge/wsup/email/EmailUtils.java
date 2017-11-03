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

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang.StringUtils;

/**
 * Utilities for dealing with email.
 * 
 * @author Kevin Hunter
 * 
 */
public class EmailUtils
{
    /**
     * Validates an email address. This version explicitly requires a
     * domain name. RFC822 defines an email address like "joe" to be legal -
     * for the purpose of this function, however, it is not.
     * 
     * @param emailAddress <code>String</code> containing the email address.
     * @return <code>true</code> if it is valid, <code>false</code> if not.
     */
    public static boolean isValidEmailAddress(String emailAddress)
    {
        String[] tokens = emailAddress.split("@", -1);
        if (tokens.length != 2)
        {
            return false;
        }

        if (StringUtils.trimToNull(tokens[0]) == null)
        {
            return false;
        }

        if (StringUtils.trimToNull(tokens[1]) == null)
        {
            return false;
        }

        try
        {
            new InternetAddress(emailAddress);
        }
        catch (AddressException e)
        {
            return false;
        }

        return true;
    }
    
    private EmailUtils()
    {
    }
    
    /*package*/ static void testCoverage()
    {
        new EmailUtils();
    }
}
