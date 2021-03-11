package zyx.existent.utils.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

@FunctionalInterface
public interface ICheck {
    static Minecraft mc = Minecraft.getMinecraft();

    boolean validate(Entity paramEntity);
}
