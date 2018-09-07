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

package net.datacrow.core.migration.itemimport;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcField;

public class ItemImporterFieldMappings {

    private Map<String, DcField> mappings = new LinkedHashMap<String, DcField>();
    
    public ItemImporterFieldMappings() {}
    
    public void clear() {
        mappings.clear();
    }
    
    public void setMapping(String source, DcField target) {
        mappings.put(source, target);
    }
    
    public DcField getTarget(String fieldName) {
        return mappings.get(fieldName);
    }
    
    public DcField getTarget(int index) {
        int counter = 0;
        for (DcField field : mappings.values()) {
            if (counter == index) return field;
            counter++;
        }
        return null;
    }
    
    public Collection<String> getSourceFields() {
        return mappings.keySet();
    }
    
    /**
     * Initializes the mapping table.
     * Tries to find the corresponding module fields.
     * @param moduleIdx
     * @param fields
     */
    public void setFields(int moduleIdx, Collection<String> fields) {
        DcModule module = DcModules.get(moduleIdx);
        DcField target;
        for (String fieldName : fields) {
            target = null;
            for (DcField field : module.getFields()) {
                if (field.getDatabaseFieldName().equals(fieldName) || 
                    field.getLabel().equals(fieldName) || 
                    field.getOriginalLabel().equals(fieldName)) {
                    target = field;
                    break;
                }
            }
            setMapping(fieldName, target);
        }
    }
}
