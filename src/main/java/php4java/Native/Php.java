package php4java.Native;

public class Php
{
    public Php() {}

    public void preloadLibrary(String libraryName)
    {
        System.loadLibrary(libraryName);
    }

    public native void init();
    public native void shutdown();
    public native Zval __eval(String code);
}
