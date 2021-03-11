package zyx.existent.module.modules.movement;

import net.minecraft.util.MovementInput;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventMove;
import zyx.existent.event.events.EventUpdate;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Options;
import zyx.existent.module.data.Setting;
import zyx.existent.utils.MoveUtils;

public class Glide extends Module {
    private final String MODE = "MODE";
    private int counter, stage, ticks;
    private double y;

    public Glide(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(MODE,new Setting(MODE,new Options("Mode", "Hypixel", new String[] {"Hypixel", "Shotbow"}),"Glide method."));
    }

    @Override
    public void onEnable() {
        this.y = 0.0D;
        this.stage = 0;
        this.ticks = 0;

        switch (((Options) settings.get(MODE).getValue()).getSelected()) {
            case "Hypixel":
            case "Shotbow":
                if (mc.thePlayer.onGround)
                    mc.thePlayer.motionY = 0.42;
                MoveUtils.setMotion(0.3 + MoveUtils.getSpeedEffect() * 0.05f);
        }
        super.onEnable();
    }
    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0F;
        mc.thePlayer.stepHeight = 0.625F;
        super.onDisable();
    }

    @EventTarget
    public void onUpdate(EventUpdate eventUpdate) {
        String currentmode = ((Options) settings.get(MODE).getValue()).getSelected();

        switch (currentmode) {
            case "Hypixel":
                if (eventUpdate.isPre()) {
                    if (this.stage > 2)
                        mc.thePlayer.motionY = 0.0D;
                    if (this.stage > 2) {
                        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.003D, mc.thePlayer.posZ);
                        this.ticks++;
                        double offset = 3.25E-4D;
                        switch (this.ticks) {
                            case 1:
                                this.y *= -0.949999988079071D;
                                break;
                            case 2:
                            case 3:
                            case 4:
                                this.y += 3.25E-4D;
                                break;
                            case 5:
                                this.y += 5.0E-4D;
                                this.ticks = 0;
                                break;
                        }
                        eventUpdate.setY(mc.thePlayer.posY + this.y);
                    }
                    break;
                } else if (this.stage > 2)
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.003D, mc.thePlayer.posZ);
                break;
            case "Shotbow":
                mc.thePlayer.cameraYaw = 0.1F;
                ++counter;
                if (counter == 2) {
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 1.0E-10D, mc.thePlayer.posZ);
                    counter = 0;
                }
                mc.thePlayer.motionY = 0.0D;
                mc.thePlayer.onGround = false;
                break;
        }
    }
    @EventTarget
    public void onMove(EventMove eventMove) {
        if (mc.thePlayer.isMoving()) {
            MoveUtils.setMotion(MoveUtils.getBaseMoveSpeed());
        }
        this.stage++;
    }
}
