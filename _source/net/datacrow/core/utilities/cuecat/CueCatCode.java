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

//////////////////////license & copyright header//////////////////////////////////
//                                                                              //
//    CueCatCode.java                                                           //
//                                                                              //
//                Copyright (c) 2000 by Tim Patton                              //
//                                                                              //
//This program is free software; you can redistribute it and/or                 //
//modify it under the terms of the GNU General Public License                   //
//as published by the Free Software Foundation; either version 2                //
//of the License, or (at your option) any later version.                        //
//                                                                              //
//This program is distributed in the hope that it will be useful,               //
//but WITHOUT ANY WARRANTY; without even the implied warranty of                //
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                 //
//GNU General Public License for more details.                                  //
//                                                                              //
//You should have received a copy of the GNU General Public License             //
//along with this program; if not, write to the Free Software                   //
//Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.   //
//                                                                              //
//                                                                              //
// Tim Patton <guinsu@timpatton.com>                                            //
//                                                                              //
////////////////////end license & copyright header////////////////////////////////

package net.datacrow.core.utilities.cuecat;

/**
  This class is used to hold the decoded output of a CueCat such as the
  CueCatID, the bar code data and type of barcode.  It also provides several
  static methods for handling the processing of the CueCat output and for barcodes
  in general.

  @author Tim Patton (guinsu@timpatton.com)
  @version 1.0
  @date 09 September 2000
 */
public class CueCatCode
{
    /**
     Unknown Barcode Type
     */
    public final static int BARCODE_UNKNOWN =0;
    //supported
    /**
     UPC-E Barcode - Supported by CueCat
     */
    public final static int BARCODE_UPC_E = 1;
    /**
     UPC-A Barcode - Supported by CueCat
     */
    public final static int BARCODE_UPC_A = 2;
    /**
     UPC-A, add2 Barcode - Supported by CueCat
     */
    public final static int BARCODE_UPC_A_ADD2 = 3;
    /**
     ISBN Barcode - Supported by CueCat
     */
    public final static int BARCODE_ISBN =4;
    /**
     ISBN, add 5 Barcode - Supported by CueCat
     */
    public final static int BARCODE_ISBN_ADD5 =5;

    //not well supported
    /**
     EAN8 Barcode - Large barcodes supported by CueCat
     */
    public final static int BARCODE_EAN_8 = 6;
    /**
     EAN13 Barcode - Large barcodes supported by CueCat
     */
    public final static int BARCODE_EAN_13 = 7;
    /**
     CODE128 Barcode - Large barcodes supported by CueCat
     */
    public final static int BARCODE_CODE128 = 8;
    /**
     CODE128-B Barcode - Large barcodes supported by CueCat
     */
    public final static int BARCODE_CODE128_B = 9;
    /**
     CODE128-C Barcode - Large barcodes supported by CueCat
     */
    public final static int BARCODE_CODE128_C = 10;
    /**
     CODE39 Barcode - Large barcodes supported by CueCat
     */
    public final static int BARCODE_CODE39 = 11;
    /**
     Interleaved 2 of 5 Barcode - Large barcodes supported by CueCat
     */
    public final static int BARCODE_INTERLEAVED_2OF5 = 12;
    /**
     ITF-6 Barcode - Large barcodes supported by CueCat
     */
    public final static int BARCODE_ITF_6 = 13;
    /**
     CueCat Barcode - Large barcodes supported by CueCat
     */
    public final static int BARCODE_CUE = 14;
    
    //not supported
    /**
     EAN13, add 2 Barcode - not belived to be supported by CueCat
     */
    public final static int BARCODE_EAN_13_ADD2 = 15;
    /**
     EAN13, add 5 Barcode - not belived to be supported by CueCat
     */
    public final static int BARCODE_EAN_13_ADD5 = 16;
    /**
     EAN128 Barcode - not belived to be supported by CueCat
     */
    public final static int BARCODE_EAN_128 = 17;
    /**
     Extended CODE39 Barcode - not belived to be supported by CueCat
     */
    public final static int BARCODE_EXTENDED_CODE39 = 18;
    /**
     CODE93 Barcode - not belived to be supported by CueCat
     */
    public final static int BARCODE_CODE93 = 19;
    /**
     2 of 5 Barcode - not belived to be supported by CueCat
     */
    public final static int BARCODE_2OF5 = 20;
    /**
     MSI Barcode - not belived to be supported by CueCat
     */
    public final static int BARCODE_MSI = 21;
    /**
     PostNet Barcode - not belived to be supported by CueCat
     */
    public final static int BARCODE_POSTNET = 22;
    /**
     RM4SCC Barcode - not belived to be supported by CueCat
     */
    public final static int BARCODE_RM4SCC = 23;
    /**
     4State Barcode - not belived to be supported by CueCat
     */
    public final static int BARCODE_4STATE = 24;
    /**
     SISAC Barcode - not belived to be supported by CueCat
     */
    public final static int BARCODE_SISAC = 25;
    
    /**
     Array of the strings output by the CueCat for each barcode type.
     */
    public final static String []barTypeStrings =
    {
        null,  "UPE", "UPA", "UP2", "IBN",
        "IB5", "E08", "E13", "128", "128",
        "128", "C39", "ITF", "ITF", "CC!",
        null,  null,  null,  null,  null,
        null,  null,  null,  null,  null,
        null
    };
    /**
    Name of each barcode type suitable for displaying
     */
    public final static String [] barTypePrintable =
    {
        "Unknown", "UPC-E", "UPC-A", "UPC-A, add 2",
        "ISBN", "ISBN, add 5", "EAN-8", "EAN-13", "CODE128",
        "CODE128-B", "CODE128-C", "CODE39", "Interleaved 2 of 5", "ITF-6",
        "Cue", "EAN-13, add 2", "EAN-13, add 5", "EAN-128", "Extended CODE39",
        "2 of 5", "MSI", "PostNet", "RM4SCC", "4-State",
        "SISAC"
    };
    
    /**
    The CueCat device ID, sent as the first part of each scan
     */
    public String cueCatID;
    /**
    The bar code type, sent as the second part of each scan
     */
    public int barType;
    /**
     The bar code type as represented by the CueCat in string form
     */
    public String barTypeStr;
    /**
    The actual bar code sent by the CueCat, in the case of ISBN's
    it is processed further to get it into a form appropriate for
    doing lookups on web sites
     */
    public String barCode;
    
    /**
      Constructor creates a new object from the 3 pieces of CueCat output
     */
    public CueCatCode(String cueCatID, String barTypeStr, String barCode)
    {
        this.cueCatID=cueCatID;
        this.barType=CueCatCode.getBarType(barTypeStr);
        this.barTypeStr=CueCatCode.getPrintableType(this.barType);
        this.barCode=CueCatCode.processCode(this.barType,barCode);
    }
    /**
    Returns the integer type of a bar code based on the type String
    that the CueCat outputs
    
    @param barString the decoded String that the CueCat used to represent the type of barcode
    @return int representing one of the constants for barcode types
     */
    public static int getBarType(String barString)
    {
        for(int i=0;i<barTypeStrings.length;i++)
            if(barString.equals(barTypeStrings[i]))
               return i;
        return BARCODE_UNKNOWN;
    }
    /**
    Returns a user-readable String of a specific barcode type.
    
    @param barType the integer representation of the bar code type
    @return String representing the printable version of the type
     */
    public static String getPrintableType(int barType)
    {
        return barTypePrintable[barType];
    }
    /**
    Method to further process any barcode such as ISBNs.
    
    @param type the bar code type (one of the constants listed above)
    @param code the actual bar code
    @return String either the processed bar code or the original bar code
    if no processing was needed
     */
    private static String processCode(int type, String code)
    {
        if(type==BARCODE_ISBN || type==BARCODE_ISBN_ADD5)
            return getISBNfromUPC(code);
        else if (type==BARCODE_UPC_E)
            return code; //fix this later
        else return  code;
    }
    /**
    method to convert UPCs of ISBNs into actual ISBNs that
    can be looked up at various web sites.
    Courtesy of:
    http://www.bisg.org/algorithms.html
    
    @param upc the UPC numerical representation of an ISBN
    @return String the ISBN number
     */
    public static String getISBNfromUPC(String upc)
    {
        StringBuffer isbn=new StringBuffer(upc);
        if (upc.indexOf("978") == 0)
        {
            isbn = new StringBuffer( upc.substring(3,3+9));
            int xsum = 0;
            int add = 0;
            String temp;
            for (int i = 0; i < 9; i++)
            {
                temp = isbn.substring(i,i+1);
                add=Integer.parseInt(temp);
                xsum += (10 - i) * add;
            }
            xsum %= 11;
            xsum = 11 - xsum;
            if (xsum == 10)
                isbn.append("X");
            else if (xsum==11)
                isbn.append("0");
            else
                isbn.append(xsum);
        }
        return isbn.toString();
    }
}
