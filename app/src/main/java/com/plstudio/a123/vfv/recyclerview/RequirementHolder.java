package com.plstudio.a123.vfv.recyclerview;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.plstudio.a123.vfv.R;
import com.plstudio.a123.vfv.model.Requirement;

public class RequirementHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private ImageView img;
    private TextView name;
    private TextView res;
    private CheckBox status;
    private Context context;
    private RecyclerViewListener listener;

    private Requirement mRequirement;
    public RequirementHolder(@NonNull View itemView, Context context, RecyclerViewListener listener) {
        super(itemView);

        this.context = context;
        img = itemView.findViewById(R.id.requirements_image);
        name = itemView.findViewById(R.id.rqu_name);
        res = itemView.findViewById(R.id.rqu_result);
        status = itemView.findViewById(R.id.status);
        this.listener = listener;


        itemView.setOnClickListener(this);
        status.setOnClickListener(this);
    }

    public  void bindRequirement(Requirement requirement){
        mRequirement = requirement;
        if(mRequirement!=null) {
            try {
                img.setImageResource(context.getResources().getIdentifier(mRequirement.getImg(),
                        "drawable", context.getPackageName()));
            } catch (Exception e) {
                Log.d("IMG", mRequirement.getImg());
            }
            name.setText(mRequirement.getName().replace("_", " "));
            res.setText(mRequirement.getCurrent_a());

            if(mRequirement.getStatus().equals("1"))
                status.setChecked(true);
            else
                status.setChecked(false);
        }
    }
    @Override
    public void onClick(View view) {
        listener.onClick(getAdapterPosition(), mRequirement);
        status.setChecked(true);
    }
}
