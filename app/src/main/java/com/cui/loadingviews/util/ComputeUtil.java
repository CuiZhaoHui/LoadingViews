package com.cui.loadingviews.util;

import android.graphics.Path;
import android.graphics.RectF;

/**
 * Created by CZH on 2017/2/20.
 * ComputeUtil
 */

public class ComputeUtil {

    /**
     * Compute contentRect and sideLength
     * */
    public static int ComputeSize(RectF contentRect, int width, int height, int padding) {
        int sideLength;//矩形边长
        if (width < height) {
            sideLength = width - padding * 2;
            contentRect.left = padding;
            contentRect.top = (height - sideLength) / 2;
        } else if (width > height) {
            sideLength = height - padding * 2;
            contentRect.left = (width - sideLength) / 2;
            contentRect.top = padding;
        } else {
            sideLength = width - padding * 2;
            contentRect.left = padding;
            contentRect.top = padding;
        }
        contentRect.right = sideLength + contentRect.left;
        contentRect.bottom = contentRect.top + sideLength;
        return sideLength;
    }

    public static void computeCircleSucSymbolPath(RectF contentRect,int sideLength,Path successSymbolPath){
        successSymbolPath.moveTo(contentRect.left + sideLength * 0.24f
                , contentRect.top + sideLength * 0.47f);

        successSymbolPath.quadTo(contentRect.left + sideLength * 0.24f, contentRect.top + sideLength * 0.47f
                , contentRect.left + sideLength * 0.44f, contentRect.top + sideLength * 0.68f);

        successSymbolPath.quadTo(contentRect.left + sideLength * 0.44f, contentRect.top + sideLength * 0.68f
                , contentRect.left + sideLength * 0.73f, contentRect.top + sideLength * 0.35f);
    }

    public static void computeCircleFailedSymbolPath(RectF contentRect,int sideLength,Path failedSymbolPath1,Path failedSymbolPath2){
        float padding = sideLength * 0.3f;
        failedSymbolPath1.moveTo(contentRect.left + padding, contentRect.top + padding);
        failedSymbolPath1.quadTo(contentRect.left + padding, contentRect.top + padding
                , contentRect.right - padding, contentRect.bottom - padding);

        failedSymbolPath2.moveTo(contentRect.right - padding, contentRect.top + padding);
        failedSymbolPath2.quadTo(contentRect.right - padding, contentRect.top + padding
                , contentRect.left + padding, contentRect.bottom - padding);
    }

}
