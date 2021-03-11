package zyx.existent.module.modules.combat;

import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.util.math.MathHelper;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventPacket;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Options;
import zyx.existent.module.data.Setting;
import zyx.existent.utils.Test;

import java.util.Random;

public class Velocity extends Module {
    private String MODE= "MODE";
    private String WATERCHECK= "WATERCHECK";
    private String HORIZONTAL= "HORIZONTAL";
    private String VERTICAL= "VERTICAL";
    private String CHANCE= "CHANCE";

    public Velocity(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(MODE, new Setting(MODE, new Options("Mode", "Cancel", new String[] {"Cancel", "Vape", "AAC4"}), "Velocity method."));
        settings.put(HORIZONTAL, new Setting(HORIZONTAL, 90, "Horizontal", 0.1, 0.0, 100.0));
        settings.put(VERTICAL, new Setting(VERTICAL, 100, "Vertical", 0.1, 0.0, 100.0));
        settings.put(CHANCE, new Setting(CHANCE, 90, "Chance", 0.1, 0.0, 100.0));
        settings.put(WATERCHECK, new Setting(WATERCHECK, false, "Water Check"));
    }

    @EventTarget
    public void onPacket(EventPacket event) {
        String currentmode = ((Options) settings.get(MODE).getValue()).getSelected();
        final double horizontal = ((Number) settings.get(HORIZONTAL).getValue()).doubleValue();
        final double vertical = ((Number) settings.get(VERTICAL).getValue()).doubleValue();
        final Random random = new Random();

        switch (currentmode) {
            case "Cancel":
                if (event.getPacket() instanceof SPacketEntityVelocity) {
                    final SPacketEntityVelocity s12PacketEntityVelocity = (SPacketEntityVelocity) event.getPacket();
                    if (this.mc.thePlayer != null && this.mc.thePlayer.getEntityId() == s12PacketEntityVelocity.getEntityID()) {
                        event.setCancelled(true);
                    }
                } else if (event.getPacket() instanceof SPacketExplosion) {
                    event.setCancelled(true);
                }
                break;
            case "AAC4":
                if (event.getPacket() instanceof SPacketEntityVelocity) {
                    SPacketEntityVelocity packetIn = (SPacketEntityVelocity) event.getPacket();
                    Entity entity = mc.theWorld.getEntityByID(packetIn.getEntityID());
                    entity.setVelocity(0.0D, packetIn.getMotionY() / 8000.0D, packetIn.getMotionZ() / 8000.0D / 2.0D);
                }
                break;
            case "Vape":
                try {
                    if (event.getPacket() == null || !this.x()) {
                        return;
                    }
                    if (event.getPacket() instanceof SPacketEntityVelocity) {
                        final SPacketEntityVelocity velocity = new SPacketEntityVelocity(mc.thePlayer);
                        final double nextDouble = random.nextDouble();

                        if (MathHelper.getInt(random, 0, 100) > 100.0 - ((Number) settings.get(CHANCE).getValue()).doubleValue()) {
                            final double x = velocity.getMotionX();
                            final double y = velocity.getMotionY();
                            final double z = velocity.getMotionZ();
                            double n6 = horizontal + (horizontal + 5.0 - horizontal) * nextDouble;

                            if (n6 >= 100.0) {
                                n6 = 100.0;
                            }
                            double n7 = vertical + (vertical + 5.0 - vertical) * nextDouble;
                            if (n7 >= 90.0) {
                                n7 = 100.0;
                            }
                            final double n8 = n6 / 100.0;
                            final double n9 = n7 / 100.0;
                            final double formatX = this.format(x, n8);
                            final double formatY = this.format(y, n9);
                            final double formatZ = this.format(z, n8);
                            velocity.setMotionX((int) formatX);
                            velocity.setMotionY((int) formatY);
                            velocity.setMotionZ((int) formatZ);
                            if (horizontal == 0.0 && vertical == 0.0) {
                                event.setCancelled(true);
                            }
                        }
                    }
                    if (event.getPacket() instanceof SPacketExplosion) {
                        final SPacketExplosion explosion = new SPacketExplosion();
                        final double nextDouble2 = random.nextDouble();

                        if (MathHelper.getInt(random, 0, 100) > 100.0 - ((Number) settings.get(CHANCE).getValue()).doubleValue()) {
                            final double n10 = explosion.getX();
                            final double n11 = explosion.getY();
                            final double n12 = explosion.getZ();
                            final double doubleValue3 = ((Number) settings.get(HORIZONTAL).getValue()).doubleValue();
                            double n15 = doubleValue3 + (doubleValue3 + 5.0 - doubleValue3) * nextDouble2;
                            if (n15 >= 100.0) {
                                n15 = 100.0;
                            }
                            final double doubleValue4 = ((Number) settings.get(VERTICAL).getValue()).doubleValue();
                            double n16 = doubleValue4 + (doubleValue4 + 5.0 - doubleValue4) * nextDouble2;
                            if (n16 >= 90.0) {
                                n16 = 100.0;
                            }
                            final double n17 = n15 / 100.0;
                            final double n18 = n16 / 100.0;
                            final int x2 = (int) (n10 * n17);
                            final int y2 = (int) (n11 * n18);
                            final int z2 = (int) (n12 * n17);
                            explosion.setX(x2);
                            explosion.setY(y2);
                            explosion.setZ(z2);
                            if (doubleValue3 == 0.0 && doubleValue4 == 0.0) {
                                event.setCancelled(true);
                            }
                        }
                    }
                } catch (Exception ignored) {
                    ;
                }
                break;
        }
    }

    private boolean x() {
        return (!(Boolean) settings.get(WATERCHECK).getValue() || !mc.thePlayer.inWater);
    }
    private double format(final double n, final double n2) {
        final String string = Double.toString(Math.abs(n));
        return new Test(string.length() - string.indexOf(string.contains(",") ? "," : ".") - 1).a(n * n2);
    }
}
