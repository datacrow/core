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

package net.datacrow.core.modules;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import net.datacrow.core.DcConfig;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.modules.xml.XmlModule;
import net.datacrow.core.modules.xml.XmlModuleWriter;
import net.datacrow.core.objects.DcImageIcon;
import net.datacrow.core.utilities.CoreUtilities;

import org.apache.log4j.Logger;

/**
 * A module jar is used to physically store the module. 
 * A module jar contains a small icon, a large icon and the XML module definition.
 * 
 * @author Robert Jan van der Waals
 */
public class ModuleJar {

    private transient static Logger logger = Logger.getLogger(ModuleJar.class.getName());
    
    private String filename;
    private XmlModule module;

    /**
     * Initializes the XML module.
     * @param filename
     */
    public ModuleJar(String filename) {
        this.filename = filename;
    }
    
    /**
     * Initializes the XML module.
     * @param module
     */
    public ModuleJar(XmlModule module) {
        this.module = module;
        this.filename = module.getJarFilename();
    }
    
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Retrieves the XML module definition.
     */
    public XmlModule getModule() {
        return module;
    }
    
    /**
     * Physically stores the module jar to disk.
     * @see #filename
     * @throws ModuleJarException
     */
    public void save() throws ModuleJarException {
        try {
            logger.debug("Saving Module JAR " + filename);
            
            byte[] icon16 = module.getIcon16();
            byte[] icon32 = module.getIcon32();
            
            icon16 = icon16 == null ? CoreUtilities.getBytes(IconLibrary._icoIcon16.getImage(), DcImageIcon._TYPE_PNG) : icon16;
            icon32 = icon32 == null ? CoreUtilities.getBytes(IconLibrary._icoIcon32.getImage(), DcImageIcon._TYPE_PNG) : icon32;

            if (	DcConfig.getInstance().getModuleDir().toLowerCase().equals("null") || 
            		CoreUtilities.isEmpty(DcConfig.getInstance().getModuleDir())) {
            	
            	throw new Exception("Module path has been set to empty, cannot save JAR " + filename);
            }
            
            net.datacrow.core.utilities.zip.ZipFile zf = new net.datacrow.core.utilities.zip.ZipFile(DcConfig.getInstance().getModuleDir(), filename);
            
            module.setIcon16Filename("icon16.png");
            module.setIcon32Filename("icon32.png");
            
            XmlModuleWriter writer = new XmlModuleWriter(module);
            byte[] xml  = writer.getXML();
            
            writer.close();
            
            zf.addEntry("module.xml", xml);
            zf.addEntry("icon16.png", icon16);
            zf.addEntry("icon32.png", icon32);
            zf.close();

        } catch (Exception exp) {
            throw new ModuleJarException(exp);
        }
    }
    
    /**
     * Deletes the jar file from the disk (cannot be undone).
     * @return
     */
    public boolean delete() {
        return new File(DcConfig.getInstance().getModuleDir(), filename).delete();
    }
    
    /**
     * Loads the module jar's content into memory. Loads the icons and the XML
     * definition.
     * @throws ModuleUpgradeException
     * @throws ModuleJarException
     * @throws InvalidModuleXmlException
     */
    public void load() throws ModuleUpgradeException, ModuleJarException, InvalidModuleXmlException {
        
        try {
            logger.debug("Loading module JAR " + filename);
            
            ZipFile zf = new ZipFile(DcConfig.getInstance().getModuleDir() + filename);

            Map<String, byte[]> content = new HashMap<String, byte[]>();
            Enumeration<? extends ZipEntry> list = zf.entries();
            while (list.hasMoreElements()) {
                ZipEntry ze = list.nextElement();

                BufferedInputStream bis = new BufferedInputStream(zf.getInputStream(ze));
                int size = (int) ze.getSize();
                byte[] bytes = new byte[size];
                bis.read(bytes);
                
                String filename = ze.getName();
                content.put(filename, bytes);
                
                bis.close();
            }        
            
            // first get the XML file
            for (String filename : content.keySet()) {
                if (filename.toLowerCase().endsWith("xml")) {
                    String UTF8 = new String(content.get(filename), "UTF-8");
                    module = new XmlModule(UTF8.getBytes("UTF-8"));
                }
            }
            
            byte[] icon16 = content.get(module.getIcon16Filename());
            byte[] icon32 = content.get(module.getIcon32Filename());
            
            module.setIcon16(icon16);
            module.setIcon32(icon32);
            
            zf.close();
        
        } catch (ZipException e) {
            throw new ModuleJarException(e, "An error occured while reading zipfile " + filename);
        } catch (NullPointerException e) {
            throw new ModuleJarException(e);
        } catch (FileNotFoundException e) {
            throw new ModuleJarException(e);
        } catch (IOException e) {
            throw new ModuleJarException(e);
        }
    }
}
