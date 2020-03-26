package php4java.Interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IPhp extends Remote
{
    IPhpReturnValue execString(String code) throws RemoteException;
}