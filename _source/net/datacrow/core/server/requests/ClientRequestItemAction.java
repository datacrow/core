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

import net.datacrow.core.objects.DcObject;
import net.datacrow.core.security.SecuredUser;

public class ClientRequestItemAction extends ClientRequest {
    
	private static final long serialVersionUID = 995483880408186456L;

	public static final int _ACTION_SAVE = 0;
    public static final int _ACTION_DELETE = 1;
    
    private int action = _ACTION_SAVE;
    private DcObject item;
    
    public ClientRequestItemAction(SecuredUser su, int action, DcObject item) {
        super(ClientRequest._REQUEST_ITEM_ACTION, su);
        
        this.item = item;
        this.action = action;
    }
    
    public int getAction() {
    	return action;
    }
    
    public DcObject getItem() {
    	return item;
    }
    
	@Override
	public void close() {
		item = null;
	}
}
