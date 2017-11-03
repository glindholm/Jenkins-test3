/*
 * Copyright (c) 2010 Greg Lindholm
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sourceforge.wsup.core;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

public final class EncodeUtils
{
    /**
     * Translates a string into application/x-www-form-urlencoded format. This method uses the UTF-8
     * encoding scheme to obtain the bytes for unsafe characters, which is the recommendation by the
     * <i>World Wide Web Consortium</i>.
     * <p>
     * Use this method to encode URL parameter values.
     * </p>
     * <p>
     * This method simply calls <code>URLEncoder.encode</code> with the UTF-8 character set. For
     * convenience, it traps the <code>UnsupportedEncodingException</code> that
     * <code>URLEncoder.encode</code> is documented to throw to avoid that exception's declaration
     * propagating into the calling code. This exception should never the thrown, since UTF-8 is a
     * required character set. If by some mischance this is actually thrown, it will be wrapped in
     * an <code>AssertionError</code>.
     * </p>
     *
     * @param rawString the value to encode
     * @return The value encoded for safe use in a URL
     */
    public static String urlEncode(String rawString)
    {
        try
        {
            return URLEncoder.encode(rawString, RequiredCharsets.UTF_8);
        }
        catch (UnsupportedEncodingException e)
        {
            /*
             * There shouldn't be any way for this exception to be thrown,
             * since UTF-8 is a required character set.
             */
            throw new AssertionError(e.getMessage());
        }
    }

    /**
     * This routine converts a character into its Unicode escape
     * equivalent:
     *
     * <pre>
     *   EncodeUtils.unicodeEscaped(' ') = "\u0020"
     *   EncodeUtils.unicodeEscaped('A') = "\u0041"
     * </pre>
     *
     * @param ch input character
     * @return output string
     * @see #unicodeEscape(String, boolean)
     */
    public static String unicodeEscape(char ch)
    {
        if (ch < 0x10)
        {
            return "\\u000" + Integer.toHexString(ch);
        }
        else if (ch < 0x100)
        {
            return "\\u00" + Integer.toHexString(ch);
        }
        else if (ch < 0x1000)
        {
            return "\\u0" + Integer.toHexString(ch);
        }

        return "\\u" + Integer.toHexString(ch);
    }

    /**
     * This routine converts a <code>String</code> into an equivalent <code>String</code> that
     * contains only ASCII characters. Non-ASCII characters are replaced with their Unicode escape
     * equivalents.
     *
     * @param input String to be escaped (may be <code>null</code>).
     * @param escapeSpaces if <code>true</code>, space characters are
     *            escaped. If <code>false</code>, they are left as spaces.
     * @return Escaped string. If the input does not contain any
     *         non-ASCII characters, the original input string is returned.
     * @see #unicodeEscape(char)
     */
    public static String unicodeEscape(String input, boolean escapeSpaces)
    {
        if (input == null)
        {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        boolean changed = false;
        int length = input.length();
        for (int i = 0; i < length; i++)
        {
            char c = input.charAt(i);

            if (c < ' ')
            {
                builder.append(unicodeEscape(c));
                changed = true;
                continue;
            }

            if (c == ' ' && escapeSpaces)
            {
                builder.append(unicodeEscape(c));
                changed = true;
                continue;
            }

            if (c > '~')
            {
                builder.append(unicodeEscape(c));
                changed = true;
                continue;
            }

            builder.append(c);
        }

        if (!changed)
        {
            return input;
        }

        return builder.toString();
    }

    /**
     * Converts an array of bytes into a string of HEX characters.
     *
     * @param bytes Input <code>byte</code> array.
     * @return String containing the hex encoding of the bytes.
     */
    public static String toHex(byte[] bytes)
    {

        if (bytes == null)
        {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < bytes.length; i++)
        {
            int value = (int) bytes[i];

            builder.append(HEX_CHARS.charAt((value >> 4) & 0x0F));
            builder.append(HEX_CHARS.charAt(value & 0x0F));
        }

        return builder.toString();
    }

    private static final String HEX_CHARS = "0123456789ABCDEF";

    /**
     * Converts a string containing hex characters into the corresponding array of bytes.
     * Will return <code>null</code> if the input string is of odd length, or contains
     * a non-hex character.
     *
     * @param hex Input string
     * @return <code>byte</code> array.
     */
    public static byte[] fromHex(String hex)
    {
        if (hex == null)
        {
            return null;
        }

        int length = hex.length();
        if ((length & 0x01) != 0)
        {
            return null;
        }

        byte[] result = new byte[length / 2];

        for (int i = 0; i < length; i += 2)
        {
            int c1 = fromHex(hex.charAt(i));
            if (c1 < 0)
            {
                return null;
            }

            int c2 = fromHex(hex.charAt(i + 1));
            if (c2 < 0)
            {
                return null;
            }

            result[i / 2] = (byte) (c1 * 16 + c2);
        }

        return result;
    }

    /**
     * Converts an individual hex character into its decimal value.
     *
     * @param c Input character.
     * @return Hex value. Returns a value less than zero for non-hex characters.
     */
    public static int fromHex(char c)
    {
        if (c >= '0' && c <= '9')
        {
            return c - '0';
        }

        if (c >= 'a' && c <= 'f')
        {
            return c - 'a' + 10;
        }

        if (c >= 'A' && c <= 'F')
        {
            return c - 'A' + 10;
        }

        return -1;
    }

    /**
     * This will percent escape all non-ascii (non-printable) characters.
     *
     * @param s string to escape
     * @return the escaped string
     */
    public static String percentEscape(String s)
    {
        boolean changed = false;
        StringBuilder out = new StringBuilder(s.length());
        StringBuilder run = new StringBuilder();

        for (int i = 0; i < s.length();)
        {
            int c = (int) s.charAt(i);
            if (c >= 0x20 && c <= 0x7E)
            {
                out.append((char) c);
                i++;
            }
            else
            {
                /*
                 * Process a run of non-ascii characters.
                 */
                run.setLength(0);
                for (;;)
                {
                    run.append((char) c);
                    i++;

                    // if start of a unicode pair
                    if (c >= 0xD800 && c <= 0xDBFF)
                    {
                        // see if next char is the 2nd char of a pair
                        if (i < s.length())
                        {
                            c = (int) s.charAt(i);
                            // If 2nd char of a Unicode pair
                            if (c >= 0xDC00 && c <= 0xDFFF)
                            {
                                run.append((char) c);
                                i++;
                            }
                        }
                    }

                    if (i < s.length())
                    {
                        c = (int) s.charAt(i);
                        if (c >= 0x20 && c <= 0x7E)
                        {
                            break;
                        }
                    }
                    else
                    {
                        break;
                    }
                }

                /*
                 * Convert the characters to bytes and write out
                 */
                byte[] bytes = run.toString().getBytes(UTF8_CHARSET);
                for (int j = 0; j < bytes.length; j++)
                {
                    out.append('%');
                    out.append(HEX_CHARS.charAt((bytes[j] >> 4) & 0x0F));
                    out.append(HEX_CHARS.charAt(bytes[j] & 0x0F));
                }
                changed = true;
            }
        }

        return (changed ? out.toString() : s);
    }

    private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

    private EncodeUtils()
    {
    }

    static void coverage()
    {
        new EncodeUtils();
    }
}
