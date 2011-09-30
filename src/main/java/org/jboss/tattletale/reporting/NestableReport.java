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

import org.jboss.tattletale.core.Archive;
import org.jboss.tattletale.core.ArchiveTypes;
import org.jboss.tattletale.core.Location;
import org.jboss.tattletale.core.NestableArchive;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javassist.bytecode.ClassFile;


/**
 * Report type used when generating an {@link ArchiveReport} for a {@link org.jboss.tattletale.core.NestableArchive}.
 *
 * @author Navin Surtani
 */
public abstract class NestableReport extends ArchiveReport
{
   private NestableArchive nestableArchive;

   /**
    * Constructor
    *
    * @param id                  The report id
    * @param severity            The severity
    * @param nestableArchive     The nestable archive
    */
   public NestableReport(String id, int severity, NestableArchive nestableArchive)
   {
      super(id, severity, nestableArchive);
      this.nestableArchive = nestableArchive;
   }

   @Override
   public void writeHtmlBodyContent(BufferedWriter bw) throws IOException
   {
      bw.write("<table>" + Dump.newLine());

      bw.write("  <tr class=\"rowodd\">" + Dump.newLine());
      bw.write("     <td>Name</td>" + Dump.newLine());
      bw.write("     <td>" + nestableArchive.getName() + "</td>" + Dump.newLine());
      bw.write("  </tr>" + Dump.newLine());

      bw.write("  <tr class=\"roweven\">" + Dump.newLine());
      bw.write("     <td>Class Version</td>" + Dump.newLine());
      bw.write("     <td>");

      if (ClassFile.JAVA_6 == nestableArchive.getVersion())
      {
         bw.write("Java 6");
      }
      else if (ClassFile.JAVA_5 == nestableArchive.getVersion())
      {
         bw.write("Java 5");
      }
      else if (ClassFile.JAVA_4 == nestableArchive.getVersion())
      {
         bw.write("J2SE 1.4");
      }
      else if (ClassFile.JAVA_3 == nestableArchive.getVersion())
      {
         bw.write("J2SE 1.3");
      }
      else if (ClassFile.JAVA_2 == nestableArchive.getVersion())
      {
         bw.write("J2SE 1.2");
      }
      else if (ClassFile.JAVA_1 == nestableArchive.getVersion())
      {
         bw.write("JSE 1.0 / JSE 1.1");
      }

      bw.write("</td>" + Dump.newLine());
      bw.write("  </tr>" + Dump.newLine());

      bw.write("  <tr class=\"rowodd\">" + Dump.newLine());
      bw.write("     <td>Locations</td>" + Dump.newLine());
      bw.write("     <td>");

      bw.write("       <table>" + Dump.newLine());

      for (Location location : nestableArchive.getLocations())
      {

         bw.write("      <tr>" + Dump.newLine());

         bw.write("        <td>" + location.getFilename() + "</td>" + Dump.newLine());
         bw.write("        <td>");
         if (location.getVersion() != null)
         {
            bw.write(location.getVersion());
         }
         else
         {
            bw.write("<i>Not listed</i>");
         }
         bw.write("</td>" + Dump.newLine());

         bw.write("      </tr>" + Dump.newLine());
      }

      bw.write("       </table>" + Dump.newLine());

      bw.write("</td>" + Dump.newLine());
      bw.write("  </tr>" + Dump.newLine());

      bw.write("  <tr class=\"roweven\">" + Dump.newLine());
      bw.write("     <td>Profiles</td>" + Dump.newLine());
      bw.write("     <td>");

      if (nestableArchive.getProfiles() != null)
      {
         Iterator<String> pit = nestableArchive.getProfiles().iterator();
         while (pit.hasNext())
         {
            String p = pit.next();

            bw.write(p);

            if (pit.hasNext())
            {
               bw.write("<br>");
            }
         }
      }

      bw.write("  <tr class=\"rowodd\">" + Dump.newLine());
      bw.write("     <td>Manifest</td>" + Dump.newLine());
      bw.write("     <td>");

      if (nestableArchive.getManifest() != null)
      {
         Iterator<String> mit = nestableArchive.getManifest().iterator();
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

      bw.write("</td>" + Dump.newLine());
      bw.write("  </tr>" + Dump.newLine());

      bw.write("  <tr class=\"roweven\">" + Dump.newLine());
      bw.write("     <td>Signing information</td>" + Dump.newLine());
      bw.write("     <td>");

      if (nestableArchive.getSign() != null)
      {
         Iterator<String> sit = nestableArchive.getSign().iterator();
         while (sit.hasNext())
         {
            String s = sit.next();

            bw.write(s);

            if (sit.hasNext())
            {
               bw.write("<br>");
            }
         }
      }

      bw.write("</td>" + Dump.newLine());
      bw.write("  </tr>" + Dump.newLine());

      bw.write("  <tr class=\"rowodd\">" + Dump.newLine());
      bw.write("     <td>Requires</td>" + Dump.newLine());
      bw.write("     <td>");

      Iterator<String> rit = nestableArchive.getRequires().iterator();
      while (rit.hasNext())
      {
         String require = rit.next();

         bw.write(require);

         if (rit.hasNext())
         {
            bw.write("<br>");
         }
      }

      bw.write("</td>" + Dump.newLine());
      bw.write("  </tr>" + Dump.newLine());

      // Table of Provides.
      bw.write("  <tr class=\"roweven\">" + Dump.newLine());
      bw.write("     <td>Provides</td>" + Dump.newLine());
      bw.write("     <td>" + Dump.newLine());

      bw.write("       <table>" + Dump.newLine());

      for (Map.Entry<String, Long> entry : nestableArchive.getProvides().entrySet())
      {

         String name = entry.getKey();
         Long serialVersionUID = entry.getValue();

         bw.write("         <tr>" + Dump.newLine());
         bw.write("           <td>" + name + "</td>" + Dump.newLine());

         if (serialVersionUID != null)
         {
            bw.write("           <td>" + serialVersionUID + "</td>" + Dump.newLine());
         }
         else
         {
            bw.write("           <td>&nbsp;</td>" + Dump.newLine());
         }
         bw.write("         </tr>" + Dump.newLine());
      }
      bw.write("       </table>" + Dump.newLine());

      bw.write("</td>" + Dump.newLine());
      bw.write("  </tr>" + Dump.newLine());

      // Sub-archives
      bw.write("  <tr class=\"rowodd\">" + Dump.newLine());
      bw.write("     <td>Sub-Archives</td>" + Dump.newLine());
      bw.write("     <td>" + Dump.newLine());

      bw.write("        <table>" + Dump.newLine());

      // The base output path for all of the sub archives.
      String outputPath = getOutputDirectory().getPath();

      for (Archive subArchive : nestableArchive.getSubArchives())
      {
         String archiveName = subArchive.getName();
         int finalDot = archiveName.lastIndexOf(".");
         String extension = archiveName.substring(finalDot + 1);

         ArchiveReport report = null;
         int depth = 1;

         if (subArchive.getType() == ArchiveTypes.JAR)
         {

            if (subArchive.getParentArchive() != null && subArchive.getParentArchive().getParentArchive() != null)
            {
               depth = 3;
            }
            else if (subArchive.getParentArchive() != null)
            {
               depth = 2;
            }
            report = new JarReport(subArchive, depth);
         }
         else if (subArchive.getType() == ArchiveTypes.WAR)
         {
            NestableArchive nestedSubArchive = (NestableArchive) subArchive;

            if (subArchive.getParentArchive() != null)
            {
               depth = 2;
            }
            report = new WarReport(nestedSubArchive, 2);
         }

         if (!archiveName.contains("WEB-INF/classes"))
         {
            report.generate(outputPath);
            bw.write("        <tr>" + Dump.newLine());
            bw.write("           <td><a href=\"./" + extension + "/" + archiveName + ".html\">" + archiveName
                  + "</a></td>" + Dump.newLine());
            bw.write("        </tr>" + Dump.newLine());

         }
      }
      bw.write("        </table>" + Dump.newLine());
      bw.write("     </td>" + Dump.newLine());
      bw.write("  </tr>");

      bw.write("</table>" + Dump.newLine());
   }

/**
    * write out the header of the report's content
    *
    * @param bw the writer to use
    * @throws IOException if an error occurs
    */
   public void writeHtmlBodyHeader(BufferedWriter bw) throws IOException
   {
      bw.write("<body>" + Dump.newLine());
      bw.write(Dump.newLine());

      bw.write("<h1>" + nestableArchive.getName() + "</h1>" + Dump.newLine());

      bw.write("<a href=\"../index.html\">Main</a>" + Dump.newLine());
      bw.write("<p>" + Dump.newLine());
   }
}
