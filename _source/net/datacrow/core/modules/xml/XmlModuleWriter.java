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

import java.util.ArrayList;
import java.util.Collection;

public class XmlModuleWriter {
    
    private StringBuffer sb = new StringBuffer();
    
    public XmlModuleWriter(XmlModule module) {
        createXML(module);
    }
    
    public byte[] getXML() {
        try {
            return sb.toString().getBytes("UTF-8");
        } catch (Exception e) {
            return sb.toString().getBytes();
        }
    }

    public void close() {
        sb = null;
    }
    
    private void createXML(XmlModule module) {
        writeHeader();
        
        writeLine("<module>", 1);
        
        int level = 2;
        writeLine("<index>" + module.getIndex() + "</index>" , level);
        writeLine("<product-version>" + module.getProductVersion() + "</product-version>" , level);
        writeLine("<display-index>" + getString(module.getDisplayIndex()) + "</display-index>" , level);
        writeLine("<label>" + getString(module.getLabel()) + "</label>" , level);
        writeLine("<name>" + getString(module.getName()) + "</name>" , level);
        writeLine("<description>" + getString(module.getDescription()) + "</description>" , level);
        writeLine("<key-stroke>" + module.getKeyStroke() + "</key-stroke>" , level);
        writeLine("<enabled>" + module.isEnabled() + "</enabled>" , level);
        writeLine("<can-be-lended>" + module.canBeLend() + "</can-be-lended>" , level);
        writeLine("<icon-16>" + getString(module.getIcon16Filename()) + "</icon-16>" , level);
        writeLine("<icon-32>" + getString(module.getIcon32Filename()) + "</icon-32>" , level);
        writeLine("<object-name>" + getString(module.getObjectName()) + "</object-name>" , level);
        writeLine("<object-class>" + getString(module.getObjectClass()) + "</object-class>" , level);
        writeLine("<object-name-plural>" + getString(module.getObjectNamePlural()) + "</object-name-plural>" , level);
        writeLine("<table-name>" + getString(module.getTableName()) + "</table-name>" , level);
        writeLine("<table-name-short>" + getString(module.getTableNameShort()) + "</table-name-short>" , level);
        writeLine("<module-class>" + getString(module.getModuleClass()) + "</module-class>" , level);
        writeLine("<child-module>" + getString(module.getChildIndex()) + "</child-module>" , level);
        writeLine("<parent-module>" + getString(module.getParentIndex()) + "</parent-module>" , level);
        writeLine("<has-search-view>" + module.hasSearchView() + "</has-search-view>" , level);
        writeLine("<has-insert-view>" + module.hasInsertView() + "</has-insert-view>" , level);
        writeLine("<is-file-backed>" + module.isFileBacked() + "</is-file-backed>" , level);
        writeLine("<has-online-services>" + module.hasOnlineServices() + "</has-online-services>" , level);
        writeLine("<is-container-managed>" + module.isContainerManaged() + "</is-container-managed>" , level);
        writeLine("<has-depending-modules>" + module.hasDependingModules() + "</has-depending-modules>" , level);
        writeLine("<default-sort-field-index>" + getString(module.getDefaultSortFieldIdx()) + "</default-sort-field-index>" , level);
        writeLine("<name-field-index>" + getString(module.getNameFieldIdx()) + "</name-field-index>" , level);
        writeLine("<is-serving-multiple-modules>" + module.isServingMultipleModules() + "</is-serving-multiple-modules>" , level);
        
        writeFields(module, level);
        
        writeLine("</module>", 1);
        writeLine("</modules>", 0);
    }
    
    private String getString(Class c) {
        return c == null ? "" : c.getName();
    }
    
    private String getString(int  i) {
        return i == -1 ? "" : "" + i;
    }

    private String getString(String s) {
        return s == null ? "" : s.replaceAll("&", "&#38;");
    }
    
    private void writeFields(XmlModule module, int level) {
        
        Collection<Integer> indices = new ArrayList<Integer>();
        for (XmlField field : module.getFields()) {
            if (field.getIndex() > 0)
                indices.add(field.getIndex());
        }

        writeLine("<fields>", level);
        int counter = 1;
        for (XmlField field : module.getFields()) {
            writeLine("<field>", level + 1);
            
            // check if the counter does not occur in the current indices
            while (indices.contains(counter))
                counter++;
            
            // re-use pre-existing field index or create a new one
            int index = field.getIndex() > 0 ? field.getIndex() : counter;
            
            if (field.getDefinition() != null) 
            	field.getDefinition().setIndex(index);
            
            field.setIndex(index);
            
            // add the index to the list (multiple occurences are okay..)
            indices.add(index);

            String reference = field.getModuleReference() == module.getIndex() ? "{index}" :
                               "" + field.getModuleReference();
            
            writeLine("<index>" + getString(index) + "</index>" , level + 2);
            writeLine("<name>" + getString(field.getName()) + "</name>" , level + 2);
            writeLine("<database-column-name>" + getString(field.getColumn()) + "</database-column-name>" , level + 2);
            writeLine("<ui-only>" + field.isUiOnly() + "</ui-only>" , level + 2);
            writeLine("<enabled>true</enabled>" , level + 2);
            writeLine("<readonly>" + field.isReadonly() + "</readonly>" , level + 2);
            writeLine("<searchable>" + field.isSearchable() + "</searchable>" , level + 2);
            writeLine("<maximum-length>" + getString(field.getMaximumLength()) + "</maximum-length>" , level + 2);
            writeLine("<field-type>" + getString(field.getFieldType()) + "</field-type>" , level + 2);
            writeLine("<module-reference>" + reference + "</module-reference>" , level + 2);
            writeLine("<value-type>" + getString(field.getValueType()) + "</value-type>" , level + 2);
            writeLine("<overwritable>" + field.isOverwritable() + "</overwritable>" , level + 2);
            
            writeLine("</field>", level + 1);
            
            counter++;
        }
        writeLine("</fields>", level);
    }
    
    private void writeHeader() {
        writeLine("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>", 0);
        writeLine("<modules xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"file://module.xsd\">", 0);        
    }
    
    private void write(String s) {
        if (!s.equals("" + null) && !s.equals("-1"))
            sb.append(s);
    }
    
    private void newLine() {
        sb.append("\r\n");
    }
    
    private void writeLine(String s, int level) {
        for (int i = 0; i < level; i++)
            sb.append("    ");

        write(s);
        newLine();
    }
}
