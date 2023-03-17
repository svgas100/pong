package de.sga.game.entities;

import de.sga.game.entities.components.collision.EntitiyCollisionComponent;
import de.sga.game.entities.components.collision.EntitiyHitboxComponent;
import de.sga.game.entities.components.input.AbstractEntityKeyboardComponent;
import de.sga.game.entities.components.movement.EntityMovementComponent;
import de.sga.game.entities.models.TexturedModel;
import org.joml.Vector3f;

import java.util.Random;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;

public class Ball extends Entity{

    private final static Random r = new Random(123);

    private boolean started = false;

    public Ball(Vector3f worldPosition, TexturedModel texturedModel) {
        super(texturedModel, worldPosition, new Vector3f(0), 1F, 0);

        components.add(new EntitiyHitboxComponent(this, 2F, 2F, -0f));

        components.add(new EntitiyCollisionComponent(this));

        components.add(new EntityMovementComponent(this, 1F, 1F, false,true));

        components.add(new AbstractEntityKeyboardComponent(this) {

            @Override
            public void onTick() {
                getComponent(EntityMovementComponent.class).ifPresent(moveComp ->
                {
                    if (!started){
                        if (keyboard.isKeyPressed(GLFW_KEY_SPACE)) {
                            moveComp.setYMovement(r.nextFloat() * 2 - 1);
                            if (r.nextInt(2) % 2 == 1) {
                                moveComp.moveLeft();
                            } else {
                                moveComp.moveRight();
                            }
                            started = true;
                        }
                    }
                });
            }
        });
    }
}
