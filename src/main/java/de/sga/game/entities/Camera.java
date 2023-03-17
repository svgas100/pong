package de.sga.game.entities;

import lombok.Getter;
import org.joml.Vector3f;

@Getter
public class Camera extends BaseEntity {

    public Camera() {
        super(new Vector3f(0,0,50));
    }

    private float pitch;
    private float yaw;
    private float roll;
}
