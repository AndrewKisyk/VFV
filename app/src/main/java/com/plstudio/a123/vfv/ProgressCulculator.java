package com.plstudio.a123.vfv;

import android.content.Context;

import com.plstudio.a123.vfv.datadriven.FileIO;
import com.plstudio.a123.vfv.model.RequirementsLab;
import com.plstudio.a123.vfv.model.User;

import java.net.UnknownServiceException;

public class ProgressCulculator {
    RequirementsLab requirementsLab;
    Context mcontext;
    User user;

    public ProgressCulculator(Context context){
        mcontext = context;
        requirementsLab = RequirementsLab.get(mcontext);
        user = User.getUser(mcontext);
    }
    //stepsView progress
    public int getAllRes(){
        int res = compoutTodoResult() + getRecomStatus()/5;
        if(user.getSex().equals("f") && res == 98)
            res+=2;
        if(user.getSex().equals("m") && user.getMax() == 20  && res == 96)
            res+=4;
        return res;
    }
    //todos
    public int getToDoRes(){
        return getResult()*100/user.getMax();
    }
    //recomendation
    public int getRecomStatus(){
        String ret = new FileIO(mcontext).readFromFile("reading.txt", true);
        String[] temp =  ret.split(" ");
        return temp.length * 25 - 25;
    }
    //check if Vfv is done
    public boolean compareAllVfvRes(){
        if(getGroupResult("1") < 2)
            return false;
        if(getGroupResult("2") < 2)
            return false;
        if(getGroupResult("3") < 2)
            return false;
        if(getGroupResult("4") < 3)
            return false;
        if(getGroupResult("5") < 3)
            return false;
        return true;
    }

    private int compoutTodoResult(){
        int res = getResult();
        if(res <= user.getMin()) {
            res *= Math.round(60 / user.getMin());
        } else {
            res =(res - user.getMin())* Math.round(20 / (user.getMax() - user.getMin())) ;
            res += 60;
        }
        return res;
    }
    private int getResult(){
        return  requirementsLab.getRequirements("1").size();
    }

    private int getGroupResult(String group){
        if(group != null)
            return requirementsLab.getRequirements(group, "1").size();
        else
            return requirementsLab.getRequirements("1").size();
    }
}
