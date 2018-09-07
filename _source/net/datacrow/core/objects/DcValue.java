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

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import net.datacrow.core.DcConfig;
import net.datacrow.core.DcRepository;
import net.datacrow.core.console.UIComponents;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.utilities.Base64;
import net.datacrow.core.utilities.Converter;
import net.datacrow.core.utilities.Rating;
import net.datacrow.core.utilities.CoreUtilities;

import org.apache.log4j.Logger;

/**
 * The value class represents a field value.
 * It knows when it has been changed.
 * 
 * @author Robert Jan van der Waals
 */
public class DcValue implements Serializable {

    private static final long serialVersionUID = 3222707222963657152L;

    private transient static Logger logger = Logger.getLogger(DcValue.class.getName());
    
    private boolean changed = false;
    private Object value = null;

    public DcValue() {}
    
    /**
     * Indicates if the value has been changed.
     * @return
     */
    public boolean isChanged() {
        return changed;
    }

    /**
     * Marks the value as changed.
     * @param b
     */
    public void setChanged(boolean b) {
        changed = b;
        
        if (!b && value instanceof Picture && value != null)
            ((Picture) value).markAsUnchanged();
    }

    /**
     * Bypasses all checks and sets the value directly.
     * @param newValue The new value to be used.
     * @param field The field for which the value is set.
     */
    public void setValueLowLevel(Object newValue, DcField field) {
        if (!field.isUiOnly())
            setChanged(true);
        
        setValueNative(newValue, field);
    }
    
    /**
     * Sets the new value for this object.
     * @param o The new value.
     * @param field The field for which the value is set.
     */
    @SuppressWarnings("unchecked")
    public void setValue(Object o, DcField field) {
        
        if (!field.isUiOnly()) 
            setChanged(true);
        else if (field.getValueType() == DcRepository.ValueTypes._DCOBJECTCOLLECTION)
            setChanged(true);

        if (field.getValueType() == DcRepository.ValueTypes._PICTURE) {
            if (o instanceof Picture) {
                if (value != null) ((Picture) value).destroy();

                setValueNative(o, field);   
            } else {
                Picture picture = value == null ? (Picture) DcModules.get(DcModules._PICTURE).getItem() : (Picture) value;
                value = picture; 

                DcImageIcon currentImage = (DcImageIcon) picture.getValue(Picture._D_IMAGE);
                DcImageIcon newImage = o instanceof DcImageIcon ? (DcImageIcon) o : 
                	                   o instanceof byte[] ? new DcImageIcon((byte[]) o) : null;

            	// prevent empty and incorrect images to be saved
	        	if (	newImage != null && 
	        			newImage.getIconHeight() != 0 && 
	        			newImage.getIconWidth() != 0) {
	        		
	        	    if (currentImage != null) 
	        	        currentImage.flush();
	        	    else 
	        	        picture.setNew(true);
	        	    
	                picture.setValue(Picture._D_IMAGE, newImage);
	                picture.isEdited(true);
	                
                    setValueNative(picture, field);
	            } else if (currentImage != null) {
	                currentImage.flush();
	                ((Picture) value).isDeleted(true);
                    setValueNative(picture, field);
                }
            }
       } else if (field.getValueType() == DcRepository.ValueTypes._ICON) {
    	   if (o instanceof DcImageIcon) {
    		   byte[] bytes = ((DcImageIcon) o).getBytes();
		       setValueNative(bytes != null ? new String(Base64.encode(bytes)) : null, field);
    	   } else if (o != null && o instanceof byte[] && ((byte[]) o).length > 0)
               setValueNative(new String(Base64.encode((byte[]) o)), field);
           else 
               setValueNative(o, field);
        } else {
            if (o == null) {
                setValueNative(null, field);
            } else {
                if (field.getValueType() == DcRepository.ValueTypes._DCOBJECTCOLLECTION) {
                    if (o instanceof Collection) // always create a new arraylist
                        setValueNative(new ArrayList<DcMapping>((Collection) o), field);
                    else 
                        logger.error("Trying to set " + o + " while expecting a collection of mappings object");
                } else if (field.getValueType() == DcRepository.ValueTypes._DCOBJECTREFERENCE) {
                    
                    if (CoreUtilities.isEmpty(o)) { 
                        setValueNative(null, field);
                    } else if (o instanceof DcObject) {
                        setValueNative(o, field);
                    } else if (!CoreUtilities.isEmpty(o) && field.getReferenceIdx() != field.getModule()) {
                        setValueNative(DcConfig.getInstance().getConnector().getItem(field.getReferenceIdx(), (String) o), field);
                    }

                    if (getValue() == null && !CoreUtilities.isEmpty(o)) {
                        setValueNative(o, field); // allow string reference to be set
                        logger.debug("Value is still null but new value not empty. Setting value for reference field (" + field + ") value '" + o + "')");
                    } 

                } else if ( (field.getValueType() == DcRepository.ValueTypes._LONG ||
                             field.getValueType() == DcRepository.ValueTypes._DOUBLE ) 
                             && !CoreUtilities.isEmpty(o)) {
                    try {
                        if (field.getFieldType() == UIComponents._FILESIZEFIELD) {
                            if (o instanceof Long) {
                                setValueNative(o, field);
                            } else if (o instanceof Number) {
                                setValueNative(Long.valueOf(((Number) o).intValue()), field);
                            } else if (o instanceof String && ((String) o).trim().length() > 0) {
                                String num = "";
                                for (char c : ((String) o).toCharArray()) {
                                    if (Character.isDigit(c))
                                        num += c; 
                                }
                                setValueNative(Long.valueOf(num), field);
                            } else {
                                throw new NumberFormatException();
                            }
                        }
                        
                        if (field.getValueType() == DcRepository.ValueTypes._LONG) {
                            if (o instanceof Long)
                                setValueNative(o, field);
                            else if (o instanceof Number)
                                setValueNative(Long.valueOf(((Number) o).intValue()), field);
                            else if (o instanceof String && ((String) o).trim().length() > 0)
                                setValueNative(Long.valueOf(((String) o).trim()), field);
                            else
                                throw new NumberFormatException();
                        }
                        
                        if (field.getValueType() == DcRepository.ValueTypes._DOUBLE) {
                            if (o instanceof Double) {
                                setValueNative(o, field);
                            } else if (o instanceof Number) {
                                setValueNative(new Double(((Number) o).doubleValue()), field);
                            } else if (o instanceof String && ((String) o).trim().length() > 0) {
                                String s = ((String) o).trim();
                                s = s.replaceAll(",", ".");
                                try {
                                    setValueNative(Double.valueOf(s), field);
                                } catch (NumberFormatException nfe) {
                                    logger.error("Could not set " + o + " for " + field.getDatabaseFieldName(), nfe);
                                }
                            } else {
                                throw new NumberFormatException();
                            }
                        }
                        
                    } catch (Exception e) {
                        logger.error("Could not set " + o + " for " + field + ". Not a number and invalid String.", e);
                        setValueNative(null, field); 
                    }
                } else if (field.getValueType() == DcRepository.ValueTypes._STRING) {
                    String s = Converter.databaseValueConverter((o instanceof String ? (String) o : o.toString()));
                    s = field.getMaximumLength() > 0 && s.length() > field.getMaximumLength() ?
                        s.substring(0, field.getMaximumLength()) : s;

                    setValueNative(s, field);         
                } else if (
                        field.getValueType() == DcRepository.ValueTypes._DATE ||
                        field.getValueType() == DcRepository.ValueTypes._DATETIME) {
                    if (o instanceof Date) {
                        setValueNative(o, field);
                    } else if (o instanceof String) {
                        try {
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            Date date = !o.equals("") ? formatter.parse((String) o) : null;
                            setValueNative(date, field);
                        } catch (java.text.ParseException e) {
                            try {
                                Date date = new SimpleDateFormat().parse((String) o);
                                setValueNative(date, field);
                            } catch (java.text.ParseException e2) {
                                logger.debug("Could not parse date for field " + field.getLabel(), e2);
                            }
                        }
                    }
                } else {
                    // for all other cases: just set the value
                    setValueNative(o, field); 
                }
            }
        }
    }
    
    private void setValueNative(Object value, DcField field) {
        this.value = value;
        this.changed = true;
    }
    
    /**
     * Clears the value and sets it to null.
     * @param nochecks Just do it, do not check whether we are dealing with an edited item
     */
    public void clear() {
    	if (value instanceof Picture) {
    		((Picture) value).unload();
    	}
    	
        value = null;
    }
    
    public Object getValue() {
        return value;
    }

    /**
     * Creates a string representation.
     */
    public String getValueAsString() {
        return value != null ? value.toString() : "";
    }
    
    @SuppressWarnings("unchecked")
    public String getDisplayString(DcField field) {
        Object o = getValue();
        String text = "";

        try {
            if (!CoreUtilities.isEmpty(o)) {
                if (field.getFieldType() == UIComponents._REFERENCESFIELD) {
                    Collection<DcMapping> mappings = (Collection<DcMapping>) o;
                    if (mappings != null) {
                        boolean first = true;
                        
                        for (DcMapping mapping : mappings) {
                            if (!first) text += ", ";
                            
                            text += mapping;
                            first = false;
                        }
                    }
                } else if (field.getFieldType() == UIComponents._RATINGCOMBOBOX) {
                    int value = o != null ? ((Long) o).intValue() : -1; 
                    text = Rating.getLabel(value);
    
                } else if (field.getFieldType() == UIComponents._TIMEFIELD) {
                    Calendar cal = Calendar.getInstance();
                    cal.clear();
    
                    int value = 0;
                        
                    if (o instanceof String)
                        value = Integer.parseInt((String) o);
                    
                    if (o instanceof Long)
                        value = ((Long) o).intValue();
    
                    int minutes = 0;
                    int seconds = 0;
                    int hours = 0;
    
                    if (value != 0) {
                        cal.set(Calendar.SECOND, value);
                        minutes = cal.get(Calendar.MINUTE);
                        seconds = cal.get(Calendar.SECOND);
                        hours = cal.get(Calendar.HOUR_OF_DAY);
                    }
    
                    String sSeconds = getDoubleDigitString(seconds);
                    String sMinutes = getDoubleDigitString(minutes);
                    text = "" + hours + ":" + sMinutes + ":" + sSeconds;
                } else if (field.getValueType() == DcRepository.ValueTypes._DOUBLE) {
                    text = CoreUtilities.toString((Double) o);
                } else if (field.getFieldType() == UIComponents._FILESIZEFIELD) {
                    text = CoreUtilities.toFileSizeString((Long) o);
                } else if (field.getFieldType() == UIComponents._FILEFIELD ||
                           field.getFieldType() == UIComponents._FILELAUNCHFIELD) {
                    text = CoreUtilities.getValidPath((String) o);
                } else {
                	text = o == null ? "" : o instanceof String ? (String) o : o.toString();
                }
            }
        } catch (Exception e) {
            logger.error("Error while creating the display string for field " + field + ", value " + o, e);
        }
        return text;
    }

    private String getDoubleDigitString(int value) {
        StringBuffer sb = new StringBuffer();
        if (value == 0) {
            sb.append("00");
        } else if (value < 10) {
            sb.append("0");
            sb.append(value);
        } else {
            sb.append(value);
        }
        return sb.toString();
    }

    @Override
    protected void finalize() throws Throwable {
        clear();
        super.finalize();
    }
    
    
}
