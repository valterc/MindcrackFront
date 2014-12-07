package com.vcutils;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

public class AnimatorEngine implements AnimationListener {

	private Activity activity;
	private int animationId;
	private View view;
	private boolean repeat;
	
	private IAnimationStartListener startListener;
	private IAnimationEndListener endListener;
	
	public AnimatorEngine(Activity act, int animationId, int viewId) {
		this(act, animationId, viewId, false);
	}
	
	public AnimatorEngine(Activity act, int animationId, int viewId, boolean repeat) {
		this.activity = act;
		this.animationId = animationId;
		this.view = act.findViewById(viewId);
		this.repeat = repeat;
	}
	
	public AnimatorEngine(Activity act, int animationId, View v) {
		this.activity = act;
		this.animationId = animationId;
		this.view = v;
		this.repeat = false;
	}

	public Animation startAnimation() {
		try{
			Animation animation = AnimationUtils.loadAnimation(this.activity.getApplicationContext(), animationId);
			animation.setAnimationListener(this);
			View animatedView = this.view;
			animatedView.startAnimation(animation);
			return animation;
		}catch(Exception ex){
			this.onAnimationEnd(null);
			return null;
		}
	}

	public void stopAnimation(){
		this.repeat = false;
	}
	
	@Override
	public void onAnimationStart(Animation animation) {
		if (startListener != null)
			startListener.onAnimationStart(animation);
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if (animation == null)
			return;
		
		if (endListener != null)
			endListener.onAnimationEnd(animation);
		
		if (this.repeat == true)
			this.startAnimation();
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
	}
	
	

	public void setStartListener(IAnimationStartListener startListener) {
		this.startListener = startListener;
	}

	public void setEndListener(IAnimationEndListener endListener) {
		this.endListener = endListener;
	}


	public interface IAnimationStartListener{
		public void onAnimationStart(Animation animation);
	}
	
	public interface IAnimationEndListener{
		public void onAnimationEnd(Animation animation);
	}
	
}

