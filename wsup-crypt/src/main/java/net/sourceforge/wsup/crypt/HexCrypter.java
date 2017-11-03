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
 * Interface for an encrypt/decrypt object that can work to and from hex.
 * 
 * @author Kevin Hunter
 * 
 */
public interface HexCrypter
{
	/**
	 * Encrypt data, producing a hex-encoded <code>String</code> as the output.
	 * 
	 * @param clearBytes Input data to be encrypted.
	 * @return <code>String</code> encoded in hex.
	 * @see #decryptFromHex(String)
	 */
	public String encryptToHex(byte[] clearBytes);

	/**
	 * Decrypt data encoded in a hex <code>String</code>.
	 * 
	 * @param hexString Encoded data in hex.
	 * @return Decoded data.
	 * @see #encryptToHex(byte[])
	 */
	public byte[] decryptFromHex(String hexString);

	/**
	 * Encrypt a string, producing the result as a hex string. The input string
	 * is converted to binary using the UTF-8 encoding.
	 * 
	 * @param clearString String to be encrypted.
	 * @return Hex string containing result of encryption.
	 * @see #decryptStringFromHex(String)
	 */
	public String encryptStringToHex(String clearString);

	/**
	 * Decrypt a hex string, producing a <code>String</code> as the result,
	 * using the UTF-8 encoding.
	 * 
	 * @param encryptedHexString Hex <code>String</code> containing encrypted
	 *            data.
	 * @return Decrypted <code>String</code>
	 * @see #encryptStringToHex(String)
	 */
	public String decryptStringFromHex(String encryptedHexString);
}
