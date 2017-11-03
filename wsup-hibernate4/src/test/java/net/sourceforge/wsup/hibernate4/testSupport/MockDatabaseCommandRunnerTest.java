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

package net.sourceforge.wsup.hibernate4.testSupport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import net.sourceforge.wsup.hibernate4.testClasses.MockSimpleCommand;

import org.hibernate.HibernateException;
import org.junit.Test;

public class MockDatabaseCommandRunnerTest
{
    public MockDatabaseCommandRunnerTest()
    {
    }

    @Test
    public void throwsWhenConstructedWithException()
    {
        MockDatabaseCommandRunner obj = new MockDatabaseCommandRunner(new HibernateException("boom"));
        try
        {
            obj.execute(new MockSimpleCommand());
            fail("didn't throw");
        }
        catch (HibernateException e)
        {
            assertEquals("boom", e.getMessage());
        }
    }

    @Test
    public void doesntThrosWhenConstructedWithoutException()
    {
        MockDatabaseCommandRunner obj = new MockDatabaseCommandRunner(null);
        obj.execute(new MockSimpleCommand());
    }

}
