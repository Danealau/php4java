package php4java.Interfaces;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

public class PhpReturnValue extends UnicastRemoteObject implements IPhpReturnValue
{
    private static final long serialVersionUID = 1L;
    private Long _asLong;
    private Double _asDouble;
    private Boolean _asBoolean;
    private String _asString;
    private String _asJson;
    private IPhpReturnValue[] _asArr;
    private Map<String, IPhpReturnValue> _asMap;
    
    public PhpReturnValue(
        Long a,
        Double b,
        Boolean c,
        String d,
        String json,
        IPhpReturnValue[] arr,
        Map<String, IPhpReturnValue> map) throws RemoteException
    {
        _asLong = a;
        _asDouble = b;
        _asBoolean = c;
        _asString = d;
        _asJson = json;
        _asArr = arr;
        _asMap = map;
    }

    @Override
    public Long asLong() { return _asLong; }

    @Override
    public Double asDouble() { return _asDouble; }

    @Override
    public Boolean asBoolean() { return _asBoolean; }

    @Override
    public String asString() { return _asString; }

    @Override
    public IPhpReturnValue[] asArray() { return _asArr; }

    @Override
    public Map<String, IPhpReturnValue> asMap() { return _asMap; }

    @Override
    public String asJson() { return _asJson; }
}