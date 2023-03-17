package de.sga.game.entities;

import de.sga.game.entities.components.EntityComponent;
import de.sga.game.entities.components.position.AttachingEntityPositionComponent;
import de.sga.game.entities.components.position.EntityPositionComponent;
import lombok.Getter;
import org.joml.Vector3f;

@Getter
public class Camera extends Entity {

    public Camera() {
        super(new Vector3f(0,0,50));
    }

    private float pitch;
    private float yaw;
    private float roll;
}
