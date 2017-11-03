/*
 *  Copyright 2012 Greg Lindholm
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

package net.sourceforge.wsup.hibernate4.utils;

import org.hibernate.criterion.LikeExpression;

public class EscapedLikeExpression extends LikeExpression
{
    private static final long serialVersionUID = 1911257340154968368L;
    private static final char ESCAPE           = '!';

    public EscapedLikeExpression(String propertyName, String value)
    {
        super(propertyName,
              "%" + LikeClauseHelper.escapeLikeValue(value, ESCAPE) + "%",
              new Character(ESCAPE),
              true);
    }
}
