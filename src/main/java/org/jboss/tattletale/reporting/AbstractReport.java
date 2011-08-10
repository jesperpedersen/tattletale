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
import java.util.SortedSet;

/**
 * Represents a report
 * @author Jesper Pedersen <jesper.pedersen@jboss.org>
 * @author <a href="mailto:torben.jaeger@jit-consulting.de">Torben Jaeger</a>
 */
public abstract class AbstractReport implements Report
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

   /** Filter implementation */
   private Filter filterImpl;

   /** output filename */
   private static final String INDEX_HTML = "index.html";

   /**
    * Constructor
    * @param id The report id
    * @param severity The severity
    */
   public AbstractReport(String id, int severity)
   {
      this.id = id;
      this.severity = severity;
      this.status = ReportStatus.GREEN;
      this.filter = null;
      this.filterImpl = null;
   }

   /**
    * Constructor
    * @param id The report id
    * @param severity The severity
    * @param name The name of the report
    * @param directory The name of the output directory
    */
   public AbstractReport(String id, int severity, String name, String directory)
   {
      this(id, severity);
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
      this.filterImpl = createFilter();
      this.filterImpl.init(filter);
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
    * Return size of index.html file
    * @return size of index.html file in KB
    */
   String getIndexHtmlSize()
   {
      File indexFile = new File(getOutputDir().getAbsolutePath() + File.separator + INDEX_HTML);
      return ((indexFile.length() / 1024) + 1) + " KB";
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
   public BufferedWriter getBufferedWriter(String filename) throws IOException
   {
      FileWriter fw = new FileWriter(getOutputDir().getAbsolutePath() + File.separator + filename);
      return new BufferedWriter(fw, 8192);
   }

   /**
    * Set the archives to be represented by this report
    * 
    * @param archives
    *           The archives represented by this report
    */
   public void setArchives(SortedSet<Archive> archives)
   {
      this.archives = archives;
   }
   
   /**
    * write the header of a html file.
    * @param bw the buffered writer
    * @throws IOException if an error occurs
    */
   protected void writeHtmlHead(BufferedWriter bw) throws IOException
   {
      bw.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"" +
               "\"http://www.w3.org/TR/html4/loose.dtd\">" + Dump.newLine());
      bw.write("<html>" + Dump.newLine());
      bw.write("<head>" + Dump.newLine());
      bw.write("  <title>" + Version.FULL_VERSION + ": " + getName() + "</title>" + Dump.newLine());
      bw.write("  <meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\">" + Dump.newLine());
      bw.write("  <link rel=\"stylesheet\" type=\"text/css\" href=\"../style.css\">" + Dump.newLine());
      bw.write("</head>" + Dump.newLine());
   }

   /**
    * write out the header of the report's content
    * @param bw the writer to use
    * @throws IOException if an error occurs
    */
   protected abstract void writeHtmlBodyHeader(BufferedWriter bw) throws IOException;

   /**
    * write out the report's content
    * @param bw the writer to use
    * @exception IOException if an error occurs
    */
   protected abstract void writeHtmlBodyContent(BufferedWriter bw) throws IOException;

   /**
    * write out the footer of the report's content
    * @param bw the writer to use
    * @throws IOException if an error occurs
    */
   protected void writeHtmlBodyFooter(BufferedWriter bw) throws IOException
   {
      bw.write(Dump.newLine());
      bw.write("<p>" + Dump.newLine());
      bw.write("<hr>" + Dump.newLine());
      bw.write("Generated by: <a href=\"http://www.jboss.org/projects/tattletale\">" + Version.FULL_VERSION + "</a>" +
               Dump.newLine());
      bw.write(Dump.newLine());
      bw.write("</body>" + Dump.newLine());
   }

   /**
    * write out the footer of the html page.
    * @param bw the writer to use
    * @throws IOException if an errror occurs
    */
   protected void writeHtmlFooter(BufferedWriter bw) throws IOException
   {
      bw.write("</html>" + Dump.newLine());
   }

   /**
    * Comparable
    * @param o The other object
    * @return The compareTo value
    */
   public int compareTo(Object o)
   {
      AbstractReport r = (AbstractReport) o;

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

      AbstractReport r = (AbstractReport)obj;

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
    * Create filter
    * @return The filter
    */
   protected Filter createFilter()
   {
      return new KeyValueFilter();
   }

   /**
    * Is filtered
    * @return True if filtered; otherwise false
    */
   protected boolean isFiltered()
   {
      if (filterImpl != null)
      {
         return filterImpl.isFiltered();
      }

      return false;
   }

   /**
    * Is filtered
    * @param archive The archive
    * @return True if filtered; otherwise false
    */
   protected boolean isFiltered(String archive)
   {
      if (filterImpl != null)
      {
         return filterImpl.isFiltered(archive);
      }

      return false;
   }

   /**
    * Is filtered
    * @param archive The archive
    * @param query The query
    * @return True if filtered; otherwise false
    */
   protected boolean isFiltered(String archive, String query)
   {
      if (filterImpl != null)
      {
         return filterImpl.isFiltered(archive, query);
      }

      return false;
   }
}
