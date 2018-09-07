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

package net.datacrow.core.objects;


/**
 * A property represents a simple item such as a category or a storage medium.
 * Properties are widely used within Data Crow to allow users to select from 
 * a predefined set of options.
 * 
 * @author Robert Jan van der Waals
 */
public class DcProperty extends DcObject {

    private static final long serialVersionUID = -936831554081323250L;

    public static final int _A_NAME = 150;
    public static final int _B_ICON = 151;
    public static final int _C_ALTERNATIVE_NAMES = 152;
    
    /**
     * Creates a new instance.
     * @param module
     */
    public DcProperty(int module) {
        super(module);
    }
    
    @Override
    public int getSystemDisplayFieldIdx() {
        return DcProperty._A_NAME;
    }
    
    @Override
	public int getDefaultSortFieldIdx() {
    	return DcProperty._A_NAME;
	}

	/**
     * The filename on which this item is based.
     * @return Returns always null.
     */
    @Override
    public String getFilename() {
        return null;
    }    
    
    @Override
    public String toString() {
        return getValue(_A_NAME) != null ? getValue(_A_NAME).toString() : "";
    }  

    @Override
    public String getName() {
        return toString();
    }
    
    @Override
    public boolean equals(Object o) {
        boolean equals = false;
        if (o != null && o instanceof DcProperty) {
            equals = ((DcProperty) o).getID().equals(getID());
        } else if (o instanceof String) {
            equals = o.toString().equals(getValue(_A_NAME)) || o.toString().equals(getValue(_ID));
        }
        return equals;
    }
}
