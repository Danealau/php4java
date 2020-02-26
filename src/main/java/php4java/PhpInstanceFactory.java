package php4java;

import java.io.*;
import java.io.File;
import java.lang.reflect.*;
import java.net.*;
import java.nio.file.Paths;

public class PhpInstanceFactory
{
    protected static Integer _internalInstanceCounter = 0;

    protected static String _tempFolderNamePattern = "tmp_php_instance_";

    protected static void _createFolderForPhpInstance()
    {
        try
        {
            Runtime.getRuntime().exec(new String[] { "sh", "-c", "rsync -r ./libphp4java.dylib ./" + _tempFolderNamePattern + _internalInstanceCounter + "/ --exclude=\\'*.jar\\'" }).waitFor();
        }
        catch (IOException | InterruptedException exc)
        {
            System.out.println(exc);
        }
    }

    public static synchronized php4java.Interfaces.IPhp CreateInstance() throws Php4JavaException
    {
        // Remove all other temp files that were created before
        try
        {
            Runtime.getRuntime().exec(new String[] { "sh", "-c", "rm -rf tmp_php_instance*" }).waitFor();
        }
        catch (IOException | InterruptedException exc)
        {
            throw new Php4JavaException(exc);
        }

        // Create new PHP instance folder
        _createFolderForPhpInstance();

        try
        {
            // Take current class loader
            Class<?> phpClass = php4java.Native.Php.class;
            URL[] urls = { phpClass.getProtectionDomain().getCodeSource().getLocation() };
            ClassLoader delegateParent = phpClass.getClassLoader().getParent();

            // Create own classloader for new PHP instance
            try(URLClassLoader cl = new URLClassLoader(urls, delegateParent))
            {
                // Reload class with new class loader
                Class<?> reloaded = cl.loadClass(phpClass.getName());
                var b = reloaded.getDeclaredConstructor().newInstance();

                try
                {
                    Runtime.getRuntime().exec(new String[] { "sh", "-c", ("install_name_tool -id " + _internalInstanceCounter + " " + _tempFolderNamePattern + _internalInstanceCounter + "/libphp4java.dylib") }).waitFor();
                }
                catch (IOException | InterruptedException exc)
                {
                    throw new Php4JavaException(exc);
                }

                // Load library with php4java
                b.getClass().getDeclaredMethod("preloadLibrary", new Class[]{String.class}).invoke(b, Paths.get(_tempFolderNamePattern + _internalInstanceCounter + "/libphp4java.dylib").toAbsolutePath().toString());

                ++_internalInstanceCounter;

                return new php4java.Impl.PhpInstance(b);
            }
        }
        catch(InvocationTargetException exc)
        {
            throw new Php4JavaException(exc);
        }
        catch(Exception exc)
        {
            throw new Php4JavaException(exc);
        }
    }
    
    protected static void _removeFiles(String folderPath, String regex)
    {
        final File[] files = new File(folderPath).listFiles(
            new FilenameFilter()
            {
                @Override
                public boolean accept(final File dir, final String name)
                {
                    return name.matches(regex);
                }
            });

        for (final File file : files)
        {
            if (!file.delete())
            {
                System.err.println("Can't remove " + file.getAbsolutePath());
            }
        }
    }
}