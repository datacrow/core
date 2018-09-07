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

import net.datacrow.core.fileimporter.FileImporter;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.services.Region;
import net.datacrow.core.services.SearchMode;
import net.datacrow.core.services.plugin.IServer;

/**
 * This client can be updated on events and results form a file import process.
 * Additionally it will ask for additional information from this client.
 * 
 * @see FileImporter
 * @author Robert Jan van der Waals
 */
public interface IFileImportClient extends IClient {
    
    /**
     * Indicates if online services should be used.
     */
    public boolean useOnlineServices();

    /**
     * The used search mode.
     * @return The search mode or null.
     */
    public SearchMode getSearchMode();
    
    /**
     * The used server.
     */
    public IServer getServer();

    /**
     * The used region.
     * @return The region or null.
     */
    public Region getRegion();
    
    /**
     * The container to which the resulted items are added.
     * @return A container or null.
     */
    public DcObject getContainer();
    
    /**
     * The storage medium to apply on the resulted items.
     * @return A storage medium or null.
     */
    public DcObject getStorageMedium();
    
    /**
     * The directory usage implementation (free form).
     */
    public int getDirectoryUsage();
    
    public int getModuleIdx();
}
