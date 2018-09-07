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

import net.datacrow.core.utilities.isbn.ISBN;

import org.apache.log4j.Logger;

/**
 * A search mode indicates a specific search such as a title, isbn, ean search.
 * 
 * @author Robert Jan van der Waals
 */
public abstract class IsbnSearchMode extends SearchMode {
    
    private static Logger logger = Logger.getLogger(IsbnSearchMode.class.getName());
    
    public IsbnSearchMode(int fieldBinding) {
        super(fieldBinding);
    }
    
    @Override
    public String getDisplayName() {
        return "ISBN";
    }

    public String getIsbn(String s) {
        String isbn = super.getSearchCommand(s);
        try {
            if (ISBN.isISBN10(isbn))
                isbn = ISBN.getISBN13(isbn);
        } catch (Exception e) {
            logger.debug("Invalid ISBN " + isbn, e);
        }
        return s;
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
    @Override
    public boolean singleIsPerfect() {
        return true;
    }
    
    /**
     * Indicates whether the search is a free form search (such as a title search).
     * ISBN, EAN and other specific search modes should set this method to return false. 
     */
    @Override
    public boolean keywordSearch() {
        return false;
    }
}
