package com.cui.loadingviews.view;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

/**
 * Created by CZH on 2017/2/16.
 * NumberLoading
 */
public class NumberLoading extends Base {

    private Paint linePaint;
    private Paint numberPaint;
    private Path successSymbolPath;
    private int progress = 0;
    private int baseLineY;

    public NumberLoading(int lineColor, int numberColor, int textSize) {
        linePaint = getPaint(lineColor, Paint.Style.STROKE);
        numberPaint = getPaint(numberColor, Paint.Style.STROKE);
        numberPaint.setTextSize(textSize);
        numberPaint.setTextAlign(Paint.Align.CENTER);
        successSymbolPath = new Path();
    }

    @Override
    public void setUp(int width, int height) {
        contentRect = new RectF();
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
    }


    @Override
    public void onDraw(Canvas canvas) {
        int sweepAngle = (int) (360 * (progress / 100f));
        canvas.drawArc(contentRect, 0, sweepAngle, false, linePaint);
        canvas.drawText(String.valueOf(progress).concat("%"), contentRect.centerX(), baseLineY, numberPaint);
    }

    @Override
    public void drawEditMode(Canvas canvas) {
        canvas.drawArc(contentRect, 0, 360, false, linePaint);
        canvas.drawPath(successSymbolPath, linePaint);
    }

    @Override
    public void startLoading() {
        loadState = LOADING;
        startAutoLoading();
    }

    @Override
    public void loadingComplete() {
        loadState = LOAD_COMPLETE;
        //TODO completeAnim
    }

    @Override
    public void loadingError() {
        loadState = LOAD_ERROR;
        //TODO errorAnim
    }

    private void startAutoLoading() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100);
        valueAnimator.setDuration(autoLoadingDuration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int tmp = (int) animation.getAnimatedValue();
                if (tmp == progress)
                    return;
                progress = tmp;
                invalidateCallback.redraw();
            }
        });
        valueAnimator.start();
    }

}
