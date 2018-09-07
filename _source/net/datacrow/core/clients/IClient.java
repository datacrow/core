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

package net.datacrow.core.clients;

/**
 * A generic Data Crow process client. This client will be notified on events for the process(es)
 * it has been registered against.
 * 
 * @author Robert Jan van der Waals
 */
public interface IClient {
    
    /** A warning message type */
    static final int _WARNING = 1;
    /** An error message type */
    static final int _ERROR = 2;
    /** An informative message type */
    static final int _INFO = 2;

    /**
     * The client is notified on a informative message.
     * @param msg   received message
     */
    void notify(String msg);
    
    /**
     * The client is notified of a warning.
     * @param msg   received warning
     */    
    void notifyWarning(String msg);
    
    /**
     * The client is notified of an error.
     * @param t   the receive Throwable / error
     */  
    void notifyError(Throwable t);
    
    /**
     * The task this client has been registered against has been completed.
     * @param success   indicates whether the task was completed successfully
     * @param taskID    the task ID which has been completed
     */
    void notifyTaskCompleted(boolean success, String taskID);
    
    /**
     * This client is notified that the task it has been registered against has been started.
     * @param taskSize  the total size of this tasks, for example the number of items to save. Used 
     * to track progress.
     */
    void notifyTaskStarted(int taskSize);
    
    /**
     * Notify a single step has been processed (for example one item of the 100 items to be saved has 
     * been saved).
     */
    void notifyProcessed();
    
    /**
     * The process this clients listens to can ask this client whether the process should be 
     * cancelled. 
     * @return  cancel y/n
     */
    boolean isCancelled();
}
