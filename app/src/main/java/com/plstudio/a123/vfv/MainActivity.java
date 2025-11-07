package com.plstudio.a123.vfv;

import androidx.activity.OnBackPressedCallback;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.plstudio.a123.vfv.animation.FragmentCloseAnimation;
import com.plstudio.a123.vfv.datadriven.PreferenceUtils;
import com.plstudio.a123.vfv.di.App;
import com.plstudio.a123.vfv.fragments.MainFragment;

import com.plstudio.a123.vfv.fragments.MenuListFragment;
import com.plstudio.a123.vfv.interfaces.MainActivityNavigationController;
import com.plstudio.a123.vfv.view.flowingdrawer_core.ElasticDrawer;
import com.plstudio.a123.vfv.view.flowingdrawer_core.FlowingDrawer;

import javax.inject.Inject;


public class MainActivity extends AppCompatActivity implements MainActivityNavigationController {
    private FlowingDrawer mDrawer;
    private static final String TAG = "MainActivity";
    @Inject
    PreferenceUtils preferenceUtils;
    private FragmentCloseAnimation fanimation;
    private FrameLayout background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.getComponent().injectMainActivity(this);

        if (preferenceUtils.getTheme().equals("dark")) {
            WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                    .setAppearanceLightStatusBars(false);
            setTheme(R.style.darktheme);
        } else {
            WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                    .setAppearanceLightStatusBars(true);
            setTheme(R.style.MainTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WindowCompat.enableEdgeToEdge(getWindow());
        background = findViewById(R.id.background);
        setUpWindowInsets(background);
        initNawigationMenu();
        if(checkDarkTheme())
            setupDarkTheme();
        else
            setUpLightTheme();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_holder, new MainFragment())
                .commit();
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (mDrawer.isMenuVisible()) {
                    mDrawer.closeMenu();
                    return;
                }

                FragmentManager fm = getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fanimation.endAnimation(() -> fm.popBackStack());
                    fm.executePendingTransactions();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                    setEnabled(true);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

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

    private void setUpWindowInsets(View view) {
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets bars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                            | WindowInsetsCompat.Type.displayCutout()
            );
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
    }

    private void setupDarkTheme(){
        background.setBackground(getResources().getDrawable(R.drawable.dt_gradient));
    }

    private void setUpLightTheme() {
        background.setBackgroundColor(getResources().getColor(R.color.white));

    }

    private boolean checkDarkTheme(){
        if(preferenceUtils.getTheme().equals("dark"))
            return true;
        return false;
    }


    @Override
    public void openRequirementsFragment() {

    }
}
