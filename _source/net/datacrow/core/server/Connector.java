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

package net.datacrow.core.server;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.datacrow.core.console.IPollerTask;
import net.datacrow.core.data.DataFilter;
import net.datacrow.core.data.DcResultSet;
import net.datacrow.core.enhancers.IValueEnhancer;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.DcSimpleValue;
import net.datacrow.core.objects.Loan;
import net.datacrow.core.objects.Picture;
import net.datacrow.core.objects.ValidationException;
import net.datacrow.core.objects.helpers.User;
import net.datacrow.core.security.SecuredUser;
import net.datacrow.core.server.response.ServerModulesRequestResponse;
import net.datacrow.core.wf.tasks.DcTask;

public abstract class Connector {

    protected static String serverAddress;
    protected static int applicationServerPort;
    protected static int imageServerPort;
    private String username;
    private String password;
    
    public void setServerAddress(String serverAddress) {
        Connector.serverAddress = serverAddress;
	}
    
    public DcServerConnection getServerConnection()  throws Exception {
        throw new Exception("Not available");
    }

	public void setApplicationServerPort(int applicationServerPort) {
	    Connector.applicationServerPort = applicationServerPort;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getServerAddress() {
		return serverAddress;
	}
	
	public void setImageServerPort(int imageServerPort) {
	    Connector.imageServerPort = imageServerPort;
    }

    public int getImageServerPort() {
        return imageServerPort;
    }

	public int getApplicationServerPort() {
		return applicationServerPort;
	}
	
	public void notifyDatabaseFailure(String msg) {
		displayError(msg);
		System.exit(0);
	}
	
	public void displayError(String msg) {}
	
	public boolean displayQuestion(String msg) {
	    return true;
	}
	
	public void applySettings() {}
	public void displayMessage(String msg) {}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
	
   public IPollerTask getPollerTask(Thread thread, String title) {
        return null;
    }
	
	public abstract void deleteModule(int moduleIdx);
	
	public abstract ServerModulesRequestResponse getModules();

    public abstract void initialize();
    
    public abstract void shutdown(boolean saveChanges);
	
	public abstract void dropUser(User user);
	
	public abstract void close();
	
	public abstract void changePassword(User user, String password);
	
	public abstract DcResultSet executeSQL(String sql);
	
	public abstract SecuredUser getUser();
	
	public abstract void createUser(User user, String password);
	
	public abstract void updateUser(User user);
	
	public abstract List<DcObject> getItems(int moduleIdx, int[] fields);
	
	public abstract SecuredUser login(String username, String password);
	
	public abstract List<DcSimpleValue> getSimpleValues(int module, boolean icons);
	
	public abstract int getCount(int module, int field, Object value);
	
	public abstract List<DcObject> getReferencingItems(int moduleIdx, String ID);

	public abstract boolean checkUniqueness(DcObject dco, boolean exitingItem);
	
	public abstract void executeTask(DcTask task);
	
	public abstract boolean deleteItem(DcObject dco) throws ValidationException;
	public abstract boolean saveItem(DcObject dco) throws ValidationException;
	
	public abstract Collection<Picture> getPictures(String parentID);

	public abstract Collection<DcObject> getReferences(int mappingModuleIdx, String parentKey, boolean full);
	
    public abstract Map<String, Integer> getChildrenKeys(String parentKey, int childModuleIdx);
	
    public abstract Collection<DcObject> getChildren(String parentKey, int childModuleIdx, int[] fields);
    
    public abstract Loan getCurrentLoan(String parentKey);
    
    public abstract List<DcObject> getLoans(String parentKey);
	
	public abstract DcObject getItemByExternalID(int moduleIdx, String type, String externalID);
	
	public abstract DcObject getItemByKeyword(int moduleIdx, String keyWord);
	
	public abstract DcObject getItemByDisplayValue(int moduleIdx, String displayValue);
	
	public abstract DcObject getItemByUniqueFields(DcObject dco);
	
	public abstract DcObject getItem(int moduleIdx, String key);
	
	public abstract DcObject getItem(int moduleIdx, String key, int[] fields);
	
	public abstract Map<String, Integer> getKeys(DataFilter df);
	
	public abstract List<DcObject> getItems(DataFilter df);

	public abstract List<DcObject> getItems(DataFilter df, int fields[]);
	
	public abstract Map<DcField, Collection<IValueEnhancer>> getValueEnhancers();
}
