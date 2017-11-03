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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * Sample bean used for exercising the hibernate software.
 * 
 * @author Kevin Hunter
 * @see TestDateBean
 * @see TestBeanDAO
 */
@Entity
@Table(name = "testTable")
public class TestBean
{
    private Long   _id;

    private int    _version;

    private String _contents;

    public TestBean()
    {
    }

    public TestBean(String contents)
    {
        _contents = contents;
    }

    @Id
    @GeneratedValue
    public Long getId()
    {
        return _id;
    }

    public void setId(Long id)
    {
        _id = id;
    }

    @Version
    public int getVersion()
    {
        return _version;
    }

    public void setVersion(int version)
    {
        _version = version;
    }

    @Column(nullable = true)
    public String getContents()
    {
        return _contents;
    }

    public void setContents(String contents)
    {
        _contents = contents;
    }
}
