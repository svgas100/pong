package de.sga.game.entities.components.position;

import de.sga.game.entities.BaseEntity;
import de.sga.game.entities.Hitbox;
import de.sga.game.entities.components.AbstractEntitiyComponent;
import de.sga.game.entities.components.collision.EntitiyCollisionComponent;
import de.sga.game.entities.components.collision.EntitiyHitboxComponent;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector3f;

import java.util.Optional;

public class EntityPositionComponent extends AbstractEntitiyComponent {

    Logger LOGGER = LogManager.getLogger();

    protected Vector3f position;

    protected Vector3f positionChange = new Vector3f();

    @Getter
    protected boolean grounded;

    @Getter
    protected boolean verticalHit;

    @Getter
    protected boolean horizontalHit;

    @Getter
    protected  Hitbox hitHitbox = null;

    public EntityPositionComponent(BaseEntity entity, Vector3f intitalPosition) {
        super(entity);
        position = new Vector3f(intitalPosition);
    }

    public Vector3f getPosition() {
        return new Vector3f(position);
    }

    public void adjustPosition(Vector3f positionChangeVector){
        positionChange.add(positionChangeVector);
    }

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public void onTick() {
        if(getCollisionComponent().isPresent()){
            var comp = getCollisionComponent().get();
            verticalHit = false;
            horizontalHit = false;

            // we first move horizontally
            // in case this results in a hit we abort the movement;
            position.add(positionChange.x, 0, 0);

            var result = comp.isHit(false, positionChange.x > 0);
            if (result.isPresent()) {

                hitHitbox = comp.getHitHitbox().get();

                // revert previous movement
                position.sub(positionChange.x, 0,0);

                horizontalHit = true;

                if(positionChange.x > 0){
                    float xOther = result.get();
                    float xThis = position.x; // original position
                    if(xOther > xThis){
                        float xDiff = xOther - xThis - 0.05f;
                        position.add(xDiff,0,0);
                    }
                }

                if(positionChange.x < 0){
                    float xOther = result.get();
                    float xThis = position.x; // original position
                    if (xThis > xOther) {
                        float xDiff = xOther - xThis + 0.05f;
                        position.add(xDiff, 0, 0);
                    }
                }
            } else {
                hitHitbox = null;
            }
            // and then vertically
            // collision detection
            position.add(0, positionChange.y, 0);
            var result2 = comp.isHit(true,positionChange.y > 0);
            if (result2.isPresent()) {

                position.sub(0,positionChange.y,0);

                if(positionChange.y < 0) {
                    grounded = true;

                    float yOther = result2.get();
                    float yThis = position.y; // original position

                    if(yThis >= yOther){
                        float yDiff = yOther - yThis + 0.01F;
                        position.add(0,yDiff,0);
                    }
                    verticalHit = true;
                }

                if(positionChange.y > 0) {
                    float yOther = result2.get();
                    float yThis = position.y; // original position

                    if(yOther >= yThis){
                        float yDiff = yOther - yThis - 0.01f;
                        position.add(0,yDiff,0);
                    }
                    verticalHit = true;
                }

                // ideally we would have no collision anymore but due to me being not
                // able to figure correct rotation.
                if(comp.isHit(false,false).isPresent()){
                    position.add(comp.pushingForce());
                }

                // ideally we would have no collision anymore but due to me being not
                // able to figure correct rotation.
                if(comp.isHit(true,false).isPresent()){
                    position.add(new Vector3f(0,comp.pushingForce().x,0));
                }

            }else{
                grounded = false;
            }

        }else{
            position.add(positionChange);
        }

        entity.getComponent(EntitiyHitboxComponent.class).ifPresent(EntitiyHitboxComponent::updateHitbox);
        positionChange = new Vector3f();
    }

    public void setPosition(Vector3f newPosition) {
        position.set(newPosition);

        if(getCollisionComponent().isPresent()) {
            var comp = getCollisionComponent().get();
            // ideally we would have no collision anymore but due to me being not
            // able to figure correct rotation.
            if (comp.isHit(false, false).isPresent()) {
                position.add(comp.pushingForce());
            }
        }

        entity.getComponent(EntitiyHitboxComponent.class).ifPresent(EntitiyHitboxComponent::updateHitbox);
    }

    public Optional<EntitiyCollisionComponent> getCollisionComponent(){
       return entity.getComponent(EntitiyCollisionComponent.class);
    }

    public HorizontalFacingDirection getHorizontalFacingDirection(){
       if ( entity.getRotation() .y < 180 ) {
           return HorizontalFacingDirection.WEST;
       }else {
           return HorizontalFacingDirection.EAST;
       }
    }

    public enum HorizontalFacingDirection{
        WEST,
        EAST
    }
}
