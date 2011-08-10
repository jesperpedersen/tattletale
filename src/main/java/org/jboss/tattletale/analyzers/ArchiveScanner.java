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
import org.jboss.tattletale.core.JarArchive;
import org.jboss.tattletale.core.Location;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
      return scan(file, null, null, null);
   }

   /**
* Scan an archive
* @param file The file
* @param gProvides The global provides map
* @param known The set of known archives
* @param blacklisted The set of black listed packages
* @return The archive
*/
   public static Archive scan(File file,
                              Map<String, SortedSet<String>> gProvides,
                              List<Archive> known,
                              Set<String> blacklisted)
   {
      Archive archive = null;
      JarFile jarFile = null;

      try
      {
         ClassPool classPool = new ClassPool();

         String name = file.getName();
         String filename = file.getCanonicalPath();
         Integer classVersion = null;
         SortedSet<String> requires = new TreeSet<String>();
         SortedMap<String, Long> provides = new TreeMap<String, Long>();
         SortedSet<String> profiles = new TreeSet<String>();
         SortedMap<String, SortedSet<String>> classDependencies = new TreeMap<String, SortedSet<String>>();
         SortedMap<String, SortedSet<String>> packageDependencies = new TreeMap<String, SortedSet<String>>();
         SortedMap<String, SortedSet<String>> blacklistedDependencies = new TreeMap<String, SortedSet<String>>();

         List<String> lSign = null;

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

                  if (classVersion == null)
                     classVersion = Integer.valueOf(ctClz.getClassFile2().getMajorVersion());

                  Long serialVersionUID = null;
                  try
                  {
                     CtField field = ctClz.getField("serialVersionUID");
                     serialVersionUID = (Long)field.getConstantValue();
                  }
                  catch (NotFoundException nfe)
                  {
                     // Ignore - not serializable
                  }

                  provides.put(ctClz.getName(), serialVersionUID);

                  int pkgIdx = ctClz.getName().lastIndexOf(".");
                  String pkg = null;
                  
                  if (pkgIdx != -1)
                     pkg = ctClz.getName().substring(0, pkgIdx);

                  Collection c = ctClz.getRefClasses();
                  Iterator it = c.iterator();

                  while (it.hasNext())
                  {
                     String s = (String)it.next();
                     requires.add(s);

                     SortedSet<String> cd = classDependencies.get(ctClz.getName());
                     if (cd == null)
                        cd = new TreeSet<String>();

                     cd.add(s);
                     classDependencies.put(ctClz.getName(), cd);

                     int rPkgIdx = s.lastIndexOf(".");
                     String rPkg = null;
                  
                     if (rPkgIdx != -1)
                        rPkg = s.substring(0, rPkgIdx);

                     boolean include = true;

                     if (known != null)
                     {
                        Iterator<Archive> kit = known.iterator();
                        while (include && kit.hasNext())
                        {
                           Archive a = kit.next();
                           if (a.doesProvide(s))
                           {
                              profiles.add(a.getName());
                              include = false;
                           }
                        }
                     }

                     if (pkg != null && rPkg != null && !pkg.equals(rPkg) && include)
                     {
                        SortedSet<String> pd = packageDependencies.get(pkg);
                        if (pd == null)
                           pd = new TreeSet<String>();

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
                              bl = true;
                        }

                        if (bl)
                        {
                           String key = pkg;

                           if (key == null)
                              key = "";

                           SortedSet<String> bld = blacklistedDependencies.get(key);
                           if (bld == null)
                              bld = new TreeSet<String>();

                           bld.add(rPkg);
                           blacklistedDependencies.put(key, bld);
                        }
                     }
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
                        is.close();
                  }
                  catch (IOException ioe)
                  {
                     // Ignore
                  }
               }
            }
            else if (jarEntry.getName().indexOf("META-INF") != -1 && jarEntry.getName().endsWith(".SF"))
            {
               InputStream is = null;
               try
               {
                  is = jarFile.getInputStream(jarEntry);

                  InputStreamReader isr = new InputStreamReader(is);
                  LineNumberReader lnr = new LineNumberReader(isr);

                  if (lSign == null)
                     lSign = new ArrayList<String>();

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
                        is.close();
                  }
                  catch (IOException ioe)
                  {
                     // Ignore
                  }
               }
            }
         }

         if (provides.size() == 0)
            return null;

         String version = null;
         List<String> lManifest = null;
         Manifest manifest = jarFile.getManifest();
         if (manifest != null)
         {
            Attributes mainAttributes = manifest.getMainAttributes();
            version = mainAttributes.getValue("Specification-Version");
            if (version == null)
               version = mainAttributes.getValue("Implementation-Version");
            if (version == null)
               version = mainAttributes.getValue("Version");

            if (version == null && manifest.getEntries() != null)
            {
               Iterator ait = manifest.getEntries().values().iterator();
               while (version == null && ait.hasNext())
               {
                  Attributes attributes = (Attributes)ait.next();

                  version = attributes.getValue("Specification-Version");
                  if (version == null)
                     version = attributes.getValue("Implementation-Version");
                  if (version == null)
                     version = attributes.getValue("Version");
               }
            }

            lManifest = readManifest(manifest);
         }
         Location location = new Location(filename, version);

         archive = new JarArchive(name, classVersion.intValue(), lManifest, lSign, requires, provides,
                                  classDependencies, packageDependencies, blacklistedDependencies, location);

         if (profiles.size() > 0)
         {
            for (String profile : profiles)
            {
               archive.addProfile(profile);
            }
         }

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
      catch (IOException ioe)
      {
         // Probably not a JAR archive
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
            // Ignore
         }
      }

      return archive;
   }

   /**
* Read the manifest
* @param manifest The manifest
* @return The manifest as strings
*/
   private static List<String> readManifest(Manifest manifest)
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
}
