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
import net.datacrow.core.console.UIComponents;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcMapping;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.utilities.CoreUtilities;

/**
 * A mapping module creates a link between two modules. Used for many to many
 * relationships.
 * 
 * @author Robert Jan van der Waals
 */
public class MappingModule extends DcModule {

    private static final long serialVersionUID = 7229196343761371630L;

    private final DcModule parentMod;
    private final DcModule referencedMod;
   
    /**
     * Creates a new mapping module to link the specified modules.
     * The table is named / formed as follows: X_main module_fieldname
     * @param parentMod The parent module.
     * @param referencedMod The child/referenced module. 
     */
    public MappingModule(DcModule parentMod, DcModule referencedMod, int fieldIdx) {
        super(referencedMod.getIndex() + DcModules._MAPPING + parentMod.getIndex() + fieldIdx, 
              "", "", "", "", 
              "X_" + parentMod.getTableName() + "_" + CoreUtilities.toDatabaseName(parentMod.getField(fieldIdx).getSystemName()), 
              "X_" + parentMod.getTableShortName() + "_" + CoreUtilities.toDatabaseName(parentMod.getField(fieldIdx).getSystemName()), 
              false);
        
        this.parentMod = parentMod;
        this.referencedMod = referencedMod;
        
        initializeSystemFields();
        initializeFields();
        initializeSettings();
    } 
    
    /**
     * Creates a new instance of a mapping item.
     * @see DcMapping
     */
    @Override
    public DcObject createItem() {
        DcMapping mapping = new DcMapping(getIndex());
        return mapping;
    }    
    
    /**
     * The referenced module index.
     * @return
     */
    public int getReferencedModIdx() {
        return getField(DcMapping._B_REFERENCED_ID).getSourceModuleIdx();
    } 
    
    /**
     * The parent module index.
     */
    public int getParentModIdx() {
        return getField(DcMapping._A_PARENT_ID).getReferenceIdx();
    } 
    
    @Override
    public int[] getMinimalFields(Collection<Integer> include) {
        return null;
    }

    /**
     * A mapping module does not have any views.
     * @return Always false
     */
    @Override
    public boolean hasInsertView() {
        return false;
    }

    /**
     * A mapping module does not have any views.
     * @return Always false
     */
    @Override
    public boolean hasSearchView() {
        return false;
    }    
    
    /**
     * Creates the default fields.
     */
    @Override
    protected void initializeFields() {
        
        addField(new DcField(DcObject._SYS_CREATED, getIndex(), "Created",
                false, true, true, true, 
                10, UIComponents._DATEFIELD, getIndex(), DcRepository.ValueTypes._DATE,
                "Created"));
        addField(new DcField(DcObject._SYS_MODIFIED, getIndex(), "Modified",
                false, true, true, true, 
                10, UIComponents._DATEFIELD, getIndex(), DcRepository.ValueTypes._DATE,
                "Modified"));
        addField(new DcField(DcMapping._A_PARENT_ID, getIndex(), "Object ID",
                 false, true, false, false, 
                 36, UIComponents._SHORTTEXTFIELD, parentMod.getIndex(), DcRepository.ValueTypes._STRING,
                 "ObjectID"));
        addField(new DcField(DcMapping._B_REFERENCED_ID, getIndex(), "Referenced ID",
                 false, true, false, false, 
                 36, UIComponents._SHORTTEXTFIELD, referencedMod.getIndex(), DcRepository.ValueTypes._STRING,
                 "ReferencedId"));
    }
    
    @Override
    public boolean equals(Object o) {
        return (o instanceof MappingModule ? ((MappingModule) o).getIndex() == getIndex() : false);
    }     
}