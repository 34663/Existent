package zyx.existent.event.events;

import net.minecraft.entity.Entity;
import zyx.existent.event.Event;

public class EventAttack extends Event {
    private Entity entity;
    private boolean preAttack;

    public EventAttack(Entity targetEntity, boolean preAttack) {
        this.entity = targetEntity;
        this.preAttack = preAttack;
    }

    public Entity getEntity() {
        return entity;
    }

    public boolean isPreAttack() {
        return preAttack;
    }

    public boolean isPostAttack() {
        return !preAttack;
    }
}
