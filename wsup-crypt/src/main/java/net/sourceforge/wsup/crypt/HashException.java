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
 * Exception thrown if something improper is done with a <code>DataHasher</code>
 * .
 * 
 * @author Kevin Hunter
 * 
 */
public class HashException extends RuntimeException
{
	private static final long serialVersionUID = -2365701260791092551L;

	/**
	 * Construtor.
	 * 
	 * @param message Message
	 */
	public HashException(String message)
	{
		super(message);
	}

	/**
	 * Constructor.
	 * 
	 * @param message Message
	 * @param cause Underlying cause.
	 */
	public HashException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
