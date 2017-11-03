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

package net.sourceforge.wsup.core.servlet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.wsup.core.Assert;

import org.apache.commons.lang.StringUtils;

/**
 * This class represents a group of HTTP headers. It is designed to preserve the
 * order, etc. of the headers that it manages, and thus uses <code>List</code>s
 * instead of <code>Set</code>s. It also supports an individual HTTP header
 * having multiple values.
 * <p>
 * The object can load from an HttpServletRequest, and load and save to a <code>String</code>. The
 * <code>String</code> representation consists of each header name, colon-space, header value,
 * carriage return.
 * </p>
 * 
 * @author Kevin Hunter
 * 
 */
public class HeaderGroup
{
    private List<Header>        headers   = new ArrayList<Header>();

    private Map<String, Header> headerMap = new HashMap<String, Header>();

    /**
     * Construct an empty header group.
     */
    public HeaderGroup()
    {
    }

    /**
     * Construct a header group from an <code>HttpServletRequest</code>.
     * 
     * @param request
     *            <code>HttpServletRequest</code> containing the headers
     * @param normalizeNames
     *            <code>true</code> to normalize the names, <code>false</code> to leave them as is.
     */
    public HeaderGroup(HttpServletRequest request, boolean normalizeNames)
    {
        load(request, normalizeNames);
    }

    /**
     * Construct a header group from a <code>String</code>.
     * 
     * @param input
     *            Input <code>String</code>
     * @param normalizeNames
     *            <code>true</code> to normalize the names, <code>false</code> to leave them as is.
     */
    public HeaderGroup(String input, boolean normalizeNames)
    {
        load(input, normalizeNames);
    }

    /**
     * Clear the contents of the object.
     */
    public void clear()
    {
        headers.clear();
        headerMap.clear();
    }

    /**
     * Load the header group from the specified request.
     * 
     * @param request
     *            <code>HttpServletRequest</code> object.
     * @param normalizeNames
     *            if <code>true</code>, header names are normalized while being
     *            loaded.
     */
    public void load(HttpServletRequest request, boolean normalizeNames)
    {
        clear();

        Enumeration<?> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements())
        {
            String headerName = (String) headerNames.nextElement();
            Enumeration<?> values = request.getHeaders(headerName);

            if (normalizeNames)
            {
                headerName = HeaderUtils.normalizeHeader(headerName);
            }

            while (values.hasMoreElements())
            {
                addHeaderValue(headerName, (String) values.nextElement());
            }
        }
    }

    /**
     * Load the header group from the specified <code>String</code>.
     * Individual headers are contained as "name", "colon", "value",
     * and multiple headers are separated by newline characters.
     * 
     * @param input
     *            Input <code>String</code> object.
     * @param normalizeNames
     *            if <code>true</code>, header names are normalized while being
     *            loaded.
     */
    public void load(String input, boolean normalizeNames)
    {
        clear();

        String[] lines = input.split("\n");
        for (String line : lines)
        {
            addIndividualHeader(line, normalizeNames);
        }
    }
    
    /**
     * Put the contents of the <code>HeaderGroup</code> into
     * the specified <code>HttpServletResponse</code>.
     * @param response <code>HttpServletResponse</code>
     */
    public void outputTo(HttpServletResponse response)
    {
        for (Header header : headers)
        {
            String name = header.getName();
            
            for (String value : header.getValues())
            {
                response.addHeader(name, value);
            }
        }
    }

    /**
     * Adds an individual header to the collection, from a string consisting
     * of the name, a colon and the value.
     * 
     * @param line Input header line.
     * @param normalizeNames if <code>true</code>, header names are normalized
     *            while being loaded.
     */
    public void addIndividualHeader(String line, boolean normalizeNames)
    {
        int colonPos = line.indexOf(':');
        if (colonPos >= 0)
        {
            String name = line.substring(0, colonPos).trim();
            if (normalizeNames)
            {
                name = HeaderUtils.normalizeHeader(name);
            }
            String value = line.substring(colonPos + 1).trim();

            addHeaderValue(name, value);
        }
    }

    /**
     * Add a new header/value pair to the object. Note that the header name is
     * used as-is. Normalization is the responsibility of the caller.
     * 
     * @param headerName
     *            Header name
     * @param headerValue
     *            Header value
     */
    public void addHeaderValue(String headerName, String headerValue)
    {
        addHeaderValue(headerName, false, headerValue);
    }

    /**
     * Add a new header/value pair to the object, potentially normalizing the
     * header name as part of the process.
     * 
     * @param headerName
     *            Header name
     * @param normalizeName
     *            if <code>true</code>, the header name is normalized while
     *            being added.
     * @param headerValue
     *            Header value
     */
    public void addHeaderValue(String headerName, boolean normalizeName, String headerValue)
    {
        if (normalizeName)
        {
            headerName = HeaderUtils.normalizeHeader(headerName);
        }

        Header header = getHeader(headerName);
        if (header == null)
        {
            header = new Header(headerName);
            headers.add(header);
            headerMap.put(headerName, header);
        }

        header.addValue(headerValue);
    }

    /**
     * Return the number of unique headers (by name). Thus, if there are two
     * headers with the same name but different values, this only counts as one
     * for this.
     * 
     * @return Number of unique headers
     */
    public int getHeaderCount()
    {
        return headers.size();
    }

    /**
     * Return a <code>List</code> of <code>Header</code> objects.
     * <p>
     * The <code>List</code> object that is returned is a copy of
     * the internal list.  Thus, altering this list by adding or
     * removing elements will not alter the contents of this
     * object.
     * </p>
     * 
     * @return <code>List</code> of <code>Header</code> objects
     */
    public List<Header> getHeaders()
    {
        return new ArrayList<Header>(headers);
    }

    /**
     * Return the <code>Header</code> for the specified name. Note that the
     * header name is case-sensitive - any normalization is the responsibility
     * of the caller.
     * 
     * @param headerName
     *            Name of the header to retrieve.
     * @return <code>Header</code> for the specified name, or <code>null</code> if there is none.
     */
    public Header getHeader(String headerName)
    {
        return headerMap.get(headerName);
    }

    /**
     * Return the Nth <code>Header</code> object.
     * 
     * @param index
     *            Index to retrieve.
     * @return <code>Header</code> object
     */
    public Header getHeader(int index)
    {
        return headers.get(index);
    }

    /**
     * Return the name from the Nth <code>Header</code>.
     * 
     * @param headerIndex
     *            Index to retrieve.
     * @return name from the Nth <code>Header</code>.
     */
    public String getHeaderName(int headerIndex)
    {
        return headers.get(headerIndex).getName();
    }

    /**
     * Return the number of values for the Nth <code>Header</code>.
     * 
     * @param headerIndex
     *            Index to retrieve.
     * @return Number of values in the Nth <code>Header</code>.
     */
    public int getValueCount(int headerIndex)
    {
        return headers.get(headerIndex).getValueCount();
    }

    /**
     * Return the Mth value from the Nth <code>Header</code>.
     * 
     * @param headerIndex
     *            Index of the <code>Header</code> to retrieve.
     * @param valueIndex
     *            Index of the value in the <code>Header</code>.
     * @return <code>String</code> value.
     */
    public String getValue(int headerIndex, int valueIndex)
    {
        return headers.get(headerIndex).getValue(valueIndex);
    }

    /**
     * Convert the entire <code>HeaderGroup</code> to a string.
     * 
     * @see #toNormalizedString()
     */
    public String toString()
    {
        StringBuilder builder = new StringBuilder();

        for (Header header : headers)
        {
            builder.append(header.toString());
        }

        return builder.toString().trim();
    }

    /**
     * Convert the entire <code>HeaderGroup</code> to a string.
     * This method differs from {@link #toString()} in that the
     * contents will be placed into a standard order. Thus, two <code>HeaderGroup</code>s that were
     * built differently, but
     * have the same contents, will have the same normalized string,
     * 
     * @see #toString()
     */
    public String toNormalizedString()
    {
        StringBuilder builder = new StringBuilder();

        for (Header header : getHeadersInNormalizedOrder())
        {
            builder.append(header.toNormalizedString());
        }

        return builder.toString().trim();
    }

    /**
     * Remove any headers that are in the specified <code>Set</code>. Note that
     * the <code>Set</code> and the normalization state of the headers in this
     * object must agree.
     * 
     * @param headersToRemove
     *            <code>Set</code> of <code>String</code>s containing headers to
     *            remove.
     */
    public void removeHeaders(Set<String> headersToRemove)
    {
        for (int i = headers.size() - 1; i >= 0; i--)
        {
            Header header = headers.get(i);
            if (headersToRemove.contains(header.getName()))
            {
                headers.remove(i);
                headerMap.remove(header.getName());
            }
        }
    }

    /**
     * Normalize the contents of this object. This consists of:
     * <ol>
     * <li>Normalizing the names of each header.</li>
     * <li>Normalizing the content of each header. (See {@link Header#normalize()})</li>
     * <li>Eliminating any headers that do not have values</li>
     * <li>Sorting the list of headers into ascending name order</li>
     * </ol>
     */
    public void normalize()
    {
        List<Header> tempHeaders = new ArrayList<Header>();
        tempHeaders.addAll(headers);
        
        headers.clear();
        headerMap.clear();
        
        /*
         * This loop does the following:
         * 1) Gets rid of headers that have empty names
         * 2) Normalizes the names of the headers
         * 3) Merges together values for headers that originally had names differing in case
         */
        for (Header header : tempHeaders)
        {
            header.normalize();
            if (header.getName() != null)
            {
                String headerName = HeaderUtils.normalizeHeader(header.getName());
                
                int count = header.getValueCount();
                for (int i = 0; i < count; i++)
                {
                    addHeaderValue(headerName, header.getValue(i));
                }
            }
        }
        
        for (Header header : headers)
        {
            header.normalize();
        }

        headers = getHeadersInNormalizedOrder();
    }
    
    private static final String[] SPECIAL_HEADERS =
    {
     HeaderUtils.HEADER_USER_AGENT.toLowerCase(),
     HeaderUtils.HEADER_X_WAP_PROFILE.toLowerCase(),
     HeaderUtils.HEADER_UA_OS.toLowerCase(),
    };
    
    private List<Header> getHeadersInNormalizedOrder()
    {
        TreeMap<String,Header> map = new TreeMap<String,Header>();
        
        for (Header header : headers)
        {
            map.put(header.getName().toLowerCase(), header);
        }
        
        ArrayList<Header> list = new ArrayList<Header>();
        
        for (int i = 0; i < SPECIAL_HEADERS.length; i++)
        {
            Header header = map.get(SPECIAL_HEADERS[i]);
            if (header != null)
            {
                list.add(header);
                map.remove(SPECIAL_HEADERS[i]);
            }
        }
        
        for (String key : map.keySet())
        {
            list.add(map.get(key));
        }
        
        return list;
    }

    /**
     * This is a routine to support unit testing, which checks to make
     * sure that the array and map are self-consistent.
     */
    /*package*/ void verifyConsistency()
    {
        Assert.isTrue(headers.size() == headerMap.size());
        for (Header header : headers)
        {
            Assert.isTrue(header == headerMap.get(header.getName()));
        }
    }

    /**
     * Nested class that is used to manage the (possibly multiple) values
     * associated with an individual header.
     * 
     * @author Kevin Hunter
     * 
     */
    public static class Header
    {
        private String       name;

        private List<String> values = new ArrayList<String>();
        private Set<String> uniqueValues = new HashSet<String>();

        /**
         * Constructor.
         * 
         * @param name
         *            Header name.
         */
        public Header(String name)
        {
            this.name = name;
        }

        /**
         * Get the header name.
         * 
         * @return Header name.
         */
        public String getName()
        {
            return name;
        }

        /**
         * Get the number of values for this <code>Header</code>.
         * 
         * @return Number of values
         */
        public int getValueCount()
        {
            return values.size();
        }

        /**
         * Get the number of unique values for this <code>Header</code>.
         * 
         * @return Number of values
         */
        public int getUniqueValueCount()
        {
            return uniqueValues.size();
        }

        /**
         * Get the <code>List</code> of values.  The returned list is immutable.
         * 
         * @return <code>List</code> of values.
         */
        public List<String> getValues()
        {
            return Collections.unmodifiableList(values);
        }

        /**
         * Get the <code>Set</code> of unique values.  The returned list is immutable.
         * 
         * @return <code>Set</code> of unique values.
         */
        public Set<String> getUniqueValues()
        {
            return Collections.unmodifiableSet(uniqueValues);
        }

        /**
         * Get the Nth value.
         * 
         * @param valueIndex
         *            index
         * @return value
         */
        public String getValue(int valueIndex)
        {
            return values.get(valueIndex);
        }

        /**
         * Add a new value to this <code>Header</code>.
         * 
         * @param value
         *            Value to add
         */
        public void addValue(String value)
        {
            values.add(value);
            uniqueValues.add(value);
        }
        
        /**
         * Indicates whether or not this header already contains a
         * particular value.
         * 
         * @param value Value to be checked
         * @return <code>true</code> if the value is already present, <code>false</code> otherwise.
         */
        public boolean containsValue(String value)
        {
            return uniqueValues.contains(value);
        }

        /**
         * Convert the contents of this <code>Header</code> to a <code>String</code>.
         */
        public String toString()
        {
            StringBuilder builder = new StringBuilder();

            for (String value : values)
            {
                builder.append(name);
                builder.append(": ");
                builder.append(value);
                builder.append("\n");
            }

            return builder.toString();
        }

        /**
         * Convert the contents of this <code>Header</code> to a <code>String</code>.
         * This method differs from {@link #toString()} in that the
         * contents will be placed into a standard order. Thus, two <code>Header</code>s that
         * were built differently, but have the same contents, will have the same normalized string,
         * 
         * @see #toString()
         */
        public String toNormalizedString()
        {
            String headerName = StringUtils.trimToNull(name);
            if (headerName == null)
            {
                return "";
            }
            
            StringBuilder builder = new StringBuilder();

            String[] headerValues = values.toArray(new String[values.size()]);
            Arrays.sort(headerValues);

            for (String value : headerValues)
            {
                value = StringUtils.trimToNull(value);
                if (value != null)
                {
                    builder.append(headerName);
                    builder.append(": ");
                    builder.append(value);
                    builder.append("\n");
                }
            }

            return builder.toString();
        }

        /**
         * Normalize the contents of this header. This consists of:
         * <ol>
         * <li>Trimming leading and trailing spaces off the name</li>
         * <li>Trimming leading and trailing spaces off each value</li>
         * <li>Eliminating any empty values</li>
         * <li>Eliminating any duplicate values</li>
         * <li>Sorting the values into ascending order</li>
         * </ol>
         */
        public void normalize()
        {
            name = StringUtils.trimToNull(name);
            
            uniqueValues.clear();
            
            for (String value : values)
            {
                value = StringUtils.trimToNull(value);
                if (value != null)
                {
                    uniqueValues.add(value);
                }
            }
            
            values.clear();
            
            int count = uniqueValues.size();
            if (count > 0)
            {
                String[] valueList = uniqueValues.toArray(new String[count]);
                Arrays.sort(valueList);
                for (String value : valueList)
                {
                    values.add(value);
                }
            }
        }
    }
}
