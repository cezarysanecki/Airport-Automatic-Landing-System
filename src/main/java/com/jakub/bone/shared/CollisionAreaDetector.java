package com.jakub.bone.shared;

import com.jakub.bone.config.Constant;

public class CollisionAreaDetector {

    private static final double ALTITUDE_COLLISION_DISTANCE = Constant.ALTITUDE_COLLISION_DISTANCE;
    private static final double HORIZONTAL_COLLISION_DISTANCE = Constant.HORIZONTAL_COLLISION_DISTANCE;

    public static boolean areClose(Coordinates loc1, Coordinates loc2) {
        double horizontalDistance = loc1.horizontalDistance(loc2);
        double altDiff = loc1.verticalDistance(loc2);
        return horizontalDistance <= HORIZONTAL_COLLISION_DISTANCE && altDiff <= ALTITUDE_COLLISION_DISTANCE;
    }

}
