/*
 *  Copyright 2010 Greg Lindholm
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

package net.sourceforge.wsup.hibernate;

/**
 * This entity exposes a business key. A business key is a property or some combination of
 * properties that is unique for each instance with the same database identity. The business key is
 * recommended means to determine entity identity in the <code>equals</code> method of a
 * database entity. The business key is often the "natural primary key" if one is available.
 *
 * @author Greg Lindholm
 */
public interface BusinessKey
{
    /**
     * The business key for this entity.
     *
     * @return business key for this entity or <code>null</code> if the entity is in an invalid
     *         state and the business key cannot be determined.
     */
    String businessKey();
}
