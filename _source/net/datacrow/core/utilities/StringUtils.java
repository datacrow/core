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

package net.datacrow.core.utilities;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    private static final Collator collator = Collator.getInstance(Locale.FRANCE);

    private static final Pattern[] normalizer = {
        Pattern.compile("('|~|\\!|@|#|\\$|%|\\^|\\*|_|\\[|\\{|\\]|\\}|\\||\\\\|;|:|`|\"|<|,|>|\\.|\\?|/|&|_|-)"),
        Pattern.compile("^(de|het|een|the|a|an|el|le|les|la|los|die|der|das|den) "),
        Pattern.compile(" (de|het|een|the|a|an|el|le|les|la|los|die|der|das|den) "),
        Pattern.compile("[(,)]")};
    
    public static String removeValuesBetween(String start, String end, String s) {
        String remove = null;
        while ((remove = StringUtils.getValueBetween(start, end, s)).length() > 0) {
            remove = start + remove + end;
            s = s.substring(0, s.indexOf(remove)) +
                s.substring(s.indexOf(remove) + remove.length());
        }
        return s;
    }
    
    public static String capitalize(String s) {
        StringBuffer sb = new StringBuffer();
        char prev = ' ';
        for (char c : s.toCharArray()) {
            if (prev == ' ' || sb.length() == 0)
                sb.append(Character.toUpperCase(c));
            else 
                sb.append(c);
            
            prev = c;
        }
        return sb.toString();
    }
    
    public static String getValueBetween(String start, String end, String s) {
        return getValueBetween(start, end, s, 0);
    }

    public static String getValueBetween(String start, String end, String s, int offset) {
        int indexBegin = s.toLowerCase().indexOf(start.toLowerCase(), offset);
        if (indexBegin > -1) {
            indexBegin += start.length();
            int indexEnd = s.toLowerCase().indexOf(end.toLowerCase(), indexBegin);
            if (indexEnd > -1) return s.substring(indexBegin, indexEnd);
        }
        return "";
    }
    
    public static String trim(String s) {
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '\n' || c == '\r' || c == '\t' || c == ' ' || Character.isSpaceChar(c)) 
                s = s.substring(1);
            else 
                break;
        }

        if (s.length() > 0) {
            for (int i = chars.length - 1; i >= 0; i--) {
                char c = chars[i];
                if (c == '\n' || c == '\r' || c == '\t' || c == ' ')
                    s = s.substring(0, s.length() - 1);
                else 
                    break;
            }
        }

        return s;
    }

    public static boolean equals(String s1, String s2) {
        
        String text1 = s1 != null ? s1 : "";
        String text2 = s2 != null ? s2 : ""; 
        
        text1 = normalize(text1.toLowerCase().trim());
        text2 = normalize(text2.toLowerCase().trim());
        
        text1 = text1.replaceAll(" ", "");
        text2 = text2.replaceAll(" ", "");
        
        collator.setStrength(Collator.FULL_DECOMPOSITION);
        return collator.compare(text1, text2) == 0;
    }
    
    public static String concatUserFriendly(String s, int length) {
    	if (length <= 0) return s;
        if (s.length() <= length) return s;
        
        s = s.substring(0, length - 1);
        for (int i = s.length() - 1; i > 0; i--) {
            if (s.charAt(i) == ' ') {
                s = s.substring(0, i) + " ....";
                break;
            }
        }
        return s;
    }
    
    public static Collection<String> getValuesBetween(String start, String end, String s) {
        Collection<String> result = new ArrayList<String>();
        int startPos = 0;
        while ((startPos = s.indexOf(start, startPos)) > -1) {
            if (startPos > -1 ) {
                startPos += start.length();
                int endPos = s.indexOf(end, startPos);
                if (endPos > -1) {
                    String part = s.substring(startPos, endPos);
                    if (part.length() > 0 && !result.contains(part))
                        result.add(part);
                }
            }
        }
        return result;
    }      
    
    public static String getContainedNumber(String s) {
        
        if (s.indexOf("%20") > -1)
            s = s.replaceAll("%20", "");
        
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if ("0123456789".indexOf(c) != -1)
                sb.append(c);
        }
        return sb.toString();
    }
    
    public static int backtrack(String s, int start, String to) {
        String check = "";
        for (int i = start; i > 0; i--) {
            check = s.charAt(i) + check;
            if (check.startsWith(to))
                return i + to.length();
        }
        return -1;
    }
    
    public static String[] getListElements(String s, String sep) {
        StringTokenizer st = new StringTokenizer(s, sep);
        String[] result = new String[st.countTokens()];
        
        int counter = 0;
        while (st.hasMoreElements())
            result[counter++] = (String) st.nextElement(); 
        
        return result;
    }    
    
    public static String normalize2(String s) {

        s = s.replaceAll("[\351\350\352\353]", "e");
        s = s.replaceAll("[\372\373\371\374]", "u");
        s = s.replaceAll("[\357\355\354\356]", "i");
        s = s.replaceAll("[\341\340\342\304\344\343\345\346]", "a");
        s = s.replaceAll("[\363\362\364\366\365\370]", "o");
        s = s.replaceAll("[\375\377]", "y");
        s = s.replaceAll("[\361]", "n");
        s = s.replaceAll("[\347]", "c");        
          
        s = s.replaceAll("[\310\311\312\313]", "E");
        s = s.replaceAll("[\332\331\333\334]", "U");
        s = s.replaceAll("[\315\314\316\317]", "I");
        s = s.replaceAll("[\301\306\300\302\302\303\305]", "A");
        s = s.replaceAll("[\323\322\324\326\325\330]", "O");
        s = s.replaceAll("[\335]", "Y");
        s = s.replaceAll("[\321]", "N");
        s = s.replaceAll("[\307]", "C");
        
        return s;
    }
    
    public static String normalize(String text) {
        String s = text == null ? "" : text.trim().toLowerCase();
        
        s = removeValuesBetween("]", "[", s);
        s = removeValuesBetween("(", ")", s);
        s = removeValuesBetween("[", "]", s);

        for (int i = 0; i < normalizer.length; i++) {
            Matcher ma = normalizer[i].matcher(s);
            s = ma.replaceAll(" ");
        }
        
        s = s.replaceAll("\n", "");
        s = s.replaceAll("\r", "");
        
        s = normalize2(s);
        
        while (s.length() > 2 && (Character.isDigit(s.charAt(s.length() -1)) || s.charAt(s.length() -1) == ' ')) 
            s = s.substring(0, s.length() -1);
        
        return s.trim();
    }
}
