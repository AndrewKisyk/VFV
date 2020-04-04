package com.plstudio.a123.vfv;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;

import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.plstudio.a123.vfv.datadriven.FileIO;
import com.plstudio.a123.vfv.model.RequirementsLab;
import com.plstudio.a123.vfv.model.User;
import com.plstudio.a123.vfv.view.StepsView;
import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;



public class MainActivity extends AppCompatActivity {
    private RelativeLayout background;
    private StepsView stepsView;
    private ProgressBar todo_stepsView, recomendation_Prog;
    private TextView todo_counter, recomendation_counter, vfv_done;
    private ImageView setting, title, card_back1, card_back2;
    private FlowingDrawer mDrawer;
    private CardView requirements;
    private CardView recomendation;
    private static final String TAG = "MainActivity";
    private User user;
    private static final long GAUGE_ANIMATION_DURATION = 5000;
    private static final TimeInterpolator GAUGE_ANIMATION_INTERPOLATOR = new DecelerateInterpolator(2);
    private SharedPreferences mSettings;
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_THEME = "theme";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        if(checkDarkTheme())
            setTheme(R.style.darktheme);
        else
            setTheme(R.style.MainTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        user = User.getUser(this);
        background = (RelativeLayout) findViewById(R.id.background);
        stepsView = (StepsView) findViewById(R.id.steps);
        todo_stepsView = (ProgressBar) findViewById(R.id.todo_progressBar);
        recomendation_Prog = (ProgressBar) findViewById(R.id.recomendation_progressBar);
        todo_counter= (TextView) findViewById(R.id.todo_progress_counter);
        recomendation_counter= (TextView) findViewById(R.id.recomendation_progress_counter);
        setting = (ImageView) findViewById(R.id.settings);
        title = (ImageView) findViewById(R.id.title);
        card_back1 = (ImageView) findViewById(R.id.card_back1);
        card_back2 = (ImageView) findViewById(R.id.card_back2);

        requirements = (CardView) findViewById(R.id.nav_requirements);
        requirements.setRadius(20);

        recomendation = (CardView) findViewById(R.id.nav_reсomendation);
        recomendation.setRadius(20);

        vfv_done = (TextView) findViewById(R.id.vfv_done);
        initStepsView(stepsView);

        new Thread((Runnable) () -> setUpAllProgress());

        initNawigationMenu();
        initListeners();
        if(checkDarkTheme())
            setupDarkTheme();
        animation();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpAllProgress();
    }
    private void animation(){
        ImageView sunImageView = (ImageView) findViewById(R.id.sun);
        Animation sunRiseAnimation = AnimationUtils.loadAnimation(this, R.anim.sun_rise);
        sunImageView.startAnimation(sunRiseAnimation);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.sun_back);
        AnimationDrawable gradient;
        gradient = (AnimationDrawable) layout.getBackground();
        gradient.setEnterFadeDuration(100);
        gradient.setExitFadeDuration(3000);
        gradient.setOneShot(true);
        gradient.start();


       cardsAnimation(recomendation, 0);
       cardsAnimation(requirements, 200);

    }
    private void cardsAnimation(CardView card, int delay){
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.liear_layout_anim);
        animation.setDuration(1000);
        animation.setStartOffset(delay);
        card.setAnimation(animation);
        card.setVisibility(View.VISIBLE);
        card.animate();

        animation.start();
    }
    private void setUpAllProgress(){
        ProgressCulculator calc = new ProgressCulculator(this);
        //vfv mark

        if(calc.compareAllVfvRes()) {
            vfv_done.setVisibility(View.VISIBLE);

        }
        //steps view
        progressBarAnim(stepsView, calc.getAllRes());
        //todos
        int res =  calc.getToDoRes();
        progressBarAnim(todo_stepsView, res);
        countAnim(todo_counter, res);
        //recom
        res = calc.getRecomStatus();
        progressBarAnim(recomendation_Prog, res);
        countAnim(recomendation_counter, res);
    }

    private void progressBarAnim(ProgressBar view, int res){
        ObjectAnimator animator = ObjectAnimator.ofInt(view, "progress", 0, res);
        animator.setInterpolator(GAUGE_ANIMATION_INTERPOLATOR);
        animator.setDuration(GAUGE_ANIMATION_DURATION);
        animator.start();
    }
    private void countAnim(TextView count, int res){
        ValueAnimator count_animator = new ValueAnimator();
        count_animator .setObjectValues(0, res);
        count_animator.addUpdateListener(animation -> count.setText(String.valueOf(animation.getAnimatedValue())));
        count_animator.setInterpolator(GAUGE_ANIMATION_INTERPOLATOR);
        count_animator.setDuration(GAUGE_ANIMATION_DURATION);;
        count_animator.start();
    }

    private void initStepsView(StepsView steps){
        float arr[] = {0.6f, 0.85f, 1f};
        int img[] = {R.drawable.bronze, R.drawable.silver, R.drawable.gold };
        steps.setSteps(arr, img);



    }

    private void setupDarkTheme(){
        //progress
        stepsView.setProgressDrawable(getResources().getDrawable(R.drawable.dark_horizontal));
        stepsView.setProgColor(R.color.darkprog);
        stepsView.setBackColor(R.color.backprog);

        todo_stepsView.setProgressDrawable(getResources().getDrawable(R.drawable.dprogress_circle));
        recomendation_Prog.setProgressDrawable(getResources().getDrawable(R.drawable.dprogress_circle));

        //icons
        setting.setImageResource(R.drawable.ic_dtsettings_black_24dp);
        title.setImageResource(R.drawable.dtitle);

        //backgrounds
        background.setBackground(getResources().getDrawable(R.drawable.dt_gradient));
        recomendation.setBackgroundResource(R.drawable.cardvieewcorners);
        requirements.setBackgroundResource(R.drawable.cardvieewcorners);
        card_back1.setBackground(getResources().getDrawable(R.drawable.dt_cardgrad));
        card_back2.setBackground(getResources().getDrawable(R.drawable.dt_cardgrad));
    }

    private void initNawigationMenu(){
        mDrawer = (FlowingDrawer) findViewById(R.id.drawerlayout);
        mDrawer.setBackgroundColor(getResources().getColor(R.color.darkprog));
        mDrawer.setTouchMode(ElasticDrawer.TOUCH_MODE_BEZEL);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawer.toggleMenu();
            }
        });
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

    private void initListeners(){
        requirements.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, GroupsActivity.class);
            startActivity(intent);
        });

        recomendation.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, RecomendationListActivity.class);
            startActivity(intent);
        });

        final Animation shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);
        vfv_done.setOnClickListener(view -> {
            vfv_done.startAnimation(shakeAnimation);
        });

    }

    private boolean checkDarkTheme(){
        if(mSettings.getString(APP_PREFERENCES_THEME, "theme").equals("dark"))
            return true;
        return false;
    }


}
