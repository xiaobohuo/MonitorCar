package com.example.monitorcar.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class SlideBackLayout extends LinearLayout {
	private int xLastTouchLocation;
	private int xCurrentTouchLocation;
	private int detaX, mWidth, mHeight;
	private boolean isClose = false;
	private int windowWidth;
	private Scroller mScroller = null;
	private Interpolator mInterpolator = null;
	private FinishCallBack mFinishCallBack = null;

	private final int TRIGER_WIDTH = 20;
	private final int TRIGER_HEIGHT = 20;
	private float FirstPointX, FirstPointY;
	private boolean Istrigger = false;
	private float shadowLeft = 0;

	public SlideBackLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public SlideBackLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SlideBackLayout(Context context) {
		super(context);
		init(context);
	}

	private void init(Context mContext) {
		mInterpolator = new DecelerateInterpolator();
		mScroller = new Scroller(mContext, mInterpolator);
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		WindowManager mWindowManager = (WindowManager) mContext
				.getSystemService(mContext.WINDOW_SERVICE);
		mWindowManager.getDefaultDisplay().getMetrics(mDisplayMetrics);
		windowWidth = mDisplayMetrics.widthPixels;
	}

	public interface FinishCallBack {
		void finish();
	}

	public void setFinishCallBack(FinishCallBack mFinishCallBack) {
		this.mFinishCallBack = mFinishCallBack;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		int detX = 0, detY = 0;
		final float x = ev.getX();
		final float y = ev.getY();
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			FirstPointX = x;
			FirstPointY = y;
			requestDisallowInterceptTouchEvent(false);
			return super.onInterceptTouchEvent(ev);
		case MotionEvent.ACTION_MOVE:
			detX = (int) (x - FirstPointX);
			detY = (int) (y - FirstPointY);
			if (Math.abs(detX) > TRIGER_WIDTH && Math.abs(detY) < TRIGER_HEIGHT
					&& FirstPointX < 10) {
				Istrigger = true;
				return true;
			}
			return false;
		case MotionEvent.ACTION_UP:
			if (Istrigger) {
				Istrigger = false;
				return super.onInterceptTouchEvent(ev);
			}
			break;
		default:
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xLastTouchLocation = (int) event.getX();
			return true;
		case MotionEvent.ACTION_MOVE:
			xCurrentTouchLocation = (int) event.getX();
			detaX = xLastTouchLocation - xCurrentTouchLocation;
			if (detaX < 0) {
				if (null != mFinishCallBack) {
					scrollTo(detaX, 0);
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			xCurrentTouchLocation = (int) event.getX();
			detaX = xLastTouchLocation - xCurrentTouchLocation;
			if (detaX < 0) {
				if (null != mFinishCallBack) {
					if (-detaX >= windowWidth / 3) {
						// close
						isClose = true;
						startMove(-(windowWidth + detaX));
						detaX = 0;
					} else {
						startMove(-detaX);
						detaX = 0;
					}
				}
			}
			break;
		}
		return super.onTouchEvent(event);
	}

	private void startMove(int deta) {
		mScroller.startScroll(getScrollX(), 0, deta, 0);
		invalidate();
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		} else {
			if (isClose) {
				if (null != mFinishCallBack) {
					mFinishCallBack.finish();
				}
			}
		}
	}

//	@Override
//	protected void dispatchDraw(Canvas canvas) {
//		super.dispatchDraw(canvas);
//		if (detaX == 0)
//			return;
//		RectF rectF = new RectF(-20, 0, 0, getMeasuredHeight());
//		Paint paint = new Paint();
//		paint.setAntiAlias(true);
//
//		LinearGradient linearGradient = new LinearGradient(-detaX - 20, 0,
//				-detaX, 0, 0x66000000, 0x00000000, TileMode.CLAMP);
//		paint.setShader(linearGradient);
//		paint.setStyle(Style.FILL);
//		canvas.drawRect(rectF, paint);
//	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mWidth = getMeasuredWidth();
		mHeight = getMeasuredHeight();
	}

}