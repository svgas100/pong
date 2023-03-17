package de.sga.game.entities;

import de.sga.game.entities.components.EntityComponent;
import de.sga.game.entities.components.physics.EntityPositionComponent;
import de.sga.game.entities.models.TexturedModel;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.*;

@Getter
public class BaseEntity {

    protected EntityPositionComponent positionComponent;

    protected List<EntityComponent> components = new ArrayList<>() {
        public boolean add(EntityComponent comp) {
            super.add(comp);
            Collections.sort(components, Comparator.comparing(EntityComponent::getOrder));
            return true;
        }
    };

    protected TexturedModel model;
    protected Vector3f rotation;
    protected float scale;

    public BaseEntity(){
    }

    public BaseEntity(Vector3f position){
        positionComponent = createPositionComponent(position);
        components.add(positionComponent);
    }

    public BaseEntity(TexturedModel model, Vector3f position, Vector3f rotation, float scale, int textureIndex) {
        this(position);
        this.model = model;
        this.rotation = rotation;
        this.scale = scale;
        this.textureIndex = textureIndex;
    }

    public <EC extends EntityComponent> Optional<EC> getComponent(Class<EC> component) {
        return components.stream().filter(c -> component.isAssignableFrom(c.getClass())).findAny().map(component::cast);
    }

    @Setter
    protected int textureIndex = 0;

    public Vector2f getTextureOffset() {
        int rows = model.getTexture().getRows();
        float xOffset = ((float) (textureIndex % rows)) / rows;
        float yOffset = ((float) (textureIndex / rows)) / rows;
        return new Vector2f(xOffset, yOffset);
    }

    protected EntityPositionComponent createPositionComponent(Vector3f position){
        return new EntityPositionComponent(this,position);
    }

    public void setRotation(Vector3f adjustment) {
        rotation.set(adjustment);
    }

    public void adjustScale(float scale) {
        this.scale = scale;
    }
}
