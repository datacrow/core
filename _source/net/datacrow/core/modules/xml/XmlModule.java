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

package net.datacrow.core.modules.xml;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.KeyStroke;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.datacrow.core.DcConfig;
import net.datacrow.core.modules.DcAssociateModule;
import net.datacrow.core.modules.DcMediaModule;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.modules.DcPropertyModule;
import net.datacrow.core.modules.InvalidModuleXmlException;
import net.datacrow.core.modules.InvalidValueException;
import net.datacrow.core.modules.ModuleUpgradeException;
import net.datacrow.core.modules.upgrade.ModuleUpgrade;
import net.datacrow.core.objects.DcAssociate;
import net.datacrow.core.objects.DcMediaObject;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.DcProperty;
import net.datacrow.core.utilities.XMLParser;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * A XML representation of a module.
 * 
 * @author Robert Jan van der Waals
 */
public class XmlModule extends XmlObject {
    
    private transient static Logger logger = Logger.getLogger(XmlModule.class.getName());
    
    private static final long serialVersionUID = 3672118094404831329L;

    private Document document;
    
    private Class moduleClass;
    private Class object;
    
    private int index;
    private int displayIndex;
    private int childIndex = -1;
    private int parentIndex = -1;
    private int nameFieldIdx = 150;
    
    private String label;
    private String name;
    private String description;
    private KeyStroke keyStroke;
    
    private String productVersion;
    
    private String icon16Filename;
    private String icon32Filename;
    
    private byte[] icon16;
    private byte[] icon32;
    
    private String objectName;
    private String objectNamePlural;
    
    private int defaultSortFieldIdx = DcObject._ID;
    
    private boolean changed = false;
    
    private boolean hasOnlineServices;
    private boolean enabled;
    private boolean canBeLend;
    private boolean hasSearchView;
    private boolean hasInsertView;
    private boolean hasDependingModules;
    private boolean isServingMultipleModules;
    private boolean isFileBacked;
    private boolean isContainerManaged;

    private String tableName;
    private String tableNameShort;
    
    private Collection<XmlField> fields = new ArrayList<XmlField>();    

    /**
     * Creates an empty instance.
     */
    public XmlModule() {}
    
    /**
     * Create a new module based on the provided existing XML module.
     * @param template
     */
    public XmlModule(XmlModule template) {
        
        object = template.getObjectClass();
        moduleClass = template.getModuleClass();
        
        // make sure we are using a transparent module class:
        if (DcModules.get(template.getIndex()) != null) {
            DcModule module = DcModules.get(template.getIndex());

            if (module.getType() == DcModule._TYPE_ASSOCIATE_MODULE) {
                object = DcAssociate.class;
                moduleClass = DcAssociateModule.class;
            } else if (module.getType() == DcModule._TYPE_MEDIA_MODULE) {
                object = DcMediaObject.class;
                moduleClass = DcMediaModule.class;
            } else if (module.getType() == DcModule._TYPE_PROPERTY_MODULE) {
                object = DcProperty.class;
                moduleClass = DcPropertyModule.class;
            } else {
                object = DcObject.class;
                moduleClass = DcModule.class;
            }
        }
        
        childIndex = -1;
        parentIndex = -1;
        nameFieldIdx = template.getNameFieldIdx();

        icon16Filename = template.getIcon16Filename();
        icon32Filename = template.getIcon32Filename();
        icon16 = template.getIcon16();
        icon32 = template.getIcon32();

        defaultSortFieldIdx = template.getDefaultSortFieldIdx();
        enabled = true;
        canBeLend = template.canBeLend();
        canBeLend = template.canBeLend();
        hasSearchView = template.hasSearchView;
        hasInsertView = template.hasInsertView;
        hasDependingModules = false;
        isServingMultipleModules = template.isServingMultipleModules();
        isFileBacked = template.isFileBacked();
        isContainerManaged = template.isContainerManaged;
        
        for (XmlField field : template.getFields())
            fields.add(new XmlField(field));
    }

    /**
     * Creates a new instance.
     * @param xml The XML file byte content 
     * @throws InvalidModuleXmlException
     * @throws ModuleUpgradeException
     */
    public XmlModule(byte[] xml) throws InvalidModuleXmlException, ModuleUpgradeException {
    	byte[] b = xml;
        if (DcConfig.getInstance().getOperatingMode() != DcConfig._OPERATING_MODE_CLIENT) {
	    	b = new ModuleUpgrade().upgrade(xml);
	        boolean same = b.length == xml.length;
	        if (same) {
	            for (int i = 0; i < b.length; i++)
	                same &= b[i] == xml[i];
	        }
	        changed = !same;
        }
        initialize(b);
    }

    public void setChanged(boolean b) {
        this.changed = b;
    }
    
    /**
     * Indicates if customizations have been made.
     */
    public boolean isChanged() {
        return changed;
    }
    
    /**
     * Parses the provided XML byte content.
     * @param xml
     * @throws InvalidModuleXmlException
     */
    private void initialize(byte[] xml) throws InvalidModuleXmlException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        InputStreamReader in = null;
        BufferedReader reader = null;
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            
            in = new InputStreamReader(new ByteArrayInputStream(xml), "UTF-8");
            reader = new BufferedReader (in); 
            InputSource input = new InputSource(reader);
            
            document = db.parse(input);
            load();
        } catch(Exception e) {
            throw new InvalidModuleXmlException(getName(), e);
        } finally {
            try {
                if (in != null) in.close();
                if (reader != null) reader.close();
            } catch (Exception e) {
                logger.debug(e, e);
            }
        }
    }

    /**
     * Reads the elements of the XML document.
     * @throws InvalidValueException
     */
    private void load() throws InvalidValueException {
        Element element = document.getDocumentElement();
        NodeList nodes = element.getElementsByTagName("module");
        Element module = (Element) nodes.item(0);
        
        moduleClass = getClass(module, "module-class");
        object = getClass(module, "object-class");
        moduleClass = object.equals(DcAssociate.class) ? DcAssociateModule.class : moduleClass;
        
        index = XMLParser.getInt(module, "index");
        
        if (XMLParser.getString(element, "display-index") != null)
            displayIndex = XMLParser.getInt(module, "display-index");
        
        if (XMLParser.getString(element, "child-module") != null)
            childIndex = XMLParser.getInt(module, "child-module");
        
        if (XMLParser.getString(element, "parent-module") != null)
            parentIndex = XMLParser.getInt(module, "parent-module");

        isFileBacked = XMLParser.getBoolean(module, "is-file-backed");
        isContainerManaged = XMLParser.getBoolean(module, "is-container-managed");
        isServingMultipleModules = XMLParser.getBoolean(module, "is-serving-multiple-modules");
        tableName =XMLParser.getString(module, "table-name");
        tableNameShort = XMLParser.getString(module, "table-short-name");
        label = XMLParser.getString(module, "label");
        productVersion = XMLParser.getString(module, "product-version");
        name = XMLParser.getString(module, "name");
        description = XMLParser.getString(module, "description");
        objectName = XMLParser.getString(module, "object-name");
        objectNamePlural = XMLParser.getString(module, "object-name-plural");
        enabled = XMLParser.getBoolean(module, "enabled");
        canBeLend = XMLParser.getBoolean(module, "can-be-lended");
        hasSearchView = XMLParser.getBoolean(module, "has-search-view");
        hasInsertView = XMLParser.getBoolean(module, "has-insert-view");
        nameFieldIdx = XMLParser.getInt(module, "name-field-index");
        
        if (index == 1 && "tbl_6a4a14ec9a6d43f380b5222a101ec4a2".equals(tableName)) {
            index = DcModules._RECORD_LABEL;
            changed = true;
        }
        
        if (XMLParser.getString(element, "default-sort-field-index") != null)
            defaultSortFieldIdx = XMLParser.getInt(module, "default-sort-field-index");

        keyStroke = XMLParser.getKeyStroke(module, "key-stroke");
        icon16Filename = XMLParser.getString(module, "icon-16");
        icon32Filename = XMLParser.getString(module, "icon-32");
        hasDependingModules = XMLParser.getBoolean(module, "has-depending-modules");
        
        if (XMLParser.getString(element, "has-online-services") == null) {
        	if (index == 50 ||
				index == 51 ||
				index == 54 ||
				index == 68 ||
				index == 30000 ||
				index == 31000 ||
				index == 34000 ||
				index == 35000 ||
				index == 36000)	
        		hasOnlineServices = true;
        	
        	changed = true;
        } else {
            hasOnlineServices = XMLParser.getBoolean(element, "has-online-services");
        }
        
        setFields(this, module);
    }
    
    /**
     * Creates the XML field definitions.
     * @param module The XML module.
     * @param element The element to parse.
     * @see XmlField
     * @throws InvalidValueException
     */
    private void setFields(XmlModule module, Element element) throws InvalidValueException {
        NodeList nodes = element.getElementsByTagName("field");
        for (int i = 0; i < nodes.getLength(); i++) {
            fields.add(new XmlField(module, (Element) nodes.item(i)));
        }
    }
    
    /**
     * Tells if items belonging to this module can be lend.
     */
    public boolean canBeLend() {
        return canBeLend;
    }

    /**
     * Retrieves the child module index. 
     * @return The index or -1.
     */
    public int getChildIndex() {
        return childIndex;
    }

    /**
     * The description for this module.
     */    
    public String getDescription() {
        return description;
    }

    /**
     * The XML document.
     */
    public Document getDocument() {
        return document;
    }

    /**
     * Is the module enabled?
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Retrieves the XML field definitions.
     */
    public Collection<XmlField> getFields() {
        return fields;
    }
    
    public boolean hasOnlineServices() {
    	return hasOnlineServices;
    }
    
    /**
     * Indicates if the module support insert views.
     */
    public boolean hasInsertView() {
        return hasInsertView;
    }

    /**
     * Indicates if the module support search views.
     */
    public boolean hasSearchView() {
        return hasSearchView;
    }

    /**
     * The unique index of the module.
     */
    public int getIndex() {
        return index;
    }

    /**
     * The key combination associated with this module.
     */
    public KeyStroke getKeyStroke() {
        return keyStroke;
    }

    /**
     * The display label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * The internal name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the items object class.
     */
    public Class getObjectClass() {
        return object;
    }

    /**
     * System name for items belonging to this module.
     */
    public String getObjectName() {
        return objectName;
    }

    /**
     * System plural name for items belonging to this module.
     */
    public String getObjectNamePlural() {
        return objectNamePlural;
    }

    /**
     * The module class.
     */
    public Class getModuleClass() {
        return moduleClass;
    }

    /**
     * The database table name.
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * The database table short name.
     */
    public String getTableNameShort() {
        return tableNameShort;
    } 
    
    /**
     * Indicates if other modules are depending on this module.
     */
    public boolean hasDependingModules() {
        return hasDependingModules;
    }

    /**
     * The position of this module as displayed in the module bar. 
     */
    public int getDisplayIndex() {
        return displayIndex;
    }

    /**
     * The field to be sorted / ordered on by default.
     */
    public int getDefaultSortFieldIdx() {
        return defaultSortFieldIdx;
    }

    /**
     * The parent module index.
     * @return The index or -1.
     */
    public int getParentIndex() {
        return parentIndex;
    }

    /**
     * Retrieves the index for the field which holds the name of an item. 
     */
    public int getNameFieldIdx() {
        return nameFieldIdx;
    }

    /**
     * Indicate if items belonging to this module can be lend.
     * @param canBeLend
     */
    public void setCanBeLend(boolean canBeLend) {
        this.canBeLend = canBeLend;
    }

    /**
     * Indicate which module is the child for this module.
     * @param childIndex The module index of the child.
     */
    public void setChildIndex(int childIndex) {
        this.childIndex = childIndex;
    }

    /**
     * Set the default field to sort / order on.
     * @param defaultSortFieldIdx The field index.
     */
    public void setDefaultSortFieldIdx(int defaultSortFieldIdx) {
        this.defaultSortFieldIdx = defaultSortFieldIdx;
    }

    /**
     * Set the description for this module.
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Set the module bar position for this module.
     * @param displayIndex
     */
    public void setDisplayIndex(int displayIndex) {
        this.displayIndex = displayIndex;
    }

    /**
     * Set the XML document.
     * @param document
     */
    public void setDocument(Document document) {
        this.document = document;
    }

    /**
     * Mark the field as enabled.
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Set the XML field definitions.
     * @param fields
     */
    public void setFields(Collection<XmlField> fields) {
        this.fields = fields;
    }

    /**
     * Indicate if other modules are depending on this module.
     * @param hasDependingModules
     */
    public void setHasDependingModules(boolean hasDependingModules) {
        this.hasDependingModules = hasDependingModules;
    }

    /**
     * Indicate if the insert view is supported.
     * @param hasInsertView
     */
    public void setHasInsertView(boolean hasInsertView) {
        this.hasInsertView = hasInsertView;
    }

    /**
     * Indicate if the search view is supported.
     * @param hasSearchView
     */
    public void setHasSearchView(boolean hasSearchView) {
        this.hasSearchView = hasSearchView;
    }
    
    /**
     * Set the unique index for this module.
     * @param index
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Set the key combination to activate this module.
     * @param keyStroke
     */
    public void setKeyStroke(KeyStroke keyStroke) {
        this.keyStroke = keyStroke;
    }

    /**
     * Sets the display label.
     * @param label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Set the module class.
     * @param moduleClass
     */
    public void setModuleClass(Class moduleClass) {
        this.moduleClass = moduleClass;
    }

    /**
     * The system name of this module.
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Indicate which field holds the name of an item. 
     * @param nameFieldIdx The field index.
     */
    public void setNameFieldIdx(int nameFieldIdx) {
        this.nameFieldIdx = nameFieldIdx;
    }

    /**
     * Sets the object class.
     * @param object
     */
    public void setObject(Class object) {
        this.object = object;
    }

    /**
     * Sets the system name for items belonging to this module.
     * @param objectName
     */
    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    /**
     * Sets the system plural name for items belonging to this module.
     * @param objectNamePlural
     */
    public void setObjectNamePlural(String objectNamePlural) {
        this.objectNamePlural = objectNamePlural;
    }

    /**
     * Indicate which module is the parent of this module.
     * @param parentIndex The module index.
     */
    public void setParentIndex(int parentIndex) {
        this.parentIndex = parentIndex;
    }

    /**
     * Sets the database table name.
     * @param tableName
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * Sets the database table short name.
     * @param tableNameShort
     */
    public void setTableNameShort(String tableNameShort) {
        this.tableNameShort = tableNameShort;
    }

    /**
     * Retrieves the small icon file name.
     */
    public String getIcon16Filename() {
        return icon16Filename;
    }

    /**
     * Sets the small icon filename.
     * @param icon16Filename
     */
    public void setIcon16Filename(String icon16Filename) {
        this.icon16Filename = icon16Filename;
    }

    /**
     * Retrieves the large icon file name.
     */
    public String getIcon32Filename() {
        return icon32Filename;
    }

    /**
     * Sets the large icon filename.
     * @param icon32Filename
     */
    public void setIcon32Filename(String icon32Filename) {
        this.icon32Filename = icon32Filename;
    }

    /**
     * Indicates if items belonging to this module are file based.
     */
    public boolean isFileBacked() {
        return isFileBacked;
    }

    /**
     * Indicate if items belonging to this module are file based.
     * @param isFileBacked
     */
    public void setFileBacked(boolean isFileBacked) {
        this.isFileBacked = isFileBacked;
    }
    
    /**
     * Indicates if items belonging to this module can be part of a container.
     */
    public boolean isContainerManaged() {
        return isContainerManaged;
    }

    /**
     * Indicate if items belonging to this module can be part of a container.
     * @param isContainerManaged
     */
    public void setContainerManaged(boolean isContainerManaged) {
        this.isContainerManaged = isContainerManaged;
    }

    /**
     * Indicates if multiple modules are using this module.
     */
    public boolean isServingMultipleModules() {
        return isServingMultipleModules;
    }

    /**
     * Indicate if multiple modules are using this module.
     * @param isServingMultipleModules
     */
    public void setServingMultipleModules(boolean isServingMultipleModules) {
        this.isServingMultipleModules = isServingMultipleModules;
    }

    /**
     * Retrieves the JAR filename in which this module is / will be stored.
     */
    public String getJarFilename() {
    	return getName().toLowerCase() + ".jar";
    }
    
    /**
     * The small icon bytes.
     */
    public byte[] getIcon16() {
        return icon16;
    }

    /**
     * The large icon bytes.
     */
    public byte[] getIcon32() {
        return icon32;
    }
    
    /**
     * Set the small icon bytes.
     * @param b The icon.
     */
    public void setIcon16(byte[] b) {
    	if (b != null && b.length > 10)
    		icon16 = b;
    }

    /**
     * Set the large icon bytes.
     * @param b The icon.
     */
    public void setIcon32(byte[] b) {
    	if (b != null && b.length > 10)
    		icon32 = b;
    }
    
    /**
     * Retrieves the Data Crow version number for which this module has been created.
     */
    public String getProductVersion() {
        return productVersion;
    }

    /**
     * Sets the Data Crow version number for which this module has been created.
     */
    public void setProductVersion(String productVersion) {
        this.productVersion = productVersion;
    }
}
