package zyx.existent.utils.hackerdetector.checks;

import zyx.existent.utils.hackerdetector.Check;
import zyx.existent.utils.hackerdetector.CheckState;
import zyx.existent.utils.hackerdetector.Hacker;

public class KillAuraCheck1 extends Check {
    @Override
    public CheckState check(Hacker en) {
        if (Math.abs(en.player.rotationYaw - en.player.prevRotationYaw) > 50 && en.player.swingProgress != 0
                && en.player.aps >= 3) {
            return CheckState.VIOLATION;
        }
        return CheckState.RESET;
    }

    @Override
    public String getName() {
        return "KillAura (YawRate)";
    }

    @Override
    public int getMaxViolations() {
        return 10;
    }

    @Override
    public int getDecayTime() {
        return 1000;
    }
}
