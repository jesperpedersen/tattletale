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
 * Represents a report severity
 *
 * @author Jesper Pedersen <jesper.pedersen@jboss.org>
 */
public class ReportSeverity
{
   /** INFO */
   public static final int INFO = 0;

   /** WARNING */
   public static final int WARNING = 1;

   /** ERROR */
   public static final int ERROR = 2;

   /** Constructor */
   private ReportSeverity()
   {
   }

   /**
    * Returns severity string
    *
    * @param severity constant value
    * @return severity string
    */
   public static String getSeverityString(int severity)
   {
      String output = "-";
      if (severity == ReportSeverity.INFO)
      {
         output = "INFO";
      }
      else if (severity == ReportSeverity.WARNING)
      {
         output = "WARNING";
      }
      else if (severity == ReportSeverity.ERROR)
      {
         output = "ERROR";
      }
      return output;
   }
}
