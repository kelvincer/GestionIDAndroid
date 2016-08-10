package com.eeec.GestionEspresso.views;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ScrollHomeBehavior extends FloatingActionButton.Behavior {

    private FloatingActionButton.OnVisibilityChangedListener listener;

    public ScrollHomeBehavior(Context context, AttributeSet attrs){
        super();

        listener = new FloatingActionButton.OnVisibilityChangedListener() {
            @Override
            public void onHidden(FloatingActionButton fab) {

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    fab.setScaleX(1);
                    fab.setScaleY(1);
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    fab.animate().alpha(0).setDuration(600).setStartDelay(0).start();
                } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

                    ObjectAnimator animator = ObjectAnimator.ofFloat(fab, View.ALPHA, 0);
                    animator.setDuration(600l);
                    animator.start();
                }else{
                    fab.setAlpha(0);
                }
            }

            @Override
            public void onShown(FloatingActionButton fab) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    fab.setScaleX(1);
                    fab.setScaleY(1);
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    fab.animate().alpha(1).setDuration(600).setStartDelay(0).start();
                } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

                    ObjectAnimator animator = ObjectAnimator.ofFloat(fab, View.ALPHA, 1);
                    animator.setDuration(600l);
                    animator.start();

                }else{
                    fab.setAlpha(1);
                }

            }
        };

    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, FloatingActionButton child, int layoutDirection) {
        return false;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        return false;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        child.hide(listener);
        return true;
        //return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
        //        || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }



    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target) {
        child.show(listener);
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, FloatingActionButton child, MotionEvent ev) {
        Log.d("RIX_TRACE", "interceptTouchEvent");
        return super.onInterceptTouchEvent(parent, child, ev);
    }

    @Override
    public boolean onNestedFling(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, float velocityX, float velocityY, boolean consumed) {
        Log.d("RIX_TRACE", "nestedFling(" + velocityX +"," + velocityY + "," + consumed+ ")");
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
    }

    @Override
    public boolean onMeasureChild(CoordinatorLayout parent, FloatingActionButton child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        Log.d("RIX_TRACE", "measureChild(" + parentWidthMeasureSpec + "," + widthUsed + "," + parentHeightMeasureSpec + "," + heightUsed);
        return super.onMeasureChild(parent, child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
    }

    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, float velocityX, float velocityY) {
        Log.d("RIX_TRACE", "nestedPreFling(" + velocityX + "," + velocityY + ")");
        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dx, int dy, int[] consumed) {
        Log.d("RIX_TRACE", "nestedPreScroll(" + dx + "," + dy+ "," + consumed + ")");
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        Log.d("RIX_TRACE", "nestedScroll(" + dxConsumed + "," + dyConsumed+ "," + dxUnconsumed + "," + dyUnconsumed + ")");
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
    }

    @Override
    public void onNestedScrollAccepted(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        Log.d("RIX_TRACE", "nestedScrollAccepted(" + nestedScrollAxes + ")");
        super.onNestedScrollAccepted(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }
}
