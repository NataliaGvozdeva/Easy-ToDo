package com.haraevanton.tasks09;

import android.app.Application;
import android.content.Context;

import com.haraevanton.tasks09.di.AppComponent;
import com.haraevanton.tasks09.di.DaggerAppComponent;

public class App extends Application {

    private static Context context;
    private static AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        component = DaggerAppComponent.create();
    }

    public static Context getContext() {
        return context;
    }

    public static AppComponent getComponent(){
        return component;
    }
}
