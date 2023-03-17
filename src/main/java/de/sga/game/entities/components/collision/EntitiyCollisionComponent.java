package de.sga.game.entities.components.collision;

import de.sga.game.Game;
import de.sga.game.entities.BaseEntity;
import de.sga.game.entities.Hitbox;
import de.sga.game.entities.components.AbstractEntitiyComponent;
import lombok.Getter;
import org.joml.Vector3f;

import java.util.*;

public class EntitiyCollisionComponent extends AbstractEntitiyComponent {

    @Getter
    private Hitbox hitbox;

    public EntitiyCollisionComponent(BaseEntity entity) {
        super(entity);
        entity.getComponent(EntitiyHitboxComponent.class).ifPresent(comp -> {
            hitbox = comp.getHitbox();
        });

        if(hitbox == null){
            throw new IllegalStateException("No hitbox for collision detection!");
        }
    }

    public Optional<Float> isHit(boolean vertical, boolean right) {
        entity.getComponent(EntitiyHitboxComponent.class).ifPresent(EntitiyHitboxComponent::updateHitbox);

        Collection<Hitbox> allBoxes = new ArrayList<>(Game.theGame().entities)
                .stream()
                .flatMap(e -> e.getComponent(EntitiyHitboxComponent.class).stream())
                .map(EntitiyHitboxComponent::getHitbox)
                .toList();

        for(var other:allBoxes){
            if (other != hitbox) {
                boolean leftCollision = hitbox.getXMin() < other.getXMax();
                boolean rightCollision =  hitbox.getXMax() > other.getXMin();
                boolean hitX = rightCollision && leftCollision;
                boolean hitY = hitbox.getYMin() < other.getYMax() &&
                        hitbox.getYMax() > other.getYMin();

                if( hitX && hitY){
                    if(vertical){
                        if(right) {
                            float heigth = hitbox.getYMax() - hitbox.getYMin();
                            return Optional.of(other.getYMin() - heigth);
                        }else{
                            return Optional.of(other.getYMax());
                        }
                    }else{
                        float width = hitbox.getXMax() - hitbox.getXMin();
                        if(right){
                            return Optional.of(other.getXMin() - (width/2));
                        }else{
                            return Optional.of(other.getXMax() + (width/2));
                        }
                   }
                }
            }
        }
        return Optional.empty();
    }

    public Vector3f pushingForce() {
        entity.getComponent(EntitiyHitboxComponent.class).ifPresent(EntitiyHitboxComponent::updateHitbox);

        Collection<Hitbox> allBoxes = new ArrayList<>(Game.theGame().entities)
                .stream()
                .flatMap(e -> e.getComponent(EntitiyHitboxComponent.class).stream())
                .map(EntitiyHitboxComponent::getHitbox)
                .toList();

        for(var other:allBoxes){
            if (other != hitbox) {
                boolean leftCollision = hitbox.getXMin() < other.getXMax();
                boolean rightCollision =  hitbox.getXMax() > other.getXMin();
                boolean hitX = rightCollision && leftCollision;
                boolean hitY = hitbox.getYMin() < other.getYMax() &&
                        hitbox.getYMax() > other.getYMin();

                if( hitX && hitY){
                    // hit
                    // x is definitely part of the normed space of the other hitbox.
                    float otherWidth = other.getXMax() - other.getXMin();
                    if(hitbox.getXMin() > other.getXMin()){
                        // collision - we come from the right side
                        float overlapping = other.getXMax() - hitbox.getXMin();
                        return new Vector3f(overlapping+0.05f,0,0);
                    }else{
                        // we come from the left side
                        float overlapping = other.getXMin() - hitbox.getXMax();
                        return new Vector3f(overlapping-0.05f,0,0);
                    }
                }
            }
        }
        return new Vector3f();
    }

    public Optional<Hitbox> getHitHitbox(){
        return new ArrayList<>(Game.theGame().entities)
                .stream()
                .flatMap(e -> e.getComponent(EntitiyHitboxComponent.class).stream())
                .map(EntitiyHitboxComponent::getHitbox)
                .filter(h -> h != hitbox)
                .filter((Hitbox other) -> {
                            boolean leftCollision = hitbox.getXMin() < other.getXMax();
                            boolean rightCollision = hitbox.getXMax() > other.getXMin();
                            boolean hitX = rightCollision && leftCollision;
                            boolean hitY = hitbox.getYMin() < other.getYMax() &&
                                    hitbox.getYMax() > other.getYMin();
                            return hitX && hitY;
                        }
                ).findFirst();
    }
}
