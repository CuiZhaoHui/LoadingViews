package com.cui.loadingviews.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by CZH on 2017/2/4.
 * 仿支付宝 支付时Loading
 */

public class ZFBLoadingView extends View {

    private Paint mPaint;
    private Path currentPath;
    private PathMeasure successPathMeasure;
    private float[] mCurrentPosition = new float[2];
    /*圆的矩形外框*/
    private RectF ovalRect;
    /*内间距*/
    private float mPadding = 20f;
    /*执行加载动画*/
    private boolean isLoading = false;
    private boolean isLoadingSuccess;
    private float startAngle = 0;
    private float sweepAngle = 0;

    private Handler handler = new Handler();

    public ZFBLoadingView(Context context) {
        super(context);
        init();
    }

    public ZFBLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ZFBLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private Runnable loadingRunnable;
    /*是否是擦除*/
    private boolean isErase = false;

    private void init() {
        ovalRect = new RectF();
        currentPath = new Path();
        successSymbolPath = new Path();
        successPathMeasure = new PathMeasure();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(5);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);

        loadingRunnable = new Runnable() {
            @Override
            public void run() {
                startAngle += 2;
                sweepAngle += 4f;
                if (isErase) {
                    if (sweepAngle >= 0) {
                        isErase = false;
                    }
                } else {
                    isErase = sweepAngle > 0 && sweepAngle % 360 == 0;
                    if (isErase) {
                        sweepAngle = -sweepAngle;
                        invalidate();
                        if (!isLoading) {
                            startSymbolAnim();
                            return;
                        }
                    }
                }
                handler.postDelayed(loadingRunnable, 2);
                invalidate();
            }
        };
    }

    private Path successSymbolPath;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        float sideLength;//矩形边长
        if (width < height) {
            sideLength = width - mPadding * 2;
            ovalRect.left = mPadding;
            ovalRect.top = (height - sideLength) / 2;
        } else if (width > height) {
            sideLength = height - mPadding * 2;
            ovalRect.left = (width - sideLength) / 2;
            ovalRect.top = mPadding;
        } else {
            sideLength = width;
            ovalRect.left = mPadding;
            ovalRect.top = mPadding;
        }

        ovalRect.right = sideLength + ovalRect.left;
        ovalRect.bottom = ovalRect.top + sideLength;

        //计算对号的Path
        successSymbolPath.reset();
        mCurrentPosition[0] = ovalRect.left + sideLength * 0.24f;
        mCurrentPosition[1] = ovalRect.top + sideLength * 0.47f;
        successSymbolPath.moveTo(ovalRect.left + sideLength * 0.24f
                , ovalRect.top + sideLength * 0.47f);

        successSymbolPath.quadTo(ovalRect.left + sideLength * 0.24f, ovalRect.top + sideLength * 0.47f
                , ovalRect.left + sideLength * 0.44f, ovalRect.top + sideLength * 0.68f);

        successSymbolPath.quadTo(ovalRect.left + sideLength * 0.44f, ovalRect.top + sideLength * 0.68f
                , ovalRect.left + sideLength * 0.73f, ovalRect.top + sideLength * 0.35f);

        successPathMeasure.setPath(successSymbolPath,false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawArc(ovalRect, startAngle, sweepAngle, false, mPaint);
        if (!isLoading){
            canvas.drawPath(currentPath, mPaint);
        }
    }

    public void startLoading() {
        if (isLoading)
            return;
        startAngle = 0;
        sweepAngle = 0;
        currentPath.reset();

        isLoading = true;
        handler.postDelayed(loadingRunnable, 5);
    }


    //开启结束动画
    public void startSymbolAnim() {
        currentPath.reset();
        if (isLoadingSuccess){
            successPathMeasure.getPosTan(0, mCurrentPosition, null);
            currentPath.moveTo(mCurrentPosition[0],mCurrentPosition[1]);
        }
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, successPathMeasure.getLength());
        valueAnimator.setDuration(1000);
        // 减速插值器
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (isLoading)
                    return;
                float value = (Float) animation.getAnimatedValue();
                // 获取当前点坐标封装到mCurrentPosition
                float[] tmp = mCurrentPosition;
                successPathMeasure.getPosTan(value, mCurrentPosition, null);
                currentPath.quadTo(tmp[0],tmp[1],mCurrentPosition[0],mCurrentPosition[1]);
                invalidate();
            }
        });
        valueAnimator.start();
    }

    public void loadingFailed(){
        //TODO 加载失败
        isLoading = false;
        isLoadingSuccess = false;
    }

    /**
     * 加载成功
     * */
    public void loadingComplete(){
        isLoading = false;
        isLoadingSuccess = true;
    }


    public void setPadding(float mPadding) {
        this.mPadding = mPadding;
    }
}
