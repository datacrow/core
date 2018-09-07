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

package net.datacrow.core.services;

import net.datacrow.core.modules.DcModule;
import net.datacrow.core.objects.DcObject;

/**
 * Classes implementing this interface can be notified by the online search tasks
 * about events and retrieved results.
 * 
 * @author Robert Jan van der Waals
 */
public interface IOnlineSearchClient {

    /**
     * Passes a result / item to this client.
     * @param dco
     */
    public void addObject(DcObject dco);
    
    /**
     * Indicates a task is currently running.
     */
    public void processing();
    
    /**
     * Indicates a task has been stopped and a new task can be started.
     */
    public void stopped();
    
    /**
     * Passes a message to this client.
     * @param message
     */
    public void addMessage(String message);
    
    /**
     * Passes an error to this client.
     * @param t 
     */
    public void addError(Throwable t);
    
    /**
     * Passes an error message to this client.
     * @param t 
     */
    public void addError(String message);
    
    /**
     * Passes a warning message to this client.
     * @param t 
     */
    public void addWarning(String warning);
    
    /**
     * Returns the total count of added items (see {@link #addObject(DcObject)})
     */
    public int resultCount();
    
    /**
     * Returns the current module.
     * @return The module
     */
    public DcModule getModule();
    
    /**
     * Passes the count of results which are going to be processed.
     * This way the client knows how many items to expect.
     * @param i The total count.
     */
    public void processingTotal(int i);
    
    /**
     * The current result number being processed (x of x).
     * @param i
     */
    public void processed(int i);
}
