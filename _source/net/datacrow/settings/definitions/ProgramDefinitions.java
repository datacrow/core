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

package net.datacrow.settings.definitions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

public class ProgramDefinitions implements IDefinitions {

	private static final long serialVersionUID = -8505998507769763398L;

	Collection<ProgramDefinition> definitions = new ArrayList<ProgramDefinition>();
    
    @Override
    public void add(Collection<Definition> c) {
        for (Definition definition : c)
            add(definition);
    }

    @Override
    public void add(Definition definition) {
        definitions.add((ProgramDefinition) definition);
    }

    @Override
    public void clear() {
        definitions.clear();
    }

    @Override
    public int getSize() {
        return definitions.size();
    }     
    
	public ProgramDefinition getDefinition(String extension) {
	    for (ProgramDefinition definition : definitions) {
			if (    definition.getExtension().equals(extension) || 
			        definition.getExtension().endsWith(extension)) 
				return definition;
		}
		return null;
	}
	
    @Override
    public boolean exists(Definition definition) {
        return definitions.contains(definition);
    }    	
	
	@Override
    public Collection<ProgramDefinition> getDefinitions() {
	    return definitions;
	}
    
    @Override
    public void add(String s) {
        StringTokenizer st = new StringTokenizer(s, "/&/");
        String[] row = {"", "", ""};
        
        if (st.hasMoreElements())
            row[0] = (String) st.nextElement();
        
        if (st.hasMoreElements())
            row[1] = (String) st.nextElement();

        if (st.hasMoreElements())
            row[2] = (String) st.nextElement();
        
        add(new ProgramDefinition(row[0], row[1], row[2]));
    }
}
