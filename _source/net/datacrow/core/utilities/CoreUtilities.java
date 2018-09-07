/******************************************************************************
 *                                     __                                     *
 *                              <-----/@@\----->                              *
 *                             <-< <  \\//  > >->                             *
 *                               <-<-\ __ /->->                               *
 *                               Data /  \ Crow                               *
 *                                   ^    ^                                   *
 *                       (c) 2003 The Data Crow team                          *
 *                              info@datacrow.net                             *
 *                                                                            *
 *                                                                            *
 *       This library is free software; you can redistribute it and/or        *
 *        modify it under the terms of the GNU Lesser General Public          *
 *       License as published by the Free Software Foundation; either         *
 *     version 2.1 of the License, or (at your option) any later version.     *
 *                                                                            *
 *      This library is distributed in the hope that it will be useful,       *
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU       *
 *           Lesser General Public License for more details.                  *
 *                                                                            *
 *     You should have received a copy of the GNU Lesser General Public       *
 *    License along with this library; if not, write to the Free Software     *
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA   *
 *                                                                            *
 ******************************************************************************/

package net.datacrow.core.utilities;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import net.datacrow.core.DcConfig;
import net.datacrow.core.DcRepository;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcAssociate;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcImageIcon;
import net.datacrow.core.objects.DcMapping;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.Picture;
import net.datacrow.core.objects.helpers.Permission;
import net.datacrow.core.utilities.comparators.DcObjectComparator;
import net.datacrow.settings.DcSettings;

import org.apache.log4j.Logger;

public class CoreUtilities {
    
    private static Logger logger = Logger.getLogger(CoreUtilities.class.getName());
    
    private static final FileSystemView fsv = new JFileChooser().getFileSystemView();
    private static final Properties languages = new Properties();
    
    private static final Pattern[] normalizer = {
        Pattern.compile("('|~|\\!|@|#|\\$|%|\\^|\\*|_|\\[|\\{|\\]|\\}|\\||\\\\|;|:|`|\"|<|,|>|\\.|\\?|/|&|_|-)"),
        Pattern.compile("[(,)]")};
    
    
    public static List<DcObject> sort(List<DcObject> items) {
    	boolean mappings = false;
    	
    	if (items == null || items.size() <= 1)
    		return items;
    	
    	List<DcObject> result = new ArrayList<DcObject>(); 
    	List<DcObject> references = new ArrayList<DcObject>();
        DcObject ref;
        for (DcObject reference : items) {
            if (reference.getModule().getType() == DcModule._TYPE_MAPPING_MODULE) {
                ref = ((DcMapping) reference).getReferencedObject();
                
                if (ref != null) {
                    if (ref.getModule().getType() == DcModule._TYPE_ASSOCIATE_MODULE)
                    	((DcAssociate ) ref).setName();
                }
                
                if (ref != null) references.add(ref);
                
                mappings = true;
            } else {
                if (reference.getModule().getType() == DcModule._TYPE_ASSOCIATE_MODULE)
                	((DcAssociate ) reference).setName();
            	
            	references.add(reference);
            }
        }
        
        int sortIdx = references.size() > 0 ? references.get(0).getDefaultSortFieldIdx() : 0;
        Collections.sort(references, new DcObjectComparator(sortIdx));
        
        if (mappings) {
        	for (DcObject reference : references) {
        		for (DcObject mapping : items) {
        		    if (mapping.getValue(DcMapping._B_REFERENCED_ID).equals(reference.getID())) {
        				result.add(mapping);
        				break;
        			}
        		}
        	}
        } else {
        	result.addAll(references);
        }
        
        return result;
    }
    
    public static String getExternalIPAddress() {
        BufferedReader in = null;
        String ip = null;
        
        try {
            URL url = new URL("http://checkip.amazonaws.com");
            in = new BufferedReader(new InputStreamReader(url.openStream()));
            ip = in.readLine();
        } catch (Exception e) {
            logger.error("Could not retrieve external IP address", e);
            
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ioe) {}
            }
        }
        
        return ip;
    }
    
    public static boolean isDriveTraversable(File drive) {
        return fsv.isTraversable(drive);
    }
    
    public static boolean canRead(File drive) {
        return fsv.isTraversable(drive) && drive.canRead();        
    }
    
    public static String getSystemName(File f) {
        return fsv.getSystemDisplayName(f);
    }
    
public static String getDatabaseTableName() {
        return "tbl_" + CoreUtilities.getUniqueID().replaceAll("-", "");
    }
    
    public static String getDatabaseColumnName() {
        return "col_" + CoreUtilities.getUniqueID().replaceAll("-", "");
    }
    
    /**
     * Converts an ordinary string to something which is allowed to be used in a
     * filename or pathname.
     */
    public static String toFilename(String text) {
        String s = text == null ? "" : text.trim().toLowerCase();

        s = s.replaceAll("\n", "");
        s = s.replaceAll("\r", "");
        
        for (int i = 0; i < normalizer.length; i++) {
            Matcher ma = normalizer[i].matcher(s);
            s = ma.replaceAll("");
        }
        
        s = StringUtils.normalize2(s);
        s = s.replaceAll("[\\-]", "");
        s = s.replaceAll(" ", "");
        
        return s.trim();
    }
    
    /**
     * Converts an ordinary string to something which is allowed to be used in a
     * column or table name. It does not check for any preserved names (!). 
     */
    public static String toDatabaseName(String text) {
        String s = toFilename(text);
        
        if (s.length() > 0) {
            char c = s.charAt(0);
            if (Character.isDigit(c))
                s = "db_" + s;      
        }
        
        return s.trim();
    }
    
    public static String getFirstName(String name) {
    	if (name.indexOf(",") > -1) {
    		return name.substring(name.indexOf(",") + 1).trim();
    	} else if (name.indexOf(" ") > -1) {
    		String firstname = name.substring(0, name.indexOf(" ")).trim();
    		if (name.indexOf("(") > -1)
    			firstname += " " + name.substring(name.indexOf("("));
    		
    		return firstname;
    	} else {
    		return "";
    	}
    }
    
    public static String getLastName(String name) {
    	if (name.indexOf(",") > -1) {
    		return name.substring(0, name.indexOf(",")).trim();
    	} else if (name.indexOf(" ") > -1) {
    		String lastname = name.substring(name.indexOf(" ") + 1).trim();
    		if (lastname.indexOf("(") > -1)
    			lastname = lastname.substring(0, lastname.indexOf("(")).trim();
    		
    		return lastname;
    	} else {
    		return name;
    	}
    }
    
    public static String getName(String firstname, String lastname) {
        firstname = firstname == null ? "" : firstname.trim();
        lastname = lastname == null ? "" : lastname.trim();
        return (firstname + " " + lastname).trim();
    }

    
    public static Object getQueryValue(Object o, DcField field) {
        Object value = o;
        
        // This first check does not make much sense but I honestly do not dare to remove it.. for now..
        if (isEmpty(value) && (field.getModule() != DcModules._PERMISSION && field.getIndex() == Permission._B_FIELD))
            value = null;
        else if ("".equals(value))
            value = null;
        else if (value instanceof DcObject)
            value = ((DcObject) value).getID();
        else if ((field.getValueType() == DcRepository.ValueTypes._DOUBLE) && value instanceof String)
            value = Double.valueOf((String) value);
        else if ((field.getValueType() == DcRepository.ValueTypes._BIGINTEGER ||
                  field.getValueType() == DcRepository.ValueTypes._LONG) &&
                 value instanceof String)
            value = Long.valueOf((String) value);
        else if (value instanceof DcObject)
            value = ((DcObject) value).getID();        

        return value;
    }  
    
    public static String getValidPath(String filename) {
        
        if (filename == null) return "";
        
        String s = filename;
        
        if (filename.indexOf('\\') > -1 || filename.indexOf('/') > -1) {
            String[] mappings = DcSettings.getStringArray(DcRepository.Settings.stDriveMappings);
            if (mappings != null) {
                for (String mapping : mappings) {
                    StringTokenizer st = new StringTokenizer(mapping, "/&/");
                    String drive = (String) st.nextElement();
                    String mapsTo = (String) st.nextElement();
                    
                    if (s.length() > drive.length() && s.substring(0, drive.length()).equalsIgnoreCase(drive)) {
                        s = mapsTo + s.substring(drive.length());
                        break;
                    }
                }
            }

            s = getRelativePath(DcConfig.getInstance().getInstallationDir(), s);
        }
        
        return s;
    }
    
    public static String getRelativePath(String basePath, String targetFile) {
        
        if (targetFile == null || targetFile.startsWith("."))
            return targetFile;
        
        String relativePath = "";
        
        //make them equal first
        if (!DcConfig.getInstance().getPlatform().isWin()) {
            basePath = new File(basePath.replaceAll("\\\\", "\\/")).toString();
            targetFile = new File(targetFile.replaceAll("\\\\", "\\/")).toString();
        } else {
            basePath = new File(basePath.replaceAll("\\/", "\\\\")).toString();
            targetFile = new File(targetFile.replaceAll("\\/", "\\\\")).toString();            
        }
        
        while (basePath.endsWith("/") || basePath.endsWith("\\"))
            basePath = basePath.substring(0, basePath.length() - 1);

        while (targetFile.endsWith("/") || targetFile.endsWith("\\"))
            targetFile = targetFile.substring(0, targetFile.length() - 1);

        if (targetFile.startsWith(basePath)) {
            relativePath = "." + File.separator + targetFile.substring(basePath.length() + 1, targetFile.length());
        } else {
            relativePath = targetFile;
        }
        
        return relativePath;
    }
    
    public static String getOriginalFilename(String filename) {
        String s = filename;
        
        if (s != null) {
            String[] mappings = DcSettings.getStringArray(DcRepository.Settings.stDriveMappings);
            if (mappings != null) {
                for (String mapping : mappings) {
                    StringTokenizer st = new StringTokenizer(mapping, "/&/");
                    String mapsTo = (String) st.nextElement();
                    String drive = (String) st.nextElement();
                    
                    if (s.length() > drive.length() && s.substring(0, drive.length()).equalsIgnoreCase(drive)) {
                        s = mapsTo + s.substring(drive.length());
                        break;
                    }
                }
            }
        }
        
        return s;
    }

//    public static boolean isKeyword(String name) {
//        String s = name.toUpperCase();
//        return Tokens.isKeyword(s) || s.equals("CREATE") || s.equals("ALTER") || s.equals("SELECT") ||
//               s.equals("DROP") || s.equals("TRUNCATE") || s.equals("MODIFY") || s.equals("TABLE") || s.equals("COLUMN");        
//    }
    


    
    public static boolean isSystemDrive(File drive) {
    	return getSystemDrives().contains(drive);
    }
    
    public static Collection<File> getSystemDrives() {
        Collection<File> drives = new ArrayList<File>();
        for (File file : File.listRoots())
            drives.add(file);
        return drives;
    }
    
    public static Collection<File> getDrives() {
        Collection<File> drives = getSystemDrives();
        String[] dirs = DcSettings.getStringArray(DcRepository.Settings.stDirectoriesAsDrives);
        
        if (dirs != null) {
            for (String dir: dirs)
                drives.add(new File(dir));
        }
        
        return drives;
    }
    

    public static boolean sameImage(byte[] img1, byte[] img2) {
        boolean same = img1.length == img2.length;
        if (same) {
            for (int i = 0; i < img1.length; i++) {
                same = img1[i] == img2[i];
                if (!same)
                    break;
            }
        }
        return same;
    }    
    
    public static Collection<String> getCharacterSets() {
        Collection<String> characterSets = new ArrayList<String>(); 
        for (String name :  Charset.availableCharsets().keySet()) {
            characterSets.add(name);
        }
        return characterSets;
    }
    
    public static String toFileSizeString(Long l) {
        if (l == null) return "";
        
        String s = DcSettings.getString(DcRepository.Settings.stDecimalGroupingSymbol);
        char groupingChar = s != null && s.length() > 0 ? s.charAt(0) : ',';

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator(groupingChar);
        symbols.setInternationalCurrencySymbol("EUR");

        DecimalFormat format = new DecimalFormat("###,###", symbols);
        format.setGroupingSize(3);
        return format.format(l) + " bytes";
    }
    
    public static String toString(Double d) {
        if (d == null) return "";
        
        String s = DcSettings.getString(DcRepository.Settings.stDecimalSeparatorSymbol);
        char decimalSep = s != null && s.length() > 0 ? s.charAt(0) : ',';
        s = DcSettings.getString(DcRepository.Settings.stDecimalGroupingSymbol);
        char groupingSep = s != null && s.length() > 0 ? s.charAt(0) : '.';
        
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setDecimalSeparator(decimalSep);
        symbols.setGroupingSeparator(groupingSep);
        
        
        DecimalFormat format = new DecimalFormat("###,###.00", symbols);
        format.setGroupingSize(3);
        return format.format(d);
    }

    public static Long getSize(File file) {
        return Long.valueOf(file.length());  
    }
    
    /**
     * Creates a unique ID. Can be used for custom IDs in the database.
     * Based on date / time + random number
     * @return unique ID as String
     */
    public static String getUniqueID() {
        return UUID.randomUUID().toString();
    }
    
    public static boolean isSameFile(File src, File tgt) {
        if (CoreUtilities.getSize(src).longValue() == CoreUtilities.getSize(tgt).longValue()) {
            String hash1 = Hash.getInstance().calculateHash(src.toString());
            String hash2 = Hash.getInstance().calculateHash(tgt.toString());
            return hash1.equals(hash2);
        }

        return false;

    }
    
    /**
     * Retrieved the file extension of a file
     * @param f file to get the extension from
     * @return extension or empty string
     */
    public static String getExtension(File f) {
        String name = f.getName().toLowerCase();
        int i = name.lastIndexOf( "." );
        if (i == -1) {
            return "";
        }
        return name.substring( i + 1 );
    }    
    
    public static int getIntegerValue(String s) {
        char[] characters = s.toCharArray();
        String test = "";
        for (int i = 0; i < characters.length; i++) {
            if (Character.isDigit(characters[i])) test += "" + characters[i];
        }
        
        int number = 0;
        try {
            number = Integer.valueOf(test).intValue();
        } catch (Exception ignore) {}
        
        return number;
    }
   
    /**
     * Reads the content of a file (fully)
     * @param file file to retrieve the content from
     * @return content of the file as a byte array
     * @throws Exception
     */
    public static byte[] readFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(is);
        
        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            bis.close();
            throw new IOException("File is too large to read " + file.getName());
        }
    
        byte[] bytes = new byte[(int)length];
    
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length && (numRead=bis.read(bytes, offset, bytes.length-offset)) >= 0)
            offset += numRead;

        bis.close();
        is.close();

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }
        
        return bytes;    
    }

    public static DcImageIcon base64ToImage(String base64) {
        byte[] bytes = Base64.decode(base64.toCharArray());
        return new DcImageIcon(bytes, false);
    }
    
    public static byte[] getBytes(DcImageIcon icon) {
        return getBytes(icon, DcImageIcon._TYPE_PNG);
    }
    
    public static byte[] getBytes(DcImageIcon icon, int type) {
        return getBytes(icon.getImage(), type);
    }
    
    public static byte[] getBytes(Image image, int type) {
    	BufferedImage bi;
    	if (image instanceof BufferedImage)
    		bi = (BufferedImage) image;
    	else 
    		bi = CoreUtilities.toBufferedImage(new DcImageIcon(image), -1, -1);
    	
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(baos);
        
        byte[] bytes = null;
        try {
            ImageIO.write(bi, (type == DcImageIcon._TYPE_JPEG ? "JPG" : "PNG"), bos);
            bos.flush();
            bytes = baos.toByteArray();
            bi.flush();
        } catch (IOException e) {
            logger.error(e, e);
        } 
        
        try {
            baos.close();
            bos.close();
        } catch (IOException e) {
            logger.error(e, e);
        }
        
        return bytes;
    }
    
    public static void writeToFile(DcImageIcon icon, File file) throws Exception {
        writeScaledImageToFile(icon, file, DcImageIcon._TYPE_PNG, -1, -1);
    }   

    public static void writeToFile(byte[] b, String filename) throws Exception {
        writeToFile(b, new File(filename));
    } 
    
    public static void writeToFile(byte[] b, File file) throws Exception {
        FileOutputStream fos = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(fos);

        bos.write(b);
        bos.flush();
        bos.close();
    }   
    
    public static Image getScaledImage(byte[] bytes) {
        return getScaledImage(bytes, 250, 200);
    }

    public static Image getScaledImage(DcImageIcon icon) {
        return getScaledImage(icon, 250, 200);
    }    

    public static Image getScaledImage(byte[] bytes, int width, int height) {
        return toBufferedImage(new DcImageIcon(bytes), width, height);
    }    
    
    public static Image getScaledImage(DcImageIcon icon, int width, int height) {
        return toBufferedImage(icon, width, height);
    }    
    
    public static void writeScaledImageToFile(DcImageIcon icon, File file) throws Exception {
    	writeScaledImageToFile(icon, file, DcImageIcon._TYPE_PNG, 250, 200);
    }

    public static void writeScaledImageToFile(DcImageIcon icon, File file, int type, int w, int h) throws Exception {
        BufferedImage bufferedImage = toBufferedImage(icon, w, h);
        ImageIO.write(bufferedImage, (type == DcImageIcon._TYPE_JPEG ? "JPG" : "PNG"), file);
        bufferedImage.flush();
    }       
    
    public static String getHexColor(Color color) {
        String hexColor = "#" + Integer.toHexString(color.getRed());
        hexColor += Integer.toHexString(color.getGreen());
        hexColor += Integer.toHexString(color.getBlue()); 
        return hexColor.toUpperCase();
    }
    
    public static String toHex(byte in[]) {
        byte ch = 0x00;
        int i = 0; 

        String pseudo[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8",
                           "9", "A", "B", "C", "D", "E", "F"};

        StringBuffer out = new StringBuffer(in.length * 2);
        while (i < in.length) {

            ch = (byte) (in[i] & 0xF0); // Strip off high nibble
            ch = (byte) (ch >>> 4);     // shift the bits down
            ch = (byte) (ch & 0x0F);    // must do this is high order bit is on!
            out.append(pseudo[ch]);

            ch = (byte) (in[i] & 0x0F); // Strip off low nibble 
            out.append(pseudo[ch]);
            
            i++;
        }

        return out.toString();
    }       
    
    public static boolean isEmpty(Object o) {
        boolean empty = o == null;
        
        if (o instanceof Number) 
            empty = o.equals(Long.valueOf(-1)) || o.equals(Long.valueOf(0));
        else if (!empty && o instanceof String)
            empty = ((String) o).trim().length() == 0;
        else if (!empty && o instanceof Collection)
            empty = ((Collection) o).size() == 0;
        else if (!empty && o instanceof Picture)
            empty = !((Picture) o).hasImage();
        
        return empty;
    }

    public static String getComparableString(Object o) {
        return CoreUtilities.isEmpty(o) ? "" : o instanceof String ? ((String) o) : o.toString();
    }
    
    public static void copy(File currentFile, File newFile, boolean overwrite) throws IOException {
        
        if (currentFile.equals(newFile))
            return;
        
        if (!overwrite && newFile.exists())
            return;
        
        // native code failed to move the file; do it the custom way
        FileInputStream fis = new FileInputStream(currentFile);
        BufferedInputStream bis = new BufferedInputStream(fis);
    
        FileOutputStream fos = new FileOutputStream(newFile);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        
        int count = 0;
        int b;
        while ((b = bis.read()) > -1) {
            bos.write(b);
            count++;
            if (count == 2000) {
                bos.flush();
                count = 0;
            }
        }
        
        bos.flush();
        
        fis.close();
        bis.close();
        bos.close();
    }
    
    public static void rename(File currentFile, File newFile, boolean overwrite) throws IOException {
        
        if (currentFile.equals(newFile))
            return;

        if (newFile.exists() && !overwrite)
            return;
        
        if (newFile.getParentFile() != null)
            newFile.getParentFile().mkdirs();
        
        boolean success = currentFile.renameTo(newFile);
        
        if (!success) {
            copy(currentFile, newFile, overwrite);
            currentFile.delete();
        }
    }

    public static String getCurrentDirectory() throws Exception {
    	File fl = new File(".");
    	fl = fl.getCanonicalFile();
        return fl.toString();
    }
    
    /**
     * Gets the content of a file and converts it to a base64 string
     * @param url URL of file
     * @return base64 content of the file
     */
    public static String fileToBase64String(File file) {
        try {
            byte[] b = readFile(file);
            file = null;
            return String.valueOf(Base64.encode(b));
        } catch (Exception e) {
            logger.error("Error while converting content from " + file + " to base64", e);
        }
        return "";
    }
    
    public static BufferedImage toBufferedImage(ImageIcon icon) {
        return toBufferedImage(icon, -1, -1);
    }  
    
    public static BufferedImage toBufferedImage(ImageIcon icon, int width, int height) {
        
        // make sure the image is loaded
        icon.setImage(icon.getImage());
        Image image = icon.getImage();
        
        int imgW = image.getWidth(null);
        int imgH = image.getHeight(null);
        
        int w = width > 0 ? width : imgW;
        int h = height > 0 ? height : imgH;
        
        if (imgW <= width && imgH <= height) {
            // do not scale down if not needed
            w = imgW;
            h = imgH;
        } else {
            // make sure the image ratio remains the same
            double scaledRatio = (double) w / (double) h;
            double imageRatio = (double) imgW / (double) imgH;
            if (scaledRatio < imageRatio)
                h = (int) (w / imageRatio);
            else
                w = (int) (h * imageRatio);
        }
        
        BufferedImage bi = null;
        if (w > -1 && h > -1) {
	        bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	        
	        Graphics2D g = bi.createGraphics();
	        g.setComposite(AlphaComposite.Src);
	        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	        g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
	        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
	        
	        g.drawImage(image, 0, 0, w, h, null);
	        g.dispose();
	        bi.flush();

        } else {
        	logger.error("The image size -1 is invalid");
        	bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        }
        
        return bi;
    }
    
    public static String getLanguage(String iso) {
        
        if (languages.isEmpty()) {
            try {
                FileInputStream fis = new FileInputStream(new File(DcConfig.getInstance().getResourcesDir(), "languages.properties"));
                languages.load(fis);
                fis.close();
            } catch (Exception e) {
                logger.error("Could not load languages file", e);
            }
        }
        
        return (String) languages.get(iso);
    }
    
}
