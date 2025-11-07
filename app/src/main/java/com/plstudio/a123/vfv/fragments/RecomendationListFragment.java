package com.plstudio.a123.vfv.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;

import com.plstudio.a123.vfv.R;
import com.plstudio.a123.vfv.animation.CardAnimator;
import com.plstudio.a123.vfv.animation.CircuarAnimator;
import com.plstudio.a123.vfv.animation.FragmentCloseAnimation;
import com.plstudio.a123.vfv.darkthem.DarkThemeCreator;
import com.plstudio.a123.vfv.datadriven.FileIO;
import com.plstudio.a123.vfv.datadriven.PreferenceUtils;
import com.plstudio.a123.vfv.di.App;
import com.plstudio.a123.vfv.interfaces.FragmentNavigator;
import com.plstudio.a123.vfv.interfaces.ThemeCreatable;
import com.plstudio.a123.vfv.view.ViewUtils;
import com.plstudio.a123.vfv.view.custom_checkbox.CustomCheckBox;
import com.plstudio.a123.vfv.view.flowingdrawer_core.FlowingDrawer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

public class RecomendationListFragment extends Fragment implements ThemeCreatable, FragmentCloseAnimation {
    private RelativeLayout background;
    private ImageView card_back1, card_back2, card_back3, card_back4;
    private ImageView setting, back;
    private CardView warmup, jump, running, strength, title_card;
    private ScrollView scroll;
    private FlowingDrawer mDrawer;
    private HashMap<String, Integer> check_boxes = new HashMap<>();
    private List<String> readed;
    private boolean animStarter = true;

    @Inject
    PreferenceUtils preferenceUtils;
    @Inject
    CardAnimator cardAnimator;
    @Inject
    CircuarAnimator circuarAnimator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recomendation_list, container, false);
        App.getComponent().injectRecomendationListFragment(this);
        
        initHash();
        initView(view);
        setUpDarkTheme(preferenceUtils.checkDarkThem(), getContext());

        String status = readStatus();
        readed = status != null && !status.trim().isEmpty() 
                ? Arrays.asList(status.split(" ")) 
                : Arrays.asList();
        initListeners();
        setTick(view);

        if (animStarter) cardAnimator.startCardFragment(title_card, getCards());
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null)
            animStarter = true;
        else
            animStarter = savedInstanceState.getBoolean("anim");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("anim", false);
    }

    @Override
    public void onResume() {
        super.onResume();
        String status = readStatus();
        readed = status != null && !status.trim().isEmpty() 
                ? Arrays.asList(status.split(" ")) 
                : Arrays.asList();
        View view = getView();
        if (view != null) {
            setTick(view);
        }
    }

    private void initView(View view) {
        background = (RelativeLayout) view.findViewById(R.id.background);
        back = view.findViewById(R.id.back);
        setting = view.findViewById(R.id.settings);

        scroll = view.findViewById(R.id.motion);
        title_card = (CardView) view.findViewById(R.id.card_view);
        ViewUtils.setUpWindowInsets(title_card);
        warmup = (CardView) view.findViewById(R.id.warm_up);
        jump = (CardView) view.findViewById(R.id.jump);
        running = (CardView) view.findViewById(R.id.run);
        strength = (CardView) view.findViewById(R.id.strength);

        card_back1 = (ImageView) view.findViewById(R.id.card_back1);
        card_back2 = (ImageView) view.findViewById(R.id.card_back2);
        card_back3 = (ImageView) view.findViewById(R.id.card_back3);
        card_back4 = (ImageView) view.findViewById(R.id.card_back4);

        mDrawer = (FlowingDrawer) getActivity().findViewById(R.id.drawerlayout);

        // Setting enable checkboxes
        CustomCheckBox toCheck;
        for (int a : check_boxes.values()) {
            toCheck = view.findViewById(a);
            if (toCheck != null) {
                toCheck.setEnabled(false);
            }
        }
    }

    private void initHash() {
        check_boxes.put("cardio", R.id.cardi_check);
        check_boxes.put("jump", R.id.jump_check);
        check_boxes.put("running", R.id.run_check);
        check_boxes.put("strength", R.id.strength_check);
    }

    private void initListeners() {
        FragmentManager fm = getFragmentManager();
        back.setOnClickListener(event -> getActivity().onBackPressed());
        setting.setOnClickListener(event -> mDrawer.toggleMenu());

        warmup.setOnClickListener(view -> startRecomendation("cardio", fm));
        jump.setOnClickListener(view -> startRecomendation("jump", fm));
        running.setOnClickListener(view -> startRecomendation("running", fm));
        strength.setOnClickListener(view -> startRecomendation("strength", fm));
    }

    private void startRecomendation(String title, FragmentManager fm) {
        boolean isRead = readed.contains(title);
        RecomendationFragment fragment = RecomendationFragment.newInstance(title, isRead);
        cardAnimator.endCardFragment(title_card, getCards(), () -> fm.beginTransaction()
                .replace(R.id.fragment_holder, fragment)
                .addToBackStack(RecomendationListFragment.class.getName())
                .commit());
    }

    private void setTick(View view) {
        if (view == null) return;
        CustomCheckBox toCheck;
        for (String key : check_boxes.keySet()) {
            if (readed.contains(key)) {
                toCheck = (CustomCheckBox) view.findViewById(check_boxes.get(key));
                if (toCheck != null) {
                    toCheck.setChecked(true, true);
                }
            }
        }
    }

    private String readStatus() {
        return new FileIO(getContext()).readFromFile("reading.txt", true);
    }

    @Override
    public RelativeLayout getMainBackground() {
        return background;
    }

    @Override
    public ImageView[] getCardsBackgrounds() {
        return new ImageView[]{card_back1, card_back2, card_back3, card_back4};
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
        return new CardView[]{warmup, jump, running, strength};
    }

    public void setUpDarkTheme(boolean cheskRes, Context context) {
        if (cheskRes)
            new DarkThemeCreator(this, getContext()).setUpDarkThem();
    }

    @Override
    public void endAnimation(FragmentNavigator fragmentNavigator) {
        cardAnimator.endCardFragment(title_card, getCards(), fragmentNavigator);
    }
}

