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

package net.sourceforge.wsup.graphics.bitmap;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import net.sourceforge.wsup.core.Assert;
import net.sourceforge.wsup.graphics.Drawable;

/**
 * This class implements the actual drawing code for the various bitmap
 * formats.  It manages a <code>BufferedImage</code> containing the actual
 * bitmap data and a <code>Graphics2D</code> instance used to draw into it.
 * 
 * @author Kevin Hunter
 */
public abstract class BaseBitmapDrawable implements Drawable
{
    private final BufferedImage image;
    private final Graphics2D    graphics;
    private int                 resolutionDPI;

    /**
     * Constructor.
     * 
     * @param image <code>BufferedImage</code> to be drawn into.
     * @param resolutionDPI Resolution in dots per inch (>= 0)
     */
    protected BaseBitmapDrawable(BufferedImage image, int resolutionDPI)
    {
        Assert.isNotNull(image, "Image cannot be null");
        Assert.isTrue(resolutionDPI >= 0, "Resolution must be >= 0");

        this.resolutionDPI = resolutionDPI;
        this.image = image;
        graphics = image.createGraphics();
    }
    
    @Override
    public int getWidth()
    {
        return image.getWidth();
    }
    
    @Override
    public int getHeight()
    {
        return image.getHeight();
    }

    @Override
    public int getResolutionDPI()
    {
        return resolutionDPI;
    }

    @Override
    public void dispose()
    {
        graphics.dispose();
    }

    @Override
    public void fillRect(int x, int y, int width, int height)
    {
        graphics.fillRect(x, y, width, height);
    }
    
    @Override
    public void fillRoundRect(int x, int y, int width, int height, int arcRadius)
    {
        graphics.fillRoundRect(x, y, width, height, arcRadius, arcRadius);
    }
    
    @Override
    public void setFont(Font font)
    {
        graphics.setFont(font);
    }
    
    @Override
    public Rectangle2D measureText(String text)
    {
        return graphics.getFont().getStringBounds(text, graphics.getFontRenderContext());
    }
    
    @Override
    public FontMetrics getFontMetrics()
    {
        return graphics.getFontMetrics();
    }
    
    @Override
    public void drawString(String text, int x, int y)
    {
        graphics.drawString(text, x, y);
    }

    /**
     * Access the underlying <code>BufferedImage</code> into which the drawing occurs.
     * 
     * @return <code>BufferedImage</code> instance. 
     */
    protected BufferedImage getImage()
    {
        return image;
    }

    /**
     * Access the underlying <code>Graphics</code> object.
     * 
     * @return <code>Graphics</code> instance. 
     */
    public Graphics2D getGraphics()
    {
        return graphics;
    }
}
