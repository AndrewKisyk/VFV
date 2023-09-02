package com.plstudio.a123.vfv;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;
import com.plstudio.a123.vfv.datadriven.FileIO;
import com.plstudio.a123.vfv.fragments.MenuListFragment;


import net.igenius.customcheckbox.CustomCheckBox;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static java.lang.Thread.sleep;


public class RecomendationListActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView back, setting;
    private FlowingDrawer mDrawer;
    private ScrollView scroll;
    private CardView warmup, jump, running, strength, title;
    private HashMap<String,Integer> check_boxes = new HashMap<>();
    private SharedPreferences mSettings;
    private RelativeLayout background;
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_THEME = "theme";
    List<String> readed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        if(checkDarkTheme())
            setTheme(R.style.darktheme);
        else
            setTheme(R.style.MainTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recomendation_list);
        initHash();
        initView();
        readed = Arrays.asList(readStatys().split(" "));
        initListeners();

        if(checkDarkTheme())
            setupDarkTheme();
        animAllCards();
        setTick();
        initNawigationMenu();

    }

    @Override
    protected void onResume() {
        super.onResume();
        readed = Arrays.asList(readStatys().split(" "));
        animAllCards();
        setTick();
    }
    private void animAllCards() {
    /*    CardAnimator c = new CardAnimator(this);
        c.cardMainAnim(title);*/

    }

    public void initListeners(){
        back.setOnClickListener(view -> onBackPressed());
        warmup.setOnClickListener(view -> startRecomendation("cardio"));
        jump.setOnClickListener(view -> startRecomendation("jump"));
        running.setOnClickListener(view -> startRecomendation("running"));
        strength.setOnClickListener(view -> startRecomendation("strength"));
    }

    private void initView(){
        scroll = findViewById(R.id.motion);
        setting = (ImageView) findViewById(R.id.settings);
        back = (ImageView)findViewById(R.id.back);
        warmup = (CardView) findViewById(R.id.warm_up);
        jump = (CardView) findViewById(R.id.jump);
        running = (CardView) findViewById(R.id.run);
        strength = (CardView) findViewById(R.id.strength);
        title = (CardView) findViewById(R.id.card_view);
        background = (RelativeLayout) findViewById(R.id.background);

        //setting eneble checkboxes
        CustomCheckBox toCheck;
        for(int a: check_boxes.values()){
            toCheck = findViewById(a);
            toCheck.setEnabled(false);
        }

    }

    private void initHash(){
        check_boxes.put("cardio", R.id.cardi_check );
        check_boxes.put("jump", R.id.jump_check );
        check_boxes.put("running", R.id.run_check );
        check_boxes.put("strength", R.id.strength_check );
    }

    private void startRecomendation(String title){
        Intent intent = new Intent(RecomendationListActivity.this, RecomendationActivity.class);
        intent.putExtra("article", title);
        if(readed.contains(title))
            intent.putExtra("status", true);
        else
            intent.putExtra("status", false);
        startActivity(intent);
    }

    private void setTick(){
        CustomCheckBox toCheck;
       for(String key: check_boxes.keySet())
            if(readed.contains(key)) {
                toCheck = (CustomCheckBox) findViewById(check_boxes.get(key));
                toCheck.setChecked(true, true);
            }
    }

    private void setupMenu() {
        FragmentManager fm = getSupportFragmentManager();
        MenuListFragment mMenuFragment = (MenuListFragment) fm.findFragmentById(R.id.id_container_menu);
        if (mMenuFragment == null) {
            mMenuFragment = new MenuListFragment();
            fm.beginTransaction().add(R.id.id_container_menu, mMenuFragment).commit();
        }
    }

    private void initNawigationMenu(){
        mDrawer = (FlowingDrawer) findViewById(R.id.drawerlayout);
        mDrawer.setTouchMode(ElasticDrawer.TOUCH_MODE_BEZEL);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawer.toggleMenu();
            }
        });


        setupMenu();
    }

    private String readStatys(){
        return new FileIO(this).readFromFile("reading.txt", true);
    }

    private void setupDarkTheme(){


        //icons
        setting.setImageResource(R.drawable.ic_dtsettings_black_24dp);
        back.setImageResource(R.drawable.ic_dchevron_left_black_24dp);


        //backgrounds
        background.setBackground(getResources().getDrawable(R.drawable.dt_gradient));
        background.setBackground(getResources().getDrawable(R.drawable.dt_gradient));
        running.setBackgroundResource(R.drawable.cardvieewcorners);
        warmup.setBackgroundResource(R.drawable.cardvieewcorners);
        strength.setBackgroundResource(R.drawable.cardvieewcorners);
        jump.setBackgroundResource(R.drawable.cardvieewcorners);
        ImageView card_back1 = (ImageView) findViewById(R.id.card_back1);
        ImageView card_back2 = (ImageView) findViewById(R.id.card_back2);
        ImageView card_back3 = (ImageView) findViewById(R.id.card_back3);
        ImageView card_back4 = (ImageView) findViewById(R.id.card_back4);
        card_back1.setBackground(getResources().getDrawable(R.drawable.dt_cardgrad));
        card_back2.setBackground(getResources().getDrawable(R.drawable.dt_cardgrad));
        card_back3.setBackground(getResources().getDrawable(R.drawable.dt_cardgrad));
        card_back4.setBackground(getResources().getDrawable(R.drawable.dt_cardgrad));

    }
    private boolean checkDarkTheme(){
        if(mSettings.getString(APP_PREFERENCES_THEME, "theme").equals("dark"))
            return true;
        return false;
    }
}
