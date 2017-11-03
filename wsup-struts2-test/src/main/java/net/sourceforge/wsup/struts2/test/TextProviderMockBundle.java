/*
 *  Copyright (c) 2011 Kevin Hunter
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

package net.sourceforge.wsup.struts2.test;

import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;

import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * This class implements the Struts <code>TextProvider</code> interface,
 * and allows testing of items requiring a <code>TextProvider</code> with
 * known content.  This version of the class gets its text from
 * a <code>ResourceBundle</code>.
 * 
 * @author Kevin Hunter
 */

public class TextProviderMockBundle implements TextProvider
{
    private ResourceBundle bundle;
    
    public TextProviderMockBundle(ResourceBundle bundle)
    {
        this.bundle = bundle;
    }
    
    @Override
    public String getText(String key)
    {
        return bundle.getString(key);
    }

    @Override
    public String getText(String key, String defaultValue)
    {
        if (!hasKey(key))
        {
            return defaultValue;
        }
        
        return getText(key);
    }

    @Override
    public String getText(String key, List<?> args)
    {
        return MessageFormat.format(getText(key), args.toArray());
    }

    @Override
    public String getText(String key, String[] args)
    {
        return MessageFormat.format(getText(key), (Object[])args);
    }

    @Override
    public String getText(String key, String defaultValue, String obj)
    {
        return MessageFormat.format(getText(key, defaultValue), new Object[]{ obj });
    }

    @Override
    public String getText(String key, String defaultValue, List<?> args)
    {
        return MessageFormat.format(getText(key, defaultValue), args.toArray());
    }

    @Override
    public String getText(String key, String defaultValue, String[] args)
    {
        return MessageFormat.format(getText(key, defaultValue), (Object[])args);
    }

    @Override
    public String getText(String key, String defaultValue, List<?> args, ValueStack stack)
    {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String getText(String key, String defaultValue, String[] args, ValueStack stack)
    {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public ResourceBundle getTexts()
    {
        return bundle;
    }

    @Override
    public ResourceBundle getTexts(String bundleName)
    {
        return ResourceBundle.getBundle(bundleName);
    }

    @Override
    public boolean hasKey(String key)
    {
        return bundle.containsKey(key);
    }
}
