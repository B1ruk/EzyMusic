package io.starter.biruk.ezymusic.util.view;


/*
Created by Biruk on 10/26/2017./
*/

public enum Direction {
    UP,
    RIGHT,
    DOWN,
    LEFT;

    /*
    * up [45,135]
    * right [0,45] or [315,360]
    * down [225,315]
    * left [135,225]
    * */
    public static Direction get(double angle){
        if (inRange(angle,45,135)){
            return Direction.UP;
        }
        else if(inRange(angle,0,45) || inRange(angle,315,360)){
            return Direction.RIGHT;
        }
        else if (inRange(angle,225,315)){
            return Direction.DOWN;
        }
        else {
            return Direction.LEFT;
        }
    }

    private static boolean inRange(double angle, int start, int end) {
        return  (angle>=start) && (angle<end);
    }
}
