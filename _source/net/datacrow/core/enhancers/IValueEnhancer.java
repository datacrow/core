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

import java.io.Serializable;

import net.datacrow.core.objects.DcField;

/**
 * A value enhancer changes a value before saving the item to the database.
 * Value enhancers should be registered in the {@link ValueEnhancers} class.
 * 
 * @author Robert Jan van der Waals
 */
public interface IValueEnhancer extends Serializable {

    /**
     * Apply the enhancement.
     * @param field The field to enhance.
     * @param value The value.
     * @return The result of the enhancement.
     */
    public Object apply(DcField field, Object value);
    
    /**
     * Creates a string representation which can be stored to disk.
     */
    public String toSaveString();

    /**
     * Unique ID for this enhancer.
     * @return
     */
    public int getIndex();
    
    /**
     * Parses a string representation.
     * @param s
     */
    public void parse(String s);
    
    /**
     * Indicates if this enhancer is enabled.
     */
    public boolean isEnabled();
    
    /**
     * Indicates if the enhancement should be performed after updating an item.
     */
    public boolean isRunOnUpdating();

    /**
     * Indicates if the enhancement should be performed after inserting an item.
     */
    public boolean isRunOnInsert();
}
