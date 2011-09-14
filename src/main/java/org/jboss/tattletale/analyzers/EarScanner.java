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
import org.jboss.tattletale.core.EarArchive;
import org.jboss.tattletale.core.Location;

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
 * Scanner type that will be used to make scan calls on .ear files.
 *
 * @author Navin Surtani
 */
public class EarScanner extends AbstractScanner
{

   /**
    * Scan a .ear archive
    *
    * @param file -  The file to be scanned.
    *
    * @return the archive
    *
    * @throws IOException - if there is a problem with the file parameter
    */
   public Archive scan(File file) throws IOException
   {
      return this.scan(file, null, null, null);
   }

   /**
    * Scan a .ear archive
    *
    * @param ear         The ear file
    * @param gProvides   The global provides map
    * @param known       The set of known archives
    * @param blacklisted The set of black listed packages
    *
    * @return the archive
    * @throws IOException - if there is a problem with the file parameter
    */
   public Archive scan(File ear, Map<String, SortedSet<String>> gProvides, List<Archive> known,
                       Set<String> blacklisted) throws IOException
   {
      EarArchive earArchive = null;
      List<Archive> subArchiveList = new ArrayList<Archive>();
      ArchiveScanner jarScanner = new JarScanner();
      ArchiveScanner warScanner = new WarScanner();
      JarFile earFile = null;
      String name = ear.getName();
      try
      {
         String canonicalPath = ear.getCanonicalPath();
         earFile = new JarFile(ear);
         File extractedDir = Extractor.extract(earFile);
         Integer classVersion = null;
         SortedSet<String> requires = new TreeSet<String>();
         SortedMap<String, Long> provides = new TreeMap<String, Long>();
         SortedSet<String> profiles = new TreeSet<String>();
         SortedMap<String, SortedSet<String>> classDependencies = new TreeMap<String, SortedSet<String>>();
         SortedMap<String, SortedSet<String>> packageDependencies = new TreeMap<String, SortedSet<String>>();
         SortedMap<String, SortedSet<String>> blacklistedDependencies = new TreeMap<String, SortedSet<String>>();
         List<String> lSign = null;

         Enumeration<JarEntry> earEntries = earFile.entries();

         while (earEntries.hasMoreElements())
         {
            JarEntry earEntry = earEntries.nextElement();
            String entryName = earEntry.getName();
            InputStream entryStream = null;

            if (entryName.endsWith(".class"))
            {
               try
               {
                  entryStream = earFile.getInputStream(earEntry);
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
                  is = earFile.getInputStream(earEntry);

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
            else if (entryName.endsWith(".war"))
            {
               File warFile = new File(extractedDir.getCanonicalPath(), entryName);
               Archive warArchive = warScanner.scan(warFile, gProvides, known, blacklisted);
               subArchiveList.add(warArchive);
            }
         }

         if (provides.size() == 0 && subArchiveList.size() == 0)
         {
            return null;
         }

         String version = null;
         List<String> lManifest = null;
         Manifest manifest = earFile.getManifest();

         if (manifest != null)
         {
            version = super.versionFromManifest(manifest);
            lManifest = super.readManifest(manifest);
         }

         Location location = new Location(canonicalPath, version);

         // Obtain the class version if it is null. In other words, if there aren't any .class files in a
         // WEB-INF/classes directory. This would get the class version from the first archive in the list of sub
         // archives.
         if (subArchiveList != null && classVersion == null)
         {
            classVersion = subArchiveList.get(0).getVersion();
         }

         earArchive = new EarArchive(name, classVersion, lManifest, lSign, requires, provides, classDependencies,
               packageDependencies, blacklistedDependencies, location, subArchiveList);
         super.addProfilesToArchive(earArchive, profiles);

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
               ss.add(earArchive.getName());
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
            if (earFile != null)
            {
               earFile.close();
            }
         }
         catch (IOException closeException)
         {
            // No op
         }
      }
      return earArchive;
   }
}
