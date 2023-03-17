package de.sga.game.renderer.textures;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class ModelTexture {
    private int textureId;

    private float shinyDamper;
    private float reflectivity;

    @Setter
    private int rows = 1;
}
