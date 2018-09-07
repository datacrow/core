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

package net.datacrow.core.server.response;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.datacrow.core.enhancers.IValueEnhancer;
import net.datacrow.core.enhancers.ValueEnhancers;
import net.datacrow.core.objects.DcField;

public class ServerValueEnhancersRequestResponse implements IServerResponse {

    private static final long serialVersionUID = -1446345813358197043L;

    private Map<DcField, Collection<IValueEnhancer>> enhancers;

	public ServerValueEnhancersRequestResponse() {
	    enhancers = new HashMap<DcField, Collection<IValueEnhancer>>();
	    enhancers = ValueEnhancers.getEnhancers();
	}

    public Map<DcField, Collection<IValueEnhancer>> getEnhancers() {
        return enhancers;
    }
}
