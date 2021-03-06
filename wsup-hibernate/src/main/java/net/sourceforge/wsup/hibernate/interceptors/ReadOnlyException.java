/*
 *  Copyright 2010 Kevin Hunter
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

package net.sourceforge.wsup.hibernate.interceptors;

import org.hibernate.HibernateException;

/**
 * ReadOnlyException is thrown when an attempt is made to write to the database
 * when in read-only mode.
 * 
 */
public class ReadOnlyException extends HibernateException
{
    private static final long serialVersionUID = 7310715626229037223L;

    public ReadOnlyException(String s)
    {
        super(s);
    }
}
