package de.sga.game.renderer.shaders;

import de.sga.game.entities.Light;
import org.joml.Matrix4f;
import org.joml.Vector2f;

public class StaticShader extends AbstractShader {

    private static final String VERTEX_FILE = "/de/sga/renderer/shaders/vertex.glsl";
    private static final String FRAGMENT_FILE = "/de/sga/renderer/shaders/fragment.glsl";

    private int location_transformationMatrix;
    private int location_projectionMatrix;

    private int location_viewMatrix;

    private int location_lightPosition;
    private int location_lightColor;

    private int location_shinyDamper;

    private int location_reflectivity;

    private int location_numberOfRows;
    private int location_offset;

    public StaticShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = getUniformLocation("transformation");
        location_projectionMatrix = getUniformLocation("projection");
        location_viewMatrix = getUniformLocation("view");
        location_lightPosition = getUniformLocation("lightPosition");
        location_lightColor = getUniformLocation("lightColor");
        location_shinyDamper = getUniformLocation("shinyDamper");
        location_reflectivity = getUniformLocation("reflectivity");
        location_numberOfRows = getUniformLocation("numberOfRows");
        location_offset = getUniformLocation("offset");
    }

    public void loadTransformationMatrix(Matrix4f mat4f) {
        loadMat4(location_transformationMatrix, mat4f);
    }

    public void loadReflectivity(float reflectivity) {
        loadFloat(location_reflectivity, reflectivity);
    }

    public void loadNumberOfRows(float numberOffRows){
        loadFloat(location_numberOfRows,numberOffRows);
    }

    public void loadTextureOffset(Vector2f offset){
        loadVec2(location_offset,offset);
    }

    public void loadShinyDamper(float shinyDamper) {
        loadFloat(location_shinyDamper, shinyDamper);
    }

    public void loadProjectionMatrix(Matrix4f mat4f) {
        loadMat4(location_projectionMatrix, mat4f);
    }


    public void loadViewMatrix(Matrix4f mat4f) {
        loadMat4(location_viewMatrix, mat4f);
    }

    public void loadLight(Light light) {
        loadVec3(location_lightPosition, light.getPositionComponent().getPosition());
        loadVec3(location_lightColor, light.getColor());
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
        super.bindAttribute(2, "normal");
    }
}
