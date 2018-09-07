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

package net.datacrow.core.objects.helpers;

import java.util.ArrayList;
import java.util.Collection;

import net.datacrow.core.DcConfig;
import net.datacrow.core.data.DataFilter;
import net.datacrow.core.data.DataFilterEntry;
import net.datacrow.core.data.Operator;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.ValidationException;
import net.datacrow.core.resources.DcResources;

public class Container extends DcObject {

    private static final long serialVersionUID = -3032927100353360734L;

    public static final int _A_NAME = 1;
    public static final int _B_TYPE = 2;
    public static final int _C_PICTUREFRONT = 3;
    public static final int _D_DESCRIPTION = 4;
    public static final int _E_ICON = 5;
    public static final int _F_PARENT = 6;

    public Container() {
        super(DcModules._CONTAINER);
    }
    
    public boolean isTop() {
        return isFilled(_F_PARENT);
    }
    
    public Container getParentContainer() {
        Object parent = getValue(_F_PARENT);
        return parent instanceof String ? 
        		(Container) DcConfig.getInstance().getConnector().getItem(DcModules._CONTAINER, (String) parent) : 
        	    (Container) parent;
    }
    
    public Collection<Container> getChildContainers() {
        Collection<Container> children = new ArrayList<Container>();
        DataFilter df = new DataFilter(DcModules._CONTAINER);
        df.addEntry(new DataFilterEntry(DataFilterEntry._AND, DcModules._CONTAINER, Container._F_PARENT, Operator.EQUAL_TO, getID()));
        
        for (DcObject dco : DcConfig.getInstance().getConnector().getItems(df)) {
            children.add((Container) dco);
        }
        
        return children;
    }
    
    @Override
    public void beforeSave() throws ValidationException {
        if (isChanged(_F_PARENT)) {
        
            Container parent = getParentContainer();
            String ID = getID();
            
            if (parent != null && parent.getID().equals(ID)) {
                throw new ValidationException(DcResources.getText("msgCannotSetItemAsParent"));
            } else  {
                while (parent != null) {
                    if (ID.equals(parent.getID()))
                        throw new ValidationException(DcResources.getText("msgCannotSetItemAsParentLoop"));        
    
                    parent = parent.getParentContainer();
                }
            }
        }
        super.beforeSave();
    }

    @Override
    public Object getValue(int index) {
        if (index == _F_PARENT) {
            Object o = super.getValue(_F_PARENT);
            return o instanceof String ? 
            		DcConfig.getInstance().getConnector().getItem(DcModules._CONTAINER, (String) o) : o;
        } else {
            return super.getValue(index);
        }
    }

    @Override
    public void loadChildren(int[] fields) {
        
        children.clear();
        
        if (   (getID() != null) &&
                getModule().getChild() != null) {

            DataFilter df = new DataFilter(DcModules._ITEM);
            df.addEntry(new DataFilterEntry(DataFilterEntry._AND, DcModules._ITEM, DcObject._SYS_CONTAINER, Operator.EQUAL_TO, this));
            children = DcConfig.getInstance().getConnector().getItems(df, new int[] {DcObject._ID});
            
            // We need to have the minimum set of information available for sorting
            for (DcObject dco : children)
                dco.load(dco.getModule().getMinimalFields(null));
            
            //Collections.sort(children, new DcObjectComparator(DcObject._SYS_DISPLAYVALUE));
        }
    } 
}
