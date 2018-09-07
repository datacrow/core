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

package net.datacrow.core.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import net.datacrow.core.DcConfig;
import net.datacrow.core.DcRepository;
import net.datacrow.core.console.UIComponents;
import net.datacrow.core.enhancers.IValueEnhancer;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.modules.xml.XmlField;
import net.datacrow.core.resources.DcResources;
import net.datacrow.settings.definitions.DcFieldDefinition;
import net.datacrow.settings.definitions.DcFieldDefinitions;

/**
 * Fields are part of a Data Crow module. A field defines how it is represented in the
 * UI, which label is used to describe it, if it can be searched on, if it is editable, 
 * the maximum length of its content and so on.
 * 
 * @author Robert Jan van der Waals
 */
public class DcField implements Serializable {

    private static final long serialVersionUID = -3426157253979375896L;
    
    private String systemName;
    private String label;
    private boolean searchable;
    private boolean readonly;
    private boolean uiOnly;
    private int maximumLength;
    private int fieldType;
    private int valueType;
    private int index;
    private int module;
    private int sourceModuleIdx;
    private String databaseFieldName;
    private String resourceKey;
    private boolean enabled = true;
    
    private Collection<IValueEnhancer> enhancers = new ArrayList<IValueEnhancer>();

    /**
     * Creates a new field based on a XML definition.
     * @param field XML definition.
     * @param module The module index to which this field belongs.
     */
    public DcField(XmlField field, int module) {
        this(field.getIndex(), module, field.getName(), field.isUiOnly(), field.isEnabled(),
             field.isReadonly(), field.isSearchable(), field.getMaximumLength(),
             field.getFieldType(), field.getModuleReference(), field.getValueType(), field.getColumn());
    }
    
    /**
     * Creates a new field.
     * @param index The unique field index.
     * @param module The module to which this field belongs.
     * @param label The display label.
     * @param uiOnly Indicates if this field is represented by a database column.
     * @param enabled Indicates if the field will be used. Can be overridden by the user.
     * @param readonly Indicates if the field can be edited.
     * @param searchable Tells if the user can search on this field.
     * @param techinfo Holds technical information?
     * @param maximumLength The maximum value length.
     * @param fieldType The (component) field type.
     * @param modRef The module reference.
     * @param valueType The value type {@link DcRepository.ValueTypes}
     * @param databaseFieldName The database column name.
     */
    public DcField( int index,
                    int module,
                    String label,
                    boolean uiOnly,
                    boolean enabled,
                    boolean readonly,
                    boolean searchable,
                    int maximumLength,
                    int fieldType,
                    int modRef,
                    int valueType,
                    String databaseFieldName) {

        this.enabled = enabled;
        
        setIndex(index);
        setModule(module);
        setLabel(label);
        setSystemName(label);
        setReadOnly(readonly);
        setSearchable(searchable);
        setFieldType(fieldType);
        setMaximumLength(maximumLength);
        setValueType(valueType);
        setDatabaseFieldName(databaseFieldName);
        setUiOnly(uiOnly);
        setSourceModuleIdx(modRef);
    }
    
    public boolean isLoanField() {
        return
            getIndex() == DcObject._SYS_AVAILABLE ||
            getIndex() == DcObject._SYS_LENDBY ||
            getIndex() == DcObject._SYS_LOANSTATUS ||
            getIndex() == DcObject._SYS_LOANSTATUSDAYS ||
            getIndex() == DcObject._SYS_LOANENDDATE ||
            getIndex() == DcObject._SYS_LOANSTARTDATE ||
            getIndex() == DcObject._SYS_LOANDUEDATE ||
            getIndex() == DcObject._SYS_LOANALLOWED ||
            getIndex() == DcObject._SYS_LOANDURATION;
    }
    
    /**
     * Sets the source module index.
     * @param modRef The module index.
     */
    public void setSourceModuleIdx(int modRef) {
        this.sourceModuleIdx = modRef;
    }

    /**
     * The source module index.
     */
    public int getSourceModuleIdx() {
        return sourceModuleIdx;
    }

    /**
     * The module reference index.
     */
    public int getReferenceIdx() {
        try {
            return DcModules.getReferencedModule(this).getIndex();
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Sets the unique field index.
     * @param index
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * The unique field index.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Set the module to which this field belongs.
     * @param module The module index.
     */
    public void setModule(int module) {
        this.module = module;
    }

    /**
     * The module to which this field belongs.
     */
    public int getModule() {
        return module;
    }

    /**
     * When a field is marked as UI only its value will not be stored in the database.
     * @param uiOnly
     */
    public void setUiOnly(boolean uiOnly) {
        this.uiOnly = uiOnly;
    }

    /**
     * When a field is marked as UI only its value will not be stored in the database.
     */
    public boolean isUiOnly() {
        return uiOnly;
    }
    
    public boolean isSystemField() {
        return getSystemName().endsWith("_persist") || getIndex() == DcObject._ID;
    }

    /**
     * Indicates if the field is enabled. Depends on both the settings and the permissions
     * of the user.
     */
    public boolean isEnabled() {
        
        if (DcModules.get(getModule()) == null ||
            DcModules.get(getModule()).getSettings() == null)
            return false;
        
        boolean authorized = 
                DcConfig.getInstance().getConnector() == null ||
                DcConfig.getInstance().getConnector().getUser() == null ||
                DcConfig.getInstance().getConnector().getUser().isAuthorized(this);
        
        if (!authorized) {
            return false;
        } else {
            DcFieldDefinitions definitions = (DcFieldDefinitions) 
                DcModules.get(getModule()).getSettings().getDefinitions(DcRepository.ModuleSettings.stFieldDefinitions);

            if (definitions != null) {
                for (DcFieldDefinition definition : definitions.getDefinitions()) {
                    if (definition.getIndex() == getIndex()) 
                        return definition.isEnabled();
                }
            }
        }
        
        // if the field definitions do not specify anything, the system default applies 
        return enabled;
    }

    /**
     * Indicate if the user is allowed to search on this field.
     * @param searchable
     */
    public void setSearchable(boolean searchable) {
        this.searchable = searchable;
    }

    /**
     * Set the database column name.
     */
    public String getDatabaseFieldName() {
        return databaseFieldName;
    }

    /**
     * The component type.
     */
    public int getFieldType() {
        return fieldType;
    }

    /**
     * The value type.
     * @see DcRepository.ValueTypes
     */
    public int getValueType() {
        return valueType;
    }

    public String getOriginalLabel() {
        return label;
    }
    
    /**
     * The display label. 
     * - If the field definitions (field settings) have a custom label defined this value will be used.
     * - If not the field label will be retrieved using the resources.
     * - Else the original label will be used.
     */
    public String getLabel() {
        String s = null;
        
        if (DcModules.get(module) != null) {
            DcFieldDefinitions definitions = DcModules.get(module).getFieldDefinitions();
            if (definitions != null && definitions.get(getIndex()) != null)
                s = definitions.get(getIndex()).getLabel();
        }
        
        if (s != null && s.trim().length() > 0)
            return s;

        if (DcModules.get(module) != null &&
            DcResources.getText(getResourceKey()) != null &&
            DcResources.getText(getResourceKey()).length() > 0)
            
            return DcResources.getText(getResourceKey());

        return label;    
    }
    
    public DcFieldDefinition getDefinition() {
        return DcModules.get(module).getFieldDefinitions().get(getIndex());
    }
    
    /**
     * The key used for setting the value in the resources.
     */
    public String getResourceKey() {
        resourceKey = resourceKey == null ?
                      DcModules.get(module).getModuleResourceKey() + "Field" + getDatabaseFieldName() : resourceKey;

        return resourceKey;
    }

    /**
     * The system name of this field.
     */
    public String getSystemName() {
        return systemName;
    }

    /**
     * Mark the field as required.
     */
    public boolean isRequired() {
        if (DcConfig.getInstance().getConnector() == null ||
            DcConfig.getInstance().getConnector().getUser() == null ||
            DcConfig.getInstance().getConnector().getUser().isAuthorized(this)) {
            
            if (DcModules.get(getModule()) == null ||
                DcModules.get(getModule()).getSettings() == null)
                return false;
            
            DcFieldDefinitions definitions = (DcFieldDefinitions) 
                DcModules.get(getModule()).getSettings().getDefinitions(DcRepository.ModuleSettings.stFieldDefinitions);
            
            if (definitions != null) {
                for (DcFieldDefinition definition : definitions.getDefinitions()) {
                    if (definition.getIndex() == getIndex()) 
                        return definition.isRequired();
                }
            }
        }
        return false;
    }

    /**
     * Indicates if the value belonging to this field can be edited.
     * Depends on both the settings and the permissions of the user.
     */
    public boolean isReadOnly() {
        if (    DcConfig.getInstance().getConnector() != null && 
                DcConfig.getInstance().getConnector().getUser() != null &&
                !DcConfig.getInstance().getConnector().getUser().isEditingAllowed(this))
            return true;

        return readonly;
    }

    /**
     * Indicates if the user can search on this field.
     * @return
     */
    public boolean isSearchable() {
        return searchable;
    }

    /**
     * Returns the maximum field length (characters positions).
     * In case the field is of type long text field the maximum value will be
     * the maximum integer (Integer.MAX_VALUE) value (maximum field setting is thus overruled).
     */
    public int getMaximumLength() {
    	if (maximumLength == 0 && getValueType() == DcRepository.ValueTypes._STRING) 
    	    maximumLength = getFieldType() == UIComponents._LONGTEXTFIELD ? 0 : 255;
    	
    	return maximumLength;
    }

    /**
     * The display label.
     */
    private void setLabel(String s) {
        label = s;
    }

    /**
     * The system name.
     */
    private void setSystemName(String s) {
        systemName = s;
    }

    /**
     * Indicate if the value of this field can edited.
     */
    public void setReadOnly(boolean b) {
        readonly = b;
    }

    /**
     * The maximum length of the value of this field.
     * @param i
     */
    public void setMaximumLength(int i) {
        maximumLength = i;
    }

    /**
     * Sets the component type.
     * @param index
     */
    public void setFieldType(int index) {
        fieldType = index;
    }

    /**
     * Sets the value type.
     * @see DcRepository.ValueTypes
     * @param index
     */
    public void setValueType(int index) {
        valueType = index;
    }

    /**
     * Sets the database column name.
     * @param s
     */
    public void setDatabaseFieldName(String s) {
        databaseFieldName = s;
    }

    @Override
    public String toString() {
        return getLabel();
    }
    
    /**
     * Remove all the registered value enhancers.
     */
    public void removeEnhancers() {
        enhancers.clear();
    }
    
    /**
     * Register a new value enhancer.
     * @param enhancer
     */
    public void addValueEnhancer(IValueEnhancer enhancer) {
        enhancers.add(enhancer);
    }
    
    /**
     * Retrieves all the registered value enhancers.
     * @return
     */
    public IValueEnhancer[] getValueEnhancers() {
        return enhancers.toArray(new IValueEnhancer[0]);
    }
    
    /**
     * Calculates the database field type definition.
     */
    public String getDataBaseFieldType() {
        String s = "";
        
        if (getValueType() == DcRepository.ValueTypes._DCOBJECTREFERENCE ||
            getValueType() == DcRepository.ValueTypes._DCPARENTREFERENCE ||
            getValueType() == DcRepository.ValueTypes._DCOBJECTCOLLECTION) {
            
            s = DcRepository.Database._FIELDSTRING + "(36)";
            
        } else if (getValueType() == DcRepository.ValueTypes._STRING) {
            if (getFieldType() == UIComponents._LONGTEXTFIELD)
                s = DcRepository.Database._FIELDOBJECT;
            else
                s = DcRepository.Database._FIELDSTRING + "(" + getMaximumLength() + ")";
        } else if (getValueType() == DcRepository.ValueTypes._DOUBLE) {
            s = DcRepository.Database._FIELDNUMERIC + "(10, 2)";
        } else if (getValueType() == DcRepository.ValueTypes._BIGINTEGER ||
                   getValueType() == DcRepository.ValueTypes._LONG) {
            s = DcRepository.Database._FIELDBIGINT;
        } else if (getValueType() == DcRepository.ValueTypes._ICON ||
                   getValueType() == DcRepository.ValueTypes._PICTURE ||
                   getValueType() == DcRepository.ValueTypes._BLOB) {
            s = DcRepository.Database._FIELDOBJECT;
        } else if (getValueType() == DcRepository.ValueTypes._BOOLEAN) {
            s = DcRepository.Database._FIELDBOOLEAN;
        } else if (getValueType() == DcRepository.ValueTypes._DATE) {
            s = DcRepository.Database._FIELDDATE;
        } else if (getValueType() == DcRepository.ValueTypes._DATETIME) {
            s = DcRepository.Database._FIELDDATE;
        }
        return s;
    }
}
