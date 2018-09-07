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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

import net.datacrow.core.DcConfig;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.DcTemplate;
import net.datacrow.core.utilities.Directory;

import org.apache.log4j.Logger;

/**
 * Holder of all plugins. Caches loaded classes and instances.
 * Not threadsafe, should only be called from the Swing thread.
 */
public class Plugins {

    private static Logger logger = Logger.getLogger(Plugins.class.getName());
    private static Plugins instance = new Plugins();
    private final Pattern pattern = Pattern.compile("[\\\\\\/]");

    private final Collection<RegisteredPlugin> registered = new ArrayList<RegisteredPlugin>();
    
    public Plugins() {
        initialize();
    }
    
    private synchronized void initialize() {
    	if (DcConfig.getInstance().getPluginsDir() != null) 
    		loadPlugins();
    }

    /**
     * Loads the plugin classes with the help of the Plugin Class Loader.
     */
    private void loadPlugins() {
        String check = File.separator + "plugins" + File.separator;
        Object[] params = new Object[] {null, null, -1, -1, -1};
        PluginClassLoader cl = new PluginClassLoader(DcConfig.getInstance().getPluginsDir());
        
        Directory dir = new Directory(DcConfig.getInstance().getPluginsDir(), true, new String[] {"class"});
        for (String filename : dir.read()) {
            try {
                String classname = filename.substring(filename.indexOf(check) + 1, filename.lastIndexOf('.'));
                classname = pattern.matcher(classname).replaceAll(".");
                Class<?> clazz = cl.loadClass(classname);
                Plugin plugin = (Plugin) clazz.getConstructors()[0].newInstance(params);
                registered.add(new RegisteredPlugin(clazz, plugin));
            } catch (Exception e) {
                logger.error(e, e);
            }
        }
    }
    
    public static Plugins getInstance() {
        return instance;
    }
    
    public Collection<RegisteredPlugin> getRegistered() {
        return new ArrayList<RegisteredPlugin>(registered);
    }
    
    public Collection<Plugin> getUserPlugins(DcObject dco, int viewIdx, int moduleIdx, int viewType) {
        Collection<Plugin> plugins = new ArrayList<Plugin>();
        for (RegisteredPlugin rp : registered) {
            if (!rp.isSystemPlugin()) {
                Object[] params = new Object[] {dco, null,  
                                                Integer.valueOf(viewIdx), 
                                                Integer.valueOf(moduleIdx),
                                                Integer.valueOf(viewType)};
                plugins.add(getInstance(rp.getClazz(), params));
            }
        }
        return plugins;
    }
    
    public Plugin get(String key, int moduleIdx) throws InvalidPluginException {
        return get(key, null, null, -1, moduleIdx, -1);
    }

    public Plugin get(String key) throws InvalidPluginException {
        return get(key, null, null, -1, -1, -1);
    }
    
    public Plugin get(String key, DcObject dco, DcTemplate template, int viewIdx, int moduleIdx, int viewType) throws InvalidPluginException {
        
        RegisteredPlugin registeredPlugin = getRegisteredPlugin(key);
        
        if (registeredPlugin == null) {
            logger.error("Could not find plugin " + key);
            throw new InvalidPluginException("Could not find plugin " + key);
        }
        
        Plugin plugin = registeredPlugin.get(dco, template, viewIdx, moduleIdx, viewType);
        if (plugin == null) {
            Object[] params = new Object[] {dco, 
                                            template,  
                                            Integer.valueOf(viewIdx), 
                                            Integer.valueOf(moduleIdx),
                                            Integer.valueOf(viewType)};
            
            plugin = getInstance(registeredPlugin.getClazz(), params);
            registeredPlugin.add(plugin);
        }
        
        return plugin;
    }

    private Plugin getInstance(Class<?> clazz, Object[] params) {
        try {
            return (Plugin) clazz.getConstructors()[0].newInstance(params);
        } catch (Exception e) {
            logger.error("Could not create plugin for " + clazz, e);
        }
        return null;
    }
    
    private RegisteredPlugin getRegisteredPlugin(String key) {
        for (RegisteredPlugin registeredPlugin : registered) {
            if (registeredPlugin.getKey().equals(key)) 
                return registeredPlugin;
        }
        return null;
    }
}
