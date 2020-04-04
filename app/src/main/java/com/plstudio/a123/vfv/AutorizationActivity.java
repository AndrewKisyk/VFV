package com.plstudio.a123.vfv;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.plstudio.a123.vfv.datadriven.FileIO;
import com.plstudio.a123.vfv.model.User;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class AutorizationActivity extends AppCompatActivity {
    private EditText age;
    private RadioButton male;
    private RadioButton female;
    private Button next;
    private ConstraintLayout container;
    private AnimationDrawable anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.autoriztion_activity);

        checkUserData();

        container = (ConstraintLayout) findViewById(R.id.main_container);
        age = (EditText) findViewById(R.id.age);
        male = (RadioButton) findViewById(R.id.radio_male);
        female = (RadioButton) findViewById(R.id.radio_fmale);
        next = (Button) findViewById(R.id.validate);

        initListeners(this);
        setupAnimation();
    }

    private void initListeners(final Context context){
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { if(validate()){

                    new FileIO(context, Context.MODE_PRIVATE).writeToFile("user.txt", age.getText()+getSex());
                    Intent intent = new Intent(view.getContext(), MainActivity.class);
                    User user = User.getUser(view.getContext());
                    user.userToNull();
                    startActivity(intent);
                }
            }
        });
    }

    private String getSex(){
        if(male.isChecked())
            return "m";
        else
            return "f";
    }

    public boolean validate(){

        try{
            String s  = age.getText().toString();
            int a = Integer.parseInt(s);
            if(s.isEmpty() || a > 17 || a < 12) throw new NumberFormatException();


        } catch (NumberFormatException e){
            age.setError("Введіть число: 12-17");
            return false;
        } finally {
            if(!(male.isChecked() || female.isChecked())){
                male.setError("Оберіть стать");
                female.setError("Оберіть стать");
                return false;
            }

        }

        return true;
    }

    public void setupAnimation(){
        anim = (AnimationDrawable) container.getBackground();
        anim.setEnterFadeDuration(100);
        anim.setExitFadeDuration(2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (anim != null && !anim.isRunning())
            anim.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (anim != null && anim.isRunning())
            anim.stop();
    }

    private void checkUserData(){
        File f = getFileStreamPath("user.txt");
        if (f.length() == 0) {
            return;
        } else {
           Intent intent = new Intent(this, MainActivity.class);
           startActivity(intent);
           finish();
        }
    }

    private void writeToFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("user.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}
