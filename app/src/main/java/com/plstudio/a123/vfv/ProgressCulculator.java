package com.plstudio.a123.vfv;

import com.plstudio.a123.vfv.model.User;
import java.util.List;

public class ProgressCulculator {
    PreferenceUtils preferences;
    User user;

    public ProgressCulculator(PreferenceUtils preferenceUtils){
        user = User.getUser(preferences);
    }
    //stepsView progress
    public int computAllRes(int requirResult, int recomResult){
        requirResult = compoutTodoResult(requirResult);
        recomResult = getRecomStatus(recomResult)/5;
        int allRes = requirResult + recomResult;
        if(user.getSex().equals("f") && allRes == 98)
            allRes+=2;
        if(user.getSex().equals("m") && user.getMax() == 20  && allRes == 96)
            allRes+=4;
        return allRes;
    }
    //todos
    public int getToDoRes(int res){
        return res*100/user.getMax();
    }

    //recomendation
    public int getRecomStatus(int recomendationResult){
        return recomendationResult * 25 - 25;
    }
    //check if Vfv is done
    public boolean compareAllVfvRes(List<Integer> groupsRes){
        if(groupsRes.get(1) < 2)
            return false;
        if(groupsRes.get(2) < 2)
            return false;
        if(groupsRes.get(3) < 2)
            return false;
        if(groupsRes.get(4) < 3)
            return false;
        if(groupsRes.get(5) < 3)
            return false;
        return true;
    }

    private int compoutTodoResult(int res){
        if(res <= user.getMin()) {
            res *= Math.round(60 / user.getMin());
        } else {
            res =(res - user.getMin())* Math.round(20 / (user.getMax() - user.getMin())) ;
            res += 60;
        }
        return res;
    }

}
