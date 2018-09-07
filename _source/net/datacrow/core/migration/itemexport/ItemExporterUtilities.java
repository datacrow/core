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

package net.datacrow.core.migration.itemexport;

import java.io.File;

import net.datacrow.core.DcConfig;
import net.datacrow.core.objects.DcImageIcon;
import net.datacrow.core.objects.Picture;
import net.datacrow.core.utilities.CoreUtilities;

import org.apache.log4j.Logger;

public class ItemExporterUtilities {

    private static Logger logger = Logger.getLogger(ItemExporterUtilities.class.getName());
    
    private ItemExporterSettings settings;
    private String exportName;
    private String exportDir;
    
    public ItemExporterUtilities(String exportFilename, ItemExporterSettings settings) {
        File file = new File(exportFilename);
        
        this.settings = settings;
        this.exportName = file.getName();
        this.exportDir = file.getParent();
        this.exportName = exportName.lastIndexOf(".") > -1 ? exportName.substring(0, exportName.lastIndexOf(".")) : exportName;
        
        if (settings.getBoolean(ItemExporterSettings._COPY_IMAGES))
            new File(getImageDir()).mkdirs();
    }
    
    private String getImageDir() {
        return new File(exportDir, exportName +  "_images/").toString();
    }
    
    public String getImageURL(Picture p) {
        String url = "";
        String imageFilename = (String) p.getValue(Picture._C_FILENAME); 
        if (!CoreUtilities.isEmpty(imageFilename) && p.hasImage()) {
        	
            if (settings.getBoolean(ItemExporterSettings._COPY_IMAGES)) {
                copyImage(p,  new File(getImageDir(), imageFilename));
                
                if (settings.getBoolean(ItemExporterSettings._ALLOWRELATIVEIMAGEPATHS))
                    url = "./" + exportName + "_images/" + imageFilename;
                else 
                    url = "file:///" +  new File(getImageDir(), imageFilename);
            } else {
            	url = !CoreUtilities.isEmpty(p.getUrl()) ? p.getUrl() :  
            		"file:///" + new File(DcConfig.getInstance().getImageDir(), (String) p.getValue(Picture._C_FILENAME));
            }
        }
        return url;
    }
    
    private void copyImage(Picture picture, File target) {
        try {
            picture.loadImage(false);
            DcImageIcon icon = (DcImageIcon) picture.getValue(Picture._D_IMAGE);
            
            if (picture.hasImage()) {
                if (settings.getBoolean(ItemExporterSettings._SCALE_IMAGES)) {
                    int width = settings.getInt(ItemExporterSettings._IMAGE_WIDTH);
                    int height = settings.getInt(ItemExporterSettings._IMAGE_HEIGHT);
                    CoreUtilities.writeScaledImageToFile(icon, target, DcImageIcon._TYPE_PNG, width, height);
                } else {
                    CoreUtilities.writeToFile(icon, target);
                }
            }
        } catch (Exception e) {
            logger.error("An error occurred while copying image to " + target, e);
        }
    }    
}
