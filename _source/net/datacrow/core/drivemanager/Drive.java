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

package net.datacrow.core.drivemanager;

import java.io.File;

import net.datacrow.core.utilities.CoreUtilities;

public class Drive {

    private File path;
    private String displayValue;
    
    public Drive(File path) {
        this.path = path;
        String s = CoreUtilities.isSystemDrive(path) ? CoreUtilities.getSystemName(path) : path.toString();
        displayValue = CoreUtilities.isEmpty(s) ? path.getPath() : s;
    }

    public File getPath() {
        return path;
    }

    @Override
    public String toString() {
        return displayValue;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Drive && ((Drive) o).getPath().equals(getPath());
    }

    @Override
    public int hashCode() {
        return getPath().hashCode();
    }
}
