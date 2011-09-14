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

package org.jboss.tattletale.analyzers;

import org.jboss.tattletale.core.Archive;
import org.jboss.tattletale.core.ClassesArchive;
import org.jboss.tattletale.core.Location;
import org.jboss.tattletale.core.WarArchive;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Class that would be used to scan .war files.
 *
 * @author Navin Surtani
 */

public class WarScanner extends AbstractScanner
{
   /**
    * Scan a .war archive
    * @param war        The file
    * @return The archive
    */
   public Archive scan(File war)
   {
      return scan(war, null, null, null);
   }

   /**
    * Scan a .war archive
    *
    * @param war        The file
    * @param gProvides   The global provides map
    * @param known       The set of known archives
    * @param blacklisted The set of black listed packages
    * @return The archive
    */
   public Archive scan(File war, Map<String, SortedSet<String>> gProvides,
                       List<Archive> known,
                       Set<String> blacklisted)
   {

      WarArchive warArchive = null;
      List<Archive> subArchiveList = new ArrayList<Archive>();
      ArchiveScanner jarScanner = new JarScanner();
      JarFile warFile = null;
      String name = war.getName();
      try
      {

         String canonicalPath = war.getCanonicalPath();
         warFile = new JarFile(war);
         File extractedDir = Extractor.extract(warFile);
         Integer classVersion = null;
         SortedSet<String> requires = new TreeSet<String>();
         SortedMap<String, Long> provides = new TreeMap<String, Long>();
         SortedSet<String> profiles = new TreeSet<String>();
         SortedMap<String, SortedSet<String>> classDependencies = new TreeMap<String, SortedSet<String>>();
         SortedMap<String, SortedSet<String>> packageDependencies = new TreeMap<String, SortedSet<String>>();
         SortedMap<String, SortedSet<String>> blacklistedDependencies = new TreeMap<String, SortedSet<String>>();
         List<String> lSign = null;

         Enumeration<JarEntry> warEntries = warFile.entries();

         while (warEntries.hasMoreElements())
         {
            JarEntry warEntry = warEntries.nextElement();
            String entryName = warEntry.getName();
            InputStream entryStream = null;
            if (entryName.endsWith(".class"))
            {
               try
               {
                  entryStream = warFile.getInputStream(warEntry);
                  classVersion = scanClasses(entryStream, blacklisted, known, classVersion, provides,
                        requires, profiles, classDependencies, packageDependencies, blacklistedDependencies);

               }
               catch (Exception openException)
               {
                  openException.printStackTrace();
               }
               finally
               {
                  if (entryStream != null)
                  {
                     entryStream.close();
                  }
               }
            }
            else if (entryName.contains("META-INF") && entryName.endsWith(".SF"))
            {
               InputStream is = null;
               try
               {
                  is = warFile.getInputStream(warEntry);

                  InputStreamReader isr = new InputStreamReader(is);
                  LineNumberReader lnr = new LineNumberReader(isr);

                  if (lSign == null)
                  {
                     lSign = new ArrayList<String>();
                  }

                  String s = lnr.readLine();
                  while (s != null)
                  {
                     lSign.add(s);
                     s = lnr.readLine();
                  }
               }
               catch (Exception ie)
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
            else if (entryName.endsWith(".jar"))
            {
               File jarFile = new File(extractedDir.getCanonicalPath(), entryName);
               Archive jarArchive = jarScanner.scan(jarFile, gProvides, known, blacklisted);
               subArchiveList.add(jarArchive);
            }
         }
         if (provides.size() == 0 && subArchiveList.size() == 0)
         {
            return null;
         }


         String version = null;
         List<String> lManifest = null;
         Manifest manifest = warFile.getManifest();

         if (manifest != null)
         {
            version = super.versionFromManifest(manifest);
            lManifest = super.readManifest(manifest);
         }

         Location location = new Location(canonicalPath, version);

         String classesName = name + "/WEB-INF/classes";
         ClassesArchive classesArchive = new ClassesArchive(classesName, classVersion, lManifest, lSign, requires,
               provides, classDependencies, packageDependencies, blacklistedDependencies, location);
         subArchiveList.add(classesArchive);

         warArchive = new WarArchive(name, classVersion, lManifest, lSign, requires, provides,
               classDependencies, packageDependencies, blacklistedDependencies, location, subArchiveList);
         super.addProfilesToArchive(warArchive, profiles);
         Iterator<String> it = provides.keySet().iterator();
         while (it.hasNext())
         {
            String provide = it.next();

            if (gProvides != null)
            {
               SortedSet<String> ss = gProvides.get(provide);
               if (ss == null)
               {
                  ss = new TreeSet<String>();
               }
               ss.add(warArchive.getName());
               gProvides.put(provide, ss);
            }
            requires.remove(provide);
         }
      }
      catch (IOException ioe)
      {
         ioe.printStackTrace();
      }
      catch (Exception e)
      {
         System.err.println("Scan: " + e.getMessage());
         e.printStackTrace(System.err);
      }
      finally
      {
         try
         {
            if (warFile != null)
            {
               warFile.close();
            }
         }
         catch (IOException ioe)
         {
            // Ignore
         }
      }
      return warArchive;
   }

}
