package php4java;

public class Php {
    static {
        System.loadLibrary("php4java");
    }
    public static native void init();
    public static native void shutdown();
    public static native Zval execString(String code);
}
