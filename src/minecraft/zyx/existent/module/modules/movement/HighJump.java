package zyx.existent.module.modules.movement;

import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventMove;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Options;
import zyx.existent.module.data.Setting;
import zyx.existent.utils.MoveUtils;

public class HighJump extends Module {
    public final String MODE = "MODE";
    double mario = 0;
    double luigi = 1337;
    boolean AAAA = false;

    public HighJump(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(MODE,new Setting(MODE,new Options("Mode", "Hypixel", new String[] {"Hypixel", "Hypixel2"}),"nigas"));
    }

    @EventTarget
    public void onMove(EventMove event) {
        String currentMode = ((Options) settings.get(MODE).getValue()).getSelected();

        switch (currentMode) {
            case "Hypixel":
            case "Hypixel2":
                if (currentMode.equalsIgnoreCase("Hypixel") && mc.thePlayer.fallDistance >= 8 && mc.thePlayer.motionY <= 0 && (!AAAA || mc.thePlayer.posY <= mario) && mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.thePlayer, mc.thePlayer.getEntityBoundingBox()
                        .offset(0, 0, 0)
                        .expand(0, 0, 0))
                        .isEmpty() && mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.thePlayer, mc.thePlayer.getEntityBoundingBox()
                        .offset(0, -10002.25, 0)
                        .expand(0, -10003.75, 0))
                        .isEmpty()) {
                    mc.thePlayer.motionY = 200 * 0.01;
                    mc.thePlayer.motionX = 20 * 0.01;
                    mc.thePlayer.motionZ = 0;
                    event.setX(0);
                    event.setZ(0);
                    mario = mc.thePlayer.posY;
                    AAAA = true;
                    MoveUtils.setMotion(MoveUtils.getSpeed());
                } else if (currentMode.equalsIgnoreCase("Hypixel2")) {
                    if (mc.thePlayer.fallDistance >= 8 && mc.thePlayer.motionY <= 0 && (!AAAA || mc.thePlayer.posY <= mario)) {
                        mc.thePlayer.motionY = 200 * 0.01;
                        mc.thePlayer.motionX = 20 * 0.01;
                        mc.thePlayer.motionZ = 0;
                        event.setX(0);
                        event.setZ(0);
                        mario = mc.thePlayer.posY;
                        AAAA = true;
                        MoveUtils.setMotion(MoveUtils.getSpeed());
                    }
                }
                break;
        }
        if (mc.thePlayer.onGround) {
            mario = 0;
            AAAA = false;
        }
    }
}
