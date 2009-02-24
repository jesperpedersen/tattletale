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

import org.jboss.tattletale.core.Archive;

import java.util.SortedSet;

/**
 * Represents a report
 * @author Jesper Pedersen <jesper.pedersen@jboss.org>
 */
public abstract class Report implements Comparable
{
   /** The severity */
   protected int severity;

   /** The actions */
   protected SortedSet<Archive> archives;

   /**
    * Constructor
    * @param severity The severity
    * @param archives The archives
    */
   public Report(int severity, SortedSet<Archive> archives)
   {
      this.severity = severity;
      this.archives = archives;
   }

   /**
    * Get the severity
    * @return The value
    */
   public int getSeverity()
   {
      return severity;
   }

   /**
    * Get the name of the report
    * @return The name
    */
   public abstract String getName();

   /**
    * Get the name of the directory
    * @return The directory
    */
   public abstract String getDirectory();

   /**
    * Generate the report(s)
    * @param outputDirectoru The top-level output directory
    */
   public abstract void generate(String outputDirectory);

   /**
    * Comparable
    * @param o The other object
    * @return The compareTo value
    */
   public int compareTo(Object o)
   {
      Report r = (Report)o;

      if (severity == r.getSeverity())
      {
         return getName().compareTo(r.getName());
      }
      else if (severity < r.getSeverity())
      {
         return -1;
      }
      else
      {
         return 1;
      }
   }

   /**
    * Equals
    * @param obj The other object
    * @return True if equals; otherwise false
    */
   public boolean equals(Object obj)
   {
      if (obj == null || !(obj instanceof Report))
      {
         return false;
      }

      Report r = (Report)obj;

      return getName().equals(r.getName());
   }

   /**
    * Hash code
    * @return The hash code
    */
   public int hashCode()
   {
      return 7 + 31 * getName().hashCode();
   }
}
