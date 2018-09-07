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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.utilities.StringUtils;

/**
 * A file pattern decides how the filename should be formed, using information
 * from an item.
 * 
 * @author Robert Jan van der Waals
 */
public class FilePattern {

    private final String pattern;
    private final int module;
    
    private Collection<FilePatternPart> parts = new ArrayList<FilePatternPart>();
    
    /**
     * Creates a new instance.
     * @param pattern String representation of the pattern.
     * @param module The module index.
     */
    public FilePattern(String pattern, int module) {
        this.pattern = pattern;
        this.module = module;
        
        createParts();
    }

    public int getModule() {
        return module;
    }

    public String getPattern() {
        return pattern;
    }

    public Collection<FilePatternPart> getParts() {
        return parts; 
    }

    /**
     * Creates the new filename based on information from the item.
     * @param dco
     * @param oldFile The current file.
     * @param baseDir The current base directory.
     * @return The new filename.
     */
    public String getFilename(DcObject dco, File oldFile, File baseDir) {
        StringBuffer sb = new StringBuffer();
        
        // use the base dir or use the location of the old file
        String base = "";
        if (baseDir != null)
            base = baseDir.toString();
        else if (oldFile.getParent() != null)
            base = oldFile.getParent();

        base = base.endsWith("/") || base.endsWith("\\") ? base.substring(0, base.length() - 1) : base;
        sb.append(base);
        
        boolean first = true;
        for (FilePatternPart part : parts) {
            String s = part.get(dco);
            
            if (first && !s.startsWith("/") && !s.startsWith("\\"))
                sb.append(File.separator);
            
            sb.append(s);
            first = false;
        }

        String name = oldFile.getName();
        int idx = name.lastIndexOf("."); 
        if (idx > -1)
            sb.append(name.substring(idx));
        
        return sb.toString();
    }

    private DcField getField(int modIdx, String label) {
        DcModule module = DcModules.get(modIdx);
        
        if (module == null) 
            return null;
        
        for (DcField field : module.getFields()) {
            if (field.getSystemName().equals(label)) { 
                return field;
            
            } else if (module.getParent() != null &&  
                       module.getParent().getObjectName().equals(label)) {
                
                return field;
            }
        }
        return null;
    }

    private void createParts() {
        String s = pattern;
        
        while (s.indexOf('[') > -1) {
            String field = StringUtils.getValueBetween("[", "]", s);
            if (field.length() > 0) {
                String tag = '[' + field + ']';
                int pos = s.indexOf(tag);
                String prefix = pos > 0 ? s.substring(0, pos) : "";
                
                s = s.substring(pos + tag.length());
                
                int next = s.indexOf('[');
                String suffix = s.startsWith("<") ? "" : s.substring(0, (next > -1 ? next : s.length()));
                
                FilePatternPart part = new FilePatternPart(getField(module, field));
                part.setSuffix(suffix);
                part.setPrefix(prefix);
                parts.add(part);
                
                s = s.substring(suffix.length());
            }
        }
    }
    
    public String toStorageString() {
        String storage = "<FILE-PATTERN>\n";
        storage += "<MODULE>" + getModule() + "</MODULE>\n";
        storage += "<STRING>" + getPattern() + "</STRING>\n";
        storage += "</FILE-PATTERN>\n";
        return storage;
    }

    @Override
    public String toString() {
        return getPattern();
    }
    
    @Override
    public boolean equals(Object o) {
        return o instanceof FilePattern ? ((FilePattern) o).getPattern().equals(getPattern()) : false;
    }

    @Override
    public int hashCode() {
        return pattern.hashCode();
    }    
}
