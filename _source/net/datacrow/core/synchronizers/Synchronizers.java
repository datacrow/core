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

package net.datacrow.core.synchronizers;

import java.util.HashMap;
import java.util.Map;

public class Synchronizers {
	
	private static final Synchronizers instance;
	
	private Map<Integer, Synchronizer> synchronizers;
	
	static {
		instance = new Synchronizers();
	}
	
	public static Synchronizers getInstance() {
		return instance;
	}

	private Synchronizers() {
		synchronizers = new HashMap<Integer, Synchronizer>();
	}
	
	public void register(Synchronizer synchronizer, int moduleIdx) {
		synchronizers.put(Integer.valueOf(moduleIdx), synchronizer);
	}
	
	public boolean hasSynchronizer(int moduleIdx) {
		return synchronizers.containsKey(Integer.valueOf(moduleIdx));
	}
	
	public Synchronizer getSynchronizer(int moduleIdx) {
		return  hasSynchronizer(moduleIdx) ? 
				synchronizers.get(Integer.valueOf(moduleIdx)).getInstance() : null;
	}
}
