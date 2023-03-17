package de.sga.game.entities.util;

import de.sga.game.entities.models.RawModel;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public final class OBJLoader {

    public static RawModel loadModel(String objPath ) {
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        float[] verticesArray;
        float[] texturesArray = null;
        float[] normalsArray = null;
        int[] indicesArray;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(OBJLoader.class.getResourceAsStream(objPath)))) {
            String line;

            loop:
            while ((line = reader.readLine()) != null) {
                String[] lineContent = line.split(" ");
                switch (lineContent[0]) {
                    case "v":
                        vertices.add(new Vector3f(Float.parseFloat(lineContent[1]), Float.parseFloat(lineContent[2]), Float.parseFloat(lineContent[3])));
                        break;

                    case "vt":
                        textures.add(new Vector2f(Float.parseFloat(lineContent[1]), Float.parseFloat(lineContent[2])));
                        break;

                    case "vn":
                        normals.add(new Vector3f(Float.parseFloat(lineContent[1]), Float.parseFloat(lineContent[2]), Float.parseFloat(lineContent[3])));
                        break;

                    case "f":
                        if (texturesArray == null || normalsArray == null) {
                            texturesArray = new float[vertices.size() * 2];
                            normalsArray = new float[vertices.size() * 3];
                        }
                        break loop;
                    default:
                        continue;
                }
            }

            while (line != null) {
                if (!line.startsWith("f")) {
                    line = reader.readLine();
                    continue;
                }
                String[] lineContent = line.split(" ");
                String[] vertex1 = lineContent[1].split("/");
                String[] vertex2 = lineContent[2].split("/");
                String[] vertex3 = lineContent[3].split("/");

                processVertex(vertex1, indices, textures, normals, texturesArray, normalsArray);
                processVertex(vertex2, indices, textures, normals, texturesArray, normalsArray);
                processVertex(vertex3, indices, textures, normals, texturesArray, normalsArray);
                line = reader.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        verticesArray = new float[vertices.size() * 3];
        indicesArray = new int[indices.size()];

        int vertexPointer = 0;
        for (Vector3f vertex : vertices) {
            verticesArray[vertexPointer++] = vertex.x;
            verticesArray[vertexPointer++] = vertex.y;
            verticesArray[vertexPointer++] = vertex.z;
        }

        for (int i = 0; i < indices.size(); i++) {
            indicesArray[i] = indices.get(i);
        }

        return EntitiyLoader.loadToVAO(verticesArray, texturesArray, indicesArray, normalsArray);
    }

    private static void processVertex(String[] vertexData, List<Integer> indices, List<Vector2f> textures,
                                      List<Vector3f> normals, float[] textureArray, float[] normalsArray) {
        int currentVertexPoint = Integer.parseInt(vertexData[0]) - 1;
        indices.add(currentVertexPoint);
        try {
            Vector2f currentTex = textures.get(Integer.parseInt(vertexData[1]) - 1);
            textureArray[currentVertexPoint * 2] = currentTex.x;
            textureArray[currentVertexPoint * 2 + 1] = 1 - currentTex.y;

            Vector3f currentNorm = normals.get(Integer.parseInt(vertexData[2]) - 1);
            normalsArray[currentVertexPoint * 3] = currentNorm.x;
            normalsArray[currentVertexPoint * 3 + 1] = currentNorm.y;
            normalsArray[currentVertexPoint * 3 + 2] = currentNorm.z;
        } catch (IndexOutOfBoundsException exx) {
            throw new IllegalStateException("Invalid model file detected!");
        }
    }
}
