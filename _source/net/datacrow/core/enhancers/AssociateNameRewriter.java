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

package net.datacrow.core.enhancers;

import net.datacrow.core.objects.DcAssociate;
import net.datacrow.core.objects.DcField;

/**
 * Transforms a title. Based on a word list the title will be transformed as follows: <br>
 * &lt;Word in list&gt;,&lt;Value without Word in list&gt;
 * 
 * @author Robert Jan van der Waals
 */
public class AssociateNameRewriter implements IValueEnhancer {

    private static final long serialVersionUID = -6256725648992706967L;

    public static final int _FIRSTLAST = 0;
    public static final int _LASTFIRST = 1;
        
    private boolean enabled = false;
    private int order;
 
    /**
     * Creates a new instance.
     */
    public AssociateNameRewriter() {}    
    
    /**
     * Creates a new instances.
     * @param enabled
     * @param list The word list. Any value starting with a word in the list will be transformed.
     */
    public AssociateNameRewriter(boolean enabled, int order) {
        this.enabled = enabled;
        this.order = order;
    }
    
    /**
     * The field to transform.
     * @return Either {@link DcAssociate#_A_NAME}
     */
    public int getField() {
        return DcAssociate._A_NAME;
    }    
    
    public int getOrder() {
        return order;
    }
    
    @Override
    public String toSaveString() {
        return enabled + "/&/" + order;
    }

    @Override
    public int getIndex() {
        return ValueEnhancers._ASSOCIATENAMEREWRITERS;
    }
    
    @Override
    public void parse(String s) {
        enabled = Boolean.valueOf(s.substring(0, s.indexOf("/&/"))).booleanValue();
        order = Integer.valueOf(s.substring(s.indexOf("/&/") + 3, s.length()));
    }

    public String getName(String firstname, String lastname) {
        firstname = firstname == null ? "" : firstname.trim();
        lastname = lastname == null ? "" : lastname.trim();
        if (lastname.startsWith("(") && firstname.indexOf(" ") > -1) {
            String tmp = lastname;
            lastname = firstname.substring(firstname.indexOf(" ") + 1);
            firstname = firstname.substring(0, firstname.indexOf(" ")) + " " + tmp;
        }
        
        String result;
        if (order == _FIRSTLAST) {
            result = firstname + " " + lastname;
        } else {
            result = lastname.length() > 0 && firstname.length() > 0 ?
                     lastname + ", " + firstname :
                     lastname.length() > 0 ? lastname : firstname;
        }
        
        return result;
    }
    
    @Override
    public Object apply(DcField field, Object value) {
        String[] names = (String[]) value;

        String firstname = names[0];
        String lastname = names[1];
        
        return getName(firstname, lastname);
    }
    
    @Override
    public boolean isRunOnUpdating() {
        return true;
    }
    
    @Override
    public boolean isRunOnInsert() {
        return true;
    }
    

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
