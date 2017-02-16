package com.cui.loadingviews.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.cui.loadingviews.callback.InvalidateCallback;

/**
 * Created by CZH on 2017/2/16.
 * Base
 */
public abstract class Base {
    static final int LOAD_COMPLETE = 1;
    static final int LOAD_ERROR = 2;
    static final int LOADING = 3;
    int loadState;
    int width;
    int height;
    int contentPadding = 1;
    RectF contentRect;
    int autoLoadingDuration = 5000;
    InvalidateCallback invalidateCallback;

    public abstract void setUp(int width ,int height);

    public abstract void onDraw(Canvas canvas);

    public abstract void drawEditMode(Canvas canvas);

    public abstract void startLoading();

    public abstract void loadingComplete();

    public abstract void loadingError();

    public Paint getPaint(int color,Paint.Style style){
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(color);
        p.setStrokeWidth(3);
        if (style != null)
            p.setStyle(style);
        return p;
    }

    public void setContentPadding(int contentPadding) {
        this.contentPadding = contentPadding;
    }

    public void setInvalidateCallback(InvalidateCallback invalidateCallback) {
        this.invalidateCallback = invalidateCallback;
    }
}
