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
import org.jboss.tattletale.core.NestableArchive;

import java.io.BufferedWriter;
import java.io.IOException;


/**
 * This type of report is to .war files as to {@link JarReport} is to .jar files.
 *
 * @author Navin Surtani
 */
public class WarReport extends NestableReport
{
   /** DIRECTORY */
   private static final String DIRECTORY = "war";

   /** File name */
   private String fileName;

   /** The level of depth from the main output directory that this jar report would sit */
   private int depth;

   /**
    * Constructor
    *
    * @param nestableArchive - the war nestableArchive.
    */
   public WarReport(NestableArchive nestableArchive)
   {
      this(nestableArchive, 1);
   }

   /**
    * Constructor
    *
    * @param nestableArchive The nestableArchive
    * @param depth   The level of depth at which this report would lie
    */
   public WarReport(NestableArchive nestableArchive, int depth)
   {
      super (DIRECTORY, ReportSeverity.INFO, nestableArchive);
      StringBuffer sb = new StringBuffer(nestableArchive.getName());
      setFilename(sb.append(".html").toString());
      this.depth = depth;
   }

   /**
    * Get the name of the directory
    *
    * @return The directory
    */
   @Override
   public String getDirectory()
   {
      return DIRECTORY;
   }

   /**
    * write the header of a html file.
    *
    * @param bw the buffered writer
    * @throws IOException if an error occurs
    */

   @Override
   public void writeHtmlHead(BufferedWriter bw) throws IOException
   {
      if (depth == 1)
      {
         super.writeHtmlHead(bw);
      }
      else
      {
         bw.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"" +
                  "\"http://www.w3.org/TR/html4/loose.dtd\">" + Dump.newLine());
         bw.write("<html>" + Dump.newLine());
         bw.write("<head>" + Dump.newLine());
         bw.write("  <title>" + Version.FULL_VERSION + ": " + getName() + "</title>" + Dump.newLine());
         bw.write("  <meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\">" + Dump.newLine());
         bw.write("  <link rel=\"stylesheet\" type=\"text/css\" href=\"");
         for (int i = 1; i <= depth; i++)
         {
            bw.write("../");
         }
         bw.write("style.css\">" + Dump.newLine());
         bw.write("</head>" + Dump.newLine());

      }
   }

   /**
    * returns a war report specific writer.
    * war reports don't use a index.html but a html per archive.
    *
    * @return the BufferedWriter
    * @throws IOException if an error occurs
    */
   @Override
   BufferedWriter getBufferedWriter() throws IOException
   {
      return getBufferedWriter(getFilename());
   }
   private String getFilename()
   {
      return fileName;
   }

   private void setFilename(String fileName)
   {
      this.fileName = fileName;
   }
}
