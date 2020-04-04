package com.plstudio.a123.vfv.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ProgressBar;

import com.plstudio.a123.vfv.R;

public class StepsView extends ProgressBar {

    private Paint _paint = new Paint();
    private float steps[];
    private int arr_img[];
    private int progColor = R.color.silver;
    private int backColor = R.color.white;
    private Bitmap temp_img;


    public StepsView(Context context) {
        super(context);
        init(null ,0);
    }
    public StepsView(Context context, AttributeSet attrs){
        super(context, attrs);
        init(attrs, 0);
    }

    public StepsView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle){
        _paint.setColor(getResources().getColor(progColor));
        _paint.setAntiAlias(true);
        _paint.setTextSize(16 * getResources().getDisplayMetrics().density);
    }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        float cx = getWidth();
        float cy = getHeight();
        float radius = getHeight()/6;

        float totalX;
        int progress = getProgress();
       // _paint.setFlags(Paint. UNDERLINE_TEXT_FLAG);
        canvas.drawText(new Integer(progress).toString(), 25, cy/2, _paint );
        if(progress < 100)
            canvas.drawLine(20, cy/2+8, 70, cy/2+8, _paint);
        else
            canvas.drawLine(20, cy/2+8, 85, cy/2+8, _paint);
        float temp_x;
        if(steps != null && arr_img != null ){
            for(int i = 0; i < steps.length; i++){
                temp_x = cx * steps[i];
                if(steps[i] == 1) {
                    totalX = temp_x - 30 ;
                } else {
                    totalX = temp_x;
                }

               if( new Float(progress)/100 >= steps[i] )
                   _paint.setColor(getResources().getColor(progColor));
               else
                   _paint.setColor(getResources().getColor(backColor));
               if(steps[i]!=1)
                    canvas.drawCircle(totalX-convertDpToPixel(20, getContext())+radius, cy-radius , radius, _paint);
               else
                   canvas.drawCircle(totalX-convertDpToPixel(5, getContext()), cy-radius , radius, _paint);

                if(i < arr_img.length){
                    temp_img = BitmapFactory.decodeResource(getResources(), arr_img[i]);
                    if(steps[i]!=1)
                        canvas.drawBitmap(temp_img, totalX-temp_img.getWidth()/2-convertDpToPixel(20, getContext())+radius, 0, _paint);
                    else
                        canvas.drawBitmap(temp_img, totalX-temp_img.getWidth()/2-convertDpToPixel(5, getContext()), 0, _paint);

                }

            }

        }
    }

    public static float convertPixelsToDp(float px, Context context){
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public void setSteps(float arr[], int img[]){
        steps = arr;
        arr_img = img;
    }

    public void setProgColor(int progColor) {
        this.progColor = progColor;
    }

    public void setBackColor(int backColor) {
        this.backColor = backColor;
    }



}
