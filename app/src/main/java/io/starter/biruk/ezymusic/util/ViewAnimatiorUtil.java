package io.starter.biruk.ezymusic.util;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;

import io.starter.biruk.ezymusic.R;
import io.starter.biruk.ezymusic.util.animation.TranslateAnimation;

/**
 * Created by Biruk on 10/9/2017.
 */
public class ViewAnimatiorUtil {
    private Context appContext;

    public ViewAnimatiorUtil(Context appContext) {
        this.appContext = appContext;
    }

    public void ripple(View v,int duration){
        v.animate()
                .alpha(0.5f)
                .alphaBy(0.2f)
                .setDuration(duration)
                .start();
    }

    public void rotate(final View v, final int duration){
        ValueAnimator valueAnimator=ValueAnimator.ofFloat(360);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                v.setRotationX(animatedValue);
            }
        });

        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    public ValueAnimator rotateY(final View v, final int duration){
        ValueAnimator valueAnimator=ValueAnimator.ofFloat(360);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                v.setRotationY(animatedValue);
            }
        });

        valueAnimator.setDuration(duration);
        valueAnimator.start();
        return valueAnimator;
    }



    public void slideUp(final View view,long duration){
        TranslateAnimation animation = new TranslateAnimation(view, duration, new DecelerateInterpolator(2.0f),
                View.VISIBLE, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 2.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }


        });

        animation.animate();
    }

    public void slideDown(final View view,long duration) {
        TranslateAnimation animation = new TranslateAnimation(view, duration, new DecelerateInterpolator(2.0f),
                View.VISIBLE, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -2.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animation.animate();
    }

    public TranslateAnimation slideDownAnimation(final View view, long duration) {
        TranslateAnimation animation = new TranslateAnimation(view, duration, new DecelerateInterpolator(2.0f),
                View.VISIBLE, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -2.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);

        return animation;
    }
}
