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
import org.jboss.tattletale.core.ArchiveTypes;
import org.jboss.tattletale.core.Location;
import org.jboss.tattletale.core.NestableArchive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;

import javassist.bytecode.ClassFile;

/**
 * Sun: Java 5
 * @author Jesper Pedersen <jesper.pedersen@jboss.org>
 */
public class SunJava5 extends NestableArchive
{
   /** Class set */
   private static SortedSet<String> classSet = new TreeSet<String>();

   static
   {
      InputStream is = null;
      try
      {
         is = Thread.currentThread().getContextClassLoader().getResourceAsStream("sunjdk5.clz.gz");

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
               is.close();
         }
         catch (IOException ioe)
         {
            // Ignore
         }
      }
   }

   /**
    * Constructor
    */
   public SunJava5()
   {
      super(ArchiveTypes.JAR, "Sun Java 5", ClassFile.JAVA_5, null, null, null, null, null, null, null, null);

      Location l = new Location("rt.jar", "Sun JDK5");
      addLocation(l);

      addSubArchive(new SunJava5JCE());
      addSubArchive(new SunJava5JSSE());
   }

   /**
    * Does the archives provide this class
    * @param clz The class name
    * @return True if the class is provided; otherwise false
    */
   @Override
   public boolean doesProvide(String clz)
   {
      if (classSet.contains(clz))
         return true;

      if (getSubArchives() != null)
      {
         for (Archive a : getSubArchives())
         {
            if (a.doesProvide(clz))
               return true;
         }
      }
      
      return false;
   }
}
