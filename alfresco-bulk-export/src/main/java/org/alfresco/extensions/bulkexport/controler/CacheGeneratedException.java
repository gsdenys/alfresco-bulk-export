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

/**
 *  This Exception is used to indicate expected behaviour under certain conditions. Not the best idea from a design point of view, oh well.
 */
public class CacheGeneratedException extends Exception
{
    public CacheGeneratedException()
    {

    }

    public CacheGeneratedException(String message)
    {
        super(message);
    }

    public CacheGeneratedException(Throwable cause)
    {
        super(cause);
    }

    public CacheGeneratedException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public CacheGeneratedException(String message, Throwable cause,
                                       boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
