/*
 *  Copyright 2010 Kevin
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

package net.sourceforge.wsup.hibernate4.command;

/**
 * Interface that may be implemented by <code>DatabaseCommand</code>s to allow
 * fine-grained observation of the <code>DatabaseCommand</code> execution process.
 * 
 * @author Kevin Hunter
 * @see DatabaseCommand
 * 
 */
public interface PreExecute
{
    /**
     * Method called once at the beginning of execution of a series of <code>DatabaseCommand</code>
     * s. This will be called before the actual database
     * transaction begins, and will not be called again if a retry is necessary.
     * 
     * @param context
     *            <code>DatabaseCommandContext</code> object
     * @see DatabaseCommandContext
     */
    public void preExecute(DatabaseCommandContext context);
}
