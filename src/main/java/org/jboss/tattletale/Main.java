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
import org.jboss.tattletale.reporting.CircularDependencyReport;
import org.jboss.tattletale.reporting.ClassDependantsReport;
import org.jboss.tattletale.reporting.ClassDependsOnReport;
import org.jboss.tattletale.reporting.ClassLocationReport;
import org.jboss.tattletale.reporting.DependantsReport;
import org.jboss.tattletale.reporting.DependsOnReport;
import org.jboss.tattletale.reporting.Dump;
import org.jboss.tattletale.reporting.EliminateJarsReport;
import org.jboss.tattletale.reporting.GraphvizReport;
import org.jboss.tattletale.reporting.InvalidVersionReport;
import org.jboss.tattletale.reporting.JarReport;
import org.jboss.tattletale.reporting.MultipleJarsReport;
import org.jboss.tattletale.reporting.MultipleLocationsReport;
import org.jboss.tattletale.reporting.NoVersionReport;
import org.jboss.tattletale.reporting.OSGiReport;
import org.jboss.tattletale.reporting.PackageDependantsReport;
import org.jboss.tattletale.reporting.PackageDependsOnReport;
import org.jboss.tattletale.reporting.PackageMultipleJarsReport;
import org.jboss.tattletale.reporting.Report;
import org.jboss.tattletale.reporting.ReportSeverity;
import org.jboss.tattletale.reporting.ReportStatus;
import org.jboss.tattletale.reporting.SealedReport;
import org.jboss.tattletale.reporting.SignReport;
import org.jboss.tattletale.reporting.TransitiveDependantsReport;
import org.jboss.tattletale.reporting.TransitiveDependsOnReport;
import org.jboss.tattletale.reporting.UnusedJarReport;
import org.jboss.tattletale.reporting.profiles.CDI10;
import org.jboss.tattletale.reporting.profiles.CommonProfile;
import org.jboss.tattletale.reporting.profiles.JavaEE5;
import org.jboss.tattletale.reporting.profiles.JavaEE6;
import org.jboss.tattletale.reporting.profiles.Seam22;
import org.jboss.tattletale.reporting.profiles.Spring25;
import org.jboss.tattletale.reporting.profiles.Spring30;
import org.jboss.tattletale.reporting.profiles.SunJava5;
import org.jboss.tattletale.reporting.profiles.SunJava6;

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
 * 
 * @author Jesper Pedersen <jesper.pedersen@jboss.org>
 * @author Jay Balunas <jbalunas@jboss.org>
 */
public class Main
{
   /** Source */
   private String source;

   /** Destination */
   private String destination;

   /** Configuration */
   private String configuration;

   /** Filter */
   private String filter;

   /** Class loader structure */
   private String classloaderStructure;

   /** Profiles */
   private String profiles;

   /** Excludes */
   private String excludes;

   /** Blacklisted */
   private String blacklisted;

   /** Fail on info */
   private boolean failOnInfo;

   /** Fail on warning */
   private boolean failOnWarn;

   /** Fail on error */
   private boolean failOnError;

   /** Reports */
   private String reports;

   /** Scan */
   private String scan;

   /**
    * Constructor
    */
   public Main()
   {
      this.source = ".";
      this.destination = ".";
      this.configuration = null;
      this.filter = null;
      this.classloaderStructure = null;
      this.profiles = null;
      this.excludes = null;
      this.blacklisted = null;
      this.failOnInfo = false;
      this.failOnWarn = false;
      this.failOnError = false;
      this.reports = null;
      this.scan = ".jar";
   }

   /**
    * Set source
    * 
    * @param source
    *           The value
    */
   public void setSource(String source)
   {
      this.source = source;
   }

   /**
    * Set destination
    * 
    * @param destination
    *           The value
    */
   public void setDestination(String destination)
   {
      this.destination = destination;
   }

   /**
    * Set configuration
    * 
    * @param configuration
    *           The value
    */
   public void setConfiguration(String configuration)
   {
      this.configuration = configuration;
   }

   /**
    * Set filter
    * 
    * @param filter
    *           The value
    */
   public void setFilter(String filter)
   {
      this.filter = filter;
   }

   /**
    * Set class loader structure
    * 
    * @param cls
    *           The value
    */
   public void setClassLoaderStructure(String cls)
   {
      this.classloaderStructure = cls;
   }

   /**
    * Set profiles
    * 
    * @param profiles
    *           The value
    */
   public void setProfiles(String profiles)
   {
      this.profiles = profiles;
   }

   /**
    * Set excludes
    * 
    * @param excludes
    *           The value
    */
   public void setExcludes(String excludes)
   {
      this.excludes = excludes;
   }

   /**
    * Set blacklisted
    * 
    * @param blacklisted
    *           The value
    */
   public void setBlacklisted(String blacklisted)
   {
      this.blacklisted = blacklisted;
   }

   /**
    * Set fail on info
    * 
    * @param b
    *           The value
    */
   public void setFailOnInfo(boolean b)
   {
      this.failOnInfo = b;
   }

   /**
    * Set fail on warn
    * 
    * @param b
    *           The value
    */
   public void setFailOnWarn(boolean b)
   {
      this.failOnWarn = b;
   }

   /**
    * Set fail on error
    * 
    * @param b
    *           The value
    */
   public void setFailOnError(boolean b)
   {
      this.failOnError = b;
   }

   /**
    * Set the reports
    * 
    * @param reports
    *           The value
    */
   public void setReports(String reports)
   {
      this.reports = reports;
   }

   /**
    * Set the scan
    * 
    * @param scan
    *           The value
    */
   public void setScan(String scan)
   {
      this.scan = scan;
   }

   /**
    * Execute
    * 
    * @exception Exception
    *               Thrown if an error occurs
    */
   public void execute() throws Exception
   {
      Properties config = null;
      Properties filters = null;

      if (configuration != null)
      {
         config = loadConfiguration();
      }
      else
      {
         config = loadDefaultConfiguration();
      }

      if (filter != null)
      {
         filters = loadFilters();
      }
      else
      {
         filters = loadDefaultFilters();
      }

      Set<String> profileSet = null;
      boolean allProfiles = false;

      Set<String> blacklistedSet = null;

      Set<String> excludeSet = null;

      boolean allReports = false;
      Set<String> reportSet = null;

      if (classloaderStructure == null)
         classloaderStructure = config.getProperty("classloader");

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

      if (profileSet == null && config.getProperty("profiles") != null)
      {
         profileSet = new HashSet<String>();

         StringTokenizer st = new StringTokenizer(config
            .getProperty("profiles"), ",");
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

      if (blacklistedSet == null && config.getProperty("blacklisted") != null)
      {
         blacklistedSet = new HashSet<String>();

         StringTokenizer st = new StringTokenizer(config
            .getProperty("blacklisted"), ",");
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

      if (excludeSet == null && config.getProperty("excludes") != null)
      {
         excludeSet = new HashSet<String>();
         excludeSet.addAll(parseExcludes(config.getProperty("excludes")));
      }

      if (reports == null)
         reports = config.getProperty("reports");

      if (reports == null || (reports != null && reports.trim().equals("*")))
      {
         allReports = true;
      }

      if (!allReports && reports != null)
      {
         reportSet = new HashSet<String>();

         StringTokenizer st = new StringTokenizer(reports, ",");
         while (st.hasMoreTokens())
         {
            String token = st.nextToken().trim();
            reportSet.add(token);
         }
      }

      if (!allReports && reportSet == null
         && config.getProperty("reports") != null)
      {
         reportSet = new HashSet<String>();

         StringTokenizer st = new StringTokenizer(
            config.getProperty("reports"), ",");
         while (st.hasMoreTokens())
         {
            String token = st.nextToken().trim();
            reportSet.add(token);
         }
      }

      if (!allReports && reportSet == null)
         allReports = true;

      if (classloaderStructure == null
         || classloaderStructure.trim().equals(""))
      {
         classloaderStructure = "org.jboss.tattletale.reporting.classloader.NoopClassLoaderStructure";
      }

      if (scan != null)
      {
         DirectoryScanner.setArchives(scan);
      }
      else
      {
         DirectoryScanner.setArchives(".jar");
      }

      Map<String, SortedSet<Location>> locationsMap = new HashMap<String, SortedSet<Location>>();
      SortedSet<Archive> archives = new TreeSet<Archive>();
      SortedMap<String, SortedSet<String>> gProvides = new TreeMap<String, SortedSet<String>>();

      // Load up selected profiles
      List<Archive> known = new ArrayList<Archive>();

      CommonProfile[] profiles = new CommonProfile[] {new SunJava5(),
         new SunJava6(), new JavaEE5(), new JavaEE6(), new CDI10(),
         new Seam22(), new Spring25(), new Spring30() };

      for (CommonProfile p : profiles)
         if (p.included(allProfiles, profileSet))
            known.add(p);

      StringTokenizer st = new StringTokenizer(source, File.pathSeparator);

      while (st.hasMoreTokens())
      {
         File f = new File(st.nextToken());

         if (f.isDirectory())
         {
            List<File> fileList = DirectoryScanner.scan(f, excludeSet);

            for (File file : fileList)
            {
               Archive archive = ArchiveScanner.scan(file, gProvides, known,
                  blacklistedSet);

               if (archive != null)
               {
                  SortedSet<Location> locations = locationsMap.get(archive
                     .getName());
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
         }
      }

      // Write out report
      if (archives != null && archives.size() > 0)
      {
         String outputDir = setupOutputDir(destination);
         outputReport(outputDir, config, allReports, reportSet,
            classloaderStructure, filters, archives, gProvides, known);
      }
   }

   /**
    * Load cpnfiguration
    * 
    * @return The properties
    */
   private Properties loadConfiguration()
   {
      Properties properties = new Properties();

      FileInputStream fis = null;
      try
      {
         fis = new FileInputStream(configuration);
         properties.load(fis);
      }
      catch (IOException e)
      {
         System.err.println("Unable to open " + configuration);
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

      return properties;
   }

   /**
    * Load default configuration values
    * 
    * @return The properties
    */
   private Properties loadDefaultConfiguration()
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
    * Load filters
    * 
    * @return The filters
    */
   private Properties loadFilters()
   {
      Properties properties = new Properties();

      FileInputStream fis = null;
      try
      {
         fis = new FileInputStream(filter);
         properties.load(fis);
      }
      catch (IOException e)
      {
         System.err.println("Unable to open " + filter);
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

      return properties;
   }

   /**
    * Load default filter values
    * 
    * @return The properties
    */
   private Properties loadDefaultFilters()
   {
      Properties properties = new Properties();
      String propertiesFile = System
         .getProperty("jboss-tattletale-filter.properties");
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
            fis = new FileInputStream("jboss-tattletale-filter.properties");
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

      return properties;
   }

   /**
    * Generate the basic reports to the output directory
    * 
    * @param outputDir
    *           Where the reports go
    * @param config
    *           Tattletale runtime properties.
    * @param allReports
    *           Should all reports be generated ?
    * @param reportSet
    *           The set of reports that should be generated
    * @param classloaderStructure
    *           The class loader structure
    * @param filters
    *           The filters
    * @param archives
    *           The archives
    * @param gProvides
    *           The global provides
    * @param known
    *           The known archives
    * @exception Exception
    *               In case of fail on settings
    */
   private void outputReport(String outputDir, Properties config,
      boolean allReports, Set<String> reportSet, String classloaderStructure,
      Properties filters, SortedSet<Archive> archives,
      SortedMap<String, SortedSet<String>> gProvides, List<Archive> known)
      throws Exception
   {
      SortedSet<Report> dependenciesReports = new TreeSet<Report>();
      SortedSet<Report> generalReports = new TreeSet<Report>();
      SortedSet<Report> archiveReports = new TreeSet<Report>();

      Report classDependsOn = new ClassDependsOnReport(archives, known,
         classloaderStructure);
      if (allReports || reportSet.contains(classDependsOn.getId()))
      {
         if (filters != null
            && filters.getProperty(classDependsOn.getId()) != null)
            classDependsOn.setFilter(filters
               .getProperty(classDependsOn.getId()));

         classDependsOn.generate(outputDir);
         dependenciesReports.add(classDependsOn);
      }

      Report classDependants = new ClassDependantsReport(archives, known,
         classloaderStructure);
      if (allReports || reportSet.contains(classDependants.getId()))
      {
         if (filters != null
            && filters.getProperty(classDependants.getId()) != null)
            classDependants.setFilter(filters.getProperty(classDependants
               .getId()));

         classDependants.generate(outputDir);
         dependenciesReports.add(classDependants);
      }

      Report dependsOn = new DependsOnReport(archives, known,
         classloaderStructure);
      if (allReports || reportSet.contains(dependsOn.getId()))
      {
         if (filters != null && filters.getProperty(dependsOn.getId()) != null)
            dependsOn.setFilter(filters.getProperty(dependsOn.getId()));

         dependsOn.generate(outputDir);
         dependenciesReports.add(dependsOn);
      }

      Report dependants = new DependantsReport(archives, classloaderStructure);
      if (allReports || reportSet.contains(dependants.getId()))
      {
         if (filters != null && filters.getProperty(dependants.getId()) != null)
            dependants.setFilter(filters.getProperty(dependants.getId()));

         dependants.generate(outputDir);
         dependenciesReports.add(dependants);
      }

      Report packageDependsOn = new PackageDependsOnReport(archives, known, classloaderStructure);
      if (allReports || reportSet.contains(packageDependsOn.getId()))
      {
         if (filters != null && filters.getProperty(packageDependsOn.getId()) != null)
            packageDependsOn.setFilter(filters.getProperty(packageDependsOn.getId()));

         packageDependsOn.generate(outputDir);
         dependenciesReports.add(packageDependsOn);
      }

      Report packageDependants = new PackageDependantsReport(archives, known, classloaderStructure);
      if (allReports || reportSet.contains((packageDependants.getId())))
      {
         if (filters != null && filters.getProperty(packageDependants.getId()) != null)
            packageDependants.setFilter(filters.getProperty(packageDependants.getId()));

         packageDependants.generate(outputDir);
         dependenciesReports.add(packageDependants);

      }

      Report transitiveDependsOn = new TransitiveDependsOnReport(archives,
         known, classloaderStructure);
      if (allReports || reportSet.contains(transitiveDependsOn.getId()))
      {
         if (filters != null
            && filters.getProperty(transitiveDependsOn.getId()) != null)
            transitiveDependsOn.setFilter(filters
               .getProperty(transitiveDependsOn.getId()));

         transitiveDependsOn.generate(outputDir);
         dependenciesReports.add(transitiveDependsOn);
      }

      Report transitiveDependants = new TransitiveDependantsReport(archives,
         classloaderStructure);
      if (allReports || reportSet.contains(transitiveDependants.getId()))
      {
         if (filters != null
            && filters.getProperty(transitiveDependants.getId()) != null)
            transitiveDependants.setFilter(filters
               .getProperty(transitiveDependants.getId()));

         transitiveDependants.generate(outputDir);
         dependenciesReports.add(transitiveDependants);
      }

      Report circularDependency = new CircularDependencyReport(archives,
         classloaderStructure);
      if (allReports || reportSet.contains(circularDependency.getId()))
      {
         if (filters != null
            && filters.getProperty(circularDependency.getId()) != null)
            circularDependency.setFilter(filters.getProperty(circularDependency
               .getId()));

         circularDependency.generate(outputDir);
         dependenciesReports.add(circularDependency);
      }

      Report graphviz = new GraphvizReport(archives, known,
         classloaderStructure, config);
      if (allReports || reportSet.contains(graphviz.getId()))
      {
         if (filters != null && filters.getProperty(graphviz.getId()) != null)
            graphviz.setFilter(filters.getProperty(graphviz.getId()));

         graphviz.generate(outputDir);
         dependenciesReports.add(graphviz);
      }

      for (Archive a : archives)
      {
         if (a.getType() == ArchiveTypes.JAR)
         {
            Report jar = new JarReport(a);
            if (allReports || reportSet.contains(jar.getId()))
            {
               if (filters != null && filters.getProperty(jar.getId()) != null)
                  jar.setFilter(filters.getProperty(jar.getId()));

               jar.generate(outputDir);
               archiveReports.add(jar);
            }
         }
      }

      Report multipleJars = new MultipleJarsReport(archives, gProvides);
      if (allReports || reportSet.contains(multipleJars.getId()))
      {
         if (filters != null
            && filters.getProperty(multipleJars.getId()) != null)
            multipleJars.setFilter(filters.getProperty(multipleJars.getId()));

         multipleJars.generate(outputDir);
         generalReports.add(multipleJars);
      }

      Report multipleLocations = new MultipleLocationsReport(archives);
      if (allReports || reportSet.contains(multipleLocations.getId()))
      {
         if (filters != null
            && filters.getProperty(multipleLocations.getId()) != null)
            multipleLocations.setFilter(filters.getProperty(multipleLocations
               .getId()));

         multipleLocations.generate(outputDir);
         generalReports.add(multipleLocations);
      }

      Report packageMultipleJars = new PackageMultipleJarsReport(archives,
         gProvides);
      if (allReports || reportSet.contains(packageMultipleJars.getId()))
      {
         if (filters != null
            && filters.getProperty(packageMultipleJars.getId()) != null)
            packageMultipleJars.setFilter(filters
               .getProperty(packageMultipleJars.getId()));

         packageMultipleJars.generate(outputDir);
         generalReports.add(packageMultipleJars);
      }

      Report eliminateJars = new EliminateJarsReport(archives);
      if (allReports || reportSet.contains(eliminateJars.getId()))
      {
         if (filters != null
            && filters.getProperty(eliminateJars.getId()) != null)
            eliminateJars.setFilter(filters.getProperty(eliminateJars.getId()));

         eliminateJars.generate(outputDir);
         generalReports.add(eliminateJars);
      }

      Report noVersion = new NoVersionReport(archives);
      if (allReports || reportSet.contains(noVersion.getId()))
      {
         if (filters != null && filters.getProperty(noVersion.getId()) != null)
            noVersion.setFilter(filters.getProperty(noVersion.getId()));

         noVersion.generate(outputDir);
         generalReports.add(noVersion);
      }

      Report classLocation = new ClassLocationReport(archives, gProvides);
      if (allReports || reportSet.contains(classLocation.getId()))
      {
         if (filters != null
            && filters.getProperty(classLocation.getId()) != null)
            classLocation.setFilter(filters.getProperty(classLocation.getId()));

         classLocation.generate(outputDir);
         generalReports.add(classLocation);
      }

      Report osgi = new OSGiReport(archives, known);
      if (allReports || reportSet.contains(osgi.getId()))
      {
         if (filters != null && filters.getProperty(osgi.getId()) != null)
            osgi.setFilter(filters.getProperty(osgi.getId()));

         osgi.generate(outputDir);
         generalReports.add(osgi);
      }

      Report sign = new SignReport(archives);
      if (allReports || reportSet.contains(sign.getId()))
      {
         if (filters != null && filters.getProperty(sign.getId()) != null)
            sign.setFilter(filters.getProperty(sign.getId()));

         sign.generate(outputDir);
         generalReports.add(sign);
      }

      Report sealed = new SealedReport(archives);
      if (allReports || reportSet.contains(sealed.getId()))
      {
         if (filters != null && filters.getProperty(sealed.getId()) != null)
            sealed.setFilter(filters.getProperty(sealed.getId()));

         sealed.generate(outputDir);
         generalReports.add(sealed);
      }

      Report invalidversion = new InvalidVersionReport(archives);
      if (allReports || reportSet.contains(invalidversion.getId()))
      {
         if (filters != null
            && filters.getProperty(invalidversion.getId()) != null)
            invalidversion.setFilter(filters
               .getProperty(invalidversion.getId()));

         invalidversion.generate(outputDir);
         generalReports.add(invalidversion);
      }

      Report blacklisted = new BlackListedReport(archives);
      if (allReports || reportSet.contains(blacklisted.getId()))
      {
         if (filters != null
            && filters.getProperty(blacklisted.getId()) != null)
            blacklisted.setFilter(filters.getProperty(blacklisted.getId()));

         blacklisted.generate(outputDir);
         generalReports.add(blacklisted);
      }

      Report unusedjar = new UnusedJarReport(archives);
      if (allReports || reportSet.contains(unusedjar.getId()))
      {
         if (filters != null && filters.getProperty(unusedjar.getId()) != null)
            unusedjar.setFilter(filters.getProperty(unusedjar.getId()));

         unusedjar.generate(outputDir);
         generalReports.add(unusedjar);
      }

      Dump.generateIndex(dependenciesReports, generalReports, archiveReports,
         outputDir);
      Dump.generateCSS(outputDir);

      if (failOnInfo || failOnWarn || failOnError)
      {
         boolean foundError = false;

         boolean first = true;
         StringBuilder sb = new StringBuilder();

         for (Report report : dependenciesReports)
         {
            if (ReportStatus.YELLOW == report.getStatus()
               || ReportStatus.RED == report.getStatus())
            {
               if (ReportSeverity.INFO == report.getSeverity() && failOnInfo)
               {
                  if (!first)
                     sb = sb.append(System.getProperty("line.separator"));

                  sb = sb.append(report.getId());
                  sb = sb.append("=");

                  if (ReportStatus.YELLOW == report.getStatus())
                  {
                     sb = sb.append("YELLOW");
                  }
                  else if (ReportStatus.RED == report.getStatus())
                  {
                     sb = sb.append("RED");
                  }

                  foundError = true;
                  first = false;
               }
               else if (ReportSeverity.WARNING == report.getSeverity()
                  && failOnWarn)
               {
                  if (!first)
                     sb = sb.append(System.getProperty("line.separator"));

                  sb = sb.append(report.getId());
                  sb = sb.append("=");

                  if (ReportStatus.YELLOW == report.getStatus())
                  {
                     sb = sb.append("YELLOW");
                  }
                  else if (ReportStatus.RED == report.getStatus())
                  {
                     sb = sb.append("RED");
                  }

                  foundError = true;
                  first = false;
               }
               else if (ReportSeverity.ERROR == report.getSeverity()
                  && failOnError)
               {
                  if (!first)
                     sb = sb.append(System.getProperty("line.separator"));

                  sb = sb.append(report.getId());
                  sb = sb.append("=");

                  if (ReportStatus.YELLOW == report.getStatus())
                  {
                     sb = sb.append("YELLOW");
                  }
                  else if (ReportStatus.RED == report.getStatus())
                  {
                     sb = sb.append("RED");
                  }

                  foundError = true;
                  first = false;
               }
            }
         }

         for (Report report : generalReports)
         {
            if (ReportStatus.YELLOW == report.getStatus()
               || ReportStatus.RED == report.getStatus())
            {
               if (ReportSeverity.INFO == report.getSeverity() && failOnInfo)
               {
                  if (!first)
                     sb = sb.append(System.getProperty("line.separator"));

                  sb = sb.append(report.getId());
                  sb = sb.append("=");

                  if (ReportStatus.YELLOW == report.getStatus())
                  {
                     sb = sb.append("YELLOW");
                  }
                  else if (ReportStatus.RED == report.getStatus())
                  {
                     sb = sb.append("RED");
                  }

                  foundError = true;
                  first = false;
               }
               else if (ReportSeverity.WARNING == report.getSeverity()
                  && failOnWarn)
               {
                  if (!first)
                     sb = sb.append(System.getProperty("line.separator"));

                  sb = sb.append(report.getId());
                  sb = sb.append("=");

                  if (ReportStatus.YELLOW == report.getStatus())
                  {
                     sb = sb.append("YELLOW");
                  }
                  else if (ReportStatus.RED == report.getStatus())
                  {
                     sb = sb.append("RED");
                  }

                  foundError = true;
                  first = false;
               }
               else if (ReportSeverity.ERROR == report.getSeverity()
                  && failOnError)
               {
                  if (!first)
                     sb = sb.append(System.getProperty("line.separator"));

                  sb = sb.append(report.getId());
                  sb = sb.append("=");

                  if (ReportStatus.YELLOW == report.getStatus())
                  {
                     sb = sb.append("YELLOW");
                  }
                  else if (ReportStatus.RED == report.getStatus())
                  {
                     sb = sb.append("RED");
                  }

                  foundError = true;
                  first = false;
               }
            }
         }

         for (Report report : archiveReports)
         {
            if (ReportStatus.YELLOW == report.getStatus()
               || ReportStatus.RED == report.getStatus())
            {
               if (ReportSeverity.INFO == report.getSeverity() && failOnInfo)
               {
                  if (!first)
                     sb = sb.append(System.getProperty("line.separator"));

                  sb = sb.append(report.getId());
                  sb = sb.append("=");

                  if (ReportStatus.YELLOW == report.getStatus())
                  {
                     sb = sb.append("YELLOW");
                  }
                  else if (ReportStatus.RED == report.getStatus())
                  {
                     sb = sb.append("RED");
                  }

                  foundError = true;
                  first = false;
               }
               else if (ReportSeverity.WARNING == report.getSeverity()
                  && failOnWarn)
               {
                  if (!first)
                     sb = sb.append(System.getProperty("line.separator"));

                  sb = sb.append(report.getId());
                  sb = sb.append("=");

                  if (ReportStatus.YELLOW == report.getStatus())
                  {
                     sb = sb.append("YELLOW");
                  }
                  else if (ReportStatus.RED == report.getStatus())
                  {
                     sb = sb.append("RED");
                  }

                  foundError = true;
                  first = false;
               }
               else if (ReportSeverity.ERROR == report.getSeverity()
                  && failOnError)
               {
                  if (!first)
                     sb = sb.append(System.getProperty("line.separator"));

                  sb = sb.append(report.getId());
                  sb = sb.append("=");

                  if (ReportStatus.YELLOW == report.getStatus())
                  {
                     sb = sb.append("YELLOW");
                  }
                  else if (ReportStatus.RED == report.getStatus())
                  {
                     sb = sb.append("RED");
                  }

                  foundError = true;
                  first = false;
               }
            }
         }

         if (foundError)
            throw new Exception(sb.toString());
      }
   }

   /**
    * Validate and create the outputDir if needed.
    * 
    * @param outputDir
    *           Where reports go
    * @return The verified output path for the reports
    * @exception IOException
    *               If the output directory cant be created
    */
   private String setupOutputDir(String outputDir) throws IOException
   {
      // Verify ending slash
      outputDir = !outputDir.substring(outputDir.length() - 1).equals(
         File.separator) ? outputDir + File.separator : outputDir;

      // Verify output directory exists & create if it does not
      File outputDirFile = new File(outputDir);

      if (outputDirFile.exists() && !outputDirFile.equals(new File(".")))
         recursiveDelete(outputDirFile);

      if (!outputDirFile.equals(new File(".")) && !outputDirFile.mkdirs())
         throw new IOException("Cannot create directory: " + outputDir);

      return outputDir;
   }

   /**
    * Recursive delete
    * 
    * @param f
    *           The file handler
    * @exception IOException
    *               Thrown if a file could not be deleted
    */
   private void recursiveDelete(File f) throws IOException
   {
      if (f != null && f.exists())
      {
         File[] files = f.listFiles();
         if (files != null)
         {
            for (int i = 0; i < files.length; i++)
            {
               if (files[i].isDirectory())
               {
                  recursiveDelete(files[i]);
               }
               else
               {
                  if (!files[i].delete())
                     throw new IOException("Could not delete " + files[i]);
               }
            }
         }
         if (!f.delete())
            throw new IOException("Could not delete " + f);
      }
   }

   /**
    * Parse excludes
    * 
    * @param s
    *           The input string
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
      System.out
         .println("Usage: Tattletale [-exclude=<excludes>] <scan-directory> [output-directory]");
   }

   /**
    * The main method
    * 
    * @param args
    *           The arguments
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
               main
                  .setExcludes(args[arg].substring(args[arg].indexOf("=") + 1));
               arg++;
            }

            main.setSource(args[arg]);
            main.setDestination(args.length > arg + 1 ? args[arg + 1] : ".");
            main.setFailOnInfo(false);
            main.setFailOnWarn(false);
            main.setFailOnError(false);

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
