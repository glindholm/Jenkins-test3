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
 * This class defines a variety of constants that may be used as the return code from
 * {@link DatabaseCommand#execute(DatabaseCommandContext, net.sourceforge.wsup.hibernate4.database.DataAccessContext)}
 * .
 * This is implemented as a set of integers, as opposed to an enum, because typical implementations
 * may need to provide additional result codes.
 * 
 * @author Kevin Hunter
 * 
 */
public class DatabaseCommandResult
{
    /**
     * Special "default" value reserved for unit tests. Basically, this can be used to exercise
     * "default" cases in switch statements, etc. No "real" command should return this value.
     */
    public static final int BOGUS_FOR_UNIT_TEST = -2;

    /**
     * This value is used by <code>AbstractDatabaseCommand</code> to help make sure that the
     * <code>execute</code> method sets a result. No "real" command should return this value.
     */
    public static final int NOT_SET             = -1;

    /**
     * Success.
     */
    public static final int OK                  = 0;

    /**
     * The object being looked for was not found.
     */
    public static final int NOT_FOUND           = 1;

    /**
     * The object being added/inserted for was already present.
     */
    public static final int DUPLICATE           = 2;

    /**
     * Application-specific values may start at this value.
     */
    public static final int APP_BASE            = 1000;

    private DatabaseCommandResult()
    {
    }

    /* package */static void codeCoverage()
    {
        new DatabaseCommandResult();
    }
}
