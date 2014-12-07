package com.vcutils.animation;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;


public class Transition3d implements AnimationListener {
    
	private ITransition3DListener listener;
	private View mView1;
	private View mView2;
	private ViewGroup mContainer;
	private boolean mTransitionRunning;
	
    public Transition3d(View view1, View view2, ViewGroup container) {
		super();
		this.mView1 = view1;
		this.mView2 = view2;
		this.mContainer = container;
		
		this.mTransitionRunning = false;
		this.mContainer.setPersistentDrawingCache(ViewGroup.PERSISTENT_ANIMATION_CACHE);
	}

	/**
     * Setup a new 3D rotation on the container view.
     *
     * @param position the item that was clicked to show a picture, or -1 to show the list
     * @param start the start angle at which the rotation must begin
     * @param end the end angle of the rotation
     */
    private void applyRotation(boolean removeView1, float start, float end) {
        // Find the center of the container
        final float centerX = mContainer.getWidth() / 2.0f;
        final float centerY = mContainer.getHeight() / 2.0f;

        // Create a new 3D rotation with the supplied parameter
        // The animation listener is used to trigger the next animation
        final Rotate3dAnimation rotation =
                new Rotate3dAnimation(start, end, centerX, centerY, 310.0f, true);
        rotation.setDuration(300);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new DisplayNextView(mView1, mView2, removeView1));

        mContainer.startAnimation(rotation);
    }

    public void toggleViews(){
    	if (mTransitionRunning)
    		return;
    	
    	if (mView1.getVisibility() == View.VISIBLE)
    		showSecondView();
    	else
    		showFirstView();
    }
    
    public void showSecondView(){
    	if (mTransitionRunning)
    		return;
    	
    	mTransitionRunning = true;
    	applyRotation(true, 0, 90);
    }

    public void showFirstView() {
    	if (mTransitionRunning)
    		return;
    	
    	mTransitionRunning = true;
        applyRotation(false, 360, 270);
    }

    /**
     * This class listens for the end of the first half of the animation.
     * It then posts a new action that effectively swaps the views when the container
     * is rotated 90 degrees and thus invisible.
     */
    private final class DisplayNextView implements Animation.AnimationListener {
        
    	private boolean mRemoveView1;
        private View mView1;
        private View mView2;

        private DisplayNextView(View view1, View view2, boolean removeView1) {
        	mView1 = view1;
        	mView2 = view2;
        	mRemoveView1 = removeView1;
        }

        public void onAnimationStart(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
            mContainer.post(new SwapViews(mView1, mView2, mRemoveView1));
        }

        public void onAnimationRepeat(Animation animation) {
        }
    }

    /**
     * This class is responsible for swapping the views and start the second
     * half of the animation.
     */
    private final class SwapViews implements Runnable {
       
    	private boolean mRemoveView1;
        private View mView1;
        private View mView2;
        
        /**
         * Constructor
         * @param view1 The first view
         * @param view2 The second view
         * @param removeView1 Remove view1 otherwise remove view2 
         */
        public SwapViews(View view1, View view2, boolean removeView1) {
        	mView1 = view1;
        	mView2 = view2;
        	mRemoveView1 = removeView1;
        }

        public void run() {
            final float centerX = mContainer.getWidth() / 2.0f;
            final float centerY = mContainer.getHeight() / 2.0f;
            Rotate3dAnimation rotation;
            
            if (mRemoveView1) {
            	mView1.setVisibility(View.GONE);
            	mView2.setVisibility(View.VISIBLE);
                mView2.requestFocus();

                rotation = new Rotate3dAnimation(270, 360, centerX, centerY, 310.0f, false);
            } else {
            	mView2.setVisibility(View.GONE);
            	mView1.setVisibility(View.VISIBLE);
                mView1.requestFocus();

                rotation = new Rotate3dAnimation(90, 0, centerX, centerY, 310.0f, false);
            }

            rotation.setAnimationListener(Transition3d.this);
            rotation.setDuration(300);
            rotation.setFillAfter(true);
            rotation.setInterpolator(new DecelerateInterpolator());
            
            mContainer.startAnimation(rotation);
        }
    }

	@Override
	public void onAnimationEnd(Animation animation) {
		this.mTransitionRunning = false;
		if (listener != null)
			listener.transitionComplete();
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		
	}

	@Override
	public void onAnimationStart(Animation animation) {
		
	}

	public ITransition3DListener getListener() {
		return listener;
	}

	public void setListener(ITransition3DListener listener) {
		this.listener = listener;
	}

	public boolean isTransitionRunning() {
		return mTransitionRunning;
	}


}
