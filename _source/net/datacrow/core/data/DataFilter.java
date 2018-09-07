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

package net.datacrow.core.data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import net.datacrow.core.DcConfig;
import net.datacrow.core.DcRepository;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.utilities.StringUtils;

import org.apache.log4j.Logger;

/**
 * Used to filter for items. 
 * A filter is created out of filter entries (see {@link DataFilterEntry}).
 * Filters can be saved to a file for reuse. Filters are used on the web as well as in 
 * the normal GUI.
 *  
 * @author Robert Jan van der Waals
 */
public class DataFilter implements Serializable {

	private static final long serialVersionUID = -8815051491433808198L;

	private transient static Logger logger = Logger.getLogger(DataFilter.class.getName());
    
    private int module;
    private int resultLimit = 0;
    
    private String name;
    
    public static final int _SORTDIRECTION_ASCENDING = 0;
    public static final int _SORTDIRECTION_DESCENDING = 1;
    
    private int sortDirection = _SORTDIRECTION_ASCENDING;
    
    private List<DcField> order = new ArrayList<DcField>();
    private List<DataFilterEntry> entries = new ArrayList<DataFilterEntry>();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
   
    /**
     * Creates a filter based on the supplied item.
     * @param dco
     */
    public DataFilter(DcObject dco) {
        this.module = dco.getModule().getIndex();
        setEntries(dco);
    }

    /**
     * Creates a filter based on an xml definition.
     * @param xml
     * @throws Exception
     */
    public DataFilter(String xml) throws Exception {
        parse(xml);
    }
    
    /**
     * Creates an empty filter for a specific module.
     * @param module
     */
    public DataFilter(int module) {
        this.module = module;
    } 

    public int getResultLimit() {
		return resultLimit;
	}

	public void setResultLimit(int resultLimit) {
		this.resultLimit = resultLimit;
	}

	/**
     * Creates a filter using the supplied entries.
     * @param module
     * @param entries
     */
    public DataFilter(int module, List<DataFilterEntry> entries) {
        this(module);
        this.entries = entries;
    }    

    public int getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(int direction) {
		this.sortDirection = direction;
    }
    
    /**
     * Sets the order. Results retrieved will be sorted based on this order.
     * @param order Array of fields.
     */
    public void setOrder(DcField field) {
        this.order = new ArrayList<DcField>();
    	this.order.add(field);
    }
    
    /**
     * Sets the order. Results retrieved will be sorted based on this order.
     * @param order Array of fields.
     */
    public void setOrder(Collection<DcField> order) {
        this.order = new ArrayList<DcField>();
        
        // check for null values at this point
        for (DcField field : order)
        	this.order.add(field);
    }
    
    /**
     * Adds a single entry to this filter.
     * @param entry
     */
    public void addEntry(DataFilterEntry entry) {
        entries.add(entry);
    }
    
    /**
     * Sets the entries for this filter.
     * Existing entries will be overwritten.
     * @param entries
     */
    public void setEntries(List<DataFilterEntry> entries) {
        this.entries = entries;
    }

    /**
     * Returns all entries belonging to this filter.
     * @return
     */
    public Collection<DataFilterEntry> getEntries() {
        return entries;
    }
    
    /**
     * Sets the entries based on the supplied item.
     * Existing entries will be overridden.
     * @param dco
     */
    public void setEntries(DcObject dco) {
        entries.clear();
        for (DcField field : dco.getFields()) {
            if (field.isSearchable() && dco.isChanged(field.getIndex())) { 
                entries.add(new DataFilterEntry(DataFilterEntry._AND,
                                                field.getModule(), 
                                                field.getIndex(), 
                                                Operator.EQUAL_TO, 
                                                dco.getValue(field.getIndex())));
            }
        }
    }
    
    /**
     * Returns the order information.
     * @return
     */
    public List<DcField> getOrder() {
        return order;
    }
    
    /**
     * Returns the name of this filter.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of this filter.
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Returns the module for which this filter has been created.
     */
    public int getModule() {
        return module;
    }

    @Override
    public String toString() {
        return getName();
    }
    
    @Override
    public boolean equals(Object o) {
        String name1 = o instanceof DataFilter ? ((DataFilter) o).getName() : "";
        String name2 = getName();
        
        name1 = name1 == null ? "" : name1;
        name2 = name2 == null ? "" : name2;
        
        return name1.equals(name2);
    }
    
    /**
     * Parses the XML filter definition.
     * @param xml Filter definition
     * @throws Exception
     */
    private void parse(String xml) throws Exception {
        module = Integer.parseInt(StringUtils.getValueBetween("<MODULE>", "</MODULE>", xml));
        name = StringUtils.getValueBetween("<NAME>", "</NAME>", xml);
        
        if (xml.contains("<SORTORDER>"))
            sortDirection = Integer.parseInt(StringUtils.getValueBetween("<SORTORDER>", "</SORTORDER>", xml));
        
        String sEntries = StringUtils.getValueBetween("<ENTRIES>", "</ENTRIES>", xml);
        int idx = sEntries.indexOf("<ENTRY>");
        while (idx != -1) {
            String sEntry = StringUtils.getValueBetween("<ENTRY>", "</ENTRY>", sEntries);
            int op = Integer.valueOf(StringUtils.getValueBetween("<OPERATOR>", "</OPERATOR>", sEntry)).intValue();
            
            Operator operator = null;
            for (Operator o : Operator.values()) {
                if (o.getIndex() == op)
                    operator = o;
            }
            
            int iField = Integer.valueOf(StringUtils.getValueBetween("<FIELD>", "</FIELD>", sEntry)).intValue();
            int iModule = Integer.valueOf(StringUtils.getValueBetween("<MODULE>", "</MODULE>", sEntry)).intValue();
            String sValue = StringUtils.getValueBetween("<VALUE>", "</VALUE>", sEntry);
            String sAndOr = StringUtils.getValueBetween("<ANDOR>", "</ANDOR>", sEntry);
            
            Object value = null;
            if (sValue.length() > 0) {
                DcField field = DcModules.get(iModule).getField(iField);
                int valueType = field.getValueType();
                if (valueType == DcRepository.ValueTypes._BOOLEAN) {
                    value = Boolean.valueOf(sValue);
                } else if (
                        valueType == DcRepository.ValueTypes._DATE ||
                        valueType == DcRepository.ValueTypes._DATETIME) {
                    value = sdf.parse(sValue);
                } else if (valueType == DcRepository.ValueTypes._DCOBJECTCOLLECTION) {
                    
                    StringTokenizer st = new StringTokenizer(sValue, ",");
                    Collection<DcObject> values = new ArrayList<DcObject>();
                    while (st.hasMoreElements()) {
                        DataFilter df = new DataFilter(field.getReferenceIdx());
                        df.addEntry(new DataFilterEntry(DataFilterEntry._AND,
                                                        field.getReferenceIdx(), 
                                                        DcObject._ID, 
                                                        Operator.EQUAL_TO, 
                                                        st.nextElement()));
                        values.addAll(DcConfig.getInstance().getConnector().getItems(df));
                    }
                
                    value = values;

                } else if (valueType == DcRepository.ValueTypes._DCOBJECTREFERENCE) {
                    DataFilter df = new DataFilter(field.getReferenceIdx());
                    df.addEntry(new DataFilterEntry(DataFilterEntry._AND,
                                                    field.getReferenceIdx(), 
                                                    DcObject._ID, 
                                                    Operator.EQUAL_TO, 
                                                    sValue));
                    List<DcObject> items = DcConfig.getInstance().getConnector().getItems(df);
                    value = items != null && items.size() == 1 ? items.get(0) : sValue;
                } else if (valueType == DcRepository.ValueTypes._LONG) {
                    value = Long.valueOf(sValue);
                } else {
                    value = sValue;
                }
            }

            addEntry(new DataFilterEntry(sAndOr, iModule, iField, operator, value));
            
            sEntries = sEntries.substring(sEntries.indexOf("</ENTRY>") + 8, sEntries.length());
            idx = sEntries.indexOf("<ENTRY>");
        }
        
        Collection<Integer> fields = new ArrayList<Integer>();
        String sOrder = StringUtils.getValueBetween("<ORDER>", "</ORDER>", xml);
        idx = sOrder.indexOf("<FIELD>");
        DcModule m = DcModules.get(module);
        int fieldIdx;
        DcField field;
        while (idx != -1) {
            fieldIdx = Integer.parseInt(StringUtils.getValueBetween("<FIELD>", "</FIELD>", sOrder));
            fields.add(fieldIdx);
            sOrder = sOrder.substring(sOrder.indexOf("</FIELD>") + 8, sOrder.length());
            idx = sOrder.indexOf("<FIELD>");
            
            for (Integer i : fields) {
            	field = m.getField(i.intValue());
            	
            	// check if the field exists (still)
            	if (field != null && !order.contains(field)) 
            		order.add(field);
            }
        }
    }
    
    /**
     * Creates a xml definition for this filter.
     */
    public String toStorageString() {
        String storage = "<FILTER>\n";
        
        storage += "<NAME>" + getName() + "</NAME>\n";
        storage += "<MODULE>" + getModule() + "</MODULE>\n";
        storage += "<SORTORDER>" + getSortDirection() + "</SORTORDER>\n";
        
        storage += "<ENTRIES>\n";
        
        Object value;
        String ids;
        for (DataFilterEntry entry : entries) {
            
            if (    entry == null || 
                    entry.getOperator() == null || 
                    (entry.getOperator().needsValue() && entry.getValue() == null)) continue;

            storage += "<ENTRY>\n";
            
            storage += "<ANDOR>" + entry.getAndOr() + "</ANDOR>\n";
            storage += "<OPERATOR>" + entry.getOperator().getIndex() + "</OPERATOR>\n";
            storage += "<MODULE>" + entry.getModule() + "</MODULE>\n";
            storage += "<FIELD>" + entry.getField() + "</FIELD>\n";
            
            value = "";
            
            if (entry.getOperator().needsValue()) {
                if (entry.getValue() instanceof Collection) {
                    ids = "";
                    for (Object o : ((Collection) entry.getValue())) {
                        if (o != null && o instanceof DcObject) {
                            ids += (ids.length() > 0 ? "," : "") + ((DcObject) o).getID();
                        } else {
                            logger.debug("Expected an instance of DcObject for Collections for DataFilter. Unexpected value encountered " + o);
                        }
                    }
                    value = ids;
                } else if (entry.getValue() instanceof DcObject) {
                    value = ((DcObject) entry.getValue()).getID();
                } else if (entry.getValue() instanceof Date) {
                    value = sdf.format((Date) entry.getValue());
                } else {
                    value = entry.getValue().toString();
                }
            }
            
            storage += "<VALUE>" + value + "</VALUE>\n";
            storage += "</ENTRY>\n";
        }
        
        storage += "</ENTRIES>\n";

        storage += "<ORDER>\n";
        
        for (DcField field : order)
            storage += "<FIELD>" + field.getIndex() + "</FIELD>\n";
        
        storage += "</ORDER>\n";
        storage += "</FILTER>\n";
        
        return storage;
    }
    
    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : super.hashCode();
    }
    
    public boolean equals(DataFilter df) {
        return name != null && df.getName() != null ? name.equals(df.getName()) : df.getName() != null || name != null;
    }
}
