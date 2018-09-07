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

import java.util.Collection;

import net.datacrow.core.DcRepository;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.console.UIComponents;
import net.datacrow.core.modules.xml.XmlModule;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcImageIcon;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.DcProperty;

/**
 * A property module is the simplest module type. <br>
 * Examples of property modules are the movie and music genres, the software category, 
 * the storage media and the platforms. A property module will never show up in the 
 * module bar. In fact it will not show up anywhere until used within another module. 
 * Its existence depends on other modules.<br>
 * Property modules are solely used by reference fields.
 * 
 * @see DcReferencesField
 * @see DcComboBox
 * 
 * @author Robert Jan van der Waals
 */
public class DcPropertyModule extends DcModule {

    private static final long serialVersionUID = -1481435217423089270L;
    
    /**
     * Creates a new module based on a XML definition.
     * @param module
     */    
    public DcPropertyModule(XmlModule xmlModule) {
        super(xmlModule);
        setServingMultipleModules(xmlModule.isServingMultipleModules());
    }  
    
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
    public DcPropertyModule(int index, 
                            String name, 
                            String tableName, 
                            String tableShortName, 
                            String objectName, 
                            String objectNamePlural) {
        
        super(index, false, name, "", objectName, objectNamePlural, tableName, tableShortName);
    }    

    public DcPropertyModule getInstance(int index, String name, String tableName,
            String tableShortName, String objectName, String objectNamePlural) {
        
        return new DcPropertyModule(index, name, tableName, tableShortName, objectName, objectNamePlural);
    }
    
    @Override
    public boolean isTopModule() {
        return false;
    }    
    
    @Override
    public boolean hasInsertView() {
        return false;
    }

    @Override
    public boolean hasSearchView() {
        return false;
    }    
    
    @Override
    public boolean hasDependingModules() {
        return true;
    }
    
    @Override
    public int getDisplayFieldIdx() {
        return DcProperty._A_NAME;
    }

    /**
     * Retrieves the index of the field on which is sorted by default.  
     * Always returns the name field. 
     * @see DcProperty#_A_NAME
     */
    @Override
    public int getDefaultSortFieldIdx() {
        return DcProperty._A_NAME;
    }

    @Override
    public int[] getSupportedViews() {
        return new int[] {};
    }
    
    /**
     * Creates a new instance of an item belonging to this module.
     */
    @Override
    public DcObject createItem() {
        return new DcProperty(getIndex());
    }
    
    /**
     * Indicates if this module is allowed to be customized. 
     * Always returns false.
     */
    @Override
    public boolean isCustomFieldsAllowed() {
        return false;
    }
    
    @Override
	public int[] getMinimalFields(Collection<Integer> include) {
		return new int[] {DcObject._ID, DcProperty._A_NAME, DcProperty._B_ICON, DcProperty._C_ALTERNATIVE_NAMES};
	}

	/**
     * Initializes the default fields.
     */
    @Override
    protected void initializeFields() {
        super.initializeFields();
        addField(new DcField(DcProperty._A_NAME, getIndex(), "Name", 
                false, true, false, true, 
                255, UIComponents._SHORTTEXTFIELD, getIndex(), DcRepository.ValueTypes._STRING,
                "Name"));
        addField(new DcField(DcProperty._B_ICON, getIndex(), "Icon", 
                false, true, false, false, 
                255, UIComponents._PICTUREFIELD, getIndex(), DcRepository.ValueTypes._ICON,
                "Icon"));
        addField(new DcField(DcProperty._C_ALTERNATIVE_NAMES, getIndex(), "Alternative Names", 
                false, true, false, false, 
                4000, UIComponents._LONGTEXTFIELD, getIndex(), DcRepository.ValueTypes._STRING,
                "alternative_names"));        
    }  
    
    @Override
    public DcImageIcon getIcon16() {
        return  super.getIcon16() == null ? IconLibrary._icoModuleTypeProperty16 : super.getIcon16();
    }

    @Override
    public DcImageIcon getIcon32() {
        return  super.getIcon32() == null ? IconLibrary._icoModuleTypeProperty32 : super.getIcon32();
    }

    /**
     * Returns the template module.
     * @return Always returns null.
     */
    @Override
    public TemplateModule getTemplateModule() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof DcPropertyModule ? ((DcPropertyModule) o).getIndex() == getIndex() : false);
    }
}
