package zyx.existent.module.modules.combat;

import com.google.common.collect.Sets;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketSpawnPlayer;
import net.minecraft.util.math.MathHelper;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventPacket;
import zyx.existent.event.events.EventUpdate;
import zyx.existent.gui.notification.NotificationPublisher;
import zyx.existent.gui.notification.NotificationType;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Options;
import zyx.existent.module.data.Setting;
import zyx.existent.utils.ChatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AntiBot extends Module {
    private final String MODE = "MODE";
    public static List<EntityPlayer> getInvalid() {
        return invalid;
    }
    public static List<EntityPlayer> invalid = new ArrayList<>();
    Entity currentEntity;
    Entity[] playerList;
    int index;
    boolean next;
    double[] oldPos;

    public AntiBot(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(MODE, new Setting(MODE, new Options("Mode", "Hypixel", new String[] {"Packet", "Matrix", "Hypixel", "Mineplex", "Shotbow"}), "Antibot method."));
    }

    @Override
    public void onEnable() {
        invalid.clear();
        super.onEnable();
    }
    @Override
    public void onDisable() {
        invalid.clear();
        super.onDisable();
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        String currentmode = ((Options) settings.get(MODE).getValue()).getSelected();

        switch (currentmode) {
            case "Hypixel":
                if (event.isPre()) {
                    for (Entity player : mc.theWorld.loadedEntityList) {
                        if (player instanceof EntityPlayer) {
                            if ((player.getName().startsWith("§") && player.getName().contains("§c")) || (isEntityBot(player) && !player.getDisplayName().getFormattedText().contains("NPC"))) {
                                mc.theWorld.removeEntity(player);
                            }
                        }
                    }
                }
                break;
            case "Mineplex":
                if (mc.getCurrentServerData() != null) {
                    if (mc.getCurrentServerData().serverIP.toLowerCase().contains("mineplex")) {
                        for (Entity e : mc.theWorld.getLoadedEntityList()) {
                            if (e instanceof EntityPlayer) {
                                EntityPlayer bot = (EntityPlayer) e;
                                if (e.ticksExisted < 2 && bot.getHealth() < 20.0F && bot.getHealth() > 0.0F && e != mc.thePlayer)
                                    mc.theWorld.removeEntity(e);
                            }
                        }
                    }
                }
                break;
            case "Shotbow":
                if (event.isPre()) {
                    for (Entity entity : mc.theWorld.getLoadedEntityList()) {
                        if (entity instanceof EntityPlayer) {
                            EntityPlayer bot = (EntityPlayer) entity;
                            if (bot.ticksExisted < 100 && isNoArmor(bot)) {
                                mc.theWorld.removeEntity(bot);
                            }
                        }
                    }
                }
                break;
            case "Matrix":
                int j = 0;

                for (int i = 0; i < mc.theWorld.getLoadedEntityList().size(); i++) {
                    if (mc.theWorld.getLoadedEntityList().get(i) instanceof EntityPlayer) {
                        playerList[j++] = mc.theWorld.getLoadedEntityList().get(i);
                    }
                }
                if (index > playerList.length - 1) {
                    index = 0;
                    return;
                }
                if (!next) {
                    currentEntity = playerList[index];
                    oldPos[0] = currentEntity.posX;
                    oldPos[1] = currentEntity.posZ;
                    next = true;
                    return;
                }

                double xDiff = oldPos[0] - currentEntity.posX;
                double zDiff = oldPos[1] - currentEntity.posZ;
                double speed = Math.sqrt(xDiff * xDiff + zDiff * zDiff) * 10; // Legit 6.753686890703971

                if (currentEntity != mc.thePlayer && speed > 6.9 && currentEntity.hurtResistantTime == 0 && currentEntity.posY > mc.thePlayer.posY - 1.5 && currentEntity.posY < mc.thePlayer.posY + 1.5 && mc.thePlayer.getDistanceToEntity(currentEntity) < 4.5) {
                    mc.theWorld.removeEntity(currentEntity);
                    NotificationPublisher.queue("AntiBot", "Remove " + currentEntity.getName(), NotificationType.INFO);
                    ChatUtils.printChatprefix("[AntiBot] Remove " + currentEntity.getName());
                }

                index++;
                next = false;
                break;
        }
    }
    @EventTarget
    public void onPacket(EventPacket ep) {
        if (((Options)settings.get(MODE).getValue()).getSelected().equalsIgnoreCase("Packet")) {
            if (ep.isIncoming() && ep.getPacket() instanceof SPacketSpawnPlayer) {
                SPacketSpawnPlayer packet = (SPacketSpawnPlayer) ep.getPacket();
                double entX = packet.getX() / 32;
                double entY = packet.getY() / 32;
                double entZ = packet.getZ() / 32;
                double posX = mc.thePlayer.posX;
                double posY = mc.thePlayer.posY;
                double posZ = mc.thePlayer.posZ;
                double var7 = posX - entX;
                double var9 = posY - entY;
                double var11 = posZ - entZ;
                float distance = MathHelper.sqrt(var7 * var7 + var9 * var9 + var11 * var11);
                if (distance <= 17 && entY > mc.thePlayer.posY + 1 && (mc.thePlayer.posX != entX && mc.thePlayer.posY != entY && mc.thePlayer.posZ != entZ)) {
                    ep.setCancelled(true);
                }
            }
        }
    }

    private boolean isEntityBot(Entity entity) {
        double distance = entity.getDistanceSqToEntity(mc.thePlayer);
        if (!(entity instanceof EntityPlayer))
            return false;
        if (mc.getCurrentServerData() == null)
            return false;
        return (((mc.getCurrentServerData()).serverIP.toLowerCase().contains("hypixel") && entity.getDisplayName().getFormattedText().startsWith("\u0e22\u0e07")) || (!isOnTab(entity) && mc.thePlayer.ticksExisted > 100));
    }
    private boolean isOnTab(Entity entity) {
        for (NetworkPlayerInfo info : mc.getConnection().getPlayerInfoMap()) {
            if (info.getGameProfile().getName().equals(entity.getName()))
                return true;
        }
        return false;
    }
    private static boolean isNoArmor(final EntityPlayer entity) {
        for (int i = 0; i < entity.inventory.armorInventory.size(); ++i) {
            if (entity.getEquipmentInSlot(i) != null) {
                return false;
            }
        }
        return true;
    }
}
