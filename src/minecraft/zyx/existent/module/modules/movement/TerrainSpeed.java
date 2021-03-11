package zyx.existent.module.modules.movement;

import net.minecraft.init.Blocks;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventUpdate;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Setting;

public class TerrainSpeed extends Module {
    private final String ICE = "ICE";
    private final String LADDER = "LADDER";

    public TerrainSpeed(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(ICE, new Setting(ICE, false, "Ice."));
        settings.put(LADDER, new Setting(LADDER, false, "Ladder."));
    }

    @Override
    public void onDisable() {
        Blocks.ICE.slipperiness = 0.98F;
        Blocks.PACKED_ICE.slipperiness = 0.98F;
        super.onDisable();
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if ((Boolean) settings.get(LADDER).getValue() && mc.thePlayer != null && !mc.thePlayer.isMoving() && mc.thePlayer.isCollidedHorizontally && mc.thePlayer.isOnLadder()) {
            mc.thePlayer.motionY = 0.287245111D;
        }
        if ((Boolean) settings.get(ICE).getValue()) {
            Blocks.ICE.slipperiness = 0.4F;
            Blocks.PACKED_ICE.slipperiness = 0.4F;
        }
    }
}
