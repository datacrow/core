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

package net.datacrow.core.plugin;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;

public class PluginClassLoader extends ClassLoader {

    private static Logger logger = Logger.getLogger(PluginClassLoader.class.getName());
    
    protected String path;
    
    protected final Map<String, Class<?>> cache = new HashMap<String, Class<?>>();
    protected final Collection<File> jarFiles = new ArrayList<File>();

    /**
     * Creates a new PluginClassLoader that searches in the directory path
     * passed as a parameter. The constructor automatically finds all JAR and ZIP
     * files in the path and first level of subdirectories. The JAR and ZIP files
     * are stored in a Vector for future searches.
     * @param path the path to the plugins directory.
     */
    public PluginClassLoader(String path) {
        init(path);
    }

    /** This version of the constructor is used when ImageJ is launched using Java WebStart. */
    public PluginClassLoader(String path, boolean callSuper) {
        super(Thread.currentThread().getContextClassLoader());
        init(path);
    }

    private void init(String path) {
    	
    	if (path == null)
    		return;
    	
        this.path = path;
        //find all JAR files on the path and subdirectories
        File f = new File(path);
        String[] list = f.list();
        if (list == null)
            return;
        for (int i = 0; i < list.length; i++) {
            f = new File(path, list[i]);
            if (f.isDirectory()) {
                String[] innerlist = f.list();
                if (innerlist == null)
                    continue;
                for (int j = 0; j < innerlist.length; j++) {
                    File g = new File(f, innerlist[j]);
                    if (g.isFile())
                        addJAR(g);
                }
            } else
                addJAR(f);
        }
    }

    private void addJAR(File f) {
        if (f.getName().endsWith(".jar") || f.getName().endsWith(".zip"))
            jarFiles.add(f);
    }

    @Override
    public URL getResource(String name) {
        // try system loader first
        URL res = super.getSystemResource(name);
        if (res != null)
            return res;

        File resFile;
        //try plugins directory
        try {
            resFile = new File(path, name);
            if (resFile.exists()) {
                res = makeURL(resFile);
                return res;
            }
        } catch (Exception e) {}

        //try subfolders
        resFile = new File(path);
        String[] list = resFile.list();
        if (list != null) {
            for (int i = 0; i < list.length; i++) {
                resFile = new File(path, list[i]);
                if (resFile.isDirectory()) {
                    try {
                        File f = new File(path + list[i], name);
                        if (f.exists()) {
                            res = makeURL(f);
                            return res;
                        }
                    } catch (Exception e) {
                    }

                }
            }
        }

        //otherwise look in JAR files
        byte[] resourceBytes;
        
        for (File jf : jarFiles) {
            try {
                resourceBytes = loadFromJar(jf.getPath(), name);
                if (resourceBytes != null) {
                    res = makeURL(name, jf);
                    return res;
                }
            } catch (MalformedURLException e) {
                logger.error(e, e);
            }
        }
        return null;
    }

    private URL makeURL(File fil) throws MalformedURLException {
        URL url = new URL("file", "", fil.toString());
        return url;
    }

    private URL makeURL(String name, File jar) throws MalformedURLException {
        StringBuffer filename = new StringBuffer("file:///");
        filename.append(jar.toString());
        filename.append("!/");
        filename.append(name);
        String sf = filename.toString();
        String sfu = sf.replace('\\', '/');
        URL url = new URL("jar", "", sfu);
        return url;
    }

    /**
     * Returns a resource from the path or JAR files as an InputStream
     * @param name a resource name.
     */
	@Override
    public InputStream getResourceAsStream(String name) {
        // try the system loader first
        InputStream is = super.getSystemResourceAsStream(name);
        if (is != null)
            return is;

        File resFile;

        // try plugins directory
        resFile = new File(path, name);
        try { // read the byte codes
            is = new FileInputStream(resFile);
        } catch (Exception e) {
        }
        if (is != null)
            return is;

        // try sub directories
        resFile = new File(path);
        String[] list = resFile.list();
        if (list != null) {
            for (int i = 0; i < list.length; i++) {
                resFile = new File(path, list[i]);
                if (resFile.isDirectory()) {
                    try {
                        File f = new File(path + list[i], name);
                        is = new FileInputStream(f);
                    } catch (Exception e) {
                        logger.debug("Error while processing the plugin file", e);
                    }
                    
                    if (is != null)
                        return is;
                }
            }
        }

        //look in JAR files
        for (File jf : jarFiles) {
            try {
            
                byte[] resourceBytes = loadFromJar(jf.getPath(), name);
                if (resourceBytes != null)
                    return new ByteArrayInputStream(resourceBytes);

            } catch (Exception e) {
                logger.error(e, e);
            }
        }
        return null;
    }

    /**
     * Returns a Class from the path or JAR files. Classes are automatically resolved.
     * @param className a class name without the .class extension.
     */
    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        return (loadClass(className, true));
    }

    /**
     * Returns a Class from the path or JAR files. Classes are resolved if resolveIt is true.
     * @param className a String class name without the .class extension.
     *        resolveIt a boolean (should almost always be true)
     */
    @Override
    public synchronized Class<?> loadClass(String className, boolean resolveIt)
            throws ClassNotFoundException {

        // try the local cache of classes
        Class<?> result = cache.get(className);
        if (result != null) {
            return result;
        }

        // try the system class loader
        try {
            result = super.findSystemClass(className);
            return result;
        } catch (Exception e) {
        }

        // Try to load it from plugins directory
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

    /**
     * This does the actual work of loading the bytes from the disk. Returns an
     * array of bytes that will be defined as a Class. This should be overloaded to have
     * the Class Loader look in more places.
     * @param name a class name without the .class extension.
     */

    protected byte[] loadClassBytes(String name) {
        byte[] classBytes = null;
        classBytes = loadIt(path, name);
        if (classBytes == null) {
            classBytes = loadFromSubdirectory(path, name);
            if (classBytes == null) {
                // Attempt to get the class data from the JAR files.
                for (File jf : jarFiles) {
                    try {
                        classBytes = loadClassFromJar(jf.getPath(), name);
                        if (classBytes != null) return classBytes;
                    } catch (Exception e) {}
                }
            }
        }
        return classBytes;
    }

    private byte[] loadIt(String path, String classname) {
        String filename = classname.replace('.', '/');
        filename = filename.substring(filename.indexOf('/') + 1);
        filename += ".class";
        File fullname = new File(path, filename);
        //ij.IJ.write("loadIt: " + fullname);
        try { // read the byte codes
            InputStream is = new FileInputStream(fullname);
            int bufsize = (int) fullname.length();
            byte buf[] = new byte[bufsize];
            is.read(buf, 0, bufsize);
            is.close();
            return buf;
        } catch (Exception e) {
            return null;
        }
    }

    private byte[] loadFromSubdirectory(String path, String name) {
        File f = new File(path);
        String[] list = f.list();
        if (list != null) {
            for (int i = 0; i < list.length; i++) {
                f = new File(path, list[i]);
                if (f.isDirectory()) {
                    byte[] buf = loadIt(path + list[i], name);
                    if (buf != null)
                        return buf;
                }
            }
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
