/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.tattletale.reporting;

/**
 * Represents a boolean filter
 *
 * @author Jesper Pedersen <jesper.pedersen@jboss.org>
 */
public class BooleanFilter implements Filter
{
   /** Boolean Filter */
   private Boolean booleanFilter;

   /** Constructor */
   public BooleanFilter()
   {
      this.booleanFilter = Boolean.FALSE;
   }

   /**
    * Is filtered
    *
    * @return True if filtered; otherwise false
    */
   public boolean isFiltered()
   {
      return booleanFilter.booleanValue();
   }

   /**
    * Is filtered
    *
    * @param archive The archive
    * @return True if filtered; otherwise false
    */
   public boolean isFiltered(String archive)
   {
      throw new UnsupportedOperationException("isFiltered(String) not supported");
   }

   /**
    * Is filtered
    *
    * @param archive The archive
    * @param query   The query
    * @return True if filtered; otherwise false
    */
   public boolean isFiltered(String archive, String query)
   {
      throw new UnsupportedOperationException("isFiltered(String, String) not supported");
   }

   /**
    * Init the filter
    *
    * @param filter The filter value
    */
   public void init(String filter)
   {
      if (filter != null)
      {
         if ("yes".equalsIgnoreCase(filter) || "on".equalsIgnoreCase(filter) || "true".equalsIgnoreCase(filter))
         {
            booleanFilter = Boolean.TRUE;
         }
         else
         {
            booleanFilter = Boolean.FALSE;
         }
      }
   }
}
