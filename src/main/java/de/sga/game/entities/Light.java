package de.sga.game.entities;

import de.sga.game.entities.components.EntityComponent;
import de.sga.game.entities.components.position.AttachingEntityPositionComponent;
import de.sga.game.entities.components.position.EntityPositionComponent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.joml.Vector3f;

@Getter
public class Light extends Entity{

    public Light() {
        super(new Vector3f(0,0,50));
        color = new Vector3f(1,1,1);
    }

    private final Vector3f color;
}
