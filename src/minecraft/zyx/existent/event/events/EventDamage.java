package zyx.existent.event.events;

import net.minecraft.entity.Entity;
import zyx.existent.event.Event;

public class EventDamage extends Event {
    Entity entity;

    public EventDamage(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }
}
