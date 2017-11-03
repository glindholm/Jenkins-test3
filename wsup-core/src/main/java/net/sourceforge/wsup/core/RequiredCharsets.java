/*
 *  Copyright (c) 2010 Kevin Hunter
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

package net.sourceforge.wsup.core;

import java.nio.charset.Charset;

/**
 * This class contains definitions for the <code>Charset</code>s that,
 * according to the documentation, every JRE is required to support.
 * 
 * @author Kevin Hunter
 * 
 */
public class RequiredCharsets
{

    /**
     * <p>
     * ISO Latin Alphabet #1, also known as ISO-LATIN-1.
     * </p>
     * <p>
     * Every implementation of the Java platform is required to support this character encoding.
     * </p>
     * 
     * @see <a
     *      href="http://java.sun.com/j2se/1.3/docs/api/java/lang/package-summary.html#charenc">JRE
     *      character encoding names</a>
     */
    public static final String  ISO_8859_1         = "ISO-8859-1";

    /**
     * <p>
     * ISO Latin Alphabet #1, also known as ISO-LATIN-1.
     * </p>
     * <p>
     * Every implementation of the Java platform is required to support this character encoding.
     * </p>
     * 
     * @see <a
     *      href="http://java.sun.com/j2se/1.3/docs/api/java/lang/package-summary.html#charenc">JRE
     *      character encoding names</a>
     */
    public static final Charset CHARSET_ISO_8859_1 = Charset.forName(ISO_8859_1);
    /**
     * <p>
     * Seven-bit ASCII, also known as ISO646-US, also known as the Basic Latin block of the Unicode
     * character set.
     * </p>
     * <p>
     * Every implementation of the Java platform is required to support this character encoding.
     * </p>
     * 
     * @see <a
     *      href="http://java.sun.com/j2se/1.3/docs/api/java/lang/package-summary.html#charenc">JRE
     *      character encoding names</a>
     */
    public static final String  US_ASCII           = "US-ASCII";
    /**
     * <p>
     * Seven-bit ASCII, also known as ISO646-US, also known as the Basic Latin block of the Unicode
     * character set.
     * </p>
     * <p>
     * Every implementation of the Java platform is required to support this character encoding.
     * </p>
     * 
     * @see <a
     *      href="http://java.sun.com/j2se/1.3/docs/api/java/lang/package-summary.html#charenc">JRE
     *      character encoding names</a>
     */
    public static final Charset CHARSET_US_ASCII   = Charset.forName(US_ASCII);

    /**
     * <p>
     * Sixteen-bit Unicode Transformation Format, byte order specified by a mandatory initial
     * byte-order mark (either order accepted on input, big-endian used on output).
     * </p>
     * <p>
     * Every implementation of the Java platform is required to support this character encoding.
     * </p>
     * 
     * @see <a
     *      href="http://java.sun.com/j2se/1.3/docs/api/java/lang/package-summary.html#charenc">JRE
     *      character encoding names</a>
     */
    public static final String  UTF_16             = "UTF-16";
    /**
     * <p>
     * Sixteen-bit Unicode Transformation Format, byte order specified by a mandatory initial
     * byte-order mark (either order accepted on input, big-endian used on output).
     * </p>
     * <p>
     * Every implementation of the Java platform is required to support this character encoding.
     * </p>
     * 
     * @see <a
     *      href="http://java.sun.com/j2se/1.3/docs/api/java/lang/package-summary.html#charenc">JRE
     *      character encoding names</a>
     */
    public static final Charset CHARSET_UTF_16     = Charset.forName(UTF_16);
    /**
     * <p>
     * Sixteen-bit Unicode Transformation Format, big-endian byte order.
     * </p>
     * <p>
     * Every implementation of the Java platform is required to support this character encoding.
     * </p>
     * 
     * @see <a
     *      href="http://java.sun.com/j2se/1.3/docs/api/java/lang/package-summary.html#charenc">JRE
     *      character encoding names</a>
     */
    public static final String  UTF_16BE           = "UTF-16BE";
    /**
     * <p>
     * Sixteen-bit Unicode Transformation Format, big-endian byte order.
     * </p>
     * <p>
     * Every implementation of the Java platform is required to support this character encoding.
     * </p>
     * 
     * @see <a
     *      href="http://java.sun.com/j2se/1.3/docs/api/java/lang/package-summary.html#charenc">JRE
     *      character encoding names</a>
     */
    public static final Charset CHARSET_UTF_16BE   = Charset.forName(UTF_16BE);
    /**
     * <p>
     * Sixteen-bit Unicode Transformation Format, little-endian byte order.
     * </p>
     * <p>
     * Every implementation of the Java platform is required to support this character encoding.
     * </p>
     * 
     * @see <a
     *      href="http://java.sun.com/j2se/1.3/docs/api/java/lang/package-summary.html#charenc">JRE
     *      character encoding names</a>
     */
    public static final String  UTF_16LE           = "UTF-16LE";
    /**
     * <p>
     * Sixteen-bit Unicode Transformation Format, little-endian byte order.
     * </p>
     * <p>
     * Every implementation of the Java platform is required to support this character encoding.
     * </p>
     * 
     * @see <a
     *      href="http://java.sun.com/j2se/1.3/docs/api/java/lang/package-summary.html#charenc">JRE
     *      character encoding names</a>
     */
    public static final Charset CHARSET_UTF_16LE   = Charset.forName(UTF_16LE);
    /**
     * <p>
     * Eight-bit Unicode Transformation Format.
     * </p>
     * <p>
     * Every implementation of the Java platform is required to support this character encoding.
     * </p>
     * 
     * @see <a
     *      href="http://java.sun.com/j2se/1.3/docs/api/java/lang/package-summary.html#charenc">JRE
     *      character encoding names</a>
     */
    public static final String  UTF_8              = "UTF-8";
    /**
     * <p>
     * Eight-bit Unicode Transformation Format.
     * </p>
     * <p>
     * Every implementation of the Java platform is required to support this character encoding.
     * </p>
     * 
     * @see <a
     *      href="http://java.sun.com/j2se/1.3/docs/api/java/lang/package-summary.html#charenc">JRE
     *      character encoding names</a>
     */
    public static final Charset CHARSET_UTF_8      = Charset.forName(UTF_8);
    
    /*package*/static void coverConstructor()
    {
        new RequiredCharsets();
    }
    
    private RequiredCharsets()
    {
    }
}
