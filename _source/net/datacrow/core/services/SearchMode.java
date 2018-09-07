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

package net.datacrow.core.services;

import net.datacrow.core.utilities.StringUtils;

/**
 * A search mode indicates a specific search such as a title, isbn, ean search.
 * 
 * @author Robert Jan van der Waals
 */
public abstract class SearchMode {
    
    private final int fieldBinding;
    
    public SearchMode(int fieldBinding) {
        this.fieldBinding = fieldBinding;
    }
    
    public int getFieldBinding() {
        return fieldBinding;
    }
    
    public abstract String getDisplayName();

    /**
     * Builds the URL / search command.
     * @param query The search string or query which needs to be incorporated in the search command.
     * @return fully qualified URL.
     */
    public String getSearchCommand(String query) {
        return !keywordSearch() && singleIsPerfect() ? String.valueOf(StringUtils.getContainedNumber(query)) : query;
    }
    
    @Override
    public String toString() {
        return getDisplayName();
    }
    
    /**
     * Indicates if a match should be considered as perfect when only one result is
     * retrieved. This is useful for ISBN and EAN searches. This is used for the 
     * 'Automatically add or update the item when a perfect match has occurred' setting. 
     */
    public abstract boolean singleIsPerfect();
    
    /**
     * Indicates whether the search is a free form search (such as a title search).
     * ISBN, EAN and other specific search modes should set this method to return false. 
     */
    public abstract boolean keywordSearch();
}
