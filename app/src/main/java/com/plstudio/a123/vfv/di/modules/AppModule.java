package com.plstudio.a123.vfv.di.modules;

import android.content.Context;

import com.plstudio.a123.vfv.PreferenceUtils;
import com.plstudio.a123.vfv.di.AppScope;
import com.plstudio.a123.vfv.model.User;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module
public class AppModule {

    private final Context context;

    public AppModule(Context context) {
        this.context = context;
    }


    @Provides
    @Singleton
    PreferenceUtils providesPreferenceUtils(){
        return new PreferenceUtils(context);
    }

    @Provides
    @Singleton
    User providesUser(PreferenceUtils preferenceUtils){
        return User.getUser(preferenceUtils);
    }

    @AppScope
    @Provides
    Context provideContext() {
        return context;
    }

}
