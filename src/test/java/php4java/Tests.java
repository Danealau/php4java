package php4java;

import org.junit.Test;

import php4java.Interfaces.IPhp;
import php4java.Impl.*;
import php4java.Php4JavaException;

public class Tests
{
    @Test
    public void testSimpleValueReturn()
    {
        System.out.printf("Creating PHP instance...\n");

        IPhp instance = null;
        try
        {
            instance = PhpInstanceFactory.CreateInstance();
        }
        catch (Php4JavaException exc)
        {
            System.out.println(exc);
        }

        final String command = "1;";
        System.out.println("Running '" + command + "' in PHP...\n");

        try
        {
            var result = instance.execString(command);

            System.out.printf("As long: %d\n\n", result.asLong());
            System.out.printf("As double: %f\n\n", result.asDouble());
            System.out.printf("As bool: %s\n\n", result.asBoolean());
            System.out.printf("As string: %s\n\n", result.asString());
            System.out.printf("As json: %s\n\n", result.asJson());
            var array = result.asArray();
            System.out.printf("As array: length=%d\n\n", array.length);
        }
        catch (Php4JavaException exception)
        {
            System.err.println(exception);
        }
    }

    @Test
    public void testReturnSituation()
    {
        System.out.printf("Creating PHP instance...\n");
        IPhp instance = null;
        try
        {
            instance = PhpInstanceFactory.CreateInstance();
        }
        catch (Php4JavaException exc)
        {
            System.out.println(exc);
        }

        final String command = "$cinosad = (14324); return $cinosad;";
        System.out.println("Running '" + command + "' in PHP...\n");

        try
        {
            var result = instance.execString(command);

            System.out.printf("As long: %d\n\n", result.asLong());
            System.out.printf("As double: %f\n\n", result.asDouble());
            System.out.printf("As bool: %s\n\n", result.asBoolean());
            System.out.printf("As string: %s\n\n", result.asString());
            System.out.printf("As json: %s\n\n", result.asJson());
            var array = result.asArray();
            System.out.printf("As array: length=%d\n\n", array.length);
        }
        catch (Php4JavaException exception)
        {
            System.err.println(exception);
        }
    }

    @Test
    public void testMultithreading()
    {
        for (int i = 0; i < 2; ++i)
        {
            IPhp instance = null;
            try
            {
                instance = PhpInstanceFactory.CreateInstance();
            }
            catch (Php4JavaException exc)
            {
                System.out.println(exc);
            }

            try
            {
                var result = instance.execString("return \\'TEST_STRING\\';");
                System.out.println("As Long: " + result != null ? result.asLong() : "NULL");
                System.out.println("As Double: " + result != null ? result.asDouble() : "NULL");
                System.out.println("As Boolean: " + result != null ? result.asBoolean() : "NULL");
                System.out.println("As String: " + result != null ? result.asString() : "NULL");
                //System.out.println(result != null ? result.asHash() : "NULL");
                //System.out.println(result != null ? result.asJson() : "NULL");
            }
            catch(Php4JavaException exception)
            {
                System.out.println("CODESTRING");
                System.out.println(exception);
            }
        }
    }
}