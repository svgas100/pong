package de.sga.game.entities.components.position;

import de.sga.game.entities.BaseEntity;
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

    /**
     * The current position of the entity managed by this component.
     */
    protected Vector3f position;

    /**
     * The current requested change of position for the current tick-cycle.
     * <p>
     *     This must be reset after each tick.
     * </p>
     */
    protected Vector3f positionChange = new Vector3f();


    /**
     * {@code true} in case the current {@link #positionChange#x} could not be executed without colliding with another entity.
     * {@code false} otherwise.
     * <p>
     *    <ul>
     *        <li><b>both</b> entites need a {@link EntitiyCollisionComponent} in order to be "hittable"</li>
     *    </ul>
     * </p>
     */
    @Getter
    protected boolean horizontalHit = false;

    /**
     * {@code true} in case the current {@link #positionChange#y} could not be executed without colliding with another entity.
     * {@code false} otherwise.
     * <p>
     *    <ul>
     *        <li><b>both</b> entites need a {@link EntitiyCollisionComponent} in order to be "hittable"</li>
     *    </ul>
     * </p>
     */
    @Getter
    protected boolean verticalHit = false;
    public EntityPositionComponent(BaseEntity entity, Vector3f initialPosition) {
        super(entity);
        position = new Vector3f(initialPosition);
    }

    /**
     * Explicitly ridden getter in order to always return a copy of the position and never the actual object.
     *
     * @return a copy of the current entity position in the world.
     */
    public Vector3f getPosition() {
        return new Vector3f(position);
    }

    /**
     * Adds a requested position change to the {@link #positionChange}-Vector which will be used for the next tick-cycle.
     * @param positionChangeVector the amount of position change to add to the {@link #positionChange}-Vector.
     */
    public void adjustPosition(Vector3f positionChangeVector) {
        positionChange.add(positionChangeVector);
    }

    /**
     * Sets the position vector forcefully to the new provided position.
     * <p>
     *     Use carefully as this does not perform all relevant movement checks.
     *     Usefully for attached objects like camera, hitboxes etc. who's position are "linked" to other entities.
     * </p>
     * @param newPosition the new position in the world.
     */
    public void setPositionForcefully(Vector3f newPosition) {
        position.set(newPosition);

        if (getCollisionComponent().isPresent()) {
            var comp = getCollisionComponent().get();
            if (comp.isAnyHit()) {
                position.add(comp.pushingForce());
            }
        }

        entity.getComponent(EntitiyHitboxComponent.class).ifPresent(EntitiyHitboxComponent::updateHitbox);
    }
    @Override
    public void onTick() {
        if (getCollisionComponent().isEmpty()) {
            // in case we have no collision component, we can just adjust the position as desired.
            position.add(positionChange);
        } else {
            // we perform the movement in separate steps in order to enable collision detection during each step.
            // we first move horizontally
            performXMovement();
            // and then vertically
            performYMovement();

            // ideally we would have no collision anymore but sometimes the entity "glitches" into other entities.
            // we therefore add a force in order to push them apart.
            if (getCollisionComponent().get().isAnyHit()) {
                position.add(useCollisionComponent().pushingForce());
            }
        }

        // we update the hitbox of the entity (in case it is present) in order to have a consistent collision detection
        // between all entities.
        entity.getComponent(EntitiyHitboxComponent.class).ifPresent(EntitiyHitboxComponent::updateHitbox);

        positionChange = new Vector3f();
    }
    private void performXMovement() {
        position.add(positionChange.x, 0, 0);
        var horizontalMovDirection = HorizontalMovementDirection.getDirectionFromChange(positionChange);
        var isHit = useCollisionComponent().isHorizontalHit(horizontalMovDirection);
        horizontalHit = isHit.isPresent();
        if (!horizontalHit) {
            return;
        }
        adjustHorizontalMovement(isHit.get(),horizontalMovDirection);
    }

    private void performYMovement() {
        position.add(0, positionChange.y, 0);
        var verticalMovDirection = VerticalMovementDirection.getDirectionFromChange(positionChange);
        var isHit = useCollisionComponent().isVerticalHit(verticalMovDirection);
        verticalHit = isHit.isPresent();
        if (!verticalHit) {
            return;
        }
        adjustVerticalMovement(isHit.get(), verticalMovDirection);
    }

    private void adjustVerticalMovement(float yHitEntity, VerticalMovementDirection verticalMovDirection) {
        float pushDirection = switch (verticalMovDirection) {
            case DOWN -> 0.01F;
            case UP -> -0.01F;
            default -> 0f;
        };

        // revert to previous movement
        position.sub(0, positionChange.y, 0);

        // we try to move just before the actually hit entity
        float yDiff = yHitEntity - position.y + pushDirection;
        position.add(0, yDiff, 0);
    }

    private void adjustHorizontalMovement(float xHitEntity, HorizontalMovementDirection horizontalMovDirection) {
        float pushDirection = switch (horizontalMovDirection) {
            case LEFT -> 0.01F;
            case RIGHT -> -0.01F;
            default -> 0f;
        };

        // revert to previous movement
        position.sub(positionChange.x, 0, 0);

        // we try to move just before the actually hit entity
        float xDiff = xHitEntity - position.x + pushDirection;
        position.add(xDiff, 0, 0);
    }

    public enum VerticalMovementDirection {
        UP,
        DOWN,

        NONE;

        static VerticalMovementDirection getDirectionFromChange(Vector3f positionChangeVector) {
            if (positionChangeVector.y > 0) {
                return UP;
            } else if (positionChangeVector.y < 0) {
                return DOWN;
            }
            return NONE;
        }
    }

    public enum HorizontalMovementDirection {
        LEFT,
        RIGHT,

        NONE;

        static HorizontalMovementDirection getDirectionFromChange(Vector3f positionChangeVector) {
            if (positionChangeVector.x > 0) {
                return RIGHT;
            } else if (positionChangeVector.x < 0) {
                return LEFT;
            }
            return NONE;
        }
    }

    private EntitiyCollisionComponent useCollisionComponent(){
        return getCollisionComponent().orElseThrow(() -> new IllegalStateException("Used collision component without ensuring that it is there!"));
    }

    public Optional<EntitiyCollisionComponent> getCollisionComponent() {
        return entity.getComponent(EntitiyCollisionComponent.class);
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
