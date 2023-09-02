package com.plstudio.a123.vfv.animation;

import android.content.Context;

import android.support.v7.widget.CardView;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.plstudio.a123.vfv.R;
import com.plstudio.a123.vfv.interfaces.FragmentNavigator;

public class CardAnimator {
    Context mcontext;
    ReverseInterpolator interpolator = new ReverseInterpolator();
    public CardAnimator(Context context){
        mcontext = context;
    }

    public void animCardList(CardView carads[]){
        accepAllCards(carads, R.anim.liear_layout_anim);
    }

    public void animDisableCardList(CardView cards[]){
        int delay = 0;
        Animation animation;

        for(CardView card: cards){
            animation= AnimationUtils.loadAnimation(mcontext, R.anim.liear_layout_anim);
            animation.setInterpolator(interpolator);
            cardAnim(card, delay, animation);
            delay+=100;
        }
    }
    private void accepAllCards(CardView carads[], int resourses){
        int delay = 0;
        Animation animation;
        for(CardView card: carads){
            animation= AnimationUtils.loadAnimation(mcontext, resourses);
            cardAnim(card, delay,animation);
            delay += 100;
        }

    }
    private void cardAnim(CardView card, int delay, Animation animation){
        animation.setDuration(500);
        animation.setStartOffset(delay);
        card.startAnimation(animation);
        card.setVisibility(View.VISIBLE);
    }

    public void cardMainAnim(CardView card){
        Animation animation = AnimationUtils.loadAnimation(mcontext, R.anim.list_item);
        animation.setDuration(AnimationVars.CARD_ANIMATION);
        card.setAnimation(animation);
        card.setVisibility(View.VISIBLE);
        card.animate();
        animation.start();
        AnimationVars.CARD_ANIMATION = 520;
    }
    public void disableMainAnim(CardView card, FragmentNavigator action){
        Animation animation = AnimationUtils.loadAnimation(mcontext, R.anim.list_item);
        animation.setDuration(AnimationVars.CARD_ANIMATION);
        animation.setInterpolator(new ReverseInterpolator());
        card.setAnimation(animation);
        card.setVisibility(View.INVISIBLE);
        card.animate();
        animation.start();
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                 action.goToAnotherFragment();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        AnimationVars.CARD_ANIMATION = 520;
    }

    public void startCardFragment(CardView main, CardView carads[]){
        cardMainAnim(main);
        animCardList(carads);
    }

    public void endCardFragment(CardView main, CardView cards[], FragmentNavigator fragmentNavigator){
        AnimationVars.CARD_ANIMATION +=(cards.length-1)*100;
        disableMainAnim(main, fragmentNavigator);
        animDisableCardList(cards);
    }
}
