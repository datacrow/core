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

package net.datacrow.core.modules;

import java.util.ArrayList;
import java.util.Collection;

import net.datacrow.core.DcConfig;
import net.datacrow.core.DcRepository;
import net.datacrow.core.console.UIComponents;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.Loan;
import net.datacrow.core.plugin.InvalidPluginException;
import net.datacrow.core.plugin.Plugins;
import net.datacrow.settings.Settings;

import org.apache.log4j.Logger;

/**
 * Represents loan items.
 * 
 * @author Robert Jan van der Waals
 */
public class LoanModule extends DcModule {

    private transient static Logger logger = Logger.getLogger(LoanModule.class.getName());
    
    private static final long serialVersionUID = -1777037389578494831L;

    public LoanModule(int index, 
            boolean topModule, 
            String name,
            String description,
            String objectName,
            String objectNamePlural,
            String tableName, 
            String tableShortName) {
        
        super(index, topModule, name, description, objectName, objectNamePlural, tableName, tableShortName);
    }
    
    /**
     * Creates a new instance of this module.
     */
    public LoanModule() {
        super(DcModules._LOAN, 
              false, 
              "Loan", 
              "", 
              "Loan",
              "Loans",
              "loans", 
              "lo");
    }
    
    @Override
    public boolean hasInsertView() {
        return false;
    }

    @Override
    public boolean hasSearchView() {
        return false;
    }
    
    @Override
    public boolean isEditingAllowed() {
        try {
            return DcConfig.getInstance().getConnector().getUser().isAuthorized(Plugins.getInstance().get("Loan"));
        } catch (InvalidPluginException ipe) {
            logger.error(ipe, ipe);
            return false;
        }
    }

    /**
     * Indicates if this module is enabled.
     */
    @Override
    public boolean isEnabled() {
        return DcModules.get(DcModules._CONTACTPERSON) != null ? 
               DcModules.get(DcModules._CONTACTPERSON).isEnabled() : false;
    }

    /**
     * Retrieves the settings for this module.
     */
    @Override
    public Settings getSettings() {
        setSetting(DcRepository.ModuleSettings.stTableColumnOrder, new int[] {Loan._C_CONTACTPERSONID, Loan._A_STARTDATE, Loan._B_ENDDATE});
        return super.getSettings();
    }

    /**
     * Creates a new instance of a loan.
     * @see Loan
     */
    @Override
    public DcObject createItem() {
        return new Loan();
    }

    @Override
    public int[] getSupportedViews() {
        return new int[] {};
    }

    @Override
    public int[] getMinimalFields(Collection<Integer> include) {
        Collection<Integer> c = new ArrayList<Integer>();
        
        if (include != null)
            c.addAll(include);
        
        if (!c.contains(Integer.valueOf(Loan._A_STARTDATE))) 
            c.add(Integer.valueOf(Loan._A_STARTDATE));
        if (!c.contains(Integer.valueOf(Loan._B_ENDDATE))) 
            c.add(Integer.valueOf(Loan._B_ENDDATE));
        if (!c.contains(Integer.valueOf(Loan._C_CONTACTPERSONID))) 
            c.add(Integer.valueOf(Loan._C_CONTACTPERSONID));
        if (!c.contains(Integer.valueOf(Loan._D_OBJECTID))) 
            c.add(Integer.valueOf(Loan._D_OBJECTID));
        if (!c.contains(Integer.valueOf(Loan._E_DUEDATE))) 
            c.add(Integer.valueOf(Loan._E_DUEDATE));
        
        return super.getMinimalFields(c);
    }
    
    /**
     * Initializes the default fields.
     */
    @Override
    protected void initializeFields() {
        super.initializeFields();

        addField(new DcField(Loan._A_STARTDATE, getIndex(), "Start date",
                false, true, false, false, 
                255, UIComponents._SHORTTEXTFIELD, getIndex(), DcRepository.ValueTypes._DATE,
                "StartDate"));
        addField(new DcField(Loan._B_ENDDATE, getIndex(), "End date",
                false, true, false, false, 
                255, UIComponents._SHORTTEXTFIELD, getIndex(), DcRepository.ValueTypes._DATE,
                "EndDate"));
        addField(new DcField(Loan._C_CONTACTPERSONID, getIndex(), "Contact Person",
                false, true, false, false, 
                36, UIComponents._SHORTTEXTFIELD, getIndex(), DcRepository.ValueTypes._STRING,
                "PersonID"));
        addField(new DcField(Loan._D_OBJECTID, getIndex(), "Object",
                false, true, false, false, 
                36, UIComponents._SHORTTEXTFIELD, getIndex(), DcRepository.ValueTypes._STRING,
                "ObjectID"));
        addField(new DcField(Loan._E_DUEDATE, getIndex(), "Due Date",
                false, true, false, false, 
                50, UIComponents._DATEFIELD, getIndex(), DcRepository.ValueTypes._DATE,
                "DueDate"));        
    }
    
    @Override
    public boolean equals(Object o) {
        return (o instanceof LoanModule ? ((LoanModule) o).getIndex() == getIndex() : false);
    }      
}
