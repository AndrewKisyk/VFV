package com.plstudio.a123.vfv.animation;

import com.plstudio.a123.vfv.interfaces.FragmentNavigator;

@FunctionalInterface
public interface FragmentCloseAnimation {
    void EndAnimation(FragmentNavigator fragmentNavigator);
}
