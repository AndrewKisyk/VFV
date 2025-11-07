package com.plstudio.a123.vfv.di.components;

import com.plstudio.a123.vfv.AutorizationActivity;
import com.plstudio.a123.vfv.MainActivity;
import com.plstudio.a123.vfv.di.AppScope;
import com.plstudio.a123.vfv.di.modules.AnimationModule;
import com.plstudio.a123.vfv.di.modules.AppModule;
import com.plstudio.a123.vfv.di.modules.DbModule;
import com.plstudio.a123.vfv.fragments.GroupsFragment;
import com.plstudio.a123.vfv.fragments.MainFragment;
import com.plstudio.a123.vfv.fragments.RecomendationFragment;
import com.plstudio.a123.vfv.fragments.RecomendationListFragment;
import com.plstudio.a123.vfv.fragments.RequirementsFragment;


import javax.inject.Singleton;

import dagger.Component;

@AppScope
@Component(modules = {AppModule.class, DbModule.class, AnimationModule.class})

@Singleton
public interface AppComponent {
    void injectAutoriztionActivity(AutorizationActivity autorizationActivity);
    void injectMainActivity(MainActivity mainActivity);
    void injectMainFragment(MainFragment mainFragment);
    void injectGroupsFragment(GroupsFragment mainFragment);
    void injectRecomendationFragment(RecomendationFragment recomendationFragment);
    void injectRecomendationListFragment(RecomendationListFragment recomendationListFragment);
    void injectRequirementsFragment(RequirementsFragment requirementsFragment);
}
