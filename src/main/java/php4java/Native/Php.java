package php4java.Native;

public class Php
{
    static
    {
        System.loadLibrary("php4java");
    }

    public Php() {}

    public void preloadLibrary(String libraryPath)
    {
        System.load(libraryPath);
    }

    public native void init();
    public native void shutdown();
    public native Zval __eval(String code);
}
