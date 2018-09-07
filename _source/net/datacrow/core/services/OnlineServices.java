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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

import net.datacrow.core.DcRepository;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcMediaObject;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.services.plugin.IServer;
import net.datacrow.core.services.plugin.ServiceClassLoader;

import org.apache.log4j.Logger;


/**
 * The online services class holds all online service for a specific module.
 * This class is populated by the module class. A specialized class loader is
 * capable of detecting custom made server classes. Based on this information this
 * class gets populated.
 * 
 * @see DcModule#getOnlineServices()
 * @see ServiceClassLoader
 *  
 * @author Robert Jan van der Waals
 */
public class OnlineServices implements Serializable {

    private static final long serialVersionUID = -1807699973082637903L;

    private static Logger logger = Logger.getLogger(OnlineServices.class.getName());
    
    private static final int _SERVICE_NAME = 0;
    private static final int _REGION_CODE = 1;
    private static final int _MODE_NAME = 2;
    private static final int _QUERY = 3;    
    
    private Collection<IServer> servers = new ArrayList<IServer>();
    
    private final int module;
    
    /**
     * Create an instance for the specified module.
     * @param module
     */
    public OnlineServices(int module) {
        this.module = module;
    }
    
    /**
     * Retrieves all registered servers.
     */
    public Collection<IServer> getServers() {
        return servers;
    }
    
    /**
     * Add a server
     * @param server
     */
    public void addServer(IServer server) {
        servers.add(server);
    }
    
    /**
     * The module to which the services belong 
     */
    public int getModule() {
        return module;
    }

    /**
     * Retrieves the service from which the information of the supplied item was retrieved.
     * @see DcObject#_SYS_SERVICE
     * @see DcObject#_SYS_SERVICEURL 
     * @param dco
     * @return The service used to update the item or null
     */
    public IServer getServer(DcObject dco) {
        return getServer(getService(dco, _SERVICE_NAME));
    }
    
    /**
     * Retrieves the region from which the information of the supplied item was retrieved.
     * @see DcObject#_SYS_SERVICE
     * @see DcObject#_SYS_SERVICEURL 
     * @param dco
     * @return The region used to update the item or null
     */
    public Region getRegion(DcObject dco) {
        return getRegion(getService(dco, _REGION_CODE));
    }

    /**
     * Retrieves the selected search mode used when the information of the supplied item 
     * was retrieved.
     * @see SearchMode
     * @param dco
     * @return The search mode or null
     */
    public SearchMode getMode(DcObject dco) {
        return getMode(getService(dco, _MODE_NAME));
    }
    
    /**
     * Retrieves the query used for updating or retrieving the item.
     * @see DcObject#_SYS_SERVICE
     * @see DcObject#_SYS_SERVICEURL 
     * @param dco
     * @return
     */
    public String getQuery(DcObject dco) {
        return getService(dco, _QUERY);
    }    
    
    /**
     * Retrieves the previously used region.
     * @see Region
     */
    public Region getDefaultRegion() {
        String code = DcModules.get(module).getSettings().getString(DcRepository.ModuleSettings.stOnlineSearchDefaultRegion);
        return getRegion(code);
    }
    
    /**
     * Retrieves the previously used server.
     * @see IServer
     */
    public IServer getDefaultServer() {
        String name = DcModules.get(module).getSettings().getString(DcRepository.ModuleSettings.stOnlineSearchDefaultServer);
        return getServer(name);
    }
    
    /**
     * Retrieves the previously used search mode.
     * @see SearchMode
     */
    public SearchMode getDefaultSearchMode() {
        String name = DcModules.get(module).getSettings().getString(DcRepository.ModuleSettings.stOnlineSearchDefaultMode);
        return getMode(name);       
    }
    
    /**
     * Retrieves the server with the given name.
     * @see IServer
     * @return The server for the given name or null if not found.
     */
    public IServer getServer(String name) {
        for (IServer server : getServers()) {
            if (server.getName().equals(name) && server.getModule() == module)
                return server;
        }
        
        return null;
    }
    
    /**
     * Retrieves the region for the given code.
     * @see Region
     * @return The region for the given code or null if not found.
     */    
    private Region getRegion(String code) {
        IServer server = getDefaultServer();
        
        if (server != null && server.getRegions() != null) {
            for (Region region : server.getRegions()) {
                if (region.getCode().equals(code))
                    return region;
            }
        }
        return null;
    }    

    /**
     * Retrieves the search mode for the display name.
     * @see SearchMode
     * @return The search mode for the given display name or null if not found.
     */       
    private SearchMode getMode(String diplayName) {
        IServer server = getDefaultServer();
        if (server != null && server.getSearchModes() != null) {
            for (SearchMode mode : server.getSearchModes()) {
                if (mode.getDisplayName().equals(diplayName))
                    return mode;
            }
        }
        return null;        
    }
    
    /**
     * Retrieves the service information for the given item.
     * @param dco
     * @param type The type indicates which information should be retrieved 
     * (search mode, region, server or query)
     * @see DcObject#_SYS_SERVICE
     * @see DcObject#_SYS_SERVICEURL
     * @return The service or null of none has been set.
     */
    private String getService(DcObject dco, int type) {
        String s = (String) dco.getValue(DcMediaObject._SYS_SERVICE);
        
        if (s == null) return null;
        
        StringTokenizer st = new StringTokenizer(s, "/");
        String[] values = new String[st.countTokens()];
        int i = 0;
        while (st.hasMoreElements())
            values[i++] = ((String) st.nextElement()).trim();
        
        String value = null;
        try {
            value = values[type];
            if (type == _QUERY && value.length() > 0) 
                value = value.substring(value.indexOf("[") + 1, value.lastIndexOf("]"));
        } catch (Exception e) {
            logger.error("Error while trying to get online service information from " + dco, e);
        }
        
        return value;
    }  
    
    /**
     * Retrieves an item (a new instance!) based on the service information of the supplied item.
     * @param dco The item on which the search is based.
     * @return The retrieved item (a new instance) or null if no information could be found.
     * @throws Exception
     */
    public DcObject query(DcObject dco) throws Exception {
        IServer server = getServer(dco);
        Region region = getRegion(dco);
        
        if (server != null) {
            SearchTask task = server.getSearchTask(null, null, region, null, dco);
            return task.query(dco);
        }
        
        return null;
    }    
}
