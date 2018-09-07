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

package net.datacrow.core.migration;

import java.io.File;

import javax.swing.ImageIcon;

import net.datacrow.core.DcThread;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.security.SecuredUser;

public abstract class ItemMigrater {

    /** Runs the parser in threaded mode  */
    public static final int _MODE_THREADED = 0;
    /** Runs the parser in a non threaded mode  */
    public static final int _MODE_NON_THREADED = 1;

    /** Indicate whether child items should be processed or not. Convenient for container items. */
    protected final boolean processChildren;
    protected final int moduleIdx;
    protected final int mode;
    protected String key;
    protected File file;
    protected DcThread task;
    
    private SecuredUser su;
    
    public ItemMigrater(SecuredUser su, int moduleIdx, String key, int mode, boolean processChildren) throws Exception {
        this.processChildren = processChildren;
        this.moduleIdx = moduleIdx;
        this.key = key;
        this.mode = mode;
        this.su = su;
    }
    
    public SecuredUser getUser() {
    	return su; 
    }
    
    public abstract DcThread getTask();
    public abstract String getName();
    protected abstract void initialize() throws Exception;

    /**
     * The icon used to represent this source reader.
     */
    public ImageIcon getIcon() {
        return null;
    }
    
    /**
     * The unique key used to represent this source reader.
     */
    public String getKey() {
        return key;
    }    
    
    /**
     * Prepares this reader. The file is set and initialized.
     * @param file
     * @throws Exception
     */
    public void setFile(File file) throws Exception {
        this.file = file;
        initialize();
    }
    
    public File getFile() {
        return file;
    }    
    
    protected DcModule getModule() {
        return DcModules.get(moduleIdx);
    }
    
    public void start() throws Exception {
        if (task != null && task.isAlive())
            task.cancel();
        
        task = getTask();
        
        if (mode == _MODE_NON_THREADED) {
            //task.run();
            task.start();
            task.join();
        } else {
            task.start();
        }
    }
    
    public void cancel() {
        if (task != null) task.cancel();
        task = null;
    }    
}
