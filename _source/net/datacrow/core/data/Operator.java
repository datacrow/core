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

package net.datacrow.core.data;

import java.util.ArrayList;
import java.util.Collection;

import net.datacrow.core.DcRepository;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.resources.DcResources;

/**
 * Contains operators which can be used in filters
 * 
 * @see DataFilter
 * @see DataFilterEntry
 * 
 * @author Robert Jan van der Waals
 */
public enum Operator {
    
    /** Exact match*/
    EQUAL_TO(1, DcResources.getText("lblEqualTo"), true),
    
    /** Does no match */
    NOT_EQUAL_TO(2, DcResources.getText("lblNotEqualTo"), true),
    
    /** The string or collection contains a certain value */
    CONTAINS(3, DcResources.getText("lblContains"), true),
    
    /** The string does not contains a certain value */
    DOES_NOT_CONTAIN(4, DcResources.getText("lblDoesNotContain"), true),
    
    /** The value is empty */
    IS_EMPTY(5, DcResources.getText("lblIsEmpty"), false),
    
    /** The value is set */
    IS_FILLED(6, DcResources.getText("lblIsFilled"), false),
    
    /** The string starts with a certain value */
    STARTS_WITH(7, DcResources.getText("lblStartsWith"), true),
    
    /** The string ends with a certain value */
    ENDS_WITH(8, DcResources.getText("lblEndsWith"), true),
    
    /** The number is less then a certain value */
    LESS_THEN(9, DcResources.getText("lblLessThen"), true),
    
    /** The number is greater then a certain value */
    GREATER_THEN(10, DcResources.getText("lblGreaterThen"), true),
    
    /** The date is before a certain value */
    BEFORE(11, DcResources.getText("lblBefore"), true),
    
    /** The date is after a certain value */
    AFTER(12, DcResources.getText("lblAfter"), true),

    DAYS_BEFORE(13, DcResources.getText("lblDaysBefore"), true),
    
    DAYS_AFTER(14, DcResources.getText("lblDaysAfter"), true),
    
    TODAY(15, DcResources.getText("lblToday"), false),
    
    MONTHS_AGO(16, DcResources.getText("lblMonthsAgo"), true),
    
    YEARS_AGO(17, DcResources.getText("lblYearsAgo"), true),
    
    /** The string or collection contains a certain value */
    CONTAINSVALUE(18, DcResources.getText("lblContainsValue"), true);
    
    private final int index;  
    private final String name; 
    private boolean needsValue;
    
    Operator(int index, String name, boolean needsValue) {
        this.index = index;
        this.name = name;
        this.needsValue = needsValue;
    }

    /**
     * The unique index of the filter.
     */
    public int getIndex() {
        return index;
    }

    /**
     * The name of the operator.
     */
    public String getName() {
        return name;
    }

    /**
     * Indicates if the filter requires a value in order for it to be successful. 
     */
    public boolean needsValue() {
        return needsValue;
    }
    
    /**
     * Get all operators which can be applied on the specific field.
     * @param field
     */
    public static Collection<Operator> get(DcField field, boolean simple) {
        ArrayList<Operator> operators = new ArrayList<Operator>();

        if (field.getIndex() == DcObject._SYS_LENDBY) {
            operators.add(EQUAL_TO);
            return operators;
        }
        
        if (    field.getValueType() != DcRepository.ValueTypes._PICTURE &&
                field.getValueType() != DcRepository.ValueTypes._DCOBJECTCOLLECTION) {
            operators.add(EQUAL_TO);
            operators.add(NOT_EQUAL_TO);
        }

        if (field.getValueType() != DcRepository.ValueTypes._BOOLEAN) {
            operators.add(IS_EMPTY);
            operators.add(IS_FILLED);
        }
        
        if (field.getValueType() == DcRepository.ValueTypes._PICTURE) {
            operators.add(IS_EMPTY);
            operators.add(IS_FILLED);
            
        } else if (
                field.getValueType() == DcRepository.ValueTypes._DATE ||
                field.getValueType() == DcRepository.ValueTypes._DATETIME) {
            
        	operators.add(BEFORE);
            operators.add(AFTER);
            
            if (!simple) {
                operators.add(TODAY);
                operators.add(DAYS_BEFORE);
                operators.add(DAYS_AFTER);
                operators.add(MONTHS_AGO);
                operators.add(YEARS_AGO);
            }
            
    	} else if (field.getValueType() == DcRepository.ValueTypes._LONG) {
            operators.add(LESS_THEN);
            operators.add(GREATER_THEN);
        } else if (field.getValueType() != DcRepository.ValueTypes._BOOLEAN) {
            
            if (field.getValueType() != DcRepository.ValueTypes._LONG &&
            	field.getValueType() != DcRepository.ValueTypes._DATE && 
            	field.getValueType() != DcRepository.ValueTypes._DATETIME &&
            	field.getValueType() != DcRepository.ValueTypes._DCOBJECTREFERENCE) {

            	operators.add(CONTAINS);
	            operators.add(DOES_NOT_CONTAIN);
	            
	            if (field.getValueType() != DcRepository.ValueTypes._DCOBJECTCOLLECTION) {
    	            operators.add(STARTS_WITH);
    	            operators.add(ENDS_WITH);
	            }
            }
        }
        
        return operators;
    }

    @Override
    public String toString() {
        return getName();
    }
}
