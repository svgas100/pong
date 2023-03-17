package de.sga.game.entities.components;

import de.sga.game.entities.Entity;

public abstract class AbstractEntitiyComponent implements EntityComponent{

    protected Entity entity;

    protected AbstractEntitiyComponent(Entity entity){
        this.entity = entity;
    }


}
