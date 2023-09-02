package com.plstudio.a123.vfv.animation;

import android.animation.TimeInterpolator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class AnimationVars {
    public static final long GAUGE_ANIMATION_DURATION = 5000;
    public static long CARD_ANIMATION = 520;
    public static final TimeInterpolator GAUGE_ANIMATION_INTERPOLATOR = new DecelerateInterpolator(2);
    public static final ReverseInterpolator REVERSE_INTERPOLATOR = new ReverseInterpolator();
    public static float CLICKX, CLICKY;
    public static int CLICK_COORDS[] = new int[2];
    public static int SCREEN_CENTER_X, SSCREEN_CENTER_Y;
    public static void setAnimationVars(View view){
        CLICKX = view.getWidth();
        CLICKY = view.getHeight();

        view.getLocationOnScreen(AnimationVars.CLICK_COORDS);
        CLICK_COORDS[0] += CLICKX / 2;
        CLICK_COORDS[1] += CLICKY / 2;
    }

}
