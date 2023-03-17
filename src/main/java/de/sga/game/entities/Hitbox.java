package de.sga.game.entities;

import de.sga.game.entities.components.collision.EntitiyCollisionComponent;
import de.sga.game.entities.components.position.AttachingEntityPositionComponent;
import de.sga.game.entities.components.position.EntityPositionComponent;
import de.sga.game.entities.models.RawModel;
import de.sga.game.entities.models.TexturedModel;
import de.sga.game.entities.util.EntitiyLoader;
import de.sga.game.renderer.textures.ModelTexture;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

public class Hitbox extends Entity {

    @Getter
    private final Entity boundEntity;

    private static final int VERTEX_COUNT = 2;

    @Getter
    private float xMin;

    @Getter
    private float yMin;

    @Getter
    private float xMax;

    @Getter
    private float yMax;

    @Getter
    private float offsetX;

    @Getter
    private float sizeX;

    @Getter
    private float sizeY;

    @Override
    public Vector3f getRotation() {
        return new Vector3f();
    }

    public Hitbox(float sizeX, float sizeY, float offsetX, Entity bound, ModelTexture texture) {
        components.add(new AttachingEntityPositionComponent(this,new Vector3f(bound.getPositionComponent().getPosition().sub(
                new Vector3f((sizeX / 2.0F + offsetX) * bound.getScale(), 0, 0)))));

        this.offsetX = offsetX;
        this.sizeY = sizeY;
        this.sizeX = sizeX;
        this.model = new TexturedModel(generateModel(sizeX, sizeY), texture);
        this.rotation = bound.getRotation();
        this.scale = bound.getScale();
        this.textureIndex = 0;
        this.boundEntity = bound;

        // intital update of the hitbox.
        update();
    }

    @Override
    public AttachingEntityPositionComponent getPositionComponent() {
        return getComponent(AttachingEntityPositionComponent.class).get();
    }

    public void update(){
        scale = boundEntity.getScale();

        float rot;
        if(super.getRotation().equals(new Vector3f())){
             rot = 1;
        }else{
            rot = -1;
        }

        getPositionComponent().setPosition(
                new Vector3f(boundEntity.getPositionComponent().getPosition().sub(
                        new Vector3f((sizeX / 2.0F + (rot * offsetX)) * boundEntity.getScale(), 0, 0))));
        float x = boundEntity.getPositionComponent().getPosition().x;
        float y = boundEntity.getPositionComponent().getPosition().y;

        xMin = x - ((sizeX / 2.0F + (rot * offsetX)) * boundEntity.getScale());
        xMax = x + ((sizeX / 2.0F - (rot * offsetX)) * boundEntity.getScale());
        yMin = y;
        yMax = y + (sizeY * boundEntity.getScale());
    }

    private static RawModel generateModel(float sizeX, float sizeY) {
        int count = VERTEX_COUNT * VERTEX_COUNT;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count * 2];
        int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)];
        int vertexPointer = 0;
        for (int i = 0; i < VERTEX_COUNT; i++) {
            for (int j = 0; j < VERTEX_COUNT; j++) {
                vertices[vertexPointer * 3] = (float) j / ((float) VERTEX_COUNT - 1) * sizeX;
                vertices[vertexPointer * 3 + 1] = (float) i / ((float) VERTEX_COUNT - 1) * sizeY;
                vertices[vertexPointer * 3 + 2] = 0;
                normals[vertexPointer * 3] = 0;
                normals[vertexPointer * 3 + 1] = 0;
                normals[vertexPointer * 3 + 2] = 1;
                int x = vertexPointer * 2;
                textureCoords[x] = (float) j / ((float) VERTEX_COUNT - 1);
                textureCoords[x + 1] = 1 - (float) i / ((float) VERTEX_COUNT - 1);
                vertexPointer++;
            }
        }
        int pointer = 0;
        for (int gy = 0; gy < VERTEX_COUNT - 1; gy++) {
            for (int gx = 0; gx < VERTEX_COUNT - 1; gx++) {
                int topLeft = ((gy + 1) * VERTEX_COUNT) + gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gy) * VERTEX_COUNT) + gx;
                int bottomRight = bottomLeft + 1;

                indices[pointer++] = bottomRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topLeft;
            }
        }
        return EntitiyLoader.loadToVAO(vertices, textureCoords, indices, normals);
    }

    @Override
    public String toString() {
        return boundEntity.getClass().getSimpleName();
    }
}
