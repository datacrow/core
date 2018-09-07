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

package net.datacrow.core.enhancers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import net.datacrow.core.DcConfig;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.server.Connector;

import org.apache.log4j.Logger;

/**
 * Collection of value enhancers. Value enhancers should be registered here to become
 * active. 
 * 
 * @see IValueEnhancer
 * @author Robert Jan van der Waals
 */
public class ValueEnhancers {

    private transient static Logger logger = Logger.getLogger(ValueEnhancers.class.getName());
    
    public static final int _AUTOINCREMENT = 0;
    public static final int _TITLEREWRITERS = 1;
    public static final int _ASSOCIATENAMEREWRITERS = 2;
    
    private static final Map<DcField, Collection<IValueEnhancer>> enhancers = 
        new HashMap<DcField, Collection<IValueEnhancer>>();
    
    /**
     * Loads all value enhancers.
     */
    public static void initialize() {
        if (DcConfig.getInstance().getOperatingMode() == DcConfig._OPERATING_MODE_CLIENT) {

            Connector connector = DcConfig.getInstance().getConnector();
            Map<DcField, Collection<IValueEnhancer>> serverEnhancers = 
                    connector.getValueEnhancers();
            
            if (serverEnhancers == null) {
                logger.error("Failed to load the value enhancers from the server");
            } else {
                for (DcField fld : serverEnhancers.keySet()) {
                    enhancers.put(DcModules.get(fld.getModule()).getField(fld.getIndex()), serverEnhancers.get(fld));
                }
            }
            
        } else {
            load(_AUTOINCREMENT);
            load(_TITLEREWRITERS);
            load(_ASSOCIATENAMEREWRITERS);
        }
    }
    
    
    /**
     * Register a value enhancer for a specific field.
     * @param field The field which will be enhanced.
     * @param valueEnhancer The value enhancer.
     */
    public static void registerEnhancer(DcField field, IValueEnhancer valueEnhancer) {
        Collection<IValueEnhancer> c = enhancers.get(field);
        c = c == null ? new ArrayList<IValueEnhancer>() : c;
        
        Collection<IValueEnhancer> newEnhancers = new ArrayList<IValueEnhancer>();
        for (IValueEnhancer current : c) {
            valueEnhancer.getIndex();
            if (current.getIndex() != valueEnhancer.getIndex())
                newEnhancers.add(current);
        }
        
        newEnhancers.add(valueEnhancer);
        enhancers.put(field, newEnhancers);
        
        field.addValueEnhancer(valueEnhancer);
    }
    
    public static Map<DcField, Collection<IValueEnhancer>> getEnhancers() {
        return enhancers;
    }

    /**
     * Retrieves all value enhancers for the specified field.
     * @param field
     * @return Empty or filled collection of enhancers.
     */
    public static Collection<IValueEnhancer> getEnhancers(DcField field) {
        Collection<IValueEnhancer> c = enhancers.get(field);
        return c == null ? new ArrayList<IValueEnhancer>() : c;
    }
    
    /**
     * Retrieves all value enhancers for the specified module.
     * @param module The module index.
     * @param idx The enhancer index/type.
     * @return Empty or filled collection of enhancers.
     */
    public static Collection<? extends IValueEnhancer> getEnhancers(int module, int idx) {
        Collection<IValueEnhancer> c = new ArrayList<IValueEnhancer>();
        for (DcField field : enhancers.keySet()) {
            if (field.getModule() == module) {
                for (IValueEnhancer enhancer : enhancers.get(field)) {
                    if (idx == enhancer.getIndex()) 
                        c.add(enhancer);
                }
            }
        }
        return c;
    }
    
    private static DcField getField(String s) {
        try {
            int mod = Integer.parseInt(s.substring(0, s.indexOf("/&/")));
            String columnName = s.substring(s.indexOf("/&/") + 3, s.length());
            
            DcModule module = DcModules.get(mod);
            for (DcField field : module.getFields()) {
                if (field.getDatabaseFieldName().equalsIgnoreCase(columnName))
                    return field;
            }
        } catch (Exception exp) {
            logger.error("Could not find valid field information in " + s);
        }
        return null;
    }
    
    /**
     * Saves the enhancer settings.
     */
    public static void save() {
        Properties incrementers = new Properties();
        Properties titleRewriters = new Properties();
        Properties associateNameRewriters = new Properties();
        
        for (DcField field : enhancers.keySet()) {
            for (IValueEnhancer enhancer : enhancers.get(field)) {
                String key = field.getModule() + "/&/" + field.getDatabaseFieldName();
                if (enhancer.getIndex() == _AUTOINCREMENT) 
                    incrementers.put(key, enhancer.toSaveString());
                else if (enhancer.getIndex() == _ASSOCIATENAMEREWRITERS) 
                    associateNameRewriters.put(key, enhancer.toSaveString());
                else
                    titleRewriters.put(key, enhancer.toSaveString());
            }
        }
        
        try {
            store(incrementers, DcConfig.getInstance().getApplicationSettingsDir() + "/enhancers_autoincrement.properties");
            store(titleRewriters, DcConfig.getInstance().getApplicationSettingsDir() + "/enhancers_titlerewriters.properties");
            store(associateNameRewriters, DcConfig.getInstance().getApplicationSettingsDir() + "/enhancers_associatenamerewriters.properties");
        } catch (Exception exp) {
            logger.error("Error while saving enhancer settings to file", exp);    
        }         
    }
    
    private static void store(Properties properties, String filename) throws Exception{
        FileOutputStream fos = new FileOutputStream(filename);
        properties.store(fos, "");
        fos.close();
    }
    
    private static void load(int idx) {
        Properties properties = new Properties();
        
        String filename = idx == _AUTOINCREMENT ? "enhancers_autoincrement.properties" :
                          idx == _TITLEREWRITERS ? "enhancers_titlerewriters.properties" : 
                          "enhancers_associatenamerewriters.properties";
        filename = DcConfig.getInstance().getApplicationSettingsDir() + filename;
        
        if (new File(filename).exists()) {
            try {
                FileInputStream fis = new FileInputStream(filename);
                properties.load(fis);
                fis.close();
                
                for (Object o : properties.keySet()) {
                    String key = (String) o;
                    DcField field = getField(key);
                    
                    if (field != null) {
                        String value = properties.getProperty(key);
                        IValueEnhancer enhancer;
                        if (idx == _AUTOINCREMENT)
                            enhancer = new AutoIncrementer(field.getIndex()); 
                        else if (idx == _TITLEREWRITERS)
                            enhancer = new TitleRewriter();
                        else 
                            enhancer = new AssociateNameRewriter();
                        
                        enhancer.parse(value);
                        
                        registerEnhancer(field, enhancer);
                    } else {
                        logger.warn("The value enhancer is invalid, the value enhancer will be deleted.");
                        
                        properties.remove(key);
                        FileOutputStream fos = new FileOutputStream(filename);
                        properties.store(fos, "");
                        fos.close();
                    } 
                }
            } catch (Exception e) {
                logger.error("Error while loading enhancer settings from " + filename, e);
            }
        }
    }    
}
