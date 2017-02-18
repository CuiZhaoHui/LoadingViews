package com.cui.loadingviews.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;

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
        pathMeasure = new PathMeasure();
        contentRect = new RectF();
        currentPath = new Path();
        int sideLength;//矩形边长
        if (width < height) {
            sideLength = width - contentPadding * 2;
            contentRect.left = contentPadding;
            contentRect.top = (height - sideLength) / 2;
        } else if (width > height) {
            sideLength = height - contentPadding * 2;
            contentRect.left = (width - sideLength) / 2;
            contentRect.top = contentPadding;
        } else {
            sideLength = width - contentPadding * 2;
            contentRect.left = contentPadding;
            contentRect.top = contentPadding;
        }
        contentRect.right = sideLength + contentRect.left;
        contentRect.bottom = contentRect.top + sideLength;

        //计算对号的Path
        successSymbolPath.reset();

        successSymbolPath.moveTo(contentRect.left + sideLength * 0.24f
                , contentRect.top + sideLength * 0.47f);

        successSymbolPath.quadTo(contentRect.left + sideLength * 0.24f, contentRect.top + sideLength * 0.47f
                , contentRect.left + sideLength * 0.44f, contentRect.top + sideLength * 0.68f);

        successSymbolPath.quadTo(contentRect.left + sideLength * 0.44f, contentRect.top + sideLength * 0.68f
                , contentRect.left + sideLength * 0.73f, contentRect.top + sideLength * 0.35f);

        Paint.FontMetricsInt fontMetrics = numberPaint.getFontMetricsInt();
        baseLineY = (height - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;

        //计算X号Path
        failedSymbolPath1.reset();
        failedSymbolPath2.reset();
        float padding = sideLength * 0.3f;
        failedSymbolPath1.moveTo(contentRect.left + padding, contentRect.top + padding);
        failedSymbolPath1.quadTo(contentRect.left + padding, contentRect.top + padding
                , contentRect.right - padding, contentRect.bottom - padding);

        failedSymbolPath2.moveTo(contentRect.right - padding, contentRect.top + padding);
        failedSymbolPath2.quadTo(contentRect.right - padding, contentRect.top + padding
                , contentRect.left + padding, contentRect.bottom - padding);
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
