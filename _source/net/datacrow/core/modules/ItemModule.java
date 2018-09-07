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

/**
 * The item module represents container managed items. The container module is
 * a so called abstract module. This means that this module does not own or manages
 * any items directly. 
 * 
 * @author Robert Jan van der Waals
 */
public class ItemModule extends DcChildModule {

	private static final long serialVersionUID = -560323358112716977L;

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
    public ItemModule(int index, 
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
    public boolean isContainerManaged() {
        return true;
    }

    @Override
    public int getParentReferenceFieldIndex() {
        return DcObject._SYS_CONTAINER;
    }
    
    /**
     * Creates a new module based on a XML definition.
     * @param module
     */  
    public ItemModule(XmlModule module) {
        super(module);
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof ItemModule ? ((ItemModule) o).getIndex() == getIndex() : false);
    }      
}
