package com.plstudio.a123.vfv.presenters;

import com.plstudio.a123.vfv.interfaces.MvpPresenter;
import com.plstudio.a123.vfv.interfaces.MvpView;

public abstract class PresenterBase<T extends MvpView> implements MvpPresenter<T> {

    private T view;

    @Override
    public void attachView(T mvpView) {
        view = mvpView;
    }

    @Override
    public void detachView() {
        view = null;
    }

    public T getView() {
        return view;
    }

    protected boolean isViewAttached() {
        return view != null;
    }

    @Override
    public void destroy() {

    }

}
