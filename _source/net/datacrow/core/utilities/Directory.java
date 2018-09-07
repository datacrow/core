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

package net.datacrow.core.utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.datacrow.core.clients.IClient;
import net.datacrow.core.resources.DcResources;

import org.apache.log4j.Logger;

/**
 * Reads all files from a specific location
 * @author Robert Jan van der Waals
 */
public class Directory {

    private static Logger logger = Logger.getLogger(Directory.class.getName());
    
    private IClient client;
    
    private String path; 
    private boolean recurse; 
    private String[] extensions;
    
    public Directory(String path, 
                     boolean recurse, 
                     String[] extensions) {

        this.path = path;
        this.recurse = recurse;
        this.extensions = extensions;
    }
   
    public void setClient(IClient client) {
        this.client = client;
    }
    
    private void sendMessageToListener(String msg) {
        if (client != null) client.notify(msg);
    }
    
    /**
     * Retrieves all files and directories from the given locations
     *
     * @param installationDir starting location for reading data
     * @param confirm ask for confirmation for each directory
     * @param recurse read sub directories
     * @param dirs allow directory names in the result
     * @param vExtensions extensions to filter on
     * @param logInformation write messages to the screen and the log
     */
    public List<String> read() {

        List<String> result = new ArrayList<String>();
        File directory = new File(path);
        path = directory.toString();

        if (!directory.exists()) {
            if (DcResources.isInitialized()) sendMessageToListener(DcResources.getText("msgReadingFilesHasFinished"));
            return result;
        }

        boolean useFilter = false;
        if (extensions != null && extensions.length > 0)
            useFilter = true;

        List<String> unhandled = new ArrayList<String>();
        unhandled.add(directory.toString());
        
        File tempFile = null;

        String current;
        String[] list;
        String sTempCurrent;
        while (unhandled.size() > 0) {
            current = unhandled.get(0);
            tempFile = new File(current);

            if (client != null && client.isCancelled()) {
                result.clear();
                if (DcResources.isInitialized()) sendMessageToListener(DcResources.getText("msgReadingFilesHasFinished"));
                return result;
            }
            
            if (tempFile.isDirectory()) {
                if (DcResources.isInitialized()) logger.debug(DcResources.getText("msgReadingFrom", current));
                if (DcResources.isInitialized()) sendMessageToListener(DcResources.getText("msgReadingFrom", current));

                list = tempFile.list();
                if (list != null) {
                    
                    for (int j = 0; j < list.length; j++) {
                        
                        if (client != null) client.notifyProcessed();

                        sTempCurrent = current + (current.endsWith(File.separator) ? "" : File.separator) + list[j];
                        
                        if (!unhandled.contains(sTempCurrent)) {
                            tempFile = new File(sTempCurrent);
                            if (tempFile.isDirectory() && recurse)
                                unhandled.add(sTempCurrent);
                            else if (!tempFile.isDirectory() && (!useFilter || (useFilter && isValid(sTempCurrent, extensions))))
                                result.add(sTempCurrent);
                        }
                    }
                }
            } else {
                if (useFilter && isValid(current, extensions))
                    result.add(current);
                else if (!useFilter)
                    result.add(current);
            }

            unhandled.remove(0);

            if (!recurse) {
                if (DcResources.isInitialized()) sendMessageToListener(DcResources.getText("msgReadingFilesHasFinished"));
                return result;
            }
        }
        if (DcResources.isInitialized()) sendMessageToListener(DcResources.getText("msgReadingFilesHasFinished"));
        return result;
    }

    private static boolean isValid(String file, String[] extensions) {
        String filename = file;
        int last = filename.lastIndexOf(".");
        boolean valid = false;
        if (last + 1 < filename.length()) {
            filename = filename.substring(last + 1, filename.length());
            for (int i = 0; i < extensions.length; i++) {
                if (extensions[i].equals(filename.toLowerCase())) {
                    valid = true;
                    break;
                }
            }
        }
        return valid;
    }
}