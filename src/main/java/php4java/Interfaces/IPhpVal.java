package php4java.Interfaces;

import java.util.Map;

public interface IPhpVal
{
    Long asLong();
    Double asDouble();
    Boolean asBoolean();
    String asString();
    IPhpVal[] asArray();
    Map<String, IPhpVal> asHash();
    String asJson();
}