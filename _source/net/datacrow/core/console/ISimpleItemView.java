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

import java.util.Collection;
import java.util.List;

import net.datacrow.core.objects.DcObject;

public interface ISimpleItemView {

    /**
     * Loads the items from the database and adds them to the view.
     */
    void load();
    
    /**
     * Clears the view from its items.
     */
    void clear();
    
    /**
     * Returns all items shown in the view
     * @return  item list
     */
    Collection<DcObject> getItems();
    
    /**
     * 
     * @param visible
     */
    void setVisible(boolean visible);
    
    void applySettings();
    
    void setItems(List<DcObject> items);
    
    void hideDialogActions(boolean b);
    
    void setParentID(String ID);
    
    String getParentID();
}
