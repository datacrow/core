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
//    CueCatDecoder.java                                                            //
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

import java.util.StringTokenizer;

import net.datacrow.core.utilities.Base64;

/**
This class handles decoding a line of CueCat output and creating
a CueCatCode object to store that data.  CueCat output consists of
3 period separated pieces of information, simialr to this:<BR><BR>
.C3nZC3nZC3nYDhv7D3DWCxnX.cGf2.ENr7C3b3DNbWChPXDxzZDNP6.<BR><BR>
The first item is the CueCat device ID.  The second is the barcode type.
The third is the actual bar code that was scanned.<BR><BR>
This class can be used to parse each item separately or all at once, returning
Strings or a CueCatCode.
<BR><BR>
The following is the process that is used to undo the encoding performed by the CueCat:
<UL>
<LI>Swap all upper and lower case characters, replace '-' wirh '/'
<LI>Base64 decode the String - note that the input is padded with '='s if the length
is not divisible by 4.  The Base 64 decoder used needs input lengths evenly divisible by 4.
 The '=' symbol is a blank or null in Base64.
<LI>XOR each byte of the result with 67.
</UL>

  @author Tim Patton (guinsu@timpatton.com)
  @version 1.0
  @date 09 September 2000
 */

public class CueCatDecoder
{
    /**
    Decodes one item fromthe CueCat output
    @param encoded the CueCat encoded item, with no periods
    @return the decoded String
     */
     
    public static String decodeToken(String encoded)
    {
        //pad out the input with ='s, our base64 decoder needs input
        //length divisibe by 4
        char chars[]=padInput(encoded);
        
        //Swap upper case to lower case and vice versa, minuses to slashes
        swapUpperLower(chars);
        
        //Decode with base64
        byte []decode_bytes=Base64.decode(chars);
        
        //Take each character XOR 67
        for(int i=0;i<decode_bytes.length;i++)
            decode_bytes[i]=(byte)(decode_bytes[i] ^ 67);
        
        return new String(decode_bytes);
    }
    /**
    Swap upper case to lower case and vice versa, minuses to slashes.  This method
    modifies the input array
    @param chars the character array to convert
     */
    public static void swapUpperLower(char []chars)
    {
        int diff='a'-'A';
        for(int i=0;i<chars.length;i++)
        {
            if(chars[i]>='a' && chars[i]<='z')
                chars[i]=(char)(chars[i]-diff);
            else if(chars[i]>='A' && chars[i]<='Z')
                chars[i] =(char)(chars[i]+diff);
            else if(chars[i] =='-')
                chars[i] = '/';
        }
    }
    /**
    Decode a while line of CueCat outout and create a CueCatCode object.  This method
    only processes the first three period separated items, anything after is ignored.

    @param s the line if input from the CueCat
    @return a CueCatCode object holding all the parsed data
     */
    public static CueCatCode decodeLine(String s)
    {
        StringTokenizer st=new StringTokenizer(s,".");
        String temp;
        String token;
        int count=0;
        String cueCode=null,barCode=null,barType=null;
        //loop through each period separated item
        while(st.hasMoreTokens() &&count<3)
        {
            token=st.nextToken();
            temp=decodeToken(token);
            if(count==0)
                cueCode=temp;
            else if (count==1)
                barType=temp;
            else if (count==2)
                barCode=temp;
            count++;
        }
        if(count<2)
            return null;
        else
            return new CueCatCode(cueCode,barType,barCode);
    }
    /**
    Adds '=' symbols to the end of any input until its length is evenly divisible
    by 4
    @param encoded the String to be padded out
    @return a new char[] of the correct length
     */
    public static char[] padInput(String encoded)
    {
        char chars[];
        if(encoded.length()%4 !=0)
        {
            int new_len= ((encoded.length() + 3) / 4) * 4;
            chars=new char[new_len];
            encoded.getChars(0,encoded.length(),chars,0);
            
            for(int i=encoded.length();i<chars.length;i++)
                chars[i]='=';
        }
        else
            chars=encoded.toCharArray();
            
        return chars;
    }
}
