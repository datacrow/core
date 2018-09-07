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

package net.datacrow.core.utilities.ical;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.datacrow.core.DcConfig;
import net.datacrow.core.clients.IClient;
import net.datacrow.core.data.DataFilter;
import net.datacrow.core.data.DataFilterEntry;
import net.datacrow.core.data.Operator;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.ExportedLoan;
import net.datacrow.core.objects.Loan;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.server.Connector;
import net.datacrow.core.wf.tasks.DcTask;
import net.datacrow.core.wf.tasks.SaveItemTask;

import org.apache.log4j.Logger;

public class ICalendarExporter extends Thread implements IClient {
    
    private static Logger logger = Logger.getLogger(ICalendarExporter.class.getName());

    private IClient client;
    
    private Calendar cal = Calendar.getInstance();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
    private SimpleDateFormat sdf2 = new SimpleDateFormat("dd' 'MMM' 'yyyy");
    private Date dt = new Date();
    private boolean full = false;
    
    private DcTask task;
    
    private File file;
    
    public ICalendarExporter(IClient client, File file, boolean full) {
        this.client = client;
        this.full = full;
        this.file = file;
    }
    
    @Override
    public void run() {
        
        client.notifyTaskStarted(100);
        
        DataFilter df = new DataFilter(DcModules._LOAN);
        df.addEntry(new DataFilterEntry(
                DcModules._LOAN, 
                Loan._B_ENDDATE, 
                Operator.IS_EMPTY,
                null));
        df.addEntry(new DataFilterEntry(
                DcModules._LOAN, 
                Loan._E_DUEDATE, 
                Operator.IS_FILLED,
                null)); 
        
        List<DcObject> loans = DcConfig.getInstance().getConnector().getItems(
        		df, 
                new int[] {
                Loan._ID,
                Loan._A_STARTDATE, 
                Loan._B_ENDDATE,
                Loan._C_CONTACTPERSONID,
                Loan._D_OBJECTID,
                Loan._E_DUEDATE});
        
        df = new DataFilter(DcModules._LOANEXPORT);
        List<DcObject> exportedLoans = DcConfig.getInstance().getConnector().getItems(df);
        
        StringBuffer sb = new StringBuffer();
        
        sb.append("BEGIN:VCALENDAR" + "\n");
        sb.append("PRODID:-//datacrow-ical v1.0//Data Crow//EN" + "\n");
        sb.append("VERSION:1.0" + "\n");
        sb.append("CALSCALE:GREGORIAN" + "\n");
        sb.append("METHOD:PUBLISH" + "\n");

        for (DcObject loan : loans) {
            
            if (client.isCancelled()) break;
            
            // Check if already exported. If so we are skipping this
            if (!full && exportedLoans.contains(loan)) continue;
            
            client.notifyProcessed();
            
            addEvent(sb, loan, "CONFIRMED", 0);
        }
        
        // cancel previously exported loans where necessary (only for incremental exports)
        if (!full && !client.isCancelled()) {
            for (DcObject exportedLoan : exportedLoans) {
                
                if (client.isCancelled()) break;
                
                // The exported loan still exists - no need to remove it
                // If it doesn't it either means the item has been returned, or the item
                // has been remove (along with the loan objects).
                // In this case we can simply cancel the event.
                if (loans.contains(exportedLoan)) continue;
                
                client.notifyProcessed();
                
                addEvent(sb, exportedLoan, "CANCELLED", 1);
            }
            sb.append("END:VCALENDAR");
        }
        
        cal.clear();
        
        try {
            
            if (!client.isCancelled())writeToFile(sb, file);
            
            if (!full && !client.isCancelled()) storeExport(loans);
            
        } catch (Exception e) {
            logger.error(e, e);
        }
        
        client.notify(DcResources.getText("msgCalendarExportComplete"));
        client.notifyTaskCompleted(true, null);
    }
    
    private void writeToFile(StringBuffer sb, File file) throws Exception {
        
        if (file.exists()) file.delete();
        
        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        out.write(sb.toString());
        out.close();
    }
    
    private void storeExport(List<DcObject> loans) {
    	
        DcConfig.getInstance().getConnector().executeSQL("DELETE FROM " + DcModules.get(DcModules._LOANEXPORT).getTableName());

    	DcModule mod = DcModules.get(DcModules._LOANEXPORT);
        DcObject exportedLoan;
        Long zero = new Long(0);
        
        client.notify(DcResources.getText("msgStoringExportedLoansDB"));
        
        if (task != null)
        	task.cancel();
        
        task = new SaveItemTask();
        task.addClient(this);
        
        for (DcObject loan : loans) {
            exportedLoan = mod.getItem();
            
            exportedLoan.setValueLowLevel(ExportedLoan._ID, loan.getID());
            exportedLoan.setValueLowLevel(ExportedLoan._A_STARTDATE, loan.getValue(Loan._A_STARTDATE));
            exportedLoan.setValueLowLevel(ExportedLoan._B_ENDDATE, loan.getValue(Loan._B_ENDDATE));
            exportedLoan.setValueLowLevel(ExportedLoan._C_CONTACTPERSONID, loan.getValue(Loan._C_CONTACTPERSONID));
            exportedLoan.setValueLowLevel(ExportedLoan._D_OBJECTID, loan.getValue(Loan._D_OBJECTID));
            exportedLoan.setValueLowLevel(ExportedLoan._E_DUEDATE, loan.getValue(Loan._E_DUEDATE));
            
            // For future use - in case we want to modify items (instead of canceling)
            exportedLoan.setValueLowLevel(ExportedLoan._F_SEQUENCE, zero);
            
            task.addItem(exportedLoan);
        }
        
        Connector connector = DcConfig.getInstance().getConnector();
        connector.executeTask(task);
    }
    
    private void addEvent(StringBuffer sb, DcObject dco, String status, int sequence) {
        Loan loan = (Loan) dco;
        String person = loan.getPerson().toString();
        DcObject item = loan.getItem();
        
        if (item == null) return;
        
        item.load(item.getModule().getMinimalFields(null));
        
        String summary = DcResources.getText("msgCalendarSummary", new String[] {
                DcResources.getText(item.getModule().getItemResourceKey()),
                item.toString(),
                person});
        
        String description = DcResources.getText("msgCalendarDescription", new String[] {
                DcResources.getText(item.getModule().getItemResourceKey()),
                item.toString(),
                person,
                sdf2.format(loan.getValue(Loan._A_STARTDATE))});
        
        client.notify(DcResources.getText("msgCalendarAddedEvent", new String[] {status, item.toString()}));
        
        sb.append("BEGIN:VEVENT" + "\n");
        
        cal.setTime((Date) loan.getValue(ExportedLoan._E_DUEDATE));
        cal.set(Calendar.HOUR_OF_DAY, 9);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        sb.append("DTSTART:" + sdf.format(cal.getTime()) + "\n");
        
        cal.set(Calendar.HOUR_OF_DAY, 10);
        sb.append("DTEND:" + sdf.format(cal.getTime()) + "\n");
        
        sb.append("SEQUENCE:" + sequence + "\n");
        sb.append("DTSTAMP:" + sdf.format(dt) + "\n");
        sb.append("UID:" + loan.getID() + "\n");
        sb.append("CREATED:" + sdf.format(dt) + "\n");
        sb.append("DESCRIPTION:" + description + "\n");
        sb.append("LAST-MODIFIED:" + sdf.format(new Date()) + "\n");
        sb.append("STATUS:" + status + "\n");
        sb.append("SUMMARY:" + summary + "\n");
        sb.append("TRANSP:OPAQUE" + "\n");
        
        sb.append("END:VEVENT" + "\n");
    }

	@Override
	public void notify(String msg) {
		logger.info(msg);
		
	}

	@Override
	public void notifyError(Throwable t) {
		logger.error("An error occured while saving the exported loan into the database", t);
	}

	@Override
	public void notifyWarning(String msg) {
		logger.warn(msg);
	}

	@Override
	public void notifyTaskCompleted(boolean success, String taskID) {
		if (success)
		    client.notify(DcResources.getText("msgStoringExportedLoansDBCompleted"));
	}

	@Override
	public void notifyProcessed() {
	    client.notifyProcessed();
	}

    @Override
    public void notifyTaskStarted(int taskSize) {}

    @Override
    public boolean isCancelled() {
        return false;
    }
}
