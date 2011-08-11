/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
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
 * Represents a report status
 *
 * @author Jesper Pedersen <jesper.pedersen@jboss.org>
 */
public class ReportStatus
{
   /** GREEN */
   public static final int GREEN = 0;

   /** YELLOW */
   public static final int YELLOW = 1;

   /** RED */
   public static final int RED = 2;

   /**
    * Constructor
    *
    * @param actions The actions
    */
   private ReportStatus()
   {
   }

   /**
    * Returns status display color
    *
    * @param status status constant value
    * @return status display color
    */
   public static String getStatusColor(int status)
   {
      String output = "-";
      if (status == ReportStatus.GREEN)
      {
         output = "green";
      }
      else if (status == ReportStatus.YELLOW)
      {
         output = "orange";
      }
      else if (status == ReportStatus.RED)
      {
         output = "red";
      }
      return output;
   }
}
