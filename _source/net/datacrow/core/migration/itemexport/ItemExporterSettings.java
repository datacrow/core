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

package net.datacrow.core.migration.itemexport;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ItemExporterSettings {

    private static Logger logger = Logger.getLogger(ItemExporterSettings.class.getName());
    
//    public static final String _NAME = "report_name";
    public static final String _COPY_IMAGES = "copy_images";
    public static final String _SCALE_IMAGES = "scale_images";
    public static final String _IMAGE_WIDTH = "image_width";
    public static final String _IMAGE_HEIGHT = "image_height";
    public static final String _MAX_TEXT_LENGTH = "max_text_length";
    public static final String _ALLOWRELATIVEIMAGEPATHS = "allowrelativeimagespaths";
    
    private final String filename;
    private final Properties properties;
    
    public ItemExporterSettings() {
        filename = null;
        properties = new Properties();
    }    
    
    public void set(String key, Object value) {
        properties.setProperty(key, "" + value);
    }
    
    public String get(String key) {
        return (String) properties.get(key);
    }
    
    public boolean getBoolean(String key) {
        String value = get(key);
        return Boolean.valueOf(value);
    }
    
    public int getInt(String key) {
        String value = get(key);
        int i = 0;
        try {
            i = Integer.parseInt(value);
        } catch (NumberFormatException nfe) {}
        return i;
    }
    
    public void save() {
        try {
            File file = new File(filename);
            FileOutputStream fos = new FileOutputStream(file);
            properties.store(fos, "");
            fos.close();
        } catch (Exception exp) {
            logger.error("Error while saving properties to file " + filename, exp);
        }
    }
}
