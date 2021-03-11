package zyx.existent.utils;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;

public class InventoryUtils implements MCUtil {
    public static boolean isValidItem(ItemStack itemStack) {
        if (itemStack.getDisplayName().startsWith("§a"))
        return true;
        return (itemStack.getItem() instanceof net.minecraft.item.ItemArmor || itemStack.getItem() instanceof ItemSword || itemStack
                .getItem() instanceof net.minecraft.item.ItemTool || itemStack.getItem() instanceof net.minecraft.item.ItemFood || (itemStack
                .getItem() instanceof ItemPotion && !isBadPotion(itemStack)) || itemStack.getItem() instanceof net.minecraft.item.ItemBlock || itemStack
                .getDisplayName().contains("Play") || itemStack.getDisplayName().contains("Game") || itemStack
                .getDisplayName().contains("Right Click"));
    }

    public static float getDamageLevel(ItemStack stack) {
        if (stack.getItem() instanceof ItemSword) {
            ItemSword sword = (ItemSword)stack.getItem();
            float sharpness = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(16), stack) * 1.25F;
            float fireAspect = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(20), stack) * 1.5F;
            return sword.getDamageVsEntity() + sharpness + fireAspect;
        }
        return 0.0F;
    }

    public static boolean isBadPotion(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion) {
            ItemPotion potion = (ItemPotion)stack.getItem();
            if (ItemPotion.isSplash(stack.getItemDamage()))
                for (Object o : PotionType.REGISTRY) {
                    PotionEffect effect = (PotionEffect)o;
                    if (effect.getPotion() == Potion.getPotionById(19) || effect.getPotion() == Potion.getPotionById(7) || effect.getPotion() == Potion.getPotionById(2) || effect.getPotion() == Potion.getPotionById(18))
                        return true;
                }
        }
        return false;
    }
}
