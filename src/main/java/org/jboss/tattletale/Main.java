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

import org.jboss.tattletale.analyzers.ArchiveScanner;
import org.jboss.tattletale.analyzers.DirectoryScanner;
import org.jboss.tattletale.core.Archive;
import org.jboss.tattletale.core.ArchiveTypes;
import org.jboss.tattletale.core.Location;
import org.jboss.tattletale.reporting.BlackListedReport;
import org.jboss.tattletale.reporting.CDI10;
import org.jboss.tattletale.reporting.ClassLocationReport;
import org.jboss.tattletale.reporting.DependantsReport;
import org.jboss.tattletale.reporting.DependsOnReport;
import org.jboss.tattletale.reporting.Dump;
import org.jboss.tattletale.reporting.EliminateJarsReport;
import org.jboss.tattletale.reporting.GraphvizReport;
import org.jboss.tattletale.reporting.InvalidVersionReport;
import org.jboss.tattletale.reporting.JarReport;
import org.jboss.tattletale.reporting.JavaEE5;
import org.jboss.tattletale.reporting.MultipleJarsReport;
import org.jboss.tattletale.reporting.MultipleLocationsReport;
import org.jboss.tattletale.reporting.NoVersionReport;
import org.jboss.tattletale.reporting.OSGiReport;
import org.jboss.tattletale.reporting.PackageMultipleJarsReport;
import org.jboss.tattletale.reporting.Report;
import org.jboss.tattletale.reporting.SealedReport;
import org.jboss.tattletale.reporting.Seam22;
import org.jboss.tattletale.reporting.SignReport;
import org.jboss.tattletale.reporting.Spring25;
import org.jboss.tattletale.reporting.SunJava5;
import org.jboss.tattletale.reporting.SunJava6;
import org.jboss.tattletale.reporting.TransitiveDependantsReport;
import org.jboss.tattletale.reporting.TransitiveDependsOnReport;
import org.jboss.tattletale.reporting.UnusedJarReport;

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
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Main
 * @author Jesper Pedersen <jesper.pedersen@jboss.org>
 * @author Jay Balunas <jbalunas@jboss.org>
 */
public class Main
{
   /** Source */
   private String source;

   /** Destination */
   private String destination;

   /** Profiles */
   private String profiles;

   /** Excludes */
   private String excludes;

   /** Blacklisted */
   private String blacklisted;

   /**
    * Constructor
    */
   public Main()
   {
      this.source = ".";
      this.destination = ".";
      this.profiles = null;
      this.excludes = null;
      this.blacklisted = null;
   }

   /**
    * Set source
    * @param source The value
    */
   public void setSource(String source)
   {
      this.source = source;
   }

   /**
    * Set destination
    * @param destination The value
    */
   public void setDestination(String destination)
   {
      this.destination = destination;
   }

   /**
    * Set profiles
    * @param profiles The value
    */
   public void setProfiles(String profiles)
   {
      this.profiles = profiles;
   }

   /**
    * Set excludes
    * @param excludes The value
    */
   public void setExcludes(String excludes)
   {
      this.excludes = excludes;
   }

   /**
    * Set blacklisted
    * @param blacklisted The value
    */
   public void setBlacklisted(String blacklisted)
   {
      this.blacklisted = blacklisted;
   }

   /**
    * Execute
    * @exception Exception Thrown if an error occurs
    */
   public void execute() throws Exception
   {
      Properties properties = loadDefault();

      String classloaderStructure = null;

      Set<String> profileSet = null;
      boolean allProfiles = false;

      Set<String> blacklistedSet = null;

      Set<String> excludeSet = null;


      classloaderStructure = properties.getProperty("classloader");
         
      if (profiles != null)
      {
         profileSet = new HashSet<String>();

         StringTokenizer st = new StringTokenizer(profiles, ",");
         while (st.hasMoreTokens())
         {
            String token = st.nextToken().trim();
            if ("*".equals(token))
            {
               allProfiles = true;
            }
            else
            {
               allProfiles = false;
               profileSet.add(token);
            }
         }
      }

      if (profileSet == null && properties.getProperty("profiles") != null)
      {
         profileSet = new HashSet<String>();
         
         StringTokenizer st = new StringTokenizer(properties.getProperty("profiles"), ",");
         while (st.hasMoreTokens())
         {
            String token = st.nextToken().trim();
            if ("*".equals(token))
            {
               allProfiles = true;
            }
            else
            {
               allProfiles = false;
               profileSet.add(token);
            }
         }
      }

      if (blacklisted != null)
      {
         blacklistedSet = new HashSet<String>();

         StringTokenizer st = new StringTokenizer(blacklisted, ",");
         while (st.hasMoreTokens())
         {
            String token = st.nextToken().trim();
               
            if (token.endsWith(".*"))
            {
               token.substring(0, token.indexOf(".*"));
            }

            if (token.endsWith(".class"))
            {
               token.substring(0, token.indexOf(".class"));
            }
            
            blacklistedSet.add(token);
         }
      }

      if (blacklistedSet == null && properties.getProperty("blacklisted") != null)
      {
         blacklistedSet = new HashSet<String>();

         StringTokenizer st = new StringTokenizer(properties.getProperty("blacklisted"), ",");
         while (st.hasMoreTokens())
         {
            String token = st.nextToken().trim();
               
            if (token.endsWith(".*"))
            {
               token.substring(0, token.indexOf(".*"));
            }

            if (token.endsWith(".class"))
            {
               token.substring(0, token.indexOf(".class"));
            }
            
            blacklistedSet.add(token);
         }
      }

      if (excludes != null)
      {
         excludeSet = new HashSet<String>();
         excludeSet.addAll(parseExcludes(excludes));
      }

      if (excludeSet == null && properties.getProperty("excludes") != null)
      {
         excludeSet = new HashSet<String>();
         excludeSet.addAll(parseExcludes(properties.getProperty("excludes")));
      }

      if (classloaderStructure == null || classloaderStructure.trim().equals(""))
      {
         classloaderStructure = "org.jboss.tattletale.reporting.classloader.NoopClassLoaderStructure";
      }

      Map<String, SortedSet<Location>> locationsMap = new HashMap<String, SortedSet<Location>>();
      SortedSet<Archive> archives = new TreeSet<Archive>();
      SortedMap<String, SortedSet<String>> gProvides = new TreeMap<String, SortedSet<String>>();
      
      List<Archive> known = new ArrayList<Archive>();

      if (profileSet == null || allProfiles || profileSet.size() == 0 || 
          profileSet.contains("java5") || profileSet.contains("Sun Java 5"))
         known.add(new SunJava5());
      
      if (profileSet == null || allProfiles || profileSet.contains("java6") || profileSet.contains("Sun Java 6"))
         known.add(new SunJava6());
      
      if (allProfiles || profileSet != null && (profileSet.contains("ee5") || profileSet.contains("Java Enterprise 5")))
         known.add(new JavaEE5());
      
      if (allProfiles || profileSet != null && (profileSet.contains("seam22") || profileSet.contains("Seam 2.2")))
         known.add(new Seam22());
      
      if (allProfiles || profileSet != null && (profileSet.contains("cdi10") || profileSet.contains("CDI 1.0")))
         known.add(new CDI10());
      
      if (allProfiles || profileSet != null && (profileSet.contains("spring25") || profileSet.contains("Spring 2.5")))
         known.add(new Spring25());
      
      File f = new File(source);
      if (f.isDirectory())
      {
         List<File> fileList = DirectoryScanner.scan(f, excludeSet);

         for (File file : fileList)
         {
            Archive archive = ArchiveScanner.scan(file, gProvides, known, blacklistedSet);

            if (archive != null)
            {
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
         }

         for (Archive a : archives)
         {
            SortedSet<Location> locations = locationsMap.get(a.getName());

            for (Location l : locations)
            {
               a.addLocation(l);
            }
         }
               
         //Write out report     
         String outputDir = setupOutputDir(destination);
         outputReport(outputDir, classloaderStructure, archives, gProvides, known);
      }
   }

   /**
    * Load default values
    * @return The properties
    */
   private Properties loadDefault()
   {
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
         FileInputStream fis = null;
         try
         {
            fis = new FileInputStream("jboss-tattletale.properties");
            properties.load(fis);
            loaded = true;
         } 
         catch (IOException ignore) 
         {
            // Nothing to do
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
            ClassLoader cl = Main.class.getClassLoader();
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

      return properties;
   }

   /**
    * Generate the basic reports to the output directory
    * @param outputDir Where the reports go
    * @param classloaderStructure The class loader structure
    * @param archives The archives
    * @param gProvides The global provides
    * @param known The known archives
    */
   private void outputReport(String outputDir, 
                             String classloaderStructure,
                             SortedSet<Archive> archives, 
                             SortedMap<String, SortedSet<String>> gProvides, 
                             List<Archive> known)
   {
      SortedSet<Report> dependenciesReports = new TreeSet<Report>();
      SortedSet<Report> generalReports = new TreeSet<Report>();
      SortedSet<Report> archiveReports = new TreeSet<Report>();

      Report dependsOn = new DependsOnReport(archives, known, classloaderStructure);
      dependsOn.generate(outputDir);
      dependenciesReports.add(dependsOn);

      Report dependants = new DependantsReport(archives, classloaderStructure);
      dependants.generate(outputDir);
      dependenciesReports.add(dependants);

      Report transitiveDependsOn = new TransitiveDependsOnReport(archives, known, classloaderStructure);
      transitiveDependsOn.generate(outputDir);
      dependenciesReports.add(transitiveDependsOn);

      Report transitiveDependants = new TransitiveDependantsReport(archives, classloaderStructure);
      transitiveDependants.generate(outputDir);
      dependenciesReports.add(transitiveDependants);

      Report graphviz = new GraphvizReport(archives, known, classloaderStructure);
      graphviz.generate(outputDir);
      dependenciesReports.add(graphviz);

      for (Archive a : archives)
      {
         if (a.getType() == ArchiveTypes.JAR)
         {
            Report jar = new JarReport(a);
            jar.generate(outputDir);
            archiveReports.add(jar);
         }
      }

      Report multipleJars = new MultipleJarsReport(archives, gProvides);
      multipleJars.generate(outputDir);
      generalReports.add(multipleJars);

      Report multipleLocations = new MultipleLocationsReport(archives);
      multipleLocations.generate(outputDir);
      generalReports.add(multipleLocations);

      Report packageMultipleJars = new PackageMultipleJarsReport(archives, gProvides);
      packageMultipleJars.generate(outputDir);
      generalReports.add(packageMultipleJars);

      Report eliminateJars = new EliminateJarsReport(archives);
      eliminateJars.generate(outputDir);
      generalReports.add(eliminateJars);

      Report noVersion = new NoVersionReport(archives);
      noVersion.generate(outputDir);
      generalReports.add(noVersion);

      Report classLocation = new ClassLocationReport(archives, gProvides);
      classLocation.generate(outputDir);
      generalReports.add(classLocation);

      Report osgi = new OSGiReport(archives, known);
      osgi.generate(outputDir);
      generalReports.add(osgi);

      Report sign = new SignReport(archives);
      sign.generate(outputDir);
      generalReports.add(sign);

      Report sealed = new SealedReport(archives);
      sealed.generate(outputDir);
      generalReports.add(sealed);

      Report invalidversion = new InvalidVersionReport(archives);
      invalidversion.generate(outputDir);
      generalReports.add(invalidversion);

      Report blacklisted = new BlackListedReport(archives);
      blacklisted.generate(outputDir);
      generalReports.add(blacklisted);

      Report unusedjar = new UnusedJarReport(archives);
      unusedjar.generate(outputDir);
      generalReports.add(unusedjar);

      Dump.generateIndex(dependenciesReports, generalReports, archiveReports, outputDir);
      Dump.generateCSS(outputDir);
   }

   /**
    * Validate and create the outputDir if needed.
    * @param outputDir Where reports go
    * @return The verified output path for the reports
    * @exception IOException If the output directory cant be created
    */
   private String setupOutputDir(String outputDir) throws IOException
   {
      // Verify ending slash
      outputDir = !outputDir.substring(outputDir.length() - 1)
                  .equals(File.separator) ?
                   outputDir + File.separator : outputDir;
                   
      // Verify output directory exists & create if it does not
      File outputDirFile = new File(outputDir);
      
      if (!outputDirFile.exists())
      {
         if (!outputDirFile.mkdirs())
            throw new IOException("Cannot create directory: " + outputDir);
      }

      return outputDir;
   }

   /**
    * Parse excludes
    * @param s The input string
    * @return The set of excludes
    */
   private Set<String> parseExcludes(String s)
   {
      Set<String> result = new HashSet<String>();

      StringTokenizer st = new StringTokenizer(s, ",");
      while (st.hasMoreTokens())
      {
         String token = st.nextToken().trim();

         if (token.startsWith("**"))
            token = token.substring(2);
         
         if (token.endsWith("**"))
            token = token.substring(0, token.indexOf("**"));
         
         result.add(token);
      }

      return result;
   }

   /**
    * The usage method
    */
   private static void usage() 
   {
      System.out.println("Usage: Tattletale [-exclude=<excludes>] <scan-directory> [output-directory]");
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
            int arg = 0;
            Main main = new Main();

            if (args[arg].startsWith("-exclude="))
            {
               main.setExcludes(args[arg].substring(args[arg].indexOf("=") + 1));
               arg++;
            }

            main.setSource(args[arg]);
            main.setDestination(args.length > arg + 1 ? args[arg + 1] : ".");

            main.execute();
         }
         catch (Exception e)
         {
            System.err.println("Exception: " + e.getMessage());
            e.printStackTrace(System.err);
         }
      }
      else 
      {
         usage();
      }
   }
}
