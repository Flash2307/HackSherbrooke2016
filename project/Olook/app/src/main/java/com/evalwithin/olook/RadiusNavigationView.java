package com.evalwithin.olook;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.design.widget.NavigationView;
import android.util.AttributeSet;

/**
 * Created by Frederik on 4/23/2016.
 */
public class RadiusNavigationView extends NavigationView {
    public RadiusNavigationView(Context context) {
        super(context);
    }

    public RadiusNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        setWillNotCacheDrawing(false);
        //invalidate();
    }

    public RadiusNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int w, int h) {
        setMeasuredDimension(800, 600);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Y SE PASSE FUCK ALL !!!!

        super.onDraw(canvas);
        // your custom drawing
        Paint p = new Paint();
        p.setColor(Color.BLUE);
        p.setStrokeWidth(4.5F);
        canvas.drawRect(0, 0, 600, 600, p);
    }
}
