package php4java.Impl;

import php4java.Interfaces.*;
import java.lang.reflect.*;
import java.util.concurrent.locks.ReentrantLock;

public class PhpInstance implements IPhp
{
    public PhpInstance(Object obj) throws Exception
    {
        if (obj.getClass().getName() != "php4java.Native.Php")
            throw new Exception();
        
        // Try to init new PHP interpreter
        Lock();
        obj.getClass().getDeclaredMethod("init").invoke(obj);
        Unlock();
        _php = obj;
    }

    @Override
    public IPhpVal execString(String code) throws php4java.Php4JavaException
    {
        Object result = null;
        try
        {
            Lock();
            result = _php.getClass().getDeclaredMethod("__eval", new Class[]{String.class}).invoke(_php, "eval('" + code + "');");
            Unlock();
        }
        catch(InvocationTargetException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException exc)
        {
            throw new php4java.Php4JavaException(exc.getCause().toString());
        }

        try
        {
            return new PhpVal(this, result);
        }
        catch(IllegalAccessException exc)
        {
            throw new php4java.Php4JavaException(exc.toString());
        }
    }

    public void Lock()
    {
        _mutex.lock();
    }

    public void Unlock()
    {
        _mutex.unlock();
    }

    protected ReentrantLock _mutex = new ReentrantLock();

    protected Object _php;
}