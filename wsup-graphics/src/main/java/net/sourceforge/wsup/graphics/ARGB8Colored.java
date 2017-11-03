/*
 *  Copyright (c) 2011 Kevin Hunter
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

package net.sourceforge.wsup.graphics;

/**
 * This interface abstracts an object whose colors are specified by
 * RGB (and possibly alpha) values.
 * 
 * @author Kevin Hunter
 */
public interface ARGB8Colored
{
    public void setColor(ARGB8Color color);

    public void setColor(int alpha, int red, int green, int blue);

    public void setColor(int red, int green, int blue);
}
