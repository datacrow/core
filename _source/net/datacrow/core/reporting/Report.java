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

package net.datacrow.core.reporting;

import java.io.File;

public class Report {
    
    private String name;
    private String filename;
    
    public Report(String filename) {
        this.filename = filename;
        
        int start = filename.lastIndexOf(File.separator) > -1 ? filename.lastIndexOf(File.separator)  + 1 : 0;
        int end = filename.lastIndexOf(".") < start ? filename.length() : filename.lastIndexOf(".");
        
        this.name = filename.substring(start, end).replaceAll("[_.]", " ");
    }
    
    public String getName() {
        return name;
    }

    public String getFilename() {
        return filename;
    }
    
    public File getFile() {
        return new File(filename);
    }

    @Override
    public String toString() {
        return getName();
    }
}