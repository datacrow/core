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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import net.datacrow.core.DcRepository;
import net.datacrow.core.utilities.Base64;
import net.datacrow.settings.DcSettings;

/**
 * This class wraps a {@link HttpURLConnection} and offers detailed methods for 
 * retrieving information from an URL. Proxies are supported.
 * 
 * @author Robert Jan van der Waals
 */
public class HttpConnection {
    
    private HttpURLConnection uc;

    /**
     * Create a new connection.
     * @param url
     * @throws HttpConnectionException
     */
    public HttpConnection(URL url) throws HttpConnectionException {
        uc = connect(url);
    }
    
    /**
     * Checks if the URL is valid.
     * @return
     */
    public boolean exists() {
        try {
            uc.getInputStream();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Retrieves the underlying text (as UTF8).
     * @throws IOException
     */
    public String getString() throws HttpConnectionException {
        return getString("UTF-8");
    }

    /**
     * Retrieves the underlying text using the specified encoding. 
     * @param charset
     * @return
     * @throws IOException
     */
    public String getString(String charset) throws HttpConnectionException {
        try {
            StringBuffer sb = new StringBuffer();
            BufferedInputStream in = new BufferedInputStream(uc.getInputStream());
            InputStreamReader reader = charset != null ? new InputStreamReader(in, charset) : new InputStreamReader(in);
            
            int c;
            while ((c = reader.read()) != -1)
                sb.append((char) c);
    
            reader.close();
            in.close();
            
            return sb.toString();
        } catch (IOException ie) {
            throw new HttpConnectionException(ie);
        }
    }    
    
    /**
     * Retrieves the underlying bytes and closes the connection.
     * @throws IOException
     */
    public final byte[] getBytes() throws HttpConnectionException {
        try {
            ByteArrayOutputStream bais = new ByteArrayOutputStream();
            InputStream is = uc.getInputStream();
            BufferedInputStream bis  = new BufferedInputStream(is);
            while (true) {
                int i = bis.read();
                if (i == -1) break;
                bais.write(i);
            }
          
            bis.close();
            is.close();
            
            byte[] b = bais.toByteArray();
            bais.close();
    
            return b;
        } catch (IOException ie) {
            throw new HttpConnectionException(ie);
        }
    }    
    
    /**
     * Retrieves the content length.
     * @return
     */
    public int getContentLength() {
    	return uc.getContentLength();
    }

    /**
     * Set the content length.
     * @param length
     */
    public void setContentLength(int length) {
        uc.setRequestProperty("Content-Length", String.valueOf(length));
    }

    /**
     * Create an output stream for uploading purposes.
     * @throws IOException
     */
    public InputStream getInputStream() throws IOException {
        uc.setDoOutput(true);
        return uc.getInputStream();
    }
    
    /**
     * Create an output stream for uploading purposes.
     * @throws IOException
     */
    public OutputStream getOutputStream() throws IOException {
        uc.setDoOutput(true);
        return uc.getOutputStream();
    }

    /**
     * Disconnect, closes the connection.
     */
    public void close() {
    	uc.disconnect();
        uc = null;
    }

    private HttpURLConnection connect(URL url) throws HttpConnectionException {
        String proxy = DcSettings.getString(DcRepository.Settings.stProxyServerName);

        String username = DcSettings.getString(DcRepository.Settings.stProxyUserName);
        String password = DcSettings.getString(DcRepository.Settings.stProxyPassword);
        int proxyPort = DcSettings.getInt(DcRepository.Settings.stProxyServerPort);

        boolean useProxy = proxy.length() > 0 && proxyPort > 0;
        if (useProxy) {
        	System.setProperty("proxySet", "true");
        	System.setProperty("http.proxyHost", proxy);
        	System.setProperty("http.proxyPort", "" + proxyPort);
        } else {
            System.setProperty("proxySet", "false");
            System.setProperty("http.proxyHost", "");
            System.setProperty("http.proxyPort", "" + -1);
        }

        try {
            HttpURLConnection uc = (HttpURLConnection) url.openConnection();
            if (useProxy && username.trim().length() > 0) {
        		String proxyUPB64 = Base64.encode(username + ":" + password);
        		uc.setRequestProperty("Proxy-Authorization", "Basic " + proxyUPB64);
            }
            
            uc.setRequestMethod("GET");
            uc.setDoOutput(true);
            //uc.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible;MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322; InfoPath.1; .NET CLR2.0.50727)");
            
            return uc;
        } catch (IOException ie) {
            throw new HttpConnectionException(ie);
        }
    }
}
