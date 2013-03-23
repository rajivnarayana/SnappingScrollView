package com.webileapps.snappingscrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/**
 * A @link {HorizontalScrollView} which snaps to pages and also shows a bit of page before and after the current one.
 * The page width is defaulted to be 3/4th the width of scrollview width and shows 1/8th of the page before and 1/8th of the page after.
 * Only to be used with a limited set of pages. No reusing of pages is done.
 * @author rajiv
 */
public class SnappingScrollView extends HorizontalScrollView {

	private GestureDetector mGestureDetector;
	private int mCurrentPageIndex = 0;
	private int mTouchSlop;
    private int mMinimumVelocity;
    
    //Constructors
	public SnappingScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		_init();
	}

	private LinearLayoutInternal mLinearLayout;
	
	
	public SnappingScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		_init();
	}

	public SnappingScrollView(Context context) {
		super(context);
		_init();
	}

	//Internal classes to layout views.
	/**
	 * Internal linearlayout class to provide widths for each page.
	 * @author rajiv
	 *
	 */
	private class LinearLayoutInternal extends LinearLayout {

		
		public LinearLayoutInternal(Context context) {
			super(context);
		}
		
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			//Set the width to be exactly 2 margin views and n adapter views.
			super.onMeasure(MeasureSpec.makeMeasureSpec(SnappingScrollView.this.getMeasuredWidth() /4 + mAdapter.getCount()*SnappingScrollView.this.getMeasuredWidth() * 3/4, MeasureSpec.EXACTLY), heightMeasureSpec);
		}
	}
	
	/**
	 * Setting the right margin on Linearlayout doesnot work apparently inside a horizontal scrollview.
	 * 
	 * A transparent custom view which is used to fill the right and left margins.
	 * 
	 * @author rajiv
	 *
	 */
	private class LeftRightMarginView extends View {
		public LeftRightMarginView(Context context) {
			super(context);
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(MeasureSpec.makeMeasureSpec(SnappingScrollView.this.getMeasuredWidth() /8, MeasureSpec.EXACTLY), heightMeasureSpec);
		}
	}
	
	
	//Touch handling and initialization.
	private void _init() {
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();

		mLinearLayout = new LinearLayoutInternal(getContext());
		mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
		LayoutParams lp = 
				new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		addView(mLinearLayout,
				lp);
		setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (mGestureDetector.onTouchEvent(event)) {
					return true;
				} else if (event.getAction() == MotionEvent.ACTION_UP
						|| event.getAction() == MotionEvent.ACTION_CANCEL) {
					int scrollX = getScrollX();
					int featureWidth = v.getMeasuredWidth() * 3/4;
					mCurrentPageIndex = (scrollX - v.getMeasuredWidth()/8 + featureWidth / 2 ) / featureWidth;
					smoothScrollTo(mCurrentPageIndex * featureWidth, 0);
					return true;
				} else {
					return false;
				}
			}
		});
		mGestureDetector = new GestureDetector(new MyGestureDetector());
		this.setFillViewport(true); //Required so linear layout shows up.
	}

	class MyGestureDetector extends SimpleOnGestureListener {
		
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {
				int featureWidth = getMeasuredWidth()*3/4;
				if (e1.getX() - e2.getX() > mTouchSlop
						&& Math.abs(velocityX) > mMinimumVelocity) {
					mCurrentPageIndex = (mCurrentPageIndex < (mAdapter.getCount() - 1)) ? mCurrentPageIndex + 1
							: mAdapter.getCount() - 1;
				}
				// left to right
				else if (e2.getX() - e1.getX() > mTouchSlop
						&& Math.abs(velocityX) > mMinimumVelocity) {
					mCurrentPageIndex = (mCurrentPageIndex > 0) ? mCurrentPageIndex - 1
							: 0;
				}
				smoothScrollTo(mCurrentPageIndex * featureWidth, 0);
				return true;
			} catch (Exception e) {
				Log.e("Fling", "There was an error processing the Fling event:"
						+ e.getMessage());
			}
			return false;
		}
	}
		
	
	private void constructPages() {
		if(mAdapter.getCount() == 0) return;
		mLinearLayout.addView(new LeftRightMarginView(getContext()), new LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT
				,android.widget.LinearLayout.LayoutParams.MATCH_PARENT,0));

		for (int i = 0; i < mAdapter.getCount(); i++) {
			View item = mAdapter.getViewAtPosition(i);
			mLinearLayout.addView(item
					, new LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
					,android.widget.LinearLayout.LayoutParams.MATCH_PARENT,1)
			);
		}
		mLinearLayout.addView(new LeftRightMarginView(getContext()), new LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT
					,android.widget.LinearLayout.LayoutParams.MATCH_PARENT,0));
		mLinearLayout.requestLayout();
	}

	private HorizontalScrollViewAdapter mAdapter;
	
	public void setAdapter(HorizontalScrollViewAdapter adapter) {
		this.mAdapter = adapter;
		this.constructPages();
	}
	
	/*
	 * A simple adapter that accepts views. May be from activity.
	 * Not as robust as a ViewGroupAdapter. Doesnot reuse pages. All pages are created at once. 
	 */
	public interface HorizontalScrollViewAdapter {
		public int getCount();
		public View getViewAtPosition(int position);
	}
}