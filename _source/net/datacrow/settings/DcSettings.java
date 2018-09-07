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

package net.datacrow.settings;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcLookAndFeel;
import net.datacrow.core.settings.Setting;
import net.datacrow.settings.definitions.IDefinitions;

/**
 * Wrapper for the application settings.
 * 
 * @author Robert Jan van der Waals
 */
public class DcSettings {

    private static DcApplicationSettings applicationSettings;

    public DcSettings() {}
    
    public static void initialize() {
        applicationSettings = new DcApplicationSettings();
    }
    
    /**
     * Saves all settings to file.
     */
    public static void save() {
        applicationSettings.save();
        for (DcModule module : DcModules.getAllModules())
            module.getSettings().save();
    }

    public static Settings getSettings() {
    	return applicationSettings;
    }
    
    public static Setting getSetting(String key) {
        return applicationSettings.getSetting(key);
    }
    
    public static void set(String key, Object value) {
        applicationSettings.set(key, value);
    }    

    public static IDefinitions getDefinitions(String key) {
        return applicationSettings.getDefinitions(key);
    }

    public static String[] getStringArray(String key) {
        return applicationSettings.getStringArray(key);
    }

    public static int[] getIntArray(String key) {
        return applicationSettings.getIntArray(key);
    }

    public static Font getFont(String key) {
        return applicationSettings.getFont(key);
    }

    public static DcLookAndFeel getLookAndFeel(String key) {
        return applicationSettings.getLookAndFeel(key);
    }
    
    public static long getLong(String key) {
        return applicationSettings.getLong(key);
    }    
    
    public static int getInt(String key) {
        return applicationSettings.getInt(key);
    }

    public static boolean getBoolean(String key) {
        return applicationSettings.getBoolean(key);
    }

    public static Color getColor(String key) {
        return applicationSettings.getColor(key);
    }

    public static Dimension getDimension(String key) {
        return applicationSettings.getDimension(key);
    }

    public static String getString(String key) {
        return applicationSettings.getString(key);
    }    
    
    public static void setStringAsValue(String key, String s) {
        applicationSettings.set(key, s);
    }     
}
