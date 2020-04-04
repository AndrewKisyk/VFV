package com.plstudio.a123.vfv;

import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class CardAnimator {
    public void cardAnim(CardView card, int delay){
        Animation animation = AnimationUtils.loadAnimation(card.getContext(), R.anim.liear_layout_anim);
        animation.setDuration(1000);
        animation.setStartOffset(delay);
        card.setAnimation(animation);
        card.setVisibility(View.VISIBLE);
        card.animate();

        animation.start();
    }

    public void cardMainAnim(CardView card){
        Animation animation = AnimationUtils.loadAnimation(card.getContext(), R.anim.list_item);
        animation.setDuration(1200);
        card.setAnimation(animation);
        card.setVisibility(View.VISIBLE);
        card.animate();

        animation.start();
    }

}
