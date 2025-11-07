package com.plstudio.a123.vfv.view;


import android.view.View;
import android.view.ViewGroup;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ViewUtils {


    public static void setUpWindowInsets(View view) {
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBarsInsets = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                            | WindowInsetsCompat.Type.displayCutout()
            );

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.topMargin = systemBarsInsets.top;
            v.setLayoutParams(params);

            return WindowInsetsCompat.CONSUMED;
        });
    }

    public static void setBottomWindowInsetMargin(View view) {
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.bottomMargin = systemBarsInsets.bottom;
            v.setLayoutParams(params);

            return WindowInsetsCompat.CONSUMED;
        });
    }

    public static void setBottomWindowInsetPadding(View view) {
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            view.setPadding(0, 0, 0, systemBarsInsets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
    }
}