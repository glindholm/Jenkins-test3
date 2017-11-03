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
 * Check if two instances of the same entity have the same value. This is an alternative to
 * <code>equals</code> which for database beans is commonly used to determine entity identity.
 *
 * @author Greg Lindholm
 * @param <ENTITYCLASS>
 */
public interface SameValue<ENTITYCLASS>
{
    /**
     * Check if all the (user settable) business fields of another instance of this entity have the
     * same value. This method is used to determine if <code>other</code> has been modified.
     *
     * @param other
     *            another instance of the same entity
     * @return true if all (user settable) business fields have the same value.
     */
    boolean sameValue(ENTITYCLASS other);
}
