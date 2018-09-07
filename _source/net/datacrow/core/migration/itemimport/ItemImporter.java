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

package net.datacrow.core.migration.itemimport;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import net.datacrow.core.DcConfig;
import net.datacrow.core.DcRepository;
import net.datacrow.core.clients.IItemImporterClient;
import net.datacrow.core.console.UIComponents;
import net.datacrow.core.migration.ItemMigrater;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcImageIcon;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.security.SecuredUser;
import net.datacrow.core.utilities.CoreUtilities;
import net.datacrow.core.utilities.StringUtils;

import org.apache.log4j.Logger;

/**
 * Source Readers are capable of reading source file and parsing the information
 * into Data Crow compatible items. Item relationships are imported in a loosely coupled way.
 * 
 * @see DataManager#createReference(DcObject, int, Object)
 * 
 * @author Robert Jan van der Waals
 */
public abstract class ItemImporter extends ItemMigrater {
    
    private static Logger logger = Logger.getLogger(ItemImporter.class.getName());
    
    protected IItemImporterClient client;
    protected ItemImporterFieldMappings mappings = new ItemImporterFieldMappings();

    // Local settings and properties > overrule the general settings
    private Map<String, String> settings = new HashMap<String, String>(); 

    public ItemImporter(SecuredUser su,
    		 		    int moduleIdx, 
    		 		    String key, 
    		 		    int mode) throws Exception {
    	
        super(su, moduleIdx, key, mode, true);
    }
    
    public boolean requiresMapping() {
        return false;
    }
    
    public void setSetting(String key, String value) {
        settings.put(key, value);
    }
    
    public String getSetting(String key) {
        return settings.get(key);
    }  
    
    /**
     * The official settings which can be used in combination with the 
     * specific source reader implementation.
     */
    public Collection<String> getSettingKeys() {
        return new ArrayList<String>();
    }
    
    /**
     * Adds a field mapping.
     */
    public void clearMappings() {
        mappings.clear();
    }
    
    /**
     * Adds a field mapping.
     */
    public void addMapping(String source, DcField target) {
        mappings.setMapping(source, target);
    }
    
    /**
     * Retrieves all field mappings.
     * @return
     */
    public ItemImporterFieldMappings getSourceMappings() {
        return mappings;
    }
    
    public abstract String[] getSupportedFileTypes();
    
    public Collection<String> getSourceFields() {
        return mappings.getSourceFields();
    }
    
    public DcField getTargetField(String source) {
        return mappings.getTarget(source);
    }
    
    public void setClient(IItemImporterClient client) {
        this.client = client;
    }
    
    private File getImagePath(String s) {
        
        String value = s;
        value = value.startsWith("file://") ? value.substring("file://".length()) : value;
        value = value.replaceAll("\\\\" , "/"); 

        File file = new File(value); 
        file = file.exists() ? file : new File(DcConfig.getInstance().getDataDir(), value);
        
        if (!file.exists()) {
            
            // maybe the path is relative ?
            String importName = getFile().getName();
            importName = importName.lastIndexOf(".") > -1 ?  importName.substring(0, importName.lastIndexOf(".")) : importName; 
            file = new File(getFile().getParent(), importName + "_images/" + file.getName());    

            if (!file.exists())
                file = new File(DcConfig.getInstance().getDataDir() , value);
        }

        return file;
    }

    protected void setValue(DcObject dco, int fieldIdx, String value, IItemImporterClient listener) {
        
        if (CoreUtilities.isEmpty(value)) return;
        
        DcField field = dco.getModule().getField(fieldIdx);
        
        if (field.getIndex() == DcObject._SYS_EXTERNAL_REFERENCES)
            return;
        
        // replace HTML enters characters
        value = value.replaceAll("<br>", "\n");
        value = StringUtils.trim(value);
        
        try {
            if (field.getValueType() == DcRepository.ValueTypes._DCOBJECTREFERENCE) {
            	dco.createReference(field.getIndex(), value);
            
            } else if (field.getValueType() == DcRepository.ValueTypes._DCOBJECTCOLLECTION) {
                StringTokenizer st = new StringTokenizer(value, ",");
                String s;
                while (st.hasMoreElements()) {
                    s = (String) st.nextElement();
                    dco.createReference(field.getIndex(), StringUtils.trim(s));
                }
                
            } else if (field.getFieldType() == UIComponents._TIMEFIELD) { 
                try {
                    dco.setValue(field.getIndex(), Long.valueOf(value));
                } catch (NumberFormatException nfe) {
                    if (value.indexOf(":") > -1) {
                        int hours = Integer.parseInt(value.substring(0, value.indexOf(":")));
                        int minutes = Integer.parseInt(value.substring(value.indexOf(":") + 1, value.lastIndexOf(":")));
                        int seconds = Integer.parseInt(value.substring(value.lastIndexOf(":") + 1));
                        dco.setValue(field.getIndex(), Long.valueOf(seconds + (minutes *60) + (hours * 60 * 60)));
                    }
                }
             } else if (field.getFieldType() == UIComponents._RATINGCOMBOBOX) {
                 try {
                     long rating = Math.round(Double.parseDouble(value));
                     
                     if (rating > 0 && rating <= 10)
                         dco.setValue(field.getIndex(), Long.valueOf(rating));
                 } catch (NumberFormatException nfe) {
                     String sValue = ""; 
                     for (char c : value.toCharArray()) {
                         if (Character.isDigit(c) || c == '.' || c == ',')
                             sValue += c;
                         else 
                             break;
                     }
                     
                     try {
                     long rating = Math.round(Double.parseDouble(sValue));
                     if (rating > 0 && rating <= 10)
                         dco.setValue(field.getIndex(), Long.valueOf(rating));
                     } catch (NumberFormatException e) { 
                         logger.warn("Could not parse rating from value " + value, e);
                     }
                 }
                
             } else if (field.getFieldType() == UIComponents._RATINGCOMBOBOX ||
                        field.getFieldType() == UIComponents._FILESIZEFIELD) {
    
                 value = value.replaceAll("\\.", "");
                 
                 try {
                     dco.setValue(field.getIndex(), Long.valueOf(value));
                 } catch (NumberFormatException nfe) {
                     String sValue = ""; 
                     for (char c : value.toCharArray()) {
                         if (Character.isDigit(c))
                             sValue += c;
                         else 
                             break;
                     }
                     
                     if (!CoreUtilities.isEmpty(sValue))
                         dco.setValue(field.getIndex(), Long.valueOf(sValue));
                }
            } else if (field.getValueType() == DcRepository.ValueTypes._PICTURE) {
                File file = getImagePath(value);
                if (file.exists())
                    dco.setValue(field.getIndex(), new DcImageIcon(file));
            } else if (field.getValueType() == DcRepository.ValueTypes._ICON) {
                File file = getImagePath(value);
                if (file.exists()) {
                    String s = CoreUtilities.fileToBase64String(file);
                    s = CoreUtilities.isEmpty(s) ? CoreUtilities.fileToBase64String(new File(value)) : s;
                    dco.setValue(field.getIndex(), s);
                } else {
                    dco.setValue(field.getIndex(), value);
                }
            } else if (field.getValueType() == DcRepository.ValueTypes._BOOLEAN) {
                dco.setValue(field.getIndex(), Boolean.valueOf(value));
            } else {
                dco.setValue(field.getIndex(), value);
            }
        } catch (Exception e) {
            String message = DcResources.getText("msgErrorWhileSettingValue", new String[] {value, field.getLabel()});
            listener.notify(message);
            logger.error(message, e);
        }                             
    }
}
