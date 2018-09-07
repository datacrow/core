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

package net.datacrow.core.utilities.isbn;



public abstract class ISBN {
    
    private static String CheckDigits = new String("0123456789X0");

    static int CharToInt(char a) {
        switch (a) {
        case '0':
            return 0;
        case '1':
            return 1;
        case '2':
            return 2;
        case '3':
            return 3;
        case '4':
            return 4;
        case '5':
            return 5;
        case '6':
            return 6;
        case '7':
            return 7;
        case '8':
            return 8;
        case '9':
            return 9;
        default:
            return -1;
        }
    }

    public static String getISBN10(String ISBN) throws InvalidBarCodeException {
        String s9;
        int i, n, v;

        s9 = ISBN.substring(3, 12);
        n = 0;
        for (i = 0; i < 9; i++) {
            v = CharToInt(s9.charAt(i));
            if (v == -1)
                throw new InvalidBarCodeException();
            else
                n = n + (10 - i) * v;
        }

        n = 11 - (n % 11);
        
        return s9 + CheckDigits.substring(n, n + 1);
    }

    public static String getISBN13(String ISBN10) throws InvalidBarCodeException {
        String s12;
        int i, n, v;
        boolean ErrorOccurred;
        ErrorOccurred = false;
        s12 = "978" + ISBN10.substring(0, 9);
        n = 0;
        for (i = 0; i < 12; i++) {
            if (!ErrorOccurred) {
                v = CharToInt(s12.charAt(i));
                if (v == -1)
                    throw new InvalidBarCodeException();
                else {
                    if ((i % 2) == 0)
                        n = n + v;
                    else
                        n = n + 3 * v;
                }
            }
        }

        n = n % 10;
        if (n != 0) n = 10 - n;
        return s12 + CheckDigits.substring(n, n + 1);
    }
    

//    private static final int[] weight13 = {1, 3, 1, 3, 1, 3, 1, 3, 1, 3, 1, 3};
//    private static final int[] weight10 = {1, 2, 3, 4, 5, 6, 7, 8, 9};
//    
//    public static String getISBN13(String isbn10) throws InvalidBarCodeException {
//        String s = "978";
//        char[] chars = isbn10.toCharArray();
//        for (int i = 0; i < chars.length; i++) {
//            if (Character.isDigit(chars[i]))
//                s += chars[i];
//        }
//        
//        if (s.length() < 13)
//            throw new InvalidBarCodeException();
//
//        // remove the check digit
//        int[] digits = new int[12];
//        chars = s.toCharArray();
//        for (int i = 0; i < digits.length; i++) {
//            digits[i] = Integer.parseInt("" + chars[i]);
//        }
//        
//        return convertToISBN13(digits);
//    }
//    
//    public static String getISBN10(String isbn13) throws InvalidBarCodeException {
//        char[] chars = isbn13.toCharArray();
//        String s = "";
//        for (int i = 0; i < chars.length; i++) {
//            if (Character.isDigit(chars[i])) 
//                s += chars[i];
//        }
//        
//        
//        if (s.length() < 10)
//            throw new InvalidBarCodeException();
//
//        int[] digits = new int[9];
//        chars = s.toCharArray();
//        int counter = 0;
//        for (int i = 3; i < chars.length - 1; i++) {
//            digits[counter++] = Integer.parseInt("" + chars[i]);
//        }
//        
//        return convertToISBN10(digits);
//    }    
    
    public static boolean isISBN13(String isbn) {
        String s = "";
        
        if (isbn != null) {
            char[] chars = isbn.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                if ("1234567890".indexOf(chars[i]) > -1) 
                    s += chars[i];
            }
        }
        
        return s.length() == 13;
    }

    public static boolean isISBN10(String isbn) {
        String s = "";
        
        if (isbn != null) {
            char[] chars = isbn.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                if ("1234567890X".indexOf(chars[i]) > -1) 
                    s += chars[i];
            }
        }
        
        return s.length() == 10;
    }
    
//    private static String convertToISBN10(int[] isbn13) {
//        String isbn10 = "";
//        
//        int sum = 0;
//        for (int i = 0; i < isbn13.length; i++)
//            sum += isbn13[i] * weight10[i];
//        
//        int checkdigit = sum % 11;
//        
//        for (int i = 0; i < isbn13.length; i++)
//            isbn10 += "" + isbn13[i];
//        
//        isbn10 += "" + checkdigit;
//        return isbn10;
//    }    
//    
//    private static String convertToISBN13(int[] isbn10) {
//        String isbn13 = "";
//        
//        int sum = 0;
//        for (int i = 0; i < isbn10.length; i++)
//            sum += isbn10[i] * weight13[i];
//        
//        int remainder = sum % 10;
//        
//        int checkdigit = 0;
//        if (remainder > 0)
//            checkdigit = 10 - remainder;
//        
//        for (int i = 0; i < isbn10.length; i++)
//            isbn13 += "" + isbn10[i];
//        
//        isbn13 += "" + checkdigit;
//        return isbn13;
//    }
}