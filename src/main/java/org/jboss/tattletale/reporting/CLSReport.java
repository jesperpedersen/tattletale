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
import org.jboss.tattletale.reporting.classloader.ClassLoaderStructure;

import java.util.List;
import java.util.SortedSet;

/**
 * Abstract base class for all CLS based reports.
 * @author <a href="mailto:torben.jaeger@jit-consulting.de">Torben Jaeger</a>
 */
public abstract class CLSReport extends Report
{
   /** Known archives */
   private List<Archive> known;

   /** the CLS */
   private ClassLoaderStructure cls = null;

   /**
    * Constructor
    * @param severity The severity
    * @param archives The archives
    * @param name The name of the report
    * @param directory The name of the output directory
    * @param classloaderStructure The ClassloaderStructure
    */
   public CLSReport(int severity,
                    SortedSet<Archive> archives,
                    String name,
                    String directory,
                    String classloaderStructure
   )
   {
      super(severity, archives, name, directory);
      setCLS(classloaderStructure);
   }

   /**
    * Constructor
    * @param severity The severity
    * @param archives The archives
    * @param name The name of the report
    * @param directory The name of the output directory
    * @param classloaderStructure The ClassloaderStructure
    * @param known The known archives
    */
   public CLSReport(int severity,
                    SortedSet<Archive> archives,
                    String name,
                    String directory,
                    String classloaderStructure,
                    List<Archive> known)
   {
      this(severity, archives, name, directory, classloaderStructure);
      setKnown(known);
   }

   /**
    * get the ClassLoaderStructure
    * @return the ClassLoaderStructure
    * @see org.jboss.tattletale.reporting.classloader.ClassLoaderStructure
    */
   ClassLoaderStructure getCLS()
   {
      return cls;
   }

   private void setCLS(String classloaderStructure)
   {
      try
      {
         Class c = CLSReport.class.getClassLoader().loadClass(classloaderStructure);
         cls = (ClassLoaderStructure)c.newInstance();
      }
      catch (Exception ntd)
      {
         // Ignore
      }
   }

   /**
    * Set the known archives
    * @param known The list of known archives
    */
   private void setKnown(List<Archive> known)
   {
      this.known = known;
   }

   /**
    * Get the known archives
    * @return The list of known archives
    */
   public List<Archive> getKnown()
   {
      return known;
   }
}
