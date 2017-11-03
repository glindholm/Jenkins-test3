/*
 *  Copyright (c) 2010 Kevin Hunter
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

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * This class performs manipulations based on the MD5 digest
 * algorithm.
 * 
 * @author Kevin Hunter
 */
public final class MD5
{
    /**
     * Hash a string using MD5. The input string is converted to
     * UTF-8, the corresponding byte stream run through MD5, and
     * the resulting bytes converted back to a string using base 16.
     * Because MD5 produces a 128 bit hash, the resulting string
     * will be 32 characters long.
     * 
     * @param src Input string
     * @return Hashed version of the string.
     * @throws RuntimeException - will wrap any exception thrown
     *             in the process.
     */
    public static String hash(String src)
    {
        BigInteger bigInt = new BigInteger(1, hash(src.getBytes(RequiredCharsets.CHARSET_UTF_8)));
        return bigInt.toString(16);
    }

    /**
     * This implements the raw MD5 hashing process on the input
     * byte array.
     * 
     * @param input Input byte array.
     * @return 16-element byte array containing the MD5 hash.
     * @throws RuntimeException wrapping anything bad that happens.
     *             (The only checked exception that this code wraps is a
     *             <code>NoSuchAlgorithmException</code>, but MD5 is a natively
     *             supported algorithm, so that shouldn't happen.
     */
    public static byte[] hash(byte[] input)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(input);
            return digest.digest();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /* package */static void coverConstructor()
    {
        new MD5();
    }

    private MD5()
    {
    }
}
