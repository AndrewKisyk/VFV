package com.plstudio.a123.vfv.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.plstudio.a123.vfv.R;
import com.plstudio.a123.vfv.animation.CircuarAnimator;
import com.plstudio.a123.vfv.animation.FragmentCloseAnimation;
import com.plstudio.a123.vfv.di.App;
import com.plstudio.a123.vfv.interfaces.FragmentNavigator;

import javax.inject.Inject;


public class RequirementsFragment extends Fragment implements FragmentCloseAnimation {
    private ImageView back;
    @Inject
    CircuarAnimator circuarAnimator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rquirements, container, false);
        App.getComponent().injectRequirementsFragment(this);
        back = view.findViewById(R.id.back);
        FragmentManager fm = getFragmentManager();
      //  back.setOnClickListener(event -> fm.beginTransaction().remove(this).commit());
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        circuarAnimator.setUpCircularAnimation(view);
    }

    @Override
    public void endAnimation(FragmentNavigator fragmentNavigator) {
     circuarAnimator.closeFragmentAnimation(getView(), fragmentNavigator);

    }
}