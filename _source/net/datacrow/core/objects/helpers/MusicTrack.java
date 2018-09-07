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

package net.datacrow.core.objects.helpers;

import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcMediaObject;
import net.datacrow.core.objects.ValidationException;

public class MusicTrack extends DcMediaObject {

	private static final long serialVersionUID = 2782531771816681715L;
	
    public static final int _F_TRACKNUMBER = 1;
    public static final int _G_ARTIST = 2;
    public static final int _H_GENRES = 3;
    public static final int _J_PLAYLENGTH = 5;
    public static final int _K_QUALITY = 6;
    public static final int _L_ENCODING = 7;
    public static final int _M_LYRIC = 8;
    public static final int _O_STATE = 10;
    public static final int _P_ALBUM = 11;

    public MusicTrack() {
        super(DcModules._MUSIC_TRACK);
    }

    @Override
    public String getFilename() {
        return (String) getValue(MusicTrack._SYS_FILENAME);
    }
    
    @Override
    public void beforeSave() throws ValidationException {
        super.beforeSave();
        
        String track = (String) getValue(MusicTrack._F_TRACKNUMBER);
        if (track != null && track.length() == 1 && Character.isDigit(track.charAt(0))) {
            track = "0" + track;
            setValue(MusicTrack._F_TRACKNUMBER, track);
        }
    }

    @Override
    public String toString() {
        return getValue(MusicTrack._A_TITLE) != null ? getValue(MusicTrack._A_TITLE).toString() : "";
    }
}
