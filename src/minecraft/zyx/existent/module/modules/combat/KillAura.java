package zyx.existent.module.modules.combat;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.*;
import net.minecraft.network.play.client.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import zyx.existent.Existent;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventAttack;
import zyx.existent.event.events.EventRender3D;
import zyx.existent.event.events.EventUpdate;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Options;
import zyx.existent.module.data.Setting;
import zyx.existent.module.modules.misc.Civbreak2;
import zyx.existent.module.modules.movement.LongJump;
import zyx.existent.module.modules.movement.Scaffold;
import zyx.existent.utils.ChatUtils;
import zyx.existent.utils.misc.MiscUtils;
import zyx.existent.utils.RayTraceUtil;
import zyx.existent.utils.RotationUtils;
import zyx.existent.utils.render.Colors;
import zyx.existent.utils.render.RenderingUtils;
import zyx.existent.utils.timer.Timer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class KillAura extends Module {
    private final String ROTMODE = "ROTMODE";
    private final String MODE = "MODE";
    private final String SORT = "SORT";
    private final String PLAYERS = "PLAYER";
    private final String ANIMALS = "ANIMAL";
    private final String MONSTERS = "MONSTER";
    private final String INVISIBLE = "INVISIBLE";
    private final String FRIENDS = "FRIENDS";
    private final String TEAMS = "TEAMS";
    private final String WALLS = "WALLS";
    private final String AUTOBLOCK = "AUTOBLOCK";
    private final String COOLDOWN = "COOLDOWN";
    private final String MAXAPS = "MAXAPS";
    private final String MINAPS = "MINAPS";
    private final String RANGE = "RANGE";
    private final String SWAPTIME = "SWAPTIME";
    private final String SWITCHSWORD = "SWITCHSWORD";
    private final String HEADROTATION = "HEADROTATION";
    public static EntityLivingBase target;
    public boolean attacking;
    public boolean blocking;
    private int targetIndex;
    public Float lastYaw;
    private final List<EntityLivingBase> targets;
    private final Timer attackTimer, switchTimer;
    public int ticks;

    public KillAura(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(ROTMODE, new Setting(ROTMODE, new Options("Rotation Mode", "Predict", new String[]{"Predict", "FacePos", "BestPos", "Vector"}), "Aura method"));
        settings.put(MODE, new Setting(MODE, new Options("Aura Mode", "Priority", new String[]{"Priority", "Switch", "Tick", "Multi"}), "Aura method"));
        settings.put(SORT, new Setting(SORT, new Options("Sort Mode", "Angle", new String[]{"Range", "Angle", "Health", "Armor", "Cycle"}), "Sort method"));
        settings.put(RANGE, new Setting(RANGE, 4.2, "Aura Range.", 0.1, 1.0, 7.0));
        settings.put(MAXAPS, new Setting(MAXAPS, 11, "MaxAps.", 1, 1, 70));
        settings.put(MINAPS, new Setting(MINAPS, 7, "MinAps.", 1, 1, 70));
        settings.put(SWAPTIME, new Setting(SWAPTIME, 600L, "SwapTime.", 1, 0L, 1000L));
        settings.put(PLAYERS, new Setting(PLAYERS, true, "Target players."));
        settings.put(ANIMALS, new Setting(ANIMALS, false, "Target animals."));
        settings.put(MONSTERS, new Setting(MONSTERS, false, "Target monsters."));
        settings.put(INVISIBLE, new Setting(INVISIBLE, false, "Target Invisible."));
        settings.put(FRIENDS, new Setting(FRIENDS, false, "Target Friends."));
        settings.put(TEAMS, new Setting(TEAMS, false, "Target teams."));
        settings.put(WALLS, new Setting(WALLS, true, "Attack through the wall."));
        settings.put(AUTOBLOCK, new Setting(AUTOBLOCK, true, "AutoBlock."));
        settings.put(COOLDOWN, new Setting(COOLDOWN, true, "Cooldown."));
        settings.put(SWITCHSWORD, new Setting(SWITCHSWORD, true, "AutoSwitch Sword."));
        settings.put(HEADROTATION, new Setting(HEADROTATION, true, "HeadRotation."));
        targets = new ArrayList<>();
        attackTimer = new Timer();
        switchTimer = new Timer();
    }

    @Override
    public void onEnable() {
        this.attackTimer.reset();
        this.switchTimer.reset();
        this.ticks = 0;
        if (mc.theWorld != null && mc.thePlayer != null) {
            unblock();
        }
        super.onEnable();
    }
    @Override
    public void onDisable() {
        target = null;
        unblock();
        if (mc.thePlayer.isActiveItemStackBlocking())
            mc.gameSettings.keyBindUseItem.pressed = false;
        super.onDisable();
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        String currentmode = ((Options) settings.get(MODE).getValue()).getSelected();
        EventAttack ej = new EventAttack(target, true);
        EventAttack ej2 = new EventAttack(target, false);
        int minAPS = ((Number) settings.get(MINAPS).getValue()).intValue() * 10;
        int maxAPS = ((Number) settings.get(MAXAPS).getValue()).intValue() * 10;
        double range = ((Number) settings.get(RANGE).getValue()).doubleValue();
        long SwapTime = ((Number) settings.get(SWAPTIME).getValue()).longValue();
        final int aps = randomNumber(minAPS, maxAPS);
        boolean rotation = (Boolean) settings.get(HEADROTATION).getValue();
        boolean flag = (Existent.getModuleManager().isEnabled(Scaffold.class) || Existent.getModuleManager().isEnabled(LongJump.class));
        boolean flag2 = (Boolean) settings.get(SWITCHSWORD).getValue();
        byte b = -1;
        targets.clear();
        setSuffix(currentmode + " " + range);

        collectTargets();
        switch (currentmode) {
            case "Switch":
                if (switchTimer.delay(SwapTime)) {
                    targetIndex++;
                    switchTimer.reset();
                }
                if (targetIndex >= targets.size()) {
                    targetIndex = 0;
                }
                target = !targets.isEmpty() && targetIndex < targets.size() ? targets.get(targetIndex) : null;
                break;
            case "Priority":
                sortTargets(event.getYaw());
                target = !targets.isEmpty() ? targets.get(0) : null;
                break;
            case "Tick":
                targets.sort(Comparator.comparingInt(e -> e.hurtTime));
                if (this.targetIndex >= this.targets.size())
                    this.targetIndex = 0;
                target = !targets.isEmpty() ? targets.get(this.targetIndex) : null;
                break;
            case "Multi":
                targets.sort(Comparator.comparingInt(e -> e.hurtTime));
                this.targetIndex = 0;
                target = !targets.isEmpty() ? targets.get(this.targetIndex) : null;
                break;
        }

        if (Existent.getModuleManager().isEnabled(Civbreak2.class) && Civbreak2.pos != null)
            return;
        if (event.isPre() && target != null) {
            if (!flag) {
                float[] rotations = null;
                switch (((Options) settings.get(ROTMODE).getValue()).getSelected()) {
                    case "BestPos":
                        rotations = RotationUtils.getRotations(target, range);
                        break;
                    case "Predict":
                        float[] rotationsCurrent = new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
                        float[] rotationsInstant = RotationUtils.getRotations(target, true, 1);
                        rotations = RotationUtils.smoothRotation(rotationsCurrent, rotationsInstant, randomNumber(180, 90) + ThreadLocalRandom.current().nextInt(5));
                        break;
                    case "Vector":
                        Vec3d playerVec = new Vec3d(event.getX(), event.getY() + mc.thePlayer.getEyeHeight(), event.getZ());
                        Vec3d targetVec = target.getPositionVector();
                        if (targetVec.yCoord - 0.7D > event.getY()) {
                            targetVec = targetVec.addVector(0.0D, (target.getEyeHeight() / 2.0F), 0.0D);
                        } else if (targetVec.yCoord > event.getY()) {
                            targetVec = targetVec.addVector(0.0D, (target.getEyeHeight() / 1.5F), 0.0D);
                        } else {
                            targetVec = targetVec.addVector(0.0D, target.getEyeHeight(), 0.0D);
                        }
                        targetVec = targetVec.addVector(target.posX - target.lastTickPosX, 0.0D, target.posZ - target.lastTickPosZ);
                        rotations = RotationUtils.getNeededFacing(targetVec, playerVec);
                        break;
                    case "FacePos":
                        rotations = RotationUtils.getFacePosEntity(target);
                        break;
                }
                if (rotations != null) {
                    if (currentmode.equalsIgnoreCase("Multi")) {
                        if (this.lastYaw != null)
                            event.setYaw(this.lastYaw);
                        float yawDist = RotationUtils.normalizeAngle(rotations[0] - event.getYaw());
                        if (yawDist > 15.0F) {
                            event.setYaw(RotationUtils.normalizeAngle(event.getYaw() + yawDist / 2.0F));
                            if (rotation) {
                                mc.thePlayer.renderYawOffset = RotationUtils.normalizeAngle(event.getYaw() + yawDist / 2.0F);
                                mc.thePlayer.rotationYawHead = RotationUtils.normalizeAngle(event.getYaw() + yawDist / 2.0F);
                            }
                        } else {
                            event.setYaw(rotations[0]);
                            if (rotation) {
                                mc.thePlayer.renderYawOffset = rotations[0];
                                mc.thePlayer.rotationYawHead = rotations[0];
                            }
                        }
                        event.setPitch(rotations[1]);
                        if (rotation)
                            mc.thePlayer.rotationPitchHead = rotations[1];
                        this.lastYaw = event.getYaw();
                    } else {
                        event.setYaw(rotations[0]);
                        event.setPitch(rotations[1]);
                        if (rotation) {
                            mc.thePlayer.renderYawOffset = rotations[0];
                            mc.thePlayer.rotationYawHead = rotations[0];
                            mc.thePlayer.rotationPitchHead = rotations[1];
                        }
                    }
                }

                if (flag2) {
                    if (this.mc.objectMouseOver == null)
                        return;
                    for (byte b1 = 0; b1 < 9; b1++) {
                        ItemStack itemStack = this.mc.thePlayer.inventory.mainInventory.get(b1);
                        if (itemStack != null && itemStack.getItem() instanceof ItemSword) {
                            b = b1;
                        }
                    }
                    if (b != -1) {
                        this.mc.thePlayer.inventory.currentItem = b;
                    }
                }
            }
        } else if (target != null) {
            if (!flag && validEntity(target)) {
                block();
                switch (currentmode) {
                    case "Switch":
                    case "Priority":
                    case "Multi":
                        if (aps != 0 && !((Boolean) settings.get(COOLDOWN).getValue())) {
                            if (attackTimer.delay(aps)) {
                                ej.call();
                                unblock();
                                attack(target);
                                attacking = true;
                                ej2.call();
                                attackTimer.reset();
                            }
                        } else if (mc.thePlayer.getCooledAttackStrength(0) >= 1) {
                            ej.call();
                            unblock();
                            attack(target);
                            attacking = true;
                            ej2.call();
                            attackTimer.reset();
                        }
                        break;
                    case "Tick":
                        boolean hurtTime = target.hurtTime >= 6;
                        this.ticks++;
                        if (this.ticks >= 4 && !hurtTime) {
                            this.ticks = 0;
                            ej.call();
                            unblock();
                            attack(target);
                            attacking = true;
                            ej2.call();
                            attackTimer.reset();
                        }
                        break;
                }
            }
        }
    }
    @EventTarget
    public void onRender3D(EventRender3D render) {
        if (target != null) {
            int color = target.hurtResistantTime > 15 ? Colors.getColor(new Color(255, 70, 70, 80)) : Colors.getColor(new Color(255, 255, 255, 80));
            if (((Options) settings.get(MODE).getValue()).getSelected().equalsIgnoreCase("Multi") || ((Options) settings.get(MODE).getValue()).getSelected().equalsIgnoreCase("MultiTick")) {
                for (EntityLivingBase ent : targets) {
                    double x = ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * this.mc.timer.renderPartialTicks - RenderManager.renderPosX;
                    double y = ent.lastTickPosY + (ent.posY - ent.lastTickPosY) * this.mc.timer.renderPartialTicks - RenderManager.renderPosY;
                    double z = ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * this.mc.timer.renderPartialTicks - RenderManager.renderPosZ;
                    x -= 0.5D;
                    z -= 0.5D;
                    y += ent.getEyeHeight() + 0.35D - (ent.isSneaking() ? 0.25D : 0.0D);
                    double mid = 0.5D;
                    GL11.glPushMatrix();
                    GL11.glEnable(3042);
                    GL11.glBlendFunc(770, 771);
                    GL11.glTranslated(x + mid, y + mid, z + mid);
                    GL11.glRotated((-ent.rotationYaw % 360.0F), 0.0D, 1.0D, 0.0D);
                    GL11.glTranslated(-(x + mid), -(y + mid), -(z + mid));
                    GL11.glDisable(3553);
                    GL11.glEnable(2848);
                    GL11.glDisable(2929);
                    GL11.glDepthMask(false);
                    RenderingUtils.glColor(color);
                    RenderingUtils.drawBoundingBox(new AxisAlignedBB(x, y, z, x + 1.0D, y + 0.05D, z + 1.0D));
                    GL11.glDisable(2848);
                    GL11.glEnable(3553);
                    GL11.glEnable(2929);
                    GL11.glDepthMask(true);
                    GL11.glDisable(3042);
                    GL11.glPopMatrix();
                }
            } else {
                double x = target.lastTickPosX + (target.posX - target.lastTickPosX) * this.mc.timer.renderPartialTicks - RenderManager.renderPosX;
                double y = target.lastTickPosY + (target.posY - target.lastTickPosY) * this.mc.timer.renderPartialTicks - RenderManager.renderPosY;
                double z = target.lastTickPosZ + (target.posZ - target.lastTickPosZ) * this.mc.timer.renderPartialTicks - RenderManager.renderPosZ;
                x -= 0.5D;
                z -= 0.5D;
                y += target.getEyeHeight() + 0.35D - (target.isSneaking() ? 0.25D : 0.0D);
                double mid = 0.5D;
                GL11.glPushMatrix();
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
                GL11.glTranslated(x + mid, y + mid, z + mid);
                GL11.glRotated((-target.rotationYaw % 360.0F), 0.0D, 1.0D, 0.0D);
                GL11.glTranslated(-(x + mid), -(y + mid), -(z + mid));
                GL11.glDisable(3553);
                GL11.glEnable(2848);
                GL11.glDisable(2929);
                GL11.glDepthMask(false);
                RenderingUtils.glColor(color);
                RenderingUtils.drawBoundingBox(new AxisAlignedBB(x, y, z, x + 1.0D, y + 0.05D, z + 1.0D));
                GL11.glDisable(2848);
                GL11.glEnable(3553);
                GL11.glEnable(2929);
                GL11.glDepthMask(true);
                GL11.glDisable(3042);
                GL11.glPopMatrix();
            }
        }
    }

    public void drawESP(Entity entity, int color) {
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks + entity.getEyeHeight() * 1.2;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks;
        double width = Math.abs(entity.boundingBox.maxX - entity.boundingBox.minX) + 0.2;
        double height = entity.height + 0.1;

        Vec3d vec = new Vec3d(x - width / 2, y, z - width / 2);
        Vec3d vec2 = new Vec3d(x + width / 2, y - 0.1, z + width / 2);
        RenderingUtils.pre3D();
        mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2);
        RenderingUtils.glColor(color);
        RenderingUtils.drawBoundingBox(new AxisAlignedBB(vec.xCoord - RenderManager.renderPosX, vec.yCoord - RenderManager.renderPosY, vec.zCoord - RenderManager.renderPosZ, vec2.xCoord - RenderManager.renderPosX, vec2.yCoord - RenderManager.renderPosY, vec2.zCoord - RenderManager.renderPosZ));
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
        RenderingUtils.post3D();
    }
    private void attack(EntityLivingBase entityLivingBase) {
        final float sharpLevel = EnchantmentHelper.getModifierForCreature(mc.thePlayer.getHeldItem(EnumHand.MAIN_HAND), EnumCreatureAttribute.UNDEFINED);

        if (sharpLevel > 0.0F)
            mc.thePlayer.onEnchantmentCritical(entityLivingBase);
        mc.thePlayer.swingArm(EnumHand.MAIN_HAND);
        mc.playerController.syncCurrentPlayItem();
        mc.thePlayer.connection.sendPacket(new CPacketUseEntity(entityLivingBase));
    }
    private void Silentattack(EntityLivingBase entityLivingBase) {
        final float sharpLevel = EnchantmentHelper.getModifierForCreature(mc.thePlayer.getHeldItem(EnumHand.MAIN_HAND), EnumCreatureAttribute.UNDEFINED);

        if (sharpLevel > 0.0F)
            mc.thePlayer.onEnchantmentCritical(entityLivingBase);
        mc.thePlayer.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
        mc.playerController.syncCurrentPlayItem();
        mc.thePlayer.connection.sendPacket(new CPacketUseEntity(entityLivingBase));
    }
    private void block() {
        if ((Boolean) settings.get(AUTOBLOCK).getValue() && !mc.thePlayer.isBlocking() && mc.thePlayer.getHeldItem(EnumHand.OFF_HAND).getItem() instanceof ItemShield) {
            mc.playerController.processRightClick(mc.thePlayer, mc.theWorld, EnumHand.OFF_HAND);
        }
    }
    private void unblock() {
        if ((Boolean) settings.get(AUTOBLOCK).getValue() && mc.thePlayer.isBlocking() && mc.thePlayer.getHeldItem(EnumHand.OFF_HAND).getItem() instanceof ItemShield) {
            mc.getConnection().getNetworkManager().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-.8, -.8, -.8), EnumFacing.DOWN));
        }
    }
    public void changeTarget() {
        if (this.targets.size() == 1)
            return;
        ++this.targetIndex;
    }
    private void swap(int slot, int hotbarNum) {
        this.mc.playerController.windowClick(this.mc.thePlayer.inventoryContainer.windowId, slot, hotbarNum, ClickType.SWAP, this.mc.thePlayer);
    }
    private void sortTargets(float yaw) {
        String Sort = ((Options) settings.get(SORT).getValue()).getSelected();

        switch (Sort) {
            case "Range":
                targets.sort((o1, o2) -> (int) (o1.getDistanceToEntity(mc.thePlayer) - o2.getDistanceToEntity(mc.thePlayer)));
                break;
            case "Health":
                targets.sort(Comparator.comparingDouble(EntityLivingBase::getHealth));
                break;
            case "Angle":
                targets.sort(Comparator.comparingDouble(RotationUtils::getYawChangeToEntity));
                break;
            case "Armor":
                targets.sort(Comparator.comparingDouble(EntityLivingBase::getTotalArmorValue));
                break;
            case "Cycle":
                this.targets.sort(Comparator.comparingDouble(player -> yawDistCycle(player, yaw)));
                break;
        }
    }
    private double yawDistCycle(EntityLivingBase e, float yaw) {
        Vec3d difference = e.getPositionVector().addVector(0.0D, (e.getEyeHeight() / 2.0F), 0.0D).subtract(this.mc.thePlayer.getPositionVector().addVector(0.0D, this.mc.thePlayer.getEyeHeight(), 0.0D));
        double d = Math.abs(yaw - Math.atan2(difference.zCoord, difference.xCoord)) % 90.0D;
        return d;
    }
    private void collectTargets() {
        targets.clear();

        for (Entity entity : mc.thePlayer.getEntityWorld().loadedEntityList) {
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase entityLivingBase = (EntityLivingBase) entity;

                if (validEntity(entityLivingBase)) {
                    targets.add(entityLivingBase);
                }
            }
        }
    }
    public EntityLivingBase getBestEntity() {
        if (this.targets != null)
            this.targets.clear();
        for (Object object : mc.theWorld.loadedEntityList) {
            if (object instanceof EntityLivingBase) {
                EntityLivingBase e = (EntityLivingBase) object;
                if (validEntity(e))
                    this.targets.add(e);
            }
        }
        if (this.targets.isEmpty())
            return null;
        this.targets.sort((o1, o2) -> {
            double dist1 = getDistanceSq((Entity) o1);
            double dist2 = getDistanceSq((Entity) o2);
            return Double.compare(dist1, dist2);
        });
        if (this.targetIndex >= this.targets.size())
            this.targetIndex = 0;
        return this.targets.get(this.targetIndex);
    }
    private double getDistanceSq(Entity target) {
        return mc.thePlayer.getDistanceSq(target.posX, target.posY, target.posZ);
    }
    boolean validEntity(EntityLivingBase entity) {
        if ((mc.thePlayer.isEntityAlive()) && !(entity instanceof EntityPlayerSP)) {
            if (mc.thePlayer.getDistanceToEntity(entity) <= ((Number) settings.get(RANGE).getValue()).doubleValue()) {
                if (!RotationUtils.canEntityBeSeen(entity) && !((Boolean) settings.get(WALLS).getValue()))
                    return false;
                if (AntiBot.getInvalid().contains(entity) || entity.isPlayerSleeping())
                    return false;
                if (entity instanceof EntityPlayer) {
                    if (((Boolean) settings.get(PLAYERS).getValue())) {
                        EntityPlayer player = (EntityPlayer) entity;
                        if (!player.isEntityAlive() && player.getHealth() == 0.0) {
                            return false;
                        } else if (!MiscUtils.isTeams((EntityPlayer) entity) && ((Boolean) settings.get(TEAMS).getValue())) {
                            return false;
                        } else if (player.isInvisible() && !((Boolean) settings.get(INVISIBLE).getValue())) {
                            return false;
                        } else if (Existent.getFriendManager().isFriend(player.getName()) && !((Boolean) settings.get(FRIENDS).getValue())) {
                            return false;
                        } else {
                            return true;
                        }
                    }
                } else {
                    if (!entity.isEntityAlive())
                        return false;
                }
                if (((Boolean) settings.get(ANIMALS).getValue())) {
                    if (entity instanceof EntityIronGolem || entity instanceof EntityAnimal || entity instanceof EntityVillager) {
                        if (entity.getName().equals("Villager") && entity instanceof EntityVillager) {
                            return false;
                        }
                        return true;
                    }
                }
                if (((Boolean) settings.get(MONSTERS).getValue())) {
                    if (entity instanceof EntityMob) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private RayTraceResult rayCast(final EntityLivingBase entityLivingBase, double x, double y, double z) {
        final float[] angles = RotationUtils.getAngles(entityLivingBase);
        if (angles == null) {
            return null;
        }
        return RayTraceUtil.rayTrace(this.mc.timer.renderPartialTicks, x, y, z, angles[1], angles[0]);
    }
    public int getNoItemSlot() {
        final int first = mc.thePlayer.inventory.currentItem;
        for (int i = 0; i < 9; ++i) {
            mc.thePlayer.inventory.currentItem = i;
            if (mc.thePlayer.getHeldItem(EnumHand.MAIN_HAND).getItem().getUnlocalizedName().equalsIgnoreCase("tile.air")) {
                mc.thePlayer.inventory.currentItem = first;
                return i;
            }
        }
        return mc.thePlayer.inventory.currentItem = first;
    }
    public static int randomNumber(int max, int min) {
        return Math.round(min + (float) Math.random() * ((max - min)));
    }
}
