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

import net.datacrow.core.IconLibrary;
import net.datacrow.core.modules.xml.XmlModule;
import net.datacrow.core.objects.DcImageIcon;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.DcTag;

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
public class DcTagModule extends DcPropertyModule {
      
	private static final long serialVersionUID = -5580785551441554790L;

	/**
     * Creates a new module based on a XML definition.
     * @param module
     */    
    public DcTagModule(XmlModule xmlModule) {
        super(xmlModule);
        setServingMultipleModules(true);
    }  
    
    public DcTagModule() {
        super(DcModules._TAG, "Tags", "Tag", "Tag", "Tag", "Tags");
        setServingMultipleModules(true);
    }    

    @Override
    public DcTagModule getInstance(
            int module,
            String name, 
            String tableName,
            String tableShortName, 
            String objectName, 
            String objectNamePlural) {
     
        return new DcTagModule();
    }
    
    @Override
    public boolean hasDependingModules() {
        return true;
    }

    /**
     * Creates a new instance of an item belonging to this module.
     */
    @Override
    public DcObject createItem() {
        return new DcTag();
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
		return new int[] {DcObject._ID, DcTag._A_NAME};
	}

    @Override
    public DcImageIcon getIcon16() {
        return  super.getIcon16() == null ? IconLibrary._icoModuleTypeProperty16 : super.getIcon16();
    }

    @Override
    public DcImageIcon getIcon32() {
        return  super.getIcon32() == null ? IconLibrary._icoModuleTypeProperty32 : super.getIcon32();
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof DcTagModule ? ((DcTagModule) o).getIndex() == getIndex() : false);
    }
}
