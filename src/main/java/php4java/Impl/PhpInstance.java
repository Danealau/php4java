package php4java.Impl;

import php4java.Interfaces.*;
import php4java.Native.Php;
import php4java.Native.Zval;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class PhpInstance extends UnicastRemoteObject implements IPhp
{
    private static final long serialVersionUID = 1L;

    public PhpInstance() throws RemoteException
    {
        super();

        var php = new Php();

        // Try to init new PHP interpreter
        lock();
        php.init();
        unlock();

        _php = php;
    }

    private IPhpReturnValue generateResult(Zval retVal) throws RemoteException
    {
        var arr = retVal.getArray();
        var iarr = new IPhpReturnValue[arr.length];
        //for (int i = 0; i < arr.length; ++i)
        //    iarr[i] = generateResult(arr[i]);

        //var map = retVal.getHash();
        var imap = new HashMap<String, IPhpReturnValue>();
        //for (var kv : map.entrySet())
        //    imap.put(kv.getKey(), generateResult(kv.getValue()));

        return new PhpReturnValue(retVal.getLong(), retVal.getDouble(), retVal.getBoolean(), retVal.getJson(), retVal.getJson(), iarr, imap);
    }

    @Override
    public IPhpReturnValue execString(String code) throws RemoteException
    {
        lock();
        var retVal = generateResult(_php.__eval("eval('" + code + "');"));
        unlock();

        return retVal;
    }

    public void lock()
    {
        _mutex.lock();
    }

    public void unlock()
    {
        _mutex.unlock();
    }

    protected ReentrantLock _mutex = new ReentrantLock();

    protected Php _php;
}