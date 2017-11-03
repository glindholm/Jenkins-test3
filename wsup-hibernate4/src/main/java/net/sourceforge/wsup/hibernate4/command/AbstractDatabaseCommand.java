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

package net.sourceforge.wsup.hibernate4.command;

/**
 * This class provides the base for most <code>DatabaseCommand</code> objects. It manages the
 * <code>result</code> property, including checking to see that it gets set by the
 * <code>execute</code> method.
 * 
 * @author Kevin Hunter
 * 
 */
public abstract class AbstractDatabaseCommand implements DatabaseCommand, PreTransaction,
    PostTransaction
{
    private int result;

    protected AbstractDatabaseCommand()
    {
    }

    public int getResult()
    {
        return result;
    }

    public void setResult(int result)
    {
        this.result = result;
    }

    public void preTransaction(DatabaseCommandContext context)
    {
        setResult(DatabaseCommandResult.NOT_SET);
    }

    public void postTransaction(DatabaseCommandContext context, boolean successful)
    {
        if (successful)
        {
            if (getResult() == DatabaseCommandResult.NOT_SET)
            {
                throw new IllegalStateException("DatabaseCommand did not set a result");
            }
        }
    }
}
