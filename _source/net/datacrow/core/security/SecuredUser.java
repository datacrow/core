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

package net.datacrow.core.security;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.helpers.Permission;
import net.datacrow.core.objects.helpers.User;
import net.datacrow.core.plugin.InvalidPluginException;
import net.datacrow.core.plugin.Plugin;
import net.datacrow.core.plugin.Plugins;
import net.datacrow.core.plugin.RegisteredPlugin;

import org.apache.log4j.Logger;

/**
 * A secured user is a user which has logged on successfully.
 * @author Robert Jan van der Waals
 */
public final class SecuredUser implements Serializable {
    
    private static final long serialVersionUID = -2189132770680792540L;

    private transient static Logger logger = Logger.getLogger(SecuredUser.class.getName());
    
    private Map<Integer, ModulePermission> modulePermissions = new HashMap<Integer, ModulePermission>();
    private Map<String, PluginPermission> pluginPermissions = new HashMap<String, PluginPermission>();
    
    private User user;
    private String password;
    
    /**
     * Creates a new instance
     * @param user The user
     * @param password An his / her password
     */
    public SecuredUser(User user, String password) {
        this.user = user;
        this.password = password;
    }
    
    /**
     * Retrieves the underlying user.
     */
    public User getUser() {
        return user;
    }
    
    /**
     * The login name of the user
     */
    public String getUsername() {
        return (String) user.getValue(User._A_LOGINNAME);
    }    
    
    /**
     * The password of the user
     */
    public String getPassword() {
        return password;
    }  

    /**
     * Indicates if the user is an administrator
     */
    public boolean isAdmin() {
        return user.isAdmin();
    }

    /**
     * Checks if the user is allowed to see the specified field
     * @param field The to be checked field 
     */
    public boolean isAuthorized(DcField field) {
        if (isAdmin()) return true;
        ModulePermission mp = modulePermissions.get(Integer.valueOf(field.getModule()));
        return mp == null ? false : mp.isAuthorized(field);
    }
    
    /**
     * Checks if the user is allowed to see the module
     * @param module The to be checked module
     */
    public boolean isAuthorized(DcModule module) {
    	if (isAdmin()) return true;
        
    	return modulePermissions.get(Integer.valueOf(module.getIndex())).isAuthorized();
    }
    
    /**
     * Indicates if the user is allowed to edit items belonging to the module.
     * @param module The to be checked module
     */
    public boolean isEditingAllowed(DcModule module) {
    	if (isAdmin()) return true;
        
        return modulePermissions.get(Integer.valueOf(module.getIndex())).isEditingAllowed();
    }

    /**
     * Indicates if the user is allowed to edit the specified field.
     * @param field The to be checked field
     */
    public boolean isEditingAllowed(DcField field) {
    	if (isAdmin()) return true;
    	
        ModulePermission mp = modulePermissions.get(Integer.valueOf(field.getModule()));
        Permission permission = mp != null ? mp.getPermision(field.getIndex()) : null;
        return permission == null ? false : permission.isEditingAllowed();
    }
    
    /**
     * Checks if the user is allowed to use the plugin.
     * @param plugin The plugin key
     */
    public boolean isAuthorized(String plugin) {
        try {
            return isAuthorized(Plugins.getInstance().get(plugin));
        } catch (InvalidPluginException ipe) {
            logger.error(ipe, ipe);
            return false;
        }
    }
    
    /**
     * Sets the password for this user.
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Checks if the user is allowed to use the plugin.
     * @param plugin
     */
    public boolean isAuthorized(Plugin plugin) {
        if (user.isAdmin()) return true;
        
        if (plugin.isAdminOnly()) {
            return false;
        } else if (!plugin.isAuthorizable()) {
            return true;
        } else if (!plugin.isAdminOnly()) {
            PluginPermission permission = pluginPermissions.get(plugin.getKey());
            return permission != null && permission.isAuthorized();
        } 
        return false;
    }    
    
    /**
     * Set the field and module privileges
     */
    public void loadPermissions() {
        Collection<DcModule> managedModules = DcModules.getSecuredModules();
        for (DcModule module : DcModules.getAllModules()) {
            ModulePermission mp = new ModulePermission(module.getIndex());
            if (managedModules.contains(module)) {
                for (DcObject child : user.getChildren()) {
                    Permission permission = (Permission) child;
                    if (permission.getPermittedModuleIdx() == module.getIndex())
                        mp.addPermission(permission);
                }
            } else {
                // not managed: access is allowed
                for (DcField field : module.getFields()) {
                    DcObject permission = DcModules.get(DcModules._PERMISSION).getItem();
                    permission.setValue(Permission._B_FIELD, Long.valueOf(field.getIndex()));
                    permission.setValue(Permission._C_MODULE, Long.valueOf(module.getIndex()));
                    permission.setValue(Permission._D_VIEW, Boolean.TRUE);
                    permission.setValue(Permission._E_EDIT, Boolean.TRUE);
                    mp.addPermission((Permission) permission);
                }
            }
            modulePermissions.put(Integer.valueOf(module.getIndex()), mp);
        }   
        
        for (RegisteredPlugin plugin : Plugins.getInstance().getRegistered()) {
             for (DcObject child : user.getChildren()) {
                 Permission permission = (Permission) child;
                 if (permission.getPlugin() != null && permission.getPlugin().equals(plugin.getKey())) {
                     PluginPermission pp = new PluginPermission(plugin.getKey());
                     pp.setAuthorized(permission.isViewingAllowed());
                     pluginPermissions.put(permission.getPlugin(), pp);                     
                 }
             }
        }
    }

	@Override
	public String toString() {
		return user != null ? user.getDisplayString(User._A_LOGINNAME) : "";
	}
}
