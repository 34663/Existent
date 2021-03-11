package zyx.existent.module.modules.misc;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventPacketReceive;
import zyx.existent.event.events.EventUpdate;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.utils.hackerdetector.*;
import zyx.existent.utils.hackerdetector.checks.KillAuraCheck1;
import zyx.existent.utils.hackerdetector.checks.KillAuraCheck2;
import zyx.existent.utils.hackerdetector.checks.NoSlowCheck;
import zyx.existent.utils.hackerdetector.checks.VelocityCheck;
import zyx.existent.utils.timer.Timer;

import java.util.ArrayList;
import java.util.HashMap;

public class HackerChecker extends Module {
    public static Timer timer = new Timer();

    public static HashMap<String, Hacker> players = new HashMap<String, Hacker>();
    public static ArrayList<String> muted = new ArrayList<String>();
    public static ArrayList<Check> checks = new ArrayList<Check>();

    public HackerChecker(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        checks.add(new VelocityCheck());
        checks.add(new KillAuraCheck1());
        checks.add(new KillAuraCheck2());
        checks.add(new NoSlowCheck());
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        for (EntityPlayer player : mc.theWorld.playerEntities) {
            if (players.containsKey(player.getName())) {
                continue;
            }
            if (player instanceof EntityPlayerSP) {
                continue;
            }
            if (player.getName().equals(mc.thePlayer.getName())) {
                continue;
            }

            Hacker hc = new Hacker(player);
            players.put(player.getName(), hc);
            muted.add(player.getName());
        }
        for (Hacker en : players.values()) {
            boolean exists = false;
            for (EntityPlayer p : mc.theWorld.playerEntities) {
                if (p.getName().equals(en.player.getName())) {
                    en.player = p;
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                continue;
            }
            en.doChecks();
        }
    }
    @EventTarget
    public void onPacketReceive(EventPacketReceive event) {
        timer.reset();
    }

    public static Check getCheck(String playerName, String checkName) {
        Hacker en = players.get(playerName);
        for (Check check : en.checks) {
            if (check.getName().equals(checkName)) {
                return check;
            }
        }
        return null;
    }
}
