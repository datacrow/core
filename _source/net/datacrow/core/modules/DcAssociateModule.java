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

package net.datacrow.core.modules;

import net.datacrow.core.DcRepository;
import net.datacrow.core.console.UIComponents;
import net.datacrow.core.modules.xml.XmlModule;
import net.datacrow.core.objects.DcAssociate;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcObject;

public class DcAssociateModule extends DcModule {
    
	private static final long serialVersionUID = -1867066332649634081L;

	/**
     * Creates a new instance.
     * @param index The module index.
     * @param topModule Indicates if the module is a top module. Top modules are allowed
     * to be displayed in the module bar and can be enabled or disabled.
     * @param name The internal unique name of the module.
     * @param description The module description
     * @param objectName The name of the items belonging to this module.
     * @param objectNamePlural The plural name of the items belonging to this module.
     * @param tableName The database table name for this module.
     * @param tableShortName The database table short name for this module.
     */
    public DcAssociateModule(int index,
                             String name,
                             String description, 
                             String objectName, 
                             String objectNamePlural,
                             String tableName, 
                             String tableShortName) {
        
        super(index, false, name, description, objectName, objectNamePlural,
              tableName, tableShortName);
    }
    
    @Override
    public int getDisplayFieldIdx() {
        return DcAssociate._A_NAME;
    }
    
    /**
     * Creates this module based on an XML module definition.
     * @param module
     */
    public DcAssociateModule(XmlModule module) {
        super(module);
    }
    
    @Override
    public boolean isContainerManaged() {
        return false;
    }

    /**
     * Indicates whether this module be selected from the module bar.
     */
    @Override
    public boolean isSelectableInUI() {
        return false;
    }
    
    @Override
    public int getDefaultSortFieldIdx() {
        return DcAssociate._A_NAME;
    }
    
    @Override
    public boolean isTopModule() {
        return true;
    }

    /**
     * Creates a new instance of an item belonging to this module.
     */
    @Override
    public DcObject createItem() {
        return new DcAssociate(getIndex());
    }
    
    @Override
    protected void initializeFields() {
        super.initializeFields();
        
        addField(new DcField(DcAssociate._A_NAME, getIndex(), "Name", 
                false, true, false, true, 
                255, UIComponents._SHORTTEXTFIELD, getIndex(), DcRepository.ValueTypes._STRING,
                "Name"));
        addField(new DcField(DcAssociate._B_DESCRIPTION, getIndex(), "Description", 
                false, true, false, true, 
                4000, UIComponents._LONGTEXTFIELD, getIndex(), DcRepository.ValueTypes._STRING,
                "Description"));
        addField(new DcField(DcAssociate._C_WEBPAGE, getIndex(), "Webpage", 
                false, true, false, true, 
                255, UIComponents._URLFIELD, getIndex(), DcRepository.ValueTypes._STRING,
                "webpage"));
        addField(new DcField(DcAssociate._D_PHOTO, getIndex(), "Photo", 
                true, true, false, false, 
                0, UIComponents._PICTUREFIELD, getIndex(), DcRepository.ValueTypes._PICTURE,
                "Photo"));
        addField(new DcField(DcAssociate._E_FIRSTNAME, getIndex(), "Firstname", 
                false, true, false, true, 
                255, UIComponents._SHORTTEXTFIELD, getIndex(), DcRepository.ValueTypes._STRING,
                "Firstname"));
        addField(new DcField(DcAssociate._F_LASTTNAME, getIndex(), "Lastname", 
                false, true, false, true, 
                255, UIComponents._SHORTTEXTFIELD, getIndex(), DcRepository.ValueTypes._STRING,
                "Lastname"));
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof DcAssociateModule ? ((DcAssociateModule) o).getIndex() == getIndex() : false);
    }      
}
