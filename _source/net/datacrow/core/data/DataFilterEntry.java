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

import java.io.Serializable;

import net.datacrow.core.modules.DcModules;
import net.datacrow.core.resources.DcResources;

/**
 * A data filter entry belongs to a data filter.
 * 
 * @author Robert Jan van der Waals
 */
public class DataFilterEntry implements Serializable {
	
	private static final long serialVersionUID = 641926559616446568L;

	public static final String _AND = DcResources.getText("lblAnd");
    public static final String _OR = DcResources.getText("lblOr");
    
    private int module;
    private int field;

    private Operator operator;
    private Object value; 
    
    private String andOr;

    public DataFilterEntry() {};
    
    /**
     * Creates a filter entry.
     * @param module The module to which the specified field belongs.
     * @param field The field to be checked on.
     * @param operator The operator.
     * @param value The value used to test against the input.
     */
    public DataFilterEntry(int module, int field, Operator operator, Object value) {
        this(_AND, module, field, operator, value);
    }
    
    /**
     * Creates a filter entry.
     * @param andOr {@link DataFilterEntry#_AND} or {@link DataFilterEntry#_OR} 
     * @param module The module to which the specified field belongs.
     * @param field The field to be checked on.
     * @param operator The operator.
     * @param value The value used to test against the input.
     */
    public DataFilterEntry(String andOr, int module, int field, Operator operator, Object value) {
        this.module = module;
        this.field = field;
        this.operator = operator;
        this.value = value;
        this.andOr = andOr;
    }
    
    /**
     * The ID of the filter entry.
     */
    public int getID() {
        return (module * 1000) + field;
    }

    public void setModule(int module) {
        this.module = module;
    }

    public void setField(int field) {
        this.field = field;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    /**
     * Specifies if the entry should be treated as an and or an or condition.
     * @see #_AND
     * @see #_OR
     * @param andOr
     */
    public void setAndOr(String andOr) {
        this.andOr = andOr;
    }

    /**
     * Indicates if the filter entry should be treated as an and or a or condition.
     * @see #_AND
     * @see #_OR
     */
    public String getAndOr() {
        return andOr;
    }

    /**
     * Indicates if the filter entry should be treated as an or condition.
     */
    public boolean isOr() {
        return andOr.equals(_OR);
    }

    /**
     * Indicates if the filter entry should be treated as an and condition.
     */
    public boolean isAnd() {
        return andOr.equals(_AND);
    }

    public int getField() {
        return field;
    }

    public int getModule() {
        return module;
    }

    public Operator getOperator() {
        return operator;
    }

    public Object getValue() {
        return value;
    }
    
    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return getAndOr() + " " + DcModules.get(module).getField(field).getLabel() + " " +
               getOperator().getName() +  (getOperator().needsValue() ? " " + getValue() : "");
    }
}
