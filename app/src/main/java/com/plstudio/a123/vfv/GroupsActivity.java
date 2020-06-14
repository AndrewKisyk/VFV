package com.plstudio.a123.vfv;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import android.widget.TextView;

import com.plstudio.a123.vfv.model.RequirementsLab;
import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;
import com.plstudio.a123.vfv.model.User;




public class GroupsActivity extends AppCompatActivity {
    private ImageView back, setting;
    private FlowingDrawer mDrawer;
    private CardView title_card;

    private CardView g_1, g_2, g_3, g_4, g_5;
    private TextView g1_counter, g2_counter, g3_counter, g4_counter, g5_counter, all_counter;
    private RelativeLayout background;
    private RequirementsLab requirementsLab;
    private SharedPreferences mSettings;
    private LinearLayout layout;

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_THEME = "theme";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if(checkDarkTheme())
            setTheme(R.style.darktheme);
        else
            setTheme(R.style.MainTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        initView();
        initListeners();
        initRusultsCounters();
        setupResult();

        if(checkDarkTheme())
            setupDarkTheme();
        cardsAnimation();
    }

    @Override
    protected void onResume(){
        super.onResume();
        initRusultsCounters();
        setupResult();
        cardsAnimation();
    }

    private void cardsAnimation(){
        CardAnimator c = new CardAnimator();
        c.cardMainAnim(title_card);
        c.cardAnim(g_5, 0);
        c.cardAnim(g_4, 100);
        c.cardAnim(g_3, 200);
        c.cardAnim(g_2, 300);
        c.cardAnim(g_1, 400);
    }



    private void initView(){
        layout = (LinearLayout) findViewById(R.id.linearLayout);
        mDrawer = (FlowingDrawer) findViewById(R.id.drawerlayout);
        setting = findViewById(R.id.settings);
        mDrawer.setTouchMode(ElasticDrawer.TOUCH_MODE_BEZEL);
        background = (RelativeLayout) findViewById(R.id.background);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawer.toggleMenu();
            }
        });

        back = findViewById(R.id.back);
        setting = findViewById(R.id.settings);

        title_card = (CardView) findViewById(R.id.card_view);
        g_1 = (CardView) findViewById(R.id.g_1);
        g_2 = (CardView) findViewById(R.id.g_2);
        g_3 = (CardView) findViewById(R.id.g_3);
        g_4 = (CardView) findViewById(R.id.g_4);
        g_5 = (CardView) findViewById(R.id.g_5);


        setupMenu();
    }
    @Override
    public void onBackPressed() {
        if (mDrawer.isMenuVisible()) {
            mDrawer.closeMenu();
        } else {
            super.onBackPressed();
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

    private void initListeners(){
        back.setOnClickListener(view -> onBackPressed());

        g_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),RequirementsActivity.class);
                intent.putExtra("GROUP", "1");
                intent.putExtra("MIN", "2");
                startActivity(intent);
            }
        });

        g_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),RequirementsActivity.class);
                intent.putExtra("GROUP", "2");
                intent.putExtra("MIN", "2");
                startActivity(intent);
            }
        });

        g_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),RequirementsActivity.class);
                intent.putExtra("GROUP", "3");
                intent.putExtra("MIN", "2");
                startActivity(intent);
            }
        });

        g_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),RequirementsActivity.class);
                intent.putExtra("GROUP", "4");
                intent.putExtra("MIN", "3");
                startActivity(intent);
            }
        });

        g_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),RequirementsActivity.class);
                intent.putExtra("GROUP", "5");
                intent.putExtra("MIN", "3");
                startActivity(intent);
            }
        });
    }

    private void initRusultsCounters(){
        g1_counter = (TextView) findViewById(R.id.g1_counter);
        g2_counter = (TextView) findViewById(R.id.g2_counter);
        g3_counter = (TextView) findViewById(R.id.g3_counter);
        g4_counter = (TextView) findViewById(R.id.g4_counter);
        g5_counter = (TextView) findViewById(R.id.g5_counter);

        all_counter = (TextView) findViewById(R.id.bottom);
    }

    private void setupResult(){
        initListeners();
        g1_counter.setText(String.valueOf(getGroupResult("1"))+"/2");
        g2_counter.setText(String.valueOf(getGroupResult("2"))+"/2");
        g3_counter.setText(String.valueOf(getGroupResult("3"))+"/2");
        g4_counter.setText(String.valueOf(getGroupResult("4"))+"/3");
        g5_counter.setText(String.valueOf(getGroupResult("5"))+"/3");
        all_counter.setText(String.valueOf("виконано: " + getGroupResult(null))+"/" + User.getUser(new PreferenceUtils(this)).getMax());
    }

    private int getGroupResult(String group){
        requirementsLab = RequirementsLab.get(this);
        if(group != null)
            return requirementsLab.getRequirements(group, "1").size();
        else
            return requirementsLab.getRequirements("1").size();
    }

    private void setupDarkTheme(){

        //icons
        setting.setImageResource(R.drawable.ic_dtsettings_black_24dp);
        back.setImageResource(R.drawable.ic_dchevron_left_black_24dp);

        //backgrounds
        background.setBackground(getResources().getDrawable(R.drawable.dt_gradient));
        g_1.setBackgroundResource(R.drawable.cardvieewcorners);
        g_2.setBackgroundResource(R.drawable.cardvieewcorners);
        g_3.setBackgroundResource(R.drawable.cardvieewcorners);
        g_4.setBackgroundResource(R.drawable.cardvieewcorners);
        g_5.setBackgroundResource(R.drawable.cardvieewcorners);
        ImageView card_back1 = (ImageView) findViewById(R.id.card_back1);
        ImageView card_back2 = (ImageView) findViewById(R.id.card_back2);
        ImageView card_back3 = (ImageView) findViewById(R.id.card_back3);
        ImageView card_back4 = (ImageView) findViewById(R.id.card_back4);
        ImageView card_back5 = (ImageView) findViewById(R.id.card_back5);
        card_back1.setBackground(getResources().getDrawable(R.drawable.dt_cardgrad));
        card_back2.setBackground(getResources().getDrawable(R.drawable.dt_cardgrad));
        card_back3.setBackground(getResources().getDrawable(R.drawable.dt_cardgrad));
        card_back4.setBackground(getResources().getDrawable(R.drawable.dt_cardgrad));
        card_back5.setBackground(getResources().getDrawable(R.drawable.dt_cardgrad));
    }

    private boolean checkDarkTheme(){
        if(mSettings.getString(APP_PREFERENCES_THEME, "theme").equals("dark"))
            return true;
        return false;
    }

}
