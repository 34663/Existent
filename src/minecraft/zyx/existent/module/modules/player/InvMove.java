package zyx.existent.module.modules.player;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.CPacketCloseWindow;
import org.lwjgl.input.Keyboard;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventPacketSend;
import zyx.existent.event.events.EventRender2D;
import zyx.existent.event.events.EventTick;
import zyx.existent.event.events.EventUpdate;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Setting;
import zyx.existent.utils.ChatUtils;

import java.util.Objects;

public class InvMove extends Module {
    private String ROTATE = "ROTATION";

    public InvMove(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(ROTATE, new Setting(ROTATE, false, "Rotate in Gui."));
    }

    @EventTarget
    public void onTick(EventTick event) {
        KeyBinding[] keys = new KeyBinding[]{mc.gameSettings.keyBindJump, mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindSprint};
        if (mc.currentScreen != null) {
            if (mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof GuiEditSign) {
                return;
            }

            KeyBinding[] array;
            for (int length = (array = keys).length, i = 0; i < length; ++i) {
                final KeyBinding key = array[i];
                key.pressed = Keyboard.isKeyDown(key.getKeyCode());
            }
        }
    }
    @EventTarget
    public void onRender2D(EventRender2D event) {
        if ((mc.currentScreen != null) && (!(mc.currentScreen instanceof GuiChat)) && ((Boolean) settings.get(ROTATE).getValue())) {
            if (Keyboard.isKeyDown(200)) {
                if (!(mc.thePlayer.rotationPitch == -90.0F))
                    pitch(mc.thePlayer.rotationPitch - 2.0F);
            }
            if (Keyboard.isKeyDown(208)) {
                if (!(mc.thePlayer.rotationPitch == 90.0F))
                    pitch(mc.thePlayer.rotationPitch + 2.0F);
            }
            if (Keyboard.isKeyDown(203)) {
                yaw(mc.thePlayer.rotationYaw - 3.0F);
            }
            if (Keyboard.isKeyDown(205)) {
                yaw(mc.thePlayer.rotationYaw + 3.0F);
            }
        }
    }

    public static void pitch(float pitch) {
        mc.thePlayer.rotationPitch = pitch;
    }
    public static void yaw(float yaw) {
        mc.thePlayer.rotationYaw = yaw;
    }
}
