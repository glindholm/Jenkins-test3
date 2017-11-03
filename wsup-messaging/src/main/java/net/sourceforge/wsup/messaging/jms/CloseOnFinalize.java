/*
 * Copyright (c) 2011 Kevin Hunter
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package net.sourceforge.wsup.messaging.jms;

import javax.jms.JMSException;

/**
 * This is a base class for items that should be closed in order to clean up
 * resources. It is designed to call the <code>close</code> method on the object
 * as part of the <code>finalize</code> procedure as a "safety net" in case
 * objects aren't closed manually. It is not wise to trust this, since there is
 * no way to tell how long it may take for the JVM to get around to finalizing
 * objects.
 */
public abstract class CloseOnFinalize
{
	protected CloseOnFinalize()
	{
	}

	@Override
	protected void finalize() throws Throwable
	{
		close();

		super.finalize();
	}

	public abstract void close() throws JMSException;
}
