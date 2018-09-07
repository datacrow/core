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

import java.util.Collection;
import java.util.Comparator;

import net.datacrow.core.objects.DcObject;

/** 
 * @author Robert Jan van der Waals
 */
public class DcObjectCompositeComparator implements Comparator<DcObject> {

    private Collection<DcObjectComparator> dcocs;

    public DcObjectCompositeComparator(Collection<DcObjectComparator> dcocs) {
        this.dcocs = dcocs;
    }
    
    @Override
    public int compare(DcObject dco1, DcObject dco2) {
        for (DcObjectComparator dcoc : dcocs) {
            int result = dcoc.compare(dco1, dco2);
            
            if (result != 0)
                return result;
        }
        
        return 0;
    }
} 
