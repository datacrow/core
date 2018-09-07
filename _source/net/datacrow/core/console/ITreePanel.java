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

import java.awt.Font;

import net.datacrow.core.objects.DcObject;

public interface ITreePanel {

	void applySettings();
	
	void updateTreeNodes(DcObject dco);
	
	void update(DcObject dco);
	
	void add(DcObject dco);
	
	void setSelected(DcObject dco);
	
	void activate();
	
	void setFont(Font font);
	
	boolean isHoldingItems();
	
	void clear();
	void remove(String key);
	boolean isActivated();
	
	String getName();
	
	void groupBy();
	void sort();
	void refreshView();
	
	boolean isEnabled();
	boolean isLoaded();
	boolean isShowing();
	
	
	void setDefaultSelection();
	
	void setSaveChanges(boolean b);
}
