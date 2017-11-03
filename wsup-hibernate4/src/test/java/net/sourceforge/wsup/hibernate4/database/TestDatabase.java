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

package net.sourceforge.wsup.hibernate4.database;

/**
 * This class adds verification SQL to its parent, which makes for a "complete" test database.
 * 
 * @author Kevin Hunter
 * 
 */
public class TestDatabase extends TestDatabaseWithoutVerificationSql
{
    private String verificationSQL = "select 1 from TestTable";

    public TestDatabase()
    {
    }

    protected String getVerificationSQL()
    {
        return verificationSQL;
    }

    public void setVerificationSQL(String sql)
    {
        this.verificationSQL = sql;
    }
}
