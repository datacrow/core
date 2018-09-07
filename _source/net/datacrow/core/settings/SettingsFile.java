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

package net.datacrow.core.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Reads and saves data to the Settings File
 * 
 * @author Robert Jan van der Waals
 */
public class SettingsFile {

    private transient static Logger logger = Logger.getLogger(SettingsFile.class.getName());
    
    /**
     * Save the settings file
     */
    public static void save(Settings settings) {
        Properties properties = new Properties();
        
        for (Setting setting : settings.getSettings()) {
            if (!setting.isTemporary())
                properties.setProperty(setting.getKey(), setting.getValueAsString());
        }

        try {
            FileOutputStream fos = new FileOutputStream(settings.getSettingsFile());
            properties.store(fos, "");
            fos.close();
        } catch (Exception e) {
            logger.error("Could not save settings to file " + settings.getSettingsFile(), e);
        } 
    }

    /**
     * Loads the settings file
     */
    public static void load(Settings settings) {
        Properties properties = new Properties();
        
        try {
            File file = settings.getSettingsFile();
            
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                properties.load(fis);
                fis.close();
            } else {
                return;
            }
        } catch (Exception ignore) {
            logger.debug("Error while loading settings from file " + settings.getSettingsFile());
        } 
        
        for (Setting setting : settings.getSettings()) {
            if (!setting.isTemporary() && properties.getProperty(setting.getKey()) != null) {
                String sValue = properties.getProperty(setting.getKey());
                settings.setString(setting.getKey(), sValue);
            } 
        }        
    }
}
