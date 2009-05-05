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
import java.util.List;

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
      try
      {
         return getFileListing(file);
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
    */
   private static List<File> getFileListing(File aStartingDir) throws Exception 
   {
      List<File> result = getFileListingNoSort(aStartingDir);
      Collections.sort(result);
      return result;
   }
   
   private static List<File> getFileListingNoSort(File aStartingDir) throws Exception 
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
               result.add(file);
            }
         }
         else if (file.isDirectory()) 
         {
            List<File> deeperList = getFileListingNoSort(file);
            result.addAll(deeperList);
         }
      }

      return result;
   }
}
