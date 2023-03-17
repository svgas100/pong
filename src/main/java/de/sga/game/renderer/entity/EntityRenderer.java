package de.sga.game.renderer.entity;

import de.sga.game.entities.Entity;
import de.sga.game.entities.Hitbox;
import de.sga.game.entities.components.collision.EntitiyCollisionComponent;
import de.sga.game.entities.components.collision.EntitiyHitboxComponent;
import de.sga.game.entities.components.position.EntityPositionComponent;
import de.sga.game.entities.models.TexturedModel;
import de.sga.game.entities.util.EntitiyLoader;
import de.sga.game.framework.utils.MatrixUtil;
import de.sga.game.renderer.shaders.StaticShader;
import de.sga.game.renderer.textures.ModelTexture;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;

public class EntityRenderer {

    private final StaticShader shader;

    public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }


    public void renderEntities(Map<TexturedModel, List<Entity>> entityBatches) {
        for (Map.Entry<TexturedModel, List<Entity>> batch : entityBatches.entrySet()) {
            if (batch.getKey() == null) {
                continue;
            }
            prepareTexturedModel(batch.getKey());
            batch.getValue().forEach(entity -> {
                shader.loadTextureOffset(entity.getTextureOffset());

                var posComp = entity.getPositionComponent();
                shader.loadTransformationMatrix(MatrixUtil.createTransformationMatrix(posComp.getPosition(), entity.getRotation(), entity.getScale()));
                GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModel().getRawModel().getVertexCount(), GL_UNSIGNED_INT, 0);
            });

            unbindTexturedModel();

            batch.getValue().forEach(e -> {
                var optCollosion = e.getComponent(EntitiyHitboxComponent.class);
                if (optCollosion.isPresent() && optCollosion.get().isDebug()) {
                    Hitbox h = optCollosion.get().getHitbox();
                    prepareTexturedModel(h.getModel());
                    shader.loadTextureOffset(h.getTextureOffset());
                    shader.loadTransformationMatrix(MatrixUtil.createTransformationMatrix(h.getPositionComponent().getPosition(), h.getRotation(), h.getScale()));
                    GL11.glDrawElements(GL11.GL_TRIANGLES, h.getModel().getRawModel().getVertexCount(), GL_UNSIGNED_INT, 0);
                }
            });
        }
    }

    private void prepareTexturedModel(TexturedModel texturedModel) {
        ModelTexture texture = texturedModel.getTexture();
        GL30.glBindVertexArray(texturedModel.getRawModel().getVaoId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        shader.loadNumberOfRows(texturedModel.getTexture().getRows());
        shader.loadReflectivity(texture.getReflectivity());
        shader.loadShinyDamper(texture.getShinyDamper());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureId());
    }

    private void unbindTexturedModel() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);

        GL30.glBindVertexArray(0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }
}
