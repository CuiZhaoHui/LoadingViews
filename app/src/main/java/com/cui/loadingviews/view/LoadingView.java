package com.cui.loadingviews.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
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
        if (isInEditMode())
            return;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingView);
        String type = typedArray.getString(R.styleable.LoadingView_type);
        int contentPadding = typedArray.getInt(R.styleable.LoadingView_contentPadding, 5);
        mLoadingView = LoadingViewFactory.createLoadingView(type, typedArray);
        mLoadingView.setContentPadding(contentPadding);
        mLoadingView.setInvalidateCallback(this);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
    }

    public void startLoading() {
        mLoadingView.startLoading();
    }

    public void loadingComplete() {
        mLoadingView.loadingComplete();
    }

    public void loadingError() {
        mLoadingView.loadingError();
    }

    @Override
    public void redraw() {
        invalidate();
    }
}
