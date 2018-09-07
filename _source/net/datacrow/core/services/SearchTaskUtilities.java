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

import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.helpers.Book;
import net.datacrow.core.utilities.StringUtils;
import net.datacrow.core.utilities.isbn.ISBN;
import net.datacrow.core.utilities.isbn.InvalidBarCodeException;

import org.apache.log4j.Logger;

public abstract class SearchTaskUtilities {

    private static Logger logger = Logger.getLogger(SearchTaskUtilities.class.getName());
    
    public static final void checkForIsbn(SearchTask task) {

        // clean search query (removed non digits)
        if (task.getMode() instanceof IsbnSearchMode) {
            String query = task.getQuery();
            query = query.replaceAll("x|X", "0");
            task.setQuery(String.valueOf(StringUtils.getContainedNumber(query)));
        }

        // already using a very specific search mode or the online service does not support 
        // search modes.
        if (task.getMode() == null || task.getMode().singleIsPerfect()) return;
        
        // check whether an ISBN is available.
        DcObject dco = task.getClient();
        
        if (dco == null) return;
        
        String isbn;
        if (    dco.getModule().getIndex() == DcModules._BOOK && 
                (dco.isFilled(Book._N_ISBN13) || dco.isFilled(Book._J_ISBN10))) {
            
            isbn = (String) dco.getValue(Book._N_ISBN13);
            isbn = isbn == null ? (String) dco.getValue(Book._J_ISBN10) : isbn;
            
        } else {
            
            isbn = String.valueOf(StringUtils.getContainedNumber(task.getQuery()));
        }
        
        boolean isbn10 = ISBN.isISBN10(isbn);
        boolean isbn13 = ISBN.isISBN13(isbn);

        // If so, set the appropriate search mode
        try {
            if (isbn10 || isbn13) {
                isbn = isbn10 ? ISBN.getISBN13(isbn) : isbn; 
                if (task.getServer().getSearchModes() != null) {
                    for (SearchMode m : task.getServer().getSearchModes()) {
                        if (m instanceof IsbnSearchMode) {
                            task.setMode(m);
                            task.setQuery(isbn);
                            break;
                        }
                    }
                }
            }
        } catch (InvalidBarCodeException e) {
            logger.error("Invalid barcode " + isbn + ". Online search will be using original information.", e);
        }        
    }
}
