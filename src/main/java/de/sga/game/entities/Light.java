package de.sga.game.entities;

import lombok.Getter;
import org.joml.Vector3f;

@Getter
public class Light extends BaseEntity {

    public Light() {
        super(new Vector3f(0,0,50));
        color = new Vector3f(1,1,1);
    }

    private final Vector3f color;
}
