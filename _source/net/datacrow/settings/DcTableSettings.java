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

package net.datacrow.settings;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.datacrow.core.utilities.StringUtils;

public class DcTableSettings implements Serializable {
    
	private static final long serialVersionUID = -1302562822792157363L;

	private int module; 
    private Map<Integer, Integer> columnWidths = new HashMap<Integer, Integer>();

    /**
     * Creates a new, empty, definition
     * @param module
     */
    public DcTableSettings(int module) {
        this.module = module;
    }
    
    public DcTableSettings(String value) {
        setValue(value);
    }
    
    public int getModuleIdx() {
        return module;
    }

    public void setColumnWidth(int fieldIdx, int width) {
        columnWidths.put(Integer.valueOf(fieldIdx), Integer.valueOf(width));
    }
    
    public int getWidth(int fieldIdx) {
        // 75 is defined as the default width by Swing
        return columnWidths.containsKey(fieldIdx) ? columnWidths.get(Integer.valueOf(fieldIdx)) : 75;
    }
    
    public String getValue() {
        return toString();
    }

    private void setValue(String definition) {
        String s = StringUtils.getValueBetween("[", "]", definition);
        module = Integer.parseInt(s);
        
        for (String value : StringUtils.getValuesBetween("{", "}", definition)) {
            int field = Integer.parseInt(value.substring(0, value.indexOf(",")));
            int width = Integer.parseInt(value.substring(value.indexOf(",") + 1));
            columnWidths.put(Integer.valueOf(field), Integer.valueOf(width));
        }
    }

    @Override
    public String toString() {
        String s = "[" + module + "]";
        for (Integer fieldIdx : columnWidths.keySet()) {
            s += "{" + String.valueOf(fieldIdx.intValue()) + "," + 
                 String.valueOf(columnWidths.get(fieldIdx).intValue()) + "}";
        }
        return s;
    } 
}