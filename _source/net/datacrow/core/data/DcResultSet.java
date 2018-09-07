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
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class DcResultSet implements Serializable {
	
    private static final long serialVersionUID = 4100611974488962280L;

    private transient static Logger logger = Logger.getLogger(DcResultSet.class.getName());
	
	private Map<String, String> columns;
	private Map<Integer, Object[]> data;
	
	public DcResultSet() {
		data = new LinkedHashMap<Integer, Object[]>();
		columns = new LinkedHashMap<String, String>();
	}
	
	public void fill(ResultSet rs) throws Exception {
		try {
			int columnCount = rs.getMetaData().getColumnCount();
			int rowNumber = 0;
			
			while (rs.next()) {
			    Object[] row = new Object[columnCount];
			    for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
			        row[columnIndex] = rs.getObject(columnIndex + 1);
			    }
			    data.put(Integer.valueOf(rowNumber++), row);
			}
	        
	        String columnName;
	        String columnType;
	        ResultSetMetaData md = rs.getMetaData();
	        for (int counter = 0; counter < columnCount; counter++) {
	        	columnName = md.getColumnName(counter + 1);
	        	columnType = md.getColumnTypeName(counter + 1);
	        	this.columns.put(columnName, columnType);
	        }
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				logger.debug("ResultSet could not be closed", e);
			}
		}
	}
	
	public String getColumnName(int col) {
		int idx = 0;
		String name = "";
		for (Object key : columns.keySet()) {
			if (col == idx) {
				name = (String) key; 
			}
			idx++;
		}
		return name;
	}
	
	public int getRowCount() {
		return data.size();
	}
	
	public int getColumnCount() {
		return columns.size();
	}
	
	public int getInt(int row, int col) {
		try {
			Number number = (Number) data.get(Integer.valueOf(row))[col];
			return number.intValue();
			
		} catch (Exception e) {
			logger.error("Could not get integer for row " + row + " column " + col, e);
		}
		return 0;
	}
	
	public Integer getInteger(int row, int col) {
		try {
			return Integer.valueOf(getInt(row, col));
		} catch (Exception e) {
			logger.error("Could not get Integer for row " + row + " column " + col, e);
		}
		return null;
	}
	
	public Long getLong(int row, int col) {
		try {
			return (Long) data.get(Integer.valueOf(row))[col];
		} catch (Exception e) {
			logger.error("Could not get Long for row " + row + " column " + col, e);
		}
		return null;
	}
	
	public String getString(int row, int col) {
		try {
			Object o = data.get(Integer.valueOf(row))[col];
			if (o == null)
				return null;
			else 
				return o.toString();
		} catch (Exception e) {
			logger.error("Could not get String for row " + row + " column " + col, e);
		}
		return null;
	}
	
	public Object getValue(int row, int col) {
       try {
            return data.get(Integer.valueOf(row))[col];
        } catch (Exception e) {
            logger.error("Could not get Object for row " + row + " column " + col, e);
        }
        return null;
	}
}
