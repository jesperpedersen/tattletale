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
import org.jboss.tattletale.core.ArchiveTypes;
import org.jboss.tattletale.reporting.classloader.ClassLoaderStructure;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Depends On report
 * @author Jesper Pedersen <jesper.pedersen@jboss.org>
 */
public class DependsOnReport extends Report
{
   /** NAME */
   private static final String NAME = "Depends On";

   /** DIRECTORY */
   private static final String DIRECTORY = "dependson";

   /** Known archives */
   private Set<Archive> known;

   /** Class loader structure */
   private String classloaderStructure;

   /**
    * Constructor
    * @param archives The archives
    * @param known The set of known archives
    * @param classloaderStructure The classloader structure
    */
   public DependsOnReport(SortedSet<Archive> archives, Set<Archive> known, String classloaderStructure)
   {
      super(ReportSeverity.INFO, archives);
      this.known = known;
      this.classloaderStructure = classloaderStructure;
   }

   /**
    * Get the name of the report
    * @return The name
    */
   public String getName()
   {
      return NAME;
   }

   /**
    * Get the name of the directory
    * @return The directory
    */
   public String getDirectory()
   {
      return DIRECTORY;
   }

   /**
    * Generate the report(s)
    * @param outputDirectory The top-level output directory
    */
   public void generate(String outputDirectory)
   {
      try
      {
         ClassLoaderStructure cls = null;

         try
         {
            Class c = Thread.currentThread().getContextClassLoader().loadClass(classloaderStructure);
            cls = (ClassLoaderStructure)c.newInstance();
         }
         catch (Exception ntd)
         {
            // Ignore
         }

         File output = new File(outputDirectory, DIRECTORY);
         output.mkdirs();

         FileWriter fw = new FileWriter(output.getAbsolutePath() + File.separator +  "index.html");
         BufferedWriter bw = new BufferedWriter(fw, 8192);
         bw.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"" + 
                  "\"http://www.w3.org/TR/html4/loose.dtd\">" + Dump.NEW_LINE);
         bw.write("<html>" + Dump.NEW_LINE);
         bw.write("<head>" + Dump.NEW_LINE);
         bw.write("  <title>" + Version.FULL_VERSION + ": " + NAME + "</title>" + Dump.NEW_LINE);
         bw.write("  <meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\">" + Dump.NEW_LINE);
         bw.write("  <link rel=\"stylesheet\" type=\"text/css\" href=\"../style.css\">" + Dump.NEW_LINE);
         bw.write("</head>" + Dump.NEW_LINE);
         bw.write("<body>" + Dump.NEW_LINE);
         bw.write(Dump.NEW_LINE);

         bw.write("<h1>" + NAME + "</h1>" + Dump.NEW_LINE);

         bw.write("<a href=\"../index.html\">Main</a>" + Dump.NEW_LINE);
         bw.write("<p>" + Dump.NEW_LINE);

         bw.write("<table>" + Dump.NEW_LINE);
         
         bw.write("  <tr>" + Dump.NEW_LINE);
         bw.write("     <th>Archive</th>" + Dump.NEW_LINE);
         bw.write("     <th>Depends On</th>" + Dump.NEW_LINE);
         bw.write("  </tr>" + Dump.NEW_LINE);

         boolean odd = true;

         Iterator<Archive> it = archives.iterator();
         while (it.hasNext())
         {
            Archive archive = it.next();

            if (archive.getType() == ArchiveTypes.JAR)
            {
               if (odd)
               {
                  bw.write("  <tr class=\"rowodd\">" + Dump.NEW_LINE);
               }
               else
               {
                  bw.write("  <tr class=\"roweven\">" + Dump.NEW_LINE);
               }
               bw.write("     <td><a href=\"../jar/" + archive.getName() + ".html\">" + archive.getName() +
                        "</a></td>" + Dump.NEW_LINE);
               bw.write("     <td>");
               
               SortedSet<String> result = new TreeSet<String>();
               
               Iterator<String> rit = archive.getRequires().iterator();
               while (rit.hasNext())
               {
                  String require = rit.next();

                  boolean found = false;
                  Iterator<Archive> ait = archives.iterator();
                  while (!found && ait.hasNext())
                  {
                     Archive a = ait.next();
                     
                     if (a.doesProvide(require) && (cls == null || cls.isVisible(archive, a)))
                     {
                        result.add(a.getName());
                        found = true;
                     }
                  }

                  if (!found)
                  {
                     Iterator<Archive> kit = known.iterator();
                     while (!found && kit.hasNext())
                     {
                        Archive a = kit.next();

                        if (a.doesProvide(require))
                        {
                           found = true;
                        }
                     }
                  }

                  if (!found)
                  {
                     result.add(require);
                  }
               }

               if (result.size() == 0)
               {
                  bw.write("&nbsp;");
               }
               else
               {
                  Iterator<String> resultIt = result.iterator();
                  while (resultIt.hasNext())
                  {
                     String r = resultIt.next();
                     if (r.endsWith(".jar"))
                     {
                        bw.write("<a href=\"../jar/" + r + ".html\">" + r + "</a>");
                     }
                     else
                     {
                        bw.write("<i>" + r + "</i>");
                        status = ReportStatus.YELLOW;
                     }
               
                     if (resultIt.hasNext())
                     {
                        bw.write(", ");
                     }
                  }
               }
               
               bw.write("</td>" + Dump.NEW_LINE);
               bw.write("  </tr>" + Dump.NEW_LINE);
               
               odd = !odd;
            }
         }

         bw.write("</table>" + Dump.NEW_LINE);

         bw.write(Dump.NEW_LINE);
         bw.write("<p>" + Dump.NEW_LINE);
         bw.write("<hr>" + Dump.NEW_LINE);
         bw.write("Generated by: <a href=\"http://www.jboss.org/projects/tattletale\">" + 
                  Version.FULL_VERSION + "</a>" + Dump.NEW_LINE);
         bw.write(Dump.NEW_LINE);
         bw.write("</body>" + Dump.NEW_LINE);
         bw.write("</html>" + Dump.NEW_LINE);

         bw.flush();
         bw.close();
      }
      catch (Exception e)
      {
         System.err.println("DependsOnReport: " + e.getMessage());
         e.printStackTrace(System.err);
      }
   }
}
