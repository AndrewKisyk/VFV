package com.plstudio.a123.vfv.model;

import com.plstudio.a123.vfv.datadriven.PreferenceUtils;

public class User {
    private static User user;
    private String sex;
    private String age;
    private int min, max = 1;
    private PreferenceUtils preferences;

    public static User getUser(PreferenceUtils preferences){
        if(user == null)
            user = new User(preferences);
        return user;
    }

    private User(PreferenceUtils preferences){
        this.preferences = preferences;
        age = preferences.getUserAge();
        sex = preferences.getUserSex();

        setMax();
        setMin();

    }

    public String getSex() {
        return sex;
    }

    public int getMin() {
        return min;
    }

    private void setMax(){
        int a= Integer.parseInt(age);

        if(sex.equals("m")) {
            if( a < 16 )
                max = 20;
            else
                max = 22;
        }

        if(sex.equals("f")) {
            if( a < 15 )
                max = 19;
            else
                max = 20;
        }

    }
    private void setMin(){
        if(sex.equals("m"))
            min = 12;
        else
            min = 10;
    }

    public String getAge() {
        return age;
    }


    public void userToNull(){
        user = null;

    }

    public int getMax() {
        return max;
    }
}
