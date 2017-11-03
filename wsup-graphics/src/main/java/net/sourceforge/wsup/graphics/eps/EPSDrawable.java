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

package net.sourceforge.wsup.graphics.eps;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import net.sourceforge.wsup.graphics.Drawable;

/**
 * This class allows the creation of files in the Encapsulated PostScript (EPS) format.
 * <p>
 * This class is "two-headed." It provides a variety of methods that allow writing data in the
 * "native" EPS coordinate system, which is measured in points (72 points per inch). It also
 * provides the <code>Drawable</code> methods. Because the latter are pixel-based, for the purposes
 * of <code>Drawable</code> processing, it provides an artificial "resolution" of 7,200 DPI. This is
 * equivalent to 1/100th of a point, which is typically sufficient for most applications.
 * </p>
 * <p>
 * Care must be used, because the two different approaches use different origins. The PostScript
 * native methods use the standard PostScript orientation, which has the origin a the lower-left
 * corner, and Y increasing upwards. The <code>Drawable</code> methods have the origin at the
 * <u>top</u> left corner, and have <code>Y</code> increasing <u>down</u>wards.
 * </p>
 * <p>
 * Note that PostScript does not support the concept of transparency - all colors are opaque. In
 * order to make it appear that we do support transparency, this class supports a "transparent"
 * flag. When this is set, <code>fill</code> and <code>stroke</code> operations are skipped, thus
 * not actually drawing anything. This allows some of the derived classes that use palettes to
 * appear to support palette entries that are transparent colors.
 * </p>
 * 
 * @author Kevin Hunter
 */
public class EPSDrawable implements Drawable
{
    /**
     * Convenience constant for the number of points in an inch.
     */
    public static final int     POINTS_PER_INCH         = 72;
    /**
     * Sub-point resolution that is used when converting <code>Drawable</code> dimensions to points.
     */
    public static final int     PS_RESOLUTION           = 100;
    /**
     * The resulting "artificial" resolution that is implemented by the class when the
     * <code>Drawable</code> methods are used.
     */
    public static final int     ARTIFICIAL_DPI          = POINTS_PER_INCH * PS_RESOLUTION;
    /*
     * Internal PostScript scale factor for Drawable conversions.
     */
    private static final double PS_SCALE                = (double) 1.0 / (double) PS_RESOLUTION;

    /*
     * Constants used when converting Java Shape's into PostScript
     */
    private static final int    POINTS_PER_PATH_SEGMENT = 3;
    private static final int    POINT_0_X               = 0;
    private static final int    POINT_0_Y               = 1;
    private static final int    POINT_1_X               = 2;
    private static final int    POINT_1_Y               = 3;
    private static final int    POINT_2_X               = 4;
    private static final int    POINT_2_Y               = 5;
    private static final double ONE_THIRD               = 1.0 / 3.0;
    private static final double TWO_THIRDS              = 2.0 / 3.0;

    /*
     * Formatter used to convert coordinate numbers (in points) into strings.
     */
    private static NumberFormat numberFormatter         = new DecimalFormat("#0.00");

    /*
     * Image and Graphics used to handle Font-related calculations. Not actually ever drawn into.
     */
    private final BufferedImage image;
    private final Graphics2D    graphics;

    /*
     * Dimensions of the EPS image.
     */
    private final double        widthPts;
    private final double        heightPts;

    /*
     * "true" if this is a CMYK EPS, "false" if an RGB
     */
    private final boolean       cmyk;

    /*
     * Buffer into which PostScript commands are accumulated.
     */
    private final StringBuilder  buffer                  = new StringBuilder();

    /*
     * Optional "creator" and "title" strings.
     */
    private String              creator;
    private String              title;

    /*
     * Flag indicating whether or not we are emulating transparency at the moment.
     */
    private boolean             transparent;

    /**
     * Constructor. Note that when the bounding box for the EPS file is created, the
     * dimensions are rounded up to the nearest integral point value.
     * 
     * @param widthPts Width of the image, in points.
     * @param heightPts Height of the image, in points.
     * @param cmyk <code>true</code> if this is to be a CMYK EPS, <code>false</code> for RGB.
     */
    public EPSDrawable(double widthPts, double heightPts, boolean cmyk)
    {
        this.widthPts = widthPts;
        this.heightPts = heightPts;
        this.cmyk = cmyk;

        byte[] entries = new byte[2];
        entries[0] = 0;
        entries[1] = (byte) 255;

        IndexColorModel model = new IndexColorModel(8, 2, entries, entries, entries);
        image = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_INDEXED, model);
        graphics = image.createGraphics();
    }

    /**
     * Convenience method for converting values expressed in points into the integer
     * values used by the <code>Drawable</code> interface.
     * 
     * @param points Dimension, in points.
     * @return Dimension, in "pixels."
     */
    public static int pointsToPseudoPixels(double points)
    {
        return (int) (points / POINTS_PER_INCH * ARTIFICIAL_DPI + 0.5);
    }

    @Override
    public int getHeight()
    {
        return pointsToPseudoPixels(heightPts);
    }

    @Override
    public int getWidth()
    {
        return pointsToPseudoPixels(widthPts);
    }

    @Override
    public int getResolutionDPI()
    {
        return ARTIFICIAL_DPI;
    }

    @Override
    public void dispose()
    {
        graphics.dispose();
    }

    @Override
    public void fillRect(int x, int y, int width, int height)
    {
        gsave();
        setupDrawableCoordinateSystem();
        createRectPath((double) x, (double) y, (double) width, (double) height);
        fill();
        grestore();
    }

    @Override
    public void fillRoundRect(int x, int y, int width, int height, int arcRadius)
    {
        gsave();
        setupDrawableCoordinateSystem();
        createRoundRectPath((double) x,
                            (double) y,
                            (double) width,
                            (double) height,
                            (double) arcRadius);
        fill();
        grestore();
    }

    @Override
    public void setFont(Font font)
    {
        graphics.setFont(font);
    }

    @Override
    public Rectangle2D measureText(String text)
    {
        FontRenderContext context = graphics.getFontRenderContext();
        Font font = graphics.getFont();
        return font.getStringBounds(text, context);
    }

    @Override
    public FontMetrics getFontMetrics()
    {
        return graphics.getFontMetrics();
    }

    @Override
    public void drawString(String text, int x, int y)
    {
        FontRenderContext context = graphics.getFontRenderContext();
        Font font = graphics.getFont();
        Shape outline = font.createGlyphVector(context, text).getOutline();

        gsave();
        setupDrawableCoordinateSystem();
        translate(x, y);
        createShapePath(outline);
        fill();
        grestore();
    }

    @Override
    public void export(OutputStream stream) throws IOException
    {
        OutputStreamWriter writer = new OutputStreamWriter(stream);

        writer.write("%!PS-Adobe-3.0 EPSF-3.0\n");
        if (getCreator() != null)
        {
            writer.write("%%Creator: ");
            writer.write(getCreator());
            writer.write('\n');
        }

        if (getTitle() != null)
        {
            writer.write("%%Title: ");
            writer.write(getTitle());
            writer.write('\n');
        }

        /*
         * BoundingBox dimensions are in points. They are required to be integers, so round up the
         * width and height as required.
         */

        writer.write("%%BoundingBox: 0 0 ");
        writer.write(Integer.toString((int) Math.ceil(widthPts)));
        writer.write(' ');
        writer.write(Integer.toString((int) Math.ceil(heightPts)));
        writer.write("\n");

        if (getCmyk())
        {
            writer.write("%%Extensions: CMYK\n");
            writer.write("%%DocumentProcessColors: Cyan Magenta Yellow Black\n");
        }

        writer.write("%%DocumentData: Clean7Bit\n");
        writer.write("%%EndComments\n");

        writer.write("gsave\n");

        writer.write(buffer.toString());

        writer.write("grestore\n");
        writer.write("showpage\n");
        writer.write("%%EOF\n");
        writer.flush();
    }

    /**
     * Return the height of the image in points.
     * 
     * @return Height of the image in points.
     */
    public double getHeightPts()
    {
        return heightPts;
    }

    /**
     * Return the width of the image in points.
     * 
     * @return Width of the image in points.
     */
    public double getWidthPts()
    {
        return widthPts;
    }

    /**
     * Is this a CMYK EPS?
     * 
     * @return <code>true</code> if so, <code>false</code> if RGB.
     */
    public boolean getCmyk()
    {
        return cmyk;
    }

    /**
     * Get optional "creator" string.
     * 
     * @return "Creator" string to be included in file.
     */
    public String getCreator()
    {
        return creator;
    }

    /**
     * Set optional "creator" string.
     * <p>
     * Only characters in the ASCII set are supported
     * </p>
     * 
     * @param creator "Creator" string to be included in file.
     */
    public void setCreator(String creator)
    {
        this.creator = creator;
    }

    /**
     * Get optional "title" string.
     * 
     * @return "Title" string to be included in file.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Set optional "title" string.
     * <p>
     * Only characters in the ASCII set are supported
     * </p>
     * 
     * @param title "Title" string to be included in file.
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Writes the specified value into the PostScript stream.
     * 
     * @param value Value to be output.
     */
    protected void write(double value)
    {
        buffer.append(numberFormatter.format(value));
        buffer.append(' ');
    }

    /**
     * Writes the specified string into the PostScript stream.
     * <p>
     * Only characters in the ASCII set are supported
     * </p>
     * 
     * @param string Value to be output.
     */
    protected void write(String string)
    {
        buffer.append(string);
    }

    /**
     * Writes the specified character into the PostScript stream.
     * <p>
     * Only characters in the ASCII set are supported
     * </p>
     * 
     * @param c Character to be output.
     */
    protected void write(char c)
    {
        buffer.append(c);
    }

    /**
     * Write a value, followed by an operation string.
     * 
     * @param value Value to be written
     * @param operation Operation name. Must end in \n or another white space character.
     */
    protected void write(double value, String operation)
    {
        write(value);
        write(operation);
    }

    /**
     * Writes a pair of values, such as an x,y coordinate pair.
     * 
     * @param x First value to write
     * @param y Second value to write
     */
    protected void write(double x, double y)
    {
        write(x);
        write(y);
    }

    /**
     * Writes a pair of values, such as an x,y coordinate pair, followed by an operation string.
     * 
     * @param x First value to write
     * @param y Second value to write
     * @param operation Operation name. Must end in \n or another white space character.
     */
    protected void write(double x, double y, String operation)
    {
        write(x);
        write(y);
        write(operation);
    }

    /**
     * Set the transparency flag. If <code>true</code>, this will cause any <code>fill</code> or
     * <code>stroke</code> operations to be skipped, thus simulating transparency. Does not affect
     * the output of path information, however.
     * 
     * @param transparent <code>true</code> to simulate transparency, <code>false</code> for default
     *            behavior.
     */
    public void setTransparent(boolean transparent)
    {
        this.transparent = transparent;
    }

    /**
     * Get the transparency flag value.
     * 
     * @return <code>true</code> if transparency is being simulated, <code>false</code> for default
     *         behavior.
     */
    public boolean getTransparent()
    {
        return transparent;
    }

    /*
     * Convert the coordinate system to that used by Drawable operations.
     */
    private void setupDrawableCoordinateSystem()
    {
        translate(0, heightPts);
        scale(PS_SCALE, -PS_SCALE);
    }

    /**
     * Native method to create a PostScript path from a Java <code>Shape</code>
     */
    public void createShapePath(Shape shape)
    {
        /*
         * Use a PathIterator to walk through the various components of the Shape. PathIterator
         * returns between zero and 3 points per path segment by writing them into an array of
         * doubles that we provide. Based on the segment type, convert the points and the operation
         * into the equivalent PostScript operation. All the points in question are offset by
         * dx and dy in order to move the shape to its appropriate position.
         */

        double[] coords = new double[POINTS_PER_PATH_SEGMENT * 2]; // points returned by PathIterator
        double lastEndpointX = 0.0; // last endpoint (needed for QUAD algorithm)
        double lastEndpointY = 0.0;
        double x1, y1;
        double x2, y2;
        double x3, y3;

        newpath();

        PathIterator pathIterator = shape.getPathIterator(null);
        while (!pathIterator.isDone())
        {
            int segmentType = pathIterator.currentSegment(coords);

            switch (segmentType)
            {
            case PathIterator.SEG_MOVETO:
                /*
                 * Move to the specified point
                 */
                x1 = coords[POINT_0_X];
                y1 = coords[POINT_0_Y];
                moveto(x1, y1);
                lastEndpointX = x1;
                lastEndpointY = y1;
                break;

            case PathIterator.SEG_LINETO:
                /*
                 * Draw a line from the last point to the specified point
                 */
                x1 = coords[POINT_0_X];
                y1 = coords[POINT_0_Y];
                lineto(x1, y1);
                lastEndpointX = x1;
                lastEndpointY = y1;
                break;

            case PathIterator.SEG_CUBICTO:
                /*
                 * Cubic curve from the current point using the three points returned in the array
                 * as the control points.
                 */
                x1 = coords[POINT_0_X];
                y1 = coords[POINT_0_Y];
                x2 = coords[POINT_1_X];
                y2 = coords[POINT_1_Y];
                x3 = coords[POINT_2_X];
                y3 = coords[POINT_2_Y];

                curveto(x1, y1, x2, y2, x3, y3);

                lastEndpointX = x2;
                lastEndpointY = y2;
                break;

            case PathIterator.SEG_QUADTO:
                /*
                 * Postscript doesn't have a quadratic curve operator. The following algorithm
                 * turns the two-point quadratic curve into a three-point cubic curve.
                 */
                x1 = lastEndpointX + TWO_THIRDS * (coords[POINT_0_X] - lastEndpointX);
                y1 = lastEndpointY + TWO_THIRDS * (coords[POINT_0_Y] - lastEndpointY);
                x2 = coords[POINT_0_X] + ONE_THIRD * (coords[POINT_1_X] - coords[POINT_0_X]);
                y2 = coords[POINT_0_Y] + ONE_THIRD * (coords[POINT_1_Y] - coords[POINT_0_Y]);
                x3 = coords[POINT_1_X];
                y3 = coords[POINT_1_Y];

                curveto(x1, y1, x2, y2, x3, y3);

                lastEndpointX = x3;
                lastEndpointY = y3;
                break;

            case PathIterator.SEG_CLOSE:
                closepath();
                break;

            default:
                break;
            }

            pathIterator.next();
        }
    }

    /**
     * Native method to create a rectangular path.
     * 
     * @param xPts Origin <code>x</code> value, in points.
     * @param yPts Origin <code>y</code> value, in points.
     * @param widthPts Width, in points.
     * @param heightPts Height, in points.
     */
    public void createRectPath(double xPts, double yPts, double widthPts, double heightPts)
    {
        newpath();
        moveto(xPts, yPts);
        lineto(xPts + widthPts, yPts);
        lineto(xPts + widthPts, yPts + heightPts);
        lineto(xPts, yPts + heightPts);
        closepath();
    }

    /**
     * Native method to create a round-cornered rectangular path.
     * 
     * @param xPts Origin <code>x</code> value, in points.
     * @param yPts Origin <code>y</code> value, in points.
     * @param widthPts Width, in points.
     * @param heightPts Height, in points.
     * @param arcRadiusPts Radius of the corner arc, in points.
     */
    public void createRoundRectPath(double xPts,
                                    double yPts,
                                    double widthPts,
                                    double heightPts,
                                    double arcRadiusPts)
    {
        newpath();
        moveto(xPts + widthPts / 2, yPts);

        arct(xPts + widthPts, yPts, xPts + widthPts, yPts + heightPts, arcRadiusPts);
        arct(xPts + widthPts, yPts + heightPts, xPts, yPts + heightPts, arcRadiusPts);
        arct(xPts, yPts + heightPts, xPts, yPts, arcRadiusPts);
        arct(xPts, yPts, xPts + widthPts, yPts, arcRadiusPts);
        closepath();
    }

    /**
     * Native method to save the current graphics state.
     */
    public void gsave()
    {
        write("gsave\n");
    }

    /**
     * Native method to restore the previous graphics state.
     */
    public void grestore()
    {
        write("grestore\n");
    }

    /**
     * Native method to stroke the current path. Will omit the <code>stroke</code> command if the
     * <code>transparent</code> property is <code>true</code>.
     * 
     * @see #setTransparent(boolean)
     */
    public void stroke()
    {
        if (!transparent)
        {
            write("stroke\n");
        }
    }

    /**
     * Native method to fill the current path. Will omit the <code>fill</code> command if the
     * <code>transparent</code> property is <code>true</code>.
     * 
     * @see #setTransparent(boolean)
     */
    public void fill()
    {
        if (!transparent)
        {
            write("fill\n");
        }
    }

    /**
     * Native method to translate the PostScript origin.
     * 
     * @param xPts Amount to translate horizontally, in points.
     * @param yPts Amount to translate vertically, in points.
     */
    public void translate(double xPts, double yPts)
    {
        write(xPts, yPts, "translate\n");
    }

    /**
     * Native method to scale the PostScript coordinate space.
     * 
     * @param xScale Horizontal scale factor.
     * @param yScale Vertical scale factor.
     */
    public void scale(double xScale, double yScale)
    {
        write(xScale, yScale, "scale\n");
    }

    /**
     * Native method to move the current point.
     * 
     * @param xPts New <code>x</code> value, in points.
     * @param yPts New <code>y</code> value, in points.
     */
    public void moveto(double xPts, double yPts)
    {
        write(xPts, yPts, "moveto\n");
    }

    /**
     * Native method to draw a line from the current point to a new point.
     * 
     * @param xPts Destination <code>x</code> value, in points.
     * @param yPts Destination <code>y</code> value, in points.
     */
    public void lineto(double xPts, double yPts)
    {
        write(xPts, yPts, "lineto\n");
    }

    /**
     * Native method to draw a cubic curve.
     * 
     * @param x1 <code>x</code> value of first control point, in points.
     * @param y1 <code>y</code> value of first control point, in points.
     * @param x2 <code>x</code> value of second control point, in points.
     * @param y2 <code>y</code> value of second control point, in points.
     * @param x3 <code>x</code> value of third control point, in points.
     * @param y3 <code>y</code> value of third control point, in points.
     */
    public void curveto(double x1, double y1, double x2, double y2, double x3, double y3)
    {
        write(x1, y1);
        write(x2, y2);
        write(x3, y3, "curveto\n");
    }

    /**
     * Native method to begin a new path.
     */
    public void newpath()
    {
        write("newpath\n");
    }

    /**
     * Native method to close the current path.
     */
    public void closepath()
    {
        write("closepath\n");
    }

    /**
     * Native method to set the current drawing color in RGB values.
     * 
     * @param r Red component (0.0 - 1.0)
     * @param g Green component (0.0 - 1.0)
     * @param b Blue component (0.0 - 1.0)
     */
    public void setrgbolor(double r, double g, double b)
    {
        write(r);
        write(g);
        write(b);
        write("setrgbcolor\n");
    }

    /**
     * Native method to execute the PostScript "arct" operation. Draws an arc that is tangent to
     * both the line defined by (currentX,currentY)->(x1,y1) and the line defined by (x1,y1)->(x2,y2),
     * drawing a line from (currentX,currentY) to the beginning of the arc, if required.
     * 
     * @param x1    <code>x</code> value of first control point.
     * @param y1    <code>y</code> value of first control point.
     * @param x2    <code>x</code> value of second control point.
     * @param y2    <code>y</code> value of second control point.
     * @param r Radius value of arc.
     */
    public void arct(double x1, double y1, double x2, double y2, double r)
    {
        write(x1, y1);
        write(x2, y2);
        write(r, "arct\n");
    }

    /*
     * Unit test method to allow access to the internal buffer.
     */
    /* package */StringBuilder getInternalBuffer()
    {
        return buffer;
    }
}
