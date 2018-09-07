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

import java.util.StringTokenizer;

/**
 * A version definition.
 * 
 * @author Robert Jan van der Waals
 */
public class Version {

    private int minor = 0;
    private int major = 0;
    private int build = 0;
    private int patch = 0;

    /**
     * Initiates a new version
     * @param major
     * @param minor
     * @param build
     * @param patch
     */
    public Version(int major, int minor, int build, int patch) {
        this.minor = minor;
        this.major = major;
        this.build = build;
        this.patch = patch;
    }
    
    /**
     * Initiates a new version based on a string representation.
     * @param version   the String representing a Data Crow version.
     */
    public Version(String version) {
        String v = version.toLowerCase().startsWith("data crow beta") ? version.substring(15) :  
                   version.toLowerCase().startsWith("data crow") ? version.substring(10) : 
                   version;
                   
        StringTokenizer st = new StringTokenizer(v, ".");
        if (st.hasMoreElements())
            major = Integer.valueOf((String) st.nextElement());
        if (st.hasMoreElements())
            minor = Integer.valueOf((String) st.nextElement());
        if (st.hasMoreElements())
            build = Integer.valueOf((String) st.nextElement());
        if (st.hasMoreElements())
            patch = Integer.valueOf((String) st.nextElement());
    }
    
    /**
     * Returns the minor version number
     * @return  the minor version number
     */
    public int getMinor() {
        return minor;
    }

    /**
     * Returns the major version number
     * @return  the major version number
     */
    public int getMajor() {
        return major;
    }

    /**
     * Returns the build version number
     * @return  the build version number
     */
    public int getBuild() {
        return build;
    }

    /**
     * Returns the patch version number
     * @return  the patch version number
     */
    public int getPatch() {
        return patch;
    }
    
    /**
     * Checks whether the version is valid.
     * @return  version number determined y/n
     */
    public boolean isUndetermined() {
        return hashCode() == 0;
    }

    /**
     * Checks if this version is newer than the supplied version.
     * @param v the version to check
     * @return  newer y/n
     */
    public boolean isNewer(Version v) {
        return hashCode() > v.hashCode();
    }

    /**
     * Checks if this version is older than the supplied version.
     * @param v the version to check
     * @return  older y/n
     */
    public boolean isOlder(Version v) {
        return hashCode() < v.hashCode();
    }
    
    /**
     * Full string representation of the current version. 
     * @return  String representation; for example Data Crow 4.0.0.0 
     */
    public String getFullString() {
        return "Data Crow " + toString();
    }
    
    @Override
    public String toString() {
        return major + "." + minor + "." + build + (patch > 0 ? "." + String.valueOf(patch) : "");
    }
    
    @Override
    public int hashCode() {
        return (major * 10000000) + (minor * 100000) + (build * 100) + (patch);
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof Version)
            return ((Version) o).hashCode() == hashCode();
            
        return false;
    }
}
