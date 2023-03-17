package de.sga.game.entities.components;

public interface EntityComponent {

    default int getOrder(){
        return 0;
    }

    default void onTick(){
        // nothing - components can override this method.
    }

    default void onFrame(){
        // nothing - components can override this method.
    }
}
