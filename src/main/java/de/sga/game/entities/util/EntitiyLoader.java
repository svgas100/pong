package de.sga.game.entities.util;

import de.sga.game.entities.models.RawModel;
import de.sga.game.renderer.textures.Texture;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class EntitiyLoader {

    private static final List<Integer> vaos = new ArrayList<>();
    private static final List<Integer> vbos = new ArrayList<>();
    private static final List<Integer> textures = new ArrayList<>();

    private static final List<Integer> normals = new ArrayList<>();

    public static RawModel loadToVAO(float[] positions, float[] textureCoords, int[] indices, float[] normals) {
        int vaoId = createVAO();
        bindIndiciesBuffer(indices);

        storeDataInAttributeList(0, positions, 3);

        float xMin = Float.MAX_VALUE;
        float xMax = Float.MIN_VALUE;
        float yMin = Float.MIN_VALUE;
        float yMax = Float.MAX_VALUE;

        for (int i =0; i < positions.length; i++){
            if(i%3 == 0){
                //x
                xMin = Math.min(xMin,positions[i]);
                xMax = Math.max(xMax,positions[i]);
            }else if(i%3 == 1){
                //y
                yMin = Math.min(yMin,positions[i]);
                yMax = Math.max(yMax,positions[i]);
            }
        }

            storeDataInAttributeList(1, textureCoords, 2);
        storeDataInAttributeList(2, normals, 3);

        unbindVAO();
        return new RawModel(vaoId, indices.length, new Vector4f(xMin,xMax,yMin,yMax));
    }

    public static int loadToVAO(float[] positions, float[] textureCoords) {
        int vaoId = createVAO();
        storeDataInAttributeList(0, positions, 2);
        storeDataInAttributeList(1, textureCoords, 2);
        unbindVAO();
        return vaoId;
    }

    public static int loadTexture(String textureFile) {
        Texture texture = new Texture(textureFile);

        int textureId = texture.getTextureID();
        textures.add(textureId);
        return textureId;
    }

    public static void cleanUp() {
        for (int vaoId : vaos) {
            GL30.glDeleteVertexArrays(vaoId);
        }

        for (int vboId : vbos) {
            GL15.glDeleteBuffers(vboId);
        }

        for (int texture : textures) {
            GL11.glDeleteTextures(texture);
        }
    }

    private static int createVAO() {
        int vaoId = GL30.glGenVertexArrays();
        vaos.add(vaoId);
        GL30.glBindVertexArray(vaoId);
        return vaoId;
    }

    private static void storeDataInAttributeList(int attributeNumber, float[] data, int size) {
        int vboId = GL15.glGenBuffers();
        vbos.add(vboId);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, size, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private static void storeDataInAttributeList(int attributeNumber, int[] data, int size) {
        int vboId = GL15.glGenBuffers();
        vbos.add(vboId);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        IntBuffer buffer = storeDataInIntBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, size, GL11.GL_INT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private static void unbindVAO() {
        GL30.glBindVertexArray(0);
    }

    private static void bindIndiciesBuffer(int[] indices) {
        int vboId = GL15.glGenBuffers();
        vbos.add(vboId);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboId);
        IntBuffer buffer = storeDataInIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    private static IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private static FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }


}
