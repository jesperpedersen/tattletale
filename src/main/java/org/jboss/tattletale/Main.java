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
import java.lang.reflect.Method;
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
 * @author Mike Moore <mike.moore@amentra.com>
 * @author Navin Surtani
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

   /** A List of the Constructors used to create dependency reports */
   private final List<Class> dependencyReports;

   /** A List of the Constructors used to create general reports */
   private final List<Class> generalReports;

   /** A List of the Constructors used to create custom reports */
   private final List<Class> customReports;


   /** Constructor */

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

      this.dependencyReports = new ArrayList<Class>();
      addDependencyReport(ClassDependsOnReport.class);
      addDependencyReport(ClassDependantsReport.class);
      addDependencyReport(DependsOnReport.class);
      addDependencyReport(DependantsReport.class);
      addDependencyReport(TransitiveDependsOnReport.class);
      addDependencyReport(TransitiveDependantsReport.class);
      addDependencyReport(CircularDependencyReport.class);
      addDependencyReport(GraphvizReport.class);

      this.generalReports = new ArrayList<Class>();
      addGeneralReport(MultipleJarsReport.class);
      addGeneralReport(MultipleLocationsReport.class);
      addGeneralReport(PackageMultipleJarsReport.class);
      addGeneralReport(EliminateJarsReport.class);
      addGeneralReport(NoVersionReport.class);
      addGeneralReport(ClassLocationReport.class);
      addGeneralReport(OSGiReport.class);
      addGeneralReport(SignReport.class);
      addGeneralReport(SealedReport.class);
      addGeneralReport(InvalidVersionReport.class);
      addGeneralReport(BlackListedReport.class);
      addGeneralReport(UnusedJarReport.class);

      this.customReports = new ArrayList<Class>();
   }

   /**
    * Set source
    *
    * @param source The value
    */
   public void setSource(String source)
   {
      this.source = source;
   }

   /**
    * Set destination
    *
    * @param destination The value
    */
   public void setDestination(String destination)
   {
      this.destination = destination;
   }

   /**
    * Set configuration
    *
    * @param configuration The value
    */
   public void setConfiguration(String configuration)
   {
      this.configuration = configuration;
   }

   /**
    * Set filter
    *
    * @param filter The value
    */
   public void setFilter(String filter)
   {
      this.filter = filter;
   }

   /**
    * Set class loader structure
    *
    * @param cls The value
    */
   public void setClassLoaderStructure(String cls)
   {
      this.classloaderStructure = cls;
   }

   /**
    * Set profiles
    *
    * @param profiles The value
    */
   public void setProfiles(String profiles)
   {
      this.profiles = profiles;
   }

   /**
    * Set excludes
    *
    * @param excludes The value
    */
   public void setExcludes(String excludes)
   {
      this.excludes = excludes;
   }

   /**
    * Add a dependency report to the list of those to be generated
    *
    * @param clazz The class definition of the dependency report
    */
   public final void addDependencyReport(Class clazz)
   {
      dependencyReports.add(clazz);
   }

   /**
    * Add a report to the list of those to be generated
    *
    * @param clazz The class definition of the report
    */
   public final void addGeneralReport(Class clazz)
   {
      generalReports.add(clazz);
   }

   /**
    * Add a report to the list of those to be generated
    *
    * @param clazz The class definition of the custom report
    */
   public final void addCustomReport(Class clazz)
   {
      customReports.add(clazz);
   }

   /**
    * /**
    * Set blacklisted
    *
    * @param blacklisted The value
    */
   public void setBlacklisted(String blacklisted)
   {
      this.blacklisted = blacklisted;
   }

   /**
    * Set fail on info
    *
    * @param b The value
    */
   public void setFailOnInfo(boolean b)
   {
      this.failOnInfo = b;
   }

   /**
    * Set fail on warn
    *
    * @param b The value
    */
   public void setFailOnWarn(boolean b)
   {
      this.failOnWarn = b;
   }

   /**
    * Set fail on error
    *
    * @param b The value
    */
   public void setFailOnError(boolean b)
   {
      this.failOnError = b;
   }

   /**
    * Set the reports
    *
    * @param reports The value
    */
   public void setReports(String reports)
   {
      this.reports = reports;
   }

   /**
    * Set the scan
    *
    * @param scan The value
    */
   public void setScan(String scan)
   {
      this.scan = scan;
   }

   /**
    * Execute
    *
    * @throws Exception Thrown if an error occurs
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
      {
         classloaderStructure = config.getProperty("classloader");
      }

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

         StringTokenizer st = new StringTokenizer(config.getProperty("profiles"), ",");
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

         StringTokenizer st = new StringTokenizer(config.getProperty("blacklisted"), ",");
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
      {
         reports = config.getProperty("reports");
      }

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

      if (!allReports && reportSet == null && config.getProperty("reports") != null)
      {
         reportSet = new HashSet<String>();

         StringTokenizer st = new StringTokenizer(config.getProperty("reports"), ",");
         while (st.hasMoreTokens())
         {
            String token = st.nextToken().trim();
            reportSet.add(token);
         }
      }

      if (!allReports && reportSet == null)
      {
         allReports = true;
      }

      if (classloaderStructure == null || classloaderStructure.trim().equals(""))
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

      CommonProfile[] profiles = new CommonProfile[]{new SunJava5(), new SunJava6(), new JavaEE5(), new JavaEE6(),
         new CDI10(), new Seam22(), new Spring25(), new Spring30()};

      for (CommonProfile p : profiles)
      {
         if (p.included(allProfiles, profileSet))
         {
            known.add(p);
         }
      }

      StringTokenizer st = new StringTokenizer(source, File.pathSeparator);

      while (st.hasMoreTokens())
      {
         File f = new File(st.nextToken());

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
         }
      }

      // Write out report
      if (archives != null && archives.size() > 0)
      {
         ReportSetBuilder reportSetBuilder = new ReportSetBuilder(destination, allReports, reportSet, filters);

         reportSetBuilder.addReportParameter("setCLS", classloaderStructure);
         reportSetBuilder.addReportParameter("setKnown", known);
         reportSetBuilder.addReportParameter("setArchives", archives);
         reportSetBuilder.addReportParameter("setConfig", config);
         reportSetBuilder.addReportParameter("setGlobalProvides", gProvides);

         loadCustomReports(config);
         outputReport(reportSetBuilder, archives);
      }
   }

   /**
    * Load configuration
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
                  //No op
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
    * Method that loads the custom reports based on the configuration in the
    * jboss-tattletale.properties file.
    *
    * @param config - the Properties configuration.
    */

   private void loadCustomReports(Properties config)
   {
      FileInputStream inputStream = null;
      try
      {
         int index = 1;
         String keyString = "customreport." + index;

         while (config.getProperty(keyString) != null)
         {
            ClassLoader cl = Main.class.getClassLoader();
            String reportName = config.getProperty(keyString);
            Class customReportClass = Class.forName(reportName, true, cl);
            addCustomReport(customReportClass);
            index++;
            keyString = "customreport." + index;
         }
      }
      catch (Exception e)
      {
         System.err.println("Exception of type: " + e.getClass().toString()
               + " thrown in loadCustomReports() in org.jboss.tattletale.Main");
      }
      finally
      {
         if (inputStream != null)
         {
            try
            {
               inputStream.close();
            }
            catch (IOException e)
            {
               // No op.
            }
         }

      }

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
      String propertiesFile = System.getProperty("jboss-tattletale-filter.properties");
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
    * @param reportSetBuilder Defines the output directory and which
    *                         reports to build
    * @param archives         The archives
    *
    * @throws Exception In case of fail on settings
    */
   private void outputReport(ReportSetBuilder reportSetBuilder, SortedSet<Archive> archives) throws Exception
   {
      reportSetBuilder.clear();
      for (Class reportDef : dependencyReports)
      {
         reportSetBuilder.addReport(reportDef);
      }
      SortedSet<Report> dependencyReportSet = reportSetBuilder.getReportSet();

      reportSetBuilder.clear();
      for (Class reportDef : generalReports)
      {
         reportSetBuilder.addReport(reportDef);
      }
      SortedSet<Report> generalReportSet = reportSetBuilder.getReportSet();

      reportSetBuilder.clear();
      for (Class reportDef : customReports)
      {
         reportSetBuilder.addReport(reportDef);
      }
      SortedSet<Report> customReportSet = reportSetBuilder.getReportSet();
      reportSetBuilder.clear();

      for (Archive a : archives)
      {
         if (a.getType() == ArchiveTypes.JAR)
         {
            reportSetBuilder.addReport(new JarReport(a));
         }
      }

      SortedSet<Report> archiveReports = reportSetBuilder.getReportSet();


      String outputDir = reportSetBuilder.getOutputDir();
      Dump.generateIndex(dependencyReportSet, generalReportSet, archiveReports, customReportSet, outputDir);
      Dump.generateCSS(outputDir);

      if (failOnInfo || failOnWarn || failOnError)
      {
         FailureCheck failureCheck = new FailureCheck();
         failureCheck.processReports(dependencyReportSet);
         failureCheck.processReports(generalReportSet);
         failureCheck.processReports(customReportSet);
         failureCheck.processReports(archiveReports);

         if (failureCheck.errorReport() != null)
         {
            throw new Exception(failureCheck.errorReport());
         }
      }
   }

   /**
    * Parse excludes
    *
    * @param s The input string
    *
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
         {
            token = token.substring(2);
         }

         if (token.endsWith("**"))
         {
            token = token.substring(0, token.indexOf("**"));
         }

         result.add(token);
      }

      return result;
   }

   /** The usage method */
   private static void usage()
   {
      System.out.println("Usage: Tattletale [-exclude=<excludes>]" + " <scan-directory> [output-directory]");
   }

   /**
    * The main method
    *
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

   /**
    * This helper class checks reports to determine whether they should fail,
    * according to the rules set.
    *
    * @author MikeMoore
    */
   private class FailureCheck
   {

      private boolean foundError = false;
      private boolean first = true;
      private StringBuilder stringbuffer = new StringBuilder();

      /**
       * @return the error report as a String if errors were found, null
       *         otherwise
       */
      String errorReport()
      {
         if (foundError)
         {
            return stringbuffer.toString();
         }
         return null;
      }

      /**
       * Checks a set of reports for failure conditions
       *
       * @param reports The reports to check
       */
      void processReports(Set<Report> reports)
      {
         for (Report report : reports)
         {
            processReport(report);
         }
      }

      /**
       * Checks a single report for failure conditions
       *
       * @param report The report to check
       */
      void processReport(Report report)
      {
         if ((ReportStatus.YELLOW == report.getStatus() || ReportStatus.RED == report.getStatus())
               && ((ReportSeverity.INFO == report.getSeverity() && failOnInfo) ||
                  (ReportSeverity.WARNING == report.getSeverity() && failOnWarn) ||
                  (ReportSeverity.ERROR == report.getSeverity() && failOnError)))
         {
            appendReportInfo(report);
         }

      }

      /**
       * Record a report failure for the error report
       *
       * @param report A report that meets the failure conditions
       */
      void appendReportInfo(Report report)
      {
         if (!first)
         {
            stringbuffer = stringbuffer.append(System.getProperty("line.separator"));
         }

         stringbuffer = stringbuffer.append(report.getId());
         stringbuffer = stringbuffer.append("=");

         if (ReportStatus.YELLOW == report.getStatus())
         {
            stringbuffer = stringbuffer.append("YELLOW");
         }
         else if (ReportStatus.RED == report.getStatus())
         {
            stringbuffer = stringbuffer.append("RED");
         }

         foundError = true;
         first = false;
      }

   }

   /**
    * This helper class generates reports from report definitions and gathers
    * report definitions into a SortedSet which can be used to build the index.
    *
    * @author MikeMoore
    */
   private class ReportSetBuilder
   {

      private final boolean allReports;
      private final String outputDir;
      private final Properties filters;
      private Set<String> reportSet;
      private SortedSet<Report> returnReportSet = new TreeSet<Report>();
      private final Map<String, Object> reportParameters = new HashMap<String, Object>();

      /**
       * @param destination Where the reports go
       * @param allReports  Should all reports be generated ?
       * @param reportSet   The set of reports that should be generated
       * @param filters     The filters
       *
       * @throws Exception
       */
      ReportSetBuilder(String destination, boolean allReports, Set<String> reportSet, Properties filters)
         throws Exception
      {
         this.outputDir = setupOutputDir(destination);
         this.allReports = allReports;
         this.reportSet = reportSet;
         this.filters = filters;
      }

      /**
       * Add a parameter which will be used to initialize the reports built
       *
       * @param setMethodName The name of the method that will set the parameter on the
       *                      report
       * @param parameter     The parameter to set
       */
      public void addReportParameter(String setMethodName, Object parameter)
      {
         reportParameters.put(setMethodName, parameter);
      }

      /**
       * Starts a new report set. This allows a single ReportSetBuilder to be
       * used to generate multiple report sets
       */
      void clear()
      {
         // start a new set, the old sets are still in use for indexing
         returnReportSet = new TreeSet<Report>();
      }

      /**
       * Generates the report from the definition, output goes to the output
       * directory.
       *
       * @param report the definition of the report to generate
       */
      void addReport(Report report)
      {
         if (allReports || reportSet.contains(report.getId()))
         {
            if (filters != null && filters.getProperty(report.getId()) != null)
            {
               report.setFilter(filters.getProperty(report.getId()));
            }
            report.generate(outputDir);
            returnReportSet.add(report);
         }
      }

      /**
       * Generates the report from the definition, output goes to the output
       * directory.
       *
       * @param reportDef the class definition of the report to generate
       *
       * @throws Exception
       */
      void addReport(Class reportDef) throws Exception
      {
         // build report from empty constructor
         Report report = (Report) reportDef.getConstructor(new Class[0]).newInstance(new Object[0]);

         // populate required report parameters
         Method[] allMethods = reportDef.getMethods();
         for (Method m : allMethods)
         {
            if (reportParameters.containsKey(m.getName()))
            {
               m.invoke(report, reportParameters.get(m.getName()));
            }
         }
         addReport(report);
      }

      /** @return A Set of reports generated, useful for building an index */
      SortedSet<Report> getReportSet()
      {
         return returnReportSet;
      }

      /** @return the String representation of the output directory */
      String getOutputDir()
      {
         return outputDir;
      }

      /**
       * Validate and create the outputDir if needed.
       *
       * @param outputDir Where reports go
       *
       * @return The verified output path for the reports
       *
       * @throws IOException If the output directory cant be created
       */
      private String setupOutputDir(String outputDir) throws IOException
      {
         // Verify ending slash
         outputDir = !outputDir.substring(outputDir.length() - 1).equals(File.separator)
                     ? outputDir + File.separator : outputDir;
         // Verify output directory exists & create if it does not
         File outputDirFile = new File(outputDir);

         if (outputDirFile.exists() && !outputDirFile.equals(new File(".")))
         {
            recursiveDelete(outputDirFile);
         }

         if (!outputDirFile.equals(new File(".")) && !outputDirFile.mkdirs())
         {
            throw new IOException("Cannot create directory: " + outputDir);
         }

         return outputDir;
      }

      /**
       * Recursive delete
       *
       * @param f The file handler
       *
       * @throws IOException Thrown if a file could not be deleted
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
                     {
                        throw new IOException("Could not delete " + files[i]);
                     }
                  }
               }
            }
            if (!f.delete())
            {
               throw new IOException("Could not delete " + f);
            }
         }
      }
   }
}

