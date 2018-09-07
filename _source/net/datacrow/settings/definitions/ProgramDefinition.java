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

import net.datacrow.core.utilities.CoreUtilities;

public class ProgramDefinition extends Definition {

	private static final long serialVersionUID = -1751426625622194638L;

	private String extension = "";
	private String program = "";
	private String parameters = "";
	
	public ProgramDefinition(String extension, String program, String parameters) {
		this.extension = extension;
		this.program = program;
		this.parameters = parameters;
	}
	
	public boolean hasParameters() {
	    return !CoreUtilities.isEmpty(parameters);
	}
	
	public String getExtension() {
		return extension;
	}

	public String getProgram() {
		return program;
	}

    public String getParameters() {
        return parameters;
    }

    @Override
    public String toSettingValue() {
        return extension + "/&/" + program + "/&/" + parameters;
    }    
    
    @Override
	public String toString() {
		return "[" + extension + "] " + program + " " + parameters;
	}
}
