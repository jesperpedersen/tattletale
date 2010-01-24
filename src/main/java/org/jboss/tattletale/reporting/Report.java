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

import org.jboss.tattletale.Version;
import org.jboss.tattletale.core.Archive;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

/**
 * Represents a report
 * @author Jesper Pedersen <jesper.pedersen@jboss.org>
 * @author <a href="mailto:torben.jaeger@jit-consulting.de">Torben Jaeger</a>
 */
public abstract class Report implements Comparable
{
   /** Report id */
   private String id;

   /** The severity */
   protected int severity;

   /** The status */
   protected int status;

   /** The actions */
   protected SortedSet<Archive> archives;

   /** name of the report */
   private String name = null;

   /** output directory of the report */
   private String directory = null;

   /** output directory */
   private File outputDir;

   /** Filter */
   private String filter;

   /** Filter initialized */
   private boolean filterInitialized;

   /** KeyValue Filters */
   private Map<String, SortedSet<String>> keyValueFilters;

   /** Key Filters */
   private SortedSet<String> keyFilters;

   /** output filename */
   private static final String INDEX_HTML = "index.html";

   /**
    * Constructor
    * @param id The report id
    * @param severity The severity
    * @param archives The archives
    */
   public Report(String id, 
                 int severity,
                 SortedSet<Archive> archives)
   {
      this.id = id;
      this.severity = severity;
      this.archives = archives;
      this.status = ReportStatus.GREEN;
      this.filter = null;
      this.filterInitialized = false;
   }

   /**
    * Constructor
    * @param id The report id
    * @param severity The severity
    * @param archives The archives
    * @param name The name of the report
    * @param directory The name of the output directory
    */
   public Report(String id,
                 int severity,
                 SortedSet<Archive> archives,
                 String name,
                 String directory)
   {
      this(id, severity, archives);
      this.name = name;
      this.directory = directory;
   }

   /**
    * Get the report id
    * @return The value
    */
   public String getId()
   {
      return id;
   }

   /**
    * Get the severity
    * @return The value
    */
   public int getSeverity()
   {
      return severity;
   }

   /**
    * Get the status
    * @return The value
    */
   public int getStatus()
   {
      return status;
   }

   /**
    * Get the name of the directory
    * @return The directory
    */
   public String getDirectory()
   {
      return directory;
   }

   /**
    * Get the name of the report
    * @return The name
    */
   public String getName()
   {
      return name;
   }

   /**
    * Get the filter for the report
    * @return The filter
    */
   public String getFilter()
   {
      return filter;
   }

   /**
    * Set the filter for the report
    * @param filter The value
    */
   public void setFilter(String filter)
   {
      this.filter = filter;

      if (isKeyValueFilter())
      {
         initKeyValueFilters();
      }
      else
      {
         initKeyFilters();
      }

      this.filterInitialized = true;
   }

   /**
    * the output directory
    * @return a file handle to the output directory
    */
   File getOutputDir()
   {
      return outputDir;
   }

   /**
    * Generate the report(s)
    * @param outputDirectory The top-level output directory
    */
   public void generate(String outputDirectory)
   {
      try
      {
         createOutputDir(outputDirectory);
         BufferedWriter bw = getBufferedWriter();

         writeHtmlHead(bw);

         writeHtmlBodyHeader(bw);
         writeHtmlBodyContent(bw);
         writeHtmlBodyFooter(bw);

         writeHtmlFooter(bw);

         bw.flush();
         bw.close();
      }
      catch (Exception e)
      {
         System.err.println(getName() + " Report: " + e.getMessage());
         e.printStackTrace(System.err);
      }
   }

   /**
    * create the output directory
    * @param outputDirectory the name of the directory
    */
   void createOutputDir(String outputDirectory)
   {
      outputDir = new File(outputDirectory, getDirectory());
      outputDir.mkdirs();
   }

   /**
    * get a default writer for writing to an index html file.
    * @return a buffered writer
    * @throws IOException if an error occurs
    */
   BufferedWriter getBufferedWriter() throws IOException
   {
      return getBufferedWriter(INDEX_HTML);
   }

   /**
    * get a writer.
    * @param filename the filename to use
    * @return a buffered writer
    * @throws IOException if an error occurs
    */
   BufferedWriter getBufferedWriter(String filename) throws IOException
   {
      FileWriter fw = new FileWriter(getOutputDir().getAbsolutePath() + File.separator + filename);
      return new BufferedWriter(fw, 8192);
   }

   /**
    * write the header of a html file.
    * @param bw the buffered writer
    * @throws IOException if an error occurs
    */
   void writeHtmlHead(BufferedWriter bw) throws IOException
   {
      bw.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"" +
               "\"http://www.w3.org/TR/html4/loose.dtd\">" + Dump.NEW_LINE);
      bw.write("<html>" + Dump.NEW_LINE);
      bw.write("<head>" + Dump.NEW_LINE);
      bw.write("  <title>" + Version.FULL_VERSION + ": " + getName() + "</title>" + Dump.NEW_LINE);
      bw.write("  <meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\">" + Dump.NEW_LINE);
      bw.write("  <link rel=\"stylesheet\" type=\"text/css\" href=\"../style.css\">" + Dump.NEW_LINE);
      bw.write("</head>" + Dump.NEW_LINE);
   }

   /**
    * write out the header of the report's content
    * @param bw the writer to use
    * @throws IOException if an errror occurs
    */
   abstract void writeHtmlBodyHeader(BufferedWriter bw) throws IOException;

   /**
    * write out the report's content
    * @param bw the writer to use
    * @exception IOException if an error occurs
    */
   abstract void writeHtmlBodyContent(BufferedWriter bw) throws IOException;

   /**
    * write out the footer of the report's content
    * @param bw the writer to use
    * @throws IOException if an errror occurs
    */
   void writeHtmlBodyFooter(BufferedWriter bw) throws IOException
   {
      bw.write(Dump.NEW_LINE);
      bw.write("<p>" + Dump.NEW_LINE);
      bw.write("<hr>" + Dump.NEW_LINE);
      bw.write("Generated by: <a href=\"http://www.jboss.org/projects/tattletale\">" + Version.FULL_VERSION + "</a>" +
               Dump.NEW_LINE);
      bw.write(Dump.NEW_LINE);
      bw.write("</body>" + Dump.NEW_LINE);
   }

   /**
    * write out the footer of the html page.
    * @param bw the writer to use
    * @throws IOException if an errror occurs
    */
   void writeHtmlFooter(BufferedWriter bw) throws IOException
   {
      bw.write("</html>" + Dump.NEW_LINE);
   }

   /**
    * Comparable
    * @param o The other object
    * @return The compareTo value
    */
   public int compareTo(Object o)
   {
      Report r = (Report)o;

      if (severity == r.getSeverity())
      {
         return getName().compareTo(r.getName());
      }
      else if (severity < r.getSeverity())
      {
         return -1;
      }
      else
      {
         return 1;
      }
   }

   /**
    * Equals
    * @param obj The other object
    * @return True if equals; otherwise false
    */
   public boolean equals(Object obj)
   {
      if (obj == null || !(obj instanceof Report))
      {
         return false;
      }

      Report r = (Report)obj;

      return getName().equals(r.getName());
   }

   /**
    * Hash code
    * @return The hash code
    */
   public int hashCode()
   {
      return 7 + 31 * getName().hashCode();
   }

   /**
    * Is filtered
    * @param archive The archive
    * @return True if filtered; otherwise false
    */
   protected boolean isFiltered(String archive)
   {
      return isFiltered(archive, null);
   }

   /**
    * Is filtered
    * @param archive The archive
    * @param query The query
    * @return True if filtered; otherwise false
    */
   protected boolean isFiltered(String archive, String query)
   {
      if (filterInitialized)
      {
         SortedSet<String> ss = null;

         if (isKeyValueFilter())
         {
            ss = keyValueFilters.get(archive);
         }
         else
         {
            ss = keyFilters;

            if (query == null)
               query = archive;
         }

         if (ss != null)
         {
            if (query.endsWith(".class"))
            {
               query = query.substring(0, query.indexOf(".class"));
            }

            if (query.endsWith(".jar"))
            {
               query = query.substring(0, query.indexOf(".jar"));
            }

            if (query.endsWith(".*"))
            {
               query = query.substring(0, query.indexOf(".*"));
            }
            
            query = query.replace('.', '/');

            Iterator<String> it = ss.iterator();
            while (it.hasNext())
            {
               String v = it.next();

               if (query.startsWith(v))
                  return true;
            }
         }
      }

      return false;
   }

   /**
    * Is key/value filters
    * @return True if key/value; false if key
    */
   protected boolean isKeyValueFilter()
   {
      return true;
   }

   /**
    * Init the filters
    */
   private void initKeyValueFilters()
   {
      keyValueFilters = new HashMap<String, SortedSet<String>>();

      if (filter != null)
      {
         StringTokenizer st = new StringTokenizer(filter, ";");
         while (st.hasMoreTokens())
         {
            String token = st.nextToken();

            int equal = token.indexOf("=");

            String key = token.substring(0, equal);
            String values = token.substring(equal + 1);

            SortedSet<String> v = new TreeSet<String>(new SizeComparator());

            StringTokenizer vt = new StringTokenizer(values, ",");
            while (vt.hasMoreTokens())
            {
               String value = vt.nextToken();

               boolean includeAll = false;

               if (value.endsWith(".class"))
               {
                  value = value.substring(0, value.indexOf(".class"));
               }

               if (value.endsWith(".jar"))
               {
                  value = value.substring(0, value.indexOf(".jar"));
               }

               if (value.endsWith(".*"))
               {
                  value = value.substring(0, value.indexOf(".*"));
                  includeAll = true;
               }

               value = value.replace('.', '/');

               if (includeAll)
                  value = value + '/';

               v.add(value);
            }

            keyValueFilters.put(key, v);
         }
      }
   }

   /**
    * Init the key filters
    */
   private void initKeyFilters()
   {
      keyFilters = new TreeSet<String>(new SizeComparator());

      if (filter != null)
      {
         StringTokenizer vt = new StringTokenizer(filter, ",");
         while (vt.hasMoreTokens())
         {
            String value = vt.nextToken();

            boolean includeAll = false;

            if (value.endsWith(".class"))
            {
               value = value.substring(0, value.indexOf(".class"));
            }

            if (value.endsWith(".jar"))
            {
               value = value.substring(0, value.indexOf(".jar"));
            }

            if (value.endsWith(".*"))
            {
               value = value.substring(0, value.indexOf(".*"));
               includeAll = true;
            }

            value = value.replace('.', '/');

            if (includeAll)
               value = value + '/';

            keyFilters.add(value);
         }
      }
   }
}
