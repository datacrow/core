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
        
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.datacrow.core.DcConfig;
import net.datacrow.core.DcRepository;
import net.datacrow.core.DcThread;
import net.datacrow.core.clients.IItemImporterClient;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.utilities.Converter;
import net.datacrow.core.utilities.CoreUtilities;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XmlImporter extends ItemImporter {
    
    private static Logger logger = Logger.getLogger(XmlImporter.class.getName());
    
    public XmlImporter(int moduleIdx, int mode) throws Exception {
        super(DcConfig.getInstance().getConnector().getUser(), moduleIdx, "XML", mode);
    }
    
    @Override
    public Collection<String> getSettingKeys() {
        Collection<String> settingKeys = super.getSettingKeys();
        settingKeys.add(DcRepository.Settings.stImportMatchAndMerge);
        return settingKeys;
    }
    
    @Override
    protected void initialize() {}

    @Override
    public DcThread getTask() {
        return new Task(file, getModule(), client);
    }

    @Override
    public String[] getSupportedFileTypes() {
        return new String[] {"xml"};
    }
    
    @Override
    public void cancel() {}

    @Override
    public String getName() {
        return DcResources.getText("lblXImport", "XML");
    }
    
    private class Task extends DcThread {
        
        private File file;
        private IItemImporterClient listener;
        private DcModule module;
        
        public Task(File file, DcModule module, IItemImporterClient listener) {
            super(null, "XML import for " + file);
            this.file = file;
            this.module = module;
            this.listener = listener;
        }
    
        private DcObject parseItem(DcModule module, Element eItem) throws Exception {
            DcObject dco = module.getItem();
            Node node;
            
            dco.setIDs();
            String value;
            // get the object
            for (DcField field : module.getFields()) {
                
                if ((   field.isUiOnly() && 
                        field.getValueType() != DcRepository.ValueTypes._DCOBJECTCOLLECTION && 
                        field.getValueType() != DcRepository.ValueTypes._PICTURE) ||  
                        field.getIndex() == DcObject._SYS_EXTERNAL_REFERENCES) 
                    continue;
                
                String fieldName = Converter.getValidXmlTag(field.getSystemName());
                NodeList nlField = eItem.getChildNodes();
                Element eField = null;
                
                try {
                    for (int i = 0; i < nlField.getLength(); i++) {
                        node = nlField.item(i);
                        if (node.getNodeName().equals(fieldName)) {
                            eField = (Element) node;
                        }
                    }
                } catch (Exception e) {
                    logger.error("Could not match " + fieldName + " with an existing child tag", e);
                }
                
                // field was not found; skip
                if (eField == null) 
                    continue;
                
                if (field.getValueType() == DcRepository.ValueTypes._DCOBJECTCOLLECTION) {
                    // retrieve the items by their module name
                    DcModule referenceMod = DcModules.get(field.getReferenceIdx());
                    String referenceName = Converter.getValidXmlTag(referenceMod.getSystemObjectName());
                    NodeList elReferences = eField.getElementsByTagName(referenceName);
                    for (int j = 0; elReferences != null && j < elReferences.getLength(); j++) {
                        // retrieve the values by the display field index (the system display field index)
                        Element eReference = (Element) elReferences.item(j);
                        DcObject reference = referenceMod.getItem();
                        String referenceField = Converter.getValidXmlTag(reference.getField(reference.getSystemDisplayFieldIdx()).getSystemName());
                        NodeList nlRefField = eReference.getElementsByTagName(referenceField);
                        if (nlRefField != null && nlRefField.getLength() > 0) {
                            Node eRefField = nlRefField.item(0);
                            setValue(dco, field.getIndex(), eRefField.getTextContent(), listener);
                        } else {
                            logger.debug("Could not set value for field " + referenceField + ". The field name does not exist in the XML file");
                        }
                    }
                } else if (field.getValueType() == DcRepository.ValueTypes._DCOBJECTREFERENCE) {
                    setValue(dco, field.getIndex(), eField.getTextContent(), listener);
                    
                } else if (field.getValueType() == DcRepository.ValueTypes._PICTURE) {
                    setValue(dco, field.getIndex(), eField.getTextContent(), listener);
                } else {
                    value = eField.getTextContent();
                    if (!CoreUtilities.isEmpty(value))
                        setValue(dco, field.getIndex(), value, listener);
                }
            }
            
            return dco;
        }
        
        @Override
        public void run() {
            InputSource input = null;
            InputStreamReader in = null;
            BufferedReader reader = null;
            
            try {
            	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                
                in = new InputStreamReader(new FileInputStream(file), "utf-8" );
                reader = new BufferedReader (in);
                input = new InputSource(reader);

                Document document = db.parse(input);
                
                Element eTop = document.getDocumentElement();
                
                String name = Converter.getValidXmlTag(module.getSystemObjectName());
                NodeList nlItems = eTop.getElementsByTagName(name);
    
                listener.notifyTaskStarted(nlItems != null ? nlItems.getLength() : 0);
                
                Element eItem;
                DcObject dco;
                DcObject child;
                DcModule cm;
                String childName;
                NodeList nlChildren;
                Element eChild;
                for (int i = 0; !isCanceled() && nlItems != null && i < nlItems.getLength(); i++) {
                    try {
                    	eItem = (Element) nlItems.item(i);
                    	
                    	if (eItem.getParentNode() != eTop) continue;
                    	
                    	dco = parseItem(module, eItem);
                    	cm = module.getChild();
                    	
                    	if (cm != null && !cm.isAbstract()) {
                    	    childName = Converter.getValidXmlTag(cm.getSystemObjectName());
                            nlChildren = eItem.getElementsByTagName(childName);
                            
                            for (int j = 0; nlChildren != null && j < nlChildren.getLength(); j++) {
                                eChild = (Element) nlChildren.item(j);
                                child = parseItem(cm, eChild);
                                dco.addChild(child);
                            }
                    	}
                    	listener.notifyProcessed(dco);
                    } catch (Exception e) {
                        listener.notify(e.getMessage());
                        logger.error(e, e) ;
                    }
                }
                
                listener.notifyTaskCompleted(true, null);
                
            } catch (Exception e) {
                logger.error(e, e) ;
            } finally {
                try {
                    if (reader != null) reader.close();
                    if (in != null) in.close();
                } catch (Exception e) {
                    logger.debug("Failed to close resource", e);
                }
            }
        }
    }
}
