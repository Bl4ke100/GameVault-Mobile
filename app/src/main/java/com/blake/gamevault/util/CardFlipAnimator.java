package com.blake.gamevault.util;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

public class CardFlipAnimator {

    private static final int DURATION_IN = 200;
    private static final int DURATION_OUT = 250;

    public static void attach(View card, Runnable onClick) {
        card.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    animateLift(v);
                    break;
                case MotionEvent.ACTION_UP:
                    animateDrop(v, onClick);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    animateDrop(v, null);
                    break;
            }
            return true;
        });
    }

    private static void animateLift(View v) {
        v.bringToFront();
        if (v.getParent() instanceof ViewGroup) {
            ((ViewGroup) v.getParent()).invalidate();
        }

        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(v, "scaleX", 1f, 1.15f),
                ObjectAnimator.ofFloat(v, "scaleY", 1f, 1.15f),
                ObjectAnimator.ofFloat(v, "translationZ", 0f, 24f),
                ObjectAnimator.ofFloat(v, "translationY", 0f, -12f),
                ObjectAnimator.ofFloat(v, "alpha", 1f, 1f)
        );
        set.setDuration(DURATION_IN);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();

    }

    private static void animateDrop(View v, Runnable onClick) {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(v, "scaleX", 1.15f, 1f),
                ObjectAnimator.ofFloat(v, "scaleY", 1.15f, 1f),
                ObjectAnimator.ofFloat(v, "translationZ", 24f, 0f),
                ObjectAnimator.ofFloat(v, "translationY", -12f, 0f)
        );
        set.setDuration(DURATION_OUT);
        set.setInterpolator(new OvershootInterpolator(1.5f));
        set.start();


        if (onClick != null) {
            set.addListener(new android.animation.AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(android.animation.Animator animation) {
                    onClick.run();
                }
            });
        }
    }


}