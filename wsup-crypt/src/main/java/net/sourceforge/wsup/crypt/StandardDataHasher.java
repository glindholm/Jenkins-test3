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

import java.security.MessageDigest;

import net.sourceforge.wsup.core.RequiredCharsets;

/**
 * A <code>DataHasher</code> that uses the standard Java
 * <code>MessageDigest</code> class in order to implement the hashing.
 * 
 * @author Kevin Hunter
 * 
 */
public class StandardDataHasher implements DataHasher
{
	/**
	 * Name of the commonly-implemented MD5 algorithm.
	 */
	public static final String ALGORITHM_MD5 = "MD5";
	/**
	 * Name of the commonly-implemented SHA1 algorithm.
	 */
	public static final String ALGORITHM_SHA1 = "SHA1";
	/**
	 * Number of bytes in an MD5 hash.
	 */
	public static final int MD5_BYTES = 16;
	/**
	 * Number of bytes in an SHA1 hash.
	 */
	public static final int SHA1_BYTES = 20;

	private MessageDigest digest;
	private byte[] result;

	/**
	 * Factory method for instances of the class.
	 * @param algorithm String describing the algorithm to be used.
	 * @return Instace of <code>DataHasher</code>
	 * @throws HashException if an invalid or unsupported algorithm is specified.
	 */
	public static StandardDataHasher buildHasher(String algorithm)
	{
		MessageDigest digest = null;
		try
		{
			digest = MessageDigest.getInstance(algorithm);
		}
		catch (Exception e)
		{
			throw new HashException("Invalid algorithm: " + algorithm, e);
		}

		return new StandardDataHasher(digest);
	}

	/**
	 * Factory method to build a MD5 hasher.
	 * @return Instance of <code>StandardDataHasher</code>.
	 */
	public static StandardDataHasher buildMD5Hasher()
	{
		return buildHasher(ALGORITHM_MD5);
	}

	/**
	 * Factory method to build a SHA1 hasher.
	 * @return Instance of <code>StandardDataHasher</code>.
	 */
	public static StandardDataHasher buildSHA1Hasher()
	{
		return buildHasher(ALGORITHM_SHA1);
	}

	private StandardDataHasher(MessageDigest digest)
	{
		this.digest = digest;
	}

	private void throwIfDead()
	{
		if (this.digest == null)
		{
			throw new HashException("Cannot add to digester after computing digest");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.wsup.core.crypt.DataDigester#add(byte)
	 */
	@Override
	public void add(byte data)
	{
		throwIfDead();
		digest.update(data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.wsup.core.crypt.DataDigester#add(char)
	 */
	@Override
	public void add(char data)
	{
		throwIfDead();
		digest.update((byte) data);
		data >>= 8;
		digest.update((byte) data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.wsup.core.crypt.DataDigester#add(short)
	 */
	@Override
	public void add(short data)
	{
		throwIfDead();
		digest.update((byte) data);
		data >>= 8;
		digest.update((byte) data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.wsup.core.crypt.DataDigester#add(int)
	 */
	@Override
	public void add(int data)
	{
		throwIfDead();
		digest.update((byte) data);
		data >>= 8;
		digest.update((byte) data);
		data >>= 8;
		digest.update((byte) data);
		data >>= 8;
		digest.update((byte) data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.wsup.core.crypt.DataDigester#add(long)
	 */
	@Override
	public void add(long data)
	{
		throwIfDead();
		digest.update((byte) data);
		data >>= 8;
		digest.update((byte) data);
		data >>= 8;
		digest.update((byte) data);
		data >>= 8;
		digest.update((byte) data);
		data >>= 8;
		digest.update((byte) data);
		data >>= 8;
		digest.update((byte) data);
		data >>= 8;
		digest.update((byte) data);
		data >>= 8;
		digest.update((byte) data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.wsup.core.crypt.DataDigester#add(byte[])
	 */
	@Override
	public void add(byte[] data)
	{
		throwIfDead();
		digest.update(data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.wsup.core.crypt.DataDigester#add(byte[], int, int)
	 */
	@Override
	public void add(byte[] data, int start, int length)
	{
		throwIfDead();
		digest.update(data, start, length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.wsup.core.crypt.DataDigester#add(java.lang.String)
	 */
	@Override
	public void add(String data)
	{
		throwIfDead();
		digest.update(data.getBytes(RequiredCharsets.CHARSET_UTF_8));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.wsup.core.crypt.DataDigester#computeHash()
	 */
	@Override
	public byte[] computeHash()
	{
		if (digest != null)
		{
			result = digest.digest();
			digest = null;
		}

		return result;
	}
}
