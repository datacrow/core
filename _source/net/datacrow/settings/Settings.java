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
import java.io.Serializable;

import net.datacrow.core.objects.DcLookAndFeel;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.settings.Setting;
import net.datacrow.core.settings.SettingsFile;
import net.datacrow.settings.definitions.IDefinitions;

import org.apache.log4j.Logger;

public abstract class Settings implements Serializable {
    
	private static final long serialVersionUID = -33466202843742845L;

	private static Logger logger = Logger.getLogger(Settings.class.getName());
    
    private final net.datacrow.core.settings.Settings settings;
    
    public Settings() {
        settings = new net.datacrow.core.settings.Settings();
        createGroups();
    }
    
    protected void load() {
        try {
            SettingsFile.load(settings);
        } catch (Exception e) {
            logger.error(DcResources.getText("msgFailedToLoadUserSettings"), e);
        }
    }
    
    protected abstract void createGroups();
    
    public boolean contains(String key) {
        return settings.getSetting(key) != null;
    }

    public Setting getSetting(String key) {
        return settings.getSetting(key);
    }
    
    public net.datacrow.core.settings.Settings getSettings() {
        return settings;
    }
    
    public void save() {
        SettingsFile.save(settings);
    }    
    
    public void addSetting(String group, Setting setting) {
        settings.addSetting(group, setting);
    }
    
    public Object get(String key) {
        return settings.getValue(key);
    }

    public void set(String key, Object value) {
        settings.setValue(key, value);
    }

    public int[] getIntArray(String key) {
        return (int[]) get(key);
    }

    public IDefinitions getDefinitions(String key) {
        return (IDefinitions) get(key);
    }

    public Font getFont(String key) {
        return (Font) get(key);
    }

    public DcLookAndFeel getLookAndFeel(String key) {
        return (DcLookAndFeel) get(key);
    }
    
    public long getLong(String key) {
        Object o = get(key);
        return o instanceof Long ? ((Long) o).longValue() : 0l;
    }    
    
    public int getInt(String key) {
        Object o = get(key);
        return o instanceof Integer ? ((Integer) o).intValue() :
               o instanceof Long ? ((Long) o).intValue() : 0;
    }

    public boolean getBoolean(String key) {
        return ((Boolean) get(key)).booleanValue();
    }

    public Color getColor(String key) {
        return (Color) get(key);
    }

    public Dimension getDimension(String key) {
        return (Dimension) get(key);
    }

    public String getString(String key) {
        return (String) get(key);
    }    

    public String[] getStringArray(String key) {
        return (String[]) get(key);
    }    
    
    public void parseString(String key, String value) {
        settings.setString(key, value);
    }
}
