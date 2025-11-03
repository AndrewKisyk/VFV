package com.plstudio.a123.vfv.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.plstudio.a123.vfv.AutorizationActivity;
import com.plstudio.a123.vfv.MainActivity;
import com.plstudio.a123.vfv.R;
import com.plstudio.a123.vfv.RecomendationListActivity;
import com.plstudio.a123.vfv.datadriven.PreferenceUtils;
import com.plstudio.a123.vfv.helpers.RequirementsLab;

import java.io.IOException;
import java.io.OutputStreamWriter;


public class MenuListFragment extends Fragment {
    private NavigationView vNavigation;
    private PreferenceUtils mSettings;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_list, container, false);

        mSettings = new PreferenceUtils(getContext());
        vNavigation = (NavigationView) view.findViewById(R.id.vNavigation);

        changeNameAppear();
        vNavigation.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent intent;
            if (id == R.id.nav_home) {
                makeUserDataEmpty(getContext());
                intent = new Intent(getActivity(), AutorizationActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_progress) {
                intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_requirements) {

            } else if (id == R.id.nav_reсomendation) {
                intent = new Intent(getActivity(), RecomendationListActivity.class);
                startActivity(intent);
            } else if (id == R.id.dark_theme) {
                changThemeSetting();
                reloadActivity();
            }

            return true;
        }) ;
        return view;
    }
    private void changThemeSetting() {
        if (mSettings.getTheme().equals("dark"))
            mSettings.setTheme("light");
        else
            mSettings.setTheme("dark");

    }
    private void changeNameAppear(){
        if(mSettings.getTheme().equals("dark")) {
            vNavigation.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            vNavigation.setItemIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.second_white)));
            vNavigation.getMenu().getItem(4).setIcon(R.drawable.ic_sunny_black_24dp);
            vNavigation.getMenu().getItem(4).setTitle(R.string.ligh_theme);
        } else {
            vNavigation.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.black)));
            vNavigation.setItemIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
            vNavigation.getMenu().getItem(4).setIcon(R.drawable.ic_brightness_2_black_24dp);
            vNavigation.getMenu().getItem(4).setTitle(R.string.dark_theme);
        }
    }

    private void makeUserDataEmpty(Context context){
            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("user.txt", Context.MODE_PRIVATE));
                outputStreamWriter.write("");
                outputStreamWriter.close();
            }
            catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        RequirementsLab requirementsLab = RequirementsLab.get(getContext());
        requirementsLab.undoAllRequirements();
        mSettings.setSex("");
        mSettings.setAge("");
    }

    private void reloadActivity(){
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
    
}
