/*
 * Copyright (C) 2006 Google Inc.
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

package net.sourceforge.wsup.struts2.guice;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.ScopeAnnotation;
import com.google.inject.servlet.ServletModule;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.Interceptor;

/**
 * This is a modified version of the Guice 2.0 Struts 2 plugin that fixes three issues.
 * The ProvidedInterceptor.destroy() checks to ensure it was created before calling destroy().
 * The ObjectFactoryModule.configure() will install() a module defined by the "wsup.guice.module"
 * parameter, this allows module defined in struts.xml to be overridden by the parameter
 * "guice.module". This uses SLF4J instead of java.util.logging.<br>
 * <br>
 * Configure Struts to use this ObjectFactory in the strut.xml file:
 *
 * <pre>
 *   [bean type="com.opensymphony.xwork2.ObjectFactory" name="wsupGuiceObjectFactory"
 *      class="net.sourceforge.wsup.struts2.guice.WsupGuiceObjectFactory" /]
 *   [constant name="struts.objectFactory" value="wsupGuiceObjectFactory" /]
 *   [constant name="wsup.guice.module" value="com.mycompany.MainModule" /]
 * </pre>
 *
 * <br>
 * <br>
 * In web.xml you also configure the GuiceFilter:
 *
 * <pre>
 *  [filter]
 *    [filter-name]guice[/filter-name]
 *    [filter-class]com.google.inject.servlet.GuiceFilter[/filter-class]
 *  [/filter]
 *  [filter-mapping]
 *    [filter-name]guice[/filter-name]
 *    [url-pattern]/*[/url-pattern]
 *  [/filter-mapping]
 * </pre>
 *
 * @author Greg Lindholm
 *
 */
public class WsupGuiceObjectFactory extends ObjectFactory
{
    private static final long         serialVersionUID = 7305692935010327688L;

    private static final Logger       logger           = LoggerFactory
                                                           .getLogger(WsupGuiceObjectFactory.class);

    private List<ProvidedInterceptor> interceptors     = new ArrayList<ProvidedInterceptor>();
    private Module                    module;
    private volatile Injector         injector;

    public WsupGuiceObjectFactory()
    {
        super();
    }

    @Override
    public boolean isNoArgConstructorRequired()
    {
        return false;
    }

    @Inject(value = "wsup.guice.module", required = false)
    void setModule(String moduleClassName)
    {
        try
        {
            // Instantiate user's module.
            @SuppressWarnings( { "unchecked" })
            Class<? extends Module> moduleClass = (Class<? extends Module>) Class
                .forName(moduleClassName);
            this.module = moduleClass.newInstance();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    protected Set<Class<?>> boundClasses = new HashSet<Class<?>>();

    @SuppressWarnings("rawtypes")
    @Override
    public Class getClassInstance(String name) throws ClassNotFoundException
    {
        Class<?> clazz = super.getClassInstance(name);

        if (injector == null)
        {
            synchronized (this)
            {
                // CHECKSTYLE:OFF - Double Check idiom works with volatile
                if (injector == null)
                {
                    // We can only bind each class once.
                    if (!boundClasses.contains(clazz))
                    {
                        try
                        {
                            // Calling these methods now helps us detect
                            // ClassNotFoundErrors
                            // early.
                            clazz.getDeclaredFields();
                            clazz.getDeclaredMethods();

                            boundClasses.add(clazz);
                        }
                        catch (Throwable t)
                        {
                            // Struts should still work even though some classes
                            // aren't in the classpath. It appears we always get
                            // the
                            // exception here when this is the case.
                            return clazz;
                        }
                    }
                }
                // CHECKSTYLE:ON
            }
        }

        return clazz;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Object buildBean(Class clazz, Map extraContext)
    {
        if (injector == null)
        {
            synchronized (this)
            {
                // CHECKSTYLE:OFF - Double Check idiom works with volatile
                if (injector == null)
                {
                    createInjector();
                }
                // CHECKSTYLE:ON
            }
        }

        return injector.getInstance(clazz);
    }

    private void createInjector()
    {
        try
        {
            logger.info("Creating injector...");
            this.injector = Guice.createInjector(new ObjectFactoryModule());

            // Inject interceptors.
            for (ProvidedInterceptor interceptor : interceptors)
            {
                interceptor.inject();
            }

        }
        catch (RuntimeException t)
        {
            logger.error("Unable to create Guice injector", t);
            throw t;
        }
        logger.info("Injector created successfully.");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Interceptor buildInterceptor(InterceptorConfig interceptorConfig,
                                        Map interceptorRefParams) throws ConfigurationException
    {
        // Ensure the interceptor class is present.
        Class<? extends Interceptor> interceptorClass;
        try
        {
            interceptorClass = getClassInstance(interceptorConfig.getClassName());
        }
        catch (ClassNotFoundException e)
        {
            throw new RuntimeException(e);
        }

        ProvidedInterceptor providedInterceptor = new ProvidedInterceptor(interceptorConfig,
                                                                          interceptorRefParams,
                                                                          interceptorClass);
        addInterceptor(providedInterceptor);
        return providedInterceptor;
    }
    
    public void addInterceptor(ProvidedInterceptor interceptor)
    {
        interceptors.add(interceptor);
    }

    Interceptor superBuildInterceptor(InterceptorConfig interceptorConfig,
                                      Map<String, String> interceptorRefParams)
        throws ConfigurationException
    {
        return super.buildInterceptor(interceptorConfig, interceptorRefParams);
    }

    class ObjectFactoryModule extends AbstractModule
    {
        @SuppressWarnings("synthetic-access")
        protected void configure()
        {
            // Install default servlet bindings.
            install(new ServletModule());

            // Install user's module.
            if (module != null)
            {
                logger.info("Installing " + module + "...");
                install(module);
            }
            else
            {
                logger.info("No module found. Set 'wsup.guice.module' to a Module "
                            + "class name if you'd like to use one.");
            }

            // Tell the injector about all the action classes, etc., so
            // it can validate them at startup.
            for (Class<?> boundClass : boundClasses)
            {
                bind(boundClass);
            }

            // Validate the interceptor class.
            for (ProvidedInterceptor interceptor : interceptors)
            {
                interceptor.validate(binder());
            }
        }
    }

    class ProvidedInterceptor implements Interceptor
    {
        private static final long                  serialVersionUID = 8592974979677816921L;
        private final InterceptorConfig            config;
        private final Map<String, String>          params;
        private final Class<? extends Interceptor> interceptorClass;
        private Interceptor                        delegate;

        ProvidedInterceptor(InterceptorConfig config,
                            Map<String, String> params,
                            Class<? extends Interceptor> interceptorClass)
        {
            this.config = config;
            this.params = params;
            this.interceptorClass = interceptorClass;
        }

        void validate(Binder binder)
        {
            if (hasScope(interceptorClass))
            {
                binder.addError("Scoping interceptors is not currently supported."
                                + " Please remove the scope annotation from "
                                + interceptorClass.getName()
                                + ".");
            }

            // Make sure it implements Interceptor.
            if (!Interceptor.class.isAssignableFrom(interceptorClass))
            {
                binder.addError(interceptorClass.getName()
                                + " must implement "
                                + Interceptor.class.getName()
                                + ".");
            }
        }

        void inject()
        {
            delegate = superBuildInterceptor(config, params);
        }

        public void destroy()
        {
            if (delegate != null)
            {
                delegate.destroy();
            }
        }

        public void init()
        {
            throw new AssertionError();
        }

        public String intercept(ActionInvocation invocation) throws Exception
        {
            return delegate.intercept(invocation);
        }
    }

    /**
     * Returns true if the given class has a scope annotation.
     */
    static boolean hasScope(Class<? extends Interceptor> interceptorClass)
    {
        for (Annotation annotation : interceptorClass.getAnnotations())
        {
            if (annotation.annotationType().isAnnotationPresent(ScopeAnnotation.class))
            {
                return true;
            }
        }
        return false;
    }
}
