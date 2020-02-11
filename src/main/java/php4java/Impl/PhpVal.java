package php4java.Impl;

import java.util.Map;
import php4java.Interfaces.*;
import java.lang.reflect.*;
import php4java.Native.*;

public class PhpVal implements IPhpVal
{
    public PhpVal(Object obj) throws IllegalAccessException
    {
        if (obj.getClass().getName() != "php4java.Native.Zval")
            throw new IllegalAccessException();
        _phpVal = obj;
    }

    @Override
    public Long asLong()
    {
        try
        {
            return (long)_phpVal.getClass().getDeclaredMethod("getLong").invoke(_phpVal);
        }
        catch(InvocationTargetException | IllegalAccessException | NoSuchMethodException exc) {}
        return null;
    }

    @Override
    public Double asDouble()
    {
        try
        {
            return (double)_phpVal.getClass().getDeclaredMethod("getDouble").invoke(_phpVal);
        }
        catch(InvocationTargetException | IllegalAccessException | NoSuchMethodException exc) {}
        return null;
    }

    @Override
    public Boolean asBoolean()
    {
        try
        {
            return (boolean)_phpVal.getClass().getDeclaredMethod("getBoolean").invoke(_phpVal);
        }
        catch(InvocationTargetException | IllegalAccessException | NoSuchMethodException exc) {}
        return null;
    }

    @Override
    public String asString()
    {
        try
        {
            return (String)_phpVal.getClass().getDeclaredMethod("getString").invoke(_phpVal);
        }
        catch(InvocationTargetException | IllegalAccessException | NoSuchMethodException exc) {}
        return null;
    }

    @Override
    public IPhpVal[] asArray()
    {
        try
        {
            var res = (Object[])_phpVal.getClass().getDeclaredMethod("getArray").invoke(_phpVal);
            var result = new IPhpVal[res.length];
            for (int i = 0; i < res.length; ++i)
                result[i] = new PhpVal(res[i]);
            return result;
        }
        catch(InvocationTargetException | IllegalAccessException | NoSuchMethodException exc) {}
        return null;
    }

    @Override
    public Map<String, IPhpVal> asHash()
    {
        try
        {
            var map = (Map<String, Zval>)_phpVal.getClass().getDeclaredMethod("getHash").invoke(_phpVal);
            return null;
        }
        catch(InvocationTargetException | IllegalAccessException | NoSuchMethodException exc) {}
        return null;
    }

    @Override
    public String asJson()
    {
        try
        {
            return (String)_phpVal.getClass().getDeclaredMethod("getString").invoke(_phpVal);
        }
        catch(InvocationTargetException | IllegalAccessException | NoSuchMethodException exc) {}
        return null;
    }

    protected Object _phpVal;
}