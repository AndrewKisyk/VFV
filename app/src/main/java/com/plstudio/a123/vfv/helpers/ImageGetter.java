package com.plstudio.a123.vfv.helpers;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

public class ImageGetter implements Html.ImageGetter {
    private  Context context;

    public ImageGetter(Context context){
        this.context = context;
    }
    public Drawable getDrawable(String source) {
        int id;

        id = context.getResources().getIdentifier(source, "drawable", context.getPackageName());

        if (id == 0) {
            id = context.getResources().getIdentifier(source, "drawable", "android");
            Log.d("ImmageGetter","the drawable resource wasn't found in our package, maybe it is a stock android drawable?");
        }

        if (id == 0) {
            Log.d("ImmageGetter","prevent a crash if the resource still can't be found");
            return null;
        }
        else {
            Drawable d = context.getResources().getDrawable(id);
            d.setBounds(0,0,getWidth(), getScreenHeight()/3);

            return d;
        }
    }
   private int getWidth(){
       return Resources.getSystem().getDisplayMetrics().widthPixels - 32;
   }
   private static int getScreenHeight()  {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }


}
