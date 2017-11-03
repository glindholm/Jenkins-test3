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

/**
 * Interface for classes that can compute hash strings (a.k.a. "digests") of
 * arbitrary input data.
 * <p>
 * The general pattern is that one obtains a <code>DataDigester</code>, adds a
 * variety of data to it, and then computes the hash on the data that has been
 * added.
 * </p>
 * 
 * @author Kevin Hunter
 */
public interface DataHasher
{
	/**
	 * Add an <code>byte</code> to the hash.
	 * 
	 * @param data Value to be added to the hash.
	 */
	public void add(byte data);

	/**
	 * Add an <code>char</code> to the hash. The value is added LSB first, MSB
	 * last.
	 * 
	 * @param data Value to be added to the hash.
	 */
	public void add(char data);

	/**
	 * Add an <code>short</code> to the hash. The value is added LSB first, MSB
	 * last.
	 * 
	 * @param data Value to be added to the hash.
	 */
	public void add(short data);

	/**
	 * Add an <code>int</code> to the hash. The value is added LSB first, MSB
	 * last.
	 * 
	 * @param data Value to be added to the hash.
	 */
	public void add(int data);

	/**
	 * Add an <code>long</code> to the hash. The value is added LSB first, MSB
	 * last.
	 * 
	 * @param data Value to be added to the hash.
	 */
	public void add(long data);

	/**
	 * Add the specified binary data to the hash. The bytes are added in
	 * sequence.
	 * 
	 * @param data Data to be added to the hash. Must not be <code>null</code>.
	 */
	public void add(byte[] data);

	/**
	 * Add a portion of a binary array to the hash.
	 * 
	 * @param data Array containing data. Must not be <code>null</code>.
	 * @param start Index of first byte to be added.
	 * @param length Number of bytes to be added.
	 */
	public void add(byte[] data, int start, int length);

	/**
	 * Add a <code>String</code> to the hash. The <code>String</code> will be
	 * converted
	 * into bytes using the UTF-8 encoding.
	 * 
	 * @param data String to be added to the hash. Must not be <code>null</code>
	 *            .
	 */
	public void add(String data);

	/**
	 * Compute the hash value for the data that has been added. Once this method
	 * is called, no further data may be added to the hash.
	 * 
	 * @return Array containing the hash value.
	 */
	public byte[] computeHash();
}
