package com.mercury1089.scoutingapp2019;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import java.io.InputStream;

public class LoadingGIFVIew extends View {

    private InputStream gifInputStream;
    private Movie gifMovie;
    private int movieWidth, movieHeight;
    private long movieDuration;
    private long movieStart;

    public LoadingGIFVIew(Context context) {
        super(context);
        init(context);
    }
    public LoadingGIFVIew(Context context, AttributeSet attributeSet) {
        super(context);
        init(context);
    }
    public LoadingGIFVIew(Context context, AttributeSet attributeSet, int defStyleAttribute) {
        super(context, attributeSet, defStyleAttribute);
    }
    private void init(Context context) {
        setFocusable(true);
        gifInputStream = context.getResources().openRawResource(R.raw.loading_gif);
        gifMovie = Movie.decodeStream(gifInputStream);
        movieWidth = gifMovie.width();
        movieHeight = gifMovie.height();
        movieDuration = gifMovie.duration();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(movieWidth, movieHeight);
    }

    public int getMovieWidth() {
        return movieWidth;
    }

    public int getMovieHeight() {
        return movieHeight;
    }

    public long getMovieDuration() {
        return movieDuration;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        long now = SystemClock.uptimeMillis();

        if(movieStart == 0) {
            movieStart = now;
        }

        if (gifMovie != null) {
            int duration = gifMovie.duration();
            if (duration == 0) {
                duration = 1000;
            }

            int relTime = (int)((now - movieStart) % duration);

            gifMovie.setTime(relTime);
            gifMovie.draw(canvas, 0, 0);
            invalidate();
        }
    }
}
