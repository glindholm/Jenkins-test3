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

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Generalized interface representing the various objects that can be drawn to.
 * 
 * @author Kevin Hunter
 * 
 */
public interface Drawable
{
    /**
     * Width of the <code>Drawable</code>, in pixels.
     * 
     * @return width of the <code>Drawable</code>, in pixels.
     */
    public int getWidth();

    /**
     * Height of the <code>Drawable</code>, in pixels.
     * 
     * @return height of the <code>Drawable</code>, in pixels.
     */
    public int getHeight();

    /**
     * Resolution of the <code>Drawable</code>, in dots per inch.
     * 
     * @return Resolution in dots per inch.
     */
    public int getResolutionDPI();

    /**
     * Fill the specified rectangle with the current color.
     * 
     * @param x Origin x (in scaled values)
     * @param y Origin y (in scaled values)
     * @param width Width (in scaled values)
     * @param height Height (in scaled values)
     */
    public void fillRect(int x, int y, int width, int height);

    /**
     * Fill a rectangle with rounded corners.
     * 
     * @param x Origin x (in scaled values)
     * @param y Origin y (in scaled values)
     * @param width Width (in scaled values)
     * @param height Height (in scaled values)
     * @param arcRadius Radius (in scaled values) of corner arc.
     */
    public void fillRoundRect(int x, int y, int width, int height, int arcRadius);

    /**
     * Set the font that will be used to draw text.
     * 
     * @param font <code>Font</code> to be used.
     * @see #drawString(String, int, int)
     */
    public void setFont(Font font);

    /**
     * Determine the rectangle that contains the specified text, based on the <code>Font</code>
     * currently set into the <code>Drawable</code>.
     * 
     * @param text Text to be measured
     * @return <code>Rectangle2D</code> that contains the text
     * @see #setFont(Font)
     */
    public Rectangle2D measureText(String text);

    /**
     * Return the <code>FontMetrics</code> for the <code>Font</code> currently set into the
     * <code>Drawable</code>.
     * 
     * @return <code>FontMetrics</code> for the current <code>Font</code>.
     * @see #setFont(Font)
     */
    public FontMetrics getFontMetrics();

    /**
     * Draw a string at the specified position using the current font. The <code>x</code> and
     * <code>y</code> coordinates identify the character origin position (usually on the baseline)
     * for the first character in the string.
     * 
     * @param text String to be drawn
     * @param x String origin x
     * @param y String origin y
     */
    public void drawString(String text, int x, int y);

    /**
     * Write the image to the specified stream.
     * 
     * @param stream <code>OutputStream</code> to which the image data should be sent.
     * @throws IOException If the stream can't be written.
     */
    public void export(OutputStream stream) throws IOException;

    /**
     * Release the internal resources associated with this object. This method must
     * be the last operation called on the object.
     */
    public void dispose();
}
