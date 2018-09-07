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

import net.datacrow.core.security.SecuredUser;

public class ClientRequestItem extends ClientRequest {
    
	private static final long serialVersionUID = 6177574052675397075L;

	public static final int _SEARCHTYPE_BY_ID = 0;
    public static final int _SEARCHTYPE_BY_KEYWORD = 1;
    public static final int _SEARCHTYPE_BY_UNIQUE_FIELDS = 2;
    public static final int _SEARCHTYPE_BY_EXTERNAL_ID = 3;
    public static final int _SEARCHTYPE_BY_DISPLAY_VALUE = 4;
    
    private int[] fields;
    private int searchType = _SEARCHTYPE_BY_ID;
    private Object value;
    private int module;
    
    private String externalKeyType;
    
    public ClientRequestItem(SecuredUser su, int searchType, int module, Object value) {
        super(ClientRequest._REQUEST_ITEM, su);
        
        this.value = value;
        this.module = module;
        this.searchType = searchType;
    }
    
	@Override
	public void close() {
		value = null;
	}

	public String getExternalKeyType() {
		return externalKeyType;
	}

	public void setExternalKeyType(String externalKeyType) {
		this.externalKeyType = externalKeyType;
	}

	public int getSearchType() {
		return searchType;
	}

	public void setSearchType(int searchType) {
		this.searchType = searchType;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
    public void setFields(int[] fields) {
        this.fields = fields;
    }

	public int[] getFields() {
		return fields;
	}
	
	public int getModule() {
		return module;
	}
}
