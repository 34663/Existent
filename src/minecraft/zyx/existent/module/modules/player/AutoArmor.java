package zyx.existent.module.modules.player;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClientStatus;
import zyx.existent.Existent;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventUpdate;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Options;
import zyx.existent.module.data.Setting;
import zyx.existent.utils.timer.Timer;

public class AutoArmor extends Module {
    private Timer timer = new Timer();
    public static String MODE = "MODE";
    public static String DELAY = "DELAY";

    public AutoArmor(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(DELAY, new Setting(DELAY, 2, "Inventory clicks delay.", 1, 0, 10));
        settings.put(MODE, new Setting(MODE, new Options("Mode", "Normal", new String[]{"Normal", "OpenInv"}), "Critical method"));
    }

    @EventTarget
    public void onUpdate(EventUpdate eventUpdate) {
        String currentMode = ((Options) settings.get(MODE).getValue()).getSelected();
        long delay = ((Number) settings.get(DELAY).getValue()).longValue() * 50;

        if ((currentMode.equalsIgnoreCase("OpenInv") && !(mc.currentScreen instanceof GuiInventory)) || Existent.getModuleManager().isEnabled(InventoryManager.class)) {
            return;
        }
        if (mc.currentScreen == null || mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiChat) {
            if (timer.delay(delay)) {
                getBestArmor();
            }
        }
    }

    public void shiftClick(int slot) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 0, ClickType.QUICK_MOVE, mc.thePlayer);
    }
    public void drop(int slot) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 1, ClickType.THROW, mc.thePlayer);
    }
    public void getBestArmor() {
        for (int type = 1; type < 5; type++) {
            if (mc.thePlayer.inventoryContainer.getSlot(4 + type).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(4 + type).getStack();
                if (isBestArmor(is, type)) {
                    continue;
                } else {
                    drop(4 + type);
                }
            }
            for (int i = 9; i < 45; i++) {
                if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                    ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                    if (isBestArmor(is, type) && getProtection(is) > 0) {
                        shiftClick(i);
                        timer.reset();
                        if (((Number) settings.get(DELAY).getValue()).longValue() > 0)
                            return;
                    }
                }
            }
        }
    }
    public static boolean isBestArmor(ItemStack stack, int type) {
        float prot = getProtection(stack);
        String strType = "";
        if (type == 1) {
            strType = "helmet";
        } else if (type == 2) {
            strType = "chestplate";
        } else if (type == 3) {
            strType = "leggings";
        } else if (type == 4) {
            strType = "boots";
        }
        if (!stack.getUnlocalizedName().contains(strType)) {
            return false;
        }
        for (int i = 5; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (getProtection(is) > prot && is.getUnlocalizedName().contains(strType))
                    return false;
            }
        }
        return true;
    }
    public static float getProtection(ItemStack stack) {
        float prot = 0;
        if ((stack.getItem() instanceof ItemArmor)) {
            ItemArmor armor = (ItemArmor) stack.getItem();
            prot += armor.damageReduceAmount + (100 - armor.damageReduceAmount) * EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(0), stack) * 0.0075D;
            prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(3), stack) / 100d;
            prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(1), stack) / 100d;
            prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(7), stack) / 100d;
            prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(34), stack) / 50d;
            prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(4), stack) / 100d;
        }
        return prot;
    }
}
