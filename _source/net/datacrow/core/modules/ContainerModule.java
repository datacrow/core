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

import net.datacrow.core.modules.xml.XmlModule;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.helpers.Container;

/**
 * The container module hold any kind of item as long as the item belongs to a
 * container managed module. Container managed items are represented by the 
 * item module.
 * 
 * @see ItemModule
 * @author Robert Jan van der Waals
 */
public class ContainerModule extends DcParentModule {

    private static final long serialVersionUID = -8086927475371268096L;

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
    public ContainerModule(int index, 
                           boolean topModule, 
                           String name,
                           String description, 
                           String objectName, 
                           String objectNamePlural,
                           String tableName, 
                           String tableShortName) {
        
        super(index, topModule, name, description, objectName, objectNamePlural,
              tableName, tableShortName);
    }
    
    @Override
    public boolean isServingMultipleModules() {
        return true;
    }

    /**
     * Creates this module based on an XML module definition.
     * @param module
     */
    public ContainerModule(XmlModule module) {
        super(module);
    }
    
//    @Override
//    public int[] getSupportedViews() {
//        return new int[] {IMasterView._LIST_VIEW};
//    }
//    
    @Override
	public boolean isContainerManaged() {
		return false;
	}

//	/**
//     * Initializes the various views.
//     */
//    @Override
//    protected void initializeUI()  {
//        if (searchView == null && hasSearchView() ) {
//            searchView = new IMasterView(getIndex());
//            searchView.setTreePanel(this);
//            
//            // list view
//            DcObjectList list = new DcObjectList(this, DcObjectList._CARDS, true, true);
//            View listView = new View(searchView, View._TYPE_SEARCH, list, IMasterView._LIST_VIEW);
//            list.setView(listView);
//
//            searchView.addView(IMasterView._LIST_VIEW, listView);            
//        }
//    }

    /**
     * Indicates whether this module be selected from the module bar.
     */
    @Override
    public boolean isSelectableInUI() {
        return true;
    }
    
    @Override
    public int getDefaultSortFieldIdx() {
        return Container._A_NAME;
    }

    /**
     * Creates a new instance of an item belonging to this module.
     */
    @Override
    public DcObject createItem() {
        return new Container();
    }
    
    @Override
    public boolean equals(Object o) {
        return (o instanceof ContainerModule ? ((ContainerModule) o).getIndex() == getIndex() : false);
    }      
}
