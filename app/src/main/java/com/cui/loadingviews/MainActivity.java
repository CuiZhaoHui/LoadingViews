package com.cui.loadingviews;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.cui.loadingviews.view.LoadingView;

public class MainActivity extends Activity {

    LoadingView chaseLoading;
    LoadingView loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chaseLoading = (LoadingView) findViewById(R.id.view_main);
        loadingView = (LoadingView) findViewById(R.id.loadingView_main);
    }

    public void startLoading(View v){
        loadingView.startLoading();
        chaseLoading.startLoading();
    }

    public void loadingSuccess(View v){
        chaseLoading.loadingComplete();
        loadingView.loadingComplete();
    }

    public void loadingFailed(View v){
        chaseLoading.loadingFailed();
        loadingView.loadingFailed();
    }
}
