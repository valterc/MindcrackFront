package com.vcutils.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class TouchImageView extends ImageView {

	private Matrix matrix = new Matrix();
	private Matrix savedMatrix = new Matrix();

	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private int mode = NONE;

	private PointF start = new PointF();
	private PointF mid = new PointF();
	private float oldDist = 1f;

	private int bWidth;
	private int bHeight;

	private int sWidth;
	private int sHeight;

	private Boolean motionEnabled;
	
	
	public TouchImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	public TouchImageView(Context context) {
		super(context);
		initialize();
	}

	public TouchImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize();
	}

	private void initialize() {
		super.setClickable(true);

		matrix.setTranslate(1f, 1f);
		setImageMatrix(matrix);
		setScaleType(ScaleType.MATRIX);
		motionEnabled = false;
		
		setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent rawEvent) {
				if (!motionEnabled)
					return true;
				
				final WrapMotionEvent event = WrapMotionEvent.wrap(rawEvent);

				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
					savedMatrix.set(matrix);
					start.set(event.getX(), event.getY());
					mode = DRAG;
					break;
				case MotionEvent.ACTION_POINTER_DOWN:
					oldDist = spacing(event);
					if (oldDist > 10f) {
						savedMatrix.set(matrix);
						midPoint(mid, event);
						mode = ZOOM;
					}
					break;
				case MotionEvent.ACTION_UP:
					int xDiff = (int) Math.abs(event.getX() - start.x);
					int yDiff = (int) Math.abs(event.getY() - start.y);
					if (xDiff < 8 && yDiff < 8) {
						performClick();
					}

					float[] v = new float[9];
					matrix.getValues(v);

					float bScale = v[0];

					float scaledWidth = bWidth * bScale;
					float scaledHeight = bHeight * bScale;

					if (scaledWidth < sWidth) {
						if (scaledWidth + v[2] > sWidth) {
							v[2] = sWidth - scaledWidth;
						}

						if (v[2] < 0) {
							v[2] = 0;
						}
					} else {
						if (v[2] > 0) {
							v[2] = 0;
						}

						if (v[2] + scaledWidth < sWidth) {
							v[2] = sWidth - scaledWidth;
						}
					}

					if (scaledHeight < sHeight) {
						if (scaledHeight + v[5] > sHeight) {
							v[5] = sHeight - scaledHeight;
						}

						if (v[5] < 0) {
							v[5] = 0;
						}
					} else {
						if (v[5] > 0) {
							v[5] = 0;
						}

						if (v[5] + scaledHeight < sHeight) {
							v[5] = sHeight - scaledHeight;
						}
					}

					matrix.setValues(v);

				case MotionEvent.ACTION_POINTER_UP:
					mode = NONE;
					break;
				case MotionEvent.ACTION_MOVE:
					if (mode == DRAG) {
						matrix.set(savedMatrix);
						matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
						
						
						
					} else if (mode == ZOOM) {
						float newDist = spacing(event);
						if (newDist > 10f) {

							float[] valuesOld = new float[9];
							matrix.getValues(valuesOld);

							matrix.set(savedMatrix);
							float scale = newDist / oldDist;
							matrix.postScale(scale, scale, mid.x, mid.y);

							float[] values = new float[9];
							matrix.getValues(values);

							if (values[0] > 2.2f) {
								values[0] = 2.2f;
								values[4] = 2.2f;
								if (valuesOld[0] == 2.2f) {
									values[2] = valuesOld[2];
									values[5] = valuesOld[5];
								}
							}

							if (values[0] < 0.6f) {
								values[0] = 0.6f;
								values[4] = 0.6f;

								if (valuesOld[0] == 0.6f) {
									values[2] = valuesOld[2];
									values[5] = valuesOld[5];
								}
							}

							matrix.setValues(values);
						}
					}
					break;
				}

				setImageMatrix(matrix);
				return true;
			}

		});
		calculateCenterMatrix(sWidth, bHeight);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		this.sWidth = w;
		this.sHeight = h;
		calculateCenterMatrix(w, h);
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		this.bWidth = bm.getWidth();
		this.bHeight = bm.getHeight();
		// calculateCenterMatrix(sWidth, bHeight);
	}

	@Override
	public void setImageResource(int resId) {
		super.setImageResource(resId);
		this.bWidth = getDrawable().getIntrinsicWidth();
		this.bHeight = getDrawable().getIntrinsicHeight();
		calculateCenterMatrix(sWidth, sHeight);
	};

	public void setImage(Bitmap bm, int displayWidth, int displayHeight) {
		super.setImageBitmap(bm);

		bWidth = bm.getWidth();
		bHeight = bm.getHeight();

		float scale;
		if ((displayHeight / bm.getHeight()) >= (displayWidth / bm.getWidth())) {
			scale = (float) displayWidth / (float) bm.getWidth();
		} else {
			scale = (float) displayHeight / (float) bm.getHeight();
		}

		savedMatrix.set(matrix);
		matrix.set(savedMatrix);
		matrix.postScale(scale, scale, mid.x, mid.y);
		setImageMatrix(matrix);

		float redundantYSpace = (float) displayHeight - (scale * (float) bm.getHeight());
		float redundantXSpace = (float) displayWidth - (scale * (float) bm.getWidth());

		redundantYSpace /= (float) 2;
		redundantXSpace /= (float) 2;

		savedMatrix.set(matrix);
		matrix.set(savedMatrix);
		matrix.postTranslate(redundantXSpace, redundantYSpace);
		setImageMatrix(matrix);

	}

	public void calculateCenterMatrix(int width, int height) {

		if (bWidth == 0 || bHeight == 0) {
			if (getDrawable() != null) {
				this.bWidth = getDrawable().getIntrinsicWidth();
				this.bHeight = getDrawable().getIntrinsicHeight();
			}
		}

		if (width == 0 || height == 0 || bWidth == 0 || bHeight == 0)
			return;

		float scale = 1;
		if (bHeight > height || bWidth > width) {
			if ((height / bHeight) >= (width / bWidth)) {
				scale = (float) width / (float) bWidth;
			} else {
				scale = (float) height / (float) bHeight;
			}
		}

		if (scale < 0.8f) {
			scale = 0.9f;
		} else if (scale > 2.2f) {
			scale = 1.6f;
		}

		savedMatrix.set(matrix);
		matrix.set(savedMatrix);
		matrix.postScale(scale, scale, mid.x, mid.y);
		setImageMatrix(matrix);

		float redundantYSpace = (float) height - (scale * (float) bHeight);
		float redundantXSpace = (float) width - (scale * (float) bWidth);

		redundantYSpace /= (float) 2;
		redundantXSpace /= (float) 2;

		if (bHeight > sHeight)
			redundantYSpace = 0;

		if (bWidth > sWidth)
			redundantXSpace = 0;

		savedMatrix.set(matrix);
		matrix.set(savedMatrix);
		matrix.postTranslate(redundantXSpace, redundantYSpace);
		setImageMatrix(matrix);
	}

	/** Determine the space between the first two fingers */
	private float spacing(WrapMotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/** Calculate the mid point of the first two fingers */
	private void midPoint(PointF point, WrapMotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	public Boolean isMotionEnabled() {
		return motionEnabled;
	}

	public void setMotionEnabled(Boolean motionEnabled) {
		this.motionEnabled = motionEnabled;
	}
	
}
