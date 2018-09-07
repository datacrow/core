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

package net.datacrow.core.filerenamer;

/**
 * This listener can be updated on events from the {@link FileRenamer}.
 * 
 * @author Robert Jan van der Waals
 */
public interface IFileRenamerListener {
   
    /**
     * Notify on a event.
     * @param msg
     */
    public void notify(String msg);
    
    /**
     * Notify of an exception.
     * @param e
     */
    public void notify(Exception e);
    
    /**
     * Notify that a file has been renamed.
     */
    public void notifyProcessed(); 
    
    /**
     * Notify of the amount of files to rename.
     * @param max
     */
    public void notifyTaskSize(int max);
    
    /**
     * Notify the task has been stopped.
     */
    public void notifyJobStopped();
    
    /**
     * Notify the task has been started.
     */
    public void notifyJobStarted();
}
