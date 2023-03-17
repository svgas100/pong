package de.sga.game.entities.components.physics.collision;

import de.sga.game.entities.components.physics.movement.HorizontalMovementDirection;
import de.sga.game.entities.components.physics.movement.VerticalMovementDirection;

public interface CollisionListenerComponent{

    void xCollision(HorizontalMovementDirection direction);

    void yCollision(VerticalMovementDirection direction);
}
