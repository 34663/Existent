package zyx.existent.utils.hackerdetector;

import zyx.existent.utils.MCUtil;
import zyx.existent.utils.timer.Timer;

public abstract class Check implements MCUtil {
    public Timer timer = new Timer();
    private int violations = 0;
    public int tempViolations = 0;
    private boolean enabled = true;

    public abstract CheckState check(Hacker en);

    public int getMaxViolations() {
        return 20;
    }

    public abstract String getName();

    public int getViolations() {
        return violations;
    }

    public void violate() {
        violations++;
        tempViolations = 0;
    }

    public void violate(int amount) {
        violations += amount;
        tempViolations = 0;
    }

    public String getPrefix() {
        return " used ";
    }

    public boolean getMentionName() {
        return true;
    }

    public int getDecayTime() {
        return 0;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
