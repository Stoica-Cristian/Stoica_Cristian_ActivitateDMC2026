package com.example.laborator11;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Random;

public class ChartView extends View {
    private float[] data;
    private String chartType = "ColumnChart";
    private Paint paint;
    private int[] colors;

    // A pleasant material-like color palette
    private final int[] palette = {
            Color.parseColor("#2196F3"), // Blue
            Color.parseColor("#F44336"), // Red
            Color.parseColor("#4CAF50"), // Green
            Color.parseColor("#FFC107"), // Amber
            Color.parseColor("#9C27B0"), // Purple
            Color.parseColor("#00BCD4"), // Cyan
            Color.parseColor("#FF9800"), // Orange
            Color.parseColor("#E91E63"), // Pink
            Color.parseColor("#3F51B5"), // Indigo
            Color.parseColor("#8BC34A")  // Light Green
    };

    public ChartView(Context context) {
        super(context);
        init();
    }

    public ChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    public void setData(float[] data, String chartType) {
        this.data = data;
        this.chartType = chartType;
        if (data != null) {
            colors = new int[data.length];
            Random random = new Random();
            for (int i = 0; i < data.length; i++) {
                if (i < palette.length) {
                    colors[i] = palette[i];
                } else {
                    // Fallback to random colors if more values than palette entries
                    colors[i] = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
                }
            }
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (data == null || data.length == 0) {
            return;
        }

        switch (chartType) {
            case "PieChart":
                drawPieChart(canvas);
                break;
            case "BarChart":
                drawBarChart(canvas);
                break;
            case "ColumnChart":
            default:
                drawColumnChart(canvas);
                break;
        }
    }

    private void drawColumnChart(Canvas canvas) {
        float maxVal = getMaxValue();
        int width = getWidth();
        int height = getHeight();
        float margin = 50f;
        float availableWidth = width - 2 * margin;
        float barWidth = availableWidth / data.length;
        float chartHeight = height - 2 * margin;

        for (int i = 0; i < data.length; i++) {
            paint.setColor(colors[i]);
            float barHeight = (data[i] / maxVal) * chartHeight;
            float left = margin + i * barWidth + 10f;
            float top = height - margin - barHeight;
            float right = margin + (i + 1) * barWidth - 10f;
            float bottom = height - margin;

            canvas.drawRect(left, top, right, bottom, paint);

            paint.setColor(Color.DKGRAY);
            paint.setTextSize(35f);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(String.valueOf(data[i]), (left + right) / 2, top - 15f, paint);
        }
    }

    private void drawBarChart(Canvas canvas) {
        float maxVal = getMaxValue();
        int width = getWidth();
        int height = getHeight();
        float margin = 50f;
        float availableHeight = height - 2 * margin;
        float barHeight = availableHeight / data.length;
        float chartWidth = width - 2 * margin - 50f;

        for (int i = 0; i < data.length; i++) {
            paint.setColor(colors[i]);
            float currentBarWidth = (data[i] / maxVal) * chartWidth;
            float top = margin + i * barHeight + 10f;
            float right = margin + currentBarWidth;
            float bottom = margin + (i + 1) * barHeight - 10f;

            canvas.drawRect(margin, top, right, bottom, paint);

            paint.setColor(Color.DKGRAY);
            paint.setTextSize(35f);
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(String.valueOf(data[i]), right + 15f, (top + bottom) / 2 + 12f, paint);
        }
    }

    private void drawPieChart(Canvas canvas) {
        float sum = 0;
        for (float val : data) sum += val;

        int width = getWidth();
        int height = getHeight();
        float margin = 120f;
        float size = Math.min(width, height) - 2 * margin;
        RectF rectF = new RectF((width - size) / 2, (height - size) / 2, (width + size) / 2, (height + size) / 2);

        float startAngle = 0;
        for (int i = 0; i < data.length; i++) {
            paint.setColor(colors[i]);
            float sweepAngle = (data[i] / sum) * 360f;
            canvas.drawArc(rectF, startAngle, sweepAngle, true, paint);

            // Draw label
            if (data[i] > 0) {
                paint.setColor(Color.WHITE);
                paint.setTextSize(30f);
                paint.setTextAlign(Paint.Align.CENTER);
                float angle = startAngle + sweepAngle / 2;
                float radius = size / 2 * 0.7f;
                float x = (float) (rectF.centerX() + radius * Math.cos(Math.toRadians(angle)));
                float y = (float) (rectF.centerY() + radius * Math.sin(Math.toRadians(angle)));
                canvas.drawText(String.valueOf(data[i]), x, y + 10f, paint);
            }

            startAngle += sweepAngle;
        }
    }

    private float getMaxValue() {
        float maxVal = 0;
        for (float val : data) {
            if (val > maxVal) maxVal = val;
        }
        return maxVal == 0 ? 1 : maxVal;
    }
}
