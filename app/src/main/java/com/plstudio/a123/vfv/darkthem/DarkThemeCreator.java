package com.plstudio.a123.vfv.darkthem;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.widget.ImageView;

import com.plstudio.a123.vfv.R;

import com.plstudio.a123.vfv.interfaces.ThemeCreatable;

public class DarkThemeCreator {
    ThemeCreatable view;
    Context context;

    public DarkThemeCreator(ThemeCreatable view, Context context){
        this.view = view;
        this.context = context;
    }
    public void setUpDarkThem(){
        setBackground();
        setCardCorners();
        setSettingsIcon();
        setCardBakgrounds();
        setBackIcon();
    }
    private void setBackground(){
        view.getMainBackground().setBackground(context.getResources().getDrawable(R.drawable.dt_gradient));
    }
    private void setCardCorners(){
        for (CardView card: view.getCards()) {
            card.setBackgroundResource(R.drawable.cardvieewcorners);
        }
    }
    private void setCardBakgrounds(){
        for (ImageView card: view.getCardsBackgrounds()) {
            card.setBackground(context.getResources().getDrawable(R.drawable.dt_cardgrad));;
        }
    }
    private void setSettingsIcon(){
        view.getSettingIcon().setImageResource(R.drawable.ic_dtsettings_black_24dp);;
    }
    private void setBackIcon(){
        if(view.getBackIcon() != null)
            view.getBackIcon().setImageResource(R.drawable.ic_dchevron_left_black_24dp);;
    }
}
