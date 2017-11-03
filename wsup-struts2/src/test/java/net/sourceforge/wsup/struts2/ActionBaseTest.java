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

package net.sourceforge.wsup.struts2;

import static junit.framework.Assert.*;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import net.sourceforge.wsup.struts2.test.StrutsTestContext;

import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.SessionMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.impl.MockLogger;
import org.slf4j.impl.MockLogger.Level;
import org.slf4j.impl.MockLogger.LogEntry;

import com.google.inject.servlet.GuiceFilter;
import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockServletContext;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.interceptor.I18nInterceptor;

public class ActionBaseTest
{

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {
    }

    @Before
    public void setUp() throws Exception
    {
        MockLogger.resetAll();
    }

    @After
    public void tearDown() throws Exception
    {
        ActionContext.setContext(null);
        new GuiceFilter().destroy();

    }

    @Test
    public final void testBuildPageList()
    {
        ActionBase actionBase = new ActionBase();

        assertEquals("[1, 2, 3, 4, 5]", actionBase.buildPageList(1, 20, 5).toString());
        assertEquals("[8, 9, 10, 11, 12]", actionBase.buildPageList(10, 20, 5).toString());
        assertEquals("[7, 8, 9, 10]", actionBase.buildPageList(10, 10, 4).toString());

        try
        {
            actionBase.buildPageList(0, 10, 5);
            fail("fail page >= 1");
        }
        catch (RuntimeException e)
        {
        }

        try
        {
            actionBase.buildPageList(11, 10, 5);
            fail("fail page <= last");
        }
        catch (RuntimeException e)
        {
        }

        try
        {
            actionBase.buildPageList(1, 10, 0);
            fail("fail num > 1");
        }
        catch (RuntimeException e)
        {
        }
    }

    @Test
    public void testInContext() throws Exception
    {
        MockLogger.get(UnknownAction.class).setOff();

        MockServletContext servletContext = new MockServletContext();
        Dispatcher dispatcher = StrutsTestContext.prepareDispatcher(servletContext, null);
        StrutsTestContext context = new StrutsTestContext(dispatcher, servletContext);

        Map<String, String> requestParams = new HashMap<String, String>();
        Map<String, Object> sessionAttributes = new HashMap<String, Object>();

        requestParams.put(I18nInterceptor.DEFAULT_REQUESTONLY_PARAMETER, "fr_FR");
        sessionAttributes.put("USERLOCALE", Locale.CANADA_FRENCH);

        ActionProxy proxy = context.createActionProxy("/struts2-testing",
                                                      "unknownxxxx",
                                                      requestParams,
                                                      sessionAttributes);
        context.getMockRequest()
            .setRequestURL("http://example.com/struts2-testing/unknownxxxx.action");
        context.getMockRequest().setContextPath("/context");

        assertEquals("execute", proxy.getMethod());
        assertTrue(proxy.getAction() instanceof UnknownAction);
        UnknownAction action = (UnknownAction) proxy.getAction();

        assertEquals(Action.SUCCESS, proxy.execute());

        assertEquals("fr", action.getUserLocale().getLanguage());

        assertEquals("unknownxxxx", action.getActionName());

        assertEquals("/context", action.getContextPath());

        assertEquals(context.getMockRequest(), action.getHttpServletRequest());

        assertEquals(Locale.CANADA_FRENCH, action.getSessionObject("USERLOCALE"));

        action.invalidateSession();
        assertEquals(null, action.getSessionObject("USERLOCALE"));

        action.addActionError("ERROR1");
        action.addActionError("ERROR2");
        action.addActionMessage("MESS1");
        action.addActionMessage("MESS2");
        action.addFieldError("FIELD1", "F1M1");
        action.addFieldError("FIELD1", "F1M2");
        action.addFieldError("FIELD2", "F2M1");

        String messages = action.actionStateMessages().toString();
        assertTrue(messages, messages.contains("unknownxxxx"));
        assertTrue(messages, messages.contains(UnknownAction.class.getName()));
        assertTrue(messages, messages.contains("ERROR1"));
        assertTrue(messages, messages.contains("ERROR2"));
        assertTrue(messages, messages.contains("MESS1"));
        assertTrue(messages, messages.contains("MESS2"));
        assertTrue(messages, messages.contains("FIELD1"));
        assertTrue(messages, messages.contains("F1M1"));
        assertTrue(messages, messages.contains("F1M2"));
        assertTrue(messages, messages.contains("FIELD2"));
        assertTrue(messages, messages.contains("F2M1"));
    }

    @Test
    public void testbaseMethods()
    {
        ActionBase action = new ActionBase();
        assertEquals(TimeZone.getDefault(), action.getUserTimeZone());
        assertEquals(DateFormat.MEDIUM, action.getDateFormatDateStyle());
        assertEquals(DateFormat.SHORT, action.getDateTimeFormatDateStyle());
        assertEquals(DateFormat.MEDIUM, action.getDateTimeFormatTimeStyle());
    }

    @Test
    public void testFormat()
    {
        ActionBase action = new ActionBase()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public Locale getUserLocale()
            {
                return Locale.ITALY;
            }

            @Override
            public TimeZone getUserTimeZone()
            {
                return TimeZone.getTimeZone("Europe/Rome");
            }

            @Override
            protected int getDateFormatDateStyle()
            {
                return DateFormat.LONG;
            }

        };

        assertEquals(TimeZone.getTimeZone("Europe/Rome"), action.getUserTimeZone());
        assertEquals(Locale.ITALY, action.getUserLocale());

        Calendar cal = Calendar.getInstance(action.getUserTimeZone(), action.getUserLocale());
        cal.set(2010, 7, 17, 14, 30, 45);

        assertEquals("17 agosto 2010", action.formatDate(cal.getTime()));
        assertEquals("17/08/10 14.30.45", action.formatDateTime(cal.getTime()));
        assertEquals("1.234.567", action.formatInteger(1234567));

        action.setNow(cal.getTime());   // force daylight savings time regardless of what time the test is run
        assertEquals("CEST", action.getUserTimeZoneName());
        assertEquals("Ora estiva dell'Europa centrale", action.getUserTimeZoneNameLong());

        // second calls use cached formats
        action.getDateFormat();
        action.getDateTimeFormat();
        action.getIntegerFormat();
    }

    @Test
    public void testExceptionCallback()
    {
        MockLogger.get(ActionBase.class).printOff();
        ActionBase action = new ActionBase();
        action.exceptionCallback(new NumberFormatException());

        assertEquals("LogEntry [level=ERROR, message=null, name=net.sourceforge.wsup.struts2.ActionBase, throwable=java.lang.NumberFormatException]",
                     MockLogger.last().toString());

        assertEquals("NumberFormatException", action.getExceptions().get(0).getClass()
            .getSimpleName());
    }

    @Test
    public void testAbbreviate()
    {
        ActionBase action = new ActionBase();

        assertEquals("12...", action.abbreviate("1234567890", 5));
    }

    @Test
    public void testCoverage()
    {
        MockLogger.get(ActionBase.class).printOff();

        ActionBase action = new ActionBase();

        action.setSession(new HashMap<String, Object>());

        action.invalidateSession();
        LogEntry entry = MockLogger.last();

        assertEquals(Level.ERROR, entry.getLevel());
        assertTrue(entry.toString(), entry.getMessage().contains(HashMap.class.getName()));

        action.setSession(new SessionMap<String, Object>(new MockHttpServletRequest())
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void invalidate()
            {
                throw new RuntimeException("invalidate failed");
            }
        });

        MockLogger.clear();
        action.invalidateSession();
        entry = MockLogger.last();
        assertEquals(entry.toString(), entry.getThrowable().getMessage(), "invalidate failed");
    }

    @Test
    public void testSetSessionObject()
    {
        HashMap<String,Object> session = new HashMap<String,Object>();
        ActionBase obj = new ActionBase();
        obj.setSession(session);
        assertTrue(session == obj.getSessionMap());
        
        obj.setSessionObject("key", new Integer(1));
        assertEquals(new Integer(1), session.get("key"));
        assertEquals(new Integer(1), obj.getSessionObject("key", Integer.class));
    }
    
    @Test
    public void testNullSessionObject()
    {
        HashMap<String,Object> session = new HashMap<String,Object>();
        ActionBase obj = new ActionBase();
        obj.setSession(session);
        
        assertNull(obj.getSessionObject("key"));
        assertNull(obj.getSessionObject("key", Integer.class));
    }
    
    @Test
    public void testSessionObjectCasting()
    {
        HashMap<String,Object> session = new HashMap<String,Object>();
        session.put("key", "value");
        ActionBase obj = new ActionBase();
        obj.setSession(session);
        
        assertEquals("value", obj.getSessionObject("key"));
        assertNull(obj.getSessionObject("key", Integer.class));
        assertEquals("value", obj.getSessionObject("key", String.class));
    }
    
    @Test
    public void testSessionObjectDerivedClass()
    {
        SessionDerived sessionObject = new SessionDerived();
        HashMap<String,Object> session = new HashMap<String,Object>();
        session.put("key", sessionObject);
        ActionBase obj = new ActionBase();
        obj.setSession(session);
        
        assertTrue(sessionObject == obj.getSessionObject("key", SessionBase.class));
        assertTrue(sessionObject == obj.getSessionObject("key", SessionDerived.class));
    }
    
    
    @Test
    public void testRemoveSessionObject()
    {
        HashMap<String,Object> session = new HashMap<String,Object>();
        session.put("key", "value");
        ActionBase obj = new ActionBase();
        obj.setSession(session);
        
        obj.removeSessionObject("key");
        assertNull(session.get("key"));
    }
    
    private static class SessionBase
    {
        public SessionBase()
        {
        }
    }
    
    private static class SessionDerived extends SessionBase
    {
        public SessionDerived()
        {
        }
    }
}
