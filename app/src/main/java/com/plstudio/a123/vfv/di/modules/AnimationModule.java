package com.plstudio.a123.vfv.di.modules;

import android.content.Context;

import com.plstudio.a123.vfv.animation.CardAnimator;
import com.plstudio.a123.vfv.animation.CircuarAnimator;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AnimationModule {
    private final Context context;

    public AnimationModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    CardAnimator providesCardAnimator(){return new CardAnimator(context);}

    @Provides
    @Singleton
    CircuarAnimator providesCircularAnimator(){return new CircuarAnimator(context);}

}
