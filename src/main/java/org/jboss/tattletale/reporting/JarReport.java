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
import org.jboss.tattletale.core.Location;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;

/**
 * JAR report
 * @author Jesper Pedersen <jesper.pedersen@jboss.org>
 */
public class JarReport extends ArchiveReport
{
   /** DIRECTORY */
   private static final String DIRECTORY = "jar";

   /**
    * Constructor
    * @param archive The archive
    */
   public JarReport(Archive archive)
   {
      super(ReportSeverity.INFO, archive);
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
         File output = new File(outputDirectory, DIRECTORY);
         output.mkdirs();

         FileWriter fw = new FileWriter(output.getAbsolutePath() + File.separator + archive.getName() + ".html");
         BufferedWriter bw = new BufferedWriter(fw, 8192);
         bw.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">" + Dump.NEW_LINE);
         bw.write("<html>" + Dump.NEW_LINE);
         bw.write("<head>" + Dump.NEW_LINE);
         bw.write("  <title>" + Version.FULL_VERSION + ": " + archive.getName() + "</title>" + Dump.NEW_LINE);
         bw.write("  <meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\">" + Dump.NEW_LINE);
         bw.write("  <link rel=\"stylesheet\" type=\"text/css\" href=\"../style.css\">" + Dump.NEW_LINE);
         bw.write("</head>" + Dump.NEW_LINE);
         bw.write("<body>" + Dump.NEW_LINE);
         bw.write(Dump.NEW_LINE);

         bw.write("<h1>" + archive.getName() + "</h1>" + Dump.NEW_LINE);

         bw.write("<a href=\"../index.html\">Main</a>" + Dump.NEW_LINE);
         bw.write("<p>" + Dump.NEW_LINE);

         bw.write("<table>" + Dump.NEW_LINE);
         
         bw.write("  <tr class=\"rowodd\">" + Dump.NEW_LINE);
         bw.write("     <td>Name</td>" + Dump.NEW_LINE);
         bw.write("     <td>" + archive.getName() + "</td>" + Dump.NEW_LINE);
         bw.write("  </tr>" + Dump.NEW_LINE);

         bw.write("  <tr class=\"roweven\">" + Dump.NEW_LINE);
         bw.write("     <td>Locations</td>" + Dump.NEW_LINE);
         bw.write("     <td>");

         bw.write("       <table>" + Dump.NEW_LINE);

         Iterator<Location> lit = archive.getLocations().iterator();
         while (lit.hasNext())
         {
            Location location = lit.next();

            bw.write("      <tr>" + Dump.NEW_LINE);

            bw.write("        <td>" + location.getFilename() + "</td>" + Dump.NEW_LINE);
            bw.write("        <td>");
            if (location.getVersion() != null)
            {
               bw.write(location.getVersion());
            }
            else
            {
               bw.write("<i>Not listed</i>");
            }
            bw.write("</td>" + Dump.NEW_LINE);
            
            bw.write("      </tr>" + Dump.NEW_LINE);
         }

         bw.write("       </table>" + Dump.NEW_LINE);

         bw.write("</td>" + Dump.NEW_LINE);
         bw.write("  </tr>" + Dump.NEW_LINE);

         bw.write("  <tr class=\"rowodd\">" + Dump.NEW_LINE);
         bw.write("     <td>Manifest</td>" + Dump.NEW_LINE);
         bw.write("     <td>");

         if (archive.getManifest() != null)
         {
            Iterator<String> mit = archive.getManifest().iterator();
            while (mit.hasNext())
            {
               String m = mit.next();

               bw.write(m);

               if (mit.hasNext())
               {
                  bw.write("<br>");
               }
            }
         }

         bw.write("</td>" + Dump.NEW_LINE);
         bw.write("  </tr>" + Dump.NEW_LINE);

         bw.write("  <tr class=\"roweven\">" + Dump.NEW_LINE);
         bw.write("     <td>Requires</td>" + Dump.NEW_LINE);
         bw.write("     <td>");

         Iterator<String> rit = archive.getRequires().iterator();
         while (rit.hasNext())
         {
            String require = rit.next();

            bw.write(require);

            if (rit.hasNext())
            {
               bw.write("<br>");
            }
         }

         bw.write("</td>" + Dump.NEW_LINE);
         bw.write("  </tr>" + Dump.NEW_LINE);

         bw.write("  <tr class=\"rowodd\">" + Dump.NEW_LINE);
         bw.write("     <td>Provides</td>" + Dump.NEW_LINE);
         bw.write("     <td>");

         bw.write("       <table>");

         Iterator pit = archive.getProvides().entrySet().iterator();
         while (pit.hasNext())
         {
            Map.Entry entry = (Map.Entry)pit.next();
            
            String name = (String)entry.getKey();
            Long serialVersionUID = (Long)entry.getValue();

            bw.write("         <tr>" + Dump.NEW_LINE);
            bw.write("           <td>" + name + "</td>" + Dump.NEW_LINE);

            if (serialVersionUID != null)
            {
               bw.write("           <td>" + serialVersionUID + "</td>" + Dump.NEW_LINE);
            }
            else
            {
               bw.write("           <td>&nbsp;</td>" + Dump.NEW_LINE);
            }
            bw.write("         </tr>" + Dump.NEW_LINE);
         }
         bw.write("       </table>");

         bw.write("</td>" + Dump.NEW_LINE);
         bw.write("  </tr>" + Dump.NEW_LINE);

         bw.write("</table>" + Dump.NEW_LINE);

         bw.write(Dump.NEW_LINE);
         bw.write("<p>" + Dump.NEW_LINE);
         bw.write("<hr>" + Dump.NEW_LINE);
         bw.write("Generated by: <a href=\"http://www.jboss.org/projects/tattletale\">" + Version.FULL_VERSION + "</a>" + Dump.NEW_LINE);
         bw.write(Dump.NEW_LINE);
         bw.write("</body>" + Dump.NEW_LINE);
         bw.write("</html>" + Dump.NEW_LINE);

         bw.flush();
         bw.close();
      }
      catch (Exception e)
      {
         System.err.println("JarReport: " + e.getMessage());
         e.printStackTrace(System.err);
      }
   }
}
