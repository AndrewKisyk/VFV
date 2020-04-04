package com.plstudio.a123.vfv.model;

import android.content.Context;

import com.plstudio.a123.vfv.datadriven.FileIO;

public class User {
    private static User user;
    private String sex;
    private String age;
    private int min, max = 1;

    public static User getUser(Context context){
        if(user == null)
            user = new User(context);
        return user;
    }

    private User(Context context){
        String s =  new FileIO(context).readFromFile("user.txt", false);
        age = s.substring(0,2);
        sex = s.substring(2);

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
