package zyx.existent.module.modules.misc;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventMove;
import zyx.existent.event.events.EventUpdate;
import zyx.existent.module.Category;
import zyx.existent.module.Module;

import java.util.Collection;

public class PotionSaver extends Module {
    private boolean canStop;
    private int goodPotions;
    private int badPotions;

    public PotionSaver(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);
    }

    @Override
    public void onDisable() {
        this.canStop = false;
        this.goodPotions = 0;
        this.badPotions = 0;
        super.onDisable();
    }

    @EventTarget
    public void EventMove(final EventUpdate event) {
        if (event.isPre()) {
            final Collection<PotionEffect> collection = PotionSaver.mc.thePlayer.getActivePotionEffects();
            this.canStop = (!PotionSaver.mc.thePlayer.isMoving() && collection.size() > 0 && !PotionSaver.mc.thePlayer.isUsingItem() && !PotionSaver.mc.thePlayer.isSwingInProgress && !event.isRotating());
            for (final PotionEffect potioneffect : collection) {
                final Potion potion = Potion.getPotionTypeForName(potioneffect.getEffectName());
                if (potion != null && potion.isUsable()) {
                    ++this.goodPotions;
                } else if (potion != null) {
                    if (!potion.isBadEffect()) {
                        continue;
                    }
                    ++this.badPotions;
                }
            }
            event.setCancelled(this.shouldStopPotion());
            if (!this.canStop) {
                this.goodPotions = 0;
                this.badPotions = 0;
            }
        }
    }

    public boolean shouldStopPotion() {
        return this.goodPotions >= this.badPotions && this.canStop;
    }
}
