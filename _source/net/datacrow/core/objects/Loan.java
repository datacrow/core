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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.datacrow.core.DcConfig;
import net.datacrow.core.data.DataFilter;
import net.datacrow.core.data.DataFilterEntry;
import net.datacrow.core.data.Operator;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.helpers.ContactPerson;
import net.datacrow.core.utilities.CoreUtilities;

/**
 * Represents a loan.
 *  
 * @author Robert Jan van der Waals
 */
public class Loan extends DcObject {

    private static final long serialVersionUID = 2452738490117020045L;
    
    private transient static final Calendar calDaysLoaned = Calendar.getInstance();
    private transient static final Calendar calDaysTillOverdue = Calendar.getInstance();

    public static final int _A_STARTDATE = 1;
    public static final int _B_ENDDATE = 2;
    public static final int _C_CONTACTPERSONID = 3;
    public static final int _D_OBJECTID = 4;
    public static final int _E_DUEDATE = 5;

    public Loan(int module) {
        super(module);
    }
    
    /**
     * Creates a new instance.
     */
    public Loan() {
        super(DcModules._LOAN);
    }
    
    @Override
    public void initializeReferences() {}     

    /**
     * Indicates if the given item is available.
     * @param ID
     */
    public boolean isAvailable(String ID) {
        Loan loan;
        if (getID() != null || getValue(_A_STARTDATE) != null)
            loan = this;
        else 
            loan = DcConfig.getInstance().getConnector().getCurrentLoan(ID);
        
        boolean available = true;
        Date start = (Date) loan.getValue(Loan._A_STARTDATE);
        Date end = (Date) loan.getValue(Loan._B_ENDDATE);
        Date current = new Date();
        if (end != null)
            available = true;
        else if (start != null && current.compareTo(start) >= 0)
            available = false;
        
        return available;
    }
    
    /**
     * Retrieves information on the person who lend the item.
     * @return The person or null.
     */
    public DcObject getPerson() {
        String personID = (String) getValue(Loan._C_CONTACTPERSONID);
        
        if (CoreUtilities.isEmpty(personID))
        	return null;
        else 
        	return DcConfig.getInstance().getConnector().getItem(DcModules._CONTACTPERSON, personID, new int[] {DcObject._ID, ContactPerson._A_NAME});
    }
    
    /**
     * This is a slow way of getting the actual lend item.
     * 
     */
    public DcObject getItem() {
        String objectID = (String) getValue(Loan._D_OBJECTID);
        
        DataFilter df = new DataFilter(DcModules._ITEM);
        df.addEntry(new DataFilterEntry(
                DcModules._ITEM,
                net.datacrow.core.objects.helpers.Item._ID,
                Operator.EQUAL_TO,
                objectID));
        
        List<DcObject> items = DcConfig.getInstance().getConnector().getItems(df);
        return items.size() == 1 ? items.get(0) : null;
    }
    
    public Date getDueDate() {
        return (Date) getValue(Loan._E_DUEDATE);
    }
    
    /**
     * Indicates the days till the loan is overdue. A negative value indicates
     * the loan is overdue.
     */
    public synchronized Long getDaysTillDueDate() {
        
        if (isFilled(_B_ENDDATE)) {
            return Long.valueOf(0);
        }
        
        Long daysLoaned = getDaysLoaned();
        Date overDueDate = getDueDate();
        Long days = null;
        if (daysLoaned != null && overDueDate != null) {
            calDaysLoaned.setTime(new java.util.Date());
            calDaysLoaned.setTime(overDueDate);

            long msPerDay = 1000 * 60 * 60 * 24;
            
            calDaysTillOverdue.setTime(new java.util.Date());
            long date1Milliseconds = calDaysTillOverdue.getTime().getTime();
            
            calDaysTillOverdue.setTime(overDueDate);
            long date2Milliseconds = calDaysTillOverdue.getTime().getTime();
            days = Long.valueOf((date2Milliseconds - date1Milliseconds) / msPerDay);            
        } else {
            days = daysLoaned;
        }
        
        return days;
    }
    
    /**
     * Calculates the due date against the current date to determine if the loan 
     * is overdue.
     */
    public boolean isOverdue() {
        
        if (isFilled(_B_ENDDATE)) return false;
        
        Long daysTillOverDue = getDaysTillDueDate();
        return daysTillOverDue != null && daysTillOverDue.longValue() < 0;
    }
    
    /**
     * The duration of the loan.
     */
    public synchronized Long getDaysLoaned() {
        Date startDate = (Date) getValue(Loan._A_STARTDATE);
        Long days = null;
        if (startDate != null && !isAvailable((String) getValue(Loan._D_OBJECTID))) {
            calDaysLoaned.setTime(new java.util.Date());
            calDaysLoaned.setTime(startDate);

            long msPerDay = 1000 * 60 * 60 * 24;
            
            calDaysLoaned.setTime(new java.util.Date());
            long date1Milliseconds = calDaysLoaned.getTimeInMillis();
            
            calDaysLoaned.setTime(startDate);
            long date2Milliseconds = calDaysLoaned.getTimeInMillis();
            days = Long.valueOf((date1Milliseconds - date2Milliseconds) / msPerDay);
        }

        return days;
    }
}
