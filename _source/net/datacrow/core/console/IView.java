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

package net.datacrow.core.console;

import java.awt.Cursor;
import java.util.List;
import java.util.Map;

import net.datacrow.core.clients.IClient;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.wf.tasks.DcTask;

public interface IView extends IClient {
	
	DcObject getSelectedItem();
	
	void activate();
	
	void deactivate();
	
	int getIndex();
	
	void delete();
	
	DcModule getModule();

	boolean isVisible();
	void setVisible(boolean b);
	
	void setParentID(String ID, boolean show);
	
	List<? extends DcObject> getChangedItems();
	
	void setListSelectionListenersEnabled(boolean b);
	
	void setParentView(IView view);
	
	void removeFromCache(String ID);
	
	void undoChanges();
	
	void save();
	void saveSelected();
	
	int getIndex(String key);

	List<String> getItemKeys();
	void setSelected(int idx);
	
	List<String> getSelectedItemKeys();
	List<? extends DcObject> getSelectedItems();
	
	void sort();
	
	void clear();
	void clear(boolean checkForChanges);
	
	void add(Map<String, Integer> keys);
	
	void applyGrouping();
	void add(DcObject dco);
	
	void add(List<DcObject> items);
	
	void loadChildren();
	
	String getParentID();
	
	int getType();
	
	void open(boolean edit);
	
	void applySettings();
	
	void remove(String[] keys);
	
	void saveSettings();
	
	int update(String key);
	
	int update(String key, DcObject item);
	
	boolean isLoaded();
	
	void refreshQuickView();
	
	void setCursor(Cursor cursor);
	
	void setActionsAllowed(boolean b);
	
	void setCheckForChanges(boolean b);
	
	boolean isChangesSaved();
	
	DcTask getCurrentTask();
	
	void applyViewDividerLocation();
	
	String getHelpIndex();
}
