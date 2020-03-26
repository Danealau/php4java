package php4java.Interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IPingService extends Remote
{
    int ping() throws RemoteException;
}