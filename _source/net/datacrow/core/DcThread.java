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

package net.datacrow.core;

/**
 * Extended Thread class. Allows for easy cancellation and offers the ability
 * to cancel all other threads part of the same thread group.
 * 
 * @author Robert Jan van der Waals
 */
public class DcThread extends Thread {
    
    private boolean canceled = false;

    /**
     * Creates a new instance.
     * @param group the thread Group this thread belongs to.
     * @param name  name of this threat, for reporting purposes.
     */
    public DcThread(ThreadGroup group, String name) {
        super(group, name);
    }

    /**
     * Cancels the current thread. Implementers of this class should 
     * use the {@link #isCanceled()} method to check whether their operation should be 
     * cancelled.
     */
    public synchronized void cancel() {
        canceled = true;
    }
    
    /**
     * Indicates if this thread has been cancelled. 
     * @return  cancelled y/n
     */
    public synchronized boolean isCanceled() {
        return canceled;
    }

    /**
     * Cancel this thread and all threads belonging to the thread group. Implementers of this 
     * class should use the {@link #isCanceled()} method to check whether their operation should 
     * be cancelled.
     */
    public void cancelOthers() {
        DcThread[] threads = new DcThread[getThreadGroup().activeCount() * 2];
        getThreadGroup().enumerate(threads, false);
        for (DcThread thread : threads) {
            if (thread != this && thread != null)
                thread.cancel();
        }
        
        while (getThreadGroup().activeCount() > 1) {
            try {
                sleep(100);
            } catch (Exception e) {}
        }
    }
}
