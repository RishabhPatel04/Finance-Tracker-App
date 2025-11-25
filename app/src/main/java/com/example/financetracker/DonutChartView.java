package com.example.financetracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

/**
 * Custom view that displays a donut chart for spending by category.
 * Shows multiple segments with different colors representing different categories.
 */
public class DonutChartView extends View {
    private Paint paint;
    private RectF rectF;
    private float[] values;
    private int[] colors;
    private float strokeWidth;

    public DonutChartView(Context context) {
        super(context);
        init();
    }

    public DonutChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DonutChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        rectF = new RectF();
        strokeWidth = 40f;

        // Default values matching the design
        values = new float[]{1200f, 320f, 120f}; // Rent, Groceries, Transport
        colors = new int[]{
                ContextCompat.getColor(getContext(), R.color.chart_purple),
                ContextCompat.getColor(getContext(), R.color.chart_red),
                ContextCompat.getColor(getContext(), R.color.chart_orange)
        };
    }

    /**
     * Sets the data for the donut chart.
     *
     * @param values array of values for each segment
     * @param colors array of colors for each segment (must match values length)
     */
    public void setData(float[] values, int[] colors) {
        if (values.length != colors.length) {
            throw new IllegalArgumentException("Values and colors arrays must have the same length");
        }
        this.values = values;
        this.colors = colors;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (values == null || values.length == 0) {
            return;
        }

        float total = 0;
        for (float value : values) {
            total += value;
        }

        if (total == 0) {
            return;
        }

        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float radius = Math.min(centerX, centerY) - strokeWidth / 2;

        rectF.set(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius
        );

        paint.setStrokeWidth(strokeWidth);

        float startAngle = -90f; // Start from top
        float sweepAngle;

        for (int i = 0; i < values.length; i++) {
            sweepAngle = (values[i] / total) * 360f;
            paint.setColor(colors[i]);
            canvas.drawArc(rectF, startAngle, sweepAngle, false, paint);
            startAngle += sweepAngle;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = Math.min(
                MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec)
        );
        setMeasuredDimension(size, size);
    }
}

