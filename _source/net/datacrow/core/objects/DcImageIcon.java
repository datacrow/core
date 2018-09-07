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

import javax.swing.ImageIcon;

import net.datacrow.core.utilities.CoreUtilities;

import org.apache.log4j.Logger;

public class DcImageIcon extends ImageIcon {

	private static final long serialVersionUID = 5371441182570021920L;

    private transient static Logger logger = Logger.getLogger(DcImageIcon.class.getName());

    public static int _TYPE_JPEG = 0;
    public static int _TYPE_PNG = 1;
    
	private byte[] bytes;
	private String filename;
	private File file;
	
    public DcImageIcon() {
        super();
    }
    
    public DcImageIcon(File file) {
        this(file.toString());
    }
    
    public DcImageIcon(String filename) {
        super(filename);
        this.filename = filename;
        this.file = new File(filename);
    }  
    
    public DcImageIcon(byte[] bytes, boolean storeBytes) {
        super(bytes);
    }
    
    public DcImageIcon(byte[] bytes) {
        super(bytes);
    }

    public DcImageIcon(Image image) {
        super(image);
    }

    public DcImageIcon(URL location) {
        super(location);
    }
    
    public void save() {
        
        if (file == null && filename == null) {
            try {
                filename = getDescription();
                if (filename != null)
                    file = new File(filename);
            } catch (Exception e) {
                logger.debug("Failed to get filename as defined in the description", e);
            }
        }
        
        if (file == null) return;
        
        try {
            CoreUtilities.writeToFile(this, file);
        } catch (Exception e) {
            logger.error("Could not save icon to file " + filename, e);
        }
    }
    
    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
    
    public boolean exists() {
        return file == null ? false : file.exists();
    }
    
    public void flush() {
        bytes = null;
        filename = null;
        file = null;
    }
    
    public String getFilename() {
        return filename;
    }
    
    public File getFile() {
        return file;
    }
    
    public void setFilename(String filename) {
    	this.filename = filename;
    	
    	if (filename != null)
    	    this.file = new File(filename);
    	else
    	    this.file = null;
    }
    
    public byte[] getCurrentBytes() {
        return bytes;
    }
    
    public byte[] getBytes() {
    	if (filename != null && bytes == null) 
            logger.debug("Retrieving bytes from " + filename);
    		
    	try {
    		this.bytes = bytes != null ? bytes :  
    		             filename != null ? CoreUtilities.readFile(file) : CoreUtilities.getBytes(this);
    	} catch (Exception ie) {
    		logger.error("Could not retrieve bytes from " + filename, ie);
    	}
        
    	return bytes;
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            flush();
            
            Image img = getImage();
            if (img != null)
                img.flush();
        } catch (Exception e) {
            logger.error(e, e);
        }
        super.finalize();
    }
}
