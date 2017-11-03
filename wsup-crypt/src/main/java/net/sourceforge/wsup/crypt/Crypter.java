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
 * Interface describing a basic encryption and decryption class.
 * 
 * @author Kevin Hunter
 */
public interface Crypter
{
	/**
	 * Encrypt binary data.
	 * 
	 * @param clearBytes Input binary data.
	 * @return Byte array of encrypted output.
	 */
	public byte[] encrypt(byte[] clearBytes);

	/**
	 * Decrypt binary data.
	 * 
	 * @param encryptedBytes Input encrypted binary data.
	 * @return Byte array of decrypted data.
	 */
	public byte[] decrypt(byte[] encryptedBytes);

	public byte[] encryptString(String input);
	
	public String decryptString(byte[] encryptedData);
}
