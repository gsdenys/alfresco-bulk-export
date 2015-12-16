/**
 *  This file is part of Alfresco Bulk Export Tool.
 * 
 *  Alfresco Bulk Export Tool is free software: you can redistribute it 
 *  and/or modify it under the terms of the GNU General Public License as
 *  published by the Free Software Foundation, either version 3 of the 
 *  License, or (at your option) any later version.
 *
 *  Alfresco Bulk Export Tool  is distributed in the hope that it will be 
 *  useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General 
 *  Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along 
 *  with Alfresco Bulk Export Tool. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.extensions.bulkexport.controler;

import java.util.Arrays;
import java.util.Comparator;

/**
 *  This code was taken from http://stackoverflow.com/questions/20375067/java-decimal-string-sort 
 *
 *  Example usage:
 *    String[] k1 = { "0.10", "0.2", "0.1", "0", "1.10", "1.2", "1.1", "1",
 *                    "2.10", "2", "2.2", "2.1" };
 *    Arrays.sort(k1, new VersionNumberComparator());
 *    System.out.println(Arrays.asList(k1));
 *       
 */
public class VersionNumberComparator implements Comparator<String> 
{
  @Override
    public int compare(String version1, String version2) 
    {
        String[] v1 = version1.split("\\.");
        String[] v2 = version2.split("\\.");
        int major1 = major(v1);
        int major2 = major(v2);
        if (major1 == major2) 
        {
            return minor(v1).compareTo(minor(v2));
        }
        return major1 > major2 ? 1 : -1;
    }

    private int major(String[] version) 
    {
        return Integer.parseInt(version[0]);
    }

    private Integer minor(String[] version) 
    {
        return version.length > 1 ? Integer.parseInt(version[1]) : 0;
    }
}
