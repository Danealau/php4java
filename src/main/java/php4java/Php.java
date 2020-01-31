package php4java;

public class Php {
    static {
        System.loadLibrary("php4java");
    }
    public static native void init();
    public static native void shutdown();
    public static Zval execString(String code) throws Php4JavaException
    {
        try {
            return __eval(code);
        } catch (Exception e)
        {
            throw new Php4JavaException(e);
        }
    }

    private static native Zval __eval(String code);
}
