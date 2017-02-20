package com.cui.loadingviews.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;

import com.cui.loadingviews.util.ComputeUtil;

/**
 * Created by CZH on 2017/2/16.
 * NumberLoading
 */
public class NumberLoading extends Base {
    private Path currentPath;
    private PathMeasure pathMeasure;
    private Paint linePaint;
    private Paint numberPaint;
    private Path successSymbolPath;
    private Path failedSymbolPath1;
    private Path failedSymbolPath2;

    private int progress = 0;
    private int baseLineY;

    public NumberLoading(int lineColor, int numberColor, int textSize) {
        linePaint = getPaint(lineColor, Paint.Style.STROKE);
        numberPaint = getPaint(numberColor, Paint.Style.STROKE);
        numberPaint.setTextSize(textSize);
        numberPaint.setTextAlign(Paint.Align.CENTER);
        successSymbolPath = new Path();
        failedSymbolPath1 = new Path();
        failedSymbolPath2 = new Path();
    }

    @Override
    public void setUp(int width, int height) {
        if (pathMeasure == null)
            pathMeasure = new PathMeasure();
        if (contentRect == null)
            contentRect = new RectF();
        if (currentPath == null)
            currentPath = new Path();

        //矩形边长
        int sideLength = ComputeUtil.ComputeSize(contentRect, width, height, contentPadding);

        //计算对号的Path
        successSymbolPath.reset();
        ComputeUtil.computeCircleSucSymbolPath(contentRect, sideLength, successSymbolPath);

        //计算X号Path
        failedSymbolPath1.reset();
        failedSymbolPath2.reset();

        ComputeUtil.computeCircleFailedSymbolPath(contentRect,sideLength,failedSymbolPath1,failedSymbolPath2);

        Paint.FontMetricsInt fontMetrics = numberPaint.getFontMetricsInt();
        baseLineY = (height - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
    }


    @Override
    public void onDraw(Canvas canvas) {
        int sweepAngle = (int) (360 * (progress / 100f));
        canvas.drawArc(contentRect, 0, sweepAngle, false, linePaint);
        if (loadState == LOADING)
            canvas.drawText(String.valueOf(progress).concat("%"), contentRect.centerX(), baseLineY, numberPaint);
        else
            canvas.drawPath(currentPath, linePaint);
    }

    @Override
    public void startLoading() {
        loadState = LOADING;
        progress = 0;
        startAutoLoading();
        currentPath.reset();
    }

    @Override
    public void loadingComplete() {
        currentPath.reset();
        final float[] currentPosition = new float[2];
        loadState = LOAD_COMPLETE;
        pathMeasure.setPath(successSymbolPath, false);
        pathMeasure.getPosTan(0, currentPosition, null);
        currentPath.moveTo(currentPosition[0], currentPosition[1]);

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, pathMeasure.getLength());
        valueAnimator.setDuration(500);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                // 获取当前点坐标封装到currentPosition
                float[] tmp = new float[2];
                tmp[0] = currentPosition[0];
                tmp[1] = currentPosition[1];
                pathMeasure.getPosTan(value, currentPosition, null);
                currentPath.quadTo(tmp[0], tmp[1], currentPosition[0], currentPosition[1]);
                invalidateCallback.redraw();
            }
        });

        progress = 100;
        valueAnimator.start();
    }

    @Override
    public void loadingFailed() {
        currentPath.reset();
        loadState = LOAD_ERROR;
        if (loadAnim != null && loadAnim.isRunning())
            loadAnim.cancel();
        startFailedAnim(true);
        progress = 100;
    }

    private void startFailedAnim(final boolean isFirstLine) {
        final float[] currentPosition = new float[2];
        pathMeasure.setPath(isFirstLine ? failedSymbolPath1 : failedSymbolPath2, false);
        pathMeasure.getPosTan(0, currentPosition, null);
        currentPath.moveTo(currentPosition[0], currentPosition[1]);

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, pathMeasure.getLength());
        valueAnimator.setDuration(200);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                // 获取当前点坐标封装到currentPosition
                float[] tmp = new float[2];
                tmp[0] = currentPosition[0];
                tmp[1] = currentPosition[1];
                pathMeasure.getPosTan(value, currentPosition, null);
                currentPath.quadTo(tmp[0], tmp[1], currentPosition[0], currentPosition[1]);
                invalidateCallback.redraw();
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (isFirstLine)
                    startFailedAnim(false);
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

    private ValueAnimator loadAnim;

    private void startAutoLoading() {
        loadAnim = ValueAnimator.ofInt(0, 100);
        loadAnim.setDuration(autoLoadingDuration);
        loadAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (loadState == LOADING) {
                    int tmp = (int) animation.getAnimatedValue();
                    if (tmp == progress)
                        return;
                    progress = tmp;
                    invalidateCallback.redraw();
                }
            }
        });
        loadAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (loadState != LOADING)
                    return;
                loadingComplete();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        loadAnim.start();
    }

}
