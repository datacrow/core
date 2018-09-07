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
import java.util.LinkedList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.datacrow.core.resources.DcResources;
import net.datacrow.core.utilities.CoreUtilities;
import net.datacrow.core.utilities.StringUtils;

public class DriveScanner {

    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(DriveScanner.class.getName());
    
    private Logger report;
    private FileHandler handler;
    
    private File drive;
    private DriveManager dm;
    
    private DriveScan ds;
    private final String filename;
    
    public DriveScanner(DriveManager dm, File drive) {
        filename = StringUtils.normalize(drive.toString()) + dm.getTempFileSuffix();

        report = Logger.getAnonymousLogger();
        report.setLevel(Level.INFO);
        report.setUseParentHandlers(false);

        this.drive = drive;
        this.dm = dm;
    }
    
    public void cancel() {
        if (ds != null) ds.cancel();
    }
    
    public boolean isRunning() {
        return ds != null && ds.isRunning();
    }
    
    public boolean start() {
        if (!isRunning()) {
            clean();

            ds = new DriveScan(this);
            ds.start();
            
            dm.sendMessage(dm.getScannerListeners(), DcResources.getText("msgScanningDriveX", drive.toString()));
            
            return true;
        }
        
        return false;
    }
    
    public void restart() {
        cancel();
        start();
    }
    
    private void clean() {
        if (handler != null) {
            handler.close();
            report.removeHandler(handler);
        }
        
        File file = dm.getTempDir();
        for (String tempFilename : file.list()) {
            if (tempFilename.startsWith(filename)) {
                File tempFile = new File(dm.getTempDir(), tempFilename);
                if (tempFile.isFile())
                    tempFile.delete();
            }
        }
        
        try {
            handler = new FileHandler(new File(dm.getTempDir(), filename).toString());
            handler.setFormatter(new DriveReportFormatter());
            report.addHandler(handler);
        } catch (Exception e) {
            logger.error(e, e);
        }
    }        
    
    protected boolean isDirExcluded(File file) {
        return dm.isDirExcluded(file);
    }
    
    protected void notifyScanComplete() {
        dm.notifyScanComplete(this);
    }

    protected File getDrive() {
        return drive;
    }
    
    protected void add(File file) {
        report.info(new StringBuffer(String.valueOf(CoreUtilities.getSize(file))).
                                     append("=").append(file).toString());
    }
    
    private static class DriveScan extends Thread {

        private boolean keepOnRunning = true;
        private DriveScanner ds;
        
        public DriveScan(DriveScanner ds) {
            this.setPriority(Thread.MIN_PRIORITY);
            this.ds = ds;
        }
        
        public void cancel() {
            keepOnRunning = false;
        }
        
        public boolean isRunning() {
            return keepOnRunning && isAlive(); 
        }
        
        @Override
        public void run() {
            LinkedList<File> tasks = new LinkedList<File>();
            
            tasks.add(ds.getDrive());
            while (tasks.size() > 0) {
                if (!keepOnRunning) break;
                
                File file = tasks.removeFirst();
                boolean excluded = ds.isDirExcluded(file);
                if (file.isDirectory() && !excluded) {
                    String[] directoryFiles = file.list();
                    if (directoryFiles != null)
                        for (String directoryFile : directoryFiles) {
                            if (!keepOnRunning) break;
                            
                            File currentFile = new File(file.getPath() + File.separator + directoryFile);
                            if (currentFile.isDirectory())
                                tasks.add(currentFile);
                            else
                                ds.add(currentFile);
                        }
                } else if (!excluded){
                    ds.add(file);
                }
            } 
            
            ds.notifyScanComplete();
            ds = null;
        }
    }
    
    @Override
    public int hashCode() {
        return getDrive().hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        return o instanceof DriveScanner ? 
               getDrive().equals(((DriveScanner) o).getDrive()) : false;
    }
}
