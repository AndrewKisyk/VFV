package com.plstudio.a123.vfv.presenters;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.plstudio.a123.vfv.PreferenceUtils;
import com.plstudio.a123.vfv.interfaces.UserContract;

public class AutorizationPresenter extends PresenterBase<UserContract.View> implements UserContract.Presenter {

    private final UserContract.View mView;
    private PreferenceUtils preferences;

    public AutorizationPresenter(@NonNull UserContract.View authView,
                                 @NonNull PreferenceUtils preferences) {
        mView = authView;
        this.preferences = preferences;
    }

    @Override
    public void init() {
        String token = preferences.getToken();
        Log.d("TrEeg", token);
        if (!TextUtils.isEmpty(token)) {
            Log.d("TrEeg", "OK");
            mView.next();
            mView.close();
        }
    }


    @Override
    public boolean validate() {
        boolean flag = true;
            String s = mView.getInAge();
            if(s.isEmpty()) {
                mView.showAgeError();
                return false;
            }
            int a = Integer.parseInt(mView.getInAge());
            if( a > 17 || a < 12) {
                mView.showAgeError();
                flag = false;
            }
            if(!(mView.isFmale() || mView.isMale())){
                mView.showSexError();
                flag =false;
            }

        return flag;
    }

    @Override
    public void logIn() {
        if(validate()){
            writeUserData();
            mView.next();
        }
    }

    @Override
    public void writeUserData() {
        preferences.setAge(mView.getInAge());
        if(mView.isMale())
            preferences.setSex("m");
        else
            preferences.setSex("f");
    }


    @Override
    public void viewIsReady() {

    }
}
