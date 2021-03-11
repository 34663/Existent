package zyx.existent.module.modules.visual;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventUpdate;
import zyx.existent.module.Category;
import zyx.existent.module.Module;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ShowInvis extends Module {
    private final List<Entity> entities;

    public ShowInvis(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);
        this.entities = new ArrayList<Entity>();
    }

    @Override
    public void onEnable() {
        for (Entity entity : this.entities) {
            entity.setInvisible(true);
        }
        this.entities.clear();
        super.onEnable();
    }

    @EventTarget
    public void onUpdate(EventUpdate eventUpdate) {
        for (final Entity entity : this.mc.theWorld.loadedEntityList) {
            if (entity.isInvisible() && entity instanceof EntityPlayer) {
                entity.setInvisible(false);
                this.entities.add(entity);
            }
        }
    }
}
