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

package net.datacrow.core.utilities.comparators;

import java.util.Comparator;
import java.util.Date;

import net.datacrow.core.DcRepository;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.utilities.CoreUtilities;

/**
 * @author Robert Jan van der Waals
 */
public class DcObjectComparator implements Comparator<DcObject> {

    public static final int _SORTORDER_ASCENDING = 0;
    public static final int _SORTORDER_DESCENDING = 1;
    
    private final int field;
    private final int order;
    
    private boolean allowReloading = false;
    
    public DcObjectComparator(int field) {
        this.field = field;
        this.order = _SORTORDER_ASCENDING;
    }
    
    public DcObjectComparator(int field, int order) {
        this.field = field;
        this.order = order;
    }
    
    public boolean isAllowReloading() {
        return allowReloading;
    }

    public void setAllowReloading(boolean allowReloading) {
        this.allowReloading = allowReloading;
    }

    @Override
    public int compare(DcObject dco1, DcObject dco2) {
        
        // this is a fix for the container child items since these cannot be loaded on retrieval from the database
        // as the children come from different modules.
        Object o1 = dco1.getValue(field);
        Object o2 = dco2.getValue(field);
        
        if (isAllowReloading() && CoreUtilities.isEmpty(o1) && CoreUtilities.isEmpty(o2)) {
            try {
                dco1.load(dco1.getModule().getMinimalFields(null));
                dco2.load(dco2.getModule().getMinimalFields(null));
                
                o1 = dco1.getValue(field);
                o2 = dco2.getValue(field);
                
            } catch (Exception e) {}
        }

        if (o1 == null && o2 == null)
            return 0;
        else if (o1 == null)
            return -1;
        else if (o2 == null)
            return 1;
        
        if (order == _SORTORDER_DESCENDING) {
            o1 = dco2.getValue(field);
            o2 = dco1.getValue(field);
        }
        
        DcField fld = dco1.getField(field);
        if (o1 instanceof Number && o2 instanceof Number &&
            fld.getValueType() == DcRepository.ValueTypes._BIGINTEGER ||
            fld.getValueType() == DcRepository.ValueTypes._DOUBLE ||
            fld.getValueType() == DcRepository.ValueTypes._LONG) {
        
            Number n1 = (Number) o1;
            Number n2 = (Number) o2;
            
            return (int) (n1.longValue() - n2.longValue());

        } else if (
                fld.getValueType() == DcRepository.ValueTypes._DATE ||
                fld.getValueType() == DcRepository.ValueTypes._DATETIME) {
            
            Date d1 = (Date) o1;
            Date d2 = (Date) o2;
        
            return d1.compareTo(d2);

        } else {
            if (order == _SORTORDER_DESCENDING) {
                return dco2.getNormalizedString(field).toLowerCase().compareTo(
                       dco1.getNormalizedString(field).toLowerCase());  
            } else {
                return dco1.getNormalizedString(field).toLowerCase().compareTo(
                       dco2.getNormalizedString(field).toLowerCase());   
            }
        }
    }
}
