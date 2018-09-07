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

package net.datacrow.core.enhancers;

import java.util.StringTokenizer;

import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcMediaObject;

/**
 * Transforms a title. Based on a word list the title will be transformed as follows: <br>
 * &lt;Word in list&gt;,&lt;Value without Word in list&gt;
 * 
 * @author Robert Jan van der Waals
 */
public class TitleRewriter implements IValueEnhancer {

    private static final long serialVersionUID = 8108647277337071336L;

    private boolean enabled = false;
    private String wordList;
    
    /**
     * Creates a new instance.
     */
    public TitleRewriter() {}    
    
    /**
     * Creates a new instances.
     * @param enabled
     * @param list The word list. Any value starting with a word in the list will be transformed.
     */
    public TitleRewriter(boolean enabled, String list) {
        this.enabled = enabled;
        this.wordList = list;
    }
    
    /**
     * The field to transform.
     * @return {@link DcMediaObject#_A_TITLE}
     */
    public int getField() {
        if (DcModules.getCurrent().getType() == DcModule._TYPE_MEDIA_MODULE) {
            return DcMediaObject._A_TITLE;
        } else {
            return DcModules.getCurrent().getDisplayFieldIdx();
        }
    }    

    public String getWordList() {
        return wordList;
    } 
    
    @Override
    public String toSaveString() {
        return enabled + "/&/" + wordList;
    }

    @Override
    public int getIndex() {
        return ValueEnhancers._TITLEREWRITERS;
    }
    
    @Override
    public void parse(String s) {
        enabled = Boolean.valueOf(s.substring(0, s.indexOf("/&/"))).booleanValue();
        wordList = s.substring(s.indexOf("/&/") + 3, s.length());
    }

    @Override
    public Object apply(DcField field, Object value) {
        Object result = value;

        if (value != null && value instanceof String) {
            String s = (String) result;
            StringTokenizer st = new StringTokenizer(wordList, ",");
            while (st.hasMoreElements()) {
                String keyword = ((String) st.nextElement()) + " ";
                
                if (s.toLowerCase().startsWith(keyword.toLowerCase())) {
                    int start = keyword.length();
                    int end = s.length();
                    
                    if (start < end) 
                        result = s.substring(start, end) + ", " + keyword.trim();
                }
            }
        }
        return result;
    }
    
    @Override
    public boolean isRunOnUpdating() {
        return true;
    }
    
    @Override
    public boolean isRunOnInsert() {
        return true;
    }
    

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
