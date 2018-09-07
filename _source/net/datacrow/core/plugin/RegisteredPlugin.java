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

import java.util.ArrayList;
import java.util.Collection;

import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.DcTemplate;

/**
 * A plugin which has been loaded by Data Crow.
 * 
 * @author Robert Jan van der Waals
 */
public class RegisteredPlugin {
    
    private Class<?> clazz;
    
    private Plugin base;
    private String label;
    
    private Collection<Plugin> cache = new ArrayList<Plugin>();

    /**
     * Creates a new instance.
     * @param clazz
     * @param base
     */
    public RegisteredPlugin(Class<?> clazz, Plugin base) {
        super();
        this.clazz = clazz;
        this.base = base;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getKey() {
        return clazz.getSimpleName();
    }

    public boolean isAdminOnly() {
        return base.isAdminOnly();
    }
    
    public boolean isSystemPlugin() {
        return base.isSystemPlugin();
    }

    public boolean isAuthorizable() {
        return base.isAuthorizable();
    }
    
    public Plugin get(DcObject dco, DcTemplate template, int viewIdx, int moduleIdx, int viewType) {
        if (dco != null) return null;

        for (Plugin plugin : new ArrayList<Plugin>(cache)) {
            if (plugin.getTemplate() == template &&
                plugin.getViewIdx() == viewIdx &&
                plugin.getModuleIdx() == moduleIdx &&
                plugin.getViewType() == viewType)
                return plugin;
        }
        return null;
    }
    
    public void add(Plugin plugin) {
        if (plugin.getItem() == null)
            cache.add(plugin);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
