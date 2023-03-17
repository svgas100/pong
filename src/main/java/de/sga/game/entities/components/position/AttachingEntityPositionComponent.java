package de.sga.game.entities.components.position;

import de.sga.game.entities.BaseEntity;
import org.joml.Vector3f;

public class AttachingEntityPositionComponent extends EntityPositionComponent {

    public AttachingEntityPositionComponent(BaseEntity entity, Vector3f position) {
        super(entity,position);
    }

    public Vector3f getPosition() {
        return new Vector3f(position);
    }

    public void setPosition(Vector3f positionChangeVector){
        position.set(positionChangeVector);
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
