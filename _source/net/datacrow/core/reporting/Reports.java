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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.datacrow.core.DcConfig;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.utilities.Directory;

public class Reports {

    private Map<Integer, String> folders = new HashMap<Integer, String>();
    
    public Reports() {
        for (DcModule module : DcModules.getModules()) {
            if (module.isSelectableInUI()) {
                String path = DcConfig.getInstance().getReportDir() + module.getName().toLowerCase().replaceAll("[/\\*%., ]", "");
                File file = new File(path);
                if (	!file.exists() && 
                		!file.getParentFile().equals(new File(DcConfig.getInstance().getInstallationDir())))
                    file.mkdirs();
                
                folders.put(module.getIndex(), path);
            }
        }
   }
    
    public Collection<String> getFolders() {
        return folders.values();
    }
    
    public boolean hasReports(int module) {
        String folder = folders.get(module);
        if (folder != null) {
            String[] extensions = {"jasper"};
            Directory dir = new Directory(folder, true, extensions);
            Collection<String> files = dir.read();
            if (files.size() > 0) return true;
        }
        return false;
    }
    
    public Collection<Report> getReports(int module) {
        String folder = folders.get(module);
        
        Collection<Report> reports = new ArrayList<Report>();
        if (folder != null) {
            String[] extensions = {"jasper"};
            Directory directory = new Directory(folder, true, extensions);
            Collection<String> files = directory.read();
            for (String filename : files) {
                Report rf = new Report(filename);
                reports.add(rf);
            }
        }
        
        return reports;
    }
}
