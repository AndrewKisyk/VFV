package com.plstudio.a123.vfv.interfaces;

import android.content.Context;
import android.support.annotation.NonNull;

import com.plstudio.a123.vfv.datadriven.PreferenceUtils;
import com.plstudio.a123.vfv.datadriven.FileIO;
import com.plstudio.a123.vfv.helpers.RequirementsLab;

import io.reactivex.rxjava3.core.Single;

public interface MainContract {

    interface View extends MvpView{

         void setUpDarkTheme(boolean cheskRes, Context context);
         void showDoneMessage();
         void setMainProgress(int res);
         void setRequirProgress(int res);
         void setRecomendationProgress(int res);
    }
        interface Presenter extends MvpPresenter<MainContract.View> {
            void init(@NonNull MainContract.View authView,
                      @NonNull PreferenceUtils preferences,
                      @NonNull RequirementsLab requremntsLab,
                      @NonNull FileIO recomendations);

            //getting data
            Single<Integer> getDoneRequirements();
            Single<Integer> getDoneRecomendation();


            //get calculation and output result
            void setUpRequirementsProgress();
            void setUpRecomendationProgress();
            void setUpMainProgress();
            void showAllProgress();
            void checkVfvResult();
        }
}
