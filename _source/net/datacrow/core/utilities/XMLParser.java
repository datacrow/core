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

package net.datacrow.core.utilities;

import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import net.datacrow.core.modules.InvalidValueException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.helpers.DefaultHandler;

public abstract class XMLParser extends DefaultHandler {

    public static KeyStroke getKeyStroke(Element element, String tag) throws InvalidValueException {
        String s = getString(element, tag);
        try {
            return KeyStroke.getKeyStroke(s);
        } catch (Exception e) {
            throw new InvalidValueException(tag, s);
        }        
    }  
    
    public static ImageIcon getIcon(Element element, String tag) throws InvalidValueException {
        String s = getString(element, tag);
        try {
            return new ImageIcon(s);
        } catch (Exception e) {
            throw new InvalidValueException(tag, s);
        }        
    }     
    
    public static File getFile(Element element, String tag) throws InvalidValueException {
        String s = getString(element, tag);
        try {
            return new File(s);
        } catch (Exception e) {
            throw new InvalidValueException(tag, s);
        }        
    }    
    
    public static boolean getBoolean(Element element, String tag) throws InvalidValueException {
        String s = getString(element, tag);
        try {
            return Boolean.valueOf(s).booleanValue();
        } catch (NumberFormatException nfe) {
            throw new InvalidValueException(tag, s);
        }        
    }    

    public static int getInt(Element element, String tag) throws InvalidValueException {
        String s = getString(element, tag);
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            throw new InvalidValueException(tag, s);
        }
    }
    
    public static String getString(Element element, String tag) {
        String s = null;
        NodeList nodes = element.getElementsByTagName(tag);
        if (nodes != null && nodes.getLength() > 0) {
            Element el = (Element) nodes.item(0);
            s = el != null && el.getFirstChild() != null ? el.getFirstChild().getNodeValue() : null;
        }
        return s;
    }
}