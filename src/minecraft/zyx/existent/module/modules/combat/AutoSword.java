package zyx.existent.module.modules.combat;

import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventUpdate;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Setting;
import zyx.existent.utils.timer.Timer;

public class AutoSword extends Module {
    private Timer timer = new Timer();

    private String INV = "INVENTORYONLY";
    private String DELAY = "DELAY";
    private String SLOT = "SLOT";

    public AutoSword(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(INV, new Setting(INV, false, "InventoryOnly."));
        settings.put(DELAY, new Setting(DELAY, 200, "SwapDelay.", 1, 1, 2000));
        settings.put(SLOT, new Setting(SLOT, 0, "SetSwordSlot", 1, 0, 8));
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        float delay = ((Number) settings.get(DELAY).getValue()).floatValue();
        int slot = ((Number) settings.get(SLOT).getValue()).intValue();

        if (event.isPre() || !this.timer.delay(delay) || (this.mc.currentScreen != null && !(this.mc.currentScreen instanceof GuiInventory)) || (!(this.mc.currentScreen instanceof GuiInventory) && (Boolean) settings.get(INV).getValue())) {
            return;
        }
        int slotId = -1;
        float n = 0.0f;
        for (int i = 9; i < 45; ++i) {
            if (this.mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack stack = this.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (stack.getItem() instanceof ItemSword) {
                    final float a = this.bestSword(stack);
                    if (a >= n) {
                        n = a;
                        slotId = i;
                    }
                }
            }
        }
        final ItemStack stack2 = this.mc.thePlayer.inventoryContainer.getSlot(36 + slot).getStack();
        if (slotId != -1 || stack2 == null || (stack2.getItem() instanceof ItemSword && n > this.bestSword(stack2))) {
            final float n2 = (stack2 != null && stack2.getItem() != null && stack2.getItem() instanceof ItemSword) ? this.bestSword(stack2) : -83474.0f;
            if (slotId != 36 + slot && slotId != -1 && n > n2) {
                this.mc.playerController.windowClick(this.mc.thePlayer.inventoryContainer.windowId, slotId, slot, ClickType.SWAP, this.mc.thePlayer);
                this.timer.reset();
            }
        }
    }

    private float bestSword(final ItemStack itemStack) {
        return ((ItemSword) itemStack.getItem()).getDamageVsEntity() + EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(16), itemStack) * 1.25f + EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(20), itemStack) * 0.01f + EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(19), itemStack) * 1.00f + EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(34), itemStack) * 1.00f;
    }
}
