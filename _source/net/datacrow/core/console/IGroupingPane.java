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

/**
 * Interface defining a Grouping Pane.
 * 
 * @author Robert Jan van der Waals
 */
public interface IGroupingPane {
    
    /**
     * Indicates whether the grouping pane is enabled.
     * @return  enabled y/n.
     */
    boolean isEnabled();
    
    /**
     * Update the current view.
     * @see IView
     */
    void updateView();
    
    /**
     * Applies the grouping.
     */
    void groupBy();
    
    /**
     * Clears the grouping pane and the view of which this pane is a part.
     */
    void clear();
    
    /**
     * Sorts the items in the view of which this pane is a part.
     */
    void sort();
    
    /**
     * Update the tree with the item information.
     * @param dco   the item to reflect in the grouping pane / tree.
     */
    void updateTreeNodes(DcObject dco);
    
    /**
     * Loads the view of which this grouping pane is part.
     */
    void load();
    
    /** 
     * Apply the user / system settings. The settings are applied on every {@link ITreePanel}.
     */
    void applySettings();
    
    /**
     * Indicates whether changes should be saved when changing the view. 
     * As a new node is selected, the view is cleared. In case the view contains items
     * which have been changed (but not yet saved) these will be saved depending on this
     * setting.
     * @param b save changes y/n
     */
    void saveChanges(boolean b);
    
    /**
     * Indicates whether the grouping pane has been loaded
     * @return  loaded y/n
     */
    boolean isLoaded();
    
    /**
     * Removes the item with the specified key from the grouping pane and {@link IView}.
     * The item is removed from every {@link ITreePanel}.
     * @param key   the item ID.
     */
    void remove(String key);
    
    /**
     * Updates the Grouping Pane and its tree panels ({@link ITreePanel}) with the item information.
     * @param dco   the item to use for the update.
     */
    void update(DcObject dco);
    
    /**
     * Adds the item to the Grouping Pane and its tree panels ({@link ITreePanel}).
     * @param dco   the item to add.
     */
    void add(DcObject dco);
    
    /** 
     * Indicates whether the Grouping Pane is visible.
     * @return  visible y/n
     */
    boolean isVisible();
    
    /**
     * Makes the Grouping Pane visible / invisible
     * @param b visible y/n
     */
    void setVisible(boolean b);
    
    /**
     * Applies the font to the Grouping Pane and its tree panels ({@link ITreePanel}).
     * @param font  the font to use.
     */
    void setFont(Font font);
    
    /**
     * Returns the view of which the grouping pane is part.
     * @return  the master view
     */
    IMasterView getView();
    
    /**
     * Retrieves the currently selected {@link ITreePanel}
     * @return  the current tree panel
     */
    ITreePanel getCurrent();
}
