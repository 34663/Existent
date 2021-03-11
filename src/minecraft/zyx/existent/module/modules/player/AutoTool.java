package zyx.existent.module.modules.player;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import zyx.existent.Existent;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventTick;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.modules.other.Debug;
import zyx.existent.utils.ChatUtils;

public class AutoTool extends Module {
    public AutoTool(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);
    }

    @EventTarget
    public void onTick(EventTick event) {
        if (!this.mc.gameSettings.keyBindAttack.pressed)
            return;
        if (this.mc.objectMouseOver == null)
            return;
        BlockPos blockPos = this.mc.objectMouseOver.getBlockPos();
        if (blockPos == null)
            return;
        updateTool(blockPos);
    }

    public void updateTool(BlockPos paramBlockPos) {
        Block block = this.mc.theWorld.getBlockState(paramBlockPos).getBlock();
        float f = 1.0F;
        byte b = -1;
        for (byte b1 = 0; b1 < 9; b1++) {
            ItemStack itemStack = this.mc.thePlayer.inventory.mainInventory.get(b1);
            ItemStack current = this.mc.thePlayer.inventory.getCurrentItem();
            if (itemStack != null && (itemStack.getStrVsBlock(block.getDefaultState())) > f && !(current.getStrVsBlock(block.getDefaultState()) > f)) {
                f = itemStack.getStrVsBlock(block.getDefaultState());
                b = b1;
            }
        }
        if (b != -1) {
            this.mc.thePlayer.inventory.currentItem = b;

            if (Existent.getModuleManager().isEnabled(Debug.class)) {
                ChatUtils.printChat("\2479SwitchTool\2477: \247f" + this.mc.thePlayer.inventory.getCurrentItem().getDisplayName());
            }
        }
    }
}
