package com.plstudio.a123.vfv.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.plstudio.a123.vfv.view.flowingdrawer_core.FlowingDrawer;
import com.plstudio.a123.vfv.animation.AnimationVars;
import com.plstudio.a123.vfv.animation.CircuarAnimator;
import com.plstudio.a123.vfv.datadriven.PreferenceUtils;
import com.plstudio.a123.vfv.R;
import com.plstudio.a123.vfv.RequirementsActivity;
import com.plstudio.a123.vfv.animation.CardAnimator;
import com.plstudio.a123.vfv.animation.FragmentCloseAnimation;
import com.plstudio.a123.vfv.darkthem.DarkThemeCreator;
import com.plstudio.a123.vfv.di.App;
import com.plstudio.a123.vfv.interfaces.FragmentNavigator;
import com.plstudio.a123.vfv.interfaces.GroupsContract;
import com.plstudio.a123.vfv.interfaces.ThemeCreatable;
import com.plstudio.a123.vfv.helpers.RequirementsLab;
import com.plstudio.a123.vfv.model.User;
import com.plstudio.a123.vfv.presenters.GroupsPresenter;

import java.util.List;

import javax.inject.Inject;

public class GroupsFragment extends Fragment implements ThemeCreatable, GroupsContract.View, FragmentCloseAnimation {
    private RelativeLayout background;
    private ImageView card_back1, card_back2, card_back3, card_back4, card_back5;
    private ImageView setting, back;
    private CardView g1, g2, g3, g4, g5, title_card;
    private TextView g1_counter, g2_counter, g3_counter, g4_counter, g5_counter, all_counter;
    private FlowingDrawer mDrawer;
    private GroupsPresenter presenter;
    private boolean animStarter = true;
    @Inject
    RequirementsLab requirementsLab;
    @Inject
    PreferenceUtils preferenceUtils;
    @Inject
    CardAnimator cardAnimator;
    @Inject
    CircuarAnimator circuarAnimator;
    @Inject
    User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups, container, false);
        App.getComponent().injectGroupsFragment(this);
        initView(view);
        setUpDarkTheme(preferenceUtils.checkDarkThem(), getContext()); //if it set in utils

        presenter = new GroupsPresenter();
        presenter.init(requirementsLab);
        presenter.attachView(this);
        presenter.setUpProgress();

        initListeners();

        if(animStarter) cardAnimator.startCardFragment(title_card, getCards());
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null)
            animStarter = true;
        else
            animStarter = savedInstanceState.getBoolean("anim");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("anim", false);

    }

    public void onResume() {
        super.onResume();
        presenter.setUpProgress();
    }

    private void initView(View view) {
        setting = view.findViewById(R.id.settings);
        background = (RelativeLayout) view.findViewById(R.id.background);

        back = view.findViewById(R.id.back);
        setting = view.findViewById(R.id.settings);

        title_card = (CardView) view.findViewById(R.id.card_view);
        g1 = (CardView) view.findViewById(R.id.g_1);
        g2 = (CardView) view.findViewById(R.id.g_2);
        g3 = (CardView) view.findViewById(R.id.g_3);
        g4 = (CardView) view.findViewById(R.id.g_4);
        g5 = (CardView) view.findViewById(R.id.g_5);

        g1_counter = (TextView) view.findViewById(R.id.g1_counter);
        g2_counter = (TextView) view.findViewById(R.id.g2_counter);
        g3_counter = (TextView) view.findViewById(R.id.g3_counter);
        g4_counter = (TextView) view.findViewById(R.id.g4_counter);
        g5_counter = (TextView) view.findViewById(R.id.g5_counter);
        card_back1 = (ImageView) view.findViewById(R.id.card_back1);
        card_back2 = (ImageView) view.findViewById(R.id.card_back2);
        card_back3 = (ImageView) view.findViewById(R.id.card_back3);
        card_back4 = (ImageView) view.findViewById(R.id.card_back4);
        card_back5 = (ImageView) view.findViewById(R.id.card_back5);

        all_counter = (TextView) view.findViewById(R.id.bottom);

        mDrawer = (FlowingDrawer) getActivity().findViewById(R.id.drawerlayout);
    }

    private void initListeners() {
        back.setOnClickListener(event -> getActivity().onBackPressed());
        setting.setOnClickListener(event -> mDrawer.toggleMenu());

        Intent intent = new Intent(getContext(), RequirementsActivity.class);
        g1.setOnClickListener(event -> {
            /*intent.putExtra("GROUP", "1");
            intent.putExtra("MIN", "2");
            startActivity(intent);*
             */
            AnimationVars.setAnimationVars(event);
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_holder, new RequirementsFragment())
                    .addToBackStack(GroupsFragment.class.getName())
                    .commit();
        });
        g2.setOnClickListener(event -> {
            /*intent.putExtra("GROUP", "2");
            intent.putExtra("MIN", "2");
            startActivity(intent);*/
            AnimationVars.setAnimationVars(event);
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_holder, new RequirementsFragment())
                    .addToBackStack(GroupsFragment.class.getName())
                    .commit();
        });

        g3.setOnClickListener(event -> {
            intent.putExtra("GROUP", "3");
            intent.putExtra("MIN", "2");
            startActivity(intent);
        });
        g4.setOnClickListener(event -> {
            intent.putExtra("GROUP", "4");
            intent.putExtra("MIN", "3");
            startActivity(intent);
        });
        g5.setOnClickListener(event -> {
            intent.putExtra("GROUP", "5");
            intent.putExtra("MIN", "3");
            startActivity(intent);
        });

    }

    @Override
    public RelativeLayout getMainBackground() {
        return background;
    }

    @Override
    public ImageView[] getCardsBackgrounds() {
        return new ImageView[]{card_back1, card_back2, card_back3, card_back4, card_back5};
    }

    @Override
    public ImageView getSettingIcon() {
        return setting;
    }

    @Override
    public ImageView getBackIcon() {
        return back;
    }

    @Override
    public CardView[] getCards() {
        return new CardView[]{g1, g2, g3, g4, g5};
    }

    @Override
    public void setUpDarkTheme(boolean cheskRes, Context context) {
        if (cheskRes)
            new DarkThemeCreator(this, getContext()).setUpDarkThem();
    }

    @Override
    public void setGroupsProgress(List<Integer> groupsProgress) {
        g1_counter.setText(groupsProgress.get(0) + "/2");
        g2_counter.setText(groupsProgress.get(1) + "/2");
        g3_counter.setText(groupsProgress.get(2) + "/2");
        g4_counter.setText(groupsProgress.get(3) + "/3");
        g5_counter.setText(groupsProgress.get(4) + "/3");
    }

    @Override
    public void setAllProgreess(int progress) {
        all_counter.setText("виконано: " + progress + "/" + user.getMax());
    }

    @Override
    public void endAnimation(FragmentNavigator fragmentNavigator) {
        cardAnimator.endCardFragment(title_card, getCards(), fragmentNavigator);
    }
}