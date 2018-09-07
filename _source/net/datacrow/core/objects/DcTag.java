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

import net.datacrow.core.modules.DcModules;

import org.apache.log4j.Logger;


/**
 * A property represents a simple item such as a category or a storage medium.
 * Properties are widely used within Data Crow to allow users to select from 
 * a predefined set of options.
 * 
 * @author Robert Jan van der Waals
 */
public class DcTag extends DcProperty {

    private transient static Logger logger = Logger.getLogger(DcProperty.class.getName());
    
	private static final long serialVersionUID = -3687586617870758603L;

	/**
     * Creates a new instance.
     * @param module
     */
    public DcTag() {
        super(DcModules._TAG);
    }
    
    @Override
    public boolean equals(Object o) {
        boolean equals = false;
        if (o != null && o instanceof DcTag) {
            equals = ((DcTag) o).getID().equals(getID());
        } else if (o instanceof String) {
            logger.debug("Should not check for equality this way", new Exception());
            equals = o.toString().equals(getValue(_A_NAME)) || o.toString().equals(getValue(_ID));
        }
        return equals;
    }
}
