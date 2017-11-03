/*
 *  Copyright (c) 2012 Kevin Hunter
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

package net.sourceforge.wsup.crypt;

import static org.junit.Assert.*;
import org.junit.Test;

public class StandardDataHasherTest
{
    public StandardDataHasherTest()
    {
    }
    
    private String toString(byte[] actual)
    {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < actual.length; i++)
        {
            int item = (int)actual[i] & 0xFF;
            builder.append(String.format("%02x", item));
        }
        return builder.toString();
    }
    
    private void assertArrayEquals(String expected, byte[] actual, int expectedLength)
    {
        //assertEquals(expectedLength, actual.length);
        //assertEquals(expectedLength * 2, expected.length());
        String actualString = toString(actual);
        assertEquals(expected, actualString);
    }
    
    @Test
    public void testByte()
    {
        DataHasher md5 = StandardDataHasher.buildMD5Hasher();
        md5.add((byte)1);
        assertArrayEquals("55a54008ad1ba589aa210d2629c1df41", md5.computeHash(), StandardDataHasher.MD5_BYTES);
        DataHasher sha1 = StandardDataHasher.buildSHA1Hasher();
        sha1.add((byte)1);
        assertArrayEquals("bf8b4530d8d246dd74ac53a13471bba17941dff7", sha1.computeHash(), StandardDataHasher.SHA1_BYTES);
    }
    
    @Test
    public void testChar()
    {
        DataHasher md5 = StandardDataHasher.buildMD5Hasher();
        md5.add('a');
        assertArrayEquals("4144e195f46de78a3623da7364d04f11", md5.computeHash(), StandardDataHasher.MD5_BYTES);
        DataHasher sha1 = StandardDataHasher.buildSHA1Hasher();
        sha1.add('a');
        assertArrayEquals("0a04b971b03da607ce6c455184037b660ca89f78", sha1.computeHash(), StandardDataHasher.SHA1_BYTES);
    }
    
    @Test
    public void testShort()
    {
        DataHasher md5 = StandardDataHasher.buildMD5Hasher();
        md5.add((short)3);
        assertArrayEquals("598f4fe64aefab8f00bcbea4c9239abf", md5.computeHash(), StandardDataHasher.MD5_BYTES);
        DataHasher sha1 = StandardDataHasher.buildSHA1Hasher();
        sha1.add((short)3);
        assertArrayEquals("688934845f22049cb14668832efa33d45013b6b9", sha1.computeHash(), StandardDataHasher.SHA1_BYTES);
    }
    
    @Test
    public void testInt()
    {
        DataHasher md5 = StandardDataHasher.buildMD5Hasher();
        md5.add(3);
        assertArrayEquals("edcfae989540fd42e4b8556d5b723bb6", md5.computeHash(), StandardDataHasher.MD5_BYTES);
        DataHasher sha1 = StandardDataHasher.buildSHA1Hasher();
        sha1.add(3);
        assertArrayEquals("8e146c3c4e33449f95a49679795f74f7ae19ecc1", sha1.computeHash(), StandardDataHasher.SHA1_BYTES);
    }
    
    @Test
    public void testLong()
    {
        DataHasher md5 = StandardDataHasher.buildMD5Hasher();
        md5.add(4L);
        assertArrayEquals("f6bd6b3389b872033d462029172c8612", md5.computeHash(), StandardDataHasher.MD5_BYTES);
        DataHasher sha1 = StandardDataHasher.buildSHA1Hasher();
        sha1.add(4L);
        assertArrayEquals("f4533a73e647c710d3ddbfb253de66e1ac8a6891", sha1.computeHash(), StandardDataHasher.SHA1_BYTES);
    }
    
    @Test
    public void testString()
    {
        DataHasher md5 = StandardDataHasher.buildMD5Hasher();
        md5.add("abc");
        assertArrayEquals("900150983cd24fb0d6963f7d28e17f72", md5.computeHash(), StandardDataHasher.MD5_BYTES);
        DataHasher sha1 = StandardDataHasher.buildSHA1Hasher();
        sha1.add("abc");
        assertArrayEquals("a9993e364706816aba3e25717850c26c9cd0d89d", sha1.computeHash(), StandardDataHasher.SHA1_BYTES);
    }
    
    @Test
    public void testArray1()
    {
        DataHasher md5 = StandardDataHasher.buildMD5Hasher();
        md5.add(new byte[]{ 1, 2, 3, 4});
        assertArrayEquals("08d6c05a21512a79a1dfeb9d2a8f262f", md5.computeHash(), StandardDataHasher.MD5_BYTES);
        DataHasher sha1 = StandardDataHasher.buildSHA1Hasher();
        sha1.add(new byte[]{1, 2, 3, 4});
        assertArrayEquals("12dada1fff4d4787ade3333147202c3b443e376f", sha1.computeHash(), StandardDataHasher.SHA1_BYTES);
    }
    
    @Test
    public void testArray2()
    {
        DataHasher md5 = StandardDataHasher.buildMD5Hasher();
        md5.add(new byte[]{ 1, 2, 3, 4}, 1, 2);
        assertArrayEquals("1fab1cffea9b6fd2f36bfb46e182fd77", md5.computeHash(), StandardDataHasher.MD5_BYTES);
        DataHasher sha1 = StandardDataHasher.buildSHA1Hasher();
        sha1.add(new byte[]{ 1, 2, 3, 4}, 1, 2);
        assertArrayEquals("2215d90c8d9b57557cdd6c736ba44d5fd5b41869", sha1.computeHash(), StandardDataHasher.SHA1_BYTES);
    }
    
    @Test(expected=HashException.class)
    public void testBadAlgorithmThrows()
    {
        StandardDataHasher.buildHasher("InvalidAlgorithm");
    }
    
    @Test(expected = HashException.class)
    public void testAddAfterComputeThrows()
    {
        DataHasher md5 = StandardDataHasher.buildMD5Hasher();
        md5.add("abc");
        md5.computeHash();
        md5.add("def");
    }
    
    @Test
    public void testGettingResultTwiceWorks()
    {
        DataHasher md5 = StandardDataHasher.buildMD5Hasher();
        md5.add(new byte[]{ 1, 2, 3, 4}, 1, 2);
        assertArrayEquals("1fab1cffea9b6fd2f36bfb46e182fd77", md5.computeHash(), StandardDataHasher.MD5_BYTES);
        assertArrayEquals("1fab1cffea9b6fd2f36bfb46e182fd77", md5.computeHash(), StandardDataHasher.MD5_BYTES);
    }
}

