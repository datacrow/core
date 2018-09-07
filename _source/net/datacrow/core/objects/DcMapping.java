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

import net.datacrow.core.DcConfig;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.utilities.CoreUtilities;

import org.apache.log4j.Logger;

/**
 * A mapping represents a many to many relationship.
 * Mappings are stored in cross reference database tables.
 * 
 * @author Robert Jan van der Waals
 */
public class DcMapping extends DcObject {
    
    private transient static Logger logger = Logger.getLogger(DcMapping.class.getName());
    
    private static final long serialVersionUID = 1314886460279316879L;
    
    public static final int _A_PARENT_ID = 1;
    public static final int _B_REFERENCED_ID = 2;
    
    private DcObject reference = null;
    
    /**
     * Creates a new instance.
     * @param module
     */
    public DcMapping(int module) {
        super(module);
    }
    
    /**
     * The filename to which this module is stored.
     * @return Always returns null.
     */
    @Override
    public String getFilename() {
        return null;
    }
    
    @Override
    public void initializeImages() {
        
    }
    
    @Override
    public boolean hasPrimaryKey() {
        return false;
    }

    @Override
    public void initializeReferences() {}

    @Override
	public void destroy() {
        reference = null;
        super.destroy();
	}

	public void setReference(DcObject dco) {
        this.reference = dco;
    }
    
    /**
     * Retrieves the referenced object.
     */
    public DcObject getReferencedObject() {
        if (reference == null) {
            try {
                reference = DcConfig.getInstance().getConnector().getItem(
                        getReferencedModuleIdx(), 
                        getReferencedID(), 
                        DcModules.get(getReferencedModuleIdx()).getMinimalFields(null));
            } catch (Exception e) {
                logger.warn(e, e);
            }
        } else {
            if (CoreUtilities.isEmpty(reference.toString()))
                reference.reload();
        }
        
        return reference;
    }
    
    /**
     * The parent module index.
     */
    public int getParentModuleIdx() {
        return getField(_A_PARENT_ID).getReferenceIdx();
    }

    /**
     * The referenced module index.
     */
    public int getReferencedModuleIdx() {
        return getField(_B_REFERENCED_ID).getSourceModuleIdx();
    }

    /**
     * The object ID of the parent.
     * 
     */
    @Override
    public String getParentID() {
        return getValueDef(_A_PARENT_ID).getValueAsString();
    }

    /**
     * The object ID of the referenced item.
     */
    public String getReferencedID() {
        try {
            return (String) getValueDef(_B_REFERENCED_ID).getValue();
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
	public DcImageIcon getIcon() {
		return getReferencedObject() == null ? super.getIcon() : getReferencedObject().getIcon();
	}

	@Override
    public String toString() {
        return getReferencedObject() == null ? "" : getReferencedObject().toString();
    }
    
    @Override
    public DcObject clone() {
        DcMapping clone = (DcMapping) super.clone();
        clone.setReference(reference != null ? reference.clone() : null);
        return clone;
    }

    @Override
    public void copy(DcObject dco, boolean overwrite, boolean allowDeletes) {
        super.copy(dco, overwrite, allowDeletes);
    }
}
