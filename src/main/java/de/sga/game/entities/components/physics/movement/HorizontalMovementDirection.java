package de.sga.game.entities.components.physics.movement;

import org.joml.Vector3f;

public enum HorizontalMovementDirection {
    LEFT,
    RIGHT,

    NONE;

    public static HorizontalMovementDirection getDirectionFromChange(Vector3f positionChangeVector) {
        if (positionChangeVector.x > 0) {
            return RIGHT;
        } else if (positionChangeVector.x < 0) {
            return LEFT;
        }
        return NONE;
    }
}
