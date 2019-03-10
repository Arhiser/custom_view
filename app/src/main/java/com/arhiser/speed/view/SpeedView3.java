package com.arhiser.speed.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.arhiser.speed.R;

public class SpeedView3 extends View {

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Typeface typeface;

    private int maxValue = 120;
    private int value = 25;
    private String text = "km/h";
    private int color = 0xff98ffb0;
    private int textColor = 0xff90a0ff;
    private int markRange = 10;

    public SpeedView3(Context context) {
        super(context);
        init(context, null);
    }

    public SpeedView3(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SpeedView3(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SpeedView3);
            CharSequence chars = a.getText(R.styleable.SpeedView3_android_text);
            text = chars != null ? chars.toString() : "km/h";

            maxValue = a.getInt(R.styleable.SpeedView3_maxValue, 120);
            value = a.getInt(R.styleable.SpeedView3_value, 25);
            markRange = a.getInt(R.styleable.SpeedView3_markRange, 10);
            color = a.getColor(R.styleable.SpeedView3_color, 0xff98ffb0);
            textColor = a.getColor(R.styleable.SpeedView3_textColor, 0xff90a0ff);
            chars = a.getText(R.styleable.SpeedView3_fontName);
            if (chars != null) {
                typeface = Typeface.createFromAsset(context.getAssets(), chars.toString());
                paint.setTypeface(typeface);
            }

            a.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();

        float aspect = width / height;
        final float normalAspect = 2f / 1f;
        if (aspect > normalAspect) {
            width = normalAspect * height;
        } if (aspect < normalAspect) {
            height = width / normalAspect;
        }

        canvas.save();

        canvas.translate(width / 2, height);
        canvas.scale(.5f * width, -1f * height);


        paint.setColor(0x40ffffff);
        paint.setStyle(Paint.Style.FILL);
        paint.setTypeface(typeface);

        canvas.drawCircle(0, 0, 1, paint);

        paint.setColor(0x20000000);

        canvas.drawCircle(0, 0, 0.8f, paint);

        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(0.005f);

        float scale = 0.9f;
        float longScale = 0.9f;
        float textPadding = 0.85f;

        double step = Math.PI / maxValue;
        for (int i = 0; i <= maxValue; i++) {
            float x1 = (float) Math.cos(Math.PI - step*i);
            float y1 = (float) Math.sin(Math.PI - step*i);
            float x2;
            float y2;
            if (i % markRange == 0) {
                x2 = x1 * scale * longScale;
                y2 = y1 * scale * longScale;
            } else {
                x2 = x1 * scale;
                y2 = y1 * scale;
            }
            canvas.drawLine(x1, y1, x2, y2, paint);
        }

        canvas.restore();

        canvas.save();

        canvas.translate(width / 2, 0);

        paint.setTextSize(height / 10);
        paint.setColor(textColor);
        paint.setStyle(Paint.Style.FILL);

        float factor = height * scale * longScale * textPadding;

        for (int i = 0; i <= maxValue; i+= markRange) {
            float x = (float) Math.cos(Math.PI - step*i) * factor;
            float y = (float) Math.sin(Math.PI - step*i) * factor;
            String text = Integer.toString(i);
            int textLen = Math.round(paint.measureText(text));
            canvas.drawText(Integer.toString(i), x - textLen / 2, height - y, paint);
        }

        canvas.drawText(text, -paint.measureText(text) /2 , height - height * 0.15f, paint);

        canvas.restore();

        canvas.save();

        canvas.translate(width / 2, height);
        canvas.scale(.5f * width, -1f * height);
        canvas.rotate(90 - (float) 180 * (value / (float) maxValue));

        paint.setColor(0xffff8899);
        paint.setStrokeWidth(0.02f);
        canvas.drawLine(0.01f, 0, 0, 1f, paint);
        canvas.drawLine(-0.01f, 0, 0, 1f, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xff88ff99);
        canvas.drawCircle(0f, 0f, .05f, paint);

        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

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

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parentState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(parentState);
        savedState.value = value;
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        setValue(savedState.value);
    }

    private static class SavedState extends BaseSavedState {

        int value;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            value = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(value);
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
