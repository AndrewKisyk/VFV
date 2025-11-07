package com.plstudio.a123.vfv.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.plstudio.a123.vfv.R;
import com.plstudio.a123.vfv.animation.CircuarAnimator;
import com.plstudio.a123.vfv.animation.FragmentCloseAnimation;
import com.plstudio.a123.vfv.datadriven.FileIO;
import com.plstudio.a123.vfv.datadriven.PreferenceUtils;
import com.plstudio.a123.vfv.darkthem.DarkThemeCreator;
import com.plstudio.a123.vfv.di.App;
import com.plstudio.a123.vfv.helpers.ImageGetter;
import com.plstudio.a123.vfv.interfaces.FragmentNavigator;
import com.plstudio.a123.vfv.interfaces.ThemeCreatable;

import javax.inject.Inject;

public class RecomendationFragment extends Fragment implements ThemeCreatable, FragmentCloseAnimation {
    private static final String ARG_ARTICLE = "article";
    private static final String ARG_STATUS = "status";
    private static final String DEBUG_TAG = "RECOMEND_FRAGMENT: ";

    private String article;
    private boolean status;
    private String bodyData;
    private TextView textView;
    private Toolbar toolbar;
    private RelativeLayout background;
    private Handler handler;
    private Runnable runnable;
    private FileIO file;
    private View fragmentView;

    @Inject
    PreferenceUtils preferenceUtils;
    @Inject
    CircuarAnimator circuarAnimator;

    public static RecomendationFragment newInstance(String article, boolean status) {
        RecomendationFragment fragment = new RecomendationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ARTICLE, article);
        args.putBoolean(ARG_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            article = getArguments().getString(ARG_ARTICLE);
            status = getArguments().getBoolean(ARG_STATUS, false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recomendation, container, false);
        fragmentView = view;
        App.getComponent().injectRecomendationFragment(this);
        file = new FileIO(getContext(), Context.MODE_APPEND);
        initView(view);
        setUpDarkTheme(preferenceUtils.checkDarkThem(), getContext());
        loadContent();
        initListeners();

        if (!status) {
            handler = new Handler();
            runnable = () -> {
                Log.d(DEBUG_TAG, "ALL DONE");
                readDone();
            };
            handler.postDelayed(runnable, 7000);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        circuarAnimator.setUpCircularAnimation(view);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentView = null;
    }

    private void initView(View view) {
        background = (RelativeLayout) view.findViewById(R.id.background);
        toolbar = (Toolbar) view.findViewById(R.id.my_toolbar);
        textView = (TextView) view.findViewById(R.id.text);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        initToolbar();
    }

    private void initToolbar() {
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_chevron_left_black_30dp);
            toolbar.setNavigationOnClickListener(event -> getActivity().onBackPressed());
        }
    }

    private void initListeners() {
        // Toolbar navigation is handled in initToolbar()
    }

    private void loadContent() {
        if (article != null) {
            bodyData = getResources().getString(getResources().getIdentifier(article, "string", getContext().getPackageName()));
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                textView.setText(Html.fromHtml(bodyData, Html.FROM_HTML_MODE_LEGACY, new ImageGetter(getContext()), null));
            } else {
                textView.setText(Html.fromHtml(bodyData, new ImageGetter(getContext()), null));
            }
        }
    }

    private void readDone() {
        if (article != null) {
            file.writeToFile("reading.txt", " " + article);
        }
    }

    @Override
    public RelativeLayout getMainBackground() {
        return background;
    }

    @Override
    public ImageView[] getCardsBackgrounds() {
        return new ImageView[]{};
    }

    @Override
    public ImageView getSettingIcon() {
        return null;
    }

    @Override
    public ImageView getBackIcon() {
        // Return null since we're using Toolbar navigation
        return null;
    }

    @Override
    public androidx.cardview.widget.CardView[] getCards() {
        return new androidx.cardview.widget.CardView[]{};
    }

    public void setUpDarkTheme(boolean cheskRes, Context context) {
        if (cheskRes) {
            new DarkThemeCreator(this, getContext()).setUpDarkThem();
            if (background != null) {
                background.setBackground(getResources().getDrawable(R.drawable.dt_gradient));
            }
            if (toolbar != null) {
                toolbar.setNavigationIcon(R.drawable.ic_dchevron_left_black_24dp);
            }
        }
    }

    @Override
    public void endAnimation(FragmentNavigator fragmentNavigator) {
        View view = fragmentView != null ? fragmentView : getView();
        if (view != null) {
            circuarAnimator.closeFragmentAnimation(view, fragmentNavigator);
        } else {
            // If view is null, just navigate without animation
            fragmentNavigator.goToAnotherFragment();
        }
    }
}

