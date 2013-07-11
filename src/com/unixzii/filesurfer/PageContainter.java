package com.unixzii.filesurfer;

import android.app.FragmentBreadCrumbs;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Scroller;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-7-11
 * Time: 上午9:02
 * To change this template use File | Settings | File Templates.
 */
public class PageContainter extends ViewGroup {

    private final static int STATE_IDLE = 0;
    private final static int STATE_SCROLLING = 1;
    private final static int STATE_ANIMATING = 2;
    private final static int STATE_OVERSCROLLED = 3;

    private final static int MIN_SNAP_VELOCITY = 500;

    private Context mContext;
    private VelocityTracker mVTracker;
    private Scroller mScroller;
    private Scroller mOverScroller;
    private OnPageChangeListener mOnPageChangeListener = null;
    private int mMaxScrollX;
    private int mTouchSlop;
    private float mLastMotionX;
    private int mCurrentPage;
    private int mScrolledX;
    private int mState = STATE_IDLE;

    public PageContainter(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context);
    }

    public PageContainter(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public PageContainter(Context context) {
        super(context);

        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mScroller = new Scroller(mContext,new DecelerateInterpolator()); //初始化滑动动画器
        mOverScroller = new Scroller(mContext,new OvershootInterpolator(2.5f)); //初始化越界动画器
        mTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop(); //获取触摸灵敏值
        mCurrentPage = 0;
    }

    @Override
    protected void onLayout(boolean b, int i, int i2, int i3, int i4) {
        //View的尺寸被改变
        if (b) {
            int left = i;

            for (int j = 0; j < getChildCount(); j++) {

                View view = getChildAt(j);
                view.measure(0,0);
                view.layout(left,i2,left + getWidth(),getHeight());

                left += getWidth();
            }
        }
    }

    @Override
    public void computeScroll() {
        switch (mState) {
            case STATE_ANIMATING:
                if (mScroller.computeScrollOffset()) {
                    scrollTo(mScroller.getCurrX(),0);
                }
                break;
            case STATE_OVERSCROLLED:
                if(mOverScroller.computeScrollOffset()) {
                    scrollTo(mOverScroller.getCurrX(),0);
                }
                break;
        }

        postInvalidate();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //决定触摸事件是否需要分发给子View
        final int action = ev.getAction();
        final float x = ev.getX();


        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = x;
                mScroller.abortAnimation();
                mState = STATE_SCROLLING;
                break;
            case MotionEvent.ACTION_MOVE:
                float diffY = Math.abs(x - mLastMotionX);
                if (diffY > mTouchSlop) {
                    mState = STATE_SCROLLING;
                }
                break;
            case MotionEvent.ACTION_UP:
                mState = STATE_ANIMATING;
                break;
        }

        return mState == STATE_SCROLLING;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //处理响应触摸事件

        final int action = event.getAction();
        final float x = event.getX();


        if (mVTracker == null) {
            mVTracker = VelocityTracker.obtain();
        }
        mVTracker.addMovement(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = x;
                mScrolledX = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = (int)(mLastMotionX - x);
                final int scrollX = getScrollX();
                mMaxScrollX = (getChildCount() - 1) * getWidth();

                //计算越界阻力
                if (scrollX < 0 || scrollX > mMaxScrollX) {
                    deltaX = (int)(deltaX / 4.0);
                }

                scrollBy(deltaX,0);

                mScrolledX = mScrolledX + (int)(x - mLastMotionX);
                mLastMotionX = x;

                break;
            case MotionEvent.ACTION_UP:
                mVTracker.computeCurrentVelocity(1000);
                final int vx = (int)mVTracker.getXVelocity();
                Log.d("TAG",String.valueOf(vx));
                if (vx > MIN_SNAP_VELOCITY) {
                    navigateTo(mCurrentPage - 1);
                } else if (vx < -MIN_SNAP_VELOCITY) {
                    navigateTo(mCurrentPage + 1);
                } else {
                    final int factor = (int)(getWidth() / 2);

                    if (mScrolledX > factor) {
                        navigateTo(mCurrentPage - 1);
                    } else if (mScrolledX < -factor) {
                        navigateTo(mCurrentPage + 1);
                    } else {
                        navigateTo(mCurrentPage);
                    }
                }
                break;
        }

        return true; //表示该事件已被处理
    }

    private void navigateTo(int index) {
        if (index < 0) {
            mCurrentPage = 0;
        } else if (index > getChildCount() - 1) {
            mCurrentPage = getChildCount() - 1;
        } else {
            mCurrentPage = index;
        }

        int targetX = mCurrentPage * getWidth();

        final int scrollX = getScrollX();
        if(scrollX < 0 || scrollX > mMaxScrollX) {
            mOverScroller.startScroll(getScrollX(),0,targetX - getScrollX(),0,250);
            mState = STATE_OVERSCROLLED;
        } else {
            mScroller.startScroll(getScrollX(),0,targetX - getScrollX(),0,250);
            mState = STATE_ANIMATING;
        }
        invalidate();

        onPageChange();
    }

    protected void onPageChange() {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageChange(this,mCurrentPage);
        }
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mOnPageChangeListener = listener;
    }

    public static interface OnPageChangeListener {
        void onPageChange(View view,int index);
    }
}
