package zyx.existent.module.modules.misc;

import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventUpdate;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Options;
import zyx.existent.module.data.Setting;
import zyx.existent.module.modules.combat.KillAura;
import zyx.existent.module.modules.movement.Scaffold;
import zyx.existent.module.modules.player.ChestStealer;
import zyx.existent.utils.misc.MiscUtils;
import zyx.existent.utils.timer.Timer;

public class Annoy extends Module {
    private String MODE = "MODE";
    private String YAW = "YAW";
    private String PITCH = "PITCH";
    private String SPEED = "RotationSpeed";
    private final Timer timer = new Timer();
    public float rot = 0;

    public Annoy(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(MODE, new Setting(MODE, new Options("Mode", "Custom", new String[]{"Custom", "Spin", "Witch", "Random"}), "Annoy method"));
        settings.put(SPEED, new Setting(SPEED, 3.0, "Spin Speed.", 0.1, 1.0, 10.0));
        settings.put(YAW, new Setting(YAW, 0.0, "Yaw offset.", 0.1, -180.0, 180.0));
        settings.put(PITCH, new Setting(PITCH, 0.0, "Pitch offset.", 0.1, -90.0, 90.0));
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        String currentmode = ((Options) settings.get(MODE).getValue()).getSelected();
        float yaw = ((Number) settings.get(YAW).getValue()).floatValue();
        float pitch = ((Number) settings.get(PITCH).getValue()).floatValue();
        float speed = ((Number) settings.get(SPEED).getValue()).floatValue();
        boolean exep = KillAura.target != null || Scaffold.data != null || ChestStealer.tileEntityChest != null;

        if (!exep) {
            switch (currentmode) {
                case "Custom":
                    event.setYaw(yaw);
                    event.setPitch(pitch);
                    mc.thePlayer.renderYawOffset = yaw;
                    mc.thePlayer.rotationYawHead = yaw;
                    mc.thePlayer.rotationPitchHead = pitch;
                    break;
                case "Spin":
                    event.setYaw((float) Math.floor(spin2(speed)));
                    event.setPitch(pitch);
                    mc.thePlayer.renderYawOffset = (float) Math.floor(spin2(speed));
                    mc.thePlayer.rotationYawHead = (float) Math.floor(spin2(speed));
                    mc.thePlayer.rotationPitchHead = pitch;
                    break;
                case "Witch":
                    event.setYaw(this.mc.thePlayer.rotationYaw + 180.0f);
                    event.setPitch(pitch);
                    mc.thePlayer.rotationYawHead = this.mc.thePlayer.rotationYaw + 180.0f;
                    mc.thePlayer.renderYawOffset = this.mc.thePlayer.rotationYaw + 180.0f;
                    mc.thePlayer.rotationPitchHead = pitch;
                    break;
                case "Random":
                    float y = MiscUtils.randomNumber(180, -180);
                    float p = MiscUtils.randomNumber(180, -180);
                    event.setYaw(y);
                    event.setPitch(p);
                    mc.thePlayer.rotationYawHead = y;
                    mc.thePlayer.renderYawOffset = y;
                    mc.thePlayer.rotationPitchHead = p;
                    break;
            }
        }
    }

    public float spin2(double r) {
        rot += r;
        return rot;
    }
}
