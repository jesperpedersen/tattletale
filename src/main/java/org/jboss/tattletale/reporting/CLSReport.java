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

import org.jboss.tattletale.profiles.Profile;
import org.jboss.tattletale.reporting.classloader.ClassLoaderStructure;

import java.util.List;

/**
 * Abstract base class for all CLS based reports.
 *
 * @author <a href="mailto:torben.jaeger@jit-consulting.de">Torben Jaeger</a>
 */
public abstract class CLSReport extends AbstractReport
{
   /** Known Profiles */
   private List<Profile> known;

   /** the CLS */
   private ClassLoaderStructure cls = null;

   /**
    * Constructor
    *
    * @param id        The report id
    * @param severity  The severity
    * @param name      The name of the report
    * @param directory The name of the output directory
    */
   public CLSReport(String id, int severity, String name, String directory)
   {
      super(id, severity, name, directory);
   }

   /**
    * get the ClassLoaderStructure
    *
    * @return the ClassLoaderStructure
    * @see org.jboss.tattletale.reporting.classloader.ClassLoaderStructure
    */
   ClassLoaderStructure getCLS()
   {
      return cls;
   }

   /**
    * Set the ClassLoader Structure
    *
    * @param classloaderStructure The Classloader Structure to be used in generating this report
    */
   public void setCLS(String classloaderStructure)
   {
      try
      {
         Class c = Thread.currentThread().getContextClassLoader().loadClass(classloaderStructure);
         cls = (ClassLoaderStructure) c.newInstance();
      }
      catch (Exception e)
      {
         try
         {
            Class c = CLSReport.class.getClassLoader().loadClass(classloaderStructure);
            cls = (ClassLoaderStructure) c.newInstance();
         }
         catch (Exception ntd)
         {
            // Ignore
         }
      }
   }

   /**
    * Set the known archives
    *
    * @param known The list of known archives
    */
   public void setKnown(List<Profile> known)
   {
      this.known = known;
   }

   /**
    * Get the known archives
    *
    * @return The list of known profiles
    */
   public List<Profile> getKnown()
   {
      return known;
   }
}
