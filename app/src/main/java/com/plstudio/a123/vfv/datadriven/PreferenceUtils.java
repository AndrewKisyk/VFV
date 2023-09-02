package com.plstudio.a123.vfv.datadriven;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtils {
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_THEME = "theme";
    public static final String USER_SEX = "sex";
    public static final String USER_AGE = "age";
    private SharedPreferences mSettings;

    public PreferenceUtils(Context context) {
        mSettings = context.getSharedPreferences(APP_PREFERENCES, 0);
    }

    private SharedPreferences.Editor getEditor() {
        return mSettings.edit();
    }

    public void setSex(String data) {
        getEditor().putString(USER_SEX, data).commit();
    }

    public void setAge(String data) {
        getEditor().putString(USER_AGE, data).commit();
    }

    public void setTheme(String data) {
        getEditor().putString(APP_PREFERENCES_THEME, data).commit();
    }

    public String getUserSex(){
        return mSettings.getString(USER_SEX, "");
    }

    public String getUserAge(){
        return mSettings.getString(USER_AGE, "");
    }

    public String getTheme(){
        return mSettings.getString(APP_PREFERENCES_THEME, "");
    }

    public boolean checkDarkThem(){
        if(getTheme().equals("dark"))
            return true;
        return false;
    }
    public String getToken(){
        return getUserSex() +""+ getUserAge();
    }

}
