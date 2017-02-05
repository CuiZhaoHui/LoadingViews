package com.cui.loadingviews.view;

import android.animation.Animator;
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
    private PathMeasure pathMeasure;
    private float[] mCurrentPosition = new float[2];
    /*圆的矩形外框*/
    private RectF ovalRect;
    /*内间距*/
    private float mPadding = 20f;
    /*执行加载动画*/
    private boolean isLoading = false;
    /*LoadingSuccess 对号*/
    private Path successSymbolPath;
    /*LoadingFailed  ×号*/
    private Path failedSymbolPath1;
    private Path failedSymbolPath2;

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
        failedSymbolPath1 = new Path();
        failedSymbolPath2 = new Path();
        pathMeasure = new PathMeasure();

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
                            if (isLoadingSuccess)
                                startSuccessSymbolAnim();
                            else
                                startFailedSymbolAnim(true);
                            return;
                        }
                    }
                }
                handler.postDelayed(loadingRunnable, 2);
                invalidate();
            }
        };
    }


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
//        mCurrentPosition[0] = ovalRect.left + sideLength * 0.24f;
//        mCurrentPosition[1] = ovalRect.top + sideLength * 0.47f;
        successSymbolPath.moveTo(ovalRect.left + sideLength * 0.24f
                , ovalRect.top + sideLength * 0.47f);

        successSymbolPath.quadTo(ovalRect.left + sideLength * 0.24f, ovalRect.top + sideLength * 0.47f
                , ovalRect.left + sideLength * 0.44f, ovalRect.top + sideLength * 0.68f);

        successSymbolPath.quadTo(ovalRect.left + sideLength * 0.44f, ovalRect.top + sideLength * 0.68f
                , ovalRect.left + sideLength * 0.73f, ovalRect.top + sideLength * 0.35f);

        //计算X号Path
        failedSymbolPath1.reset();
        failedSymbolPath2.reset();
        float padding = sideLength * 0.3f;
        failedSymbolPath1.moveTo(ovalRect.left + padding, ovalRect.top + padding);
        failedSymbolPath1.quadTo(ovalRect.left + padding, ovalRect.top + padding
                , ovalRect.right - padding, ovalRect.bottom - padding);

        failedSymbolPath2.moveTo(ovalRect.right - padding, ovalRect.top + padding);
        failedSymbolPath2.quadTo(ovalRect.right - padding, ovalRect.top + padding
                , ovalRect.left + padding, ovalRect.bottom - padding);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawArc(ovalRect, startAngle, sweepAngle, false, mPaint);
        if (!isLoading) {
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


    /**
     * 开始加载成功动画
     */
    private void startSuccessSymbolAnim() {
        currentPath.reset();
        pathMeasure.setPath(successSymbolPath, false);
        pathMeasure.getPosTan(0, mCurrentPosition, null);
        currentPath.moveTo(mCurrentPosition[0], mCurrentPosition[1]);

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, pathMeasure.getLength());
        valueAnimator.setDuration(500);
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
                pathMeasure.getPosTan(value, mCurrentPosition, null);
                currentPath.quadTo(tmp[0], tmp[1], mCurrentPosition[0], mCurrentPosition[1]);
                invalidate();
            }
        });
        valueAnimator.start();
    }

    /**
     * 开始加载失败动画
     */
    private void startFailedSymbolAnim(final boolean isFirst) {
        if (isFirst)
            currentPath.reset();
        pathMeasure.setPath(isFirst ? failedSymbolPath1 : failedSymbolPath2, false);
        pathMeasure.getPosTan(0, mCurrentPosition, null);
        currentPath.moveTo(mCurrentPosition[0], mCurrentPosition[1]);

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, pathMeasure.getLength());
        valueAnimator.setDuration(200);
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
                pathMeasure.getPosTan(value, mCurrentPosition, null);
                currentPath.quadTo(tmp[0], tmp[1], mCurrentPosition[0], mCurrentPosition[1]);
                invalidate();
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (isFirst)
                    startFailedSymbolAnim(false);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        valueAnimator.start();
    }

    public void loadingFailed() {
        //TODO 加载失败
        isLoading = false;
        isLoadingSuccess = false;
    }

    /**
     * 加载成功
     */
    public void loadingComplete() {
        isLoading = false;
        isLoadingSuccess = true;
    }


    public void setPadding(float mPadding) {
        this.mPadding = mPadding;
    }
}
