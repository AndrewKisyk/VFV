package com.plstudio.a123.vfv;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;

import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.support.v4.app.Fragment;

import com.plstudio.a123.vfv.model.RequirementsLab;
import com.plstudio.a123.vfv.model.User;

import java.io.IOException;
import java.io.OutputStreamWriter;


public class MenuListFragment extends Fragment {
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_THEME = "theme";
    private NavigationView vNavigation;
    private SharedPreferences mSettings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_list, container, false);

        mSettings = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        vNavigation = (NavigationView) view.findViewById(R.id.vNavigation);

        changeNameAppear();
        vNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();
                Intent intent = null;
                switch (id) {
                    case R.id.nav_home:
                        makeUserDataEmpty(getContext());
                        intent = new Intent(getActivity(), AutorizationActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_progress:
                        intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_requirements:
                        intent = new Intent(getActivity(), GroupsActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_reсomendation:
                        intent = new Intent(getActivity(), RecomendationListActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.dark_theme:
                        changThemeSetting();
                        reloadActivity();
                        break;
                }

                return true;
            }
        }) ;
        return view;
    }
    private void changThemeSetting() {
        SharedPreferences.Editor editor = mSettings.edit();
        if (mSettings.getString(APP_PREFERENCES_THEME, "theme").equals("dark")) {
            editor.putString(APP_PREFERENCES_THEME, "light");
        } else {
            editor.putString(APP_PREFERENCES_THEME, "dark");
        }
        editor.apply();
    }
    private void changeNameAppear(){
        if(mSettings.getString(APP_PREFERENCES_THEME, "theme").equals("dark")) {
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
    }

    private void reloadActivity(){
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
    
}
