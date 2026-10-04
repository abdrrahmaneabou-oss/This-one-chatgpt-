package rikka.shizuku.server;

/** Pure monotonic-clock state machine. Caller supplies elapsed realtime, never wall time. */
public final class ForegroundGate {
    public static final long DURATION_MS = 60_000;
    public static final long MAX_HEARTBEAT_GAP_MS = 1_500;
    private int pid = -1;
    private long started;
    private long last;

    public synchronized long pulse(int callerPid, long now) {
        if (pid != callerPid || now < last || now - last > MAX_HEARTBEAT_GAP_MS) {
            pid = callerPid;
            started = now;
        }
        last = now;
        return Math.max(0, DURATION_MS - (now - started));
    }

    public synchronized void reset() {
        pid = -1;
        started = 0;
        last = 0;
    }
}
