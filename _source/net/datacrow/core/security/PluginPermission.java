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

/**
 * Indicates which permissions the user has for a specific plugin.
 * 
 * @author Robert Jan van der Waals
 */
public final class PluginPermission implements Serializable {

    private static final long serialVersionUID = -6365857869321863289L;

    private String key;
    private boolean authorized = true;
    
    /**
     * Creates a new instance
     * @param key The key of the plugin (class name)
     */
    protected PluginPermission(String key) {
        super();
        this.key = key;
    }
    
    /**
     * The key of the plugin
     */
    public final String getKey() {
        return key;
    }
    
    /**
     * Checks if the user is authorized to use the plugin.
     */
    public final boolean isAuthorized() {
        return authorized;
    }

    /**
     * Set the permission
     */
    public final void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }
}
