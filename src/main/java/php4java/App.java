package php4java;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Date;

import php4java.Impl.PhpInstance;
import php4java.Interfaces.IPhp;
import php4java.Interfaces.IPingService;

public class App
{
    public static void main(String[] argv)
    {
        System.out.println("Php4Java Server");
        System.out.println("Usage: java -jar {name} <rmi_local_port> <postfix> <registry_ping_service_name>");

        var port = Integer.decode(argv[0]);
        var postfix = argv[1];
        var pingServiceName = argv[2];

        //if (System.getSecurityManager() == null)
        //    System.setSecurityManager(new SecurityManager());

        IPingService pingService = null;

        try
        {
            var php = (IPhp)new PhpInstance();
            
            System.out.println("Getting registry...");
            
            // Connect to registry and check time
            var time = new Date().getTime();
            var registry = LocateRegistry.getRegistry("127.0.0.1", port);
            System.out.println("Passed: " + (new Date().getTime() - time) + " millis");

            // Bind our service to allow using PHP instance
            System.out.println("Connecing...");
            time = new Date().getTime();
            registry.bind("JPhpServer_" + postfix, php);
            System.out.println("Passed: " + (new Date().getTime() - time) + " millis");
            System.out.println("Connected!");

            // Get pinging service to check registry is up
            System.out.println("Connecing...");
            pingService = (IPingService)registry.lookup(pingServiceName);
            System.out.println("Passed: " + (new Date().getTime() - time) + " millis");
            System.out.println("Connected!");
        }
        catch(Exception exception)
        {
            System.out.println(exception.getMessage());
            System.exit(1);
        }
        System.out.println("Successfuly done!");

        while (true)
        {
            try
            {
                // Wait for some time
                System.out.println("Waiting...");
                Thread.sleep(5000);

                // Try to ping registry
                System.out.println("Making ping to registry (trying to get registry)...");
                System.out.println(pingService.ping());
                System.out.println("Done!");
            }
            catch(RemoteException exception)
            {
                System.out.println("Could not ping registry - exiting...");

                // Exit on disconnection
                System.exit(0);
            }
            catch(InterruptedException exception) {}
        }
    }
}
