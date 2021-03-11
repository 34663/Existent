package zyx.existent.utils.hackerdetector.checks;

import net.minecraft.entity.Entity;
import zyx.existent.utils.PlayerUtils;
import zyx.existent.utils.hackerdetector.Check;
import zyx.existent.utils.hackerdetector.CheckState;
import zyx.existent.utils.hackerdetector.Hacker;

public class KillAuraCheck2 extends Check {
    public KillAuraCheck2() {
        super();
    }

    @Override
    public CheckState check(Hacker en) {
        Entity entity = PlayerUtils.getClosestEntityToEntity(5f, en.player);
        if (entity == null) {
            return CheckState.RESET;
        }
        if (en.player.swingProgress < 2f && en.player.swingProgress != 0f) {
            float[] rots = PlayerUtils.getFacePosEntityRemote(en.player, entity);
            boolean highYawRate = false;
            if (Math.abs((en.player.rotationYaw - en.player.prevRotationYaw)) > 40) {
                highYawRate = true;
            }
            if (Math.abs((en.player.rotationYaw - rots[0])) < 2) {
                if (highYawRate) {
                    tempViolations += 50;
                }
                return CheckState.VIOLATION;
            } else {
                return CheckState.RESET;
            }
        }
        return CheckState.RESET;
    }

    @Override
    public String getPrefix() {
        return " may be using ";
    }

    @Override
    public String getName() {
        return "KillAura (Accuracy)";
    }

    @Override
    public int getMaxViolations() {
        return 100;
    }

    @Override
    public int getDecayTime() {
        return 5000;
    }
}
