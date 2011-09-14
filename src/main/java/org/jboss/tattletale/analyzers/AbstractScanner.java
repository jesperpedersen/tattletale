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
import org.jboss.tattletale.profiles.Profile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;


/**
 * Abstract class that contains utility methods that other scanner extensions can use.
 *
 * @author Navin Surtani
 * */
public abstract class AbstractScanner implements ArchiveScanner
{
   /**
    * Read the manifest
    *
    * @param manifest The manifest
    * @return The manifest as strings
    */
   protected List<String> readManifest(Manifest manifest)
   {
      List<String> result = new ArrayList<String>();

      try
      {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         manifest.write(baos);

         ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
         InputStreamReader isr = new InputStreamReader(bais);
         LineNumberReader lnr = new LineNumberReader(isr);

         String s = lnr.readLine();
         while (s != null)
         {
            result.add(s);
            s = lnr.readLine();
         }
      }
      catch (IOException ioe)
      {
         // Ignore
      }

      return result;
   }

   /**
    * Returns the version as a String once read from the manifest.
    *
    * @param manifest - the manifest obtained from the JarFile
    * @return - the version as a String.
    */
   protected String versionFromManifest(Manifest manifest)
   {

      Attributes mainAttributes = manifest.getMainAttributes();
      String version = mainAttributes.getValue("Specification-Version");
      if (version == null)
      {
         version = mainAttributes.getValue("Implementation-Version");
      }
      if (version == null)
      {
         version = mainAttributes.getValue("Version");
      }

      if (version == null && manifest.getEntries() != null)
      {
         Iterator ait = manifest.getEntries().values().iterator();
         while (version == null && ait.hasNext())
         {
            Attributes attributes = (Attributes) ait.next();

            version = attributes.getValue("Specification-Version");
            if (version == null)
            {
               version = attributes.getValue("Implementation-Version");
            }
            if (version == null)
            {
               version = attributes.getValue("Version");
            }
         }
      }
      return version;
   }

   /**
    * Method that will add a set of profiles (Strings) to the archive.
    *
    * @param archive - the archive
    * @param profiles - the set of Strings.
    */

   protected void addProfilesToArchive(Archive archive, SortedSet<String> profiles)
   {
      if (profiles.size() > 0)
      {
         for (String profile : profiles)
         {
            archive.addProfile(profile);
         }
      }
   }

/**
    * Static method called to scan class files within an input stream and populate the data structure parameters.
    *
    *
    * @param is - input stream
    * @param blacklisted The set of black listed packages
    * @param known       The set of known archives
    * @param classVersion - the version of the class file
    * @param provides - the map of provides
    * @param requires - the set of requires
    * @param profiles - the set of profiles
    * @param classDependencies - the map of class dependencies
    * @param packageDependencies - the map of package dependencies
    * @param blacklistedDependencies - the map of blacklisted dependencies
    *
    * @return An {@link Integer} representing the class version.
    *
    * @throws IOException - if the Javassist ClassPool cannot make the CtClass based on the input stream.
    */

   public Integer scanClasses(InputStream is, Set<String> blacklisted, List<Profile> known, Integer classVersion,
                           SortedMap<String, Long> provides, SortedSet<String> requires,
                           SortedSet<String> profiles, SortedMap<String, SortedSet<String>> classDependencies,
                           SortedMap<String, SortedSet<String>> packageDependencies,
                           SortedMap<String, SortedSet<String>> blacklistedDependencies)
      throws IOException
   {
      ClassPool classPool = new ClassPool();
      CtClass ctClz = classPool.makeClass(is);

      if (classVersion == null)
      {
         classVersion = Integer.valueOf(ctClz.getClassFile2().getMajorVersion());
      }

      Long serialVersionUID = null;
      try
      {
         CtField field = ctClz.getField("serialVersionUID");
         serialVersionUID = (Long) field.getConstantValue();
      }
      catch (NotFoundException nfe)
      {
         // Ignore - not serializable
      }

      provides.put(ctClz.getName(), serialVersionUID);

      int pkgIdx = ctClz.getName().lastIndexOf(".");
      String pkg = null;

      if (pkgIdx != -1)
      {
         pkg = ctClz.getName().substring(0, pkgIdx);
      }

      Collection c = ctClz.getRefClasses();
      Iterator it = c.iterator();

      while (it.hasNext())
      {
         String s = (String) it.next();
         requires.add(s);

         SortedSet<String> cd = classDependencies.get(ctClz.getName());
         if (cd == null)
         {
            cd = new TreeSet<String>();
         }

         cd.add(s);
         classDependencies.put(ctClz.getName(), cd);

         int rPkgIdx = s.lastIndexOf(".");
         String rPkg = null;

         if (rPkgIdx != -1)
         {
            rPkg = s.substring(0, rPkgIdx);
         }

         boolean include = true;

         if (known != null)
         {
            Iterator<Profile> kit = known.iterator();
            while (include && kit.hasNext())
            {
               Profile p = kit.next();
               if (p.doesProvide(s))
               {
                  profiles.add(p.getName());
                  include = false;
               }
            }
         }

         if (pkg != null && rPkg != null && !pkg.equals(rPkg) && include)
         {
            SortedSet<String> pd = packageDependencies.get(pkg);
            if (pd == null)
            {
               pd = new TreeSet<String>();
            }

            pd.add(rPkg);
            packageDependencies.put(pkg, pd);
         }

         if (blacklisted != null)
         {
            boolean bl = false;

            Iterator<String> bit = blacklisted.iterator();
            while (!bl && bit.hasNext())
            {
               String blp = bit.next();
               if (s.startsWith(blp))
               {
                  bl = true;
               }
            }

            if (bl)
            {
               String key = pkg;

               if (key == null)
               {
                  key = "";
               }

               SortedSet<String> bld = blacklistedDependencies.get(key);
               if (bld == null)
               {
                  bld = new TreeSet<String>();
               }

               bld.add(rPkg);
               blacklistedDependencies.put(key, bld);
            }
         }
      }
      return classVersion;
   }

}
