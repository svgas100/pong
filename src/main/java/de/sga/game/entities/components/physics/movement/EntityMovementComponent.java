package de.sga.game.entities.components.physics.movement;

import de.sga.game.entities.BaseEntity;
import de.sga.game.entities.components.AbstractEntitiyComponent;
import de.sga.game.entities.components.physics.collision.CollisionListenerComponent;
import de.sga.game.entities.components.physics.EntityPositionComponent;
import lombok.Getter;
import org.joml.Vector3f;

import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Math.min;

public class EntityMovementComponent extends AbstractEntitiyComponent implements CollisionListenerComponent {

    /**
     * Describes the maxmimal speed at which the entity assigned to this component may move.
     * <p>
     * The entity may move slower or not at all, but never faster then the allowed max speed.
     * (when moving with this component, other component may not be bound to this speed limit.)
     * </p>
     */
    private final float maxSpeed;

    /**
     * Described the maximal acceleration of the attached entity.
     * <p>
     * The max acceleration of the entity, when the max speed is reached the maximal possible acceleration is zero.
     * As the entity is not allowed to move faster.
     * The acceleration is only an upper limit of movement change rate. The entity may "stop" faster but may not
     * move faster tin the other direction.
     * </p>
     */
    private final float maxAcceleration;

    /**
     * Flag indicating is an entity was moved during this tick.
     */
    @Getter
    private boolean didMove = false;

    private boolean decay;

    private boolean invertMovementOnHit;

    private boolean hitX =false;

    private boolean hitY = false;

    private final Vector3f movement = new Vector3f();

    public EntityMovementComponent(BaseEntity entity, float maxSpeed, float maxAcceleration, boolean decay, boolean invertMovementOnHit) {
        super(entity);
        this.maxAcceleration = maxAcceleration;
        this.maxSpeed = maxSpeed;
        this.decay = decay;
        this.invertMovementOnHit = invertMovementOnHit;
    }

    @Override
    public void onTick() {
        EntityPositionComponent posComponent = entity.getPositionComponent();

        // movement = direction * speed (+/- determine the direction while the amount is the speed)
        // movement is like the dt (the amount the position should be changed each tick.)

        posComponent.adjustPosition(movement);

        // in case the movement vector is unequal to the (0,0,0) vector the entity did move
        didMove = !movement.equals(new Vector3f());

        if(invertMovementOnHit){
            var comp = entity.getPositionComponent();

            Vector3f colMovement = new Vector3f();
            if(hitX) {
                colMovement.add(-movement.x*2,0,0);
                movement.set(-movement.x,movement.y,movement.z);
            }

            if(hitY){
                colMovement.add(0,-movement.y*2,0);
                movement.set(movement.x,-movement.y,movement.z);
            }
            posComponent.adjustPosition(colMovement);
        }

        //post movement
        if(decay) {
            decayMovement();
        }

        hitX = false;
        hitY = false;
    }

    private void decayMovement() {
        var comp = entity.getPositionComponent();
        AtomicReference<Float> xDecay = new AtomicReference<>((float) 0);
        if((movement.x < 0.05 && movement.x > -0.05) || hitX){
            xDecay.set(0f);
            movement.set(0f,movement.y,movement.z);
        }else {
            if (movement.x < 0) {
                xDecay.set(Math.max(movement.x, -0.25F));
            } else if (movement.x > 0) {
                xDecay.set(min(movement.x, 0.25F));
            }
        }

        AtomicReference<Float> yDecay = new AtomicReference<>((float) 0);
        if(movement.y < 0.05 && movement.y > -0.05 || hitY){
            yDecay.set(0f);
            movement.set(movement.x,0f,movement.z );
        }else {
            if (movement.y < 0) {
                yDecay.set(Math.max(movement.y, -0.25F));
            } else if (movement.y > 0) {
                yDecay.set(min(movement.y, 0.25F));
            }
        }

        if(hitX){
            yDecay.set(movement.y);
        }

        if(hitY){
            xDecay.set(movement.x);
        }

        movement.sub(new Vector3f(xDecay.get(), yDecay.get(), 0));
    }

    public void moveLeft() {
        if(!entity.getRotation().equals(new Vector3f(0))){
            entity.setRotation(new Vector3f(0));
        }
        movement.set(Math.max(movement.x - getSpeedChange(), -maxSpeed), movement.y, movement.z);
    }

    public void moveRight() {
        if(entity.getRotation().equals(new Vector3f(0))){
            entity.setRotation(new Vector3f(0,180,0));
        }
        movement.set(min(movement.x + getSpeedChange(), maxSpeed), movement.y, movement.z);
    }

    public void moveUp() {
        movement.set(movement.x, min(movement.y + getSpeedChange(), maxSpeed), movement.z);
    }

    public void moveDown() {
        movement.set(movement.x, Math.max(movement.y - getSpeedChange(), -maxSpeed), movement.z);
    }

    public void setYMovement(Float yMovement){
        if(yMovement < 0){
            movement.set(movement.x, Math.max(yMovement,-maxSpeed), movement.z);
        }else{
            movement.set(movement.x, Math.min(yMovement,maxSpeed), movement.z);
        }
    }

    private float getSpeedChange() {
        AtomicReference<Float> speedChange = new AtomicReference<>(maxAcceleration);
        return speedChange.get();
    }

    @Override
    public void xCollision(HorizontalMovementDirection direction) {
        hitX = true;
    }

    @Override
    public void yCollision(VerticalMovementDirection direction) {
        hitY = true;
    }
}
