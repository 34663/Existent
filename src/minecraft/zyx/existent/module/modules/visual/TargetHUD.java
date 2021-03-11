package zyx.existent.module.modules.visual;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import zyx.existent.Existent;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventAttack;
import zyx.existent.event.events.EventRender2D;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Setting;
import zyx.existent.module.modules.combat.KillAura;
import zyx.existent.utils.render.Colors;
import zyx.existent.utils.render.RenderingUtils;
import zyx.existent.utils.render.animate.AnimationUtil;
import zyx.existent.utils.timer.Timer;

import java.awt.*;

public class TargetHUD extends Module {
    private static final Color COLOR = new Color(40, 40, 40, 255);
    private static Color COLORS = new Color(255, 255, 255, 255);
    private final Timer animationStopwatch = new Timer();
    private EntityOtherPlayerMP target;
    private double healthBarWidth;
    private double hudHeight;

    public TargetHUD(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);
    }

    @EventTarget
    public final void onRenderGui(EventRender2D event) {
        KillAura aura = (KillAura) Existent.getModuleManager().getClazz(KillAura.class);
        float scaledWidth = event.getResolution().getScaledWidth();
        float scaledHeight = event.getResolution().getScaledHeight();

        if (KillAura.target != null && aura.isEnabled()) {
            if (KillAura.target instanceof EntityOtherPlayerMP) {
                this.target = (EntityOtherPlayerMP) KillAura.target;
                float width = 140.0F, height = 40.0F, xOffset = 40.0F;
                float x = scaledWidth / 2.0F - 70.0F, y = scaledHeight / 2.0F + 80.0F;
                float health = this.target.getHealth();
                double hpPercentage = (health / this.target.getMaxHealth());
                hpPercentage = MathHelper.clamp(hpPercentage, 0.0D, 1.0D);
                double hpWidth = 92.0D * hpPercentage;
                int healthColor = Colors.getHealthColor(this.target.getHealth(), this.target.getMaxHealth()).getRGB();
                String healthStr = String.valueOf((int)this.target.getHealth() / 2.0F);
                if (this.animationStopwatch.delay(15L)) {
                    this.healthBarWidth = AnimationUtil.animate(hpWidth, this.healthBarWidth, 0.3529999852180481D);
                    this.hudHeight = AnimationUtil.animate(40.0D, this.hudHeight, 0.10000000149011612D);
                    this.animationStopwatch.reset();
                }

                GL11.glEnable(3089);
                RenderingUtils.prepareScissorBox(x - 4.0F, y - 4.0F, x + 141.0F, y + 47.0F);
                RenderingUtils.drawRect((x - 4.0F), (y - 4.0F), (x + 141.0F), (y + 47.0F), Colors.getTeamColor(this.target));
                RenderingUtils.drawRect((x - 3.0F), (y - 3.0F), (x + 140.0F), (y + 46.0F), COLOR.getRGB());
                RenderingUtils.drawRect((x + 40.0F), (y + 15.0F), (x + 40.0F) + this.healthBarWidth, (y + 25.0F), healthColor);
                mc.fontRendererObj.drawStringWithShadow(healthStr, x + 40.0F + 46.0F - mc.fontRendererObj.getStringWidth(healthStr) / 2.0F, y + 16.0F, -1);
                mc.fontRendererObj.drawStringWithShadow(this.target.getName(), x + 40.0F, y + 2.0F, -1);
                GuiInventory.drawEntityOnScreen(x + 13, y + 40.0, 20, this.target.rotationYaw, this.target.rotationPitch, this.target);
                GL11.glDisable(3089);
            }
        } else {
            this.healthBarWidth = 92.0D;
            this.hudHeight = 0.0D;
            this.target = null;
        }
    }
}
