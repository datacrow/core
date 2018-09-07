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

import net.datacrow.core.objects.helpers.User;
import net.datacrow.core.security.SecuredUser;

public class ClientRequestUser extends ClientRequest {

	private static final long serialVersionUID = 6756227495879754410L;

	public static final int _ACTIONTYPE_UPDATE = 0;
	public static final int _ACTIONTYPE_DROP = 1;
	public static final int _ACTIONTYPE_CHANGEPASSWORD = 2;
	public static final int _ACTIONTYPE_CREATE = 2;
	
	private int actionType;
	private User user;
	private String password;
	
	public ClientRequestUser(int actionType, SecuredUser su, User user, String password) {
		super(ClientRequest._USER_MGT, su);
		
		this.actionType = actionType;
		this.user = user;
		this.password = password;
	}
	
	public User getUser() {
		return user;
	}
	
	public int getActionType() {
		return actionType;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public void close() {
		user = null;
		password = null;
	}
}
