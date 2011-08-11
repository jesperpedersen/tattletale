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
package org.jboss.tattletale.reporting.profiles;

import org.jboss.tattletale.core.Location;
import org.jboss.tattletale.core.NestableArchive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;

/**
 * Base profile class.
 *
 * @author Michele
 */
public abstract class CommonProfile extends NestableArchive
{

   /**
    * Constructor
    *
    * @param classSet The .gz file with the classes
    * @param type     Archive type
    * @param name     Profile name
    * @param version  Profile's class version
    * @param location Profile's location
    */
   public CommonProfile(String classSet, int type, String name, int version, String location)
   {
      super(type, name, version, null, null, null, null, null, null, null, null);
      loadClassList(classSet);
      addLocation(new Location(location, name));
   }

   /** Content of the class set file */
   protected SortedSet<String> classSet = new TreeSet<String>();

   /**
    * Loads this profile's classlist from the resources.
    *
    * @param resourceFile File name
    */
   protected void loadClassList(String resourceFile)
   {
      InputStream is = null;
      try
      {
         is = CDI10.class.getClassLoader().getResourceAsStream(resourceFile);

         GZIPInputStream gis = new GZIPInputStream(is);
         InputStreamReader isr = new InputStreamReader(gis);
         BufferedReader br = new BufferedReader(isr);

         String s = br.readLine();
         while (s != null)
         {
            classSet.add(s);
            s = br.readLine();
         }
      }
      catch (Exception e)
      {
         // Ignore
      }
      finally
      {
         try
         {
            if (is != null)
            {
               is.close();
            }
         }
         catch (IOException ioe)
         {
            // Ignore
         }
      }
   }

   /**
    * Returns true if this profile is selected by the supplied configuration
    * information.
    *
    * @param allProfiles All-Profiles flag
    * @param profileSet  Selected profiles as specified in the configuration
    * @return True if the Profile is to be included
    */
   public boolean included(boolean allProfiles, Set<String> profileSet)
   {
      return allProfiles || profileSet != null && (profileSet.contains(getProfileCode())
            || profileSet.contains(getProfileName()));
   }

   /** @return The code name of the profile */
   public abstract String getProfileCode();

   /** @return The long name of the profile */
   protected abstract String getProfileName();

   /**
    * Does the archives provide this class
    *
    * @param clz The class name
    * @return True if the class is provided; otherwise false
    */
   @Override
   public boolean doesProvide(String clz)
   {
      if (classSet.contains(clz))
      {
         return true;
      }

      return false;
   }
}
