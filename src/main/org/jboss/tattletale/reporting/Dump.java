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

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.SortedSet;

/**
 * Dump
 * @author Jesper Pedersen <jesper.pedersen@jboss.org>
 * @author Jay Balunas <jbalunas@jboss.org>
 */
public class Dump
{
   /** New line character */
   protected static final String NEW_LINE = System.getProperty("line.separator");
   
   /**
    * Generate CSS files
    * @param outputDir where the reports go
    */
   public static void generateCSS(String outputDir)
   {
      byte buffer[] = new byte[8192];
      int bytesRead;

      InputStream is = null;
      OutputStream os = null;
      try
      {
         is = Thread.currentThread().getContextClassLoader().getResourceAsStream("style.css");
         os = new FileOutputStream(outputDir + "style.css");
               
         while ((bytesRead = is.read(buffer)) != -1)
         {
            os.write(buffer, 0, bytesRead);
         }

         os.flush();
      }
      catch (Exception e)
      {
         System.err.println("GenerateCSS: " + e.getMessage());
         e.printStackTrace(System.err);
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

         try
         {
            if (os != null)
               os.close();
         }
         catch (IOException ioe)
         {
         }
      }
   }

   /**
    * Generate index.html
    * @param dependenciesReports The dependencies reports
    * @param generalReports The general reports
    * @param archiveReports The archive reports
    * @param outputDir where the reports go
    */
   public static void generateIndex(SortedSet<Report> dependenciesReports, SortedSet<Report> generalReports, SortedSet<Report> archiveReports, String outputDir)
   {
      try
      {
         FileWriter fw = new FileWriter(outputDir + "index.html");
         BufferedWriter bw = new BufferedWriter(fw, 8192);

         bw.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">" + NEW_LINE);
         bw.write("<html>" + NEW_LINE);
         bw.write("<head>" + NEW_LINE);
         bw.write("  <title>" + Version.FULL_VERSION + ": Index</title>" + NEW_LINE);
         bw.write("  <meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\">" + NEW_LINE);
         bw.write("  <link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\">" + NEW_LINE);
         bw.write("</head>" + NEW_LINE);
         bw.write("<body>" + NEW_LINE);
         bw.write(NEW_LINE);

         bw.write("<h1>" + Version.FULL_VERSION + "</h1>" + NEW_LINE);
         
         bw.write("<h2>Dependencies</h2>" + NEW_LINE);
         bw.write("<ul>" + NEW_LINE);

         if (dependenciesReports != null)
         {
            for (Report r : dependenciesReports)
            {
               bw.write("<li><a href=\"" + r.getDirectory() + "/index.html\">" + r.getName() + "</a></li>" + NEW_LINE);
            }
         }

         bw.write("</ul>" + NEW_LINE);

         bw.write("<h2>Reports</h2>" + NEW_LINE);
         bw.write("<ul>" + NEW_LINE);

         if (generalReports != null)
         {
            for (Report r : generalReports)
            {
               bw.write("<li><a href=\"" + r.getDirectory() + "/index.html\">" + r.getName() + "</a></li>" + NEW_LINE);
            }
         }

         bw.write("</ul>" + NEW_LINE);

         bw.write("<h2>Archives</h2>" + NEW_LINE);
         bw.write("<ul>" + NEW_LINE);

         if (archiveReports != null)
         {
            for (Report r : archiveReports)
            {
               bw.write("<li><a href=\"" + r.getDirectory() + "/" + r.getName() + ".html\">" + r.getName() + "</a></li>" + NEW_LINE);
            }
         }

         bw.write("</ul>" + NEW_LINE);

         bw.write(NEW_LINE);
         bw.write("<p>" + NEW_LINE);
         bw.write("<hr>" + NEW_LINE);
         bw.write("Generated by: <a href=\"http://www.jboss.org/projects/tattletale\">" + Version.FULL_VERSION + "</a>" + NEW_LINE);
         bw.write(NEW_LINE);
         bw.write("</body>" + NEW_LINE);
         bw.write("</html>" + NEW_LINE);

         bw.flush();
         bw.close();
      }
      catch (Exception e)
      {
         System.err.println("GenerateIndex: " + e.getMessage());
         e.printStackTrace(System.err);
      }
   }
}
