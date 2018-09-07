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

package net.datacrow.core;

import net.datacrow.settings.DcSettings;

/**
 * The user mode determines which actions are available to the user.
 * A beginner might feel overwhelmed and therefore less features will be available to this user.
 *  
 * @author Robert Jan van der Waals
 */
public abstract class UserMode {
    
    /** Beginner mode */
    public static final int _XP_BEGINNER = 0;
    /** Expert mode */
    public static final int _XP_EXPERT = 1;
    
    private static int current = _XP_EXPERT;
    
    public static void setUserMode(int mode) {
        if (current != mode) {
            current = mode;
        }
        
        DcSettings.set(DcRepository.Settings.stXpMode, mode);
        DcConfig.getInstance().getConnector().applySettings();
    }

    /**
     * Returns the current selecte user mode / experience level
     * @return  the user mode; {@link #_XP_BEGINNER} or {@link #_XP_EXPERT}
     */
    public static int getUserMode() {
        return current;
    }
    
    /**
     * Checks if the supplied user level corresponds with the current user mode.
     * @param level     the level to check ({@link #_XP_BEGINNER} or {@link #_XP_EXPERT})
     * @return  level matches with the current experience level / user mode 
     */
    public static boolean isCorrectXpLevel(int level) {
        if (getUserMode() == _XP_EXPERT)
            return true;
        else if (getUserMode() == _XP_BEGINNER && level == _XP_EXPERT)
            return false;
        else 
            return true;    
    }
}
