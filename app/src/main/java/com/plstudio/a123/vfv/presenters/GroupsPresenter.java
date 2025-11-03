package com.plstudio.a123.vfv.presenters;

import androidx.annotation.NonNull;

import com.plstudio.a123.vfv.interfaces.GroupsContract;
import com.plstudio.a123.vfv.helpers.RequirementsLab;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;




public class GroupsPresenter implements GroupsContract.Presenter {

    private GroupsContract.View mview;
    private RequirementsLab requirementsLab;
    private CompositeDisposable subscribes;
    @Override
    public void init( @NonNull RequirementsLab requremntsLab) {
        this.requirementsLab = requremntsLab;
        subscribes = new CompositeDisposable();
    }
    @Override
    public void setUpProgress(){
        setGroupsProgress();
        setAllProgreess();
    }

    @Override
    public void setGroupsProgress() {
        Disposable disposable = Observable.just("1", "2", "3", "4", "5")
                                            .map(gnum -> requirementsLab.getGroupResult(gnum))
                                            .toList()
                                            .subscribeOn(Schedulers.newThread())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(list-> mview.setGroupsProgress(list));
        subscribes.add(disposable);
    }

    @Override
    public void setAllProgreess() {
        Disposable disposable = Single.just(requirementsLab.getAllDoneResult())
                                     .subscribeOn(Schedulers.newThread())
                                     .observeOn(AndroidSchedulers.mainThread())
                                     .subscribe(el -> mview.setAllProgreess(el));
        subscribes.add(disposable);
    }

    @Override
    public void attachView(GroupsContract.View mvpView) {
        mview = mvpView;
    }

    @Override
    public void viewIsReady() {

    }

    @Override
    public void detachView() {

    }

    @Override
    public void destroy() {
        this.mview = null;
        subscribes.dispose();
    }
}
