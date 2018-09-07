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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

import net.datacrow.core.objects.DcLookAndFeel;
import net.datacrow.settings.definitions.IDefinitions;

/**
 * Contains all Settings Groups and there settings. 
 * The Settings class is the only class which needs to be referenced
 * to add groups, add settings, get settings and get values of settings.
 * 
 * @author Robert Jan van der Waals
 */
public class Settings implements Serializable {

	private static final long serialVersionUID = -5005244859315267321L;

	private File settingsFile = new File("unnamed.ini");
    private LinkedHashMap<String, SettingsGroup> groups = new LinkedHashMap<String, SettingsGroup>();
    
    /**
     * Adds a group to the hashtable. A group may contain sub-groups.
     * Only the parent, to which a sub-group belongs, should be added.
     * 
     * @param key unique identifier for this group
     * @param group a top-level group (parent)
     */ 
    public void addGroup(String key, SettingsGroup group) {
        groups.put(key, group);
    }

    /**
     * Specifies the location and name of the settings file
     * 
     * @param file the settings file
     */
    public void setSettingsFile(File file) {
        settingsFile = file;
    }
    
    public File getSettingsFile() {
        return settingsFile;   
    }

    /**
     * Retrieves a settings group with the key
     */
    private SettingsGroup getSettingGroup(String key) {
        Collection<SettingsGroup> groups = getGroups();
        for (SettingsGroup group : groups) {
            if (group.getKey().equals(key))
                return group; 
        }
        return null;
    }

    /**
     * Adds a setting to a specified group. A group can be either
     * a parent or a child.
     * 
     * @param key the key of the group
     * @param setting the setting to be added to the specified group
     */
    public void addSetting(String key, Setting setting) {
        SettingsGroup stGroup = getSettingGroup(key);
        stGroup.add(setting);
    }

    public boolean isSettingKeyValid(String key) {
        boolean bValid = getSetting(key) == null ? false : true;
        return bValid;
    }

    /**
     * Retrieves a setting
     */
    public Setting getSetting(String key) {
        for (SettingsGroup group : groups.values()) {
            Setting setting = group.getSetting(key);
            if (setting != null) return setting;
        }
        return null;
    }

    /**
     * Retrieves a value of a settings
     */
    public Object getValue(String key) {
        Setting setting = getSetting(key);
        return setting != null ? setting.getValue() : null;
    }

    /**
     * Returns the value of the setting as a String array
     */
    public String[] getStringArray(String key) {
        return (String[]) getValue(key);
    }
    
    public int[] getIntArray(String key) {
        return (int[]) getValue(key);
    }

    public IDefinitions getDefinitions(String key) {
        return (IDefinitions) getValue(key);
    }      
    
    public Font getFont(String key) {
        return (Font) getValue(key);
    }    

    public DcLookAndFeel getLookAndFeel(String key) {
        return (DcLookAndFeel) getValue(key);
    } 
    
    /**
     * Returns the value of the setting as an integer
     */
    public int getInt(String key) {
        Object o = getValue(key);
        return o != null ? ((Integer) o).intValue() : -1;
    }

    /**
     * Returns the value of the setting as a boolean
     */
    public boolean getBoolean(String key) {
        Object o = getValue(key);
        return o == null ? false : ((Boolean) o).booleanValue();
    }

    /**
     * Returns the value of the setting as a color object
     */
    public Color getColor(String key) {
        return (Color) getValue(key);
    }
    
    public Dimension getDimension(String key) {
        return (Dimension) getValue(key);
    }

    public String getString(String key) {
        Object o = getValue(key);
        return o != null ? o.toString() : null;
    }

    /**
     * Sets the value of a setting
     */
    public void setValue(String key, Object value) {
        Setting setting = getSetting(key);
        if (setting != null) {
            setting.setValue(value);
        }
    }

    /**
     * Sets a string as a value for the setting (by parsing the string)
     */
    public void setString(String key, String s) {
        Setting setting = getSetting(key);
        if (setting != null)
            setting.setStringAsValue(s);
    }

    /**
     * Retrieves all the top level groups
     */    
    public LinkedHashMap<String, SettingsGroup> getSettingsGroups() {
        return groups;   
    }

    /**
     * Retrieves all settings groups without an hierarchy
     */
    public Collection<SettingsGroup> getGroups() {
        Collection<SettingsGroup> c = new ArrayList<SettingsGroup>();
        c.addAll(groups.values());

        for (SettingsGroup group : groups.values())
            c.addAll(group.getChildren().values());

        return c;
    }
    
    /**
     * Retrieves all the settings
     */
    public Collection<Setting> getSettings() {
        Collection<Setting> settings = new ArrayList<Setting>();
        for (SettingsGroup group : getGroups())
            settings.addAll(group.getSettings().values());
        
        return settings;
    }
}
