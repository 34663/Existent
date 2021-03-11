package zyx.existent.module.modules.player;

import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventUpdate;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Setting;
import zyx.existent.utils.misc.MiscUtils;
import zyx.existent.utils.timer.Timer;

import java.util.ArrayList;
import java.util.List;

public class ChestStealer extends Module {
    public static TileEntityChest tileEntityChest;
    private final List<TileEntityChest> entityChests;
    private final Timer timer1 = new Timer();
    private final Timer timer2 = new Timer();
    private final String[] blacklist;

    private String DELAY = "DELAY";
    private String AURA = "CHESTAURA";

    public ChestStealer(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(DELAY, new Setting(DELAY, 22, "Inventory clicks delay.", 1, 0, 100));
        settings.put(AURA, new Setting(AURA, false, "ChestAura."));
        this.blacklist = new String[] { "menu", "selector", "game", "gui", "server", "inventory", "play", "teleporter", "shop", "melee", "armor", "block", "castle", "mini", "warp", "teleport", "user", "team", "tool", "sure", "trade", "cancel", "accept", "soul", "book", "recipe", "profile", "tele", "port", "map", "kit", "select", "lobby", "vault", "lock", "quick", "travel", "cake", "war", "pvp" };
        this.entityChests = new ArrayList<TileEntityChest>();
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (event.isPre()) {
            if ((Boolean) settings.get(AURA).getValue()) {
                this.tileEntityChest = this.getTileEntityChest();
            }
            if (this.mc.currentScreen instanceof GuiChest) {
                if (this.tileEntityChest != null) {
                    this.entityChests.add(this.tileEntityChest);
                }
                final GuiChest guiChest = (GuiChest) this.mc.currentScreen;
                final String lowerCase = guiChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase();
                final String[] ys = this.blacklist;
                final float delay = ((Number) settings.get(DELAY).getValue()).floatValue() * 10;

                for (int length = ys.length, i = 0; i < length; ++i) {
                    if (lowerCase.contains(ys[i])) {
                        return;
                    }
                }
                for (int index = 0; index < guiChest.getLowerChestInventory().getSizeInventory(); index++) {
                    ItemStack stack = guiChest.getLowerChestInventory().getStackInSlot(index);
                    if (stack != null && isValidItem(stack))
                        if (this.timer2.delay(delay)) {
                            this.mc.playerController.windowClick(guiChest.inventorySlots.windowId, index, 0, ClickType.QUICK_MOVE, this.mc.thePlayer);
                            this.timer2.reset();
                        }
                }
                if (this.isChestEmpty(guiChest)) {
                    if (this.timer1.delay(MiscUtils.Random2(75, 150))) {
                        this.mc.thePlayer.closeScreen();
                    }
                } else {
                    this.timer1.reset();
                }
            } else if ((Boolean) settings.get(AURA).getValue() && this.tileEntityChest != null) {
                final float[] rotationChest = this.getRotationChest(this.tileEntityChest.getPos().getX() + 0.5f, this.tileEntityChest.getPos().getY() - 1, this.tileEntityChest.getPos().getZ() + 0.5f, this.mc.thePlayer);
                event.setPitch(rotationChest[1]);
                event.setYaw(rotationChest[0]);
                mc.thePlayer.rotationYawHead = rotationChest[0];
                mc.thePlayer.renderYawOffset = rotationChest[0];
                mc.thePlayer.rotationPitchHead = rotationChest[1];
            }
        } else if (this.tileEntityChest != null && !(this.mc.currentScreen instanceof GuiChest)) {
            this.mc.thePlayer.swingArm(EnumHand.MAIN_HAND);
            this.mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(this.tileEntityChest.getPos(), EnumFacing.UP, EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
        }
        if (this.mc.currentScreen == null) {
            this.timer1.reset();
        }
    }

    private TileEntityChest getTileEntityChest() {
        TileEntity tileEntity = null;
        double distanceSq = 3.5 * 3.5;
        for (final TileEntity tileEntity2 : this.mc.theWorld.loadedTileEntityList) {
            if (tileEntity2 instanceof TileEntityChest && !this.entityChests.contains(tileEntity2) && this.mc.thePlayer.getDistanceSq(tileEntity2.getPos()) < distanceSq) {
                tileEntity = tileEntity2;
                distanceSq = this.mc.thePlayer.getDistanceSq(tileEntity.getPos());
            }
        }
        return (TileEntityChest) tileEntity;
    }
    private float[] getRotationChest(final double n, final double n2, final double n3, final EntityPlayer entityPlayer) {
        final double n4 = entityPlayer.posX - n;
        final double n5 = entityPlayer.posY - n2;
        final double n6 = entityPlayer.posZ - n3;
        final double sqrt = Math.sqrt(n4 * n4 + n5 * n5 + n6 * n6);
        return new float[]{(float) (Math.atan2(n6 / sqrt, n4 / sqrt) * 180.0 / 3.141592653589793 + 90.0), (float) (Math.asin(n5 / sqrt) * 180.0 / 3.141592653589793)};
    }
    private boolean isValidItem(ItemStack itemStack) {
        return (itemStack.getItem() instanceof ItemArmor || itemStack.getItem() instanceof ItemSword || itemStack.getItem() instanceof ItemTool || itemStack.getItem() instanceof ItemFood || itemStack.getItem() instanceof ItemPotion || itemStack.getItem() instanceof ItemBlock);
    }
    private boolean isChestEmpty(GuiChest chest) {
        for (int index = 0; index < chest.getLowerChestInventory().getSizeInventory(); index++) {
            ItemStack stack = chest.getLowerChestInventory().getStackInSlot(index);
            if (stack != null && isValidItem(stack))
                return false;
        }
        return true;
    }
}
