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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import net.datacrow.core.IconLibrary;
import net.datacrow.core.objects.DcImageIcon;
import net.datacrow.core.resources.DcResources;

public class Rating {
    
    private static final Map<Long, DcImageIcon> ratings = new HashMap<Long, DcImageIcon>();

    private static final int scale = 10;
    private static final int starSize = 11;

    public static synchronized DcImageIcon getIcon(Long rating) {
        if (ratings.size() == 0) initialize();
        return rating != null ? ratings.get(rating) : null;
    }    
     
    public static synchronized String getLabel(int rating) {
        return rating == -1 ?
               DcResources.getText("lblRatingNotRated") : 
               rating + " " + DcResources.getText("lblRatingOutOf");
    }
    
    private static void initialize() {
        for (int rating = 0; rating <= scale; rating++) {
            BufferedImage bi = new BufferedImage(scale * starSize, 15, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = (Graphics2D) bi.getGraphics();
            
            for (int i = 0; i < rating; i++) {
                g2d.drawImage(IconLibrary._icoRatingOK.getImage(), i*6, 0, null);
            }
            
            for (int i = rating; i < scale; i++) {
                g2d.drawImage(IconLibrary._icoRatingNOK.getImage(), i*6, 0, null);
            }

            bi.flush();
            ratings.put(Long.valueOf(rating), new DcImageIcon(bi));
        } 
    }    
}
