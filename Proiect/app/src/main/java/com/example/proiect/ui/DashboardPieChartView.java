package com.example.proiect.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class DashboardPieChartView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF pieBounds = new RectF();
    private String title = "Distributie";
    private String[] labels = new String[0];
    private int[] values = new int[0];

    public DashboardPieChartView(Context context) {
        super(context);
    }

    public DashboardPieChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setChartData(String title, String[] labels, int[] values) {
        this.title = title == null ? "Distributie" : title;
        this.labels = labels == null ? new String[0] : labels;
        this.values = values == null ? new int[0] : values;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float density = getResources().getDisplayMetrics().density;
        float scaledDensity = getResources().getDisplayMetrics().scaledDensity;
        float left = getPaddingLeft() + 16f * density;
        float top = getPaddingTop() + 14f * density;
        float right = getWidth() - getPaddingRight() - 16f * density;
        float bottom = getHeight() - getPaddingBottom() - 14f * density;

        paint.setColor(Color.parseColor("#0E252D"));
        paint.setTextSize(16f * scaledDensity);
        paint.setFakeBoldText(true);
        canvas.drawText(title, left, top + 18f * scaledDensity, paint);
        paint.setFakeBoldText(false);

        int total = 0;
        for (int value : values) {
            total += Math.max(0, value);
        }
        if (total == 0) {
            paint.setColor(Color.parseColor("#526A73"));
            paint.setTextSize(14f * scaledDensity);
            canvas.drawText("Nu exista date.", left, top + 54f * density, paint);
            return;
        }

        float chartTop = top + 40f * density;
        float availableHeight = Math.max(110f * density, bottom - chartTop);
        float pieSize = Math.min(132f * density, Math.min(availableHeight, (right - left) * 0.42f));
        pieBounds.set(left, chartTop, left + pieSize, chartTop + pieSize);

        float startAngle = -90f;
        for (int i = 0; i < values.length; i++) {
            int value = Math.max(0, values[i]);
            if (value == 0) {
                continue;
            }
            float sweep = value * 360f / total;
            paint.setColor(colorAt(i));
            canvas.drawArc(pieBounds, startAngle, sweep, true, paint);
            startAngle += sweep;
        }

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2f * density);
        paint.setColor(Color.WHITE);
        canvas.drawOval(pieBounds, paint);
        paint.setStyle(Paint.Style.FILL);

        float legendX = pieBounds.right + 20f * density;
        float legendY = chartTop + 18f * density;
        float legendWidth = Math.max(80f * density, right - legendX);
        paint.setTextSize(13f * scaledDensity);
        for (int i = 0; i < values.length; i++) {
            int value = Math.max(0, values[i]);
            String label = labels.length > i ? labels[i] : "Serie " + (i + 1);
            int percent = Math.round(value * 100f / total);
            float y = legendY + i * 28f * density;

            paint.setColor(colorAt(i));
            canvas.drawRoundRect(legendX, y - 11f * density, legendX + 14f * density, y + 3f * density, 4f * density, 4f * density, paint);

            paint.setColor(Color.parseColor("#0E252D"));
            String legendText = label + ": " + value + " (" + percent + "%)";
            canvas.drawText(fitText(legendText, legendWidth - 22f * density), legendX + 22f * density, y, paint);
        }
    }

    private String fitText(String value, float maxWidth) {
        if (paint.measureText(value) <= maxWidth) {
            return value;
        }
        String suffix = "...";
        int end = value.length();
        while (end > 0 && paint.measureText(value.substring(0, end) + suffix) > maxWidth) {
            end--;
        }
        return end == 0 ? suffix : value.substring(0, end).trim() + suffix;
    }

    private int colorAt(int index) {
        String label = labels.length > index ? labels[index] : "Serie " + (index + 1);
        return ChartColors.forLabel(title, label, index);
    }
}
