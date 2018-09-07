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
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.datacrow.core.DcConfig;
import net.datacrow.core.DcRepository;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.synchronizers.FileSynchronizer;
import net.datacrow.core.utilities.Hash;
import net.datacrow.core.utilities.CoreUtilities;
import net.datacrow.settings.DcSettings;

import org.apache.log4j.Logger;

/**
 * The Drive Manager of Data Crow. Schedules tasks to scan drives for files,
 * allows current file locations to be retrieved and checks the system for new
 * mounted / inserted discs. 
 */
public class DriveManager {

    public static final int _PRECISION_LOWEST = 0;
    public static final int _PRECISION_MEDIUM = 1;
    public static final int _PRECISION_HIGHEST = 2;
    
    private static Logger logger = Logger.getLogger(DriveManager.class);

    private static DriveManager instance = new DriveManager();
    
    private FileSynchronizer fs;
    private DrivePoller dp;

    private Collection<File> drives;
    private Collection<String> excludedDirs = new ArrayList<String>();
    private Map<File, DriveScanner> scanners = new HashMap<File, DriveScanner>();
    
    private Map<File, String> hashes = new HashMap<File, String>();
    
    private boolean drivesWereScanned = false;
    
    private Collection<IDriveManagerListener> pollerListeners = new ArrayList<IDriveManagerListener>();
    private Collection<IDriveManagerListener> scannerListeners = new ArrayList<IDriveManagerListener>();
    private Collection<IDriveManagerListener> synchronizerListeners = new ArrayList<IDriveManagerListener>();
    
    public static DriveManager getInstance() {
        return instance;
    }
    
    private DriveManager()  {
        new File(DcConfig.getInstance().getDataDir(), "temp/").mkdirs();
        
        clean();
    }
    
    public void startDrivePoller() throws JobAlreadyRunningException {
        if (dp != null && dp.isRunning())
            throw new JobAlreadyRunningException();

        dp = dp == null ? new DrivePoller() : dp;
        dp.start();
    }
    
    public void startFileSynchronizer(int precision) throws JobAlreadyRunningException {
        if (fs != null && fs.isRunning())
            throw new JobAlreadyRunningException();

        hashes.clear();
        fs = fs == null ? new FileSynchronizer() : fs;
        fs.start(precision);
    }    
    
    public void sendMessage(Collection<IDriveManagerListener> listeners, String msg) {
        for (IDriveManagerListener listener : listeners)
            listener.notify(msg);
    }

    public void notifyJobStopped(Collection<IDriveManagerListener> listeners) {
        for (IDriveManagerListener listener : listeners)
            listener.notifyJobStopped();
    }

    public void notifyJobStarted(Collection<IDriveManagerListener> listeners) {
        for (IDriveManagerListener listener : listeners)
            listener.notifyJobStarted();
    }
    
    public void restartScan(File drive) {
        if (getDrives().contains(drive)) {
            notifyJobStarted(getScannerListeners());
            DriveScanner scanner = scanners.get(drive);
            scanner.restart();
        }
    }
    
    public void startScanners() throws JobAlreadyRunningException {
        if (isScanActive()) 
            throw new JobAlreadyRunningException();
        
        initializeScanners();
        notifyJobStarted(getScannerListeners());
        
        for (File drive : getDrives()) {
            if (CoreUtilities.isDriveTraversable(drive) && drive.canRead()) {
                scanners.get(drive).cancel();
                scanners.get(drive).start();
            } else { 
                sendMessage(getScannerListeners(), DcResources.getText("msgSkippingUnreadbleDrive", drive.toString()));
            }
        }
        
        drivesWereScanned = true;
    }    
    
    public boolean drivesWereScanned() {
        return drivesWereScanned;
    }

    public void stopScanners() {
        for (DriveScanner scanner : scanners.values()) 
            scanner.cancel();
        
        notifyJobStopped(getScannerListeners());
    }

    public void stopDrivePoller() {
        if (dp != null) dp.cancel();
    }

    public void stopFileSynchronizer() {
        if (fs != null) fs.cancel();
    }    
    
    public void setDrives(Collection<File> drives) throws JobAlreadyRunningException {
        if (isScanActive()) 
            throw new JobAlreadyRunningException();
            
        this.drives = drives;
    }
    
    public Collection<String> getExcludedDirs() {
        return excludedDirs;
    }    
    
    protected String getTempFileSuffix() {
        return "_drive_report.properties";
    }
    
    protected File getTempDir() {
        return new File(DcConfig.getInstance().getDataDir(), "temp/");
    }    
    
    private void initializeScanners() {
        for (File drive : getDrives()) {
            try {
                if (scanners.get(drive) == null) 
                    scanners.put(drive, new DriveScanner(this, drive));    
            } catch (Exception e) {
                logger.error(e, e);
            }      
        }
    }

    public Collection<IDriveManagerListener> getPollerListeners() {
        return pollerListeners;
    }

    public Collection<IDriveManagerListener> getScannerListeners() {
        return scannerListeners;
    }

    public Collection<IDriveManagerListener> getSynchronizerListeners() {
        return synchronizerListeners;
    } 
    
    private void clean() {
        File file = getTempDir();
        
        if (file.exists()) return;
        
        try {
            String[] files = file.list();
            
            if (files == null) return;
            
            for (String filename : files) {
                if (filename.endsWith(getTempFileSuffix())) {
                    File tempFile = new File(getTempDir(), filename);
                    if (tempFile.isFile())
                        tempFile.delete();
                }
            }
        } catch (Exception e) {
            logger.error(e, e);
        }
    }    
    
    public synchronized Collection<File> getDrives() {
        if (drives == null || drives.size() == 0) {
            drives = drives == null ? new ArrayList<File>() : drives;
            for (Drive drive : new Drives().getDrives())
                drives.add(drive.getPath());
        }
        return drives;
    }

    public boolean isScanActive() {
        boolean running = false;
        for (DriveScanner scanner : scanners.values())
            running |= scanner.isRunning();
        
        return running;
    }
    
    protected void notifyScanComplete(DriveScanner scanner) {
        sendMessage(getScannerListeners(), 
                    DcResources.getText("msgScanHasCompletedForX", "" + scanner.getDrive()));

        scanner.cancel();
        if (!isScanActive())
            notifyJobStopped(getScannerListeners());
    }
    
    public void setExcludedDirectories(Collection<String> directories) throws JobAlreadyRunningException {
        if (isScanActive()) 
            throw new JobAlreadyRunningException();
        
        this.excludedDirs = directories;
    }
    
    protected boolean isDirExcluded(File directory) {
        return excludedDirs.contains(directory.toString());
    }
    
    public void exclude(String directory) {
        String dir = directory.toLowerCase();
        dir = dir.replaceAll("'\'", File.separator);
        dir = dir.replaceAll("'/'", File.separator);
        excludedDirs.add(dir);
    }    
    
    private FileInfo getFileInfo(File file, String hash, Long size) {
        hash = hash == null ? Hash.getInstance().calculateHash(file.toString()) : hash;
        size = size == null ? CoreUtilities.getSize(file) : size;
        return new FileInfo(hash, file.toString(), size);        
    }

    public void addPollerListener(IDriveManagerListener listener) {
        pollerListeners.add(listener);
    }

    public void addScannerListener(IDriveManagerListener listener) {
        scannerListeners.add(listener);
    }

    public void addSynchronizerListener(IDriveManagerListener listener) {
        synchronizerListeners.add(listener);
    }

    /**
     * Calculates the hash for the given file and stores it for future references.
     */
    private String getHash(File file) {
        String hash = hashes.get(file);
        
        if (file.length() < DcSettings.getInt(DcRepository.Settings.stHashMaxFileSizeKb)) 
            hash = hash == null ? Hash.getInstance().calculateHash(file.toString()) : hash;
            
        return hash;
    }
    
    /**
     * Retrieves the actual location / file for the given filename.
     * The size and the hash of the original file are used to determine its new
     * location.
     */
    public FileInfo find(FileInfo fi, int precision) {
        File file = new File(fi.getFilename());
        
        if (file.exists())
            return getFileInfo(file, fi.getHash(), fi.getSize());
        
        try {
	        String name =  file.getName();
	        FileInfo result = null;

	        for (String propertyFile : getTempDir().list()) {
	            
	            if (!propertyFile.endsWith(getTempFileSuffix())) continue;
	            
	            File tmpFile = new File(getTempDir(), propertyFile);
	            if (!tmpFile.canRead()) {
	                logger.info("Could not read drive information file: " + tmpFile + " The drive for which this file was created will be skipped!");
	                continue;
	            }
	            
                RandomAccessFile raf = new RandomAccessFile(tmpFile, "r");
	            long length = raf.length();
	            
	            while (raf.getFilePointer() < length) {
	                try {
	                    String line = raf.readLine();
	                    int idx = line.indexOf("=");
	                    
	                    Long fodSize = Long.valueOf(line.substring(0, idx));
	                    String fodName = line.substring(idx + 1);
	                    File fod = new File(fodName);
                        
                        // make sure the file exists; might be dealing with info from an unmounted drive!
	                    if (!fod.exists()) {
	                        logger.info("The file as found in " + tmpFile + " does not exist and will be skipped.");
	                        continue;
	                    }
	                    
                        boolean match = false;
                        String newHash = null;
                        
                        // low: match file name
                        if (precision == DriveManager._PRECISION_LOWEST && name.equals(fod.getName())) {
                            match = true;
                            
                        // medium: match on file size and file name
                        } else if (precision == DriveManager._PRECISION_MEDIUM && 
                                   name.equals(fod.getName()) && fodSize.equals(fi.getSize())) {
                            match = true;
	                   
                        // high: match on file size and file hash
                        } else if (precision == DriveManager._PRECISION_HIGHEST) {
	                       
                            if (fodSize.equals(fi.getSize())) {
                            
                                newHash = getHash(fod);
    	                       
                                // can happen when out of memory, when the file is not readable or when the settings
                                // do not allow for the hash to be calculated based on the file size.
                                if (newHash == null)
                                    continue;
                                
                                hashes.put(fod, newHash);
                                
    	                        if (newHash.equals(fi.getHash())) {
    	                           match = true;
    	                        }
                            }
                        }
	                    
                        if (match) {
                            // calculate the new hash if needed
                            newHash = getHash(fod);
                            Long filesize = CoreUtilities.getSize(fod);
                            
                            logger.info("Match found for " + name + " (hash: " + newHash + ", filesize " + String.valueOf(filesize) + ")");
                            result = getFileInfo(fod, newHash, filesize);
                            break;
                        }
		            } catch (IOException ioe) {
		                logger.error(ioe, ioe);
		            } 
	            }
                
                try {
                    raf.close();
                } catch (IOException ioe) {
                    logger.error("Could not close RAF", ioe);
                }
                
                if (result != null)
                    return result;
	        }
        } catch (Exception e) {
        	logger.error(e, e);
        }
        
        return null;
    }
}
