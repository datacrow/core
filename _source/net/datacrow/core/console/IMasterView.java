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
import java.util.Map;

import net.datacrow.core.DcRepository;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.objects.DcObject;

/**
 * Interface for the MasterView. The master view manages the various view types for a single
 * {@link DcModule}. The module dictates the available views, which are then registered here.
 * The MasterView manages the updates, removals and addition of items to the views it manages.
 * 
 * @author Robert Jan van der Waals
 */
public interface IMasterView {

    /** The table view */
    static final int _TABLE_VIEW = 0;
    /** The list / card view */
    static final int _LIST_VIEW = 1;
    
    /**
     * Make the view with the supplied index active.
     * @param index the view index; {@link #_TABLE_VIEW} or {@link #_LIST_VIEW}
     */
    void setView(int index);
    
    /** 
     * Retrieves the view
     * @param index the view index; {@link #_TABLE_VIEW} or {@link #_LIST_VIEW} 
     * @return  the view instance
     */
    IView get(int index);
    
    /**
     * Retrieves the module to which the master view belongs.
     * @return  the module
     */
    DcModule getModule();
    
    /**
     * Retrieves the currently selected view.
     * @return  the selected view
     */
    IView getCurrent();
    
    /**
     * Apply settings to the views this master view manages
     */
    void applySettings();
    
    /**
     * Adds a view to this master view. This method is mainly used by the {@link DcModule}/
     * @param index index of this view; {@link #_TABLE_VIEW} or {@link #_LIST_VIEW} 
     * @param view  the view to add.
     */
    void addView(int index, IView view);
    
    /**
     * Refreshes the quick view information.
     */
    void refreshQuickView();
    
    /**
     * Indicates whether the view currently selected has been loaded / holds items.
     * @return  loaded y/n
     */
    boolean isLoaded();

    /**
     * Save the settings for the views.
     */
    void saveSettings();
    
    /**
     * Sort the items in the view, based on the {@link DcRepository.ModuleSettings#stSearchOrder}
     */
    void sort();
    
    /**
     * Refresh the views.
     */
    void refresh();
    
    /**
     * Clear the views.
     */
    void clear();
    
    /**
     * Adds the items to the view(s). The items are added based on their key.
     * The views are responsible for loading the item information.
     * @param keys  Key map, containing the ID of the item (String) and the module index (Integer).
     */
    void add(final Map<String, Integer> keys);
    
    /**
     * Adds a single item to the view(s).
     * @param dco   the item to add.
     */
    void add(DcObject dco);
    
    /**
     * Updates the information in the view for the supplied item.
     * @param dco   the item to update in the view.
     */
    void update(DcObject dco);
    
    /**
     * Removes the item from the view.
     * @param key   the ID of the item.
     */
    void remove(String key);
    
    /**
     * Returns all the views managed by this master view.
     * @return  the managed view collection.
     */
    Collection<IView> getViews();
    
    /**
     * Returns the grouping pane. Returns NULL in case there is no grouping pane. 
     * @return  the grouping pane or NULL.
     */
    IGroupingPane getGroupingPane();
}
