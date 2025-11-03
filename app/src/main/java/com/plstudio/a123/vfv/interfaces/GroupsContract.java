package com.plstudio.a123.vfv.interfaces;

import android.content.Context;
import androidx.annotation.NonNull;

import com.plstudio.a123.vfv.helpers.RequirementsLab;

import java.util.List;

public interface GroupsContract {
    interface View extends MvpView{
        void setUpDarkTheme(boolean cheskRes, Context context);
        void setGroupsProgress(List<Integer> groupsProgress);
        void setAllProgreess(int progress);
    }
    interface Presenter extends MvpPresenter<GroupsContract.View> {
        void init(@NonNull RequirementsLab requremntsLab);

        //get calculation and output result
        void setGroupsProgress();
        void setAllProgreess();
        void setUpProgress();
    }
}
