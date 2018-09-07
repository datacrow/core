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

package net.datacrow.core.filerenamer;

import net.datacrow.core.DcConfig;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.server.Connector;

/**
 * A file pattern part is part of a file pattern. It represents a field, 
 * a suffix and a prefix. Based on this information a part of the filename is formed. 
 * 
 * @see FilePattern
 * @author Robert Jan van der Waals
 */
public class FilePatternPart {
    
    private DcField field;
    
    private String suffix;
    private String prefix;
    
    /**
     * Creates a new instance.
     * @param field The field from which the value will be used to create this 
     * part of the filename.
     */
    protected FilePatternPart(DcField field) {
        this.field = field;
    }
    
    /**
     * Create this part of the filename.
     * @param dco
     */
    public String get(DcObject dco) {
        StringBuffer sb = new StringBuffer(prefix);
        
        if (field.getIndex() == dco.getParentReferenceFieldIndex()) {
            String parentID = (String) dco.getValue(field.getIndex());
            int parentModIdx = dco.getModule().getParent().getIndex();
            
            Connector connector = DcConfig.getInstance().getConnector();
            DcObject parent = connector.getItem(parentModIdx, parentID);
            
            sb.append(normalize(parent.toString()));
                 
        } else if (field.getIndex() == DcObject._SYS_FILENAME) {
            String filename = dco.getFilename();
            int idx = filename.lastIndexOf("\\") > -1 ? filename.lastIndexOf("\\") :
                      filename.lastIndexOf("/");
            filename = idx > -1 && idx < filename.length() ? filename.substring(idx + 1) : filename;
            
            idx = filename.lastIndexOf('.');
            filename = idx > -1 && idx < filename.length() ? filename.substring(0, idx) : filename;   
            sb.append(normalize(filename));

        } else {
            sb.append(normalize(dco.getDisplayString(field.getIndex())));
        }
        
        sb.append(suffix);
        return sb.toString();
    }

    /**
     * Remove invalid characters from the string.
     * @param s
     */
    private String normalize(String s) {
        return s != null ? s.replaceAll("[,.!@#$%^&{}'~`\\*;:\r\n!\\?\\[\\]\\\\\\/\\(\\)\"]", "").trim() : "";
    }
    
    public void setField(DcField field) {
        this.field = field;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public DcField getField() {
        return field;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }
}
