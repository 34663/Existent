package zyx.existent.utils;

public class Test {
    private final int a;

    public Test(final int a) {
        this.a = a;
    }

    public Test(final String s) {
        this.a = s.length() - (s.indexOf(".") + 1);
    }

    public double a(final double n) {
        return Double.parseDouble(this.b(n));
    }

    public String b(final double n) {
        final String replaceAll = String.valueOf(n).replaceAll(",", ".");
        if (replaceAll.contains("E")) {
            return replaceAll;
        }
        if (replaceAll.contains(".")) {
            return replaceAll.substring(0, Math.min(replaceAll.indexOf(46) + this.a + 1, replaceAll.length()));
        }
        return replaceAll;
    }
}
