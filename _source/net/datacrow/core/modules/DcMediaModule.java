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

import java.util.ArrayList;
import java.util.Collection;

import net.datacrow.core.DcRepository;
import net.datacrow.core.console.UIComponents;
import net.datacrow.core.modules.xml.XmlModule;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcMediaObject;

public class DcMediaModule extends DcModule {
    
    private static final long serialVersionUID = -4813944714346927458L;

    /**
     * Creates a new instance based on a XML definition.
     * @param module
     */
    public DcMediaModule(XmlModule module) {
        super(module);
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
    public DcMediaModule(int index, 
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
     * Initializes all fields belonging to this module.
     * @see DcField
     */
    @Override
    protected void initializeFields() {
    	super.initializeFields();

    	addField(new DcField(DcMediaObject._A_TITLE, getIndex(), "Title", 
                false, true, false, true, 
                255, UIComponents._SHORTTEXTFIELD, getIndex(), DcRepository.ValueTypes._STRING,
                "Title"));
        addField(new DcField(DcMediaObject._B_DESCRIPTION, getIndex(), "Description", 
                false, true, false, true,  
                0, UIComponents._LONGTEXTFIELD, getIndex(), DcRepository.ValueTypes._STRING,
                "Description"));
        addField(new DcField(DcMediaObject._C_YEAR, getIndex(), "Year", 
                false, true, false, true,  
                4, UIComponents._NUMBERFIELD, getIndex(), DcRepository.ValueTypes._LONG,
                "Year"));

        if (getIndex() != DcModules._IMAGE && !isAbstract())
            addField(new DcField(DcMediaObject._D_LANGUAGE, getIndex(), "Languages", 
                    true, true, false, true,  
                    255, UIComponents._REFERENCESFIELD, DcModules._LANGUAGE, DcRepository.ValueTypes._DCOBJECTCOLLECTION,
                    "Languages"));
        
        addField(new DcField(DcMediaObject._E_RATING, getIndex(), "Rating", 
                false, true, false, true,  
                255, UIComponents._RATINGCOMBOBOX, getIndex(), DcRepository.ValueTypes._LONG,
                "Rating"));
        addField(new DcField(DcMediaObject._F_COUNTRY, getIndex(), "Countries", 
                true, true, false, true,  
                255, UIComponents._REFERENCESFIELD, DcModules._COUNTRY, DcRepository.ValueTypes._DCOBJECTCOLLECTION,
                "Countries"));           
    }  
    
    @Override
    public int[] getMinimalFields(Collection<Integer> include) {
        Collection<Integer> c = new ArrayList<Integer>();
        
        if (include != null)
            c.addAll(include);
        
        if (!c.contains(Integer.valueOf(DcMediaObject._A_TITLE))) 
            c.add(Integer.valueOf(DcMediaObject._A_TITLE));
        
        return super.getMinimalFields(c);
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof DcMediaModule ? ((DcMediaModule) o).getIndex() == getIndex() : false);
    }      
}
