package de.sga.game.entities.components;

import de.sga.game.entities.BaseEntity;

public abstract class AbstractEntitiyComponent implements EntityComponent{

    protected BaseEntity entity;

    protected AbstractEntitiyComponent(BaseEntity entity){
        this.entity = entity;
    }


}
