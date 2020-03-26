package php4java.Interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface IPhpReturnValue extends Remote
{
    Long asLong() throws RemoteException;
    Double asDouble() throws RemoteException;
    Boolean asBoolean() throws RemoteException;
    String asString() throws RemoteException;
    IPhpReturnValue[] asArray() throws RemoteException;
    Map<String, IPhpReturnValue> asMap() throws RemoteException;
    String asJson() throws RemoteException;
}