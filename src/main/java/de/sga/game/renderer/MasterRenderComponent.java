package de.sga.game.renderer;

import de.sga.game.entities.Camera;
import de.sga.game.entities.BaseEntity;
import de.sga.game.entities.Light;
import de.sga.game.entities.models.TexturedModel;
import de.sga.game.renderer.entity.EntityRenderer;
import de.sga.game.renderer.font.rendering.TextMaster;
import de.sga.game.renderer.shaders.StaticShader;
import de.sga.game.ui.Display;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class MasterRenderComponent {

    private static final float FOV = 70;
    private static final float NEAR_PLANE = 0.1F;
    private static final float FAR_PLANE = 1000F;

    private StaticShader shader;

    private EntityRenderer entityRenderer;

    private final Map<TexturedModel, List<BaseEntity>> entityBatches = new HashMap<>();

    public MasterRenderComponent(){init();
    }

    public void init() {
        shader = new StaticShader();
        Matrix4f projectionMatrix = createProjectionMatrix();

        entityRenderer = new EntityRenderer(shader, projectionMatrix);

        GL11.glEnable(GL_CULL_FACE);
        GL11.glCullFace(GL_BACK);
    }

    public void render(Light light, Camera camera) {
        GL11.glEnable(GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        shader.start();
        shader.loadLight(light);
        shader.loadViewMatrix(createViewMatrix(camera));
        entityRenderer.renderEntities(entityBatches);
        shader.stop();
        entityBatches.clear();

        TextMaster.render();
    }

    public void processEntity(BaseEntity entity) {
        TexturedModel model = entity.getModel();
        List<BaseEntity> batch = entityBatches.computeIfAbsent(model, (m) -> new ArrayList<>());
        batch.add(entity);
    }

    private static Matrix4f createViewMatrix(Camera camera) {
        var pos = camera.getPositionComponent().getPosition();
        return new Matrix4f().identity()
                .rotateX((float) Math.toRadians(camera.getPitch()))
                .rotateY((float) Math.toRadians(camera.getYaw()))
                .translate(-pos.x, -pos.y, -pos.z)
                ;
    }

    private Matrix4f createProjectionMatrix() {
        Display display = Display.getInstance();
        float aspectRatio = (float) display.getWidth() / (float) display.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;
        return new Matrix4f().identity()
                .m00(x_scale)
                .m11(y_scale)
                .m22(-((FAR_PLANE + NEAR_PLANE) / frustum_length))
                .m23(-1)
                .m32(-((2 * NEAR_PLANE * FAR_PLANE) / frustum_length))
                .m33(0);
    }

    public void updateProjectionMatrix() {
        shader.start();
        shader.loadProjectionMatrix(createProjectionMatrix());
        shader.stop();
    }

    public void destroy() {
        shader.cleanUp();
    }
}
