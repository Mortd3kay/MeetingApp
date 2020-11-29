package com.lazysecs.meetingapp.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class CustomSwipeToRefresh extends SwipeRefreshLayout {

    private int mTouchSlop;
    private float mPrevX;
    private float mPrevY;

    private boolean mDeclined;

    public CustomSwipeToRefresh(Context context, AttributeSet attrs) {
        super(context, attrs);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @SuppressLint("Recycle")
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPrevX = MotionEvent.obtain(event).getX();
                mPrevY = MotionEvent.obtain(event).getY();
                mDeclined = false; // New action
                break;

            case MotionEvent.ACTION_MOVE:
                final float eventX = event.getX();
                float xDiff = Math.abs(eventX - mPrevX);

                final float eventY = event.getY();
                float yDiff = Math.abs(eventY - mPrevY);

                if (xDiff > mTouchSlop && yDiff < mTouchSlop * 3.14 * 4) {
                    return false;
                }
        }

        return super.onInterceptTouchEvent(event);
    }
}