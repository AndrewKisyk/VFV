package com.plstudio.a123.vfv;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.plstudio.a123.vfv.animation.FragmentCloseAnimation;
import com.plstudio.a123.vfv.datadriven.PreferenceUtils;
import com.plstudio.a123.vfv.di.App;
import com.plstudio.a123.vfv.fragments.MainFragment;

import com.plstudio.a123.vfv.fragments.MenuListFragment;
import com.plstudio.a123.vfv.view.flowingdrawer_core.ElasticDrawer;
import com.plstudio.a123.vfv.view.flowingdrawer_core.FlowingDrawer;

import javax.inject.Inject;


public class MainActivity extends AppCompatActivity {
    private FlowingDrawer mDrawer;
    private static final String TAG = "MainActivity";
    @Inject
    PreferenceUtils preferenceUtils;
    FragmentCloseAnimation fanimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.getComponent().injectMainActivity(this);
        if (preferenceUtils.getTheme().equals("dark"))
            setTheme(R.style.darktheme);
        else
            setTheme(R.style.MainTheme);
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
    @Override
    public void onBackPressed() {
        if (mDrawer.isMenuVisible()) {
            mDrawer.closeMenu();
        }
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fanimation.endAnimation(() -> fm.popBackStack());
            fm.executePendingTransactions();
        } else
            super.onBackPressed();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        try {
            if (!(fragment instanceof MenuListFragment))
                fanimation = (FragmentCloseAnimation) fragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(fragment.toString() + " implementation error");
        }
    }

    private void initNawigationMenu() {
        mDrawer = (FlowingDrawer) findViewById(R.id.drawerlayout);
        mDrawer.setBackgroundColor(getResources().getColor(R.color.darkprog));
        mDrawer.setTouchMode(ElasticDrawer.TOUCH_MODE_BEZEL);
//        setting.setOnClickListener(view -> mDrawer.toggleMenu());
        setupMenu();
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
