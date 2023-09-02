package com.plstudio.a123.vfv.di.modules;

import android.content.Context;

import com.plstudio.a123.vfv.helpers.RequirementsBaseHelper;
import com.plstudio.a123.vfv.helpers.RequirementsLab;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = AppModule.class)
public class DbModule{
    @Provides
    RequirementsBaseHelper providesDBHelper(Context context){
        return new RequirementsBaseHelper(context);}
    @Provides
    @Singleton
    RequirementsLab providesRequirementsLab(Context context){
        return RequirementsLab.get(context);
    }

}
