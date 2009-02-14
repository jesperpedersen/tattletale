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
package org.jboss.tattletale;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Main
 * @author Jesper Pedersen <jesper.pedersen@jboss.org>
 */
public class Main
{

   /**
    * The usage method
    */
   private static void usage() 
   {
      System.out.println("Usage: Tattletale <directory>");
   }

   /**
    * The main method
    * @param args The arguments
    */
   public static void main(String[] args) 
   {
      if (args.length > 0) 
      {
         try 
         {
            String arg = args[0];

            Properties properties = new Properties();
            String propertiesFile = System.getProperty("jboss-tattletale.properties");
            boolean loaded = false;
            
            if (propertiesFile != null) 
            {
               FileInputStream fis = null;
               try
               {
                  fis = new FileInputStream(propertiesFile);
                  properties.load(fis);
                  loaded = true;
               } 
               catch (IOException e) 
               {
                  System.err.println("Unable to open " + propertiesFile);
               } 
               finally 
               {
                  if (fis != null) 
                  {
                     try
                     {
                        fis.close();
                     } 
                     catch (IOException ioe) 
                     {
                        // Nothing to do
                     }
                  }
               }
            }
            if (!loaded) 
            {
               InputStream is = null;
               try 
               {
                  ClassLoader cl = Thread.currentThread().getContextClassLoader();
                  is = cl.getResourceAsStream("jboss-tattletale.properties");
                  properties.load(is);
                  loaded = true;
               } 
               catch (Exception ie)
               {
                  // Properties file not found
               } 
               finally 
               {
                  if (is != null) 
                  {
                     try
                     {
                        is.close();
                     } 
                     catch (IOException ioe) 
                     {
                        // Nothing to do
                     }
                  }
               }
            }

            if (loaded)
            {
            }

            Map<String, SortedSet<Location>> locationsMap = new HashMap<String, SortedSet<Location>>();
            SortedSet<Archive> archives = new TreeSet<Archive>();
            SortedMap<String, SortedSet<String>> gProvides = new TreeMap<String, SortedSet<String>>();

            Set<Archive> known = new HashSet<Archive>();
            known.add(new Java5());

            File f = new File(arg);
            if (f.isDirectory())
            {
               List<File> fileList = DirectoryScanner.scan(f);

               for (File file : fileList)
               {
                  Archive archive = ArchiveScanner.scan(file, gProvides);
                  
                  SortedSet<Location> locations = locationsMap.get(archive.getName());
                  if (locations == null)
                  {
                     locations = new TreeSet<Location>();
                  }
                  locations.addAll(archive.getLocations());
                  locationsMap.put(archive.getName(), locations);

                  if (!archives.contains(archive))
                  {
                     archives.add(archive);
                  }
               }

               for (Archive a : archives)
               {
                  SortedSet<Location> locations = locationsMap.get(a.getName());

                  for (Location l : locations)
                  {
                     a.addLocation(l);
                  }
               }

               Dump.generateDependencies(archives, known);
               Dump.generateMultipleJars(gProvides);
               Dump.generateMultipleLocations(archives);

               for (Archive a : archives)
               {
                  Dump.generateArchiveReport(a);
               }

               Dump.generateIndex(archives);
            }

         }
         catch (Throwable t)
         {
            System.err.println(t.getMessage());
         }
      } 
      else 
      {
         usage();
      }
   }
}
