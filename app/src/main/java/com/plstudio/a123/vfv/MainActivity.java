package com.plstudio.a123.vfv;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.plstudio.a123.vfv.di.App;
import com.plstudio.a123.vfv.fragments.MainFragment;
import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;

import javax.inject.Inject;


public class MainActivity extends AppCompatActivity  {
    private FlowingDrawer mDrawer;
    private static final String TAG = "MainActivity";
    @Inject PreferenceUtils preferenceUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.getComponent().injectMainActivity(this);
        if(preferenceUtils.getTheme().equals("dark"))
            setTheme(R.style.darktheme);
        else
            setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initNawigationMenu();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_holder, new MainFragment())
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    private void initNawigationMenu(){
        mDrawer = (FlowingDrawer) findViewById(R.id.drawerlayout);
        mDrawer.setBackgroundColor(getResources().getColor(R.color.darkprog));
        mDrawer.setTouchMode(ElasticDrawer.TOUCH_MODE_BEZEL);
//        setting.setOnClickListener(view -> mDrawer.toggleMenu());
        setupMenu();
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isMenuVisible()) {
            mDrawer.closeMenu();
        } else {
            super.onBackPressed();
        }
    }

    private void setupMenu() {
        FragmentManager fm = getSupportFragmentManager();
        MenuListFragment mMenuFragment = (MenuListFragment) fm.findFragmentById(R.id.id_container_menu);
        if (mMenuFragment == null) {
            mMenuFragment = new MenuListFragment();
            fm.beginTransaction().add(R.id.id_container_menu, mMenuFragment).commit();
        }
    }


}
