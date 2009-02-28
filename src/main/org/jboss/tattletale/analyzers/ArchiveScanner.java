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
import org.jboss.tattletale.core.ArchiveTypes;
import org.jboss.tattletale.core.Location;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;

/**
 * Archive scanner
 * @author Jesper Pedersen <jesper.pedersen@jboss.org>
 */
public class ArchiveScanner
{

   /**
    * Scan an archive
    * @param file The file
    * @return The archive
    */
   public static Archive scan(File file)
   {
      return scan(file, null);
   }

   /**
    * Scan an archive
    * @param file The file
    * @param gProvides The global provides map
    * @return The archive
    */
   public static Archive scan(File file, Map<String, SortedSet<String>> gProvides)
   {
      Archive archive = null;
      JarFile jarFile = null;

      try
      {
         ClassPool classPool = new ClassPool();

         String name = file.getName();
         String filename = file.getCanonicalPath();
         SortedSet<String> requires = new TreeSet<String>();
         SortedMap<String, Long> provides = new TreeMap<String, Long>();

         jarFile = new JarFile(file);
         Enumeration<JarEntry> e = jarFile.entries();
         
         while (e.hasMoreElements())
         {
            JarEntry jarEntry = e.nextElement();

            if (jarEntry.getName().endsWith(".class"))
            {
               InputStream is = null;
               try
               {
                  is = jarFile.getInputStream(jarEntry); 
                  CtClass ctClz = classPool.makeClass(is);
                  Long serialVersionUID = null;

                  try
                  {
                     CtField field = ctClz.getField("serialVersionUID");
                     serialVersionUID = (Long)field.getConstantValue();
                  }
                  catch (NotFoundException nfe)
                  {
                  }

                  provides.put(ctClz.getName(), serialVersionUID);
                  
                  Collection c = ctClz.getRefClasses();
                  Iterator it = c.iterator();

                  while (it.hasNext())
                  {
                     String s = (String)it.next();
                     requires.add(s);
                  }
               }
               catch (Exception ie)
               {
               }
               finally
               {
                  try
                  {
                     if (is != null)
                        is.close();
                  }
                  catch (IOException ioe)
                  {
                  }
               }
            }
         }

         String version = null;
         Manifest manifest = jarFile.getManifest();
         if (manifest != null)
         {
            Attributes mainAttributes = manifest.getMainAttributes();
            version = mainAttributes.getValue("Implementation-Version");
            if (version == null)
               version = mainAttributes.getValue("Version");
            if (version == null)
               version = mainAttributes.getValue("Specification-Version");
         }
         Location location = new Location(filename, version);

         archive = new Archive(ArchiveTypes.JAR, name, requires, provides, location);

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

               ss.add(archive.getName());
               gProvides.put(provide, ss);
            }

            requires.remove(provide);
         }
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
            if (jarFile != null)
               jarFile.close();
         }
         catch (IOException ioe)
         {
         }
      }

      return archive;
   }
}
