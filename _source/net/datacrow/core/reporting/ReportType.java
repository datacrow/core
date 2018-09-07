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

package net.datacrow.core.reporting;

import net.datacrow.core.resources.DcResources;


public enum ReportType {
	
	PDF("pdf", DcResources.getText("lblReportTypePDF")),
	HTML("html",DcResources.getText("lblReportTypeHTML")),
//	XHTML("xhtml",DcResources.getText("lblReportTypeXHTML")),
	RTF("rtf", DcResources.getText("lblReportTypeRTF")),
	DOCX("docx", DcResources.getText("lblReportTypeDOCX")),
	XLSX("xlsx", DcResources.getText("lblReportTypeXLSX")),
	XLS("xls", DcResources.getText("lblReportTypeXLS"));

	private String extension;
	private String name;
	
    ReportType(String extention, String name) {
    	this.extension = extention;
    	this.name = name;
    }

	public String getExtension() {
		return extension;
	}

	public String getName() {
		return name;
	}
    
	@Override
	public String toString() {
		return name;
	}
}
