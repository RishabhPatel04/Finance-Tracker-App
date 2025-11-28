package com.example.financetracker.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.financetracker.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DonutChartView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rectF = new RectF();
    private float centerX = 0f;
    private float centerY = 0f;
    private float radius = 0f;
    private List<Float> data = new ArrayList<>();
    private List<Integer> colors = new ArrayList<>();
    private float total = 0f;
    private static final float DONUT_HOLE_RATIO = 0.6f;
    private String centerLabel = "Total Spent";

    public DonutChartView(Context context) {
        super(context);
        init(context);
    }

    public DonutChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DonutChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        // Default colors
        List<Integer> defaultColors = new ArrayList<>();
        defaultColors.add(ContextCompat.getColor(context, R.color.primary));
        defaultColors.add(ContextCompat.getColor(context, R.color.primary_dark));
        defaultColors.add(ContextCompat.getColor(context, R.color.primary_end));
        
        this.colors = new ArrayList<>(defaultColors); // Create a new list to avoid external modifications

        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(42f);
        textPaint.setColor(ContextCompat.getColor(context, R.color.text_primary));
        textPaint.setFakeBoldText(true);

        labelPaint.setTextAlign(Paint.Align.CENTER);
        labelPaint.setTextSize(26f);
        labelPaint.setColor(ContextCompat.getColor(context, R.color.text_secondary));
    }

    public void setData(@Nullable List<Float> values, @Nullable List<Integer> colorList) {
        // Handle null or empty data
        if (values == null || values.isEmpty()) {
            this.data = new ArrayList<>();
            this.total = 0f;
            invalidate();
            return;
        }

        // Filter out negative values
        List<Float> validValues = new ArrayList<>();
        for (float value : values) {
            if (value > 0) {
                validValues.add(value);
            }
        }

        this.data = validValues;
        
        // Calculate total of valid values
        this.total = 0f;
        for (float value : this.data) {
            this.total += value;
        }

        // Update colors if provided
        if (colorList != null && !colorList.isEmpty()) {
            this.colors = new ArrayList<>(colorList); // Create a new list to avoid external modifications
        }

        invalidate();
    }

    public void setCenterLabel(@Nullable String label) {
        this.centerLabel = label != null ? label : "";
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w == 0 || h == 0) return;
        
        float min = Math.min(w, h) * 0.9f;
        radius = min / 2f;
        centerX = w / 2f;
        centerY = h / 2f;
        rectF.set(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // Don't draw if no data or total is zero
        if (data.isEmpty() || total <= 0f) {
            return;
        }

        float startAngle = -90f; // Start from top
        
        // Draw the donut chart
        for (int i = 0; i < data.size(); i++) {
            if (i >= 0 && i < colors.size()) { // Ensure we have a color for this segment
                float value = data.get(i);
                if (value > 0) { // Only draw positive values
                    float sweepAngle = (value / total) * 360f;
                    paint.setColor(colors.get(i % colors.size()));
                    canvas.drawArc(rectF, startAngle, sweepAngle, true, paint);
                    startAngle += sweepAngle;
                }
            }
        }
        
        // Draw the center hole
        paint.setColor(ContextCompat.getColor(getContext(), R.color.surface));
        canvas.drawCircle(centerX, centerY, radius * DONUT_HOLE_RATIO, paint);

        // Draw label and total in the center (two lines)
        String labelText = centerLabel;
        String totalText = String.format(Locale.getDefault(), "$%,.0f", total);

        // Baseline around which we center the two lines
        float centerBaseline = centerY - (textPaint.descent() + textPaint.ascent()) / 2f;

        // Position label slightly above the center
        float labelY = centerBaseline - textPaint.getTextSize() * 0.6f;
        // Position amount slightly below the center
        float amountY = centerBaseline + labelPaint.getTextSize() * 0.6f;

        if (!labelText.isEmpty()) {
            canvas.drawText(labelText, centerX, labelY, labelPaint);
        }
        canvas.drawText(totalText, centerX, amountY, textPaint);
    }
}