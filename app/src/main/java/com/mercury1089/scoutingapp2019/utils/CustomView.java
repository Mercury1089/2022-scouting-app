package com.mercury1089.scoutingapp2019.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import com.mercury1089.scoutingapp2019.R;


public class CustomView extends View {

    private final Paint drawPaint;
    private int size;

    public CustomView (final Context context, final AttributeSet attrs) {
        super(context, attrs);
        drawPaint = new Paint();
        drawPaint.setColor(GenUtils.getAColor(getContext(), R.color.orange));
        drawPaint.setAntiAlias(true);
        setOnMeasureCallback();
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(size, size, size, drawPaint);
    }

    private void setOnMeasureCallback() {
        ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                size = getMeasuredWidth() / 2;
            }
        });
    }
}