package com.plstudio.a123.vfv.animation;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Parcelable;
import android.support.constraint.solver.widgets.WidgetContainer;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;

import com.google.auto.value.AutoValue;
import com.plstudio.a123.vfv.interfaces.FragmentNavigator;
import com.plstudio.a123.vfv.interfaces.GroupsContract;

import java.util.function.Consumer;

public class CircuarAnimator {
    Context mcontext;
    int maxRadius;
    public CircuarAnimator(Context context){
        mcontext = context;
        maxRadius = getMaxRadius();
    }

    private int getMaxRadius(){
        Point size = new Point();
        WindowManager wm = (WindowManager) mcontext.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getSize(size);
        return size.y;
    }
    private int[] coordsOfStart(View view){
        int animationViewCoords[] = new int[2];
        view.getLocationOnScreen(animationViewCoords);
        animationViewCoords[0] = AnimationVars.CLICK_COORDS[0] - animationViewCoords[0];
        animationViewCoords[1] =  AnimationVars.CLICK_COORDS[1] - animationViewCoords[1];
        return animationViewCoords;
    }

    public void setUpCircularAnimation(View viewToReveal){
        int animCoords[] = coordsOfStart(viewToReveal);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            ViewAnimationUtils.createCircularReveal(viewToReveal, animCoords[0], animCoords[1], 0, maxRadius).start();
    }

    public void closeFragmentAnimation(View view, FragmentNavigator backEvent){
        int animCoords[] = coordsOfStart(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Animator animator = ViewAnimationUtils.createCircularReveal(view, animCoords[0], animCoords[1], 0, maxRadius);
            animator.setInterpolator(AnimationVars.REVERSE_INTERPOLATOR);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(View.INVISIBLE);
                    backEvent.goToAnotherFragment();

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animator.start();
        }
    }
}

