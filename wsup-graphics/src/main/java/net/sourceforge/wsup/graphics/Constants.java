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
 * Constants used inside the package.
 * 
 * @author Kevin Hunter
 */
public class Constants
{
    /**
     * Bit mask that leaves only the least significant byte in an int.
     */
    public static final int BYTE_MASK              = 0xFF;
    /**
     * Bit mask that leaves only the least significant word (2 bytes) in an int.
     */
    public static final int WORD_MASK              = 0xFFFF;
    /**
     * Constant to shift left or right by one byte.
     */
    public static final int ONE_BYTE_SHIFT         = 8;
    /**
     * Constant to shift left or right by two bytes.
     */
    public static final int TWO_BYTE_SHIFT         = 16;
    /**
     * Constant to shift left or right by three bytes.
     */
    public static final int THREE_BYTE_SHIFT       = 24;
    /**
     * Along with DPI_TO_DPM_DENOMINATOR, forms a fraction equal to 39.37, which is the
     * number of inches in a meter. This is used to convert from pixels per inch to pixels per
     * meter.
     */
    public static final int DPI_TO_DPM_NUMERATOR   = 3937;
    /**
     * Along with DPI_TO_DPM_NUMERATOR, forms a fraction equal to 39.37, which is the
     * number of inches in a meter. This is used to convert from pixels per inch to pixels per
     * meter.
     */
    public static final int DPI_TO_DPM_DENOMINATOR = 100;

    private Constants()
    {
    }

    /*package*/ static Constants codeCoverage()
    {
        return new Constants();
    }
}
