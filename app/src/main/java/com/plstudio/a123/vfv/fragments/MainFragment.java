package com.plstudio.a123.vfv.fragments;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.plstudio.a123.vfv.MainActivity;
import com.plstudio.a123.vfv.view.flowingdrawer_core.FlowingDrawer;

import com.plstudio.a123.vfv.animation.AnimationVars;

import com.plstudio.a123.vfv.datadriven.PreferenceUtils;
import com.plstudio.a123.vfv.R;

import com.plstudio.a123.vfv.animation.CardAnimator;
import com.plstudio.a123.vfv.animation.FragmentCloseAnimation;
import com.plstudio.a123.vfv.darkthem.DarkThemeCreator;
import com.plstudio.a123.vfv.datadriven.FileIO;
import com.plstudio.a123.vfv.di.App;
import com.plstudio.a123.vfv.interfaces.FragmentNavigator;
import com.plstudio.a123.vfv.interfaces.MainContract;
import com.plstudio.a123.vfv.interfaces.ThemeCreatable;
import com.plstudio.a123.vfv.helpers.RequirementsLab;
import com.plstudio.a123.vfv.presenters.MainActivityPresenter;
import com.plstudio.a123.vfv.view.StepsView;

import javax.inject.Inject;


public class MainFragment extends Fragment implements ThemeCreatable, MainContract.View, FragmentCloseAnimation {
    private RelativeLayout background;
    private StepsView stepsView;
    private ProgressBar todo_stepsView, recomendation_Prog;
    private TextView todo_counter, recomendation_counter, vfv_done;
    private ImageView setting, title, card_back1, card_back2, sunImageView;
    private FlowingDrawer mDrawer;
    private CardView requirements, recomendation, title_card;;
    private static final String TAG = "MainActivity";
    private MainActivityPresenter presenter;

    @Inject
    PreferenceUtils preferenceUtils;
    @Inject
    RequirementsLab requirementsLab;
    @Inject
    CardAnimator cardAnimation;
    private boolean wasStoped = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        App.getComponent().injectMainFragment(this);
        initView(view);
        setUpDarkTheme(preferenceUtils.checkDarkThem(), getContext());

        presenter = new MainActivityPresenter();
        presenter.init(this, preferenceUtils, requirementsLab, new FileIO(getContext()));
        presenter.showAllProgress();

        initListeners();

        animation(view);
        cardAnimation.startCardFragment(title_card, getCards());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStart() {
        super.onStart();
       // Log.d(TAG, "Start");

    }

    private void initView(View view){
        background = (RelativeLayout) view.findViewById(R.id.background);
        mDrawer = (FlowingDrawer) getActivity().findViewById(R.id.drawerlayout);
        stepsView = (StepsView) view.findViewById(R.id.steps);
        todo_stepsView = (ProgressBar) view.findViewById(R.id.todo_progressBar);
        recomendation_Prog = (ProgressBar) view.findViewById(R.id.recomendation_progressBar);
        todo_counter= (TextView) view.findViewById(R.id.todo_progress_counter);
        recomendation_counter= (TextView) view.findViewById(R.id.recomendation_progress_counter);
        setting = (ImageView) view.findViewById(R.id.settings);
        title = (ImageView) view.findViewById(R.id.title);
        card_back1 = (ImageView) view.findViewById(R.id.card_back1);
        card_back2 = (ImageView) view.findViewById(R.id.card_back2);
        sunImageView = (ImageView) view.findViewById(R.id.sun);

        title_card = (CardView) view.findViewById(R.id.card_view);
        requirements = (CardView) view.findViewById(R.id.nav_requirements);
        recomendation = (CardView) view.findViewById(R.id.nav_reсomendation);

        vfv_done = (TextView) view.findViewById(R.id.vfv_done);
        initStepsView(stepsView);

    }

    private void animation(View view){
        Animation sunRiseAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.sun_rise);
        sunImageView.startAnimation(sunRiseAnimation);

        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.sun_back);
        AnimationDrawable gradient;
        gradient = (AnimationDrawable) layout.getBackground();
        gradient.setEnterFadeDuration(100);
        gradient.setExitFadeDuration(3000);
        gradient.setOneShot(true);
        gradient.start();

    }


    private void progressBarAnim(ProgressBar view, int res){
        ObjectAnimator animator = ObjectAnimator.ofInt(view, "progress", 0, res);
        animator.setInterpolator(AnimationVars.GAUGE_ANIMATION_INTERPOLATOR);
        animator.setDuration(AnimationVars.GAUGE_ANIMATION_DURATION);
        animator.start();
    }

    private void countAnim(TextView count, int res){
        ValueAnimator count_animator = new ValueAnimator();
        count_animator .setObjectValues(0, res);
        count_animator.addUpdateListener(animation -> count.setText(String.valueOf(animation.getAnimatedValue())));
        count_animator.setInterpolator(AnimationVars.GAUGE_ANIMATION_INTERPOLATOR);
        count_animator.setDuration(AnimationVars.GAUGE_ANIMATION_DURATION);;
        count_animator.start();
    }

    private void initStepsView(StepsView steps){
        float arr[] = {0.6f, 0.85f, 1f};
        int img[] = {R.drawable.bronze, R.drawable.silver, R.drawable.gold };
        steps.setSteps(arr, img);
    }

    @Override
    public void setUpDarkTheme(boolean cheskResult, Context context){
        if(cheskResult) {
            DarkThemeCreator creator = new DarkThemeCreator(this, context);
            creator.setUpDarkThem();
            //progress
            stepsView.setProgressDrawable(getResources().getDrawable(R.drawable.dark_horizontal));
            stepsView.setProgColor(R.color.darkprog);
            stepsView.setBackColor(R.color.backprog);

            todo_stepsView.setProgressDrawable(getResources().getDrawable(R.drawable.dprogress_circle));
            recomendation_Prog.setProgressDrawable(getResources().getDrawable(R.drawable.dprogress_circle));

            title.setImageResource(R.drawable.dtitle);
        }
    }

    private void initListeners(){

        FragmentManager fm = getFragmentManager();
        requirements.setOnClickListener(view -> {
                    cardAnimation.endCardFragment(title_card, getCards(), ()->fm.beginTransaction()
                            .replace(R.id.fragment_holder, new GroupsFragment())
                            .addToBackStack(MainFragment.class.getName())
                            .commit());
        });


        recomendation.setOnClickListener(view -> {
            cardAnimation.endCardFragment(title_card, getCards(), () -> fm.beginTransaction()
                    .replace(R.id.fragment_holder, new RecomendationListFragment())
                    .addToBackStack(MainFragment.class.getName())
                    .commit());
        });

        final Animation shakeAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
        vfv_done.setOnClickListener(view -> vfv_done.startAnimation(shakeAnimation));

        setting.setOnClickListener(event -> mDrawer.toggleMenu());

    }

    @Override
    public RelativeLayout getMainBackground() {
        return background;
    }

    @Override
    public ImageView[] getCardsBackgrounds() {
        ImageView backgrounds[] = {card_back1, card_back2};
        return backgrounds;
    }

    @Override
    public ImageView getSettingIcon() {
        return setting;
    }

    @Override
    public ImageView getBackIcon() {
        return null;
    }

    @Override
    public CardView[] getCards() {
        CardView cards[] = {requirements, recomendation};
        return cards;
    }

    @Override
    public void showDoneMessage() {
        vfv_done.setVisibility(View.VISIBLE);
    }

    @Override
    public void setMainProgress(int res) {
        progressBarAnim(stepsView, res);
    }

    @Override
    public void setRequirProgress(int res) {
        progressBarAnim(todo_stepsView, res);
        countAnim(todo_counter, res);
    }

    @Override
    public void setRecomendationProgress(int res) {
        progressBarAnim(recomendation_Prog, res);
        countAnim(recomendation_counter, res);
    }

    @Override
    public void endAnimation(FragmentNavigator fragmentNavigator) {
        cardAnimation.endCardFragment(title_card, getCards(), fragmentNavigator);

    }
}
