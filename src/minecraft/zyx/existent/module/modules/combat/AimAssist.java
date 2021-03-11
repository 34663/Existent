package zyx.existent.module.modules.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Mouse;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventUpdate;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Setting;
import zyx.existent.utils.RotationUtils;
import zyx.existent.utils.misc.MiscUtils;
import zyx.existent.utils.timer.Timer;

import java.util.ArrayList;

public class AimAssist extends Module {
    private final String RANGE = "RANGE";
    private final String SPEED = "SPEED";
    private final String FOV = "FOV";

    public ArrayList<EntityLivingBase> targets = new ArrayList();
    public EntityLivingBase target;
    private final Timer switchTimer = new Timer();
    private final Timer clickTimer = new Timer();
    public int index;
    public boolean clicked;

    public AimAssist(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(RANGE, new Setting(RANGE, 3.0, "MaxAps.", 0.1, 3.0, 6.0));
        settings.put(SPEED, new Setting(SPEED, 1.0, "MaxAps.", 0.1, 1.0, 6.0));
        settings.put(FOV, new Setting(FOV, 60, "MaxAps.", 1, 30, 180));
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (event.isPre()) {
            this.targets.clear();
            this.target = null;
            for (Entity ent : mc.theWorld.loadedEntityList) {
                if (ent instanceof EntityLivingBase) {
                    EntityLivingBase entity = (EntityLivingBase) ent;
                    if (isValidEntity(entity))
                        this.targets.add(entity);
                }
            }
            if (this.targets.isEmpty()) {
                this.switchTimer.reset();
                return;
            }
            this.targets.sort((e1, e2) -> (int) (mc.thePlayer.getDistanceToEntity(e1) - mc.thePlayer.getDistanceToEntity(e2)));
            this.targets.sort((e1, e2) -> (int) (RotationUtils.getAngleDistance(e1) - RotationUtils.getAngleDistance(e2)));
            if (this.targets.size() > 1) {
                if (this.switchTimer.delay(1100.0F)) {
                    this.switchTimer.reset();
                    this.index++;
                    if (this.index >= this.targets.size())
                        this.index = 0;
                }
            } else {
                this.index = 0;
            }
            if (this.clicked && this.clickTimer.delay(275.0F)) {
                this.clickTimer.reset();
                this.clicked = false;
            }
            if (Mouse.isButtonDown(0)) {
                this.clicked = true;
                this.clickTimer.reset();
            }
            if (!this.clicked)
                return;
            this.target = this.targets.get(this.index);
            Vec3d playerVec = new Vec3d(event.getX(), event.getY() + mc.thePlayer.getEyeHeight(), event.getZ());
            Vec3d targetVec = this.target.getPositionEyes(1.0F);
            targetVec.addVector(this.target.posX - this.target.lastTickPosX, this.target.posY - this.target.lastTickPosY, this.target.posZ - this.target.lastTickPosZ);
            float[] rotations = RotationUtils.getNeededFacing(targetVec, playerVec);
            double aimSpeed = 6.0D - ((Number) settings.get(SPEED).getValue()).doubleValue();
            mc.thePlayer.rotationYaw = (float) (mc.thePlayer.rotationYaw + RotationUtils.normalizeAngle(rotations[0] - mc.thePlayer.rotationYaw) / Math.max(1.0D, aimSpeed));
            mc.thePlayer.rotationPitch = (float) (mc.thePlayer.rotationPitch + RotationUtils.normalizeAngle(rotations[1] - mc.thePlayer.rotationPitch) / Math.max(1.0D, aimSpeed));
        }
    }

    public boolean isValidEntity(EntityLivingBase entity) {
        if (entity == null)
            return false;
        if (entity.getEntityId() == mc.thePlayer.getEntityId())
            return false;
        if (entity.deathTime > 0)
            return false;
        if (mc.thePlayer.getDistanceToEntity(entity) > ((Number) settings.get(RANGE).getValue()).doubleValue())
            return false;
        if (Math.abs(RotationUtils.normalizeAngle(RotationUtils.getYaw(entity))) > ((Number) settings.get(FOV).getValue()).intValue())
            return false;
        if (entity instanceof IMob || entity instanceof IAnimals)
            return false;
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            if (!MiscUtils.isTeams(player))
                return false;
            return !AntiBot.getInvalid().contains(player);
        }
        return false;
    }
}
