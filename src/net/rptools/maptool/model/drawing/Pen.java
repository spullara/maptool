/* The MIT License
 * 
 * Copyright (c) 2005 David Rice, Trevor Croft
 * 
 * Permission is hereby granted, free of charge, to any person 
 * obtaining a copy of this software and associated documentation files 
 * (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, 
 * publish, distribute, sublicense, and/or sell copies of the Software, 
 * and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be 
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS 
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN 
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
 * SOFTWARE.
 */
package net.rptools.maptool.model.drawing;

import java.awt.Color;

/**
 * The color and thickness to draw a {@link Drawable}with. Also used to erase
 * by drawing {@link Drawable}s with a Pen whose {@link #setEraser}is true.
 */
public class Pen {
    public static int MODE_SOLID       = 0;
    public static int MODE_TRANSPARENT = 1;

    public static Pen DEFAULT = new Pen(new DrawableColorPaint(Color.black), 3.0f);

    private int foregroundMode = MODE_SOLID;
    private DrawablePaint paint;

    private int backgroundMode = MODE_SOLID;
    private DrawablePaint backgroundPaint;

    private float thickness;
    private boolean eraser;

    public Pen() {
    }

    public Pen(DrawablePaint paint, float thickness) {
        this(paint, thickness, false);
    }

    public Pen(DrawablePaint paint, float thickness, boolean eraser) {
        this.paint = paint;
        this.thickness = thickness;
        this.eraser = eraser;
    }

    public Pen(Pen copy) {
        this.paint = copy.paint;
        this.foregroundMode = copy.foregroundMode;
        this.backgroundPaint = copy.backgroundPaint;
        this.backgroundMode = copy.backgroundMode;
        this.thickness = copy.thickness;
        this.eraser = copy.eraser;
    }

    public DrawablePaint getPaint() {
        return paint;
    }

    public void setPaint(DrawablePaint paint) {
        this.paint = paint;
    }

    public DrawablePaint getBackgroundPaint() {
        return backgroundPaint;
    }

    public void setBackgroundPaint(DrawablePaint paint) {
        this.backgroundPaint = paint;
    }

    public boolean isEraser() {
        return eraser;
    }

    public void setEraser(boolean eraser) {
        this.eraser = eraser;
    }

    public float getThickness() {
        return thickness;
    }

    public void setThickness(float thickness) {
        this.thickness = thickness;
    }

    public int getBackgroundMode() {
        return backgroundMode;
    }

    public void setBackgroundMode(int backgroundMode) {
        this.backgroundMode = backgroundMode;
    }

    public int getForegroundMode() {
        return foregroundMode;
    }

    public void setForegroundMode(int foregroundMode) {
        this.foregroundMode = foregroundMode;
    }
}
