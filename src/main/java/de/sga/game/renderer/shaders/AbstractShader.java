package de.sga.game.renderer.shaders;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.*;
import java.nio.FloatBuffer;

public abstract class AbstractShader {

    private final int shaderProgramId;
    private final int vertexShaderId;
    private final int fragmentShaderId;

    protected AbstractShader(String vertexFilePath, String fragmentFilePath) {
        vertexShaderId = loadShader(vertexFilePath, GL20.GL_VERTEX_SHADER);
        fragmentShaderId = loadShader(fragmentFilePath, GL20.GL_FRAGMENT_SHADER);
        shaderProgramId = GL20.glCreateProgram();
        GL20.glAttachShader(shaderProgramId, vertexShaderId);
        GL20.glAttachShader(shaderProgramId, fragmentShaderId);
        bindAttributes();
        GL20.glLinkProgram(shaderProgramId);
        GL20.glValidateProgram(shaderProgramId);
        getAllUniformLocations();
    }

    protected abstract void getAllUniformLocations();

    protected int getUniformLocation(String uniformName) {
        return GL20.glGetUniformLocation(shaderProgramId, uniformName);
    }

    protected void loadFloat(int location, float value) {
        GL20.glUniform1f(location, value);
    }

    protected void loadVec3(int location, Vector3f vector3f) {
        GL20.glUniform3f(location, vector3f.x, vector3f.y, vector3f.z);
    }

    protected void loadVec2(int location, Vector2f vector2f) {
        GL20.glUniform2f(location, vector2f.x, vector2f.y);
    }

    protected void loadBoolean(int location, boolean bool) {
        GL20.glUniform1f(location, bool ? 1 : 0);
    }

    protected void loadMat4(int location, Matrix4f mat4f) {
        FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
        mat4f.get(matrixBuffer);
        GL20.glUniformMatrix4fv(location, false,matrixBuffer);
    }

    protected abstract void bindAttributes();

    protected void bindAttribute(int attribute, String variableName) {
        GL20.glBindAttribLocation(shaderProgramId, attribute, variableName);
    }

    public void start() {
        GL20.glUseProgram(shaderProgramId);
    }

    public void stop() {
        GL20.glUseProgram(0);
    }

    public void cleanUp() {
        stop();
        GL20.glDetachShader(shaderProgramId, vertexShaderId);
        GL20.glDeleteShader(vertexShaderId);
        GL20.glDetachShader(shaderProgramId, fragmentShaderId);
        GL20.glDeleteShader(fragmentShaderId);
        GL20.glDeleteProgram(shaderProgramId);
    }

    private static int loadShader(String file, int type) {
        StringBuilder shaderSource = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(AbstractShader.class.getResourceAsStream(file)));
            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line).append("//\n");
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        int shaderID = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderID, shaderSource);
        GL20.glCompileShader(shaderID);
        if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
            System.err.println("Could not compile shader '%s'!".formatted(file));
            System.exit(-1);
        }
        return shaderID;
    }
}
