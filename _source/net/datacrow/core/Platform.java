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

package net.datacrow.core;

/**
 * Describes the platform on which Data Crow is running. 
 * 
 * @author Robert Jan van der Waals
 */
public final class Platform {
	
	private boolean isWin;
	private boolean isMac; 
	private boolean isLinux; 
	private boolean isVista;
	private boolean isWindows7;
	private boolean isWindows8;
	
	private boolean isJava2;
	private boolean isJava14;
	private boolean isJava15;
	private boolean isJava16; 
	
	private boolean isJavaSun;
	private boolean isJavaOracle;
	
	/**
	 * Creates a new instance and will gather all the necessary information 
	 * from the current Java VM and the Operation System.
	 */
	public Platform() {
		String os = System.getProperty("os.name");
		isWin = os.startsWith("Windows");
		isMac = !isWin && os.startsWith("Mac");
		isLinux = os.startsWith("Linux");
		isVista = isWin && os.indexOf("Vista")!=-1;
		String version = System.getProperty("java.version").substring(0,3);
		isJavaSun = System.getProperty("java.vendor").toLowerCase().indexOf("sun") > -1;
		isJavaOracle = System.getProperty("java.vendor").toLowerCase().indexOf("oracle") > -1;
		isWindows7 = isWin && os.startsWith("Windows 7");
		isWindows8 = isWin && os.startsWith("Windows 8");
		
		isJava2  = version.compareTo("1.1") > 0;
		isJava14 = version.compareTo("1.3") > 0;
		isJava15 = version.compareTo("1.4") > 0;
		isJava16 = version.compareTo("1.5") > 0;
	}

	/**
	 * Indicates if the Data Crow is running on a Windows platform.
     * @return  is Windows y/n
	 */
	public final boolean isWin() {
		return isWin;
	}

    /**
     * Indicates if the Data Crow is running on a Macintosh (Mac OS) platform.
     * @return  is MacOs y/n
     */
	public final boolean isMac() {
		return isMac;
	}

    /**
     * Indicates if the Data Crow is running on a Linux platform.
     * @return  is Linux y/n
     */
	public final boolean isLinux() {
		return isLinux;
	}
	
	/**
	 * Indicates if the Java version is from Sun
     * @return  is Java Sun y/n
	 */
    public final boolean isJavaSun() {
        return isJavaSun;
    }
    
    /**
     * Indicates if the Java version is from Oracle
     * @return  is Java Oracle y/n
     */
    public final boolean isJavaOracle() {
        return isJavaOracle;
    }    

    /**
     * Indicates whether the current Operating System is Windows 7
     * @return  is Windows 7 y/n
     */
    public boolean isWindows7() {
        return isWindows7;
    }

    /**
     * Indicates whether the current Operating System is Windows 8
     * @return  is Windows 8 y/n
     */
    public boolean isWindows8() {
        return isWindows8;
    }
    
    /**
     * Indicates if the Data Crow is running on a Windows Vista platform.
     * @return  is Windows Vista y/n
     */
	public final boolean isVista() {
		return isVista;
	}

    /**
     * Indicates if the Data Crow is running on Java 1.2.
     * @return  is Java 1.2 y/n
     */
	public final boolean isJava2() {
		return isJava2;
	}

    /**
     * Indicates if the Data Crow is running on Java 1.4.
     * @return  is Java 1.4 y/n
     */
	public final boolean isJava14() {
		return isJava14;
	}

    /**
     * Indicates if the Data Crow is running on Java 1.5.
     * @return  is Java 1.5 y/n
     */
	public final boolean isJava15() {
		return isJava15;
	}

    /**
     * Indicates if the Data Crow is running on Java 1.6.
     * @return  is Java 1.6 y/n
     */
	public final boolean isJava16() {
		return isJava16;
	}
}
