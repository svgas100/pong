package de.sga.game.entities.components.physics.collision;

import de.sga.game.entities.BaseEntity;
import de.sga.game.entities.Hitbox;
import de.sga.game.entities.components.AbstractEntitiyComponent;
import de.sga.game.entities.util.EntitiyLoader;
import de.sga.game.renderer.textures.ModelTexture;
import de.sga.game.ui.KeyboardSingleton;
import lombok.Getter;
import org.lwjgl.glfw.GLFW;

public class EntitiyHitboxComponent extends AbstractEntitiyComponent {

    @Getter
    private final Hitbox hitbox;

    @Getter
    private boolean debug = false;

    public EntitiyHitboxComponent(BaseEntity entity, float sizeX, float sizeY, float offsetX) {
        super(entity);
        ModelTexture modelTexture = new ModelTexture(EntitiyLoader.loadTexture("/de/sga/renderer/textures/border.png"), 10, 0, 1);
        hitbox = new Hitbox(sizeX, sizeY, offsetX, entity, modelTexture);
    }

    public EntitiyHitboxComponent(BaseEntity entity, float sizeX, float sizeY) {
        this(entity,sizeX,sizeY,0);
    }

    @Override
    public void onTick() {
        if (KeyboardSingleton.getInstance().isKeyPressed(GLFW.GLFW_KEY_B)) {
            debug = !debug;
        }
    }

    public void updateHitbox(){
        hitbox.update();
    }
}
