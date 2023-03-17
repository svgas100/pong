package de.sga.game.entities.components;

import de.sga.game.entities.BaseEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public abstract class AbstractEntitiyComponent implements EntityComponent{

    protected BaseEntity entity;

    protected AbstractEntitiyComponent(BaseEntity entity){
        this.entity = entity;
    }


    protected <T extends EntityComponent> Optional<T> getComponent(Class<T> component){
        return entity.getComponent(component);
    }

    protected List<EntityComponent> getComponents(){
        return entity.getComponents();
    }
}


