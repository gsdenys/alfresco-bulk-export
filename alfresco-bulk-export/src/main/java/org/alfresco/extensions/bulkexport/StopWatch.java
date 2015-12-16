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
package org.alfresco.extensions.bulkexport;

import java.io.IOException;

public class StopWatch 
{ 
    public StopWatch() 
    {
        startTime = System.currentTimeMillis();
    } 

    public long elapsedTime() 
    {
        long now = System.currentTimeMillis();
        return (now - startTime) / 1000;
    }

    private long startTime;
} 
