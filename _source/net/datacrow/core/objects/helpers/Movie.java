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

public class Movie extends DcMediaObject {

    private static final long serialVersionUID = -776640935064109319L;
    
    public static final int _F_TITLE_LOCAL = 1;
    public static final int _G_WEBPAGE = 2;
    public static final int _H_GENRES = 3;
    public static final int _I_ACTORS = 4;
    public static final int _J_DIRECTOR = 5;
    public static final int _L_PLAYLENGTH = 7;
    public static final int _N_VIDEOCODEC = 9;
    public static final int _O_AUDIOCODEC = 10;
    public static final int _P_WIDTH = 11;
    public static final int _Q_HEIGHT = 12;
    public static final int _R_FPS = 13;
    public static final int _S_FRAMES = 14;
    public static final int _T_AUDIOBITRATE = 15;
    public static final int _U_AUDIOSAMPLINGRATE = 16;
    public static final int _V_AUDIOCHANNEL = 17;
    public static final int _W_VIDEOBITRATE = 18;
    public static final int _X_PICTUREFRONT = 19;
    public static final int _Y_PICTUREBACK = 20;
    public static final int _Z_PICTURECD = 21;
    public static final int _1_AUDIOLANGUAGE = 23;
    public static final int _2_SUBTITLELANGUAGE = 24;
    public static final int _3_CERTIFICATION = 25;
    public static final int _6_STATE = 28;
    public static final int _7_STORAGEMEDIUM = 29;
    public static final int _8_SERIES = 30;
    public static final int _9_SEASON = 31;
    public static final int _10_EPISODE_NR = 32;
    public static final int _11_EPISODE_TITLE = 33;
    public static final int _12_EAN = 34;
    public static final int _13_COLOR = 35;
    public static final int _14_ESPECT_RATIO = 36;

    public Movie() {
        super(DcModules._MOVIE);
    }
}
