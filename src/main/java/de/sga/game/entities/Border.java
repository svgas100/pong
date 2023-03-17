package de.sga.game.entities;

import de.sga.game.entities.components.collision.EntitiyCollisionComponent;
import de.sga.game.entities.components.collision.EntitiyHitboxComponent;
import de.sga.game.entities.models.TexturedModel;
import org.joml.Vector3f;

public class Border extends BaseEntity {

    public Border(Vector3f worldPosition, TexturedModel texturedModel) {
        super(texturedModel, worldPosition, new Vector3f(0), 1F, 0);

        components.add(new EntitiyHitboxComponent(this, 100F, 2F, -0f));

        components.add(new EntitiyCollisionComponent(this));
    }
}
