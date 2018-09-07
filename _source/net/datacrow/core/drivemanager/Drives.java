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
import java.util.ArrayList;
import java.util.Collection;

import net.datacrow.core.utilities.CoreUtilities;

public class Drives {

    private Collection<Drive> drives = new ArrayList<Drive>();
    
    public Drives() {
        initialize();
    }
    
    public Collection<Drive> getDrives() {
        return drives;
    }

    private void initialize() {
        drives.clear();
        Collection<File> drives = CoreUtilities.getDrives();
        for (File drive : drives) {
            if (CoreUtilities.isDriveTraversable(drive))
                this.drives.add(new Drive(drive));
        }
    }
}
