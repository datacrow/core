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
 * A client for Item Export processes
 * 
 * @author Robert Jan van der Waals
 */
public interface IModuleWizardClient extends IClient{

    /**
     * A module import consists of many small tasks.
     * Notifies this client that a new task has been started.
     */
    public void notifyNewTask();
    
	/**
	 * Indicates a sub process has been started. This allows the client to 
	 * either update its secondary progress bar or temporarily use the main 
	 * progress bar to indicate the status of the sub process.
	 *  
	 * @param count size of the task
	 */
	public void notifyStartedSubProcess(int count);
	
	/**
	 * Notifies that a single step in the sub process has been completed.
	 * Solely used for progress tracking.
	 */
	public void notifySubProcessed();
}
