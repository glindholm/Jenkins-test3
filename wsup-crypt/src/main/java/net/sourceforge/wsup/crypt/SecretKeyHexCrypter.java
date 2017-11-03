/*
 * Copyright (c) 2012 Kevin Hunter
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sourceforge.wsup.crypt;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import net.sourceforge.wsup.core.EncodeUtils;
import net.sourceforge.wsup.core.RequiredCharsets;

/**
 * Encryption/decription class that implements both the <code>Crypter</code> and
 * <code>HexCrypter</code> interfaces.
 * 
 * @author Kevin Hunter
 * @see Crypter
 * @see HexCrypter
 */
public class SecretKeyHexCrypter implements Crypter, HexCrypter
{
	private String algorithm;
	private SecretKeySpec secretKeySpec;

	/**
	 * Constructor for the class.
	 * 
	 * @param algorithm <code>String</code> containing the algorithm to be used.
	 * @param password <code>byte</code> array containing the password data.
	 *            Must match the requirements of the algorithm.
	 */
	public SecretKeyHexCrypter(String algorithm, byte[] password)
	{
		try
		{
			this.algorithm = algorithm;
			this.secretKeySpec = new SecretKeySpec(password, algorithm);
		}
		catch (Exception e)
		{
			throw new CryptException("Error building secret key", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.wsup.crypt.Crypter#encrypt(byte[])
	 */
	@Override
	public byte[] encrypt(byte[] clearBytes)
	{
		if (clearBytes == null)
		{
			return null;
		}

		try
		{
			Cipher cipher = Cipher.getInstance(algorithm);
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
			return cipher.doFinal(clearBytes);
		}
		catch (Exception e)
		{
			throw new CryptException("Error encrypting", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.wsup.crypt.Crypter#decrypt(byte[])
	 */
	@Override
	public byte[] decrypt(byte[] encryptedBytes)
	{
		if (encryptedBytes == null)
		{
			return null;
		}

		try
		{
			Cipher cipher = Cipher.getInstance(algorithm);
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
			return cipher.doFinal(encryptedBytes);
		}
		catch (Exception e)
		{
			throw new CryptException("Error encrypting", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.wsup.crypt.Crypter#encryptString(String)
	 */
	@Override
	public byte[] encryptString(String input)
	{
		if (input == null)
		{
			return null;
		}

		return encrypt(input.getBytes(RequiredCharsets.CHARSET_UTF_8));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.wsup.crypt.Crypter#decryptString(byte[])
	 */
	@Override
	public String decryptString(byte[] encrypted)
	{
		if (encrypted == null)
		{
			return null;
		}

		byte[] decrypted = decrypt(encrypted);
		return new String(decrypted, RequiredCharsets.CHARSET_UTF_8);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.wsup.crypt.Crypter#encryptToHex(byte[])
	 */
	@Override
	public String encryptToHex(byte[] clearBytes)
	{
		byte[] encrypted = encrypt(clearBytes);
		return EncodeUtils.toHex(encrypted);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.wsup.crypt.Crypter#decryptFromHex(String)
	 */
	@Override
	public byte[] decryptFromHex(String hexString)
	{
		if (hexString == null)
		{
			return null;
		}

		byte[] encrypted = EncodeUtils.fromHex(hexString);
		if (encrypted == null)
		{
			throw new CryptException("Input string was not hex string");
		}

		return decrypt(encrypted);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.wsup.crypt.Crypter#encryptStringToHex(String)
	 */
	@Override
	public String encryptStringToHex(String clearString)
	{
		if (clearString == null)
		{
			return null;
		}

		return encryptToHex(clearString.getBytes(RequiredCharsets.CHARSET_UTF_8));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.wsup.crypt.Crypter#decryptStringFromHex(String)
	 */
	@Override
	public String decryptStringFromHex(String encryptedHexString)
	{
		byte[] decryptedBytes = decryptFromHex(encryptedHexString);
		if (decryptedBytes == null)
		{
			return null;
		}

		return new String(decryptedBytes, RequiredCharsets.CHARSET_UTF_8);
	}

	/**
	 * Factory method that will build an object using the <code>AES</code>
	 * algorithm.
	 * 
	 * @param passwordBytes Password data. Must be exactly 16 bytes long.
	 * @return Instance of <code>SecretKeyHexCrypter</code>.
	 */
	public static SecretKeyHexCrypter buildAESCrypter(byte[] passwordBytes)
	{
		return new SecretKeyHexCrypter("AES", passwordBytes);
	}

	/**
	 * Factory method that will build an object using the <code>AES</code>
	 * algorithm.
	 * 
	 * @param password Password data. Must result in a byte array exactly
	 *            16 bytes long when converted using UTF-8.
	 * @return Instance of <code>SecretKeyHexCrypter</code>.
	 */
	public static SecretKeyHexCrypter buildAESCrypter(String password)
	{
		byte[] passwordBytes = password.getBytes(RequiredCharsets.CHARSET_UTF_8);
		return buildAESCrypter(passwordBytes);
	}
}
