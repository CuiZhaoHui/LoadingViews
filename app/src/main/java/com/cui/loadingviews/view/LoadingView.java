package com.cui.loadingviews.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

import com.cui.loadingviews.R;
import com.cui.loadingviews.callback.InvalidateCallback;
import com.cui.loadingviews.util.LoadingViewFactory;

/**
 * Created by CZH on 2017/2/16.
 * LoadingView
 */
public class LoadingView extends View implements InvalidateCallback {

    private Base mLoadingView;
    private int contentPadding;
    private String type;

    public LoadingView(Context context, String type) {
        super(context);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingView);
        String type = typedArray.getString(R.styleable.LoadingView_type);
        int contentPadding = (int) typedArray.getDimension(R.styleable.LoadingView_contentPadding, 5);
        if (!isInEditMode()){
            mLoadingView = LoadingViewFactory.createLoadingView(type, typedArray);
            mLoadingView.setContentPadding(contentPadding);
            mLoadingView.setInvalidateCallback(this);
        }else{
            this.type = type;
            this.contentPadding = contentPadding;
        }
        typedArray.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!isInEditMode())
            mLoadingView.setUp(getMeasuredWidth(), getMeasuredHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isInEditMode())
            mLoadingView.onDraw(canvas);
        else
            EditModeDrawer.drawEditMode(type,canvas,getWidth(),getHeight(),contentPadding, Color.GRAY);
    }

    public void startLoading() {
        mLoadingView.startLoading();
    }

    public void loadingComplete() {
        mLoadingView.loadingComplete();
    }

    public void loadingFailed() {
        mLoadingView.loadingFailed();
    }

    @Override
    public void redraw() {
        invalidate();
    }
}
