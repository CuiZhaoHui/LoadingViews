package com.cui.loadingviews.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Handler;
import android.view.animation.DecelerateInterpolator;

import com.cui.loadingviews.util.ComputeUtil;

/**
 * Created by CZH on 2017/2/16.
 * ChaseLoading
 */

public class ChaseLoading extends Base {

    private Paint mPaint;
    private Path currentPath;
    private PathMeasure pathMeasure;
    private float[] mCurrentPosition = new float[2];
    /*圆的矩形外框*/
    private RectF contentRect;
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
    private Runnable loadingRunnable;
    private Handler handler = new Handler();
    /*是否是擦除*/
    private boolean isErase = false;

    @Override
    public void setUp(int width, int height) {
        this.width = width;
        this.height = height;
        contentRect = new RectF();
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
                        invalidateCallback.redraw();
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
                invalidateCallback.redraw();
            }
        };

        //矩形边长
        int sideLength = ComputeUtil.ComputeSize(contentRect, width, height, contentPadding);

        //计算对号的Path
        successSymbolPath.reset();
        ComputeUtil.computeCircleSucSymbolPath(contentRect, sideLength, successSymbolPath);

        //计算X号Path
        failedSymbolPath1.reset();
        failedSymbolPath2.reset();

        ComputeUtil.computeCircleFailedSymbolPath(contentRect,sideLength,failedSymbolPath1,failedSymbolPath2);
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawArc(contentRect, startAngle, sweepAngle, false, mPaint);
        if (!isLoading) {
            canvas.drawPath(currentPath, mPaint);
        }
    }

    @Override
    public void startLoading() {
        if (isLoading)
            return;
        startAngle = 0;
        sweepAngle = 0;
        currentPath.reset();

        isLoading = true;
        handler.postDelayed(loadingRunnable, 5);
    }

    @Override
    public void loadingComplete() {
        isLoading = false;
        isLoadingSuccess = true;
    }

    @Override
    public void loadingFailed() {
        //TODO 加载失败
        isLoading = false;
        isLoadingSuccess = false;
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
                invalidateCallback.redraw();
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
                invalidateCallback.redraw();
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

}
