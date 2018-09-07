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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import net.datacrow.core.DcRepository;
import net.datacrow.core.console.UIComponents;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcMapping;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.Picture;
import net.datacrow.core.utilities.CoreUtilities;

public class DataFilterConverter {
	
	private DataFilter df;
	
	public DataFilterConverter(DataFilter df) {
		this.df = df;
	}
	
    public String toSQL(int[] fields, boolean orderResults, boolean includeMod) {
        DcField field;
        
        DcModule m = DcModules.get(df.getModule());
        int[] queryFields = fields == null || fields.length == 0 ? m.getFieldIndices() : fields;
        
        Collection<DcModule> modules = new ArrayList<DcModule>();
        if (m.isAbstract())
        	modules.addAll(DcModules.getPersistentModules(m));
        else 
        	modules.add(m);
        
        StringBuffer sql = new StringBuffer();
        
        int columnCounter = 0;
        int moduleCounter = 0;
        if (m.isAbstract()) {
        	sql.append("SELECT ");
        	if (df.getResultLimit() > 0) {
    			sql.append(" TOP ");
    			sql.append(df.getResultLimit());
    			sql.append(" ");
    		}
        	sql.append("MODULEIDX");
        	
        	for (int idx : queryFields) {
				field = m.getField(idx);
				if (!field.isUiOnly()) {
					sql.append(", ");
					sql.append(field.getDatabaseFieldName());
					columnCounter++;
				}
			}
        	
        	sql.append(" FROM (");
        }
        
        for (DcModule module : modules) {
        	columnCounter = 0;
        	if (moduleCounter > 0)
				sql.append(" UNION ");
			
    		sql.append(" SELECT ");
    		
    		// for abstract module queries the TOP x has already been specified.
    		if (!m.isAbstract() && df.getResultLimit() > 0) {
    			sql.append(" TOP ");
    			sql.append(df.getResultLimit());
    			sql.append(" ");
    		}
        		
       		if (m.isAbstract() || includeMod) {
        		sql.append(module.getIndex());
        		sql.append(" AS MODULEIDX ");
        		columnCounter++;
        	}
			
			if (m.isAbstract()) {
				for (DcField abstractField : m.getFields()) {
					if (!abstractField.isUiOnly()) {
						if (columnCounter > 0) sql.append(", ");
						sql.append(abstractField.getDatabaseFieldName());
						columnCounter++;
					}
				}
			} else {
				for (int idx : queryFields) {
					field = m.getField(idx);
					if (field != null && !field.isUiOnly()) {
						if (columnCounter > 0) sql.append(", ");
						sql.append(field.getDatabaseFieldName());
						columnCounter++;
					}
				}
			}
			
			sql.append(" FROM ");
			sql.append(module.getTableName());

			if (orderResults)
			    addOrderByClause(sql);
			
	        addEntries(sql, module);

	        moduleCounter++;
        }
        
        if (m.isAbstract()) sql.append(") media ");
	        
        // add a join to the reference table part of the sort
        if (orderResults) addOrderBy(sql);
        return sql.toString();
    }
    
	private void addEntries(StringBuffer sql, DcModule module) {
    	boolean hasConditions = false;
        DcModule entryModule; 
        
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        List<DataFilterEntry> childEntries = new ArrayList<DataFilterEntry>();

		Object value;
        int operator;
        int counter2;
        int counter = 0;
        String queryValue = null;
        DcField field;
        DataFilterConverter dfc;
        
        DataFilter subFilter;
        
        DcModule m = DcModules.get(df.getModule());
        
        for (DataFilterEntry entry : df.getEntries()) {
        	
        	if (!m.isAbstract()) {
	        	entryModule = DcModules.get(entry.getModule());
	            if (entry.getModule() != df.getModule()) {
	                childEntries.add(entry);
	                continue;
	            }
        	} else {
        		entryModule = module;
        	}
            
            field = entryModule.getField(entry.getField());
            
            if (    field.isUiOnly() && 
                    field.getValueType() != DcRepository.ValueTypes._DCOBJECTCOLLECTION &&
                    field.getValueType() != DcRepository.ValueTypes._PICTURE) 
                continue;
            
            hasConditions = true;
            
            operator = entry.getOperator().getIndex();
            value = entry.getValue() != null ? CoreUtilities.getQueryValue(entry.getValue(), field) : null;
            
            if (value != null) {
                
                if (value instanceof Date) {
                    queryValue = "'" + formatter.format((Date) value) + "'";
                } else {
                    queryValue = String.valueOf(value);
                    if (    field.getValueType() == DcRepository.ValueTypes._STRING ||
                            field.getValueType() == DcRepository.ValueTypes._BOOLEAN ||
                            field.getValueType() == DcRepository.ValueTypes._DCOBJECTREFERENCE ||
                            field.getValueType() == DcRepository.ValueTypes._DCOBJECTCOLLECTION ||
                            field.getValueType() == DcRepository.ValueTypes._DCOBJECTREFERENCE ||
                            field.getValueType() == DcRepository.ValueTypes._DATE ||
                            field.getValueType() == DcRepository.ValueTypes._DATETIME) {
                        queryValue = queryValue.replaceAll("\'", "''");
                        queryValue = queryValue.replaceAll("\\%", "\\\\%");
                        queryValue = queryValue.replaceAll("\\_", "\\\\_");
                    }
                }
            }
            
            if (counter > 0) sql.append(entry.isAnd() ? " AND " : " OR ");
            
            if (counter == 0) sql.append(" WHERE ");
            
            boolean useUpper = field.getValueType() == DcRepository.ValueTypes._STRING &&
                field.getIndex() != DcObject._ID &&
                field.getValueType() != DcRepository.ValueTypes._DCOBJECTREFERENCE &&
                field.getValueType() != DcRepository.ValueTypes._DCPARENTREFERENCE &&
                field.getValueType() != DcRepository.ValueTypes._DCOBJECTCOLLECTION;
            
            if (field.getValueType() == DcRepository.ValueTypes._STRING) {
                if (useUpper) sql.append("UPPER(");
                sql.append(field.getDatabaseFieldName());
                if (useUpper) sql.append(")");
            } else if (field.getValueType() == DcRepository.ValueTypes._DCOBJECTCOLLECTION ||
                       field.getValueType() == DcRepository.ValueTypes._PICTURE) {
                sql.append("ID");
            } else {
                sql.append(field.getDatabaseFieldName());
            }
            
            if (field.getValueType() == DcRepository.ValueTypes._PICTURE) {
                
                if (operator == Operator.IS_EMPTY.getIndex()) 
                    sql.append(" NOT");
                
                DcModule picModule = DcModules.get(DcModules._PICTURE);
                sql.append(" IN (SELECT OBJECTID FROM " + picModule.getTableName() + 
                           " WHERE " + picModule.getField(Picture._B_FIELD).getDatabaseFieldName() + 
                           " = '" + field.getDatabaseFieldName() + "')");
            
            } else if ((operator == Operator.IS_EMPTY.getIndex() && field.getValueType() == DcRepository.ValueTypes._DCOBJECTCOLLECTION) ||
                       (operator == Operator.IS_FILLED.getIndex() && field.getValueType() == DcRepository.ValueTypes._DCOBJECTCOLLECTION)) {
                
                if (operator == Operator.IS_EMPTY.getIndex()) 
                    sql.append(" NOT");
                
                DcModule mapping = DcModules.get(DcModules.getMappingModIdx(entryModule.getIndex(), field.getReferenceIdx(), field.getIndex()));
                sql.append(" IN (SELECT " + mapping.getField(DcMapping._A_PARENT_ID).getDatabaseFieldName() + 
                                 " FROM " + mapping.getTableName() + 
                                 " WHERE " + mapping.getField(DcMapping._A_PARENT_ID).getDatabaseFieldName() + " = ID)");
            
            } else if (operator == Operator.CONTAINSVALUE.getIndex()) {
                
                if (    field.getValueType() == DcRepository.ValueTypes._DCOBJECTCOLLECTION ||
                        field.getValueType() == DcRepository.ValueTypes._DCOBJECTREFERENCE) {
                    
                    sql.append(" IN (");
                    
                    DcModule mapping = DcModules.get(DcModules.getMappingModIdx(entryModule.getIndex(), field.getReferenceIdx(), field.getIndex()));
                    
                    if (mapping != null) { // we're dealing with a reference name based lookup
                        sql.append("SELECT ");
                        sql.append(mapping.getField(DcMapping._A_PARENT_ID).getDatabaseFieldName());
                        sql.append(" FROM ");
                        sql.append(mapping.getTableName());
                        sql.append(" WHERE ");
                        sql.append(mapping.getField(DcMapping._B_REFERENCED_ID).getDatabaseFieldName());
                        sql.append(" IN (");
                    }
                    
                    DcModule referenceMod =  DcModules.get(field.getReferenceIdx());
                    sql.append("SELECT ");
                    sql.append(referenceMod.getField(DcObject._ID).getDatabaseFieldName());
                    sql.append(" FROM ");
                    sql.append(referenceMod.getTableName());
                    sql.append(" WHERE UPPER(");
                    sql.append(referenceMod.getField(referenceMod.getDisplayFieldIdx()).getDatabaseFieldName());
                    sql.append(") LIKE UPPER('%");
                    sql.append(queryValue);
                    sql.append("%'))");

                    if (mapping != null)
                        sql.append(")");
                } else {
                    sql.append(" LIKE ");
                    
                    if (useUpper) sql.append("UPPER(");
                    sql.append("'%" + queryValue + "%'");
                    if (useUpper) sql.append(")");
                }
                
            } else if ( operator == Operator.CONTAINS.getIndex() || 
                        operator == Operator.DOES_NOT_CONTAIN.getIndex() ||
                       (operator == Operator.EQUAL_TO.getIndex() && field.getValueType() == DcRepository.ValueTypes._DCOBJECTCOLLECTION) ||
                       (operator == Operator.NOT_EQUAL_TO.getIndex() && field.getValueType() == DcRepository.ValueTypes._DCOBJECTCOLLECTION)) {

                if (    field.getValueType() == DcRepository.ValueTypes._DCOBJECTCOLLECTION ||
                        field.getValueType() == DcRepository.ValueTypes._DCOBJECTREFERENCE) {
                    
                	if (operator == Operator.DOES_NOT_CONTAIN.getIndex() ||
                        operator == Operator.IS_EMPTY.getIndex() ||
                        operator == Operator.NOT_EQUAL_TO.getIndex()) 
                        sql.append(" NOT");

                    sql.append(" IN (");
                    
                    DcModule mapping = DcModules.get(DcModules.getMappingModIdx(entryModule.getIndex(), field.getReferenceIdx(), field.getIndex()));
                    
                    if (mapping != null) { // we're dealing with a reference name based lookup
                    	sql.append("SELECT ");
                        sql.append(mapping.getField(DcMapping._A_PARENT_ID).getDatabaseFieldName());
                        sql.append(" FROM ");
                        sql.append(mapping.getTableName());
                        sql.append(" WHERE ");
                        sql.append(mapping.getField(DcMapping._B_REFERENCED_ID).getDatabaseFieldName());
                        sql.append(" IN (");
                    }
                    
                    if (!(value instanceof Collection)) {
                        sql.append("'");
                        sql.append(queryValue);
                        sql.append("'");
                        sql.append(")");
                    } else {
                        counter2 = 0;
                        for (Object o : (Collection) value) {
                            
                            if (counter2 > 0)  sql.append(",");

                            sql.append("'");
                            if (o instanceof DcObject)
                                sql.append(((DcObject) o).getID());
                            else
                                sql.append(o.toString());
                            
                            sql.append("'");
                            
                            counter2++;
                        }
                        sql.append(")");
                    }
	                 
                    if (mapping != null)
                        sql.append(")");
                } else {
                    if (operator == Operator.DOES_NOT_CONTAIN.getIndex()) sql.append(" NOT");
                    sql.append(" LIKE ");
                    
                    if (useUpper) sql.append("UPPER(");
                    sql.append("'%" + queryValue + "%'");
                    if (useUpper) sql.append(")");
                }

            } else if (operator == Operator.ENDS_WITH.getIndex()) {
                sql.append(" LIKE ");
                if (useUpper) sql.append("UPPER(");
                sql.append("'%" + queryValue + "'");
                if (useUpper) sql.append(")");
            } else if (operator == Operator.EQUAL_TO.getIndex()) {
                if (useUpper) {
                    sql.append(" = UPPER('"+ queryValue +"')");
                } else {
                    sql.append(" = ");
                    if (value instanceof String) sql.append("'");
                    sql.append(queryValue);
                    if (value instanceof String) sql.append("'");
                }
            } else if (operator == Operator.BEFORE.getIndex() ||
                       operator == Operator.LESS_THEN.getIndex()) {
                sql.append(" < ");
                sql.append(queryValue);
            } else if (operator == Operator.AFTER.getIndex() ||
                       operator == Operator.GREATER_THEN.getIndex()) {
                
                
                sql.append(" > ");
                sql.append(queryValue);
            } else if (operator == Operator.IS_EMPTY.getIndex()) {
                sql.append(" IS NULL");
            } else if (operator == Operator.IS_FILLED.getIndex()) {
                sql.append(" IS NOT NULL");
            } else if (operator == Operator.NOT_EQUAL_TO.getIndex()) {
                sql.append(" <> ");
                if (useUpper) {
                    sql.append(" UPPER('"+ queryValue +"')");
                } else {
                    if (value instanceof String) sql.append("'");
                    sql.append(queryValue);
                    if (value instanceof String) sql.append("'");
                }
            } else if (operator == Operator.STARTS_WITH.getIndex()) {
                sql.append(" LIKE ");
                if (useUpper) sql.append("UPPER(");
                sql.append("'" + queryValue + "%'");
                if (useUpper) sql.append(")");
            } else if (operator == Operator.TODAY.getIndex()) {
                sql.append(" = TODAY");
            } else if (operator == Operator.DAYS_BEFORE.getIndex()) {
                cal.setTime(new Date());
                Long days = (Long) entry.getValue();
                cal.add(Calendar.DATE, -1 * days.intValue());
                sql.append(" = '" + formatter.format(cal.getTime()) + "'");
            } else if (operator == Operator.DAYS_AFTER.getIndex()) {
                Long days = (Long) entry.getValue();
                cal.add(Calendar.DATE, days.intValue());
                sql.append(" = '" + formatter.format(cal.getTime()) + "'");
            } else if (operator == Operator.MONTHS_AGO.getIndex()) {
                Long days = (Long) entry.getValue();
                cal.add(Calendar.MONTH, -1 * days.intValue());
                cal.set(Calendar.DAY_OF_MONTH, 1);
                sql.append(" BETWEEN '" + formatter.format(cal.getTime()) + "'");
                cal.set(Calendar.DAY_OF_MONTH, cal.getMaximum(Calendar.DAY_OF_MONTH));
                sql.append(" AND '" + formatter.format(cal.getTime()) + "'");
            } else if (operator == Operator.YEARS_AGO.getIndex()) {
                Long days = (Long) entry.getValue();
                cal.add(Calendar.YEAR, -1 * days.intValue());
                cal.set(Calendar.MONTH, 1);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                sql.append(" BETWEEN '" + formatter.format(cal.getTime()) + "'");
                cal.set(Calendar.MONTH, 12);
                cal.set(Calendar.DAY_OF_MONTH, 31);
                sql.append(" AND '" + formatter.format(cal.getTime()) + "'");
            }
            
            counter++;
        }
        
        if (childEntries.size() > 0) {
            DcModule childModule = DcModules.get(childEntries.get(0).getModule());
            
            subFilter = new DataFilter(childModule.getIndex());
            for (DataFilterEntry entry : childEntries)
            	subFilter.addEntry(entry);
            
            dfc = new DataFilterConverter(subFilter);
            String subSelect = dfc.toSQL(new int[] {childModule.getParentReferenceFieldIndex()}, false, false);
            
            if (hasConditions)
                sql.append(" AND ID IN (");
            else 
                sql.append(" WHERE ID IN (");
            
            sql.append(subSelect);
            sql.append(")");
        }
        
        addLoanConditions(this.df.getEntries(), module, sql, hasConditions);
    }
    
    private void addOrderByClause(StringBuffer sql) {
    	Collection<DcField> order = df.getOrder();
        for (DcField orderOn : order) {
            if (orderOn.getFieldType() == UIComponents._REFERENCEFIELD ||
                orderOn.getFieldType() == UIComponents._REFERENCESFIELD) {
                
                String column = orderOn.getFieldType() == UIComponents._REFERENCESFIELD ?
                        DcModules.get(orderOn.getModule()).getPersistentField(orderOn.getIndex()).getDatabaseFieldName() :
                        orderOn.getDatabaseFieldName();
                
            	String referenceTableName = DcModules.get(orderOn.getReferenceIdx()).getTableName();
                sql.append(" LEFT OUTER JOIN ");
                sql.append(referenceTableName);
                sql.append(" ON ");
                sql.append(referenceTableName);
                sql.append(".ID = ");
                sql.append(column);
            } 
        }
    }
    
    private void addOrderBy(StringBuffer sql) {
    	int counter = 0; 
        DcModule module = DcModules.get(df.getModule());
        DcModule referenceMod;
        
        
        Collection<DcField> order = df.getOrder();
        DcField field = module.getField(module.getDefaultSortFieldIdx());

        if (order.size() > 0) {
	        for(DcField orderOn : order) {
	            if (orderOn != null) {
	            	sql.append(counter == 0 ? " ORDER BY " : ", ");
	            	if (orderOn.getValueType() == DcRepository.ValueTypes._DCOBJECTREFERENCE ||
	            	    orderOn.getValueType() == DcRepository.ValueTypes._DCOBJECTCOLLECTION) {
	
	            	    referenceMod = DcModules.get(orderOn.getReferenceIdx());
	                    sql.append(referenceMod.getTableName());
	                    sql.append(".");
	                    sql.append(referenceMod.getField(referenceMod.getSystemDisplayFieldIdx()).getDatabaseFieldName());
	                    sql.append(df.getSortDirection() == DataFilter._SORTDIRECTION_ASCENDING ? "" : " DESC");
	            	} else if (!orderOn.isUiOnly() && orderOn.getDatabaseFieldName() != null) {
	                    sql.append(orderOn.getDatabaseFieldName());
	                    sql.append(df.getSortDirection() == DataFilter._SORTDIRECTION_ASCENDING ? "" : " DESC");
	            	}
	            	counter++;
	            }
	        }
        } else if (field != null && !field.isUiOnly()) {
            sql.append(" ORDER BY ");
            sql.append(module.getField(module.getDefaultSortFieldIdx()).getDatabaseFieldName());
        }
    }
    
    private void addLoanConditions(Collection<DataFilterEntry> entries, DcModule module, StringBuffer sql, boolean hasConditions) {
        
        Object person = null;
        Object duration = null;
        Object available = null;
        
        Object queryValue;
        
        for (DataFilterEntry entry : entries) {
            queryValue = CoreUtilities.getQueryValue(entry.getValue(), DcModules.get(entry.getModule()).getField(entry.getField()));
            if (entry.getField() == DcObject._SYS_AVAILABLE)
                available = queryValue;
            if (entry.getField() == DcObject._SYS_LENDBY)
                person = queryValue;
            if (entry.getField() == DcObject._SYS_LOANDURATION)
                duration = queryValue;
        }
        
        if (available == null && person == null && duration == null)
            return;
        
        sql.append(hasConditions ? " AND " : " WHERE ");
        
        String maintable = module.getTableName();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String current = formatter.format(new Date());
        String daysCondition = duration != null ? " AND DATEDIFF('dd', startDate , '" + current + "') >= " + duration : "";
        String personCondition = person != null ? " AND PersonID = '" + person + "'" : "";

        if (available != null && Boolean.valueOf(available.toString()))
            sql.append(" ID NOT IN (select objectID from Loans where objectID = " +  maintable
                       + ".ID AND enddate IS NULL AND startDate <= '" + current +  "')");
        else
            sql.append(" ID IN (select objectID from Loans where objectID = " +  maintable
                       + ".ID "  + daysCondition + " AND enddate IS NULL AND startDate <= '" + current +  "'" + personCondition + ")");
    }   
}
