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
import net.datacrow.core.console.IMasterView;
import net.datacrow.core.console.UIComponents;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.modules.DcParentModule;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcImageIcon;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.helpers.User;
import net.datacrow.core.security.SecuredUser;

/**
 * The user module represents users.
 * 
 * @see User
 * 
 * @author Robert Jan van der Waals
 */
public class UserModule extends DcParentModule {
    
    private static final long serialVersionUID = 8781289658107612773L;

    /**
     * Creates a new instance.
     */
    public UserModule() {
        super(DcModules._USER, 
              true,
              "User",
              "",
              "User", 
              "Users", 
              "user", 
              "usr");
    }
    
    /**
     * The small icon.
     */
    @Override
    public DcImageIcon getIcon16() {
        return IconLibrary._icoUser16;
    }

    /**
     * The large icon.
     */
    @Override
    public DcImageIcon getIcon32() {
        return IconLibrary._icoUser32;
    }

    /**
     * Indicates if other modules are depending on this module.
     * @return Always false.
     */
    @Override
    public boolean hasDependingModules() {
        return false;
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
    
    @Override
    public int[] getSupportedViews() {
        return new int[] {IMasterView._LIST_VIEW};
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
     * Creates a new user instance.
     * @see User
     */
    @Override
    public DcObject createItem() {
        return new User();
    }
    
    /**
     * Retrieves the child module
     * @see PermissionModule
     */
    @Override
    public DcModule getChild() {
        return DcModules.get(DcModules._PERMISSION);
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
     * Indicates if this module is a parent module.
     * @return Always true
     */
    @Override
    public boolean isParentModule() {
        return true;
    }

    /**
     * Initializes the default fields.
     */
    @Override
    protected void initializeFields() {
        super.initializeFields();
        addField(new DcField(User._A_LOGINNAME, getIndex(), "Login Name",
                false, true, false, true, 
                255, UIComponents._LOGINNAMEFIELD, getIndex(), DcRepository.ValueTypes._STRING,
                "LoginName"));
        addField(new DcField(User._B_ENABLED, getIndex(), "Enabled",
                false, true, false, true, 
                4, UIComponents._CHECKBOX, getIndex(), DcRepository.ValueTypes._BOOLEAN,
                "Enabled"));
        addField(new DcField(User._C_NAME, getIndex(), "Name",
                false, true, false, true, 
                255, UIComponents._SHORTTEXTFIELD, getIndex(), DcRepository.ValueTypes._STRING,
                "Name"));
        addField(new DcField(User._D_DESCRIPTION, getIndex(), "Description",
                false, true, false, true, 
                4000, UIComponents._LONGTEXTFIELD, getIndex(), DcRepository.ValueTypes._STRING,
                "Description"));
        addField(new DcField(User._E_PHOTO, getIndex(), "Photo",
                true, true, false, false, 
                255, UIComponents._PICTUREFIELD, getIndex(), DcRepository.ValueTypes._PICTURE,
                "Photo"));        
        addField(new DcField(User._F_EMAIL, getIndex(), "Email",
                false, true, false, true, 
                255, UIComponents._SHORTTEXTFIELD, getIndex(), DcRepository.ValueTypes._STRING,
                "Email"));        
        addField(new DcField(User._G_ADDRESS, getIndex(), "Address",
                false, true, false, true, 
                255, UIComponents._SHORTTEXTFIELD, getIndex(), DcRepository.ValueTypes._STRING,
                "Address"));     
        addField(new DcField(User._H_PHONE_HOME, getIndex(), "Phone (Home)",
                false, true, false, false, 
                255, UIComponents._SHORTTEXTFIELD, getIndex(), DcRepository.ValueTypes._STRING,
                "PhoneHome"));     
        addField(new DcField(User._I_PHONE_WORK, getIndex(), "Phone (Work)",
                false, true, false, false, 
                255, UIComponents._SHORTTEXTFIELD, getIndex(), DcRepository.ValueTypes._STRING,
                "PhoneWork"));        
        addField(new DcField(User._J_CITY, getIndex(), "City",
                false, true, false, true, 
                255, UIComponents._SHORTTEXTFIELD, getIndex(), DcRepository.ValueTypes._STRING,
                "City"));
        addField(new DcField(User._K_COUNTRY, getIndex(), "Country",
                false, true, false, true, 
                255, UIComponents._SHORTTEXTFIELD, getIndex(), DcRepository.ValueTypes._STRING,
                "Country"));
        addField(new DcField(User._L_ADMIN, getIndex(), "Admin",
                false, true, false, true, 
                4, UIComponents._CHECKBOX, getIndex(), DcRepository.ValueTypes._BOOLEAN,
                "Admin"));
    }
    
    @Override
    public boolean equals(Object o) {
        return (o instanceof UserModule ? ((UserModule) o).getIndex() == getIndex() : false);
    }     
}
