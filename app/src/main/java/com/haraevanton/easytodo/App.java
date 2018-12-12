package com.haraevanton.easytodo;

import android.app.Application;
import android.content.Context;

import com.haraevanton.easytodo.di.AppComponent;
import com.haraevanton.easytodo.di.DaggerAppComponent;

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

    public static AppComponent getComponent() {
        return component;
    }
}
