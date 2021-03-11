package zyx.existent.module.modules.visual;

import com.google.common.collect.Lists;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventDamage;
import zyx.existent.event.events.EventRender3D;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.utils.render.Colors;
import zyx.existent.utils.render.animate.AnimationUtil;
import zyx.existent.utils.render.font.CFontRenderer;
import zyx.existent.utils.render.font.Fonts;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DamageHit extends Module {
    private final List<DamageText> damageTexts = Lists.newArrayList();

    public DamageHit(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);
    }

    @Override
    public void onEnable() {
        damageTexts.clear();
        super.onEnable();
    }

    @EventTarget
    public void onRender3D(EventRender3D event) {
        if (!damageTexts.isEmpty()) {
            for (DamageText text : damageTexts) {
                Entity entity = text.getEntity();
                text.updateAlpha();
                GL11.glPushMatrix();

                double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks - RenderManager.renderPosX;
                double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks - RenderManager.renderPosY + entity.height / 2;
                double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks - RenderManager.renderPosZ;
                GL11.glTranslated(x, y, z);
                GL11.glRotated(-mc.getRenderManager().playerViewY, 0, 1, 0);
                GL11.glRotated(mc.getRenderManager().playerViewX, 1, 0, 0);
                GL11.glScaled(-.03, -.03, 1);
                GlStateManager.disableDepth();
                CFontRenderer font = Fonts.elliot18;
                font.drawStringWithShadow(text.getText(), -font.getStringWidth(text.getText()) / 2F + (float) (text.isNegative() ? -text.getrX() : text.getrX()), (float) (text.isNegative() ? -text.getrY() : text.getrY()), Colors.getColor(250, 50, 50, text.getAlpha()));
                GlStateManager.enableDepth();
                GL11.glPopMatrix();

                if (text.getAlpha() == 0) {
                    damageTexts.remove(text);
                }
            }
        }
    }
    @EventTarget
    public void onDamage(EventDamage event) {
        Entity entity = event.getEntity();
        if (entity == mc.thePlayer || !(entity instanceof EntityPlayer))
            return;
        DamageText text = new DamageText("Pow!", entity);
        if (!contains(text)) {
            damageTexts.add(text);
        }
    }

    private boolean contains(DamageText text) {
        for (DamageText t : damageTexts) {
            if (t.getId() == text.getId())
                return true;
        }
        return false;
    }

    public class DamageText {
        private final String text;
        private int alpha, id;
        private Entity entity;
        private double rX, startX, rMaxX, startY, rMaxY, rY;
        private boolean negative;

        public DamageText(String text, Entity entity) {
            this.text = text;
            this.alpha = 255;
            this.entity = entity;
            this.id = (int) (1000 * Math.random());
            this.negative = ThreadLocalRandom.current().nextBoolean();
            this.rMaxX = ThreadLocalRandom.current().nextDouble(0, 50);
            this.rMaxY = ThreadLocalRandom.current().nextDouble(0, 50);
            this.rX = this.startX = ThreadLocalRandom.current().nextDouble(0, rMaxX);
            this.rY = this.startY = ThreadLocalRandom.current().nextDouble(0, rMaxY);
        }

        public String getText() {
            return text;
        }

        public Entity getEntity() {
            return entity;
        }

        public int getAlpha() {
            return alpha;
        }

        public int getId() {
            return id;
        }

        public double getrX() {
            return rX;
        }

        public double getrY() {
            return rY;
        }

        public boolean isNegative() {
            return negative;
        }

        public void updateAlpha() {
            alpha -= AnimationUtil.delta2 * 0.25;
            alpha = MathHelper.clamp(alpha, 0, 255);
            rX = AnimationUtil.slide(rX, startX, rMaxX, 0.1, true);
            rY = AnimationUtil.slide(rY, startY, rMaxY, 0.1, true);
        }
    }
}
