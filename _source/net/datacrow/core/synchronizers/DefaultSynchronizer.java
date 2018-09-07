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

package net.datacrow.core.synchronizers;

import java.util.Collection;

import net.datacrow.core.DcConfig;
import net.datacrow.core.DcRepository;
import net.datacrow.core.clients.ISynchronizerClient;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.ValidationException;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.server.Connector;
import net.datacrow.core.services.OnlineSearchHelper;
import net.datacrow.core.services.SearchMode;
import net.datacrow.core.services.SearchTask;
import net.datacrow.core.services.Servers;
import net.datacrow.core.services.plugin.IServer;
import net.datacrow.core.utilities.CoreUtilities;
import net.datacrow.core.utilities.StringUtils;

import org.apache.log4j.Logger;

public abstract class DefaultSynchronizer extends Synchronizer {

    private static final long serialVersionUID = 3378042573756736554L;

    private transient static Logger logger = Logger.getLogger(DefaultSynchronizer.class.getName());
    
    protected DcObject dco;
    
    public DefaultSynchronizer(String title, int module) {
        super(title, module);
    }
    
    @Override
    public boolean canParseFiles() {
        return false;
    }
    
    public DcObject getDcObject() {
        return dco;
    }

    protected int getSearchFieldIdx(SearchMode mode) {
        return  mode != null ? mode.getFieldBinding() : dco.getDisplayFieldIdx();
    }
    
    protected String getSearchString(int field, IServer server) {
        return dco.getDisplayString(field);
    }
    
    protected boolean matches(DcObject result, String searchString, int fieldIdx) {
    	String s = result.getDisplayString(fieldIdx);
    	s = CoreUtilities.isEmpty(s) ? result.toString() : s;
    	
    	// let's also check whether a normal to string is equal to what we're looking for
        boolean matches = StringUtils.equals(searchString, s);
        if (!matches) {
            s = result.toString();
            matches = StringUtils.equals(searchString, s);
        }
        
        return matches;
    }
    
    @Override
    public boolean onlineUpdate(ISynchronizerClient client, DcObject dco) {
        
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            logger.debug(e, e);
        }
        
        String item = dco.toString();
        this.client = client;
        this.dco = dco;

        client.notify(DcResources.getText("msgSearchingOnlineFor", item));
        
        // use the original service settings
        if (dco.getModule().getSettings().getBoolean(DcRepository.ModuleSettings.stMassUpdateUseOriginalServiceSettings))
            return exactSearch(dco);


        boolean updated = false;
        int fieldIdx = getSearchFieldIdx(client.getSearchMode());
        String searchString = getSearchString(fieldIdx, client.getServer());
        searchString = CoreUtilities.isEmpty(searchString) ? item : searchString;
        
        if (CoreUtilities.isEmpty(searchString)) return updated;
        
        OnlineSearchHelper osh = new OnlineSearchHelper(dco.getModule().getIndex(), SearchTask._ITEM_MODE_FULL);
        osh.setServer(client.getServer());
        osh.setRegion(client.getRegion());
        osh.setMode(client.getSearchMode());
        
        if (dco.getModule().getSettings().getBoolean(DcRepository.ModuleSettings.stMassUpdateAlwaysUseFirst))
            osh.setMaximum(1);
        else
            osh.setMaximum(2);
        
        Collection<DcObject> results = osh.query(searchString, dco);
        for (DcObject result : results) {
            if (    dco.getModule().getSettings().getBoolean(DcRepository.ModuleSettings.stMassUpdateAlwaysUseFirst) || 
                    matches(result, searchString, fieldIdx)) {
                merge(dco, result, osh);
                updated = true;
                break;
            }
        }
            
        if (!updated) {
            searchString = StringUtils.normalize(searchString);
            client.notify(DcResources.getText("msgSearchingOnlineFor", searchString));
            results.clear();
            results.addAll(osh.query(searchString, dco));
            for (DcObject result : results) {
                if (matches(result, searchString, fieldIdx)) {
                    merge(dco, result, osh);
                    updated = true;
                    break;
                }
            }
        }

        if (updated) {
            client.notify(DcResources.getText("msgMatchFound", new String[] {searchString, item}));
            client.notifyProcessed(dco);
        }
        
        return updated;
    }
    
    private boolean exactSearch(DcObject dco) {
        try {
            DcObject dcoNew = Servers.getInstance().getOnlineServices(module).query(dco);
            if (dcoNew != null) {
                dco.copy(dcoNew, true, false);
                return true;
            }
        } catch (Exception e) {
            logger.error("Error while retrieving exact match for " + dco, e);
        }        
        return false;
    }     
    
    @Override
    public boolean canUseOnlineServices() {
        return true;
    }
    
    @Override
    public Thread getTask() {
        return new Task(client.getItemKeys());
    }    
    
    private class Task extends Thread {
        
    	private Collection<String> keys;
    	
    	private Task(Collection<String> keys) {
    		this.keys = keys;
    	}
    	
        @Override
        public void run() {
            try {
                client.notifyTaskStarted(keys.size());
                
                Connector connector = DcConfig.getInstance().getConnector();
                
                for (String key : keys) {
                    if (client.isCancelled()) break;
                    
                    DcObject dco = DcConfig.getInstance().getConnector().getItem(module, key, DcModules.get(module).getMinimalFields(null));
                    
                    boolean updated = false;

                    updated = parseFiles(dco);
                    updated |= onlineUpdate(client, dco);
                    
                    client.notifyProcessed();
                    
                    try {
                        if (updated) {
                            connector.saveItem(dco);
                            client.notifyProcessed(dco);
                        }
                    } catch (ValidationException ve) {
                        client.notifyError(ve);
                    }
                    
                    try {
                        sleep(1000);
                    } catch (Exception exp) {}
                }
            } finally {
                client.notify(DcResources.getText("msgSynchronizerEnded"));
                client.notifyTaskCompleted(true, null);
            }  
        }
    }
}
