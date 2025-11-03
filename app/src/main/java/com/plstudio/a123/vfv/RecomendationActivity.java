package com.plstudio.a123.vfv;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.plstudio.a123.vfv.datadriven.FileIO;
import com.plstudio.a123.vfv.helpers.ImageGetter;

public class RecomendationActivity extends AppCompatActivity {
    private final String DEBUG_TAG = "RECOMEND_LIST: ";
    private String extra;
    Toolbar toolbar;
    String bodyData;

    Handler handler;
    Runnable runnable;
    FileIO file;
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_THEME = "theme";
    private SharedPreferences mSettings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        if(checkDarkTheme())
            setTheme(R.style.darktheme);
        else
            setTheme(R.style.AppTheme_AppBarOverlay);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recomendation);
        ConstraintLayout clContainer = findViewById(R.id.clContainer);
        setUpWindowInsets(clContainer);
        initToolbar();
        file = new FileIO(this, Context.MODE_APPEND);

        TextView textView = findViewById(R.id.text);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        extra = getIntent().getStringExtra("article");

        bodyData= getResources().getString(getResources().getIdentifier(extra, "string", getPackageName()));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(bodyData,  Html.FROM_HTML_MODE_LEGACY, new ImageGetter(this), null));
        } else {
            textView.setText(Html.fromHtml(bodyData, new ImageGetter(this), null));
        }

        if(!getIntent().getBooleanExtra("status", false)) {
            handler = new Handler();
            runnable = () -> {
                Log.d(DEBUG_TAG, "ALL DOne");
                readDone();
            };
            handler.postDelayed(runnable, 7000);
        }

        if(checkDarkTheme())
            setupDarkTheme();


    }
    @Override
    protected void onPause() {
        super.onPause();
        if(handler != null)
            handler.removeCallbacks(runnable);
    }

    private void readDone(){
        file.writeToFile("reading.txt", " "+extra);
    }

    private void initToolbar(){
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_black_30dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setupDarkTheme(){
        //icons
        toolbar.setNavigationIcon(R.drawable.ic_dchevron_left_black_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //backgrounds
        FrameLayout background = (FrameLayout) findViewById(R.id.background);
        background.setBackground(getResources().getDrawable(R.drawable.dt_gradient));
    }

    private boolean checkDarkTheme(){
        if(mSettings.getString(APP_PREFERENCES_THEME, "theme").equals("dark"))
            return true;
        return false;
    }
    private void setUpWindowInsets(View view) {
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets bars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                            | WindowInsetsCompat.Type.displayCutout()
            );
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
    }

}
