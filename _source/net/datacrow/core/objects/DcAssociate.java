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

import net.datacrow.core.enhancers.IValueEnhancer;
import net.datacrow.core.utilities.CoreUtilities;

/**
 * Represents companies and persons.  
 * 
 * @author Robert Jan van der Waals
 */
public class DcAssociate extends DcObject {

    private static final long serialVersionUID = 6420907381739662714L;
    
    public static final int _A_NAME = 1;
    public static final int _B_DESCRIPTION = 2;
    public static final int _C_WEBPAGE = 3;
    public static final int _D_PHOTO = 4;
    public static final int _E_FIRSTNAME = 5;
    public static final int _F_LASTTNAME = 6;
    
    /**
     * Creates a new instance.
     * @param module
     */
    public DcAssociate(int module) {
        super(module);
    }

    /**
     * Executed before inserting or updating the item.
     */
    @Override
    public void beforeSave() throws ValidationException {
        setName();
        super.beforeSave();
    }

	@Override
	public int getDefaultSortFieldIdx() {
		return DcAssociate._A_NAME;
	}
	
    /**
     * Applies the enhancers on this item.
     * @see ValueEnhancers 
     * @param update Indicates if the item is new or existing.
     */
	@Override
    public void applyEnhancers(boolean update) {
        for (DcField field : getFields()) {

            for (IValueEnhancer enhancer : field.getValueEnhancers()) {
                if (enhancer.isEnabled() && 
                    (update && enhancer.isRunOnUpdating() || !update && enhancer.isRunOnInsert())) {
                    Object newVal = enhancer.apply(field, new String[] {getDisplayString(_E_FIRSTNAME), getDisplayString(_F_LASTTNAME)});
                    Object oldVal = getValue(field.getIndex());
                    
                    if (!CoreUtilities.isEmpty(newVal) && !newVal.equals(oldVal))
                        setValue(field.getIndex(), newVal);
                }
            }
        }
    }  

	/**
     * Sets the various name parts. The last and first name are calculated if only the
     * full name is available. If only the last and first name are available the full name
     * is created out of these parts.
     */
    public void setName() {
        String name = (String) getValue(DcAssociate._A_NAME);
        String firstname = (String) getValue(DcAssociate._E_FIRSTNAME);
        String lastname = (String) getValue(DcAssociate._F_LASTTNAME);
        
        boolean isNameSet = !CoreUtilities.isEmpty(name);
        boolean isFirstNameSet = !CoreUtilities.isEmpty(firstname);
        boolean isLastNameSet = !CoreUtilities.isEmpty(lastname);
        
        if (!isNameSet && (isLastNameSet || isFirstNameSet)) {
        	name = CoreUtilities.getName(firstname, lastname);
            setValue(DcAssociate._A_NAME, name);
        } else if (isNameSet && (!isLastNameSet || !isFirstNameSet)) {
        	firstname = CoreUtilities.getFirstName(name);
        	lastname = CoreUtilities.getLastName(name);
        	name = CoreUtilities.getName(firstname, lastname);
        	setValueLowLevel(DcAssociate._A_NAME, name);
        	setValueLowLevel(DcAssociate._E_FIRSTNAME, firstname);
        	setValueLowLevel(DcAssociate._F_LASTTNAME, lastname);
        	
        	applyEnhancers(true);
        }
    }

    @Override
    public int getSystemDisplayFieldIdx() {
        return DcAssociate._A_NAME;
    }
    
    @Override
    public String toString() {
		return getValue(DcAssociate._A_NAME) != null  ? (String) getValue(DcAssociate._A_NAME) : "";
    }  

    @Override
    public String getName() {
        return toString();
    }    
    
    /**
     * Returns the name formatted as First name, Last name.
     */
    public String getNameNormal() {
        if (isFilled(DcAssociate._E_FIRSTNAME) && isFilled(DcAssociate._F_LASTTNAME))
            return (getDisplayString(DcAssociate._E_FIRSTNAME) + " " + getDisplayString(DcAssociate._F_LASTTNAME)).trim();

        return getName();
    }     
}
