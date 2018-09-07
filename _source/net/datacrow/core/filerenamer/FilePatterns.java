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
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.datacrow.core.DcConfig;
import net.datacrow.core.utilities.CoreUtilities;
import net.datacrow.core.utilities.StringUtils;

import org.apache.log4j.Logger;

/**
 * Holder of saved file patterns. 
 * 
 * @author Robert Jan van der Waals
 */
public class FilePatterns {

    private static Logger logger = Logger.getLogger(FilePatterns.class.getName());
    
    private static final Map<Integer, Collection<FilePattern>> patterns = new HashMap<Integer, Collection<FilePattern>>();
    private static final File file = new File(DcConfig.getInstance().getApplicationSettingsDir(), "filepatterns.xml");
    
    /**
     * Loads the file patterns from file (XML format).
     */
    public static void load() {
        if (!file.exists())
            return;
        
        try {
            patterns.clear();
            
            byte[] b = CoreUtilities.readFile(file);
            String xml = new String(b, "UTF-8");
            
            while (xml.indexOf("<FILE-PATTERN>") > -1) {
                String part = xml.substring(xml.indexOf("<FILE-PATTERN>"), xml.indexOf("</FILE-PATTERN>") + 15);
                
                int module = Integer.valueOf(StringUtils.getValueBetween("<MODULE>", "</MODULE>", part));
                String string = StringUtils.getValueBetween("<STRING>", "</STRING>", part);
                
                FilePattern fp = new FilePattern(string, module);
                
                Collection<FilePattern> c = patterns.get(module);
                c = c == null ? new ArrayList<FilePattern>() : c;
                c.add(fp);
                patterns.put(module, c);
                
                xml = xml.substring(xml.indexOf(part) + part.length());
            }
        } catch (Exception exp) {
            logger.error("An error occurred while loading filters from " + file, exp);
        }
    }
    
    /**
     * Checks if a file pattern is part of this collection.
     * @param fp
     */
    public static boolean exists(FilePattern fp) {
        return get(fp.getModule()).contains(fp);
    }
    
    /**
     * Save all file patterns to disk (XML format).
     */
    public static void save() {
        String xml = "<FILE-PATTERNS>\n";
        for (Collection<FilePattern> c : patterns.values()) {
            for (FilePattern fp : c)
                xml += "\n" + fp.toStorageString();
        }
        xml += "</FILE-PATTERNS>";
        
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(xml.getBytes("UTF-8"));
            fos.close();
        } catch (Exception exp) {
            logger.error("An error occurred while saving filters to " + file, exp);
        }         
    }
    
    /**
     * Removes a file pattern.
     * @param fp
     */
    public static void delete(FilePattern fp) {
        Collection<FilePattern> c = get(fp.getModule());
        c.remove(fp);
    }
    
    /**
     * Retrieves all file patterns for the specified module.
     * @param module
     * @return An empty or fille collection.
     */
    public static Collection<FilePattern> get(int module) {
        Collection<FilePattern> c = patterns.get(module);
        return c != null ? c : new ArrayList<FilePattern>();
    }
    
    /**
     * Adds a file pattern to this collection.
     * @param fp
     */
    public static void add(FilePattern fp) {
        Collection<FilePattern> c = patterns.get(fp.getModule());
        c = c == null ? new ArrayList<FilePattern>() : c;
        c.remove(fp);
        c.add(fp);
        patterns.put(fp.getModule(), c);
    }
}
