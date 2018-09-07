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

import java.util.List;

import net.datacrow.core.objects.DcObject;
import net.datacrow.core.synchronizers.Synchronizer;

/**
 * A client for item synchronizer processes. The processes will notify this client on events,
 * such as errors and warnings, as well as ask the client for additional information to complete
 * or start the process successfully.
 * 
 * @see Synchronizer
 * 
 * @author Robert Jan van der Waals
 */
public interface ISynchronizerClient extends IFileImportClient {
    
    /**
     * Indicates whether existing files should be reparsed to extract new information.
     * @return  reparse y/n
     */
    public boolean isReparseFiles();
    
    /**
     * Returns the item pick mode. This indicates which items should be synchronized.
     * @see Synchronizer
     * @return  the item pick mode; {@link Synchronizer#_ALL} or {@link Synchronizer#_SELECTED}
     */
    public int getItemPickMode();
    
    /**
     * Returns the item keys to be processed by the Synchronizer.
     * @return  item key list
     */
    public List<String> getItemKeys();
    
    /**
     * The client is notified that an item has been processed. Used for progress tracking 
     * and performing GUI updates.
     * @param dco   the processed (synchronized) item
     */
    public void notifyProcessed(DcObject dco);
}
