package com.plstudio.a123.vfv.presenters;

import androidx.annotation.NonNull;

import com.plstudio.a123.vfv.datadriven.PreferenceUtils;
import com.plstudio.a123.vfv.helpers.ProgressCulculator;
import com.plstudio.a123.vfv.datadriven.FileIO;
import com.plstudio.a123.vfv.interfaces.MainContract;
import com.plstudio.a123.vfv.helpers.RequirementsLab;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class MainActivityPresenter extends PresenterBase<MainContract.View> implements MainContract.Presenter {
    PreferenceUtils preferences;
    MainContract.View mView;
    ProgressCulculator calculator;
    RequirementsLab requirementsLab;
    FileIO recomendations;
    private CompositeDisposable subscriptions;

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
        this.subscriptions = new CompositeDisposable();
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
      Disposable disposable =  getDoneRequirements()
                            .subscribeOn(Schedulers.newThread())
                            .map(el -> calculator.getToDoRes(el))
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(el -> mView.setRequirProgress(el));
      subscriptions.add(disposable);
    }

    @Override
    public void setUpRecomendationProgress() {
        Disposable disposable = getDoneRecomendation()
                            .subscribeOn(Schedulers.newThread())
                            .map(el -> calculator.getRecomStatus(el))
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(el -> mView.setRecomendationProgress(el));
        subscriptions.add(disposable);
    }

    @Override
    public void setUpMainProgress() {
         Disposable disposable = getDoneRequirements()
                            .mergeWith(getDoneRecomendation())
                            .subscribeOn(Schedulers.newThread())
                            .reduce((rqu, reco)-> calculator.computAllRes(rqu, reco))
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(el -> mView.setMainProgress(el));
         subscriptions.add(disposable);
    }

    @Override
    public Single<Integer> getDoneRequirements(){
        return Single.just(requirementsLab.getAllDoneResult());
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
    @Override
    public void destroy(){
        subscriptions.dispose();
        mView = null;
    }

}
