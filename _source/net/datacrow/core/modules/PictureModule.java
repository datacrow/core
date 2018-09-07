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

package net.datacrow.core.modules;

import net.datacrow.core.DcRepository;
import net.datacrow.core.console.UIComponents;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.Picture;

/**
 * Holder of picture items.
 * 
 * @see Picture 
 * @author Robert Jan van der Waals
 */
public class PictureModule extends DcModule {
    
    private static final long serialVersionUID = 4278200439507269874L;

    /**
     * Creates a new instance
     */
    public PictureModule() {
        super(DcModules._PICTURE,
              false,
              "",
              "",
              "",
              "",
              "picture",
              "pic");
    }
    
    /**
     * Creates a new picture instance.
     * @see Picture
     */
    @Override
    public DcObject createItem() {
        return new Picture();
    }
   
    @Override
    public int[] getSupportedViews() {
        return new int[] {};
    }
    
    @Override
    public boolean hasInsertView() {
        return false;
    }

    @Override
    public boolean hasSearchView() {
        return false;
    }    
    
    /**
     * Initializes the default fields.
     */
    @Override
    protected void initializeFields() {
        addField(new DcField(Picture._A_OBJECTID, getIndex(), "ObjectID", 
                false, true, false, false, 
                36, UIComponents._SHORTTEXTFIELD, getIndex(), DcRepository.ValueTypes._STRING, 
                "ObjectID"));
        addField(new DcField(Picture._B_FIELD, getIndex(), "Field", 
                false, true, false, false, 
                100, UIComponents._SHORTTEXTFIELD, getIndex(), DcRepository.ValueTypes._STRING, 
                "Field"));
        addField(new DcField(Picture._C_FILENAME, getIndex(), "Filename", 
                false, true, true, false,  
                500, UIComponents._FILELAUNCHFIELD, getIndex(), DcRepository.ValueTypes._STRING, 
                "Filename"));
        addField(new DcField(Picture._D_IMAGE, getIndex(), "Image", 
                true, true, true, false,  
                0, UIComponents._PICTUREFIELD, getIndex(), DcRepository.ValueTypes._IMAGEICON, 
                "Image"));        
        addField(new DcField(Picture._E_HEIGHT, getIndex(), "Height", 
                false, true, true, false,  
                0, UIComponents._NUMBERFIELD, getIndex(), DcRepository.ValueTypes._LONG, 
                "Height"));        
        addField(new DcField(Picture._F_WIDTH, getIndex(), "Width", 
                false, true, true, false,  
                0, UIComponents._NUMBERFIELD, getIndex(), DcRepository.ValueTypes._LONG, 
                "Width"));  
        addField(new DcField(Picture._G_EXTERNAL_FILENAME, getIndex(), "External Filename", 
                false, true, true, false,  
                500, UIComponents._FILELAUNCHFIELD, getIndex(), DcRepository.ValueTypes._STRING, 
                "External_Filename"));        
    }
    
    @Override
    public boolean equals(Object o) {
        return (o instanceof PictureModule ? ((PictureModule) o).getIndex() == getIndex() : false);
    }   
}
