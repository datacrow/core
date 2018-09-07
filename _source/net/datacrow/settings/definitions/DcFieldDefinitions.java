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

package net.datacrow.settings.definitions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import net.datacrow.core.DcRepository;
import net.datacrow.core.modules.DcModules;


public class DcFieldDefinitions implements IDefinitions {
    
	private static final long serialVersionUID = 7631062844554482317L;

	private java.util.List<DcFieldDefinition> definitions = new ArrayList<DcFieldDefinition>();
    private final int module;
    
    public DcFieldDefinitions(int module) {
        this.module = module;
    }
    
    @Override
    public void add(Definition definition) {
        if (!exists(definition))
            definitions.add((DcFieldDefinition) definition);
    }

    @Override
    public void add(Collection<Definition> c) {
        for (Definition definition : c)
            add(definition);
    }

    @Override
    public void clear() {
        definitions.clear();
    }

    @Override
    public int getSize() {
        return definitions.size();
    }
    
    @Override
    public List<DcFieldDefinition> getDefinitions() {
        return definitions;
    }    
    
    public DcFieldDefinition get(int field) {
        for (DcFieldDefinition definition : definitions) {
            if (definition.getIndex() == field) 
                return definition;
        }
        return null;
    }
    
    private void removeDefinition(int field) {
        DcFieldDefinition def = null;
        for (DcFieldDefinition definition : definitions)
            def = definition.getIndex() == field ? definition : def;
        
    	if (def != null)
    		definitions.remove(def);
    }
    
    /**
     * Other settings depend on the global field settings:
     * - Quick view settings
     * - Table column order
     */
    public void checkDependencies() {
        
        if (module == -1 || !DcModules.get(module).isTopModule())
            return;
        
        int[] columnOrder = DcModules.get(module).getSettings().getIntArray(DcRepository.ModuleSettings.stTableColumnOrder);
        
        QuickViewFieldDefinitions qvDefs = (QuickViewFieldDefinitions) DcModules.getCurrent().getSetting(DcRepository.ModuleSettings.stQuickViewFieldDefinitions);
        
        Collection<Integer> c = new ArrayList<Integer>();
        for (int column : columnOrder) {
            for (DcFieldDefinition definition : getDefinitions()) {
                if (definition.getIndex() == column && definition.isEnabled())
                    c.add(Integer.valueOf(column));
            }
        }
        
        columnOrder = new int[c.size()];
        int i = 0;
        for (Integer field : c) 
            columnOrder[i++] = field.intValue();
        
        DcModules.getCurrent().setSetting(DcRepository.ModuleSettings.stTableColumnOrder, columnOrder);
        
        QuickViewFieldDefinitions qvDefsNew = new QuickViewFieldDefinitions(DcModules.getCurrent().getIndex());
        for (QuickViewFieldDefinition qvDef : qvDefs.getDefinitions()) {
            for (DcFieldDefinition definition : getDefinitions()) {
                if (qvDef.getField() == definition.getIndex() && definition.isEnabled())
                    qvDefsNew.add(qvDef);
            }
        }
        
        DcModules.getCurrent().setSetting(DcRepository.ModuleSettings.stQuickViewFieldDefinitions, qvDefsNew);
    }
    
    @Override
    public boolean exists(Definition definition) {
        return definitions.contains(definition);
    }

    @Override
    public void add(String s) {
        StringTokenizer st = new StringTokenizer(s, "/&/");
        Collection<String> c = new ArrayList<String>();
        while (st.hasMoreTokens())
            c.add((String) st.nextElement());
        
        String[] values = c.toArray(new String[0]);
        
        int field = Integer.valueOf(values[0]).intValue();
        
        String name = values[1] == null || values[1].toLowerCase().equals("null") ? "" : values[1];
        boolean enabled = Boolean.valueOf(values[2]).booleanValue();
        boolean required = Boolean.valueOf(values[3]).booleanValue();
        boolean descriptive = Boolean.valueOf(values[4]).booleanValue();
        boolean unique = values.length >= 6 ? Boolean.valueOf(values[5]).booleanValue() : false;
        String tab = values.length < 7 || values[6] == null || values[6].equalsIgnoreCase("null") ? "" : values[6];
        
        if (module == DcModules._SOFTWARE && field == 7)
            enabled = false;
        
        removeDefinition(field);
    	add(new DcFieldDefinition(module, field, name, enabled, required, descriptive, unique, tab));	
    }
}