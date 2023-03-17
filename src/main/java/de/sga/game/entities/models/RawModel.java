package de.sga.game.entities.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.joml.Vector4f;

@AllArgsConstructor
@Getter
public class RawModel {
    private int vaoId;
    private int vertexCount;
    private Vector4f minMax;
}
