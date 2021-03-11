package zyx.existent.utils.timer;

public class Timer2 {
    private long time = this.getTime();

    private long getTime() {
        return System.nanoTime() / 1000000L;
    }

    public boolean hasReached(long milliseconds) {
        if (this.getTime() - this.time >= milliseconds) {
            this.reset();
            return true;
        } else {
            return false;
        }
    }

    public final long getElapsedTime() {
        return getTime() - this.time;
    }

    public void reset() {
        this.time = this.getTime();
    }
}
