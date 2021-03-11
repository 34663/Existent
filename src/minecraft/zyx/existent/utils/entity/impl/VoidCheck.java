package zyx.existent.utils.entity.impl;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import zyx.existent.utils.entity.ICheck;

public final class VoidCheck implements ICheck {
    public boolean validate(Entity entity) {
        return isBlockUnder(entity);
    }

    private boolean isBlockUnder(Entity entity) {
        for (int offset = 0; offset < entity.posY + entity.getEyeHeight(); offset += 2) {
            AxisAlignedBB boundingBox = entity.getEntityBoundingBox().offset(0.0D, -offset, 0.0D);
            if (!mc.theWorld.getEntitiesWithinAABBExcludingEntity(entity, boundingBox).isEmpty())
                return true;
        }
        return false;
    }
}
