package com.arhiser.speed.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

public class SpeedView2 extends View {

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int maxValue = 120;
    private int value = 25;

    public SpeedView2(Context context) {
        super(context);
    }

    public SpeedView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SpeedView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();

        float width = getWidth();
        float height = getHeight();

        float aspect = width / height;
        final float normalAspect = 2f / 1f;
        if (aspect > normalAspect) {
            width = normalAspect * height;
        } if (aspect < normalAspect) {
            height = width / normalAspect;
        }

        canvas.scale(.5f * width, -1f * height);
        canvas.translate(1.f, -1.f);

        paint.setColor(0x40ffffff);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(0, 0, 1, paint);

        paint.setColor(0x20000000);

        canvas.drawCircle(0, 0, 0.8f, paint);

        paint.setColor(0xff88ff99);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(0.005f);

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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(widthMeasureSpec);

        float aspect = width / (float)height;
        final float normalAspect = 2f / 1f;
        if (aspect > normalAspect) {
            if (widthMode != MeasureSpec.EXACTLY) {
                width = Math.round(normalAspect * height);
            }
        } if (aspect < normalAspect) {
            if (heightMode != MeasureSpec.EXACTLY) {
                height = Math.round(width / normalAspect);
            }
        }
        setMeasuredDimension(width, height);
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        if (value > maxValue) {
            value = maxValue;
        }
        invalidate();
    }

    public void setValue(int value) {
        this.value = Math.min(value, maxValue);
        invalidate();
    }

    ObjectAnimator objectAnimator;
    public void setValueAnimated(int value) {
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
        objectAnimator = ObjectAnimator.ofInt(this, "value", this.value, value);
        objectAnimator.setDuration(100 + Math.abs(this.value - value) * 5);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int newValue = getTouchValue(event.getX(), event.getY());
                setValueAnimated(newValue);
                return true;
            case MotionEvent.ACTION_MOVE:
                return true;
            case MotionEvent.ACTION_UP:
                return true;
            default:
                return super.onTouchEvent(event);
        }

    }

    private int getTouchValue(float x, float y) {
        if (x != 0 && y != 0) {
            float startX = getWidth() / 2;
            float startY = getHeight();

            float dirX = startX - x;
            float dirY = startY - y;

            float angle = (float) Math.acos(dirX / Math.sqrt(dirX * dirX + dirY * dirY));

            return Math.round(maxValue * (angle / (float) Math.PI));
        } else {
            return value;
        }
    }


}
