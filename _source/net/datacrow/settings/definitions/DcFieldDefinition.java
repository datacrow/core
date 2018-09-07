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

import net.datacrow.core.DcRepository;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.utilities.CoreUtilities;

public class DcFieldDefinition extends Definition {

	private static final long serialVersionUID = -2251987748376443119L;
	
	private int module;
    private int index;
    
    private boolean required;
    private boolean enabled;
    private boolean unique;
    private boolean descriptive;
    
    private String label;
    private String tab;
    
    public DcFieldDefinition(int module,
                             int index, 
                             String label, 
                             boolean enabled, 
                             boolean required, 
                             boolean descriptive, 
                             boolean unique,
                             String tab) {
        
        this.module = module;
        this.index = index;
        this.required = required;
        this.enabled = enabled;
        this.descriptive = descriptive;
        this.label = label == null || "null".equalsIgnoreCase(label) ? "" : label;
        this.unique = unique;
        this.tab = tab;
    }
    
    public void setIndex(int index) {
        this.index = index;
    }
    
    public int getIndex() {
        return index;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public boolean isEnabled() {
        return enabled;
    }    

    public void isEnabled(boolean b) {
        this.enabled = b;
    }  
    
    public String getTabNative() {
        return tab;
    }
    
    public void setRequired(boolean required) {
        this.required = required;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setDescriptive(boolean descriptive) {
        this.descriptive = descriptive;
    }

    public String getTab() {
        return tab;
    }
    
    public String getTab(int module) {
        if (CoreUtilities.isEmpty(tab)) {
            DcField field = DcModules.get(module).getField(index);
            
            if ((!field.isUiOnly() || field.getValueType() == DcRepository.ValueTypes._DCOBJECTCOLLECTION) && 
                  field.isEnabled() && 
                  field.getValueType() != DcRepository.ValueTypes._PICTURE && // check the field type
                  field.getValueType() != DcRepository.ValueTypes._ICON &&
                 (index != DcModules.get(module).getParentReferenceFieldIndex() || 
                  index == DcObject._SYS_CONTAINER )) { // not a reference field
                
                tab = "lblInformation";
            }
        }
        
        if (tab != null && !tab.startsWith("lbl"))
            tab = DcResources.getText("lbl" + tab) != null ? DcResources.getText("lbl" + tab) : tab;
        
        return tab != null && tab.startsWith("lbl") ? DcResources.getText(tab) : tab;
    }
    
    public int getModule() {
        return module;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

    public boolean isRequired() {
        return required;
    }
    
    public boolean isDescriptive() {
        return descriptive;
    }
    
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    
    @Override
    public String toSettingValue() {
        return index +  
               "/&/" + (label == null || label.length() == 0 ? "null" : label) + 
               "/&/" + enabled + "/&/" + required + "/&/" + descriptive + 
               "/&/" + unique + "/&/" + (tab == null || tab.length() == 0 ? "null" : tab);
    }
    
    @Override
    public int hashCode() {
        return index + (module * 10000);
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof DcFieldDefinition) {
            DcFieldDefinition def = (DcFieldDefinition) o;
            return def.getIndex() == getIndex();
        }
        return false;
    }
}
