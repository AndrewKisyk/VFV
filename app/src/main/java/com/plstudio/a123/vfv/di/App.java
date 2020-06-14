package com.plstudio.a123.vfv.di;

import android.app.Application;
import android.content.Context;

import com.plstudio.a123.vfv.di.components.AppComponent;

import com.plstudio.a123.vfv.di.components.DaggerAppComponent;
import com.plstudio.a123.vfv.di.modules.AppModule;
import com.plstudio.a123.vfv.di.modules.DbModule;

public class App extends Application {

    private static AppComponent component;
    public static App getApp(Context context) {
        return (App)context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        component = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .dbModule(new DbModule())
                .build();
    }

    public static AppComponent getComponent() {
        return component;
    }

}
