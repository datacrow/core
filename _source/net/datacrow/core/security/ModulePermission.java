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
import java.util.HashMap;
import java.util.Map;

import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.helpers.Permission;

/**
 * Indicates which permissions the user has for a specific module.
 * 
 * @author Robert Jan van der Waals
 */
public final class ModulePermission implements Serializable {

	private static final long serialVersionUID = -110561509699847482L;

	private int moduleIdx;
    
    private Map<Integer, Permission> fields = new HashMap<Integer, Permission>();
    
    /**
     * Creates a new instance
     * @param moduleIdx
     */
    protected ModulePermission(int moduleIdx) {
        this.moduleIdx = moduleIdx;
    }

    /**
     * The module index.
     */
    public final int getModuleIdx() {
        return moduleIdx;
    }

    /**
     * Sets the module index
     * @param moduleIdx Module index
     */
    public final void setModuleIdx(int moduleIdx) {
        this.moduleIdx = moduleIdx;
    }

    public final Permission getPermision(int fieldIdx) {
        return fields.get(Integer.valueOf(fieldIdx));
    }
    
    /**
     * Sets the permission for a specific field
     * @param field
     */
    public final void addPermission(Permission field) {
        fields.put(Integer.valueOf(field.getFieldIdx()), field);
    }
    
    /**
     * Indicates if the user is authorized to view the field.
     * @param field
     */
    public final boolean isAuthorized(DcField field) {
        Permission permission = fields.get(Integer.valueOf(field.getIndex()));
        return permission != null ? permission.isViewingAllowed() : true;
    }
    
    /**
     * Checks if the user is allowed to see the module.
     * The check is actually based on the field permissions. If the user is not allowed
     * to view any of the fields the user will not have access to the module.
     */
    public final boolean isAuthorized() {
        for (Permission permission : fields.values()) {
            if (permission.isViewingAllowed())
                return true;
        }
        return false;
    }

    /**
     * Checks if the user is allowed to make changes to items belonging to the module.
     * The check is actually based on the field permissions. If the user is not allowed
     * to edit any of the fields the user will not have write access to the module.
     */
    public final boolean isEditingAllowed() {
        for (Permission permission : fields.values()) {
            if (permission.isEditingAllowed())
                return true;
        }
        return false;
    }
}
