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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Directory scanner
 *
 * @author Jesper Pedersen <jesper.pedersen@jboss.org>
 */
public class DirectoryScanner
{
   /** Archives types that should be scanned */
   private static Set<String> archives = new HashSet<String>();

   static
   {
      archives.add(".jar");
   }

   /** Constructor */
   private DirectoryScanner()
   {
   }

   /**
    * Set archives
    *
    * @param scan The archives
    */
   public static void setArchives(String scan)
   {
      archives.clear();

      if (scan != null)
      {
         StringTokenizer st = new StringTokenizer(scan, ",");
         while (st.hasMoreTokens())
         {
            String token = st.nextToken();

            if (token.startsWith("*"))
            {
               token = token.substring(1);
            }

            archives.add(token.toLowerCase(Locale.US));
         }
      }

      if (archives.isEmpty())
      {
         archives.add(".jar");
      }
   }

   /**
    * Scan a directory for JAR files
    *
    * @param file The root directory
    * @return The list of JAR files
    */
   public static List<File> scan(File file)
   {
      return scan(file, null);
   }


   /**
    * Scan a directory for JAR files
    *
    * @param file     The root directory
    * @param excludes The set of excludes
    * @return The list of JAR files
    */
   public static List<File> scan(File file, Set<String> excludes)
   {
      try
      {
         return getFileListing(file, excludes);
      }
      catch (Exception e)
      {
         System.err.println(e.getMessage());
      }

      return null;
   }


   /**
    * Recursively walk a directory tree and return a List of all
    * Files found; the List is sorted using File.compareTo().
    *
    * @param aStartingDir is a valid directory, which can be read.
    * @param excludes     The set of excludes
    */
   private static List<File> getFileListing(File aStartingDir, Set<String> excludes) throws Exception
   {
      List<File> result = getFileListingNoSort(aStartingDir, excludes);
      Collections.sort(result);
      return result;
   }

   private static List<File> getFileListingNoSort(File aStartingDir, Set<String> excludes) throws Exception
   {
      List<File> result = new ArrayList<File>();

      File[] filesAndDirs = aStartingDir.listFiles();

      List<File> filesDirs = Arrays.asList(filesAndDirs);

      for (File file : filesDirs)
      {
         if (file.isFile())
         {
            String extension = null;

            if (file.getName().lastIndexOf(".") != -1)
            {
               extension = file.getName().substring(file.getName().lastIndexOf("."));
            }

            if (extension != null && archives.contains(extension))
            {
               boolean include = true;

               if (excludes != null)
               {
                  Iterator<String> it = excludes.iterator();
                  while (include && it.hasNext())
                  {
                     String exclude = it.next();

                     if (file.getName().equals(exclude) || file.getAbsolutePath().indexOf(exclude) != -1)
                     {
                        include = false;
                     }
                  }
               }

               if (include)
               {
                  result.add(file);
               }
            }
         }
         else if (file.isDirectory())
         {
            List<File> deeperList = getFileListingNoSort(file, excludes);
            result.addAll(deeperList);
         }
      }

      return result;
   }
}
