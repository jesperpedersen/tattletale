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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Directory scanner
 * @author Jesper Pedersen <jesper.pedersen@jboss.org>
 */
public class DirectoryScanner
{

   /**
    * Scan a directory for JAR files
    * @param file The root directory
    * @return The list of JAR files
    */
   public static List<File> scan(File file)
   {
      return scan(file, null);
   }

  
   /**
    * Scan a directory for JAR files
    * @param file The root directory
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
    * @param excludes The set of excludes
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
            if (file.getName().endsWith(".jar"))
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
                  result.add(file);
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
