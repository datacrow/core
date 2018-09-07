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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import net.datacrow.core.DcRepository.ValueTypes;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.objects.DcAssociate;
import net.datacrow.core.objects.DcMapping;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.Picture;

public class XmlWriter extends XmlBaseWriter {
    
    private ItemExporterSettings settings;
    
    private int tagIdent;
    private int valueIdent;
    
    private ItemExporterUtilities utilities;
    private String schemaFile;
    private final int stepSize = 4;
    
    public XmlWriter(String filename, String schemaFile, ItemExporterSettings properties) throws IOException {
        this(new BufferedOutputStream(new FileOutputStream(filename)), filename, schemaFile, properties);
    }
    
    public XmlWriter(BufferedOutputStream bos, String filename, String schemaFile, ItemExporterSettings settings) {
        super(bos);
        
        this.utilities = new ItemExporterUtilities(filename, settings);
        this.schemaFile = schemaFile;
        this.settings = settings;
        
        resetIdent();
    }    
    
    public void resetIdent() {
        tagIdent = stepSize * 1;
        valueIdent =  stepSize * 2;
    }
    
    public void setIdent(int times) {
        tagIdent =  (stepSize * (1 * times)) + stepSize;
        valueIdent = stepSize * (2 * times);
    }
    
    public void startDocument() throws IOException {
        writeTag("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        newLine();
        
        String xsd = new File(schemaFile).getName();
        writeTag("<" + uberTag + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"" + xsd + "\">");
        newLine();
    }
    
    public void endDocument() throws IOException  {
        writeTag("</" + uberTag + ">");
        newLine();
        bos.close();
    }    
    
    public void startEntity(DcObject dco) throws IOException {
		ident(tagIdent);
		writeTag("<" + getValidTag(dco.getModule().getSystemObjectName()) + ">");
		newLine();
    }

    public void endEntity(DcObject dco) throws IOException {
        ident(tagIdent);
        writeTag("</" + getValidTag(dco.getModule().getSystemObjectName()) + ">");
        newLine();
    }
    
    @SuppressWarnings("unchecked")
    public void writeAttribute(DcObject dco, int field) throws IOException {
        
        if (dco.getField(field) == null)
            return;   
        
        ident(valueIdent);
        String tag = getValidTag(dco.getField(field).getSystemName());
        writeTag("<" + tag + ">");
        writeValue(dco, field);
        writeTag("</" + tag + ">");
        newLine();
        
        if (dco.getField(field).getValueType() == ValueTypes._DCOBJECTCOLLECTION) {
            writeTag("<" + tag + "-list>");
            
            if (dco.isFilled(field)) {
                StringBuffer sb = new StringBuffer();
                for (DcObject ref : (Collection<DcObject>) dco.getValue(field)) {
                    if (sb.length() > 0) sb.append(", ");
                    sb.append(ref.toString());
                }
                write(sb.toString());
            }
            
            writeTag("</" + tag + "-list>");
            newLine();
        }
    }

    public void startRelations(DcModule childModule) throws IOException {
        ident(valueIdent);
        writeTag("<" + getValidTag(childModule.getSystemObjectNamePlural()) + ">");
        newLine();
    }

    public void endRelations(DcModule childModule) throws IOException {
        ident(valueIdent);
        writeTag("</" + getValidTag(childModule.getSystemObjectNamePlural()) + ">");
        newLine();
    }
    
    @SuppressWarnings("unchecked")
    private void writeValue(DcObject dco, int field) throws IOException {
       Object o = dco.getValue(field);
       
       if (dco.getModule().getType() == DcModule._TYPE_ASSOCIATE_MODULE) {
           write(((DcAssociate) dco).getNameNormal());
       } else if (o instanceof Collection) {
            newLine();

            tagIdent += (stepSize * 2);
            valueIdent += (stepSize * 2);
            
            for (DcObject subDco : (Collection<DcObject>) o) {
               if (subDco instanceof DcMapping)
                    subDco = ((DcMapping) subDco).getReferencedObject();

                if (subDco != null) { 
	                startEntity(subDco);
	                int fieldIdx = subDco.getSystemDisplayFieldIdx();
	                writeAttribute(subDco, fieldIdx);
	                endEntity(subDco);
                }
            }
            valueIdent -= (stepSize * 2);
            tagIdent -= (stepSize * 2);
            ident(valueIdent);            
            
        } else if (o instanceof Picture) {
            Picture picture = (Picture) o;
            String filename = utilities.getImageURL(picture);
            write(filename);
        } else if (o instanceof Date) {
            Date date = (Date) o;
            write(new SimpleDateFormat("yyyy-MM-dd").format(date));
        } else {
            String text = dco.getDisplayString(field);
            int maximumLength = settings.getInt(ItemExporterSettings._MAX_TEXT_LENGTH);
            if (maximumLength > 0 && text.length() > maximumLength) {
                text = text.substring(0, maximumLength);
                
                if (text.lastIndexOf(" ") > -1)
                    text = text.substring(0, text.lastIndexOf(" ")) + "...";
            }
            write(text);
        }
    }
    
    private void ident(int x) throws IOException {
        String s = "";
        for (int i = 0; i < x; i++)
            s += " ";
        bos.write(s.getBytes());
    }    
    
    private void write(String value) throws IOException {
        String s = value;
        s = s.replaceAll("&", "&amp;");
        s = s.replaceAll("<", "&lt;");
        s = s.replaceAll(">", "&gt;");
        s = s.replaceAll("\"", "&quot;");
        s = s.replaceAll("'", "&apos;");
        bos.write(s.getBytes("UTF8"));
    }    
}
