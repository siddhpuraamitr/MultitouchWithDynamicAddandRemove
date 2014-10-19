/**
 * PhotoSorterView.java
 * 
 * (c) Luke Hutchison (luke.hutch@mit.edu)
 * 
 * TODO: Add OpenGL acceleration.
 * 
 * Released under the Apache License v2.
 */
package com.amitsid.dynamicaddimageview;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import com.amitsid.dynamicaddimageview.MultiTouchController.MultiTouchObjectCanvas;
import com.amitsid.dynamicaddimageview.MultiTouchController.PointInfo;
import com.amitsid.dynamicaddimageview.MultiTouchController.PositionAndScale;

public class PhotoSortrView extends View implements
		MultiTouchObjectCanvas<MultiTouchEntity> {

	// private static final int[] IMAGES = { R.drawable.m74hubble };
	private Path drawPath;
	// drawing and canvas paint
	private Paint drawPaint, canvasPaint;
	// initial color
	private int paintColor = Color.TRANSPARENT;
	// canvas
	private Canvas drawCanvas;
	// canvas bitmap
	private Bitmap canvasBitmap;

	// public PhotoSortrView(Context context, AttributeSet attrs) {
	// super(context, attrs);

	// }
	private ArrayList<MultiTouchEntity> imageIDs = new ArrayList<MultiTouchEntity>();

	// --

	private MultiTouchController<MultiTouchEntity> multiTouchController = new MultiTouchController<MultiTouchEntity>(
			this);

	// --

	private PointInfo currTouchPoint = new PointInfo();

	private boolean mShowDebugInfo = true;

	private static final int UI_MODE_ROTATE = 1, UI_MODE_ANISOTROPIC_SCALE = 2;

	private int mUIMode = UI_MODE_ROTATE;

	// --

	private static final float SCREEN_MARGIN = 100;

	private int width, height, displayWidth, displayHeight;

	// ---------------------------------------------------------------------------------------------------

	public PhotoSortrView(Context context) {
		this(context, null);
	}

	public PhotoSortrView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		setupDrawing();
	}

	public PhotoSortrView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
		// loadImages(context);
	}

	private void init(Context context) {
		Resources res = context.getResources();
		setBackgroundColor(Color.TRANSPARENT);

		DisplayMetrics metrics = res.getDisplayMetrics();
		this.displayWidth = res.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math
				.max(metrics.widthPixels, metrics.heightPixels) : Math.min(
				metrics.widthPixels, metrics.heightPixels);
		this.displayHeight = res.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math
				.min(metrics.widthPixels, metrics.heightPixels) : Math.max(
				metrics.widthPixels, metrics.heightPixels);
	}

	/** Called by activity's onResume() method to load the images */
	public void addImages(Context context, int resourceId) {
		Resources res = context.getResources();
		imageIDs.add(new ImageEntity(resourceId, res));
		float cx = getWidth() / 2;
		// SCREEN_MARGIN
		// + (float) (Math.random() * (displayWidth - 2 * SCREEN_MARGIN));
		float cy = getHeight() / 2;
		// SCREEN_MARGIN
		// + (float) (Math.random() * (displayHeight - 2 * SCREEN_MARGIN));
		imageIDs.get(imageIDs.size() - 1).load(context, cx, cy);
		invalidate();
	}

	public void removeAllImages() {

		imageIDs.removeAll(imageIDs);
		invalidate();
	}

	/**
	 * Called by activity's onPause() method to free memory used for loading the
	 * images
	 */
	public void removeImage() {
		// Resources res = context.getResources();
		// int n = imageIDs.size();
		// for (int i = 0; i < n; i++)
		if (imageIDs.size() > 0) {
			imageIDs.remove(imageIDs.size() - 1);
		}

		invalidate();
	}

	// ---------------------------------------------------------------------------------------------------

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
		canvas.drawPath(drawPath, drawPaint);
		int n = imageIDs.size();
		for (int i = 0; i < n; i++)
			imageIDs.get(i).draw(canvas);
	}

	// ---------------------------------------------------------------------------------------------------

	public void trackballClicked() {
		mUIMode = (mUIMode + 1) % 3;
		invalidate();
	}

	/** Pass touch events to the MT controller */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float touchX = event.getX();
		float touchY = event.getY();
		// respond to down, move and up events
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			drawPath.moveTo(touchX, touchY);
			break;
		case MotionEvent.ACTION_MOVE:
			drawPath.lineTo(touchX, touchY);
			break;
		case MotionEvent.ACTION_UP:
			drawPath.lineTo(touchX, touchY);
			drawCanvas.drawPath(drawPath, drawPaint);
			drawPath.reset();
			break;
		default:
			return false;
		}
		// redraw
		invalidate();
		// return true;

		return multiTouchController.onTouchEvent(event);
	}

	/**
	 * Get the image that is under the single-touch point, or return null
	 * (canceling the drag op) if none
	 */
	public MultiTouchEntity getDraggableObjectAtPoint(PointInfo pt) {
		float x = pt.getX(), y = pt.getY();
		int n = imageIDs.size();
		for (int i = n - 1; i >= 0; i--) {
			ImageEntity im = (ImageEntity) imageIDs.get(i);
			if (im.containsPoint(x, y))
				return im;
		}
		return null;
	}

	/**
	 * Select an object for dragging. Called whenever an object is found to be
	 * under the point (non-null is returned by getDraggableObjectAtPoint()) and
	 * a drag operation is starting. Called with null when drag op ends.
	 */
	public void selectObject(MultiTouchEntity img, PointInfo touchPoint) {
		currTouchPoint.set(touchPoint);
		if (img != null) {
			// Move image to the top of the stack when selected
			drawPaint.setColor(Color.TRANSPARENT);
			imageIDs.remove(img);
			imageIDs.add(img);
		} else {
			// Called with img == null when drag stops.
		}
		invalidate();
	}

	/**
	 * Get the current position and scale of the selected image. Called whenever
	 * a drag starts or is reset.
	 */
	public void getPositionAndScale(MultiTouchEntity img,
			PositionAndScale objPosAndScaleOut) {
		// FIXME affine-izem (and fix the fact that the anisotropic_scale part
		// requires averaging the two scale factors)
		objPosAndScaleOut.set(img.getCenterX(), img.getCenterY(),
				(mUIMode & UI_MODE_ANISOTROPIC_SCALE) == 0,
				(img.getScaleX() + img.getScaleY()) / 2,
				(mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0, img.getScaleX(),
				img.getScaleY(), (mUIMode & UI_MODE_ROTATE) != 0,
				img.getAngle());
	}

	/** Set the position and scale of the dragged/stretched image. */
	public boolean setPositionAndScale(MultiTouchEntity img,
			PositionAndScale newImgPosAndScale, PointInfo touchPoint) {
		currTouchPoint.set(touchPoint);
		boolean ok = ((ImageEntity) img).setPos(newImgPosAndScale);
		if (ok)
			invalidate();
		return ok;
	}

	public boolean pointInObjectGrabArea(PointInfo pt, MultiTouchEntity img) {
		return false;
	}

	private void setupDrawing() {

		// prepare for drawing and setup paint stroke properties
		drawPath = new Path();
		drawPaint = new Paint();
		drawPaint.setColor(paintColor);
		drawPaint.setAntiAlias(true);
		drawPaint.setStrokeWidth(10);
		drawPaint.setStyle(Paint.Style.STROKE);
		// drawPaint.setStyle(Paint.Style.STROKE);
		drawPaint.setStrokeJoin(Paint.Join.ROUND);
		drawPaint.setStrokeCap(Paint.Cap.ROUND);
		canvasPaint = new Paint(Paint.DITHER_FLAG);

	}

	// size assigned to view
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if (w > 0 && h > 0) {

			super.onSizeChanged(w, h, oldw, oldh);
			canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
			drawCanvas = new Canvas(canvasBitmap);
		}
	}

	// draw the view - will be called after touch event

	// register user touches as drawing action

	// update color
	public void setColor(String newColor) {
		invalidate();
		paintColor = Color.parseColor(newColor);
		drawPaint.setColor(paintColor);
	}

	public void clearCanvas() {
		drawCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		invalidate();

	}

	public void setTranspertColor() {
		drawPaint.setColor(Color.TRANSPARENT);
	}
}
