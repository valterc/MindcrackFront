package com.vcutils.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class XImageView extends ImageView {

	public XImageView(Context context) {
		super(context);
	}
	
	public XImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public XImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	@Override
	public void setPressed(boolean pressed) {
		if (pressed && getParent() instanceof View && ((View)getParent()).isPressed()) {
            return;
        }
        super.setPressed(pressed);
	}

}
