package php4java.Native;

import java.util.Map;

public class Zval
{
    private long zvalAddr;
    public native long getLong();
    public native double getDouble();
    public native boolean getBoolean();
    public native String getString();
    public native Zval[] getArray();
    public native Map<String, Zval> getHash();
    public native String getJson();
    public native void dispose();
    public void finalize() throws Throwable
    {
        super.finalize();
        this.dispose();
    }
}
