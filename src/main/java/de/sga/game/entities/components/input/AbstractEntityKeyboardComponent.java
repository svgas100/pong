package de.sga.game.entities.components.input;

import de.sga.game.entities.BaseEntity;
import de.sga.game.entities.components.AbstractEntitiyComponent;
import de.sga.game.ui.KeyboardSingleton;

public class AbstractEntityKeyboardComponent extends AbstractEntitiyComponent {

    protected KeyboardSingleton keyboard = KeyboardSingleton.getInstance();

    public AbstractEntityKeyboardComponent(BaseEntity entity) {
        super(entity);
    }
}
