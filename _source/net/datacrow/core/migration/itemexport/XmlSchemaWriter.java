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

import net.datacrow.core.DcRepository;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcAssociate;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.DcProperty;

/**
 * @author Robert Jan van der Waals
 */
public class XmlSchemaWriter extends XmlBaseWriter {
    
    private Collection<XmlReference> references = new ArrayList<XmlReference>();
    
    private int[] fields;
    
    public XmlSchemaWriter(String filename) throws IOException {
        super(filename);
    }
    
    public void create(DcObject dco) throws IOException {
        startDocument(dco);
        
        Collection<String> handled = new ArrayList<String>();
        if (dco.getModule().isAbstract()) {
            for (DcModule module : DcModules.getModules()) {
                if ((module.getType() == DcModule._TYPE_MEDIA_MODULE || 
                     dco.getModule().getIndex() != DcModules._MEDIA) && 
                     module.isTopModule() && !module.isAbstract()) {
                    
                    DcObject tmp = module.getItem();
                    handle(tmp, handled);
                    tmp.destroy();
                }
            }
        } else {
            handle(dco, handled);
        }
        
        endDocument();
    }
    
    public int[] getFields() {
        return fields;
    }

    public void setFields(int[] fields) {
        this.fields = fields;
    }

    private void handle(DcObject dco, Collection<String> handled) throws IOException {
        for (int fieldIdx : fields) {
            DcField field = dco.getField(fieldIdx);
            if (field != null && field.getValueType() == DcRepository.ValueTypes._DCOBJECTCOLLECTION) {
                DcModule sm = DcModules.get(field.getReferenceIdx());
                DcObject so = sm.getItem();

                if (!handled.contains(so.getModule().getSystemObjectName())) {
                    writeDco(so);
                    newLine();
                }
                handled.add(so.getModule().getSystemObjectName());
            }
        }
        
        if (dco.getModule().getChild() != null) {
            writeDco(dco.getModule().getChild().getItem());
            newLine();
            
            handled.add(dco.getModule().getChild().getSystemObjectName());
        }
        
        writeDco(dco);
        handled.add(dco.getModule().getSystemObjectName());
    }
    
    private void addReference(String name, String reference) {
        XmlReference xmlReference = new XmlReference(name, reference);
        if (!references.contains(xmlReference))
            references.add(xmlReference);
    }
    
    private void writeField(DcField field) throws IOException {
        String label = getValidTag(field.getSystemName());

        if (field.getValueType() == DcRepository.ValueTypes._DCOBJECTCOLLECTION) {
            DcModule sm = DcModules.get(field.getReferenceIdx());
            String name = getValidTag(field.getSystemName());
            String reference = getValidTag(sm.getSystemObjectName());

            writeLine("<xsd:element name=\"" + name + "\"/>", 3);
            addReference(name, reference);
            writeLine("<xsd:element name=\"" + label + "-list\" type=\"xsd:string\"/>", 3);
        } else {
            String type;
            switch (field.getValueType()) {
            case DcRepository.ValueTypes._BIGINTEGER :
                type = "long";
                break;
            case DcRepository.ValueTypes._BOOLEAN :
                type = "boolean";
                break;
            case DcRepository.ValueTypes._DATETIME :
            case DcRepository.ValueTypes._DATE :
                type = "date";
                break;
            case DcRepository.ValueTypes._LONG :
                type = "integer";
                break;
            default:
                type = "string";
            }
            
            writeLine("<xsd:element name=\"" + label + "\" type=\"xsd:" + type + "\"/>", 3);
        }        
    }
    
    private void writeDco(DcObject dco) throws IOException {
        String baseName = getValidTag(dco.getModule().getSystemObjectName());
        
        newLine();
        writeLine("<xsd:element name=\"" + baseName + "\" type=\"type-" + baseName + "\"/>", 1);
        writeLine("<xsd:complexType name=\"type-" + baseName + "\">", 1);
        writeLine("<xsd:sequence>", 2);
        
        if (    dco.getModule().getType() == DcModule._TYPE_PROPERTY_MODULE || 
                dco.getModule().getType() == DcModule._TYPE_ASSOCIATE_MODULE) {
            
            int field = dco instanceof DcProperty ? DcProperty._A_NAME : DcAssociate._A_NAME;
            String label = getValidTag(dco.getField(field).getSystemName());
            writeLine("<xsd:element name=\"" + label + "\" type=\"xsd:string\"/>", 3);
            
        } else if (
                dco.getModule().getType() == DcModule._TYPE_MEDIA_MODULE || 
                dco.getModule().getType() == DcModule._TYPE_MODULE) {
            
            writeField(dco.getField(DcObject._SYS_MODULE));
            
            for (int fieldIdx : fields) {
                DcField field = dco.getField(fieldIdx);
                if (field != null && !field.getSystemName().endsWith("_persist")) writeField(field);
            }
        }
        
        if (dco.getModule().getChild() != null) {
            String name = getValidTag(dco.getModule().getChild().getSystemObjectNamePlural());
            String reference = getValidTag(dco.getModule().getChild().getSystemObjectName());
            
            writeLine("<xsd:element name=\"" + name + "\"/>", 3);
            addReference(name, reference);
        }
        
        writeLine("</xsd:sequence>", 2);
        writeLine("</xsd:complexType>", 1);
    }
    
    private void startDocument(DcObject dco) throws IOException{
        writeLine("<?xml version=\"1.0\"?>", 0);
        writeLine("<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\">", 0);
        writeLine("<xsd:element name=\"data-crow-objects\">", 1);
        writeLine("<xsd:complexType>", 1);
        writeLine("<xsd:sequence>", 2);
        writeLine("<xsd:element maxOccurs=\"unbounded\" ref=\"" + 
                    getValidTag(dco.getModule().getSystemObjectName())  + 
                  "\"/>", 3);
        writeLine("</xsd:sequence>", 2);
        writeLine("</xsd:complexType>", 1);
        writeLine("</xsd:element>", 0);
    }
    
    private void endDocument() throws IOException {
        writeReferences();
        writeLine("</xsd:schema>", 0);
        bos.flush();
        bos.close();
    }
    
    private void writeReferences() throws IOException {
        
        for (XmlReference reference : references) {
            
            newLine();

            writeLine("<xsd:element name=\"" + reference.getName() + "\">", 1);
            writeLine("<xsd:complexType>", 2);
            writeLine("<xsd:sequence>", 3);
            writeLine("<xsd:element maxOccurs=\"unbounded\" ref=\"" + reference.getReference() + "\"/>", 4);
            writeLine("</xsd:sequence>", 3);
            writeLine("</xsd:complexType>", 2);
            writeLine("</xsd:element>", 1);
        }        
    }
}