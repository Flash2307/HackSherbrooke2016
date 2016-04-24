package com.evalwithin.olook;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class Compass extends View {

    private float direction = 0;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private boolean firstDraw;
    private RectF dstNeedle = new RectF();

    private float angles[] = new float[5];
    private int index;

    public Compass(Context context) {
        super(context);
        init();
    }

    public Compass(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Compass(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(5);
        paint.setColor(Color.WHITE);
        paint.setTextSize(30);

        firstDraw = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int cxCompass = getMeasuredWidth()/2;
        int cyCompass = getMeasuredHeight()/2;
        float radiusCompass;

        if(cxCompass > cyCompass){
            radiusCompass = (float) (cyCompass * 0.9);
        }
        else{
            radiusCompass = (float) (cxCompass * 0.9);
        }
        canvas.drawCircle(cxCompass, cyCompass, radiusCompass, paint);

        dstNeedle.set(cxCompass-10, (int)(cyCompass - radiusCompass), cxCompass+10, cyCompass);

        if(!firstDraw){

            angles[index] = (float)(Math.toDegrees(direction) + 360) % 360;
            index++;
            if(index >= 5)
                index = 0;

            float angle = 0;
            for (int i =0; i < 5; i++)
                angle += angles[i];
            angle /= 5;

            paint.setColor(Color.RED);

            canvas.save();
            canvas.rotate(-angle, cxCompass, cyCompass);
            canvas.drawOval(dstNeedle, paint);
            canvas.restore();

            paint.setColor(Color.WHITE);
        }

    }

    public void updateDirection(float dir)
    {
        firstDraw = false;
        direction = dir;
        invalidate();
    }
}
