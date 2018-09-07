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

import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcObject;

public class Permission extends DcObject {

    private static final long serialVersionUID = -6649072271337366239L;
    
    public static final int _A_PLUGIN = 1;
    public static final int _B_FIELD = 2;
    public static final int _C_MODULE = 3;
    public static final int _D_VIEW = 4;
    public static final int _E_EDIT = 5;
    public static final int _F_USER = 6;
    
    public Permission() {
        super(DcModules._PERMISSION);
    }

    public String getPlugin() {
        return (String) getValue(Permission._A_PLUGIN);
    }
    
    public boolean isEditingAllowed() {
        Object o = getValue(Permission._E_EDIT);
        return o == null ? false : (Boolean) getValue(Permission._E_EDIT);
    }

    public boolean isViewingAllowed() {
        Object o = getValue(Permission._D_VIEW);
        return o == null ? false : (Boolean) getValue(Permission._D_VIEW);
    }
    
    public int getFieldIdx() {
        return getValue(Permission._B_FIELD) != null ?  
               ((Long) getValue(Permission._B_FIELD)).intValue() : -1;
    }
    
    public int getPermittedModuleIdx() {
        return getValue(Permission._C_MODULE) != null ?  
               ((Long) getValue(Permission._C_MODULE)).intValue() : -1;
    }

}
