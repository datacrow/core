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

package net.datacrow.core.objects.helpers;

import net.datacrow.core.DcConfig;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcImageIcon;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.Picture;
import net.datacrow.core.objects.ValidationException;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.server.Connector;

public class User extends DcObject {

    private static final long serialVersionUID = -6350928968206517038L;
    
    public static final int _A_LOGINNAME = 1;
    public static final int _B_ENABLED = 2;
    public static final int _C_NAME = 3;
    public static final int _D_DESCRIPTION = 4;
    public static final int _E_PHOTO = 5;
    public static final int _F_EMAIL = 6;
    public static final int _G_ADDRESS = 7;
    public static final int _H_PHONE_HOME = 8;
    public static final int _I_PHONE_WORK = 9;
    public static final int _J_CITY = 11;
    public static final int _K_COUNTRY = 12;
    public static final int _L_ADMIN = 13;
    
    public User() {
        super(DcModules._USER);
    }
    
    @Override
    public void loadChildren(int[] fields) {
    	
        if (isNew()) return;
    	
        // Permissions do not need to be reloaded as the number of permission is static.
        // Note that doing this for other modules will re-introduce the bug: deleted children are shown.
        if (getModule().getChild() != null && children.size() == 0) {
            children.clear();
            int childIdx = getModule().getChild().getIndex();
            for (DcObject dco : DcConfig.getInstance().getConnector().getChildren(getID(), childIdx, fields))
                children.add(dco);
        }
    }      
    
    @Override
    public void beforeSave() throws ValidationException {
        super.beforeSave();

        if (isNew())
        	setValue(_B_ENABLED, Boolean.TRUE);
        
        Picture picture = (Picture) getValue(_E_PHOTO);
        if (picture == null || picture.getValue(Picture._D_IMAGE) == null) {
            setValue(User._E_PHOTO, new DcImageIcon(DcConfig.getInstance().getInstallationDir() + "icons/" + "user.png"));
        }
        
        String loginname = (String) getValue(_A_LOGINNAME);
        if (loginname != null)
            setValue(_A_LOGINNAME, loginname.toLowerCase());
        else 
            throw new ValidationException(DcResources.getText("msgLoginNameNotFilled"));
    }
    
    @Override
    public void afterSave() {
    	Connector connector = DcConfig.getInstance().getConnector();
    	if (isNew()) {
    		connector.createUser(this, "");
    	} else {
    		connector.updateUser(this);
    	}
    }

    @Override
    public void checkIntegrity() throws ValidationException {
    	super.checkIntegrity();
    	
    	if (!isNew() & isChanged(User._A_LOGINNAME)) {
    		DcObject original = DcConfig.getInstance().getConnector().getItem(DcModules._USER, getID(), new int[] {User._A_LOGINNAME, DcObject._ID});
    		
    		if (!original.getValue(User._A_LOGINNAME).equals(getValue(User._A_LOGINNAME))) {
        		setValue(User._A_LOGINNAME, original.getValue(User._A_LOGINNAME));
        		getValueDef(User._A_LOGINNAME).setChanged(false);
        		throw new ValidationException(DcResources.getText("msgLoginnameIsNotAllowedToChange"));
    		}
    	}
    }

    
    
    @Override
	public void afterDelete() {

	}

	@Override
    public void beforeDelete() throws ValidationException {
        boolean canBeDeleted = true;
        
        if (isAdmin())
            canBeDeleted = DcConfig.getInstance().getConnector().getCount(DcModules._USER, User._L_ADMIN, Boolean.TRUE) > 1;
                
        if (!canBeDeleted) {
        	throw new ValidationException(DcResources.getText("msgCannotDeleteThisUser"));
        } 
        
        Connector conn = DcConfig.getInstance().getConnector();
        conn.dropUser(this);
    }

    public boolean isAdmin() {
        return getValue(_L_ADMIN) != null ? Boolean.valueOf(getValue(_L_ADMIN).toString()) : false;
    }
}
