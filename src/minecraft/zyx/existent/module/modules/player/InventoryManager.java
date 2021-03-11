package zyx.existent.module.modules.player;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import zyx.existent.Existent;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventUpdate;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Options;
import zyx.existent.module.data.Setting;
import zyx.existent.module.modules.movement.Scaffold;
import zyx.existent.utils.timer.Timer;

import java.util.ArrayList;

public class InventoryManager extends Module {
    public static final String BLOCKCAP = "BLOCKCAP";
    public static final String DELAY = "DELAY";
    public static final String ARCHERY = "ARCHERY";
    public static final String FOOD = "FOOD";
    public static final String INVCLEANER = "INVCLEANER";
    public static final String MODE = "MODE";
    public static final String SWORD = "SWORD";
    public static final String INV = "INVENTORY ONLY";

    public static int weaponSlot = 36, pickaxeSlot = 37, axeSlot = 38, shovelSlot = 39;
    Timer timer = new Timer();
    ArrayList<Integer> whitelistedItems = new ArrayList<>();

    public InventoryManager(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(BLOCKCAP, new Setting(BLOCKCAP, 128, "Max blocks allowed in your inventory.", 8, -1, 256));
        settings.put(DELAY, new Setting(DELAY, 1, "Inventory clicks delay.", 1, 0, 10));
        settings.put(ARCHERY, new Setting(ARCHERY, false, "Clean bows and arrows."));
        settings.put(FOOD, new Setting(FOOD, false, "Clean food. Keeps Golden Apples."));
        settings.put(SWORD, new Setting(SWORD, false, "Keep only swords as weapon."));
        settings.put(INVCLEANER, new Setting(INVCLEANER, true, "Cleans your inventory for you."));
        settings.put(INV, new Setting(INV, false, ""));
        settings.put(MODE, new Setting(MODE, new Options("Mode", "Basic", new String[]{"Basic", "Anni"}), "InvManager method."));
    }

    @EventTarget
    public void onUpdate(EventUpdate eventUpdate) {
        long delay = ((Number) settings.get(DELAY).getValue()).longValue() * 50;
        String mode = ((Options) settings.get(MODE).getValue()).getSelected();
        Module armor = Existent.getModuleManager().getClazz(AutoArmor.class);
        long Adelay = ((Number) armor.getSetting(AutoArmor.DELAY).getValue()).longValue() * 50;
        String Amode = ((Options) armor.getSetting(AutoArmor.MODE).getValue()).getSelected();
        this.setSuffix(mode);
        if (!eventUpdate.isPre())
            return;
        if (timer.delay(Adelay) && armor.isEnabled()) {
            if (!Amode.equalsIgnoreCase("OpenInv") || mc.currentScreen instanceof GuiInventory) {
                if (mc.currentScreen == null || mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiChat) {
                    getBestArmor();
                }
            }
        }
        if (armor.isEnabled())
            for (int type = 1; type < 5; type++) {
                if (mc.thePlayer.inventoryContainer.getSlot(4 + type).getHasStack()) {
                    ItemStack is = mc.thePlayer.inventoryContainer.getSlot(4 + type).getStack();
                    if (!AutoArmor.isBestArmor(is, type)) {
                        return;
                    }
                } else if (invContainsType(type - 1)) {
                    return;
                }
            }
        if ((!(this.mc.currentScreen instanceof GuiInventory) && (Boolean) settings.get(INV).getValue())) {
            return;
        }
        if (mc.currentScreen == null || mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiChat) {
            if (timer.delay(delay) && weaponSlot >= 36) {
                if (!mc.thePlayer.inventoryContainer.getSlot(weaponSlot).getHasStack()) {
                    getBestWeapon(weaponSlot);
                } else {
                    if (!isBestWeapon(mc.thePlayer.inventoryContainer.getSlot(weaponSlot).getStack())) {
                        getBestWeapon(weaponSlot);
                    }
                }
            }
            if (timer.delay(delay) && pickaxeSlot >= 36) {
                getBestPickaxe(pickaxeSlot);
            }
            if (timer.delay(delay) && shovelSlot >= 36) {
                getBestShovel(shovelSlot);
            }
            if (timer.delay(delay) && axeSlot >= 36) {
                getBestAxe(axeSlot);
            }
            if (timer.delay(delay) && (Boolean) settings.get(INVCLEANER).getValue()) {
                for (int i = 9; i < 45; i++) {
                    if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                        ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                        if (shouldDrop(is, i)) {
                            drop(i);
                            timer.reset();
                            if (delay > 0)
                                break;
                        }
                    }
                }
            }
        }
    }

    public void shiftClick(int slot) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 0, ClickType.QUICK_MOVE, mc.thePlayer);
    }
    public void swap(int slot1, int hotbarSlot) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot1, hotbarSlot, ClickType.SWAP, mc.thePlayer);
    }
    public void drop(int slot) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 1, ClickType.THROW, mc.thePlayer);
    }
    public boolean isBestWeapon(ItemStack stack) {
        float damage = getDamage(stack);
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (getDamage(is) > damage && (is.getItem() instanceof ItemSword || !(Boolean) settings.get(SWORD).getValue()))
                    return false;
            }
        }
        if ((stack.getItem() instanceof ItemSword || !(Boolean) settings.get(SWORD).getValue())) {
            return true;
        } else {
            return false;
        }
    }
    public void getBestWeapon(int slot) {
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (isBestWeapon(is) && getDamage(is) > 0 && (is.getItem() instanceof ItemSword || !(Boolean) settings.get(SWORD).getValue())) {
                    swap(i, slot - 36);
                    timer.reset();
                    break;
                }
            }
        }
    }
    private float getDamage(ItemStack stack) {
        float damage = 0;
        Item item = stack.getItem();
        if (item instanceof ItemTool) {
            ItemTool tool = (ItemTool) item;
            damage += tool.getDamageVsEntity();
        }
        if (item instanceof ItemSword) {
            ItemSword sword = (ItemSword) item;
            damage += sword.getDamageVsEntity();
        }
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(16), stack) * 1.25f + EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(20), stack) * 0.01f;
        return damage;
    }
    public boolean shouldDrop(ItemStack stack, int slot) {
        if (stack.getDisplayName().toLowerCase().contains("(right click)")) {
            return false;
        }
        if (stack.getDisplayName().toLowerCase().contains("§k||")) {
            return false;
        }
        if ((slot == weaponSlot && isBestWeapon(mc.thePlayer.inventoryContainer.getSlot(weaponSlot).getStack())) ||
                (slot == pickaxeSlot && isBestPickaxe(mc.thePlayer.inventoryContainer.getSlot(pickaxeSlot).getStack()) && pickaxeSlot >= 0) ||
                (slot == axeSlot && isBestAxe(mc.thePlayer.inventoryContainer.getSlot(axeSlot).getStack()) && axeSlot >= 0) ||
                (slot == shovelSlot && isBestShovel(mc.thePlayer.inventoryContainer.getSlot(shovelSlot).getStack()) && shovelSlot >= 0)) {
            return false;
        }
        if (stack.getItem() instanceof ItemArmor) {
            for (int type = 1; type < 5; type++) {
                if (mc.thePlayer.inventoryContainer.getSlot(4 + type).getHasStack()) {
                    ItemStack is = mc.thePlayer.inventoryContainer.getSlot(4 + type).getStack();
                    if (AutoArmor.isBestArmor(is, type)) {
                        continue;
                    }
                }
                if (AutoArmor.isBestArmor(stack, type)) {
                    return false;
                }
            }
        }
        if (stack.getItem() instanceof ItemBlock && (getBlockCount() > ((Number) settings.get(BLOCKCAP).getValue()).intValue() || Scaffold.invalidBlocks.contains(((ItemBlock) stack.getItem()).getBlock()))) {
            return true;
        }
        if (stack.getItem() instanceof ItemPotion) {
            if (isBadPotion(stack)) {
                return true;
            }
        }
        if (stack.getItem() instanceof ItemFood && (Boolean) settings.get(FOOD).getValue() && !(stack.getItem() instanceof ItemAppleGold)) {
            return true;
        }
        if (stack.getItem() instanceof ItemHoe || stack.getItem() instanceof ItemTool || stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemArmor) {
            return true;
        }
        if ((stack.getItem() instanceof ItemBow || stack.getItem().getUnlocalizedName().contains("arrow")) && (Boolean) settings.get(ARCHERY).getValue()) {
            return true;
        }

        return (stack.getItem().getUnlocalizedName().contains("tnt")) ||
                (stack.getItem().getUnlocalizedName().contains("stick")) ||
                (stack.getItem().getUnlocalizedName().contains("egg")) ||
                (stack.getItem().getUnlocalizedName().contains("string")) ||
                (stack.getItem().getUnlocalizedName().contains("cake")) ||
                (stack.getItem().getUnlocalizedName().contains("mushroom")) ||
                (stack.getItem().getUnlocalizedName().contains("flint")) ||
                (stack.getItem().getUnlocalizedName().contains("compass")) ||
                (stack.getItem().getUnlocalizedName().contains("dyePowder")) ||
                (stack.getItem().getUnlocalizedName().contains("feather")) ||
                (stack.getItem().getUnlocalizedName().contains("bucket")) ||
                (stack.getItem().getUnlocalizedName().contains("chest") && !stack.getDisplayName().toLowerCase().contains("collect")) ||
                (stack.getItem().getUnlocalizedName().contains("snow")) ||
                (stack.getItem().getUnlocalizedName().contains("fish")) ||
                (stack.getItem().getUnlocalizedName().contains("enchant")) ||
                (stack.getItem().getUnlocalizedName().contains("exp")) ||
                (stack.getItem().getUnlocalizedName().contains("shears")) ||
                (stack.getItem().getUnlocalizedName().contains("anvil")) ||
                (stack.getItem().getUnlocalizedName().contains("torch")) ||
                (stack.getItem().getUnlocalizedName().contains("seeds")) ||
                (stack.getItem().getUnlocalizedName().contains("leather")) ||
                (stack.getItem().getUnlocalizedName().contains("reeds")) ||
                (stack.getItem().getUnlocalizedName().contains("skull")) ||
                (stack.getItem().getUnlocalizedName().contains("record")) ||
                (stack.getItem().getUnlocalizedName().contains("snowball")) ||
                (stack.getItem() instanceof ItemGlassBottle) ||
                (stack.getItem().getUnlocalizedName().contains("piston"));
    }
    public ArrayList<Integer> getWhitelistedItem() {
        return whitelistedItems;
    }
    private int getBlockCount() {
        int blockCount = 0;
        for (int i = 0; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                Item item = is.getItem();
                if (is.getItem() instanceof ItemBlock && !Scaffold.invalidBlocks.contains(((ItemBlock) item).getBlock())) {
                    blockCount += is.stackSize;
                }
            }
        }
        return blockCount;
    }
    private void getBestPickaxe(int slot) {
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

                if (isBestPickaxe(is) && pickaxeSlot != i) {
                    if (!isBestWeapon(is))
                        if (!mc.thePlayer.inventoryContainer.getSlot(pickaxeSlot).getHasStack()) {
                            swap(i, pickaxeSlot - 36);
                            timer.reset();
                            if (((Number) settings.get(DELAY).getValue()).longValue() > 0)
                                return;
                        } else if (!isBestPickaxe(mc.thePlayer.inventoryContainer.getSlot(pickaxeSlot).getStack())) {
                            swap(i, pickaxeSlot - 36);
                            timer.reset();
                            if (((Number) settings.get(DELAY).getValue()).longValue() > 0)
                                return;
                        }
                }
            }
        }
    }
    private void getBestShovel(int slot) {
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

                if (isBestShovel(is) && shovelSlot != i) {
                    if (!isBestWeapon(is))
                        if (!mc.thePlayer.inventoryContainer.getSlot(shovelSlot).getHasStack()) {
                            swap(i, shovelSlot - 36);
                            timer.reset();
                            if (((Number) settings.get(DELAY).getValue()).longValue() > 0)
                                return;
                        } else if (!isBestShovel(mc.thePlayer.inventoryContainer.getSlot(shovelSlot).getStack())) {
                            swap(i, shovelSlot - 36);
                            timer.reset();
                            if (((Number) settings.get(DELAY).getValue()).longValue() > 0)
                                return;
                        }
                }
            }
        }
    }
    private void getBestAxe(int slot) {
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

                if (isBestAxe(is) && axeSlot != i) {
                    if (!isBestWeapon(is))
                        if (!mc.thePlayer.inventoryContainer.getSlot(axeSlot).getHasStack()) {
                            swap(i, axeSlot - 36);
                            timer.reset();
                            if (((Number) settings.get(DELAY).getValue()).longValue() > 0)
                                return;
                        } else if (!isBestAxe(mc.thePlayer.inventoryContainer.getSlot(axeSlot).getStack())) {
                            swap(i, axeSlot - 36);
                            timer.reset();
                            if (((Number) settings.get(DELAY).getValue()).longValue() > 0)
                                return;
                        }
                }
            }
        }
    }
    private boolean isBestPickaxe(ItemStack stack) {
        Item item = stack.getItem();
        if (!(item instanceof ItemPickaxe))
            return false;
        float value = getToolEffect(stack);
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (getToolEffect(is) > value && is.getItem() instanceof ItemPickaxe) {
                    return false;
                }
            }
        }
        return true;
    }
    private boolean isBestShovel(ItemStack stack) {
        Item item = stack.getItem();
        if (!(item instanceof ItemSpade))
            return false;
        float value = getToolEffect(stack);
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (getToolEffect(is) > value && is.getItem() instanceof ItemSpade) {
                    return false;
                }
            }
        }
        return true;
    }
    private boolean isBestAxe(ItemStack stack) {
        Item item = stack.getItem();
        if (!(item instanceof ItemAxe))
            return false;
        float value = getToolEffect(stack);
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (getToolEffect(is) > value && is.getItem() instanceof ItemAxe && !isBestWeapon(stack)) {
                    return false;
                }
            }
        }
        return true;
    }
    private float getToolEffect(ItemStack stack) {
        Item item = stack.getItem();
        if (!(item instanceof ItemTool))
            return 0;
        String name = item.getUnlocalizedName();
        ItemTool tool = (ItemTool) item;
        float value = 1;
        if (item instanceof ItemPickaxe) {
            value = tool.getStrVsBlock(stack, Blocks.STONE.getDefaultState());
            if (name.toLowerCase().contains("gold")) {
                value -= 5;
            }
        } else if (item instanceof ItemSpade) {
            value = tool.getStrVsBlock(stack, Blocks.DIRT.getDefaultState());
            if (name.toLowerCase().contains("gold")) {
                value -= 5;
            }
        } else if (item instanceof ItemAxe) {
            value = tool.getStrVsBlock(stack, Blocks.LOG.getDefaultState());
            if (name.toLowerCase().contains("gold")) {
                value -= 5;
            }
        } else
            return 1f;
        value += EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(32), stack) * 0.0075D;
        value += EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(34), stack) / 100d;
        return value;
    }
    private boolean isBadPotion(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion) {
            for (final Object o : PotionUtils.getEffectsFromStack(stack)) {
                final PotionEffect effect = (PotionEffect) o;
                if (effect.getPotion() == Potion.getPotionById(19) || effect.getPotion() == Potion.getPotionById(7) || effect.getPotion() == Potion.getPotionById(2) || effect.getPotion() == Potion.getPotionById(18)) {
                    return true;
                }
            }
        }
        return false;
    }
    boolean invContainsType(int type) {
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                Item item = is.getItem();
                if (item instanceof ItemArmor) {
                    ItemArmor armor = (ItemArmor) item;
                    if (type == armor.armorType.getIndex()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public void getBestArmor() {
        for (int type = 1; type < 5; type++) {
            if (mc.thePlayer.inventoryContainer.getSlot(4 + type).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(4 + type).getStack();
                if (AutoArmor.isBestArmor(is, type)) {
                    continue;
                } else {
                    drop(4 + type);
                }
            }
            for (int i = 9; i < 45; i++) {
                if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                    ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                    if (AutoArmor.isBestArmor(is, type) && AutoArmor.getProtection(is) > 0) {
                        shiftClick(i);
                        timer.reset();
                        if (((Number) settings.get(DELAY).getValue()).longValue() > 0)
                            return;
                    }
                }
            }
        }
    }
}
