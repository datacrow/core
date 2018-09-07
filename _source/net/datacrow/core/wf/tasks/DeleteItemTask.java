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

package net.datacrow.core.wf.tasks;

import net.datacrow.core.DcConfig;
import net.datacrow.core.clients.IClient;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.ValidationException;
import net.datacrow.core.server.Connector;

public class DeleteItemTask extends DcTask {

    public DeleteItemTask() {
        super("Delete-Items-Task");
    }
    
    @Override
	public int getType() {
		return _TYPE_DELETE_TASK;
	}

	@Override
    public void run() {
        try {

        	startTask();
            
        	Connector connector = DcConfig.getInstance().getConnector();
        	
            for (DcObject dco : items) {
            	
            	if (isCanceled()) break;
            	
            	try {
            		connector.deleteItem(dco);
            		success = true;
            	} catch (ValidationException ve) {
            		success = false;
                	notifyClients(IClient._WARNING, ve.getMessage());
                }
                
                try {
                    Thread.sleep(10);
                } catch (Exception ignore) {}
            }
        } finally {
            endTask();
        }
    }
}
