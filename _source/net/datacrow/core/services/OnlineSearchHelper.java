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

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.datacrow.core.DcRepository;
import net.datacrow.core.fileimporter.FileImporter;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.services.plugin.IServer;
import net.datacrow.core.utilities.StringUtils;

import org.apache.log4j.Logger;

/**
 * Simple online search which can run completely in the background.
 * It has implemented the {@link IOnlineSearchClient} interface. This class can
 * be used by other processes which want to enable online search (such as the {@link FileImporter})
 * 
 * @author Robert Jan van der Waals
 */
public class OnlineSearchHelper implements IOnlineSearchClient {
    
    private static Logger logger = Logger.getLogger(OnlineSearchHelper.class.getName());
    
    private final int module;
    
    private SearchTask task;
    
    private int maximum = 2;
    
    private IServer server;
    private Region region;
    private SearchMode mode;
    private int itemMode;
    
    private Collection<DcObject> result = new ArrayList<DcObject>();
    
    /**
     * Creates a new instance.
     * @param module The module index
     * @param itemMode {@link SearchTask#_ITEM_MODE_FULL} or {@link SearchTask#_ITEM_MODE_SIMPLE} 
     */
    public OnlineSearchHelper(int module, int itemMode) {
        this.module = module;
        this.itemMode = itemMode;
    }

    /**
     * The server to be used.
     * @param server
     */
    public void setServer(IServer server) {
        this.server = server;
    }

    /**
     * The region to be used.
     * @param region
     */
    public void setRegion(Region region) {
        this.region = region;
    }

    /**
     * The search mode to be used.
     * @param mode
     */
    public void setMode(SearchMode mode) {
        this.mode = mode;
    }

    /**
     * The maximum search result.
     * @param maximum
     */
    public void setMaximum(int maximum) {
        this.maximum = maximum;
    }
    
    /**
     * Queries for new information for the supplied item.
     * Uses the services URL as stored in the item (see {@link DcObject#_SYS_SERVICEURL}).
     * @param item
     * @return The supplied item. Either updated or not. 
     */
    public DcObject query(DcObject item) {
        IServer server = getServer();
        Region region = getRegion(server);
        
        task = server.getSearchTask(this, getSearchMode(server), region, null, item);
        task.setItemMode(SearchTask._ITEM_MODE_FULL);
        
        try {
            return task.getItem(new URL((String) item.getValue(DcObject._SYS_SERVICEURL)));
        } catch (Exception e) {
            logger.error(e, e);
            return item;
        }
    }
    
    /**
     * Queries for items and checks if they are similar to the supplied item
     * the item most similar to the base item will be returned. Similarity is based
     * on the values of the provided field indices. 
     * @param base The item to check the results against.
     * @param query The query to base the search on.
     * @param matcherFieldIdx The field indices used to check for similarity.
     * @return The most similar result or null. 
     */
    public DcObject query(DcObject base, String query, int[] matcherFieldIdx) {
        IServer server = getServer();
        Region region = getRegion(server);
        
        task = server.getSearchTask(this, getSearchMode(server), region, query, base);
        task.setItemMode(itemMode);
        task.setMaximum(maximum);
        task.run();
        
        return getMatchingItem(base, matcherFieldIdx);
    }
    
    /**
     * Searches for items based on the provided query string.
     * @param query
     * @return Collection of results.
     */
    public List<DcObject> query(String query, DcObject client) {
        IServer server = getServer();
        Region region = getRegion(server);
        
        task = server.getSearchTask(this, getSearchMode(server), region, query, client);
        task.setItemMode(itemMode);
        task.setMaximum(maximum);
        task.run();
        
        return new ArrayList<DcObject>(result);
    }    

    /**
     * Retrieves the server to be used. If no server has yet been set the default server
     * will be used. If no default server is available it will be selected at random.
     * @return The server to be used.
     */
    private IServer getServer() {
        OnlineServices os = Servers.getInstance().getOnlineServices(module);
        IServer defaultSrv = 
            os.getServer(DcModules.get(module).getSettings().getString(DcRepository.ModuleSettings.stOnlineSearchDefaultServer));
        
        IServer server = this.server != null ? this.server : defaultSrv;
        return server == null ? (IServer) os.getServers().toArray()[0] : server;
    }

    /**
     * Retrieves the region to be used. In case a region has already been specified this
     * region will be used only when the region is part of the provided server.
     * If not, the default region will be used. If no default region is available it will be selected 
     * at random or be left empty if the server simply doesn't have any regions.
     * @param server The server for which a region is being retrieved.
     * @return The region to be used or null.
     */
    private Region getRegion(IServer server) {
        OnlineServices os = Servers.getInstance().getOnlineServices(module);
        Region region = this.region != null ? this.region : os.getDefaultRegion();
        if (region != null) {
            boolean partOfServer = false;
            
            for (Region serverRegion : server.getRegions()) {
                if (serverRegion.getCode().equals(region.getCode())) {
                    region = serverRegion;
                    partOfServer = true;
                }
            }
            if (!partOfServer) region = null;
        } 
            
        return region == null && server.getRegions().size() > 0 ? (Region) server.getRegions().toArray()[0] : region;
    }
    
    /**
     * Retrieves the search mode to be used. In case a mode has already been specified this
     * mode will be used only when the mode is part of the provided server.
     * If not, the default mode will be used. If no default mode is available it will be selected 
     * at random or be left empty if the server simply doesn't support search modes.
     * @param server The server for which a search mode is being retrieved.
     * @return The search mode to be used or null.
     */    
    private SearchMode getSearchMode(IServer server) {
        OnlineServices os = Servers.getInstance().getOnlineServices(module);
        SearchMode mode = this.mode == null ? os.getDefaultSearchMode() : this.mode;
        
        if (server.getSearchModes() == null) 
            return null;
        
        if (mode != null) {
            boolean partOfServer = false;
            for (SearchMode serverMode : server.getSearchModes()) {
                if (serverMode.getDisplayName().equals(mode.getDisplayName())) {
                    mode = serverMode;
                    partOfServer = true;
                }
            }
            if (!partOfServer) mode = null;
        } 
        
        if (mode == null && server.getSearchModes() != null) {
            for (SearchMode serverMode : server.getSearchModes())
                mode = serverMode.keywordSearch() ? serverMode : mode;
        }
            
        return mode;
    }
    
    /**
     * Retrieves the matching result. Each of the results is checked against the provided 
     * base item. The match field indices indicate which values are to be checked.
     * @param base The item to check against.
     * @param matcherFieldIdx The field indices to use for checking for similarities.
     * @return A matching result or null.
     */
    private DcObject getMatchingItem(DcObject base, int[] matcherFieldIdx) {
        for (DcObject dco : result) {
            boolean match = true;
            for (int i = 0; i < matcherFieldIdx.length; i++) {
                Object o1 = base.getValue(matcherFieldIdx[i]);
                Object o2 = dco.getValue(matcherFieldIdx[i]);
                
                String value1 = o1 == null || o1.toString().equals("-1") ? "" : StringUtils.normalize(o1.toString().trim());
                String value2 = o2 == null || o2.toString().equals("-1") ? "" : StringUtils.normalize(o2.toString().trim());
                
                match &= value1.equals(value2);
            }
            
            if (match) return dco;
        }
        
        return null;
    }
    
    /**
     * Free resources.
     * @param except Do not clear the resources of this item.
     */
    public void clear() {
        task = null;
        server = null;
        region = null;
        result.clear();
        result = null;
    }
    
    @Override
    public void addError(Throwable t) {
        logger.error(t, t);
    }

    @Override
    public void addError(String message) {
        logger.error(message);
    }

    @Override
    public void addMessage(String message) {}

    @Override
    public void addObject(DcObject dco) {
        if (result != null) {
            result.add(dco);
            if (isPerfectMatch(dco)) 
                task.cancel();
        }
    }
    
    private boolean isPerfectMatch(DcObject dco) {
        String string =  StringUtils.normalize(task.getQuery()).toLowerCase();
        String item = StringUtils.normalize(dco.toString()).toLowerCase();
        return string.equals(item);
    }

    @Override
    public void addWarning(String warning) {}

    public DcObject getDcObject() {
        return null;
    }

    @Override
    public DcModule getModule() {
        return DcModules.get(module);
    }

    @Override
    public void processed(int i) {}

    @Override
    public void processing() {}

    @Override
    public void processingTotal(int i) {}

    @Override
    public int resultCount() {
        return result.size();
    }

    @Override
    public void stopped() {}
}
