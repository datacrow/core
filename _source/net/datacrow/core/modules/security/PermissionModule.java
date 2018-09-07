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

package net.datacrow.core.modules.security;

import net.datacrow.core.DcConfig;
import net.datacrow.core.DcRepository;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.console.UIComponents;
import net.datacrow.core.modules.DcChildModule;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcImageIcon;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.helpers.Permission;
import net.datacrow.core.objects.helpers.User;
import net.datacrow.core.security.SecuredUser;

/**
 * The permission module holds all permissions as part of the security functionality.
 * The permission module is a child module of the user module.
 * 
 * @see UserModule
 * @see User
 * @see Permission
 * 
 * @author Robert Jan van der Waals
 */
public class PermissionModule extends DcChildModule {

    private static final long serialVersionUID = -7129402893574458367L;

    /**
     * Creates a new instance.
     */
    public PermissionModule() {
        super(DcModules._PERMISSION, 
              false,
              "Permission",
              "",
              "Permission", 
              "Permissions", 
              "permission", 
              "perm");
    }
    
    /**
     * Creates a new permission.
     * @see Permission
     */
    @Override
    public DcObject createItem() {
        return new Permission();
    }
    
    /**
     * Retrieves the parent module.
     * @see UserModule
     */
    @Override
    public DcModule getParent() {
        return DcModules.get(DcModules._USER);
    }

    /**
     * The small icon.
     */
    @Override
    public DcImageIcon getIcon16() {
        return IconLibrary._icoPermission16;
    }

    /**
     * The large icon.
     */
    @Override
    public DcImageIcon getIcon32() {
        return IconLibrary._icoPermission32;
    }

    /**
     * Indicates of other modules are depending on this module.
     * @return Always false for this module.
     */
    @Override
    public boolean hasDependingModules() {
        return false;
    }

    /**
     * Indicates if this module has a search view.
     * @return Always true.
     */
    @Override
    public boolean hasSearchView() {
        return true;
    }
    
    /**
     * Indicates if this module has an insert view.
     * @return Always false.
     */
    @Override
    public boolean hasInsertView() {
        return false;
    }

    /**
     * Indicates if items belonging to this module are file based.
     * @return Always false.
     */
    @Override
    public boolean isFileBacked() {
        return false;
    }
    
    /**
     * Indicates if this module is allowed to be customized.
     * @return Always false.
     */
    @Override
    public boolean isCustomFieldsAllowed() {
        return false;
    }
    
    /**
     * Indicates if this is a child module.
     * @return Always true.
     */
    @Override
    public boolean isChildModule() {
        return true;
    }
    
    /**
     * Indicates if this module is enabled.
     * @return Depends if the user currently logged on is an administrator. 
     */
    @Override
    public boolean isEnabled() {
        SecuredUser su = DcConfig.getInstance().getConnector().getUser();
        return su != null ? su.isAdmin() : true;
    }
    
    /**
     * Creates the default fields.
     */
    @Override
    protected void initializeFields() {
        super.initializeFields();
        addField(new DcField(Permission._A_PLUGIN, getIndex(), "Plugin",
                false, true, false, false, 
                255, UIComponents._SHORTTEXTFIELD, getIndex(), DcRepository.ValueTypes._STRING,
                "Plugin"));
        addField(new DcField(Permission._B_FIELD, getIndex(), "Field",
                false, true, false, false, 
                4000, UIComponents._NUMBERFIELD, getIndex(), DcRepository.ValueTypes._LONG,
                "Field"));
        addField(new DcField(Permission._C_MODULE, getIndex(), "Module",
                false, true, false, false, 
                10, UIComponents._NUMBERFIELD, getIndex(), DcRepository.ValueTypes._LONG,
                "Module"));
        addField(new DcField(Permission._D_VIEW, getIndex(), "View",
                false, true, false, false, 
                1, UIComponents._CHECKBOX, getIndex(), DcRepository.ValueTypes._BOOLEAN,
                "View"));
        addField(new DcField(Permission._E_EDIT, getIndex(), "Edit",
                false, true, false, false, 
                1, UIComponents._CHECKBOX, getIndex(), DcRepository.ValueTypes._BOOLEAN,
                "Edit"));
        addField(new DcField(Permission._F_USER, DcModules._USER, "User",
                false, true, false, false, 
                36, UIComponents._SHORTTEXTFIELD, getIndex(), DcRepository.ValueTypes._DCPARENTREFERENCE,
                "UserID"));
    }
    
    @Override
    public boolean equals(Object o) {
        return (o instanceof PermissionModule ? ((PermissionModule) o).getIndex() == getIndex() : false);
    }     
}
