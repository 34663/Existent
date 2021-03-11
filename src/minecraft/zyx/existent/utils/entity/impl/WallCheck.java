package zyx.existent.utils.entity.impl;

import net.minecraft.entity.Entity;
import zyx.existent.utils.entity.ICheck;

public final class WallCheck implements ICheck {
    public boolean validate(Entity entity) {
        return mc.thePlayer.canEntityBeSeen(entity);
    }
}
