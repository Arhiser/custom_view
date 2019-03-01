package com.arhiser.speed.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class SpeedView extends View {

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public SpeedView(Context context) {
        super(context);
    }

    public SpeedView(Context context,  @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SpeedView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();

        canvas.scale(.5f * getWidth(), -1f * getHeight());
        canvas.translate(1.f, -1.f);

        paint.setColor(0x40ffffff);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(0, 0, 1, paint);

        paint.setColor(0x20000000);

        canvas.drawCircle(0, 0, 0.8f, paint);

        paint.setColor(0xff88ff99);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(0.005f);

        int maxValue = 120;
        int value = 25;

        float scale = 0.9f;

        double step = Math.PI / maxValue;
        for (int i = 0; i <= maxValue; i++) {
            float x1 = (float) Math.cos(Math.PI - step*i);
            float y1 = (float) Math.sin(Math.PI - step*i);
            float x2;
            float y2;
            if (i % 20 == 0) {
                x2 = x1 * scale * 0.9f;
                y2 = y1 * scale * 0.9f;
            } else {
                x2 = x1 * scale;
                y2 = y1 * scale;
            }
            canvas.drawLine(x1, y1, x2, y2, paint);
        }

        canvas.save();

        canvas.rotate(90 - (float) 180 * (value / (float) maxValue));

        paint.setColor(0xffff8899);
        paint.setStrokeWidth(0.02f);
        canvas.drawLine(0.01f, 0, 0, 1f, paint);
        canvas.drawLine(-0.01f, 0, 0, 1f, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xff88ff99);
        canvas.drawCircle(0f, 0f, .05f, paint);

        canvas.restore();

        canvas.restore();
    }
}
