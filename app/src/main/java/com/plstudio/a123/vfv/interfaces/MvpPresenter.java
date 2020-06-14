package com.plstudio.a123.vfv.interfaces;

public interface MvpPresenter<V extends MvpView> {
    void attachView(V mvpView);

    void viewIsReady();

    void detachView();

    void destroy();
}
