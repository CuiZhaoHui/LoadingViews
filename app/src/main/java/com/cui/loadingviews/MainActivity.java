package com.cui.loadingviews;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.cui.loadingviews.view.LoadingView;
import com.cui.loadingviews.view.ZFBLoadingView;

public class MainActivity extends Activity {

    private ZFBLoadingView view_main;
    LoadingView loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view_main = (ZFBLoadingView) findViewById(R.id.view_main);
        loadingView = (LoadingView) findViewById(R.id.loadingView_main);
    }

    public void startLoading(View v){
        loadingView.startLoading();
        view_main.startLoading();
//        view_main.startSymbolAnim();
    }

    public void loadingSuccess(View v){
        view_main.loadingComplete();
    }

    public void loadingFailed(View v){
        view_main.loadingFailed();
        loadingView.loadingFailed();
    }
}
