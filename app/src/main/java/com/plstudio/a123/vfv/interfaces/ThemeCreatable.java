package com.plstudio.a123.vfv.interfaces;

import androidx.cardview.widget.CardView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public interface ThemeCreatable {
    RelativeLayout getMainBackground();
    ImageView[] getCardsBackgrounds();
    ImageView getSettingIcon();
    ImageView getBackIcon();
    CardView[] getCards();

}
