package de.sga.game.entities.components.input;

import de.sga.game.entities.Entity;
import de.sga.game.entities.components.AbstractEntitiyComponent;
import de.sga.game.ui.KeyboardSingleton;

public class AbstractEntityKeyboardComponent extends AbstractEntitiyComponent {

    protected KeyboardSingleton keyboard = KeyboardSingleton.getInstance();

    public AbstractEntityKeyboardComponent(Entity entity) {
        super(entity);
    }
}
