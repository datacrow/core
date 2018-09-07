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

package net.datacrow.core.http;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Simplification for retrieving data from a specific address.
 */
public class HttpConnectionUtil {

    /**
     * Creates a new connection.
     * @param url
     * @return
     * @throws HttpConnectionException
     */
    public static HttpConnection getConnection(URL url) throws HttpConnectionException {
        return new HttpConnection(url);
    }
    
    /**
     * Retrieves the page content (UTF8).
     * @param url
     * @return
     * @throws HttpConnectionException
     */
    public static String retrievePage(String url) throws HttpConnectionException {
        return retrievePage(getURL(url), "UTF-8");
    }

    /**
     * Retrieves the page content using the supplied character set.
     * @param url
     * @param charset
     * @throws HttpConnectionException
     */
    public static String retrievePage(String url, String charset) throws HttpConnectionException {
        return retrievePage(getURL(url), charset);
    }

    /**
     * Retrieves the page content (UTF8).
     * @param url
     * @throws HttpConnectionException
     */
    public static String retrievePage(URL url) throws HttpConnectionException {
        return retrievePage(url, "UTF-8");
    }

    /**
     * Retrieves the page content using the supplied character set.
     * @param url
     * @param charset
     * @throws HttpConnectionException
     */
    public static String retrievePage(URL url, String charset) throws HttpConnectionException {
        HttpConnection connection = new HttpConnection(url);
        String page = connection.getString(charset);
        connection.close();
        return page;
    }

    /**
     * Retrieves the page content as a byte array.
     * @param url
     * @throws HttpConnectionException
     */
    public static byte[] retrieveBytes(String url) throws HttpConnectionException {
        return retrieveBytes(getURL(url));
    }

    /**
     * Retrieves the page content as a byte array.
     * @param url
     * @throws HttpConnectionException
     */
    public static byte[] retrieveBytes(URL url) throws HttpConnectionException {
        HttpConnection connection = new HttpConnection(url);
        byte[] bytes = connection.getBytes();
        connection.close();
        return bytes;
    }
    
    private static URL getURL(String url) throws HttpConnectionException {
        try {
            return new URL(url);
        } catch (MalformedURLException mue) {
            throw new HttpConnectionException(mue);
        }
    }
}
