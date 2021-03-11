package zyx.existent.module.modules.player;

import net.minecraft.entity.player.EnumPlayerModelParts;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventTick;
import zyx.existent.module.Category;
import zyx.existent.module.Module;

import java.util.Random;

public class SkinFlash extends Module {
    public SkinFlash(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);
    }

    @Override
    public void onDisable() {
        if (this.mc.thePlayer != null) {
            EnumPlayerModelParts[] parts = EnumPlayerModelParts.values();
            if (parts != null) {
                EnumPlayerModelParts[] arrayOfEnumPlayerModelParts1;
                int j = (arrayOfEnumPlayerModelParts1 = parts).length;
                for (int i = 0; i < j; i++) {
                    EnumPlayerModelParts part = arrayOfEnumPlayerModelParts1[i];
                    this.mc.gameSettings.setModelPartEnabled(part, true);
                }
            }
        }
        super.onDisable();
    }

    @EventTarget
    public void onTick(EventTick eventTick) {
        if (this.mc.thePlayer != null) {
            EnumPlayerModelParts[] parts = EnumPlayerModelParts.values();
            if (parts != null) {
                EnumPlayerModelParts[] arrayOfEnumPlayerModelParts1;
                int j = (arrayOfEnumPlayerModelParts1 = parts).length;
                for (int i = 0; i < j; i++) {
                    EnumPlayerModelParts part = arrayOfEnumPlayerModelParts1[i];
                    boolean newState = !isEnabled() || (new Random()).nextBoolean();
                    this.mc.gameSettings.setModelPartEnabled(part, newState);
                }
            }
        }
    }
}
