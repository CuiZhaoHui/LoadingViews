package com.cui.loadingviews.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

/**
 * Created by CZH on 2017/2/18.
 * EditModeDrawer
 */

public class EditModeDrawer {


    public static void drawEditMode(String type,Canvas canvas
            , int width, int height, int contentPadding, int lineColor){
        if (type == null)
            throw new RuntimeException("No type found!");
        switch (type) {
            case Base.ChaseLoading:
                drawCircleEditMode(canvas,width,height,contentPadding,lineColor);
                break;
            case Base.NumberLoading:
                drawCircleEditMode(canvas,width,height,contentPadding,lineColor);
                break;
            default:
                throw new RuntimeException("No type found!");
        }
    }

    private static void drawCircleEditMode(Canvas canvas, int width, int height, int contentPadding, int lineColor) {
        RectF contentRect = new RectF();
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
        Path successSymbolPath = new Path();
        //计算对号的Path
        successSymbolPath.reset();

        successSymbolPath.moveTo(contentRect.left + sideLength * 0.24f
                , contentRect.top + sideLength * 0.47f);

        successSymbolPath.quadTo(contentRect.left + sideLength * 0.24f, contentRect.top + sideLength * 0.47f
                , contentRect.left + sideLength * 0.44f, contentRect.top + sideLength * 0.68f);

        successSymbolPath.quadTo(contentRect.left + sideLength * 0.44f, contentRect.top + sideLength * 0.68f
                , contentRect.left + sideLength * 0.73f, contentRect.top + sideLength * 0.35f);

        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(lineColor);
        p.setStrokeWidth(3);
        p.setStyle(Paint.Style.STROKE);

        canvas.drawArc(contentRect, 0, 360, false, p);
        canvas.drawPath(successSymbolPath, p);

    }

}
