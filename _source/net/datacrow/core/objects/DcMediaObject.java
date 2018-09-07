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

import net.datacrow.core.modules.DcMediaModule;

/**
 * Media objects come with a set of predefined fields such as a title,
 * a description, a year and a rating. 
 * Items of this type are represented by their own modules and the abstract
 * media module. 
 * 
 * @see DcMediaModule
 * 
 * @author Robert Jan van der Waals
 */
public class DcMediaObject extends DcObject {
    
    private static final long serialVersionUID = -2320044892640837631L;
    
    public static final int _A_TITLE = 150;
    public static final int _B_DESCRIPTION = 151;
    public static final int _C_YEAR = 152;
    public static final int _D_LANGUAGE = 153;
    public static final int _E_RATING = 154;
    public static final int _F_COUNTRY = 155;
    
    public static final int _U1_USER_LONGTEXT = 101;
    public static final int _U2_USER_SHORTTEXT1 = 102;
    public static final int _U3_USER_SHORTTEXT2 = 103;
    public static final int _U4_USER_NUMERIC1 = 104;
    public static final int _U5_USER_NUMERIC2 = 105;    
    
    /**
     * Creates a new instance.
     * @param module
     */
    public DcMediaObject(int module) {
        super(module);
    }
    
    /**
     * The default field to sort on.
     * @return {@link #_A_TITLE}
     */
    @Override
    public int getDefaultSortFieldIdx() {
        return DcMediaObject._A_TITLE;
    } 
    
    @Override
    public int getSystemDisplayFieldIdx() {
        return DcMediaObject._A_TITLE;
    }    
    
    @Override
    public String toString() {
        String s = (String) getValue(DcMediaObject._A_TITLE);
        return s == null || s.trim().length() == 0 ? super.toString() : s;
    }    
}
