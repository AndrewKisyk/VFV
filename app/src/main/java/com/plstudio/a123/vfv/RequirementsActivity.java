package com.plstudio.a123.vfv;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;

import com.plstudio.a123.vfv.model.Requirement;
import com.plstudio.a123.vfv.helpers.RequirementsLab;
import com.plstudio.a123.vfv.recyclerview.RecyclerViewListener;
import com.plstudio.a123.vfv.recyclerview.RequirementsAdapter;

import java.util.List;

import at.markushi.ui.CircleButton;

public class RequirementsActivity extends AppCompatActivity {

    private RecyclerView mrecyclerview;
    private RequirementsAdapter mAdapter;
    private String group;
    private Dialog dialog_done_requirements;
    private CircleButton add_requi, add_done;
    private RecyclerViewListener recyclerlistener;
    private RecyclerViewListener donerecyclerlistener;
    private List<Requirement> requirements;
    private List<Requirement> done_requirements_list;
    private RequirementsAdapter done_adapter;
    private RecyclerView done_requirements;
    private RequirementsLab requirementsLab;
    private ImageView back;

    private TextView min;
    private TextView done_counter;

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_THEME = "theme";
    private SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        if(checkDarkTheme())
            setTheme(R.style.darktheme);
        else
            setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requirements);

        initView();

        initListeners();
        setupRequirements();
        ShowDoneRequirements();
        setCurrentResult();
        runLayoutAnimation(mrecyclerview);
    }

    private void initView(){
        dialog_done_requirements = new Dialog(this);
        add_requi = (CircleButton) findViewById(R.id.pluss_requi);

        group = getIntent().getStringExtra("GROUP");

        back = (ImageView) findViewById(R.id.back);
        if(checkDarkTheme())
            back.setImageResource(R.drawable.ic_dchevron_left_black_24dp);


        done_counter = (TextView)findViewById(R.id.req_counter);
        min = (TextView)findViewById(R.id.min);
        min.setText(getIntent().getStringExtra("MIN"));

        mrecyclerview = findViewById(R.id.requirements_recucler);
        mrecyclerview.setLayoutManager(new LinearLayoutManager(this));

        requirementsLab = RequirementsLab.get(this);
    }

    private void setupRequirements(){
        requirementsLab = RequirementsLab.get(this);
        requirements = requirementsLab.getRequirements(group, "0");
        if(mAdapter == null) {
            mAdapter = new RequirementsAdapter(this, requirements, recyclerlistener, "0");
            mrecyclerview.setAdapter(mAdapter);
        } else {
            mAdapter.setRequirements(requirements);
            mAdapter.notifyDataSetChanged();
        }
        setCurrentResult();
    }

    public void ShowDoneRequirements(){
        dialog_done_requirements.setContentView(R.layout.pop_done_requirements);


        dialog_done_requirements.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_done_requirements.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        dialog_done_requirements.setCancelable(true);
        dialog_done_requirements.setCanceledOnTouchOutside(true);
        dialog_done_requirements.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        add_done = (CircleButton) dialog_done_requirements.findViewById(R.id.add);
        add_done.setOnClickListener(view -> {
            dialog_done_requirements.dismiss();
        });


        done_requirements = (RecyclerView) dialog_done_requirements.findViewById(R.id.done_requirements_recycler);
        done_requirements.setLayoutManager(new LinearLayoutManager(this));
        setupDoneRequirements();

    }

    private void setupDoneRequirements(){
        requirementsLab = RequirementsLab.get(this);
        done_requirements_list = requirementsLab.getRequirements(group, "1");
        if(done_adapter == null){
            done_adapter = new RequirementsAdapter(this, done_requirements_list, donerecyclerlistener, "1");
            done_requirements.setAdapter(done_adapter);
        } else {
            done_adapter.setRequirements(done_requirements_list);
            done_adapter.notifyDataSetChanged();
        }
        setCurrentResult();
        runLayoutAnimation(done_requirements);
    }

    private void initRecyclerViewListener(int position, Requirement requirement){
        requirementsLab = RequirementsLab.get(this);
        requirementsLab.doRequirement(requirement, "1");
        try {
            requirements.remove(position);
            mAdapter.notifyItemRemoved(position);
        } catch (ArrayIndexOutOfBoundsException e){
            Log.e("RequirementsActivity", e.toString());
        }
        requirement.setStatus("1");
        done_requirements_list.add(requirement);

        setupDoneRequirements();
    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.list_animation);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    private void initDoneRecyclerViewListener(int position, Requirement requirement){
        requirementsLab = RequirementsLab.get(this);
        requirementsLab.doRequirement(requirement, "0");

        try {
            done_requirements_list.remove(position);
            done_adapter.notifyItemRemoved(position);
        } catch (ArrayIndexOutOfBoundsException e){
            Log.e("RequirementsActivity", e.toString());
        }
        requirement.setStatus("0");
        Log.d("RA", requirement.toString());
        requirements.add(requirement);
        mAdapter.notifyDataSetChanged();
        setCurrentResult();
    }

    private void initListeners(){
        this.recyclerlistener = (position, requirement) -> {
            initRecyclerViewListener(position, requirement);
        };

        this.donerecyclerlistener = (position, requirement) -> {
            initDoneRecyclerViewListener(position, requirement);
        };

        //Big pluss button
        add_requi.setOnClickListener(view -> {
            dialog_done_requirements.show();
            runLayoutAnimation(done_requirements);
        });


        back.setOnClickListener(view -> onBackPressed());
    }

    private void setCurrentResult(){
        if(done_requirements_list != null)
            done_counter.setText(String.valueOf(done_requirements_list.size()) + '/' + String.valueOf(allRequirementsSize()));
        else
            done_counter.setText("0" + '/' + String.valueOf(allRequirementsSize()));
    }

    private int allRequirementsSize(){
        if(done_requirements_list == null)
            return requirements.size();
        if(requirements == null)
            return done_requirements_list.size();
        if(done_requirements_list != null && requirements != null)
            return done_requirements_list.size() + requirements.size();
        return 0;
    }

    private boolean checkDarkTheme(){
        if(mSettings.getString(APP_PREFERENCES_THEME, "theme").equals("dark"))
            return true;
        return false;
    }

}
