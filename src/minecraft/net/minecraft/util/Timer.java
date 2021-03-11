package net.minecraft.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;

public class Timer {
    public int elapsedTicks;
    public float renderPartialTicks;
    public float elapsedPartialTicks;
    public long lastSyncSysClock;
    public float tickLength;

    public float timerSpeed = 1.0F;
    private long counter;
    private long lastSyncHRClock;
    private double timeSyncAdjustment = 1.0D;
    private double lastHRTime;
    float ticksPerSecond;


    public Timer(float p_i1018_1_) {
        this.tickLength = 1000.0F / p_i1018_1_;
        this.lastSyncSysClock = Minecraft.getSystemTime();

        this.ticksPerSecond = p_i1018_1_;
        this.lastSyncHRClock = System.nanoTime() / 1000000L;
    }

    public void updateTimer() {
//        long i = Minecraft.getSystemTime();
//        this.elapsedPartialTicks = (float) (i - this.lastSyncSysClock) / this.tickLength;
//        this.lastSyncSysClock = i;
//        this.renderPartialTicks += this.elapsedPartialTicks;
//        this.elapsedTicks = (int) this.renderPartialTicks;
//        this.renderPartialTicks -= (float) this.elapsedTicks;

        long i = Minecraft.getSystemTime();
        long j = i - this.lastSyncSysClock;
        long k = System.nanoTime() / 1000000L;
        double d0 = (double) k / 1000.0D;

        if (j <= 1000L && j >= 0L) {
            this.counter += j;

            if (this.counter > 1000L) {
                long l = k - this.lastSyncHRClock;
                double d1 = (double) this.counter / (double) l;
                this.timeSyncAdjustment += (d1 - this.timeSyncAdjustment) * 0.20000000298023224D;
                this.lastSyncHRClock = k;
                this.counter = 0L;
            }

            if (this.counter < 0L) {
                this.lastSyncHRClock = k;
            }
        } else {
            this.lastHRTime = d0;
        }

        this.lastSyncSysClock = i;
        double d2 = (d0 - this.lastHRTime) * this.timeSyncAdjustment;
        this.lastHRTime = d0;
        d2 = MathHelper.clamp(d2, 0.0D, 1.0D);
        this.elapsedPartialTicks = (float) ((double) this.elapsedPartialTicks + d2 * (double) this.timerSpeed * (double) this.ticksPerSecond);
        this.elapsedTicks = (int) this.elapsedPartialTicks;
        this.elapsedPartialTicks -= (float) this.elapsedTicks;

        if (this.elapsedTicks > 10) {
            this.elapsedTicks = 10;
        }

        this.renderPartialTicks = this.elapsedPartialTicks;
    }
}
