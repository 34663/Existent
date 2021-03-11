package zyx.existent.module.modules.misc;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.Vec3d;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventPacket;
import zyx.existent.event.events.EventRender3D;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Setting;
import zyx.existent.utils.ChatUtils;
import zyx.existent.utils.render.Colors;
import zyx.existent.utils.render.RenderingUtils;
import zyx.existent.utils.timer.Timer;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Blink extends Module {
    private List<Packet> packets = new CopyOnWriteArrayList<>();
    private List<Vec3d> crumbs = new CopyOnWriteArrayList<>();
    private String BREADCRUMBS = "CRUMBS";
    private final Timer timer = new Timer();

    public Blink(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(BREADCRUMBS, new Setting(BREADCRUMBS, true, "Draws a line on your blink path."));
    }

    @Override
    public void onEnable() {
        super.onEnable();
        crumbs.clear();
    }
    @Override
    public void onDisable() {
        super.onDisable();
        crumbs.clear();
        for (Packet packet : packets) {
            mc.thePlayer.connection.sendPacket(packet);
        }
        packets.clear();
    }

    @EventTarget
    public void onEvent(EventPacket ep) {
        setSuffix((mc.thePlayer.isMoving() ? "\2477" : "\2478") + packets.size());
        if (ep.isOutgoing() && ep.isPre() && (ep.getPacket() instanceof CPacketPlayer || ep.getPacket() instanceof CPacketKeepAlive)) {
            packets.add(ep.getPacket());
            ep.setCancelled(true);
        }
        if (ep.isIncoming() && ep.isPre()) {
            if (ep.getPacket() instanceof SPacketPlayerPosLook) {
                ep.setCancelled(true);
            }
        }
    }

    @EventTarget
    public void onEvent(EventRender3D render) {
        if (((Boolean) settings.get(BREADCRUMBS).getValue())) {
            if (timer.delay(50)) {
                crumbs.add(new Vec3d(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ));
                timer.reset();
            }

            int[] counter = {1};
            if (!crumbs.isEmpty() && crumbs.size() > 2) {
                for (int i = 1; i < crumbs.size(); i++) {
                    Vec3d vecBegin = crumbs.get(i - 1);
                    Vec3d vecEnd = crumbs.get(i);
                    int color = Colors.rainbow(counter[0] * 60, 0.8F, 1.0F);
                    float beginX = (float) ((float) vecBegin.xCoord - RenderManager.renderPosX);
                    float beginY = (float) ((float) vecBegin.yCoord - RenderManager.renderPosY);
                    float beginZ = (float) ((float) vecBegin.zCoord - RenderManager.renderPosZ);
                    float endX = (float) ((float) vecEnd.xCoord - RenderManager.renderPosX);
                    float endY = (float) ((float) vecEnd.yCoord - RenderManager.renderPosY);
                    float endZ = (float) ((float) vecEnd.zCoord - RenderManager.renderPosZ);
                    final boolean bobbing = mc.gameSettings.viewBobbing;
                    mc.gameSettings.viewBobbing = false;
                    RenderingUtils.drawLine3D(beginX, beginY, beginZ, endX, endY, endZ, color);
                    mc.gameSettings.viewBobbing = bobbing;
                    counter[0] -= 1;
                }
            }
        }
    }
}