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

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import net.datacrow.core.utilities.Converter;

public abstract class XmlBaseWriter {

    protected final String uberTag = "data-crow-objects";
    protected final BufferedOutputStream bos;

    protected XmlBaseWriter(BufferedOutputStream bos) {
        this.bos = bos;
    }

    protected XmlBaseWriter(String filename) throws IOException {
        FileOutputStream fos = new FileOutputStream(filename);
        bos = new BufferedOutputStream(fos);
    }    
    
    protected String getValidTag(String s) {
        return Converter.getValidXmlTag(s);
    }
    
    protected void newLine() throws IOException {
        bos.write("\r\n".getBytes());
    }
    
    protected void writeLine(String s, int level) throws IOException {
        for (int i = 0; i < level; i++)
            bos.write("    ".getBytes());

        writeTag(s);
        newLine();
    }
    
    protected void writeTag(String s) throws IOException {
        bos.write(s.getBytes("UTF8"));
    }
}
