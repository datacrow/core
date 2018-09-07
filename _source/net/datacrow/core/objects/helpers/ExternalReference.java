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

package net.datacrow.core.objects.helpers;

import net.datacrow.core.objects.DcObject;

public class ExternalReference extends DcObject {

    private static final long serialVersionUID = 9031499353731926500L;

    public static final int _EXTERNAL_ID = 151;
    public static final int _EXTERNAL_ID_TYPE = 152;
    
    public ExternalReference(int module) {
        super(module);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ExternalReference && ((DcObject) o).getID() != null &&  
               ((DcObject) o).getID().equals(getID());
    }

    @Override
    public int getDefaultSortFieldIdx() {
        return ExternalReference._EXTERNAL_ID;
    }

    @Override
    public int getDisplayFieldIdx() {
        return ExternalReference._EXTERNAL_ID;
    }

    @Override
    public String getName() {
        return getDisplayString(_EXTERNAL_ID_TYPE) + ": " + getDisplayString(_EXTERNAL_ID); 
    }
}
