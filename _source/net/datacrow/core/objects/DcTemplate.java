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

package net.datacrow.core.objects;

import java.util.List;

import net.datacrow.core.DcConfig;
import net.datacrow.core.data.DataFilter;
import net.datacrow.core.data.DataFilterEntry;
import net.datacrow.core.data.Operator;
import net.datacrow.core.objects.template.Templates;
import net.datacrow.core.server.Connector;

import org.apache.log4j.Logger;

/**
 * A template can be applied on new items.
 * 
 * @author Robert Jan van der Waals
 */
public class DcTemplate extends DcObject {
	
	private transient static Logger logger = Logger.getLogger(DcTemplate.class.getName());

    private static final long serialVersionUID = -6626046268821765360L;

    public static final int _SYS_TEMPLATENAME = 400;
    public static final int _SYS_DEFAULT = 401;
    
    private final int parent;
 
    /**
     * Creates a new instance.
     * @param module The index of its module.
     * @param parent The module by which the template will be used.
     */
    public DcTemplate(int module, int parent) {
        super(module);
        this.parent = parent;
    }

    /**
     * The name of the template {@link #_SYS_TEMPLATENAME} 
     */
    public String getTemplateName() {
        return (String) getValue(_SYS_TEMPLATENAME);
    }     

    /**
     * Indicates if this is the default template.
     * Only one template can be the default.
     */
     public boolean isDefault() {
         Object o = getValue(_SYS_DEFAULT);
         
         if (o == null)
             return false;
         
         if (o instanceof String)
             return Boolean.valueOf((String) o);
         
         return ((Boolean) getValue(_SYS_DEFAULT)).booleanValue();
     }

     @Override
     public void beforeSave() throws ValidationException {
         
    	 // do call super.beforeSave() prevent external reference errors and the likes
         
         if (isNew()) {
             setValue(_SYS_CREATED, getCurrentDate());
             setIDs();
         }

         setValue(_SYS_MODIFIED, getCurrentDate());
         
         saveIcon();
     }

    /**
      * The module which uses the template.
      */
     public int getParentModule() {
         return parent;
     }     
     
     @Override
     public String toString() {
         return getValue(DcTemplate._SYS_TEMPLATENAME) != null ? 
                getValue(DcTemplate._SYS_TEMPLATENAME).toString() : "";
     } 

     @Override
	public void afterSave() {
		super.afterSave();
		
		Templates.refresh();
		
		if (isDefault()) {
			Connector connector = DcConfig.getInstance().getConnector();
			
			DataFilter df = new DataFilter(module);
			df.addEntry(new DataFilterEntry(module, DcTemplate._SYS_DEFAULT, Operator.EQUAL_TO, Boolean.TRUE));
			df.addEntry(new DataFilterEntry(module, DcTemplate._SYS_TEMPLATENAME, Operator.NOT_EQUAL_TO, getValue(DcTemplate._SYS_TEMPLATENAME)));
			
			List<DcObject> templates = connector.getItems(df);
			for (DcObject template : templates) {
				try {
					// save directly, no error checking
					connector.saveItem(template);
				} catch (ValidationException e) {
					logger.error("Could not update template with ID " + getID(), e);
				}
			}
		}
	}

	@Override
     public void checkIntegrity() throws ValidationException {
         validateRequiredFields();
         isUnique();
     }  
}
