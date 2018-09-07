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

package net.datacrow.core.server.requests;

import java.io.Serializable;

import net.datacrow.core.security.SecuredUser;

public abstract class ClientRequest implements Serializable {

	private static final long serialVersionUID = -6116073911491261119L;
	
	public static final int _REQUEST_LOGIN = 0;
	public static final int _REQUEST_ITEMS = 1;
	public static final int _REQUEST_ITEM = 2;
	public static final int _REQUEST_ITEM_KEYS = 3;
	public static final int _USER_MGT = 4;
	public static final int _REQUEST_EXECUTE_SQL = 5;
	public static final int _REQUEST_ITEM_ACTION = 6;
	public static final int _REQUEST_SIMPLE_VALUES = 7;
	public static final int _REQUEST_REFERENCING_ITEMS = 8;
	public static final int _REQUEST_MODULES = 9;
	public static final int _REQUEST_APPLICATION_SETTINGS = 10;
	public static final int _REQUEST_VALUE_ENHANCERS_SETTINGS = 11;

	private int type;
	
	private String clientKey;
	private String username;
	private String password;
	
	public ClientRequest(int type, SecuredUser su) {
		this.type = type;
		
		if (su != null) {
			this.clientKey = su.getUser().getID();
			this.username = su.getUsername();
			this.password = su.getPassword();
		}
	}
	
	public String getUsername() {
	    return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getClientKey() {
		return clientKey;
	}
	
	public int getType() {
		return type;
	}
	
	public abstract void close();
}
