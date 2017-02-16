package com.cui.loadingviews.util;

import android.content.res.TypedArray;
import android.graphics.Color;

import com.cui.loadingviews.R;
import com.cui.loadingviews.view.Base;
import com.cui.loadingviews.view.ChaseLoading;
import com.cui.loadingviews.view.NumberLoading;

/**
 * Created by CZH on 2017/2/16.
 * LoadingViewFactory
 */

public class LoadingViewFactory {

    private static final String ChaseLoading = "ChaseLoading";
    private static final String NumberLoading = "NumberLoading";

    public static Base createLoadingView(String type, TypedArray typedArray) {
        if (typedArray == null)
            return new NumberLoading(Color.GRAY, Color.GRAY, 35);
        switch (type) {
            case ChaseLoading:
                return new ChaseLoading();
            case NumberLoading:
                int lineColor = typedArray.getColor(R.styleable.LoadingView_lineColor, Color.GRAY);
                int numberColor = typedArray.getColor(R.styleable.LoadingView_lineColor, Color.GRAY);
                int textSize = typedArray.getDimensionPixelSize(R.styleable.LoadingView_textSize, 35);
                return new NumberLoading(lineColor, numberColor, textSize);
            default:
                throw new RuntimeException("No type found!");
        }
    }


}
