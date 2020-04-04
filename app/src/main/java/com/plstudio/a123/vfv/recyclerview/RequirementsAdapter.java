package com.plstudio.a123.vfv.recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.plstudio.a123.vfv.R;
import com.plstudio.a123.vfv.model.Requirement;

import java.util.List;

public class RequirementsAdapter extends RecyclerView.Adapter<RequirementHolder> {
    private Context context;
    private List<Requirement> mRequirements;
    private RecyclerViewListener listener;
    private String done;
    public RequirementsAdapter(Context context, List<Requirement> requirements,
                               RecyclerViewListener listener, String done) {
        this.context = context;
        mRequirements = requirements;
        this.listener = listener;
        this.done = done;

    }
    @NonNull
    @Override
    public RequirementHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i ) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view;
        if(done.equals("0"))
            view = layoutInflater.inflate(R.layout.requirement_row, viewGroup, false);
        else
            view = layoutInflater.inflate(R.layout.requirement_done_row, viewGroup, false);
        return new RequirementHolder(view, context, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull RequirementHolder requirementHolder, int i) {
        Requirement requirement = mRequirements.get(i);

        requirementHolder.bindRequirement(requirement);
    }

    public void setRequirements(List<Requirement> requirements){
        mRequirements = requirements;
    }

    @Override
    public int getItemCount() {
        return mRequirements.size();
    }
}
