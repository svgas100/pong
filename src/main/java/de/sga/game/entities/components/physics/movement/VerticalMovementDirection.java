package de.sga.game.entities.components.physics.movement;

import org.joml.Vector3f;

public enum VerticalMovementDirection {
    UP,
    DOWN,

    NONE;

    public static VerticalMovementDirection getDirectionFromChange(Vector3f positionChangeVector) {
        if (positionChangeVector.y > 0) {
            return UP;
        } else if (positionChangeVector.y < 0) {
            return DOWN;
        }
        return NONE;
    }
}
