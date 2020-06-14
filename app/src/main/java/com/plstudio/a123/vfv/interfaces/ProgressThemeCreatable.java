package com.plstudio.a123.vfv.interfaces;

import android.widget.ImageView;
import android.widget.ProgressBar;

import com.plstudio.a123.vfv.view.StepsView;

public interface ProgressThemeCreatable extends ThemeCreatable{
     StepsView getStepsView();
     ProgressBar[] getCircleProgress();
     ImageView getTitleImage();
}
