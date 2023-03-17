package de.sga.game.entities.models;

import de.sga.game.renderer.textures.ModelTexture;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TexturedModel {
    private RawModel rawModel;
    private ModelTexture texture;
}
