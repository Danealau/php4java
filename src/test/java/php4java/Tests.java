package php4java;

import org.junit.Test;

public class Tests
{
    @Test public void testSomeLibraryMethod()
    {
        System.out.printf("Initializing PHP...\n");
        Php.init();

        System.out.println("Requiring test.php...\n");
        Zval result = Php.execString("new RuntimeException(); ");

        System.out.printf("As long: %d\n\n", result.getLong());

        System.out.printf("As double: %f\n\n", result.getDouble());

        System.out.printf("As bool: %s\n\n", result.getBoolean());

        System.out.printf("As string: %s\n\n", result.getString());

        System.out.printf("As json: %s\n\n", result.getJson());

        Zval[] array = result.getArray();
        System.out.printf("As array: length=%d\n\n", array.length);
        for (Zval e : array) {
            e.dispose();
        }

        // TODO Fix leak
        //Map<String, Zval> hash = result.getHash();
        //System.out.printf("As hash: %s\n\n", hash);
        //for (Zval e : hash.values()) {
        //    e.dispose();
        //}
        //Zval path = hash.get("PATH");
        //if (path != null) {
        //    System.out.printf("key[PATH] = %s\n\n", path.getString());
        //    path.dispose();
        //}

        System.out.println("Shutting down PHP...\n");
        result.dispose();
        Php.shutdown();
        System.out.println("Fin");
    }
}