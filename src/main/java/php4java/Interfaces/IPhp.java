package php4java.Interfaces;

public interface IPhp
{
    IPhpVal execString(String code) throws php4java.Php4JavaException;
}