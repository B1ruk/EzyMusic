package io.starter.biruk.ezymusic.util.view;

import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by Biruk on 10/26/2017.
 */
public class OnSwipeListener extends GestureDetector.SimpleOnGestureListener {

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        /*
        * Grab two events located on the plane at e1=(x1,y1) and e2=(x2,y2)
        * let e1 be the initial event
        * e2 can be located at 4 different positions
        *
        * */

        float x1 = e1.getX();
        float y1 = e1.getY();

        float x2 = e2.getX();
        float y2 = e2.getY();


        Direction direction = getDirection(x1, y1, x2, y2);
        return onSwipe(direction);
    }

    /*
    * override this method .The Direction enum will tell you
    * how the user swiped
    * */
    public boolean onSwipe(Direction direction) {
        return false;
    }

    public Direction getDirection(float x1, float y1, float x2, float y2) {
        double angle = getAngle(x1, y1, x2, y2);
        return Direction.get(angle);
    }

    /*
    * finds the angle between two points in the plane (x1,y1) and (x2,y2)
    *
    * */
    public double getAngle(float x1, float y1, float x2, float y2) {
        double rad = Math.atan2(y1 - y2, x2 - x1) + Math.PI;
        return (rad * 180 / Math.PI + 180) % 360;
    }
}
