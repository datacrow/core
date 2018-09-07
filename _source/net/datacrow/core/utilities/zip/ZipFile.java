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

package net.datacrow.core.utilities.zip;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

public class ZipFile {

	private static Logger logger = Logger.getLogger(ZipFile.class.getName());
	
	private ZipOutputStream zout;
	private FileOutputStream fos;
	private BufferedOutputStream bos;
	
	public ZipFile(String path, String filename) throws FileNotFoundException {
		this(new File(path + filename));
	}

	public ZipFile(File file) throws FileNotFoundException {
		fos = new FileOutputStream(file);
        bos = new BufferedOutputStream(fos);
        zout = new ZipOutputStream(bos);
	}
	
    public void addEntry(String name, byte[] bytes) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        
        ZipEntry ze = new ZipEntry(name);
        zout.putNextEntry(ze);
        
        byte b[] = new byte[512];
        int len = 0;
        while ((len = bais.read(b)) != -1) {
            zout.write(b, 0, len);
        }
        
        zout.closeEntry();
        bais.close();
    }	
    
    public void close() {
    	try {
    		if (zout != null) zout.close();
    		if (bos != null) bos.close();
    		if (fos != null) fos.close();
    	} catch (Exception e) {
    		logger.debug("Could not close zip file streams", e);
    	}
    }
}
