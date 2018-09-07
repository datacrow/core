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
import java.util.HashMap;
import java.util.Map;

import net.datacrow.core.resources.DcResources;
import net.datacrow.core.utilities.CoreUtilities;

import org.apache.log4j.Logger;

/**
 * Polls drives for newly inserted discs. Start a scan when a new disc has been 
 * detected.
 */
public class DrivePoller {
    
    private static Logger logger = Logger.getLogger(DrivePoller.class);
    
    private Task task;
    
    protected DrivePoller() {}
    
    public boolean start() {
        if (task == null || !task.isAlive()) {
            task = new Task();
            task.start();
            return true;
        }
        return false;
    }    
    
    public boolean isRunning() {
        return task != null && task.isAlive();
    }
    
    public void cancel() {
        if (task != null) task.cancel();
    }    
    
    private static class Task extends Thread {
        
        private boolean keepOnRunning = true;
        
        public Task() {
            this.setPriority(Thread.MIN_PRIORITY);
        }

        public void cancel() {
            keepOnRunning = false;
        }
        
        @Override
        public void run() {
            DriveManager dm = DriveManager.getInstance();
            
            dm.notifyJobStarted(dm.getPollerListeners());
            
            Map<File, Boolean> drives = new HashMap<File, Boolean>();
            for (File drive : CoreUtilities.getDrives())
                drives.put(drive, CoreUtilities.canRead(drive));
            
            while (keepOnRunning) {
                try {
                    
                    for (File drive : drives.keySet()) {
                        if (drives.get(drive)) {
                            // recheck traversability
                            boolean canRead = CoreUtilities.canRead(drive);
                            drives.put(drive, canRead);
                            
                            if (!canRead)
                                dm.sendMessage(dm.getPollerListeners(), DcResources.getText("msgDiscUnmounted", "" + drive));
                            
                        } else {
                            // this drive was not traversable; refresh the scanned information 
                            // as a new disk might have been inserted.
                            if (CoreUtilities.canRead(drive)) {
                                dm.sendMessage(dm.getPollerListeners(), DcResources.getText("msgDiscMounted", "" + drive));
                                dm.restartScan(drive);
                                drives.put(drive, Boolean.TRUE);
                            }
                        }
                    }
    
                    try {
                        sleep(2000);
                    } catch (Exception ignore) {}
    
                } catch (Exception e) {
                    logger.error(e, e);
                }
            }
            
            dm.notifyJobStopped(dm.getPollerListeners());
            dm = null;
        }
    }
}
