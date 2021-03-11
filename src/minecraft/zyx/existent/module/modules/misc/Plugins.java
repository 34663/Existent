package zyx.existent.module.modules.misc;

import joptsimple.internal.Strings;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.network.play.server.SPacketTabComplete;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventPacket;
import zyx.existent.event.events.EventUpdate;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.utils.ChatUtils;
import zyx.existent.utils.timer.Timer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Plugins extends Module {
    private final Timer tickTimer = new Timer();

    public Plugins(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);
    }

    @Override
    public void onEnable() {
        if (mc.thePlayer == null)
            return;
        mc.getConnection().sendPacket(new CPacketTabComplete("/"));
        tickTimer.reset();
        super.onEnable();
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (tickTimer.delay(2000)) {
            ChatUtils.printChatprefix("Plugins check timed out...");
            tickTimer.reset();
        }
    }

    @EventTarget
    public void onPacket(EventPacket event) {
        if (event.getPacket() instanceof SPacketTabComplete) {
            final SPacketTabComplete s3APacketTabComplete = (SPacketTabComplete) event.getPacket();
            final List<String> plugins = new ArrayList<>();
            final String[] commands = s3APacketTabComplete.getMatches();

            for (final String command1 : commands) {
                final String[] command = command1.split(":");
                if (command.length > 1) {
                    final String pluginName = command[0].replace("/", "");
                    if (!plugins.contains(pluginName))
                        plugins.add(pluginName);
                }
                ChatUtils.printChat(command[0]);
            }

            Collections.sort(plugins);
            if (!plugins.isEmpty()) {
                ChatUtils.printChatprefix("§aPlugins §7(§8" + plugins.size() + "§7): §c" + Strings.join(plugins.toArray(new String[0]), "§7, §c"));
            } else {
                ChatUtils.printChatprefix("§cNo plugins found.");
            }
            toggle();
            tickTimer.reset();
        }
    }
}
