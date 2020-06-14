package com.plstudio.a123.vfv.interfaces;

public interface UserContract {

    interface View extends MvpView {

        boolean isMale();
        boolean isFmale();
        String getInAge();

        void showAgeError();
        void showSexError();

        //next screen
        void next();
        void close();
    }


    interface Presenter extends MvpPresenter<View> {

        void init();
        // field is filled
        boolean validate();

        void logIn();
        void writeUserData();

    }

}
