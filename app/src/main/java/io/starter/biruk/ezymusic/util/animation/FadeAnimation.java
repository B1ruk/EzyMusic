package io.starter.biruk.ezymusic.util.animation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Interpolator;

/**
 * Created by Biruk on 10/20/2017.
 */
public class FadeAnimation extends AlphaAnimation {

    private View mView;
    private long mDuration;
    private float mFromAlpha;
    private float mToAlpha;
    private Interpolator mInterpolator;

    public FadeAnimation(View mView, long mDuration, float mFromAlpha, float mToAlpha, Interpolator mInterpolator) {
        super(mFromAlpha, mToAlpha);
        this.mView = mView;
        this.mDuration = mDuration;
        this.mFromAlpha = mFromAlpha;
        this.mToAlpha = mToAlpha;
        this.mInterpolator = mInterpolator;
    }

    public void animate(){
        if(mView==null)
            return;
        if (mFromAlpha==mToAlpha)
            return;
        if (mDuration==0)
            return;
        if (mFromAlpha>mToAlpha){
            //fade out animation
            this.setAnimationListener(fadeoutListener);
        }else{
            //fade in animation
            this.setAnimationListener(fadeinListener);
        }

        this.setDuration(mDuration);
        if (mInterpolator!=null)
            this.setInterpolator(mInterpolator);
        mView.startAnimation(this);
    }

    /*
    * Fade in animation listener
    * */
    private AnimationListener fadeinListener=new AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            mView.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };


    private AnimationListener fadeoutListener=new AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            mView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mView.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };
}
