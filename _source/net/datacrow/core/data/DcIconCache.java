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

package net.datacrow.core.data;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import net.datacrow.core.DcConfig;
import net.datacrow.core.objects.DcImageIcon;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.utilities.CoreUtilities;

/**
 * This class is used for client-server configurations to avoid excessive reloading of icons.
 */
public class DcIconCache {

	private static final DcIconCache is = new DcIconCache();
	
	private ConcurrentHashMap<String, DcImageIcon> icons = new ConcurrentHashMap<String, DcImageIcon>();
	
	private DcIconCache() {}
	
	public static DcIconCache getInstance() {
		return is;
	}
	
	public void initialize() {
        for (String file : new File(DcConfig.getInstance().getIconsDir()).list()) {
        	if (new File(DcConfig.getInstance().getIconsDir(), file).isFile())
        		icons.put(file.substring(0, file.length() - 4), new DcImageIcon(DcConfig.getInstance().getIconsDir() + file));
        }
	}
	
    public DcImageIcon addIcon(String ID, String base64) {
        DcImageIcon icon = null;
        if (icons.containsKey(ID)) {
            icon = icons.get(ID);
        } else { 
            if (base64 != null) {
                icon = CoreUtilities.base64ToImage(base64);
                String filename = DcConfig.getInstance().getIconsDir() + ID + ".png";
                icon.setFilename(filename);
                icon.save();
            }
        }
        
        if (icon != null && !icon.exists())
            icon.save();
        
        // re-load image if necessary
        if (icon != null) {
            icons.put(ID, icon);
            icon.setImage(icon.getImage());
        }
        
        return icon;
    }
    
    public DcImageIcon getIcon(DcObject dco) {
        
        DcImageIcon icon;
    	String ID = dco.getID();
        
        if (icons.containsKey(ID) && icons.get(ID) != null) {
            icon = icons.get(dco.getID());
        } else {
            icon = dco.createIcon();
            if (icon != null) {
                String filename = DcConfig.getInstance().getIconsDir() + ID + ".png";
                icon.setFilename(filename);
                icon.save();
                
                icons.put(dco.getID(), icon);
            }
        }
        
        if (icon != null && !icon.exists()) {
            // check if the file exists
            if (icon.getFile() == null && icon.getFilename() == null) {
                icon.setFilename(DcConfig.getInstance().getIconsDir() + dco.getID() + ".png");
                icons.put(dco.getID(), icon);
            }
            
            icon.save();
        }
        
        if (icon != null)
            icon.setImage(icon.getImage());
        
        return icon;
    }
    
    public void removeIcon(String ID) {
        updateIcon(ID);
    }

    public void updateIcon(String ID) {
        DcImageIcon icon = icons.remove(ID);
        if (icon != null && icon.getFilename() != null) { 
            new File(icon.getFilename()).delete();
            icon.flush();
        }
    }
    
    public void deleteIcons() {
        for (DcImageIcon icon : icons.values()) {
            if (icon != null && icon.getFilename() != null)
                new File(icon.getFilename()).delete();
        }
    }
}
