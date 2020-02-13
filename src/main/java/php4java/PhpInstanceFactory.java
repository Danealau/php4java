package php4java;

import java.io.*;
import java.io.File;
import java.lang.reflect.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class PhpInstanceFactory
{
    protected static Integer _internalInstanceCounter = 0;

    public static synchronized php4java.Interfaces.IPhp CreateInstance()
    {
        // Remove all other temp files that were created before
        _removeFiles(".", "libphp4java_tmp_.*");
        _removeFiles(".", "libphp_tmp_.*");

        try
        {
            // Take current class loader
            Class<?> phpClass = php4java.Native.Php.class;
            URL[] urls = { phpClass.getProtectionDomain().getCodeSource().getLocation() };
            ClassLoader delegateParent = phpClass.getClassLoader().getParent();

            // Create own classloader for new PHP instance
            try(URLClassLoader cl = new URLClassLoader(urls, delegateParent))
            {
                // Prepare temp library files with PHP-interpreter
                Files.copy(new File("libphp4java.dylib").toPath(), new File("libphp4java_tmp_" + _internalInstanceCounter + ".dylib").toPath(), StandardCopyOption.REPLACE_EXISTING);
                Files.copy(new File("libphp.dylib").toPath(), new File("libphp_tmp_" + _internalInstanceCounter + ".dylib").toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Change dependencies in php4java wrapping library to point to the correct library
                Runtime.getRuntime().exec("install_name_tool -change libphp.dylib @loader_path/libphp_tmp_" + _internalInstanceCounter + ".dylib libphp4java_tmp_" + _internalInstanceCounter + ".dylib").waitFor();
                Runtime.getRuntime().exec("install_name_tool -id libphp4java_tmp_" + _internalInstanceCounter + ".dylib libphp4java_tmp_" + _internalInstanceCounter + ".dylib").waitFor();
                Runtime.getRuntime().exec("install_name_tool -id libphp_tmp_" + _internalInstanceCounter + ".dylib libphp_tmp_" + _internalInstanceCounter + ".dylib").waitFor();

                // Reload class with new class loader
                Class<?> reloaded = cl.loadClass(phpClass.getName());


                var b = reloaded.newInstance();

                // Load library with php4java
                b.getClass().getDeclaredMethod("preloadLibrary", new Class[]{String.class}).invoke(b, "php4java_tmp_" + _internalInstanceCounter);

                ++_internalInstanceCounter;

                return new php4java.Impl.PhpInstance(b);
            }
        }
        catch(InvocationTargetException aaa)
        {
            System.out.println("Excaaa: ");
            System.out.println(aaa.getCause());
        }
        catch(Exception ccc)
        {
            System.out.println("Exc: ");
            ccc.printStackTrace();
        }
        return null;
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