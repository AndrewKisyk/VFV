package com.plstudio.a123.vfv.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.plstudio.a123.vfv.R;
import com.plstudio.a123.vfv.animation.CircuarAnimator;
import com.plstudio.a123.vfv.animation.FragmentCloseAnimation;
import com.plstudio.a123.vfv.datadriven.PreferenceUtils;
import com.plstudio.a123.vfv.di.App;
import com.plstudio.a123.vfv.helpers.RequirementsLab;
import com.plstudio.a123.vfv.interfaces.FragmentNavigator;
import com.plstudio.a123.vfv.model.Requirement;
import com.plstudio.a123.vfv.recyclerview.RecyclerViewListener;
import com.plstudio.a123.vfv.recyclerview.RequirementsAdapter;
import com.plstudio.a123.vfv.view.ViewUtils;

import java.util.List;

import javax.inject.Inject;

import at.markushi.ui.CircleButton;

public class RequirementsFragment extends Fragment implements FragmentCloseAnimation {
    private static final String ARG_GROUP = "GROUP";
    private static final String ARG_MIN = "MIN";
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_THEME = "theme";

    private String group;
    private String min;
    private ImageView back;
    private View fragmentView;
    
    private RecyclerView mrecyclerview;
    private RequirementsAdapter mAdapter;
    private Dialog dialog_done_requirements;
    private CircleButton add_requi, add_done;
    private RecyclerViewListener recyclerlistener;
    private RecyclerViewListener donerecyclerlistener;
    private List<Requirement> requirements;
    private List<Requirement> done_requirements_list;
    private RequirementsAdapter done_adapter;
    private RecyclerView done_requirements;
    private RequirementsLab requirementsLab;
    private TextView minTextView;
    private TextView done_counter;
    private SharedPreferences mSettings;
    
    @Inject
    CircuarAnimator circuarAnimator;
    @Inject
    PreferenceUtils preferenceUtils;

    public static RequirementsFragment newInstance(String group, String min) {
        RequirementsFragment fragment = new RequirementsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GROUP, group);
        args.putString(ARG_MIN, min);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            group = getArguments().getString(ARG_GROUP);
            min = getArguments().getString(ARG_MIN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rquirements, container, false);
        fragmentView = view;
        App.getComponent().injectRequirementsFragment(this);
        
        mSettings = requireContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        requirementsLab = RequirementsLab.get(requireContext());
        
        initView(view);
        initListeners();
        setupRequirements();
        ShowDoneRequirements();
        setCurrentResult();
        
        // Run layout animation after adapter is set
        if (mAdapter != null && mrecyclerview != null) {
            runLayoutAnimation(mrecyclerview);
        }
        
        if (checkDarkTheme()) {
            setupDarkTheme();
        }
        
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        circuarAnimator.setUpCircularAnimation(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh requirements when returning to the fragment
        if (group != null && mrecyclerview != null) {
            setupRequirements();
            setupDoneRequirements();
        }
    }

    private void initView(View view) {
        dialog_done_requirements = new Dialog(requireContext());
        add_requi = (CircleButton) view.findViewById(R.id.pluss_requi);
        back = (ImageView) view.findViewById(R.id.back);
        done_counter = (TextView) view.findViewById(R.id.req_counter);
        minTextView = (TextView) view.findViewById(R.id.min);
        if (min != null) {
            minTextView.setText(min);
        }

        mrecyclerview = view.findViewById(R.id.requirements_recucler);
        mrecyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));

        TextView title = (TextView) view.findViewById(R.id.title);
        ViewUtils.setUpWindowInsets(back);
        ViewUtils.setUpWindowInsets(title);
        ViewUtils.setBottomWindowInsetMargin(done_counter);
    }

    private void setupRequirements() {
        if (group != null && requirementsLab != null) {
            requirements = requirementsLab.getRequirements(group, "0");
            if (mAdapter == null && mrecyclerview != null) {
                mAdapter = new RequirementsAdapter(requireContext(), requirements, recyclerlistener, "0");
                mrecyclerview.setAdapter(mAdapter);
            } else if (mAdapter != null) {
                mAdapter.setRequirements(requirements);
                mAdapter.notifyDataSetChanged();
            }
            setCurrentResult();
        }
    }

    public void ShowDoneRequirements() {
        dialog_done_requirements.setContentView(R.layout.pop_done_requirements);
        dialog_done_requirements.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_done_requirements.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog_done_requirements.setCancelable(true);
        dialog_done_requirements.setCanceledOnTouchOutside(true);
        dialog_done_requirements.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        
        add_done = (CircleButton) dialog_done_requirements.findViewById(R.id.add);
        add_done.setOnClickListener(view -> {
            dialog_done_requirements.dismiss();
        });

        done_requirements = (RecyclerView) dialog_done_requirements.findViewById(R.id.done_requirements_recycler);
        done_requirements.setLayoutManager(new LinearLayoutManager(requireContext()));
        setupDoneRequirements();
    }

    private void setupDoneRequirements() {
        if (group != null && requirementsLab != null && done_requirements != null) {
            done_requirements_list = requirementsLab.getRequirements(group, "1");
            if (done_adapter == null) {
                done_adapter = new RequirementsAdapter(requireContext(), done_requirements_list, donerecyclerlistener, "1");
                done_requirements.setAdapter(done_adapter);
            } else {
                done_adapter.setRequirements(done_requirements_list);
                done_adapter.notifyDataSetChanged();
            }
            setCurrentResult();
            runLayoutAnimation(done_requirements);
        }
    }

    private void initRecyclerViewListener(int position, Requirement requirement) {
        requirementsLab.doRequirement(requirement, "1");
        try {
            requirements.remove(position);
            mAdapter.notifyItemRemoved(position);
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.e("RequirementsFragment", e.toString());
        }
        requirement.setStatus("1");
        done_requirements_list.add(requirement);

        setupDoneRequirements();
    }

    private void initDoneRecyclerViewListener(int position, Requirement requirement) {
        requirementsLab.doRequirement(requirement, "0");

        try {
            done_requirements_list.remove(position);
            done_adapter.notifyItemRemoved(position);
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.e("RequirementsFragment", e.toString());
        }
        requirement.setStatus("0");
        Log.d("RF", requirement.toString());
        requirements.add(requirement);
        mAdapter.notifyDataSetChanged();
        setCurrentResult();
    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.list_animation);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    private void initListeners() {
        this.recyclerlistener = (position, requirement) -> {
            initRecyclerViewListener(position, requirement);
        };

        this.donerecyclerlistener = (position, requirement) -> {
            initDoneRecyclerViewListener(position, requirement);
        };

        // Big plus button
        add_requi.setOnClickListener(view -> {
            dialog_done_requirements.show();
            runLayoutAnimation(done_requirements);
        });

        back.setOnClickListener(view -> getActivity().onBackPressed());
    }

    private void setCurrentResult() {
        if (done_requirements_list != null)
            done_counter.setText(String.valueOf(done_requirements_list.size()) + '/' + String.valueOf(allRequirementsSize()));
        else
            done_counter.setText("0" + '/' + String.valueOf(allRequirementsSize()));
    }

    private int allRequirementsSize() {
        if (done_requirements_list == null)
            return requirements != null ? requirements.size() : 0;
        if (requirements == null)
            return done_requirements_list.size();
        if (done_requirements_list != null && requirements != null)
            return done_requirements_list.size() + requirements.size();
        return 0;
    }

    private boolean checkDarkTheme() {
        return preferenceUtils.checkDarkThem();
    }

    private void setupDarkTheme() {
        if (back != null) {
            back.setImageResource(R.drawable.ic_dchevron_left_black_24dp);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Dismiss dialog if showing
        if (dialog_done_requirements != null && dialog_done_requirements.isShowing()) {
            dialog_done_requirements.dismiss();
        }
        fragmentView = null;
    }
}