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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.KeyStroke;

import net.datacrow.core.DcConfig;
import net.datacrow.core.DcRepository;
import net.datacrow.core.console.IMasterView;
import net.datacrow.core.console.UIComponents;
import net.datacrow.core.enhancers.IValueEnhancer;
import net.datacrow.core.modules.xml.XmlField;
import net.datacrow.core.modules.xml.XmlModule;
import net.datacrow.core.objects.DcAssociate;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcImageIcon;
import net.datacrow.core.objects.DcMediaObject;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.helpers.Container;
import net.datacrow.core.objects.helpers.Item;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.security.SecuredUser;
import net.datacrow.core.server.Connector;
import net.datacrow.core.services.Servers;
import net.datacrow.core.utilities.CoreUtilities;
import net.datacrow.settings.DcModuleSettings;
import net.datacrow.settings.definitions.DcFieldDefinition;
import net.datacrow.settings.definitions.DcFieldDefinitions;
import net.datacrow.settings.definitions.QuickViewFieldDefinition;
import net.datacrow.settings.definitions.QuickViewFieldDefinitions;

import org.apache.log4j.Logger;

/**
 * A module represents items. The module dictates among other things where the 
 * items are stored, which views they can be displayed, which fields they have and 
 * which online services are available to update or search for new items.
 * The module is where it all comes together. <br>
 * Modules can have relationships between each other and come in different types. An
 * important property of the module is the top module boolean which indicates if the
 * module can be displayed within module bar and if the module can be disabled and or
 * enabled by the user.<br>
 * Furthermore is it important to know that modules are generic. They can be created
 * from an {@link XmlModule} which holds a flexible XML definition of the module.<br>
 * The {@link DcModules} class creates and holds all modules.
 * 
 * @see XmlModule
 * @see DcModules
 * @see DcPropertyModule
 * @see DcMediaModule
 * @see DcChildModule
 * @see DcMediaChildModule
 * @see DcParentModule
 * @see DcMediaParentModule  
 * 
 * @author Robert Jan van der Waals
 */
public class DcModule implements Comparable<DcModule>, Serializable {

	private static final long serialVersionUID = 154180107544049967L;

	private transient static Logger logger = Logger.getLogger(DcModule.class.getName());
    
    public static final int _TYPE_MODULE = 0;
    public static final int _TYPE_PROPERTY_MODULE = 1;
    public static final int _TYPE_MEDIA_MODULE = 2;
    public static final int _TYPE_ASSOCIATE_MODULE = 3;
    public static final int _TYPE_EXTERNALREFERENCE_MODULE = 4;
    public static final int _TYPE_MAPPING_MODULE = 5;
    public static final int _TYPE_TEMPLATE_MODULE = 6;

    private final int type;
    private final int index;
    
    private int displayIndex;
    private int defaultSortFieldIdx;
    private int nameFieldIdx;
    
    private String label;
    
    private final String name;
    private final String tableName;
    private final String tableShortName;
    private final String description;
    
    private final String moduleResourceKey;
    private final String itemResourceKey;
    private final String itemPluralResourceKey;

    private final String systemObjectName;
    private final String systemObjectNamePlural;
    
    private final String objectName;
    private final String objectNamePlural;
    
    private boolean isValid = true;
    private boolean isNew = false;
    private boolean isDefaultDataLoaded = false;
    
    private net.datacrow.settings.Settings settings;
    
    private Class objectClass;
    
    private int childIdx = -1;
    private int parentIdx = -1;
    
    private boolean hasSearchView = true;
    private boolean hasInsertView  = true;
    
    private boolean isServingMultipleModules = false;
    
    private byte[] icon16;
    private byte[] icon32;
    
    private String icon16filename;
    private String icon32filename;

    protected Map<Integer, DcField> fields = new LinkedHashMap<Integer, DcField>();
    private Map<Integer, DcField> systemFields = new LinkedHashMap<Integer, DcField>();
    
    private Collection<DcField> sortedFields;

    private KeyStroke keyStroke;
    
    private boolean hasOnlineServices = false;
    private boolean canBeLended = false;
    private boolean topModule = false;
    private boolean isFileBacked = false;
    private boolean isContainerManaged = false;
    private boolean hasDependingModules = true;
    
    private boolean hasImages = false;
    private boolean hasReferences = false;
    
    private XmlModule xmlModule;
    
    /**
     * Creates a new instance.
     * @param index The module index.
     * @param name The internal unique name of the module.
     * @param description The module description
     * @param objectName The name of the items belonging to this module.
     * @param objectNamePlural The plural name of the items belonging to this module.
     * @param tableName The database table name for this module.
     * @param tableShortName The database table short name for this module.
     * @param topModule Indicates if the module is a top module. Top modules are allowed
     * to be displayed in the module bar and can be enabled or disabled.
     */
    protected DcModule(int index, 
                       String name,
                       String description,
                       String objectName,
                       String objectNamePlural,
                       String tableName, 
                       String tableShortName, 
                       boolean topModule) { 

        this.index = index;
        
        this.tableName = (tableName == null ? "" : tableName).toLowerCase();
        this.tableShortName  = (tableShortName == null ? (tableName != null ? tableName.substring(0, (tableName.length() > 5 ? 4 : 2)) : "") : tableShortName).toLowerCase();
        this.name = name;
        this.label = name;
        this.description = description;
        
        this.topModule = topModule;

        this.systemObjectName = objectName;
        this.systemObjectNamePlural = objectNamePlural;

        this.objectName = objectName;
        this.objectNamePlural = objectNamePlural;
        
        String s = isAbstract() ? label : tableName;
        if (s != null && s.length() > 1) s = s.substring(0, 1).toUpperCase() + s.substring(1);
        
        this.moduleResourceKey = "sys" + s;
        this.itemResourceKey = moduleResourceKey + "Item";
        this.itemPluralResourceKey = moduleResourceKey + "ItemPlural";
        
        // lower level determination of the type of module; avoiding instanceof calls in the future
        this.type = 
            this instanceof DcAssociateModule ? _TYPE_ASSOCIATE_MODULE :
            this instanceof MappingModule ? _TYPE_MAPPING_MODULE :
            this instanceof DcPropertyModule ? _TYPE_PROPERTY_MODULE :
            this instanceof DcMediaModule ? _TYPE_MEDIA_MODULE :
            this instanceof TemplateModule ? _TYPE_TEMPLATE_MODULE :
            this instanceof ExternalReferenceModule ? _TYPE_EXTERNALREFERENCE_MODULE :
            _TYPE_MODULE;        
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
    public DcModule(int index, 
                    boolean topModule, 
                    String name,
                    String description,
                    String objectName,
                    String objectNamePlural,
                    String tableName, 
                    String tableShortName) {

        this(index, name, description, objectName, objectNamePlural, tableName, 
             tableShortName, topModule);
        
        initializeSystemFields();
        initializeFields();
        initializeMultiReferenceFields();
        initializeSettings();
        initializeProperties();
    }
    
	public boolean hasInsertView() {
		return hasInsertView;
	}

	public boolean hasSearchView() {
		return hasSearchView;
	}
	
	public boolean hasOnlineServices() {
	    if (Servers.getInstance().isInitialized()) // the server class has been instantiated
	        return Servers.getInstance().hasOnlineService(getIndex());
	    else // if not, the setting is based on the module setting
	    	return hasOnlineServices;
	}

	/**
     * Creates a new module based on a XML definition.
     * @param module
     */
    public DcModule(XmlModule module) {
        this(module.getIndex(), module.getName(), module.getDescription(), module.getObjectName(), 
             module.getObjectNamePlural(), module.getTableName(), module.getTableNameShort(),
             true);

        this.xmlModule = module;
        
        childIdx = module.getChildIndex();
        parentIdx = module.getParentIndex();
        label = module.getLabel();
        
        hasOnlineServices = module.hasOnlineServices();
        isFileBacked = module.isFileBacked();
        isContainerManaged = module.isContainerManaged();
        hasInsertView = module.hasInsertView();
        hasSearchView = module.hasSearchView();
        keyStroke = module.getKeyStroke();
        objectClass = module.getObjectClass();
        hasDependingModules = module.hasDependingModules();
        
        icon16filename = module.getIcon16Filename();
        icon32filename = module.getIcon32Filename();
        icon16 = module.getIcon16();
        icon32 = module.getIcon32();
        
        nameFieldIdx = module.getNameFieldIdx();
        canBeLended = module.canBeLend();
        
        displayIndex = module.getDisplayIndex();
        defaultSortFieldIdx = module.getDefaultSortFieldIdx();
        
        initializeSystemFields();
        
        if (module.getChildIndex() > -1)
            setChild(module.getChildIndex());
        
        for (XmlField xmlField : module.getFields())
            addField(new DcField(xmlField, getIndex()));
        
        initializeFields();
        initializeMultiReferenceFields();
        initializeSettings();
        initializeProperties();

        setServingMultipleModules(module.isServingMultipleModules());
        
        // Set it to disabled only if the XML module is defined as disabled.
        // There is no use case for this (yet).
        if (!module.isEnabled()) isEnabled(false);
    }

    public int getType() {
        return type;
    }
    
    private void initializeProperties() {
        for (DcField field : getFields()) {
            if (field.getValueType() == DcRepository.ValueTypes._PICTURE)
                hasImages = true;
            else if (field.getValueType() == DcRepository.ValueTypes._DCOBJECTCOLLECTION ||
                    field.getValueType() == DcRepository.ValueTypes._DCOBJECTREFERENCE)
                hasReferences = true;
        }
    }
    
    public boolean isHasImages() {
        return hasImages;
    }

    public boolean isHasReferences() {
        return hasReferences;
    }

    public DcField getIconField() {
        for (DcField field : getFields()) {
            if (field.getValueType() == DcRepository.ValueTypes._ICON)
                return field;
        }
        return null;
    }
    
    /**
     * Indicates if the module is abstract. An abstract module represents items belonging
     * to other modules; it represents items from multiple modules. An abstract module does
     * not dictate where the items should be stored in the database, this is done by its
     * actual module. The media module is a good example of an abstract module. 
     */
    public boolean isAbstract() {
        return getIndex() == DcModules._ITEM ||
               getIndex() == DcModules._MEDIA;
    }
    
    public int[] getSupportedViews() {
        return isAbstract() ? new int[] {IMasterView._LIST_VIEW} : new int[] {IMasterView._LIST_VIEW, IMasterView._TABLE_VIEW};
    }
    
    /**
     * Returns whether this module is new for this installation.
     * New means that it was registered on startup.
     */
    public boolean isNew() {
        return isNew;
    }
    
    /**
     * Indicate this module as new for this installation
     * @param isNew
     */
    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    /**
     * Indicate if this module is valid (fully functional).
     * Some processes register a new module temporarily. Since in that case the table has not been
     * created yet or not all referenced exist the module is in an invalid state.
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Indicate if this module is valid (fully functional).
     * Some processes register a new module temporarily. Since in that case the table has not been
     * created yet or not all referenced exist the module is in an invalid state.
     * @param isValid
     */
    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }

    public boolean isDefaultDataLoaded() {
        return isDefaultDataLoaded;
    }

    public void setDefaultDataLoaded(boolean b) {
        this.isDefaultDataLoaded = b;
    }

    /**
     * Indicates if the user is allowed to edit items belonging to this module.
     */
    public boolean isEditingAllowed() {
        return DcConfig.getInstance().getConnector().getUser().isEditingAllowed(this);
    }

    /**
     * Indicates if this module is a child of another module.
     */
    public boolean isChildModule() {
        return getParent() != null;
    }
    
    /**
     * Indicates if this module is a parent to another module.
     */
    public boolean isParentModule() {
        return getChild() != null;
    }    
    
    /**
     * Retrieves the index of the module (unique!)
     */
    public int getIndex() {
        return index;
    }    

    /**
     * The small icon used to represent the module.
     */
    public DcImageIcon getIcon16() {
    	return icon16 != null ? new DcImageIcon(icon16) : null;
    }
    
    /**
     * The large icon used to represent the module.
     */
    public DcImageIcon getIcon32() {
        return icon32 != null ? new DcImageIcon(icon32) : null;
    }

    public String getModuleResourceKey() {
        return moduleResourceKey;
    }
    
    public String getItemResourceKey() {
        return itemResourceKey;
    }

    public String getItemPluralResourceKey() {
        return itemPluralResourceKey;
    }
    
    /**
     * The name of the items belonging to this module.
     */
    public String getObjectName() {
        return DcResources.getText(getItemResourceKey()) != null ? DcResources.getText(getItemResourceKey()) : objectName;
    }
    
    /**
     * The name of the items belonging to this module without translating it.
     */
    public String getSystemObjectName() {
        return systemObjectName;
    }    
    
    /**
     * The plural name of the items belonging to this module.
     */
    public String getObjectNamePlural() {
        if (DcResources.getText(getItemPluralResourceKey()) != null)
            return DcResources.getText(getItemPluralResourceKey());

        return objectNamePlural;
    }

    /**
     * The plural name of the items belonging to this module without translating it.
     */
    public String getSystemObjectNamePlural() {
        return systemObjectNamePlural;
    }
    
    
    /**
     * The keys combination associated with this module.
     */
    public KeyStroke getKeyStroke() {
        return keyStroke;
    }    

    /**
     * Sets the small icon used to represent this module.
     * @param icon16
     */
    public void setIcon16(DcImageIcon icon16) {
        this.icon16 = icon16.getBytes();
    }

    /**
     * Sets the large icon used to represent this module.
     * @param icon16
     */
    public void setIcon32(DcImageIcon icon32) {
        this.icon32 = icon32.getBytes();
    }
    
    /**
     * Tells whether the module is enabled.
     * @see DcModuleSettings
     */
    public boolean isEnabled() {
    	SecuredUser su = DcConfig.getInstance().getConnector().getUser();
    	
        if (su == null || su.isAuthorized(this)) 
            return settings.getBoolean(DcRepository.ModuleSettings.stEnabled);

        return false;
    }

    /**
     * Marks the module as enabled or disabled.
     * @see DcModuleSettings
     */
    public void isEnabled(boolean b) {
        settings.set(DcRepository.ModuleSettings.stEnabled, b);
    }    

    /**
     * The name of this module.
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * The description for this module.
     */
    public  String getDescription() {
        return description;
    }    

    /** 
     * The name of the table used to store its items. 
     */
    public String getTableName() {
        return tableName;
    }

    /** 
     * The short name of the table used to store its items. 
     */
    public String getTableShortName() {
        return tableShortName;
    }

    /**
     * Tells if the module is created by a user.
     */
    public boolean isCustomModule() {
        return ((getIndex() < 50 ||
                (getIndex() >= 20000 && getIndex() < 30000)) ||
                 getIndex() >= 10000000) &&
                getType() != DcModule._TYPE_TEMPLATE_MODULE && 
                getType() != DcModule._TYPE_MAPPING_MODULE ;
    } 
    
    /**
     * Creates a new instance of an item belonging to this module.
     */
    protected DcObject createItem() {
        try {
            try {
                return (DcObject) objectClass.getConstructors()[0].newInstance(new Object[] {});    
            } catch (Exception exp) {
                return (DcObject) objectClass.getConstructors()[0].newInstance(new Object[] {Integer.valueOf(getIndex())});
            }
        } catch (Exception e) {
            logger.error("Could not instantiate " + objectClass, e);
        }

        return null;
    }  

    public void release(DcObject dco) {
        dco.destroy();
    }
    
    /**
     * Creates a new instance of an item belonging to this module.
     */
    public synchronized final DcObject getItem() {
        return createItem();
    }  
    
    public DcField getFileField() {
        if (isFileBacked())
            return getField(DcObject._SYS_FILENAME);

        for (DcField field : getFields()) {
            if (field.getFieldType() == UIComponents._FILELAUNCHFIELD ||
                field.getFieldType() == UIComponents._FILEFIELD)
                return field;
        }
        
        return null;
   }
    
    public int getSystemDisplayFieldIdx() {
        return getDisplayFieldIdx();
    }
    
    /**
     * Educated guess..
     */
    public int getDisplayFieldIdx() {
        for (DcFieldDefinition definition : getFieldDefinitions().getDefinitions()) {
            if (definition.isDescriptive())
                return definition.getIndex();
        }
        return getDefaultSortFieldIdx();
    }
    
    public int[] getMinimalFields(Collection<Integer> include) {
        Collection<Integer> fields = new ArrayList<Integer>();
        for (DcFieldDefinition definition : getFieldDefinitions().getDefinitions())
            if (definition.isDescriptive() && definition.isEnabled()) 
                fields.add(Integer.valueOf(definition.getIndex()));
            
        if (!fields.contains(Integer.valueOf(getSystemDisplayFieldIdx())))
            fields.add(Integer.valueOf(getSystemDisplayFieldIdx()));
        
        if (include != null) { 
            for (Integer field : include) 
                if (!fields.contains(field)) 
                    fields.add(field);
        }
        
        if (getType() == _TYPE_ASSOCIATE_MODULE) {
            if (!fields.contains(Integer.valueOf(DcAssociate._A_NAME)))
                fields.add(Integer.valueOf(DcAssociate._A_NAME));
            if (!fields.contains(Integer.valueOf(DcAssociate._E_FIRSTNAME)))
                fields.add(Integer.valueOf(DcAssociate._E_FIRSTNAME));
            if (!fields.contains(Integer.valueOf(DcAssociate._F_LASTTNAME)))
                fields.add(Integer.valueOf(DcAssociate._F_LASTTNAME));
        }
        
        if (getIndex() == DcModules._CONTAINER)
            fields.add(Integer.valueOf(Container._F_PARENT));
        
        if (getField(DcObject._ID) != null && !fields.contains(Integer.valueOf(DcObject._ID))) 
            fields.add(Integer.valueOf(DcObject._ID));
        
        if (!fields.contains(Integer.valueOf(DcObject._SYS_DISPLAYVALUE)))
        	fields.add(DcObject._SYS_DISPLAYVALUE);	
        
        DcField iconField = getIconField();
        if (iconField != null)
            fields.add(iconField.getIndex());
        
        int[] result = new int[fields.size()];
        int i = 0;
        for (Integer field : fields)
            result[i++] = field.intValue();
        
        return result;
    }

    /**
     * Retrieve the property module for the given index used by this module.
     * @param modIdx
     */
    public DcPropertyModule getPropertyModule(int modIdx) {
        return (DcPropertyModule) DcModules.get(getIndex() + modIdx);
    }

    /**
     * Returns the template module.
     * @return Template module or null.
     */
    public TemplateModule getTemplateModule() {
        if ((isTopModule() || isChildModule()) && !isAbstract())
            return (TemplateModule) DcModules.get(getIndex() + DcModules._TEMPLATE);

        return null;
    }
    
    /**
     * Indicates if the module can be selected from the module bar.
     */
    public boolean isSelectableInUI() {
        return getIndex() == DcModules._CONTACTPERSON || 
               getIndex() == DcModules._MEDIA || 
              (isTopModule() && isEnabled() && !hasDependingModules());
    }

    /**
     * Adds a field to this module.
     * @param field
     */
    public void addField(DcField field) {
        fields.put(field.getIndex(), field);
    }
    
//    /**
//     * Returns all views.
//     */
//    public IMasterView[] getViews() {
//        if (getSearchView() != null && getInsertView() != null)
//            return new IMasterView[] {getSearchView(), getInsertView()};
//        else if (getSearchView() != null)
//            return new IMasterView[] {getSearchView()};
//        else if (getInsertView() != null)
//            return new IMasterView[] {getInsertView()};
//        else
//        	return new IMasterView[0];
//    }

    /**
     * Retrieves the field definition for the given index.
     * @param index The field index.
     */
    public DcField getField(int index) {
        DcField field = fields.get(index);
        return field == null ? getSystemField(index) : field;
    }
    
    /**
     * Retrieves the field definition for the given index.
     * @param columnName The database column name.
     */
    public DcField getField(String columnName) {
        for (DcField field : getFields()) {
            if (field.getDatabaseFieldName().equalsIgnoreCase(columnName))
                return field;
        }
        return null;
    }
    
    /**
     * Registers a value enhancer for a specific field. 
     * @param enhancer
     * @param field
     */
    public void addValueEnhancer(IValueEnhancer enhancer, int field) {
        getField(field).addValueEnhancer(enhancer);
    }
    
    /**
     * Removes all enhancers.
     */
    public void removeEnhancers() {
        for (DcField field : fields.values())
            field.removeEnhancers();
    }       
    
    /**
     * Retrieves the system field for the given index.
     * @param index The field index.
     */
    public DcField getSystemField(int index) {
        return systemFields.get(index);
    }

    /**
     * Register a child module.
     * @param module
     */
    public void setChild(int module) {
    	this.childIdx = module;
    }
    
    /**
     * Retrieves the parent module instance.
     * @return The parent module or null if not applicable.
     */
    public DcModule getParent() {
        return parentIdx > 0 ? DcModules.get(parentIdx) : null;
    }
    
    /**
     * Retrieves the child module instance.
     * @return The child module or null if not applicable.
     */
    public DcModule getChild() {
    	return childIdx > 0 ? DcModules.get(childIdx) : null;
    }
    
    /**
     * Retrieves the index for the field holding the reference to the parent item.
     * @return The field index or -1 if not found.
     */
    public int getParentReferenceFieldIndex() {
        for (DcField field : getFields()) {
            if (field.getValueType() == DcRepository.ValueTypes._DCPARENTREFERENCE)
                return field.getIndex();
        }
        
        return -1;
    }

    /**
     * Indicates if this module is used by multiple modules.
     */
    public boolean isServingMultipleModules() {
        return isServingMultipleModules;
    }

    /**
     * Indicates if this module is used by multiple modules.
     * @param isServingMultipleModules
     */
    public void setServingMultipleModules(boolean isServingMultipleModules) {
        this.isServingMultipleModules = isServingMultipleModules;
    }    
    
    /**
     * Indicates if this module is a top module. Top modules are allowed
     * to be displayed in the module bar and can be enabled or disabled.
     */
    public boolean isTopModule() {
        return topModule;
    }

    /**
     * The number of fields belonging to this module.
     */
    public int getFieldCount() {
        return fields.size();
    }

    /**
     * Indicates if items belonging to this module can be lend.
     */
    public boolean canBeLend() {
        return canBeLended &&  
               DcModules.get(DcModules._CONTACTPERSON) != null && 
               DcModules.get(DcModules._CONTACTPERSON).isEnabled();
    }

    /**
     * Indicates if other modules depend on this module.
     */
    public boolean hasDependingModules() {
        return hasDependingModules;    
    }

    /**
     * Retrieves the module setting value.
     * @param key The setting key {@link DcRepository.ModuleSettings}.
     */
    public Object getSetting(String key) {
        return settings == null ? null : settings.get(key);
    }
    
    /**
     * Sets the module setting value.
     * @param key The setting key {@link DcRepository.ModuleSettings}.
     * @param value The value to set.
     */
    public void setSetting(String key, Object value) {
        settings.set(key, value);
    }

    /**
     * Tells if the module holds a reference to the given module.
     * This check is based on the original module index.
     * @param module The module index. 
     */
    public boolean hasReferenceTo(int module) {
        for (DcField field : getFields()) {
            if (field.getSourceModuleIdx() == module)
                return true;
        }
        return false;
    }
    
    /**
     * Tells if the module holds a reference to the given module.
     * This check is based on the calculated module index.
     * @param module The module index. 
     */
    public boolean hasActualReferenceTo(int module) {
        int parentRefIdx = getParentReferenceFieldIndex();
        boolean isMapping = DcModules.get(module).getType() == DcModule._TYPE_MAPPING_MODULE;
        for (DcField field : getFields()) {
            if (field != null && (isMapping || parentRefIdx != field.getIndex()) && field.getReferenceIdx() == module)
                return true;
        }
        return false;
    }

    /**
     * Retrieves all fields. 
     */
    public Collection<DcField> getFields() {
        if (sortedFields == null || sortedFields.size() < fields.size()) {
            sortedFields = new ArrayList<DcField>();
            sortedFields.addAll(fields.values());
            Collections.sort((List<DcField>) sortedFields, new Comparator<DcField>() {
                @Override
                public int compare(DcField fld1, DcField fld2) {
                    return fld1.getOriginalLabel().compareTo(fld2.getOriginalLabel());
                }
            });
        }
        return sortedFields;
    }

    /**
     * Gets the field definitions. The field definitions contain the user settings such as the
     * modified label and the enabled setting.
     * @see DcFieldDefinition
     */
    public DcFieldDefinitions getFieldDefinitions() {
        return (DcFieldDefinitions) getSetting(DcRepository.ModuleSettings.stFieldDefinitions);
    }

    /**
     * Retrieves the quick view field definitions / settings
     * @see QuickViewFieldDefinition
     */    
    public QuickViewFieldDefinitions getQuickViewFieldDefinitions() {
        return (QuickViewFieldDefinitions) getSetting(DcRepository.ModuleSettings.stQuickViewFieldDefinitions);
    }
    
    /**
     * Retrieves all field indices.
     */
    public int[] getFieldIndices() {
        DcFieldDefinitions definitions = getFieldDefinitions();
        int counter = 0;
        int[] indices = null;
        
        Set<Integer> keys = fields.keySet();
        if (definitions == null) {
            indices = new int[keys.size()];
            for (Integer key : keys)
                indices[counter++] = key.intValue();
        } else {
        	// the number of fields between the definitions and module fields should always be the same
        	// it can however happen that the field definitions holds more values due to missing fields (such
        	// as the online search fields in case no online search JARs are available).
            indices = new int[keys.size()];
            for (DcFieldDefinition def : definitions.getDefinitions()) {
            	if (keys.contains(def.getIndex()))
            		indices[counter++] = def.getIndex();
            }

            // final check
        	if (counter != keys.size()) {
        		logger.debug("The number of fields (" + keys.size() + ") in the field definitions (" + counter + ") "
        				+ "does not match the number of actual fields of module " + this);
        	}
        }

        return indices;
    }
    
    /**
     * Indicates if this module is allowed to be customized. 
     */
    public boolean isCustomFieldsAllowed() {
        return true;
    }
    
    /**
     * The module settings.
     * @see DcRepository.ModuleSettings
     */
    public net.datacrow.settings.Settings getSettings() {
        return settings;
    }
    
    /**
     * Indicates if the module is managed by (can belong to) a container.
     * @see ContainerModule
     * @see Container
     */
    public boolean isContainerManaged() {
        return isContainerManaged;
    }
    
    public DcField getPersistentField(int fieldIdx) {
        return getField(fieldIdx + 100000000);
    }
    
    /**
     * Creates a simple reference field for each multiple references field for ordering purposes.
     * It does need to have all modules registered before this method can be called.
     */
    public void initializeMultiReferenceFields() {
        
        if (    getType() == DcModule._TYPE_TEMPLATE_MODULE ||
                getType() == DcModule._TYPE_MAPPING_MODULE ||
                getType() == DcModule._TYPE_EXTERNALREFERENCE_MODULE)
            return;
        
        for (DcField field : getFields()) {
            
            if (field.getValueType() != DcRepository.ValueTypes._DCOBJECTCOLLECTION) 
                continue;
            
            DcField fld = new DcField(
                    field.getIndex() + 100000000, 
                    field.getModule(), 
                    field.getSystemName() + "_persist", 
                    false, 
                    false, 
                    true, 
                    false, 256, 
                    UIComponents._SHORTTEXTFIELD, 
                    field.getModule(), 
                    DcRepository.ValueTypes._STRING,
                    CoreUtilities.toDatabaseName(field.getSystemName()) + "_persist");
            
            addField(fld);
        }
    }
    
    /**
     * Initializes the default fields.
     */
    protected void initializeFields() {
        try {
            addField(new DcField(DcObject._ID, getIndex(), "ID",
                                 false, true, true, false, 
                                 36, UIComponents._SHORTTEXTFIELD, getIndex(), DcRepository.ValueTypes._STRING,
                                 "ID"));
            addField(new DcField(DcObject._SYS_CREATED, getIndex(), "Created",
                                 false, true, true, true, 
                                 10, UIComponents._DATEFIELD, getIndex(), DcRepository.ValueTypes._DATETIME,
                                 "Created"));
            addField(new DcField(DcObject._SYS_MODIFIED, getIndex(), "Modified",
                                 false, true, true, true, 
                                 10, UIComponents._DATEFIELD, getIndex(), DcRepository.ValueTypes._DATETIME,
                                 "Modified"));
            
            if ((isTopModule() || isChildModule()) && isCustomFieldsAllowed()) {
                addField(new DcField(DcMediaObject._U1_USER_LONGTEXT, getIndex(), "User Long Text Field", 
                        false, false, false, true,  
                        4000, UIComponents._LONGTEXTFIELD, getIndex(), DcRepository.ValueTypes._STRING,
                        "UserLongText1"));         
                addField(new DcField(DcMediaObject._U2_USER_SHORTTEXT1, getIndex(), "User Short Text Field 1",  
                        false, false, false, true,  
                        255, UIComponents._SHORTTEXTFIELD, getIndex(), DcRepository.ValueTypes._STRING,
                        "UserShortText1")); 
                addField(new DcField(DcMediaObject._U3_USER_SHORTTEXT2, getIndex(), "User Short Text Field 2",  
                        false, false, false, true,  
                        255, UIComponents._SHORTTEXTFIELD, getIndex(), DcRepository.ValueTypes._STRING,
                        "UserShortText2")); 
                addField(new DcField(DcMediaObject._U4_USER_NUMERIC1, getIndex(), "User Numeric Field 1",  
                        false, false, false, true,  
                        255, UIComponents._NUMBERFIELD, getIndex(), DcRepository.ValueTypes._LONG,
                        "UserInteger1"));
                addField(new DcField(DcMediaObject._U5_USER_NUMERIC2, getIndex(), "User Numeric Field 2",  
                        false, false, false, true,  
                        255, UIComponents._NUMBERFIELD, getIndex(), DcRepository.ValueTypes._LONG,
                        "UserInteger2"));            
            }
            
            if (isTopModule() && !hasDependingModules()) {
                addField(new DcField(DcMediaObject._VALUE, getIndex(), "Item Value",  
                        false, true, false, true,  
                        255, UIComponents._DECIMALFIELD, getIndex(), DcRepository.ValueTypes._DOUBLE,
                        "CurrencyValue"));            
            }
            
            if (    isTopModule() && !isAbstract() &&
                    getIndex() != DcModules._CONTAINER && getIndex() != DcModules._USER &&
                    getIndex() != DcModules._EXTERNALREFERENCE && getIndex() != DcModules._LOAN &&
                    getIndex() != DcModules._MAPPING && getIndex() != DcModules._PERMISSION &&
                    getIndex() != DcModules._CONTACTPERSON && getIndex() != DcModules._TAG) {
                
                addField(new DcField(DcObject._SYS_EXTERNAL_REFERENCES, getIndex(), "External References",  
                        true, true, false, true,  
                        4, UIComponents._SIMPLEREFERENCESFIELD, DcModules._EXTERNALREFERENCE, DcRepository.ValueTypes._DCOBJECTCOLLECTION,
                        "externalreferences"));     
            }

            if (    isTopModule() && !isAbstract() &&
                    getIndex() != DcModules._USER && getIndex() != DcModules._EXTERNALREFERENCE && 
                    getIndex() != DcModules._MAPPING && getIndex() != DcModules._PERMISSION &&
                    getIndex() != DcModules._TAG) {
                
                addField(new DcField(DcObject._SYS_TAGS, getIndex(), "Tags", 
                        true, true, false, true,  
                        4000, UIComponents._TAGFIELD, DcModules._TAG, DcRepository.ValueTypes._DCOBJECTCOLLECTION,
                        "Tags"));     
            }
            
            if (isContainerManaged())
                addField(getField(DcObject._SYS_CONTAINER));
            
            if (isTopModule() && hasOnlineServices()) {
                addField(getField(DcObject._SYS_SERVICE));
                addField(getField(DcObject._SYS_SERVICEURL));
            }
            
            addField(getField(DcObject._SYS_DISPLAYVALUE));
            
            if (isFileBacked) {
                addField(getField(DcObject._SYS_FILENAME));
                addField(getField(DcObject._SYS_FILESIZE));
                addField(getField(DcObject._SYS_FILEHASH));
                addField(getField(DcObject._SYS_FILEHASHTYPE));
            }
            
            // do not check whether the contact person module is enabled.
            // just add the fields if the settings allow for this
            if (canBeLended) {
                addField(getField(DcObject._SYS_AVAILABLE));
                addField(getField(DcObject._SYS_LENDBY));
                addField(getField(DcObject._SYS_LOANDURATION));
                addField(getField(DcObject._SYS_LOANDUEDATE));
                addField(getField(DcObject._SYS_LOANSTATUS));
                addField(getField(DcObject._SYS_LOANSTATUSDAYS));
                addField(getField(DcObject._SYS_LOANSTARTDATE));
                addField(getField(DcObject._SYS_LOANENDDATE));
//                addField(getField(DcObject._SYS_LOANALLOWED));
            }
            addField(getField(DcObject._SYS_MODULE));
        } catch (Exception e) {
            logger.error(e, e);
        }
    }    
    
    /**
     * Initializes and corrects the module settings (if necessary)
     */
    public void initializeSettings() {
        settings = new DcModuleSettings(this);
        
        QuickViewFieldDefinitions qvDefinitions = 
            (QuickViewFieldDefinitions) settings.get(DcRepository.ModuleSettings.stQuickViewFieldDefinitions);
        
        QuickViewFieldDefinitions newQvDefinitions = new QuickViewFieldDefinitions(getIndex());
        for (QuickViewFieldDefinition definition : qvDefinitions.getDefinitions()) {
            if (getField(definition.getField()) != null)
                newQvDefinitions.add(definition);
        } 
        
        DcFieldDefinitions definitions = 
            (DcFieldDefinitions) settings.get(DcRepository.ModuleSettings.stFieldDefinitions);
        
        DcFieldDefinitions newDefinitions = new DcFieldDefinitions(getIndex());
        for (DcFieldDefinition definition : definitions.getDefinitions()) {
            if (getField(definition.getIndex()) != null)
                newDefinitions.add(definition);
        }
        
        settings.set(DcRepository.ModuleSettings.stQuickViewFieldDefinitions, newQvDefinitions);
        settings.set(DcRepository.ModuleSettings.stFieldDefinitions, newDefinitions);
    }
    
    /**
     * Initializes the system fields.
     */
    protected void initializeSystemFields() {
        systemFields.put(DcObject._SYS_MODULE,
                new DcField(DcObject._SYS_MODULE, getIndex(), "Item",
                            true, true, true, true, 
                            255, UIComponents._SHORTTEXTFIELD, getIndex(), DcRepository.ValueTypes._STRING,
                            "Item"));
        systemFields.put(DcObject._SYS_AVAILABLE,
                new DcField(DcObject._SYS_AVAILABLE, getIndex(), "Available",
                            true, true, true, true, 
                            4, UIComponents._AVAILABILITYCOMBO, getIndex(), DcRepository.ValueTypes._BOOLEAN,
                            "Available"));
        systemFields.put(DcObject._SYS_LENDBY,
                new DcField(DcObject._SYS_LENDBY, getIndex(), "Lend by",
                            true, true, true, true, 
                            255, UIComponents._REFERENCEFIELD, DcModules._CONTACTPERSON, DcRepository.ValueTypes._DCOBJECTREFERENCE,
                            "LendBy"));
        systemFields.put(DcObject._SYS_LOANDURATION,
                new DcField(DcObject._SYS_LOANDURATION, getIndex(), "Days Loaned",
                            true, true, true, true, 
                            10, UIComponents._NUMBERFIELD, getIndex(), DcRepository.ValueTypes._LONG,
                            "DaysLoaned"));
        systemFields.put(DcObject._SYS_LOANDUEDATE,
                new DcField(DcObject._SYS_LOANDUEDATE, getIndex(), "Due date",
                            true, true, true, false, 
                            10, UIComponents._DATEFIELD, getIndex(), DcRepository.ValueTypes._DATE,
                            "DueDate")); 
        systemFields.put(DcObject._SYS_LOANSTATUS,
                new DcField(DcObject._SYS_LOANSTATUS, getIndex(), "Loan status",
                            true, true, true, false, 
                            255, UIComponents._SHORTTEXTFIELD, getIndex(), DcRepository.ValueTypes._STRING,
                            "LoanStatus"));
//        systemFields.put(DcObject._SYS_LOANALLOWED,
//                new DcField(DcObject._SYS_LOANALLOWED, getIndex(), "Is allowed to be lend?",
//                            false, true, false, false, 
//                            10, UIComponents._CHECKBOX, getIndex(), DcRepository.ValueTypes._BOOLEAN,
//                            "LoanAllowed"));          
        systemFields.put(DcObject._SYS_LOANSTATUSDAYS,
                new DcField(DcObject._SYS_LOANSTATUSDAYS, getIndex(), "Days",
                            true, true, true, false, 
                            10, UIComponents._NUMBERFIELD, getIndex(), DcRepository.ValueTypes._LONG,
                            "LoanStatusDays"));   
        systemFields.put(DcObject._SYS_LOANSTARTDATE,
                new DcField(DcObject._SYS_LOANSTARTDATE, getIndex(), "Loan start date",
                            true, true, true, false, 
                            10, UIComponents._DATEFIELD, getIndex(), DcRepository.ValueTypes._DATE,
                            "LoanStartDate"));         
        systemFields.put(DcObject._SYS_LOANENDDATE,
                new DcField(DcObject._SYS_LOANENDDATE, getIndex(), "Loan end date",
                            true, true, true, false, 
                            10, UIComponents._DATEFIELD, getIndex(), DcRepository.ValueTypes._DATE,
                            "LoanEndDate"));    
        
        if (isTopModule() && hasOnlineServices()) {
            systemFields.put(Integer.valueOf(DcObject._SYS_SERVICE),
                             new DcField(DcObject._SYS_SERVICE, getIndex(), "Service",
                                         false, true, true, false, 
                                         255, UIComponents._SHORTTEXTFIELD, getIndex(), DcRepository.ValueTypes._STRING,
                                         "service"));
            systemFields.put(Integer.valueOf(DcObject._SYS_SERVICEURL),
                             new DcField(DcObject._SYS_SERVICEURL, getIndex(), "Service URL",
                                         false, true, true, false, 
                                         255, UIComponents._URLFIELD, getIndex(), DcRepository.ValueTypes._STRING,
                                         "serviceurl"));        
        }
        
        if (isContainerManaged()) {
            systemFields.put(Integer.valueOf(DcObject._SYS_CONTAINER),
                    new DcField(DcObject._SYS_CONTAINER, getIndex(), "Container",
                                true, true, false, true,  
                                10, UIComponents._REFERENCESFIELD, DcModules._CONTAINER, DcRepository.ValueTypes._DCOBJECTCOLLECTION,
                                "Container"));
        }
        
        systemFields.put(Integer.valueOf(DcObject._SYS_DISPLAYVALUE),
                new DcField(Item._SYS_DISPLAYVALUE, getIndex(), "Label",
                            true, true, true, false, 
                            255, UIComponents._SHORTTEXTFIELD, getIndex(), DcRepository.ValueTypes._STRING,
                            "Label"));
        
        if (isFileBacked) {
            systemFields.put(Integer.valueOf(DcObject._SYS_FILENAME),
                    new DcField(DcObject._SYS_FILENAME, getIndex(), "Filename",
                                false, true, false, true, 
                                500, UIComponents._FILELAUNCHFIELD, getIndex(), DcRepository.ValueTypes._STRING,
                                "Filename"));
            systemFields.put(Integer.valueOf(DcObject._SYS_FILEHASH),
                    new DcField(DcObject._SYS_FILEHASH, getIndex(), "Filehash",
                                false, false, true, false, 
                                32, UIComponents._SHORTTEXTFIELD, getIndex(), DcRepository.ValueTypes._STRING,
                                "Filehash"));
            systemFields.put(Integer.valueOf(DcObject._SYS_FILESIZE),
                    new DcField(DcObject._SYS_FILESIZE, getIndex(), "Filesize",
                                false, true, true, true, 
                                10, UIComponents._FILESIZEFIELD, getIndex(), DcRepository.ValueTypes._LONG,
                                "Filesize"));
            systemFields.put(Integer.valueOf(DcObject._SYS_FILEHASHTYPE),
                    new DcField(DcObject._SYS_FILEHASHTYPE, getIndex(), "Filehash Type",
                                false, false, true, false, 
                                10, UIComponents._SHORTTEXTFIELD, getIndex(), DcRepository.ValueTypes._STRING,
                                "FilehashType"));
        }
    }    
    
    /**
     * Indicates if the module holds items with a reference to a file.
     */
    public boolean isFileBacked() {
        return isFileBacked;
    }

    /**
     * Retrieves the index of the field on which is sorted by default.
     * Return 1 if this field exists or else the defined default index.
     */
    public int getDefaultSortFieldIdx() {
        if (getIndex() == DcModules._RECORD_LABEL)
            return 3;
        else 
            return getField(1) != null ? 1 : defaultSortFieldIdx;
    }

    /**
     * The location of the module in the module bar. 
     */
    public int getDisplayIndex() {
        return displayIndex;
    }

    /**
     * The field index holding the title of the item.
     */
    public int getNameFieldIdx() {
        return nameFieldIdx;
    }

    public String getIcon16Filename() {
        return icon16filename;
    }

    public String getIcon32Filename() {
        return icon32filename;
    }

    @Override
    public int hashCode() {
        return index;
    }
    
    @Override
    public boolean equals(Object o) {
        return (o instanceof DcModule ? ((DcModule) o).getIndex() == getIndex() : false);
    }

    /**
     * Retrieves the XML definition of this module.
     * @return The XML definition or null. 
     */
    public XmlModule getXmlModule() {
        return xmlModule;
    }

    /**
     * Sets the XML definition for this module.
     */
    public void setXmlModule(XmlModule xmlModule) {
        this.xmlModule = xmlModule;
    }    
    
    public String getLabel() {
        if (DcResources.getText(moduleResourceKey) != null)
            return DcResources.getText(moduleResourceKey);

        return label;
    }
    
    /**
     * Compares the supplied module with the current module. The check is performed
     * by comparing the labels.
     */
    @Override
    public int compareTo(DcModule module) {
        return getLabel().toLowerCase().compareTo(module.getLabel().toLowerCase());
    }

    @Override
    public String toString() {
        return getName();
    }
    
    /**
     * Deletes this module. Cannot be undone.
     */
    public void delete() throws Exception {
        if (getXmlModule() != null && !new ModuleJar(getXmlModule()).delete())
            throw new Exception("Module file could not be deleted. " +
                                "Please check the access rights for file: " + getXmlModule().getJarFilename()); 
        
        if (DcConfig.getInstance().getOperatingMode() == DcConfig._OPERATING_MODE_CLIENT) 
            throw new Exception("User tries to delete: " + getXmlModule().getJarFilename() + " but is not allowed to do this");
        
        Connector conn =  DcConfig.getInstance().getConnector();
        
        if (this instanceof DcPropertyModule && getXmlModule() != null && !getXmlModule().isServingMultipleModules()) {
            // We are (or might be) working on a property base module with a calculated tablename
            DcModule module = DcModules.get(getName());
            
            try {
            	conn.executeSQL("DROP TABLE " + module.getTableName());
            } catch (Exception e) {
                // happens for property base modules
                logger.debug("Table does not exist, no need to drop " + module.getTableName(), e);
            }
        } else {
            try {
            	conn.executeSQL("DROP TABLE " + getTableName());
            } catch (Exception e) {
                // happens for property base modules
                logger.debug("Table does not exist, no need to drop " + getTableName(), e);
            }
                
            if (getTemplateModule() != null)
                getTemplateModule().delete();
        }
    }
}
