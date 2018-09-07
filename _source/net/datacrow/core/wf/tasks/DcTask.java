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

package net.datacrow.core.wf.tasks;

import java.util.ArrayList;
import java.util.Collection;

import net.datacrow.core.clients.IClient;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.utilities.CoreUtilities;

public abstract class DcTask implements Runnable {

    public static int _TYPE_DELETE_TASK = 0;
	public static int _TYPE_SAVE_TASK = 1;
	public static int _TYPE_LOAD_TASK = 2;
    
    private int moduleIdx;

    private boolean executing = false;
    private boolean canceled = false;
    
    private Collection<IClient> clients = new ArrayList<IClient>();

    protected Collection<DcObject> items = new ArrayList<DcObject>();

    protected boolean success = false;
    
    private String name;
    private String id;
    
    public DcTask(String name) {
    	this.name = name;
    	this.id = CoreUtilities.getUniqueID();
    }
    
    public abstract int getType();
    
    public String getName() {
    	return name;
    }
    
    public String getId() {
    	return id;
    }
    
    public int getTaskSize() {
        return items != null ? items.size() : 0;
    }
    
    public void addClient(IClient client) {
    	clients.add(client);
    }
    
    public void removeClient(IClient client) {
    	clients.remove(client);
    }
    
    protected void notifyClients(boolean success) {
        for (IClient client : clients) {
            client.notifyTaskCompleted(success, getId());
        }
    }
    
    protected void notifyClients(int type, Throwable t) {
    	for (IClient client : clients) {
	    	if (type == IClient._ERROR)
	    		client.notifyError(t);
	    	else if (type == IClient._WARNING)
	    	    client.notifyWarning(t.getMessage());
	    	else if (type == IClient._INFO)
	    	    client.notify(t.getMessage());
    	}
    }
    
    protected void notifyClients(int type, String msg) {
        for (IClient client : clients) {
	    	if (type == IClient._ERROR)
	    	    client.notifyError(new Exception(msg));
	    	else if (type == IClient._WARNING)
	    	    client.notifyWarning(msg);
	    	else if (type == IClient._INFO)
	    	    client.notify(msg);
    	}		
    }
    
    public void addItems(Collection<DcObject> items) {
    	this.items = items;
    }
    
    public void setModule(int moduleIdx) {
    	this.moduleIdx = moduleIdx;
    }
    
    public DcModule getModule() {
    	return moduleIdx > 0 ? DcModules.get(moduleIdx) : null;
    }
    
    public void addItem(DcObject item) {
    	this.items.add(item);
    }
    
    @Override
    protected void finalize() throws Throwable {
        if (items != null) {
            items.clear();
            items = null;
        }
        
        if (clients != null) {
            clients.clear();
            clients = null;
        }
        
        super.finalize();
    }

	public void startTask() {
        executing = true;
        canceled = false;
        
        for (IClient client : clients) {
            client.notifyTaskStarted(0);
        }
    }
    
	public void endTask() {
    	canceled = true;
    	executing = false;
    	
        for (IClient client : clients) {
        	client.notifyTaskCompleted(success, getId());
        }
    }

	public void cancel() {
    	canceled = true;
    }

	public boolean isRunning() {
    	return executing;
    }

	public boolean isCanceled() {
    	return canceled;
    }
}
