package de.sga.game.framework.utils;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class MatrixUtil {

    public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation, float scale){
        return new Matrix4f().identity().translate(translation)
                .rotateX((float)Math.toRadians(rotation.x))
                .rotateY((float)Math.toRadians(rotation.y))
                .rotateZ((float)Math.toRadians(rotation.z))
                .scale(scale);
    }
}
