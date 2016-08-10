package com.eeec.GestionEspresso.views;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eeec.GestionEspresso.R;

/**
 * Created by rrodriguez on 10/07/16.
 */
public class TutorialView extends RelativeLayout implements View.OnTouchListener {

    private Activity activity;
    private ViewGroup parent;
    private ImageView handImage;
    private boolean isShowing = false;

    public TutorialView(Activity activity) {
        super(activity);

        parent = (ViewGroup) activity.findViewById(android.R.id.content);
        parent.addView(this, parent.getChildCount());


        LayoutInflater inflater = LayoutInflater.from(activity);
        inflater.inflate(R.layout.tutorial, this, true);




        //setBackgroundColor(getResources().getColor(R.color.overlay));
        setOnTouchListener(this);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setAlpha(0f);
        }

        handImage = (ImageView)findViewById(R.id.handInstruction);

        //TextView tutorialtext = (TextView)findViewById(R.id.slideInstructions);
        //tutorialtext.setText("Deslice para pasar\na la siguiente nota");
    }



    private void animateHand(final boolean toggle){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Interpolator interpolador = AnimationUtils.loadInterpolator(this.getContext(),
                    android.R.interpolator.fast_out_slow_in);

            handImage.animate().translationX(toggle ? -50f : 0f).setInterpolator(interpolador).setDuration(500l).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    animateHand(!toggle);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(handImage, View.TRANSLATION_X, toggle ? -50f : 0f);
            animator.setDuration(500l);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    animateHand(!toggle);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animator.start();
        }

    }



    public void show(){
        isShowing = true;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            animate().alpha(1f).setDuration(50).setStartDelay(0).start();
        } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(this, View.ALPHA, 0f, 1f);
            animator.setDuration(50l);
            animator.start();
        }

        animateHand(true);
    }

    public boolean isShowing() {
        return isShowing;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Interpolator interpolador = AnimationUtils.loadInterpolator(getContext(),
                    android.R.interpolator.linear_out_slow_in);

            animate().alpha(0f).setDuration(1000).setInterpolator(interpolador).setStartDelay(0).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            ObjectAnimator animator = ObjectAnimator.ofFloat(this, View.ALPHA, 0f);
            animator.setDuration(1000l);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animator.start();

        }else{
            setVisibility(View.GONE);
        }
        return true;
    }
}
