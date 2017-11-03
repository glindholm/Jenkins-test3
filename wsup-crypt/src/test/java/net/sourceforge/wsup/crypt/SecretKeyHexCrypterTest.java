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
import net.sourceforge.wsup.core.EncodeUtils;
import net.sourceforge.wsup.core.RequiredCharsets;

import org.junit.Test;

public class SecretKeyHexCrypterTest
{
	private static final String AES_PASSWORD = "1234567890ABCDEF";
	private static final String AES_INPUT = "This is a test";
	private static final String AES_OUTPUT = "04097BCC9EEB57DBC550DC7AB0C71528";
	
	@Test
	public void encryptUsingAES()
	{
		SecretKeyHexCrypter crypter = SecretKeyHexCrypter.buildAESCrypter(AES_PASSWORD);
		byte[] input = AES_INPUT.getBytes(RequiredCharsets.CHARSET_UTF_8);
		byte[] output = crypter.encrypt(input);
		String outputString = EncodeUtils.toHex(output);
		assertEquals(AES_OUTPUT, outputString);
	}
	
	@Test
	public void decryptUsingAES()
	{
		SecretKeyHexCrypter crypter = SecretKeyHexCrypter.buildAESCrypter(AES_PASSWORD);
		byte[] input = EncodeUtils.fromHex(AES_OUTPUT);
		byte[] output = crypter.decrypt(input);
		String outputString = new String(output, RequiredCharsets.CHARSET_UTF_8);
		assertEquals(AES_INPUT, outputString);
	}
	
	@Test
	public void encryptStringUsingAES()
	{
		SecretKeyHexCrypter crypter = SecretKeyHexCrypter.buildAESCrypter(AES_PASSWORD);
		byte[] output = crypter.encryptString(AES_INPUT);
		String outputString = EncodeUtils.toHex(output);
		assertEquals(AES_OUTPUT, outputString);
	}
	
	@Test
	public void decryptStringUsingAES()
	{
		SecretKeyHexCrypter crypter = SecretKeyHexCrypter.buildAESCrypter(AES_PASSWORD);
		byte[] input = EncodeUtils.fromHex(AES_OUTPUT);
		String outputString = crypter.decryptString(input);
		assertEquals(AES_INPUT, outputString);
	}
	
	@Test
	public void encryptToHexUsingAES()
	{
		SecretKeyHexCrypter crypter = SecretKeyHexCrypter.buildAESCrypter(AES_PASSWORD);
		byte[] input = AES_INPUT.getBytes(RequiredCharsets.CHARSET_UTF_8);
		String outputString = crypter.encryptToHex(input);
		assertEquals(AES_OUTPUT, outputString);
	}
	
	@Test
	public void decryptFromHexUsingAES()
	{
		SecretKeyHexCrypter crypter = SecretKeyHexCrypter.buildAESCrypter(AES_PASSWORD);
		byte[] output = crypter.decryptFromHex(AES_OUTPUT);
		String outputString = new String(output, RequiredCharsets.CHARSET_UTF_8);
		assertEquals(AES_INPUT, outputString);
	}
	
	@Test
	public void encryptStringToHexUsingAES()
	{
		SecretKeyHexCrypter crypter = SecretKeyHexCrypter.buildAESCrypter(AES_PASSWORD);
		String outputString = crypter.encryptStringToHex(AES_INPUT);
		assertEquals(AES_OUTPUT, outputString);
	}
	
	@Test
	public void decryptStringFromHexUsingAES()
	{
		SecretKeyHexCrypter crypter = SecretKeyHexCrypter.buildAESCrypter(AES_PASSWORD);
		String outputString = crypter.decryptStringFromHex(AES_OUTPUT);
		assertEquals(AES_INPUT, outputString);
	}
	
	@Test
	public void encryptNullReturnsNull()
	{
		SecretKeyHexCrypter crypter = SecretKeyHexCrypter.buildAESCrypter(AES_PASSWORD);
		assertNull(crypter.encrypt(null));
		assertNull(crypter.encryptString(null));
		assertNull(crypter.encryptToHex(null));
		assertNull(crypter.encryptStringToHex(null));
	}
	
	@Test
	public void decryptNullReturnsNull()
	{
		SecretKeyHexCrypter crypter = SecretKeyHexCrypter.buildAESCrypter(AES_PASSWORD);
		assertNull(crypter.decrypt(null));
		assertNull(crypter.decryptString(null));
		assertNull(crypter.decryptFromHex(null));
		assertNull(crypter.decryptStringFromHex(null));
	}
	
	@Test(expected = CryptException.class)
	public void decryptOddLengthHexThrows()
	{
		SecretKeyHexCrypter crypter = SecretKeyHexCrypter.buildAESCrypter(AES_PASSWORD);
		crypter.decryptFromHex("A");
	}
	
	@Test(expected = CryptException.class)
	public void decryptInvalidHexThrows()
	{
		SecretKeyHexCrypter crypter = SecretKeyHexCrypter.buildAESCrypter(AES_PASSWORD);
		crypter.decryptFromHex("XX");
	}
	
	@Test(expected = CryptException.class)
	public void invalidAlgorithmThrowsDuringEncrypt()
	{
		SecretKeyHexCrypter crypter = new SecretKeyHexCrypter("InvalidAlgorithm", new byte[]{1, 2, 3});
		crypter.encryptStringToHex("abc");
	}
	
	@Test(expected = CryptException.class)
	public void invalidAlgorithmThrowsDuringDecrypt()
	{
		SecretKeyHexCrypter crypter = new SecretKeyHexCrypter("InvalidAlgorithm", new byte[]{1, 2, 3});
		crypter.decryptFromHex("AA");
	}
	
	@Test(expected = CryptException.class)
	public void nullPasswordThrowsDuringConstruct()
	{
		SecretKeyHexCrypter crypter = new SecretKeyHexCrypter("InvalidAlgorithm", null);
		crypter.decryptFromHex("AA");
	}
	
}
