package zyx.existent.module.modules.misc;

import net.minecraft.network.play.client.CPacketTabComplete;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventPacket;
import zyx.existent.module.Category;
import zyx.existent.module.Module;

import java.util.Random;

public class AntiTabComplete extends Module {
    public AntiTabComplete(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);
    }

    @EventTarget
    public void onPacket(EventPacket event) {
        if (event.isOutgoing() && event.getPacket() instanceof CPacketTabComplete) {
            CPacketTabComplete packet = (CPacketTabComplete) event.getPacket();
            if (packet.getMessage().startsWith(".")) {
                String[] arguments = packet.getMessage().split(" ");
                String[] messages = {"hey what's up ", "dude ", "hey ", "hi ", "man ", "yo ", "howdy ", "omg "};
                Random random = new Random();
                packet.setMessage(messages[random.nextInt(messages.length)] + arguments[arguments.length - 1]);
            }
        }
    }
}
