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

/**
 * A parent module is a parent to another module. The child module and its items
 * depend on the existence of this module.
 * 
 * @see DcChildModule
 * @author Robert Jan van der Waals
 */
public class DcParentModule extends DcModule  {

    private static final long serialVersionUID = 4714961191122806192L;

    /**
     * Creates a new module based on a XML definition.
     * @param module
     */
    public DcParentModule(XmlModule xmlModule) {
        super(xmlModule);
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
    public DcParentModule(int index, 
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
    
    /**
     * Indicates if this module is a parent module.
     * Always returns true.
     */
    @Override
    public boolean isParentModule() {
        return true;
    }      
    
    /**
     * Indicates if this module is a top module. Top modules are allowed
     * to be displayed in the module bar and can be enabled or disabled.
     */
    @Override
    public boolean isTopModule() {
        return true;
    }
    
    @Override
    public boolean equals(Object o) {
        return (o instanceof DcParentModule ? ((DcParentModule) o).getIndex() == getIndex() : false);
    }     
}