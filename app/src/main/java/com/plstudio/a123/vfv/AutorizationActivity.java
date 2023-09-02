package com.plstudio.a123.vfv;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.plstudio.a123.vfv.datadriven.PreferenceUtils;
import com.plstudio.a123.vfv.di.App;
import com.plstudio.a123.vfv.interfaces.UserContract;

import com.plstudio.a123.vfv.presenters.AutorizationPresenter;


import javax.inject.Inject;


public class AutorizationActivity extends AppCompatActivity implements UserContract.View {
    private EditText age;
    private RadioButton male;
    private RadioButton female;
    private Button next;
    private ConstraintLayout container;
    private AnimationDrawable anim;
    private AutorizationPresenter presenter;
    @Inject
    PreferenceUtils preferenceUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.autoriztion_activity);
        App.getComponent().injectAutoriztionActivity(this);
        initView();
        setupAnimation();

        presenter = new AutorizationPresenter(this, preferenceUtils);
        presenter.init();

        initListeners();
    }

    public void initView(){
        container = (ConstraintLayout) findViewById(R.id.main_container);
        age = (EditText) findViewById(R.id.age);
        male = (RadioButton) findViewById(R.id.radio_male);
        female = (RadioButton) findViewById(R.id.radio_fmale);
        next = (Button) findViewById(R.id.validate);
        container = (ConstraintLayout) findViewById(R.id.main_container);
        next = (Button) findViewById(R.id.validate);
    }

    public void initListeners(){
        next.setOnClickListener(event ->{
            presenter.logIn();
        });
    }

    @Override
    public boolean isMale() {
        return male.isChecked();
    }

    @Override
    public boolean isFmale() {
        return female.isChecked();
    }

    @Override
    public String getInAge() {
        return age.getText().toString();
    }

    @Override
    public void showAgeError() {
        age.setError(getResources().getString(R.string.ageError));
    }

    @Override
    public void showSexError() {
        male.setError(getResources().getString(R.string.sexError));
    }

    @Override
    public void next() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void close() {
        finish();
    }


    public void setupAnimation(){
        anim = (AnimationDrawable) container.getBackground();
        anim.setEnterFadeDuration(100);
        anim.setExitFadeDuration(2000);
    }
}
