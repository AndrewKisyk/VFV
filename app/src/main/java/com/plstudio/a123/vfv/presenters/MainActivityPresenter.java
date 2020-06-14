package com.plstudio.a123.vfv.presenters;

import android.support.annotation.NonNull;
import android.util.Log;

import com.plstudio.a123.vfv.PreferenceUtils;
import com.plstudio.a123.vfv.ProgressCulculator;
import com.plstudio.a123.vfv.datadriven.FileIO;
import com.plstudio.a123.vfv.interfaces.MainContract;
import com.plstudio.a123.vfv.model.RequirementsLab;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class MainActivityPresenter extends PresenterBase<MainContract.View> implements MainContract.Presenter {
    PreferenceUtils preferences;
    MainContract.View mView;
    ProgressCulculator calculator;
    RequirementsLab requirementsLab;
    FileIO recomendations;


    @Override
    public void init(@NonNull MainContract.View authView,
                     @NonNull PreferenceUtils preferences,
                     @NonNull RequirementsLab requirementsLab,
                     @NonNull FileIO recomendations) {
        this.preferences = preferences;
        this.requirementsLab = requirementsLab;
        this.recomendations = recomendations;
        mView = authView;
        calculator = new ProgressCulculator(preferences);
    }

    @Override
    public boolean checkDarkThem() {
        if(preferences.getTheme().equals("dark"))
            return true;
        return false;
    }

    @Override
    public void showAllProgress() {
        setUpRequirementsProgress();
        setUpRecomendationProgress();
        setUpMainProgress();

        //check and show label if VFV is done
        checkVfvResult();
    }

    @Override
    public void setUpRequirementsProgress() {
       getDoneRequirements()
                            .subscribeOn(Schedulers.newThread())
                            .map(el -> calculator.getToDoRes(el))
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(el -> mView.setRequirProgress(el));
    }

    @Override
    public void setUpRecomendationProgress() {
       getDoneRecomendation()
                            .subscribeOn(Schedulers.newThread())
                            .map(el -> calculator.getRecomStatus(el))
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(el -> mView.setRecomendationProgress(el));
    }

    @Override
    public void setUpMainProgress() {
         getDoneRequirements()
                            .mergeWith(getDoneRecomendation())
                            .subscribeOn(Schedulers.newThread())
                            .reduce((rqu, reco)-> calculator.computAllRes(rqu, reco))
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(el -> mView.setMainProgress(el));
    }

    @Override
    public Single<Integer> getDoneRequirements(){
        return Single.just(requirementsLab.getRequirements("1").size());
    }

    @Override
    public Single<Integer> getDoneRecomendation(){
        return  Single.just(recomendations.readFromFile("reading.txt", true)
                                .split(" ")
                                .length);
    }

    @Override
    public void checkVfvResult(){
         Observable.just("1", "2", "3", "4", "5")
                         .map(el -> requirementsLab.getRequirements(el, "1").size())
                         .toList()
                         .subscribeOn(Schedulers.newThread())
                         .observeOn(AndroidSchedulers.mainThread())
                         .subscribe(list -> { if(calculator.compareAllVfvRes(list))  mView.showDoneMessage(); });

    }

    @Override
    public void viewIsReady() {

    }


}
