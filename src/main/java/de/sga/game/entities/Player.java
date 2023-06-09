package de.sga.game.entities;

import de.sga.game.entities.components.physics.collision.EntitiyCollisionComponent;
import de.sga.game.entities.components.physics.collision.EntitiyHitboxComponent;
import de.sga.game.entities.components.input.AbstractEntityKeyboardComponent;
import de.sga.game.entities.components.physics.movement.EntityMovementComponent;
import de.sga.game.entities.models.TexturedModel;
import org.joml.Vector3f;

public class Player extends BaseEntity {

    public Player(Vector3f worldPosition, TexturedModel texturedModel, int downKey, int upKey) {
        super(texturedModel, worldPosition, new Vector3f(0), 1F, 0);

        components.add(new EntitiyHitboxComponent(this, 2.5F, 6F, -0f));

        components.add(new EntitiyCollisionComponent(this));

        components.add(new EntityMovementComponent(this, 2.5F, 1F, true,false));

        components.add(new AbstractEntityKeyboardComponent(this) {

            @Override
            public void onTick() {
                getComponent(EntityMovementComponent.class).ifPresent(moveComp ->
                {
                    if (keyboard.isKeyPressed(downKey) ^ keyboard.isKeyPressed(upKey)) {
                        if (keyboard.isKeyPressed(upKey)) {
                            moveComp.moveUp();
                        } else {
                            moveComp.moveDown();
                        }
                    }
                });
            }
        });
    }
}
