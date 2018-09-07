/******************************************************************************
 *                                     __                                     *
 *                              <-----/@@\----->                              *
 *                             <-< <  \\//  > >->                             *
 *                               <-<-\ __ /->->                               *
 *                               Data /  \ Crow                               *
 *                                   ^    ^                                   *
 *                              info@datacrow.net                             *
 *                                                                            *
 *                       This file is part of Data Crow.                      *
 *       Data Crow is free software; you can redistribute it and/or           *
 *        modify it under the terms of the GNU General Public                 *
 *       License as published by the Free Software Foundation; either         *
 *              version 3 of the License, or any later version.               *
 *                                                                            *
 *        Data Crow is distributed in the hope that it will be useful,        *
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *           MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.             *
 *           See the GNU General Public License for more details.             *
 *                                                                            *
 *        You should have received a copy of the GNU General Public           *
 *  License along with this program. If not, see http://www.gnu.org/licenses  *
 *                                                                            *
 ******************************************************************************/

package net.datacrow.core.services.plugin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.datacrow.core.DcConfig;
import net.datacrow.core.utilities.Directory;

import org.apache.log4j.Logger;

/**
 * The class loader used to find custom and standard online services.
 * @author Robert Jan van der Waals
 */
public class ServiceClassLoader extends ClassLoader {

    private static Logger logger = Logger.getLogger(ServiceClassLoader.class.getName());
    
    protected final Collection<File> jarFiles = new ArrayList<File>();
    protected final Map<String, Class<?>> cache = new HashMap<String, Class<?>>();

    /**
     * Creates a new PluginClassLoader that searches in the directory path
     * passed as a parameter. The constructor automatically finds all JAR and ZIP
     * files in the path and first level of sub directories. The JAR and ZIP files
     * are stored in a Vector for future searches.
     * @param path the path to the services directory.
     * 
     */
    public ServiceClassLoader(String path) {
        init(path);
        
        ZipFile zf = null;
        Enumeration<? extends ZipEntry> entries;
        ZipEntry entry;
        String name;
        for (File jf : jarFiles) {
            try {
                
                zf = new ZipFile(jf);
                entries = zf.entries();
                
                while (entries.hasMoreElements()) {
                    entry = entries.nextElement();
                    name = entry.getName();
                    
                    if (name.endsWith(".class")) {
                        name = name.replaceAll("/", ".");
                        name = name.substring(0, name.lastIndexOf("."));
                        loadClass(name);
                    }
                }
            } catch (Exception e) {
                logger.error(e, e);
            } finally {
                try {
                    if (zf != null) zf.close();
                } catch (Exception e) {
                    logger.debug("Could not close zip file", e);
                }
            }
        }
    }
    
    public Collection<Class<?>> getClasses() {
        return cache.values();
    }

    /** This version of the constructor is used when ImageJ is launched using Java WebStart. */
    public ServiceClassLoader(String path, boolean callSuper) {
        super(Thread.currentThread().getContextClassLoader());
        init(path);
    }

    private void init(String path) {
        Directory dir = new Directory(DcConfig.getInstance().getServicesDir(), true, new String[] {"jar"});
        for (String filename :  dir.read())
            jarFiles.add(new File(filename));
    }

    /**
     * Returns a Class from the path or JAR files. Classes are automatically resolved.
     * @param className a class name without the .class extension.
     */
    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        return loadClass(className, true);
    }

    /**
     * Returns a Class from the path or JAR files. Classes are resolved if resolveIt is true.
     * @param className a String class name without the .class extension.
     *        resolveIt a boolean (should almost always be true)
     */
    @Override
    public synchronized Class<?> loadClass(String className, boolean resolveIt) throws ClassNotFoundException {

        // try the local cache of classes
        Class<?> result = cache.get(className);
        if (result != null) return result;

        
        // try the system class loader
        try {
            result = super.findSystemClass(className);
            return result;
        } catch (Exception e) {
        }
        
        // Try to load it from one of the jar files
        byte[] classBytes = loadClassBytes(className);
        if (classBytes == null) {
            result = getParent().loadClass(className);
            if (result != null) 
                return result;
        }

        if (classBytes == null)
            throw new ClassNotFoundException(className);

        // Define it (parse the class file)
        result = defineClass(className, classBytes, 0, classBytes.length);
        if (result == null)
            throw new ClassFormatError();

        // Resolve if necessary
        if (resolveIt) resolveClass(result);

        cache.put(className, result);
        return result;
    }

    protected byte[] loadClassBytes(String name) {
        for (File jf : jarFiles) {
            try {
                byte[] classBytes = loadClassFromJar(jf.getPath(), name);
                if (classBytes != null) return classBytes;
            } catch (Exception e) {}
        }
        return null;
    }

    private byte[] loadClassFromJar(String jar, String className) {
        String name = className.replace('.', '/');
        name += ".class";
        return loadFromJar(jar, name);
    }

    // Load class or resource from a JAR file
    private byte[] loadFromJar(String jar, String name) {
        BufferedInputStream bis = null;
        ZipFile zf = null;
        try {
            zf = new ZipFile(jar);
            Enumeration<? extends ZipEntry> entries = zf.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().equals(name)) {
                    bis = new BufferedInputStream(zf.getInputStream(entry));
                    int size = (int) entry.getSize();
                    byte[] data = new byte[size];
                    int b = 0, eofFlag = 0;
                    while ((size - b) > 0) {
                        eofFlag = bis.read(data, b, size - b);
                        if (eofFlag == -1)
                            break;
                        b += eofFlag;
                    }
                    return data;
                }
            }
        } catch (Exception e) {
            logger.debug(e, e);
        } finally {
            try {
                if (zf != null)
                    zf.close();
                if (bis != null)
                    bis.close();
            } catch (IOException e) {
                logger.error(e, e);
            }
        }
        return null;
    }
}
