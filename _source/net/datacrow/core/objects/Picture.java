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

package net.datacrow.core.objects;

import java.awt.Image;
import java.io.File;
import java.net.URL;

import net.datacrow.core.DcConfig;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.utilities.CoreUtilities;

import org.apache.log4j.Logger;

/**
 * A picture represents a physical picture file.
 * Every image stored in Data Crow (such as screenshots) is represented by 
 * a picture object.
 * 
 * @author Robert Jan van der Waals
 */
public class Picture extends DcObject {

    private static final long serialVersionUID = 2871103592900195025L;

    private transient static Logger logger = Logger.getLogger(Picture.class.getName());
    
    public static final int _A_OBJECTID = 1;
    public static final int _B_FIELD = 2;
    public static final int _C_FILENAME = 3;
    public static final int _D_IMAGE = 4;
    public static final int _E_HEIGHT = 5;
    public static final int _F_WIDTH = 6;
    public static final int _G_EXTERNAL_FILENAME = 7;
    
    protected boolean edited = false;
    protected boolean deleted = false;
    
    private String url;
    private String thumbnailUrl;
    
    /**
     * Creates a new instance
     */
    public Picture() {
        super(DcModules._PICTURE);
    }
    
    @Override
    public void initializeImages() {}

    public String getUrl() {
        return url;
    }
    
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
    
    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    @Override
    public boolean hasPrimaryKey() {
        return false;
    }

    @Override
    public void initializeReferences() {}    
    
    /**
     * Checks whether an image has been defined and, if so, if the image exists.
     */
    public boolean hasImage() {
        return  isFilled(_D_IMAGE) || 
                !CoreUtilities.isEmpty(getUrl()) ||
               (isFilled(_C_FILENAME) && new File(DcConfig.getInstance().getImageDir(), getDisplayString(_C_FILENAME)).exists());
    }

    public void loadImage(boolean external) {
        String filename = (String) getValue(_G_EXTERNAL_FILENAME);
        filename = !external || filename == null || !new File(filename).exists() ? (String) getValue(_C_FILENAME) : filename;

        DcImageIcon image = (DcImageIcon) getValue(Picture._D_IMAGE);
        
        boolean loaded = false;
        if (image != null) {
            image.flush();
            image = new DcImageIcon(image.getImage());
            loaded = true;
        } else if (!CoreUtilities.isEmpty(filename)) {
            filename = new File(filename).exists() ? filename : DcConfig.getInstance().getImageDir() + filename;

            if (new File(filename).exists()) {
                loaded = true;
                image = new DcImageIcon(filename);
            }
        }
        
        if (!loaded && !CoreUtilities.isEmpty(getUrl())) {
            try {
                URL url = new URL(getUrl());
                image = new DcImageIcon(url);
            } catch (Exception e) {
                logger.error("Error while loading image from URL: " + getUrl(), e);
            }
        }
        
        setValue(Picture._D_IMAGE, image);
        markAsUnchanged();
    }
    
    public Image getImage() {
        DcImageIcon image = (DcImageIcon) getValue(_D_IMAGE);
        return image != null ? image.getImage() : null;
    }
    
    @Override
    public void destroy() {
        unload();
        deleted = false;
        edited = false;
    }
    
    public String getImageFilename() {
        return (String) getValue(Picture._C_FILENAME);
    }
    
    public DcImageIcon getScaledPicture() {
        String filename = getScaledFilename();
        DcImageIcon thumbnail = null;
        if (filename != null) {
            if (DcConfig.getInstance().getOperatingMode() == DcConfig._OPERATING_MODE_CLIENT) {
                try {
                    thumbnail = new DcImageIcon(new URL(thumbnailUrl));
                } catch (Exception e) {
                    logger.warn("Could not load picture from URL " + thumbnailUrl, e);
                }
            } else {
                thumbnail = new DcImageIcon(new File(DcConfig.getInstance().getImageDir(), filename));
            }
        }
        
        return thumbnail;
    }
    
    public String getScaledFilename() {
        return getScaledFilename((String) getValue(Picture._C_FILENAME));
    }

    public String getScaledFilename(String filename) {
        if (filename != null) {
            try {
                int idx = filename.indexOf(".jpg");
                String plain = filename.substring(0, idx);
                String scaledFilename = plain + "_small.jpg";
                return scaledFilename;
            } catch (Exception e) {
                logger.debug("Unable to determine scaled image filename for " + filename + ". Is this a new item?", e);
            }
        }
        return null;
    }
    
    public void unload() {
        if (getValues() != null && (!isNew() && !edited)) {
	    	DcImageIcon image = ((DcImageIcon) getValue(_D_IMAGE));

	    	if (image != null) image.flush();
	    	
	        setValueLowLevel(_D_IMAGE, null);
	        setChanged(_D_IMAGE, false);
        }
    }
    
    @Override
    public void markAsUnchanged() {
        super.markAsUnchanged();

        edited = false;
        deleted = false;
    }    
    
    public void isEdited(boolean b) {
        edited = b;
        if (b) deleted = false;
    }
    
    public void isDeleted(boolean b) {
        deleted = b;
        if (b) edited = false;
    }
    
    @Override
    public boolean isLoaded() {
        return getValue(Picture._D_IMAGE) != null;
    }
    
    @Override
    public boolean isNew() {
        return super.isNew() && !isDeleted() && getValue(_D_IMAGE) != null; 
    }
    
    public boolean isEdited() {
        return edited;
    }
    
    public boolean isDeleted() {
        return deleted;
    }
    
    @Override
    public String toString() {
        return getValue(_C_FILENAME) != null ? (String) getValue(_C_FILENAME) : "";
    }
    
    @Override
    public int hashCode() {
        return (getValue(Picture._C_FILENAME) != null ? getValue(Picture._C_FILENAME).hashCode() : 0); 
    }
    
    @Override
    public boolean equals(Object o) {
        boolean equals = false;
        
        if (o instanceof Picture) {
            Picture picture = (Picture) o;
            
            String filename1 = (String) picture.getValue(Picture._C_FILENAME);
            String filename2 = (String) getValue(Picture._C_FILENAME);
            
            equals = filename1 == filename2 || (filename1 != null && filename1.equals(filename2));
        } else {
            equals = super.equals(o);
        }
        
        return equals;
   }

	@Override
	protected void finalize() throws Throwable {
		unload();
		super.finalize();
	}    
}