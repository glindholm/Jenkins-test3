/*
 *  Copyright (c) 2012 Greg Lindholm
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

package net.sourceforge.wsup.email;

import net.jcip.annotations.Immutable;

/**
 * EmailConfig encapsulates the information needed to use a mail server. This includes the hostName,
 * smtpPort, user, password and TLS flag.
 *
 */
@Immutable
public class EmailConfig
{
    private final String  host;
    private final Integer port;
    private final Boolean tls;
    private final String  user;
    private final String  password;

    public EmailConfig(String host, Integer port, Boolean tls, String user, String password)
    {
        this.host = host;
        this.port = port;
        this.tls = tls;
        this.user = user;
        this.password = password;
    }

    public String getHost()
    {
        return host;
    }

    public Integer getPort()
    {
        return port;
    }

    public Boolean getTls()
    {
        return tls;
    }

    public String getUser()
    {
        return user;
    }

    public String getPassword()
    {
        return password;
    }

}
