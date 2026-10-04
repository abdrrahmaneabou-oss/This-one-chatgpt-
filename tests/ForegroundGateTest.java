import rikka.shizuku.server.ForegroundGate;

public class ForegroundGateTest {
    private static void eq(long expected, long actual) {
        if (expected != actual) throw new AssertionError(expected + " != " + actual);
    }
    public static void main(String[] args) {
        ForegroundGate g = new ForegroundGate();
        eq(60_000, g.pulse(10, 0));
        for (long t = 250; t < 60_000; t += 250) eq(60_000 - t, g.pulse(10, t));
        eq(0, g.pulse(10, 60_000));
        g.reset(); // pause/focus loss even for a fraction of a second
        eq(60_000, g.pulse(10, 60_001));
        eq(59_750, g.pulse(10, 60_251));
        eq(60_000, g.pulse(10, 62_000)); // lost heartbeat cannot count background time
        eq(60_000, g.pulse(11, 62_250)); // process death/restart resets progress
        eq(60_000, g.pulse(11, 10)); // backwards clock fails closed
        ForegroundGate restarted = new ForegroundGate();
        eq(60_000, restarted.pulse(11, 900_000));
        System.out.println("Foreground gate tests passed");
    }
}
