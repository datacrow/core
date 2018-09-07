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
 * The starter client is used to listen for events from the {@link DcStarter} class.
 * It will be notified on errors and warnings as well as on general events.
 * 
 * @author Robert Jan van der Waals
 */
public interface IStarterClient {
    
    void configureLog4j();
    
    /**
     * Indicates that the Log4j logger has been configured.
     */
    void notifyLog4jConfigured();
    
    /**
     * Indicates that the a fatal error has occurred.
     */
    void notifyFatalError(String msg);
    
    /**
     * A warning has been send by the {@link DcStarter} class
     */
    void notifyWarning(String msg);
    
    /**
     * An error has occurred
     */
    void notifyError(String msg);
    
    /**
     * Requests interaction to configured the data folder (user directory).
     * @param dataDir   the currently selected data folder.
     */
    void requestDataDirSetup(String dataDir);
}
