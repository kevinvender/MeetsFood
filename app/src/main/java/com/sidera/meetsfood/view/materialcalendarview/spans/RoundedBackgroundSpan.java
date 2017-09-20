package com.sidera.meetsfood.view.materialcalendarview.spans;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;

/**
 * Created by elisa.schir on 09/08/2016.
 */
public class RoundedBackgroundSpan implements LineBackgroundSpan {

    /**
     * Default radius used
     */
    public static final float DEFAULT_RADIUS = 3;

    private final float radius;
    private final int color;
    private final int txt_color;

    /**
     * @see #DEFAULT_RADIUS
     */
    public RoundedBackgroundSpan() {
        this.radius = DEFAULT_RADIUS;
        this.color = 0;
        this.txt_color = 0;
    }

    /**
     * @see #DEFAULT_RADIUS
     */
    public RoundedBackgroundSpan(int color) {
        this.radius = DEFAULT_RADIUS;
        this.color = color;
        this.txt_color = 0;
    }

    public RoundedBackgroundSpan(float radius) {
        this.radius = radius;
        this.color = 0;
        this.txt_color = 0;
    }

    /**
     * Create a span to draw a dot using a specified radius and color
     *
     * @param radius radius for the dot
     * @param color color of the dot
     */
    public RoundedBackgroundSpan(float radius, int color) {
        this.radius = radius;
        this.color = color;
        this.txt_color = 0;
    }

    public RoundedBackgroundSpan(float radius, int color,int txt_color) {
        this.radius = radius;
        this.color = color;
        this.txt_color = txt_color;
    }

    @Override
    public void drawBackground(
            Canvas canvas, Paint paint,
            int left, int right, int top, int baseline, int bottom,
            CharSequence charSequence,
            int start, int end, int lineNum
    ) {
        int oldColor = paint.getColor();
        if(color != 0) {
            paint.setColor(color);
        }
        canvas.drawCircle((left + right) / 2, (bottom-top)/2, radius, paint);
        paint.setColor(txt_color);
    }
}
