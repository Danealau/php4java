package php4java.Impl;

import java.util.Map;
import php4java.Interfaces.*;
import java.lang.reflect.*;
import php4java.Native.*;

public class PhpVal implements IPhpVal
{
    public PhpVal(PhpInstance parent, Object obj) throws IllegalAccessException
    {
        if (obj.getClass().getName() != "php4java.Native.Zval")
            throw new IllegalAccessException();
        _phpVal = obj;
        _parent = parent;
    }

    @Override
    public Long asLong()
    {
        try
        {
            _parent.Lock();
            var lg = (long)_phpVal.getClass().getDeclaredMethod("getLong").invoke(_phpVal);
            _parent.Unlock();
            return lg;
        }
        catch(InvocationTargetException | IllegalAccessException | NoSuchMethodException exc) {}
        return null;
    }

    @Override
    public Double asDouble()
    {
        try
        {
            _parent.Lock();
            var dbl = (double)_phpVal.getClass().getDeclaredMethod("getDouble").invoke(_phpVal);
            _parent.Unlock();
            return dbl;
        }
        catch(InvocationTargetException | IllegalAccessException | NoSuchMethodException exc) {}
        return null;
    }

    @Override
    public Boolean asBoolean()
    {
        try
        {
            _parent.Lock();
            var bl = (boolean)_phpVal.getClass().getDeclaredMethod("getBoolean").invoke(_phpVal);
            _parent.Unlock();
            return bl;
        }
        catch(InvocationTargetException | IllegalAccessException | NoSuchMethodException exc) {}
        return null;
    }

    @Override
    public String asString()
    {
        try
        {
            _parent.Lock();
            var str = (String)_phpVal.getClass().getDeclaredMethod("getString").invoke(_phpVal);
            _parent.Unlock();
            return str;
        }
        catch(InvocationTargetException | IllegalAccessException | NoSuchMethodException exc) {}
        return null;
    }

    @Override
    public IPhpVal[] asArray()
    {
        try
        {
            _parent.Lock();
            var res = (Object[])_phpVal.getClass().getDeclaredMethod("getArray").invoke(_phpVal);
            _parent.Unlock();

            var result = new IPhpVal[res.length];
            for (int i = 0; i < res.length; ++i)
                result[i] = new PhpVal(_parent, res[i]);
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
            _parent.Lock();
            var map = (Map<String, Zval>)_phpVal.getClass().getDeclaredMethod("getHash").invoke(_phpVal);
            _parent.Unlock();

            // FIXME: C-code fails if we use this map
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
            _parent.Lock();
            var json = (String)_phpVal.getClass().getDeclaredMethod("getString").invoke(_phpVal);
            _parent.Unlock();
            return json;
        }
        catch(InvocationTargetException | IllegalAccessException | NoSuchMethodException exc) {}
        return null;
    }

    protected Object _phpVal;
    protected PhpInstance _parent;
}