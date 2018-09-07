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

package net.datacrow.core.migration.itemexport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import net.datacrow.core.DcConfig;
import net.datacrow.core.DcThread;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.server.Connector;

import org.apache.log4j.Logger;

/**
 * Creates a XML extract for a collection of items. The Resulting XML can be used in reports 
 * and or can be used to migrate information from one system to another.
 * 
 * @author Robert Jan van der Waals
 */
public class XmlExporter extends ItemExporter {
    
    private static Logger logger = Logger.getLogger(XmlExporter.class.getName());
    
    public XmlExporter(
    		int moduleIdx, 
    		int mode, 
    		boolean processChildren) throws Exception {
    	
        super(moduleIdx, "XML", mode, processChildren);
    }

    @Override
    public String getFileType() {
        return "xml";
    }
  
    @Override
    public DcThread getTask() {
        return new Task(items);
    }

    @Override
    public String getName() {
        return DcResources.getText("lblXmlExport");
    }    
    
    private class Task extends DcThread {
        
        private Collection<String> items;
        
        public Task(Collection<String> items) {
            super(null, "XML export to " + file);
            
            this.items = new ArrayList<String>();
            this.items.addAll(items);            
        }

        @Override
        public void run() {
            try {
                
                String schemaFile = file.toString();
                schemaFile = schemaFile.substring(0, schemaFile.lastIndexOf(".xml")) + ".xsd";

                if (!isCanceled())
                    generateXsd(schemaFile);
                
                if (!isCanceled())
                    generateXml(schemaFile);
                
            } catch (Exception exp) {
                success = false;
                logger.error(DcResources.getText("msgErrorWhileCreatingReport", exp.toString()), exp);
                client.notify(DcResources.getText("msgErrorWhileCreatingReport", exp.toString()));
            } finally {
                if (items != null) items.clear();
                client.notifyTaskCompleted(true, null);
            }
        }

        /**
         * Writes the schema file. The schema is based on the object of the current module.
         * @param schemaFile
         * @throws IOException
         */
        private void generateXsd(String schemaFile) throws Exception {
            XmlSchemaWriter schema = new XmlSchemaWriter(schemaFile);
            schema.setFields(getFields());
            DcObject dco = DcModules.getCurrent().getItem();
            schema.create(dco);
        }
        
        private void generateXml(String schemaFile) throws Exception {
            if (items == null || items.size() == 0) return;
            
            XmlWriter writer = new XmlWriter(bos, file.toString(), schemaFile, settings);
            writer.startDocument();
            
            Connector conn = DcConfig.getInstance().getConnector();
            DcObject dco;
            for (String item : items) {
            	
                if (isCanceled()) break;
                
                dco = conn.getItem(getModule().getIndex(), item, null);
                
                writer.startEntity(dco);
                client.notify(DcResources.getText("msgExportingX", dco.toString()));

                writer.writeAttribute(dco, DcObject._SYS_MODULE);
                
                for (int fieldIdx : getFields()) {
                    DcField field = dco.getField(fieldIdx);
                    if (field != null && !field.getSystemName().endsWith("_persist")) 
                        writer.writeAttribute(dco, field.getIndex());
                }

                if (processChildren) {
                    if (dco.getModule().getChild() != null) {
                    
                        dco.loadChildren(null);
                        
                        writer.startRelations(dco.getModule().getChild());
                        writer.setIdent(2);
    
                        for (DcObject child : dco.getChildren()) {
                            writer.startEntity(child);
                            writer.writeAttribute(child, DcObject._SYS_MODULE);
                            int[] fields = child.getFieldIndices();
                            for (int i = 0; i < fields.length; i++)
                                writer.writeAttribute(child, fields[i]);
                            
                            writer.endEntity(child);
                        }
                        
                        writer.resetIdent();
                        writer.endRelations(dco.getModule().getChild());
                    }
                }
                    
                writer.endEntity(dco);
                client.notifyProcessed();
                bos.flush();
                
                // release the object
                dco.destroy();
            }
            
            writer.endDocument();
            client.notify(DcResources.getText("lblExportHasFinished"));
        }
    }    
}
