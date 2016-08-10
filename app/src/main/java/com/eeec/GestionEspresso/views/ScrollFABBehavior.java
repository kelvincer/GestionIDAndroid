package com.eeec.GestionEspresso.views;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by rrodriguez on 15/07/16.
 */
public class ScrollFABBehavior extends FloatingActionButton.Behavior {

    private FloatingActionButton.OnVisibilityChangedListener listener;

    public ScrollFABBehavior(Context context, AttributeSet attrs) {
        super();

        listener = new FloatingActionButton.OnVisibilityChangedListener() {
            @Override
            public void onHidden(FloatingActionButton fab) {

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    fab.animate().alpha(0).setDuration(600).setStartDelay(0).start();
                } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    ObjectAnimator animator = ObjectAnimator.ofFloat(fab, View.ALPHA, 0);
                    animator.setDuration(600l);
                    animator.start();
                }else{
                    fab.setImageAlpha(0);
                }
            }

            @Override
            public void onShown(FloatingActionButton fab) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    fab.animate().alpha(1).setDuration(600).setStartDelay(0).start();
                } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

                    ObjectAnimator animator = ObjectAnimator.ofFloat(fab, View.ALPHA, 1);
                    animator.setDuration(600l);
                    animator.start();

                }else{
                    fab.setImageAlpha(1);
                }

            }
        };

    }

    @Override
    public boolean onStartNestedScroll(final CoordinatorLayout coordinatorLayout, final FloatingActionButton child,
                                       final View directTargetChild, final View target, final int nestedScrollAxes) {
        //child.hide(listener);
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
                || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }




    @Override
    public void onNestedScroll(final CoordinatorLayout coordinatorLayout, final FloatingActionButton child,
                               final View target, final int dxConsumed, final int dyConsumed,
                               final int dxUnconsumed, final int dyUnconsumed) {

        Log.d("RIX_TRACE", "nestedScroll" + dxConsumed + "," + dyConsumed + ";" + dxUnconsumed + "," + dyUnconsumed);
        //super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if (dyConsumed > 0 ) {
            child.hide(listener);
        } else if (dyConsumed < 0 || dyUnconsumed > 0 ) {
            child.show(listener);
        }
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dx, int dy, int[] consumed) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);

        Log.d("RIX_TRACE", "nestedPREScroll" + dx + "," + dy+ ";" + consumed);
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target) {

    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, FloatingActionButton child, int layoutDirection) {
        return false;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        return false;
    }
}

