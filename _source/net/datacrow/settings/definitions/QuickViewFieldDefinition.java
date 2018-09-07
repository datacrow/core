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

import net.datacrow.core.modules.DcModule;

public class QuickViewFieldDefinition extends Definition {

	private static final long serialVersionUID = 8343642952728935678L;

	private int module;
    private int field;
    private boolean enabled;
    private String direction = "";
    private int maxLength = 0;
    
    public QuickViewFieldDefinition(int module, int field, boolean enabled, String direction, int maxLength) {
        super();
        
        this.module = module;
        this.field = field;
        this.enabled = enabled;
        this.direction = direction;
        this.maxLength = maxLength;
    }
    
    public int getMaxLength() {
        return maxLength;
    }

    public String getDirectrion() {
        return direction;
    }

    public int getField() {
        return field;
    }    
    
    public boolean isEnabled() {
        return enabled;
    }      

    @Override
    public String toSettingValue() {
        return field + "/&/" + enabled + "/&/" + direction + "/&/" + maxLength;
    }    
    
    public Object[] getDisplayValues(DcModule module) {
        return new Object[] {module.getField(field).getLabel(),
                             enabled, direction, module.getField(field), maxLength};
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof QuickViewFieldDefinition) {
            QuickViewFieldDefinition def = (QuickViewFieldDefinition) o;
            return def.getField() == getField();
        }
        return false;
    }

	@Override
	public int hashCode() {
		return getField() + (module * 10000);
	}
}
