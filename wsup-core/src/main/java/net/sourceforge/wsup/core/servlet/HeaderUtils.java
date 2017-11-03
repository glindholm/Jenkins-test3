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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Definitions of HTTP/1.1 headers and other non-standard headers of interest,
 * and utilities related to processing HTTP headers.
 * 
 * @author Kevin Hunter
 * 
 */
public class HeaderUtils
{
	/**
	 * Standard HTTP/1.1 header (Section 14.1 of RFC 2616)
	 */
	public static final String HEADER_ACCEPT = "Accept";

	/**
	 * Standard HTTP/1.1 header (Section 14.2 of RFC 2616)
	 */
	public static final String HEADER_ACCEPT_CHARSET = "Accept-Charset";

	/**
	 * Standard HTTP/1.1 header (Section 14.3 of RFC 2616)
	 */
	public static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";

	/**
	 * Standard HTTP/1.1 header (Section 14.4 of RFC 2616)
	 */
	public static final String HEADER_ACCEPT_LANGUAGE = "Accept-Language";

	/**
	 * Standard HTTP/1.1 header (Section 14.5 of RFC 2616)
	 */
	public static final String HEADER_ACCEPT_RANGES = "Accept-Ranges";

	/**
	 * Standard HTTP/1.1 header (Section 14.6 of RFC 2616)
	 */
	public static final String HEADER_AGE = "Age";

	/**
	 * Standard HTTP/1.1 header (Section 14.7 of RFC 2616)
	 */
	public static final String HEADER_ALLOW = "Allow";

	/**
	 * Standard HTTP/1.1 header (Section 14.8 of RFC 2616)
	 */
	public static final String HEADER_AUTHORIZATION = "Authorization";

	/**
	 * Standard HTTP/1.1 header (Section 14.9 of RFC 2616)
	 */
	public static final String HEADER_CACHE_CONTROL = "Cache-Control";

	/**
	 * Standard HTTP/1.1 header (Section 14.10 of RFC 2616)
	 */
	public static final String HEADER_CONNECTION = "Connection";

    /**
     * Standard HTTP/1.1 header (Section 14.11 of RFC 2616)
     */
    public static final String HEADER_CONTENT_ENCODING = "Content-Encoding";

	/**
	 * Standard HTTP/1.1 header (Section 14.12 of RFC 2616)
	 */
	public static final String HEADER_CONTENT_LANGUAGE = "Content-Language";

	/**
	 * Standard HTTP/1.1 header (Section 14.13 of RFC 2616)
	 */
	public static final String HEADER_CONTENT_LENGTH = "Content-Length";

	/**
	 * Standard HTTP/1.1 header (Section 14.14 of RFC 2616)
	 */
	public static final String HEADER_CONTENT_LOCATION = "Content-Location";

	/**
	 * Standard HTTP/1.1 header (Section 14.15 of RFC 2616)
	 */
	public static final String HEADER_CONTENT_MD5 = "Content-MD5";

	/**
	 * Standard HTTP/1.1 header (Section 14.16 of RFC 2616)
	 */
	public static final String HEADER_CONTENT_RANGE = "Content-Range";

	/**
	 * Standard HTTP/1.1 header (Section 14.17 of RFC 2616)
	 */
	public static final String HEADER_CONTENT_TYPE = "Content-Type";

	/**
	 * Standard HTTP/1.1 header (Section 14.18 of RFC 2616)
	 */
	public static final String HEADER_DATE = "Date";

	/**
	 * Standard HTTP/1.1 header (Section 14.19 of RFC 2616)
	 */
	public static final String HEADER_ETAG = "ETag";

	/**
	 * Standard HTTP/1.1 header (Section 14.20 of RFC 2616)
	 */
	public static final String HEADER_EXPECT = "Expect";

	/**
	 * Standard HTTP/1.1 header (Section 14.21 of RFC 2616)
	 */
	public static final String HEADER_EXPIRES = "Expires";

	/**
	 * Standard HTTP/1.1 header (Section 14.22 of RFC 2616)
	 */
	public static final String HEADER_FROM = "From";

	/**
	 * Standard HTTP/1.1 header (Section 14.23 of RFC 2616)
	 */
	public static final String HEADER_HOST = "Host";

	/**
	 * Standard HTTP/1.1 header (Section 14.24 of RFC 2616)
	 */
	public static final String HEADER_IF_MATCH = "If-Match";

	/**
	 * Standard HTTP/1.1 header (Section 14.25 of RFC 2616)
	 */
	public static final String HEADER_IF_MODIFIED_SINCE = "If-Modified-Since";

	/**
	 * Standard HTTP/1.1 header (Section 14.26 of RFC 2616)
	 */
	public static final String HEADER_IF_NONE_MATCH = "If-None-Match";

	/**
	 * Standard HTTP/1.1 header (Section 14.27 of RFC 2616)
	 */
	public static final String HEADER_IF_RANGE = "If-Range";

	/**
	 * Standard HTTP/1.1 header (Section 14.28 of RFC 2616)
	 */
	public static final String HEADER_IF_UNMODIFIED_SINCE = "If-Unmodified-Since";

	/**
	 * Standard HTTP/1.1 header (Section 14.29 of RFC 2616)
	 */
	public static final String HEADER_LAST_MODIFIED = "Last-Modified";

	/**
	 * Standard HTTP/1.1 header (Section 14.30 of RFC 2616)
	 */
	public static final String HEADER_LOCATION = "Location";

	/**
	 * Standard HTTP/1.1 header (Section 14.31 of RFC 2616)
	 */
	public static final String HEADER_MAX_FORWARDS = "Max-Forwards";

	/**
	 * Standard HTTP/1.1 header (Section 14.32 of RFC 2616)
	 */
	public static final String HEADER_PRAGMA = "Pragma";

	/**
	 * Standard HTTP/1.1 header (Section 14.33 of RFC 2616)
	 */
	public static final String HEADER_PROXY_AUTHENTICATE = "Proxy-Authenticate";

	/**
	 * Standard HTTP/1.1 header (Section 14.34 of RFC 2616)
	 */
	public static final String HEADER_PROXY_AUTHORIZATION = "Proxy-Authorization";

	/**
	 * Standard HTTP/1.1 header (Section 14.35 of RFC 2616)
	 */
	public static final String HEADER_RANGE = "Range";

	/**
	 * Standard HTTP/1.1 header (Section 14.36 of RFC 2616)
	 */
	public static final String HEADER_REFERER = "Referer";

	/**
	 * Standard HTTP/1.1 header (Section 14.37 of RFC 2616)
	 */
	public static final String HEADER_RETRY_AFTER = "Retry-After";

	/**
	 * Standard HTTP/1.1 header (Section 14.38 of RFC 2616)
	 */
	public static final String HEADER_SERVER = "Server";

	/**
	 * Standard HTTP/1.1 header (Section 14.39 of RFC 2616)
	 */
	public static final String HEADER_TE = "TE";

	/**
	 * Standard HTTP/1.1 header (Section 14.40 of RFC 2616)
	 */
	public static final String HEADER_TRAILER = "Trailer";

	/**
	 * Standard HTTP/1.1 header (Section 14.41 of RFC 2616)
	 */
	public static final String HEADER_TRANSFER_ENCODING = "Transfer-Encoding";

	/**
	 * Standard HTTP/1.1 header (Section 14.42 of RFC 2616)
	 */
	public static final String HEADER_UPGRADE = "Upgrade";

	/**
	 * Standard HTTP/1.1 header (Section 14.43 of RFC 2616)
	 */
	public static final String HEADER_USER_AGENT = "User-Agent";

	/**
	 * Standard HTTP/1.1 header (Section 14.44 of RFC 2616)
	 */
	public static final String HEADER_VARY = "Vary";

	/**
	 * Standard HTTP/1.1 header (Section 14.45 of RFC 2616)
	 */
	public static final String HEADER_VIA = "Via";

	/**
	 * Standard HTTP/1.1 header (Section 14.46 of RFC 2616)
	 */
	public static final String HEADER_WARNING = "Warning";

	/**
	 * Standard HTTP/1.1 header (Section 14.47 of RFC 2616)
	 */
	public static final String HEADER_WWW_AUTHENTICATE = "WWW-Authenticate";

	/**
	 * Cookie header - RFC 2109
	 */
	public static final String HEADER_COOKIE = "Cookie";
	
	/**
	 * Set-Cookie header - RFC 2109
	 */
	public static final String HEADER_SET_COOKIE = "Set-Cookie";
	
	/**
	 * Cookie2 header - RFC 2965
	 */
	public static final String HEADER_COOKIE2 = "Cookie2";
	
	/**
	 * Set-Cookie2 header - RFC 2965
	 */
	public static final String HEADER_SET_COOKIE2 = "Set-Cookie2";
	
    /**
     * Content-Disposition header - RFC 2183
     */
    public static final String HEADER_CONTENT_DISPOSITION = "Content-Disposition";

	/**
	 * Non-standard header defined by many HTTP/1.0 clients.
	 */
	public static final String HEADER_KEEP_ALIVE = "Keep-Alive";
	
	/**
	 * Non-standard header implemented by some servers.
	 */
	public static final String HEADER_PROXY_CONNECTION = "Proxy-Connection";
	
	/**
	 * Non-standard header. Provides the operating system. Supported by some
	 * versions of Microsoft's Pocket Internet Explorer.
	 */
	public static final String HEADER_UA_OS = "UA-OS";

	/**
	 * Non-standard header for identifying the originating IP address of a
	 * client connecting to a web server through an HTTP proxy or load balancer.
	 */
	public static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";

	/**
	 * Non-standard header. Typically contains a URL to an RDF file as part of
	 * the UAProf initiative. The URL may optionaly be enclosed in quotation
	 * marks.
	 * 
	 * <pre>
	 * X-Wap-Profile: http://nds1.nds.nokia.com/uaprof/N6230ir200.xml
	 * or
	 * X-Wap-Profile: "http://nds1.nds.nokia.com/uaprof/N6230ir200.xml"
	 * </pre>
	 */
	public static final String HEADER_X_WAP_PROFILE = "X-Wap-Profile";

	/**
	 * Non-standard header. Can be used to specify differences from the UAProf
	 * information specified by the X-Wap-Profile header.
	 */
	public static final String HEADER_X_WAP_PROFILE_DIFF = "X-Wap-Profile-Diff";

	/*
	 * Contains exceptions to the
	 * "capital letters only as the first character or after a dash" rule for
	 * header normalization.
	 */
	private static final Map<String, String> NORMALIZATION_EXCEPTIONS = new HashMap<String, String>();
	static
	{
		NORMALIZATION_EXCEPTIONS.put("Www-Authenticate",
				HEADER_WWW_AUTHENTICATE);
		NORMALIZATION_EXCEPTIONS.put("Content-Md5", HEADER_CONTENT_MD5);
		NORMALIZATION_EXCEPTIONS.put("Etag", HEADER_ETAG);
		NORMALIZATION_EXCEPTIONS.put("Te", HEADER_TE);
		NORMALIZATION_EXCEPTIONS.put("Ua-Os", HEADER_UA_OS);
	}

	/**
	 * "Normalize" an HTTP header into its canonical form (i.e. capital letters
	 * at the beginning and after each dash, lower case otherwise).
	 * 
	 * @param input
	 *            Input <code>String</code>
	 * @return Normalized <code>String</code>
	 */
	public static String normalizeHeader(String input)
	{
		StringBuilder builder = new StringBuilder();
		boolean toUpper = true;

		int length = input.length();
		for (int i = 0; i < length; i++)
		{
			char c = input.charAt(i);

			if (c == '-')
			{
				builder.append(c);
				toUpper = true;
			}
			else
			{
				if (toUpper)
				{
					builder.append(Character.toUpperCase(c));
					toUpper = false;
				}
				else
				{
					builder.append(Character.toLowerCase(c));
				}
			}
		}

		String result = builder.toString();
		String exception = NORMALIZATION_EXCEPTIONS.get(result);
		if (exception != null)
		{
			return exception;
		}

		return result;
	}

	/**
	 * Array of the header names (normalized) that are specific to the actual
	 * connection (i.e. browser-to-proxy, proxy-to-server) as opposed to being
	 * end-to-end.
	 */
	public static final String[] PER_CONNECTION_HEADERS =
	{
			HEADER_CONNECTION, HEADER_KEEP_ALIVE, HEADER_PROXY_AUTHENTICATE,
			HEADER_PROXY_AUTHORIZATION, HEADER_PROXY_CONNECTION, HEADER_TE,
			HEADER_TRAILER, HEADER_TRANSFER_ENCODING, HEADER_UPGRADE,
			HEADER_VIA
	};

	/**
	 * Array of the header names (normalized) that are specific to the particular
	 * session between client and server.
	 */
	public static final String[] SESSION_HEADERS =
	{
			HEADER_COOKIE, HEADER_COOKIE2, HEADER_SET_COOKIE, HEADER_SET_COOKIE2
	};

	/**
	 * <code>Set</code> of the header names (normalized) that are specific to
	 * the actual connection (i.e. browser-to-proxy, proxy-to-server) as opposed
	 * to being end-to-end.
	 */
	public static final Set<String> PER_CONNECTION_HEADER_SET = new HashSet<String>();
	static
	{
		for (String header : PER_CONNECTION_HEADERS)
		{
			PER_CONNECTION_HEADER_SET.add(header);
		}
	}

	/**
	 * <code>Set</code> of the header names (normalized) that are specific to the particular
	 * session between client and server.
	 */
	public static final Set<String> SESSION_HEADER_SET = new HashSet<String>();
	static
	{
		for (String header : SESSION_HEADERS)
		{
			SESSION_HEADER_SET.add(header);
		}
	}

	/* package */static void coverage()
	{
		new HeaderUtils();
	}

	private HeaderUtils()
	{
	}
}
