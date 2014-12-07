package com.vcutils.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

/**
 * Created by Valter on 23/05/2014.
 */
public class PixelImageView extends ImageView {

    private int realWidth;
    private int realHeight;
    private int lastImageResource;

    public PixelImageView(Context context) {
        super(context);
        ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener(){
            @Override
            public boolean onPreDraw() {
                ViewTreeObserver vto = getViewTreeObserver();
                vto.removeOnPreDrawListener(this);
                realWidth = getMeasuredHeight();
                realHeight = getMeasuredWidth();

                setImageResource(lastImageResource);

                return true;
            }
        });
    }

    public PixelImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener(){
            @Override
            public boolean onPreDraw() {
                ViewTreeObserver vto = getViewTreeObserver();
                vto.removeOnPreDrawListener(this);
                realWidth = getMeasuredHeight();
                realHeight = getMeasuredWidth();

                setImageResource(lastImageResource);

                return true;
            }
        });
    }

    public PixelImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener(){
            @Override
            public boolean onPreDraw() {
                ViewTreeObserver vto = getViewTreeObserver();
                vto.removeOnPreDrawListener(this);
                realWidth = getMeasuredHeight();
                realHeight = getMeasuredWidth();

                setImageResource(lastImageResource);

                return true;
            }
        });
    }


    @Override
    public void setImageResource(int resId) {

        lastImageResource = resId;

        if (realHeight <= 0 || realWidth <= 0) {
            super.setImageResource(resId);
            return;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        Bitmap bmp = BitmapFactory.decodeResource(this.getResources(), resId, options);

        if (bmp == null)
            return;

        float scaleWidth = realWidth / (float)bmp.getWidth();
        float scaleHeight = realHeight / (float)bmp.getHeight();

        float scale = scaleWidth > scaleHeight ? scaleWidth : scaleHeight;

        float finalWidth = bmp.getWidth() * scale;
        float finalHeight = bmp.getHeight() * scale;

        Bitmap scaledbmp = Bitmap.createScaledBitmap(bmp, (int)finalWidth, (int)finalHeight, false);
        bmp.recycle();
        bmp = null;

        setImageBitmap(scaledbmp);
    }
}
