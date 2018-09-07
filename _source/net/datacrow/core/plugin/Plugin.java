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

package net.datacrow.core.plugin;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import net.datacrow.core.UserMode;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.DcTemplate;

/**
 * The Plugin class should be extended by every plugin.
 * 
 * @author Robert Jan van der Waals
 */
public abstract class Plugin extends AbstractAction {

    private static final long serialVersionUID = 4600350535556305957L;
    
    public static final int _VIEWTYPE_SEARCH = 0;
    public static final int _VIEWTYPE_INSERT = 1;
    
    private final int viewIdx;
    private final int viewType;
    private final int moduleIdx;
    
    private DcObject dco;
    private DcTemplate template;

    private String label = null;
    
    /**
     * Creates a new instance
     * @param dco The item for which the plugin is being created (or null)
     * @param template The template to be used (or null)
     * @param viewIdx The view index from which this plugin is being called / created
     * @param moduleIdx The module index to which this plugin belongs
     */
    protected Plugin(DcObject dco, 
                     DcTemplate template, 
                     int viewIdx, 
                     int moduleIdx,
                     int viewType) {
        
        this.moduleIdx = moduleIdx;
        this.viewIdx = viewIdx;
        this.viewType = viewType;
        this.template = template;
        this.dco = dco;
    }
    
    /**
     * The required user experience level.
     * @see UserMode
     */
    public int getXpLevel() {
        return UserMode._XP_BEGINNER;
    }

    public int getViewType() {
        return viewType;
    }

    /**
     * Indicates if this plugin can only be used by an administrator.
     */
    public boolean isAdminOnly() {
        return false;
    }
    
    /**
     * Retrieves the view index from which this plugin was called / created.
     */
    public int getViewIdx() {
        return viewIdx;
    }

    /**
     * Retrieves the item for which this plugin was called / created.
     */
    public DcObject getItem() {
        return dco;
    }

    public DcTemplate getTemplate() {
        return template;
    }

    /**
     * The short name of this plugin
     */
    public String getLabelShort() {
        return getLabel();
    }
    
    /**
     * Retrieves the module index for which this plugin was called / created.
     */    
    public final int getModuleIdx() {
		return moduleIdx <= 0 ? DcModules.getCurrent().getIndex() : moduleIdx;
	}

    /**
     * Retrieves the module for which this plugin was called / created.
     */  
    public final DcModule getModule() {
        return moduleIdx != -1 ? DcModules.get(moduleIdx) : DcModules.getCurrent();
    }
    
    /**
     * The help text.
     */
    public String getHelpText() {
        return null;
    }

    /**
     * The key combination to active the plugin.
     */
    public KeyStroke getKeyStroke() {
        return null;
    }
    
    /**
     * Sets the label for this plugin.
     * @param label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * The display label for this plugin.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Free all resources.
     */
    public void clear() {
        if (dco != null) {
            // only clear when the action is item specific 
            dco = null;
            template = null;
        }
    }
    
    /**
     * The unique key by which this plugin is referenced.
     */
    public final String getKey() {
        return getClass().getSimpleName();
    }

    /**
     * Indicates if this plugin is part of the core Data Crow product.
     */
    public boolean isSystemPlugin() {
        return false;
    }

    /**
     * Indicates if the plugin should be shown on the toolbar.
     */
    public boolean isShowOnToolbar() {
        return true;
    }
    
    /**
     * Indicates if the plugin should be shown in the popup menu of the view.
     */
    public boolean isShowInPopupMenu() {
        return true;
    }

    /**
     * Indicates if the plugin should be shown in the menu bar.
     */
    public boolean isShowInMenu() {
        return true;
    }
    
    /**
     * Indicates if a user can get special permissions in order to use this plugin.
     */
    public boolean isAuthorizable() {
        return true;
    }
    
    /**
     * Indicates if the plugin is enabled.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
    
    public abstract ImageIcon getIcon();
    @Override
    public abstract void actionPerformed(ActionEvent ae);

    @Override
    public final boolean equals(Object o) {
        if (o instanceof Plugin) {
            Plugin p = (Plugin) o;
            return p.getItem() == null && 
                   getItem() == null &&
                   p.getModuleIdx() == getModuleIdx() &&
                   p.getTemplate() == getTemplate() &&
                   p.getViewIdx() == getViewIdx();
        }
        return false;
    }

    @Override
    public final int hashCode() {
        int hash = getItem() != null ? getItem().hashCode() : 0;
        hash += getTemplate() != null ? getTemplate().hashCode() : 0;
        hash += viewIdx;
        hash += getModule().getIndex();
        return hash;
    }
}