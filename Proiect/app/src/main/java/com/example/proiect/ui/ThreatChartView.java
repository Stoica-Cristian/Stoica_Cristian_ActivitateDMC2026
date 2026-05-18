package com.example.proiect.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class ThreatChartView extends View {
    private static final String TYPE_BAR = "Bar";
    private static final String TYPE_COLUMN = "Column";
    private static final String TYPE_PIE = "Piechart";

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rect = new RectF();
    private String title = "Analiza";
    private String chartType = TYPE_BAR;
    private String[] labels = new String[0];
    private int[] values = new int[0];

    public ThreatChartView(Context context) {
        super(context);
    }

    public ThreatChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setChartData(String title, String[] labels, int[] values) {
        setChartData(title, TYPE_BAR, labels, values);
    }

    public void setChartData(String title, String chartType, String[] labels, int[] values) {
        this.title = title == null ? "Analiza" : title;
        this.chartType = chartType == null ? TYPE_BAR : chartType;
        this.labels = labels == null ? new String[0] : labels;
        this.values = values == null ? new int[0] : values;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.parseColor("#F7FAFB"));

        drawTitle(canvas);

        if (values.length == 0 || totalValue() == 0) {
            paint.setColor(Color.parseColor("#526A73"));
            paint.setTextSize(sp(14));
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText("Nu exista date pentru grafic.", dp(18), dp(78), paint);
            return;
        }

        if (TYPE_PIE.equals(chartType)) {
            drawPieChart(canvas);
        } else if (TYPE_COLUMN.equals(chartType)) {
            drawColumnChart(canvas);
        } else {
            drawBarChart(canvas);
        }
    }

    private void drawTitle(Canvas canvas) {
        paint.setColor(Color.parseColor("#0E252D"));
        paint.setTextSize(sp(16));
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setFakeBoldText(true);
        canvas.drawText(fitText(title, getWidth() - dp(36)), dp(18), dp(30), paint);
        paint.setFakeBoldText(false);
    }

    private void drawBarChart(Canvas canvas) {
        int max = maxValue();
        float left = dp(18);
        float top = dp(54);
        float right = getWidth() - dp(18);
        float bottom = getHeight() - dp(18);
        float rowHeight = Math.max(dp(28), (bottom - top) / values.length);
        float maxBarWidth = Math.max(dp(90), right - left - dp(58));

        paint.setTextAlign(Paint.Align.LEFT);
        for (int i = 0; i < values.length; i++) {
            float rowTop = top + i * rowHeight;
            float barTop = rowTop + dp(16);
            float barBottom = Math.min(rowTop + rowHeight - dp(5), barTop + dp(16));
            float barWidth = values[i] * maxBarWidth / max;

            paint.setColor(Color.parseColor("#0E252D"));
            paint.setTextSize(sp(11));
            canvas.drawText(fitText(labelAt(i), maxBarWidth), left, rowTop + dp(11), paint);

            paint.setColor(colorAt(i));
            rect.set(left, barTop, left + barWidth, barBottom);
            canvas.drawRoundRect(rect, dp(4), dp(4), paint);

            paint.setColor(Color.parseColor("#0E252D"));
            paint.setTextSize(sp(12));
            canvas.drawText(String.valueOf(values[i]), left + barWidth + dp(8), barBottom, paint);
        }
    }

    private void drawColumnChart(Canvas canvas) {
        int max = maxValue();
        float left = dp(22);
        float top = dp(58);
        float right = getWidth() - dp(22);
        float bottom = getHeight() - dp(52);
        float chartHeight = Math.max(dp(60), bottom - top);
        float slotWidth = Math.max(dp(24), (right - left) / values.length);
        float barWidth = Math.min(dp(36), slotWidth * 0.58f);

        paint.setTextAlign(Paint.Align.CENTER);
        for (int i = 0; i < values.length; i++) {
            float centerX = left + slotWidth * i + slotWidth / 2f;
            float barHeight = values[i] * chartHeight / max;
            float barLeft = centerX - barWidth / 2f;
            float barTop = bottom - barHeight;
            float barRight = centerX + barWidth / 2f;

            paint.setColor(colorAt(i));
            rect.set(barLeft, barTop, barRight, bottom);
            canvas.drawRoundRect(rect, dp(4), dp(4), paint);

            paint.setColor(Color.parseColor("#0E252D"));
            paint.setTextSize(sp(11));
            canvas.drawText(String.valueOf(values[i]), centerX, Math.max(dp(50), barTop - dp(6)), paint);

            paint.setColor(Color.parseColor("#526A73"));
            paint.setTextSize(sp(9));
            canvas.drawText(fitText(labelAt(i), slotWidth - dp(4)), centerX, bottom + dp(18), paint);
        }
    }

    private void drawPieChart(Canvas canvas) {
        int total = totalValue();
        float top = dp(56);
        float left = dp(18);
        float availableHeight = getHeight() - top - dp(18);
        float pieSize = Math.min(dp(132), Math.min(availableHeight, getWidth() * 0.38f));
        rect.set(left, top, left + pieSize, top + pieSize);

        float startAngle = -90f;
        for (int i = 0; i < values.length; i++) {
            if (values[i] <= 0) {
                continue;
            }
            float sweepAngle = values[i] * 360f / total;
            paint.setColor(colorAt(i));
            canvas.drawArc(rect, startAngle, sweepAngle, true, paint);
            startAngle += sweepAngle;
        }

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(2));
        paint.setColor(Color.WHITE);
        canvas.drawOval(rect, paint);
        paint.setStyle(Paint.Style.FILL);

        drawLegend(canvas, rect.right + dp(18), top + dp(12), getWidth() - rect.right - dp(36), total);
    }

    private void drawLegend(Canvas canvas, float x, float startY, float maxWidth, int total) {
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(sp(11));
        for (int i = 0; i < values.length; i++) {
            float y = startY + i * dp(25);
            int percent = Math.round(values[i] * 100f / total);

            paint.setColor(colorAt(i));
            rect.set(x, y - dp(10), x + dp(13), y + dp(3));
            canvas.drawRoundRect(rect, dp(3), dp(3), paint);

            paint.setColor(Color.parseColor("#0E252D"));
            String text = labelAt(i) + ": " + values[i] + " (" + percent + "%)";
            canvas.drawText(fitText(text, maxWidth - dp(20)), x + dp(20), y, paint);
        }
    }

    private int maxValue() {
        int max = 1;
        for (int value : values) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    private int totalValue() {
        int total = 0;
        for (int value : values) {
            total += Math.max(0, value);
        }
        return total;
    }

    private String labelAt(int index) {
        return labels.length > index ? labels[index] : "Serie " + (index + 1);
    }

    private int colorAt(int index) {
        return ChartColors.forLabel(title, labelAt(index), index);
    }

    private String fitText(String value, float maxWidth) {
        if (maxWidth <= 0 || paint.measureText(value) <= maxWidth) {
            return value;
        }
        String suffix = "...";
        int end = value.length();
        while (end > 0 && paint.measureText(value.substring(0, end) + suffix) > maxWidth) {
            end--;
        }
        return end == 0 ? suffix : value.substring(0, end).trim() + suffix;
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }

    private float sp(float value) {
        return value * getResources().getDisplayMetrics().scaledDensity;
    }
}
