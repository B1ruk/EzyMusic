package io.starter.biruk.ezymusic.util.animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.widget.ImageView;

/**
 * Created by Biruk on 10/29/2017.
 */
public class TranslateAnimation extends android.view.animation.TranslateAnimation {


    private View view;
    private long duration;
    private int finalVisibility;
    private int newImageResourceId;
    private Interpolator interpolator;

    private boolean mChangeImageResource = false;

    /*
    * use this constructor to animate a view from one location to another
    * */
    public TranslateAnimation(View view, long duration ,Interpolator interpolator, int finalVisibility,
                              int fromXType, float fromXValue, int toXType,
                              float toXValue, int fromYType, float fromYValue,
                              int toYType, float toYValue) {

        super(fromXType, fromXValue, toXType,
                toXValue, fromYType, fromYValue,
                toYType, toYValue);

        this.view = view;
        this.duration = duration;
        this.interpolator = interpolator;
        this.finalVisibility = finalVisibility;
    }

    /*
    * uses this constructor to animate an ImageView from one location to another and change the image at the end of the animation
    * */
    public TranslateAnimation(View view, long duration, int newImageResourceId ,Interpolator interpolator, int finalVisibility,
                              int fromXType, float fromXValue, int toXType,
                              float toXValue, int fromYType, float fromYValue,
                              int toYType, float toYValue) {

        super(fromXType, fromXValue, toXType,
                toXValue, fromYType, fromYValue,
                toYType, toYValue);

        this.view = view;
        this.duration = duration;
        this.newImageResourceId = newImageResourceId;
        this.interpolator = interpolator;
        this.finalVisibility = finalVisibility;
    }

    /*
    * performs the fade animation
    * */
    public void animate() {
        if (view == null) {
            return;
        }

        if (duration == 0) {
            return;
        }

        //      set the animation parameters
        this.setAnimationListener(translateListener);
        this.setDuration(duration);
        this.setInterpolator(interpolator);
        view.startAnimation(this);

    }

    /*
    * translate animation listener
    * */
    private AnimationListener translateListener=new AnimationListener() {


        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            view.setVisibility(finalVisibility);

            if (mChangeImageResource && (view instanceof ImageView)){
                ((ImageView) view).setImageResource(newImageResourceId);
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };


}
