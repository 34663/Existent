package zyx.existent.utils.hackerdetector;

import net.minecraft.entity.player.EntityPlayer;
import zyx.existent.gui.notification.NotificationPublisher;
import zyx.existent.gui.notification.NotificationType;
import zyx.existent.module.modules.misc.HackerChecker;
import zyx.existent.utils.hackerdetector.checks.KillAuraCheck1;
import zyx.existent.utils.hackerdetector.checks.KillAuraCheck2;
import zyx.existent.utils.hackerdetector.checks.NoSlowCheck;
import zyx.existent.utils.hackerdetector.checks.VelocityCheck;

import java.util.ArrayList;

public class Hacker {
    public EntityPlayer player;
    public ArrayList<Check> checks = new ArrayList<Check>();
    public int maxAps = 0;
    public double maxYawrate = 0;
    boolean didIntercept = false;

    public Hacker(EntityPlayer player) {
        this.player = player;
        checks.add(new VelocityCheck());
        checks.add(new KillAuraCheck1());
        checks.add(new KillAuraCheck2());
        checks.add(new NoSlowCheck());
    }

    public void updateEnabledChecks() {
        for (Check check : HackerChecker.checks) {
            Check found = HackerChecker.getCheck(this.player.getName(), check.getName());
            assert found != null;
            found.setEnabled(check.isEnabled());
        }
    }

    public void doChecks() {
        updateEnabledChecks();
        maxAps = Math.max(maxAps, player.aps);
        maxYawrate = Math.max(maxYawrate, Math.abs(player.rotationYaw - player.prevRotationYaw));
        for (Check check : this.checks) {
            if (!check.isEnabled()) {
                continue;
            }
            if (HackerChecker.timer.getTime() < 200) {
                if (didIntercept) {
                    didIntercept = false;
                    return;
                }
                CheckState state = check.check(this);
                if (state == CheckState.VIOLATION) {
                    check.timer.reset();
                    check.tempViolations++;
                } else if (state == CheckState.RESET) {
                    if (check.timer.delay(check.getDecayTime())) {
                        check.tempViolations = 0;
                    }
                } else if (state == CheckState.IDLE) {

                }
            } else {
                didIntercept = true;
            }
            if (check.tempViolations >= check.getMaxViolations()) {
                check.violate();
                if (!HackerChecker.muted.contains(player.getName())) {
                    if (check.getMentionName()) {
                        NotificationPublisher.queue("HackerChecker", "Player " + "" + player.getName() + check.getPrefix() + check.getName() + " vl=(" + check.getViolations() + ")", NotificationType.WARNING);
                    } else {
                        NotificationPublisher.queue("HackerChecker", "Player " + "" + player.getName() + check.getPrefix() + " vl=(" + check.getViolations() + ")", NotificationType.WARNING);
                    }
                }
            }
        }
    }

    public int getViolations() {
        int i = 0;
        for (Check check : checks) {
            if (!check.isEnabled()) {
                continue;
            }
            i += check.getViolations();
        }
        return i;
    }
}
