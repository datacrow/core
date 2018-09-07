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

import java.io.Serializable;

public class DcSimpleValue implements Serializable {
    
    private static final long serialVersionUID = 5332435146147496435L;

    private String name;
    private String ID;
    private DcImageIcon icon;
    
    public DcSimpleValue(String ID, String name) {
        this(ID, name, null);
    }
    
    public DcSimpleValue(String ID, String name, DcImageIcon icon) {
        this.name = name;
        this.ID = ID;
        this.icon = icon;
    }
    
    public void setIcon(DcImageIcon icon) {
        this.icon = icon;
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }
    
    public DcImageIcon getIcon() {
        return icon;
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        return o instanceof DcSimpleValue && ((DcSimpleValue) o).getID().equals(getID());
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    protected void finalize() throws Throwable {
        ID = null;
        name = null;
        
        if (icon != null) {
            icon.flush();
            icon = null;
        }
        
        super.finalize();
    }
}