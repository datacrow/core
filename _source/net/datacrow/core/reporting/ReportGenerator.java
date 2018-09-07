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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.datacrow.core.clients.IItemExporterClient;
import net.datacrow.core.migration.itemexport.ItemExporter;
import net.datacrow.core.migration.itemexport.ItemExporterSettings;
import net.datacrow.core.migration.itemexport.ItemExporters;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.resources.DcResources;
import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.query.JRXPathQueryExecuterFactory;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRXmlUtils;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

@SuppressWarnings("deprecation")
public class ReportGenerator {
    
    private static Logger logger = Logger.getLogger(ReportGenerator.class.getName());
    
    private File source;
    private File target;
    private Report report;
    
    private List<String> items;
    private IItemExporterClient client;
    private BufferedOutputStream bos;
    
    private GenerateReportTask rt;
    private ItemExporter exporter;
    
    private ReportType reportType;

    protected boolean canceled = false;

    public ReportGenerator(
            IItemExporterClient client, 
            List<String> items, 
            File target, 
            Report report,
            ReportType reportType) {
        
        this.reportType = reportType;
        this.client = client;
        this.items = items;
        
        this.target = target;
        this.report = report;
    }
    
    public void start() {
        String s = target.toString();
        s = s.substring(0, s.lastIndexOf(".")) + ".xml";
        this.source = new File(s);
        
        if (rt != null) rt.cancel();
        
        rt = new GenerateReportTask();
        rt.start();
    }
    
    public void cancel() {
        canceled = true;
        if (exporter != null) exporter.cancel();
        if (rt != null) rt.cancel();
    }    
    
    private void translate() {
    	// future implementation
    }

    @SuppressWarnings("incomplete-switch")
	private void createReport() throws Exception {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            Document document;

            translate();
           
            logger.debug("Reporting: start reading XML document");
            document = JRXmlUtils.parse(JRLoader.getLocationInputStream(source.toString()));
            logger.debug("Reporting: XML document has been read successfully");
            
            params.put(JRXPathQueryExecuterFactory.PARAMETER_XML_DATA_DOCUMENT, document);
            params.put(JRXPathQueryExecuterFactory.XML_DATE_PATTERN, "yyyy-MM-dd");
            params.put(JRXPathQueryExecuterFactory.XML_NUMBER_PATTERN, "#,##0.##");
            params.put(JRXPathQueryExecuterFactory.XML_LOCALE, Locale.ENGLISH);
            params.put(JRParameter.REPORT_LOCALE, Locale.US);

            logger.debug("Reporting: starting to fill the report (" + report.getFilename() + ")");
            JasperPrint print = JasperFillManager.fillReport(report.getFilename(), params);
            logger.debug("Reporting: report has been filled successfully");

            logger.debug("Reporting: starting the export to type " + reportType);
            
            
            switch (reportType) {
            case PDF:
            	JasperExportManager.exportReportToPdfFile(print, target.toString());
            	break;
            case HTML:
            	JasperExportManager.exportReportToHtmlFile(print, target.toString());
            	break;
            default:
            	JRAbstractExporter exporter = null;
            	switch (reportType) {
                case RTF:
                    exporter = new JRRtfExporter();
                    break;
                case DOCX:
                    exporter = new JRDocxExporter();
                    break;            	
                case XLSX:
                    exporter = new JRXlsxExporter();
                    break;            	
                case XLS:
                    exporter = new JRXlsExporter();
                    break;            	
                }
            	
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
                exporter.setParameter(JRExporterParameter.OUTPUT_FILE, target);
                exporter.exportReport();
        	}

            logger.debug("Reporting: export has finished. Exported file " + target + ". Exists: " + target.exists());
            
        } catch (Exception e) {
            client.notifyError(e);
        }
    }
    
    protected void setSettings(ItemExporterSettings properties) {
        properties.set(ItemExporterSettings._ALLOWRELATIVEIMAGEPATHS, Boolean.TRUE);
    }
    
    private class GenerateReportTask extends Thread {
        
        private ItemExporter exporter;
        
        public void cancel() {
            if (exporter != null) exporter.cancel();
        }
        
        @Override
        public void run() {
            try {
                canceled = false;
                
                ItemExporterSettings settings = new ItemExporterSettings();
                settings.set(ItemExporterSettings._MAX_TEXT_LENGTH, new Integer(0));
                
                settings.set(ItemExporterSettings._COPY_IMAGES, Boolean.TRUE);
                settings.set(ItemExporterSettings._SCALE_IMAGES, Boolean.TRUE);
                settings.set(ItemExporterSettings._IMAGE_HEIGHT, new Integer(200));
                settings.set(ItemExporterSettings._IMAGE_WIDTH, new Integer(200));
                
                logger.debug("Reporting: starting the XML export to file: " + source);
                
                // export the items to an XML file
                exporter = ItemExporters.getInstance().getExporter("XML", DcModules.getCurrent().getIndex(), ItemExporter._MODE_NON_THREADED);
                exporter.setSettings(settings);
                exporter.setFile(source);
                exporter.setClient(client);
                exporter.setItems(items);
                exporter.start();

                logger.debug("Reporting: XML export completed to file " + source + ". File exists : " + source.exists());
                
                // create the report
                if (exporter.isSuccessfull() && !canceled) {
                	client.notifyTaskStarted(0);
                    client.notify(DcResources.getText("msgTransformingOutput", reportType.getName()));
                    
                    logger.debug("Reporting: starting the report creation process; createReport()");
                    createReport();
                    logger.debug("Reporting: report process has been completed");
                    
                    client.notify(DcResources.getText("msgTransformationSuccessful", target.toString()));
                } else {
                	logger.debug("Reporting: process has been cancelled or extract was unsuccessful");
                }

            } catch (Exception exp) {
                logger.error(DcResources.getText("msgErrorWhileCreatingReport", exp.toString()), exp);
                client.notify(DcResources.getText("msgErrorWhileCreatingReport", exp.toString()));
            } finally {
                if (client != null) client.notifyTaskCompleted(true, null);
                
                try {
                    source.delete();
                    String name = source.getName();
                    name = name.replace(".xml", ".xsd");
                    File xsd = new File(source.getParent(), name);
                    xsd.delete();
                    
                    File imgDir = new File(target.getParent(), name.replace(".xsd", "") + "_images/");
                    File imgFile;
                    for (String imgName : imgDir.list()) {
                        imgFile = new File(imgDir, imgName);
                        imgFile.delete();
                    }
                    
                    imgDir.delete();
                    
                } catch (Exception ignore) {
                    logger.debug("Could not cleanup reporting files.", ignore);
                }
                
                source = null;
                report = null;
                items = null;
                target = null;
                client = null;
                exporter = null;
                
                try {
                    if (bos != null) bos.close();
                } catch (IOException e) {
                    logger.debug("Error while closing resource", e);
                }
                
                bos = null;                   
            }
        }
    }
}
