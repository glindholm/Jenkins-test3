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
 * Abstracts objects that specify colors using indices into a palette (as opposed to direct color
 * models of some sort.)
 * 
 * @author Kevin Hunter
 */
public interface PaletteColored
{
    /**
     * Set the color to be used next.
     * 
     * @param index Index into the palette.
     */
    public void setColor(int index);

    /**
     * Get the number of entries in the palette.
     * 
     * @return number of entries in the palette.
     */
    public int getNumPaletteEntries();
}
