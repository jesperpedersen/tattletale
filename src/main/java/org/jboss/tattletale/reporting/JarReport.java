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
import org.jboss.tattletale.core.Location;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javassist.bytecode.ClassFile;

/**
 * JAR report
 * @author Jesper Pedersen <jesper.pedersen@jboss.org>
 * @author <a href="mailto:torben.jaeger@jit-consulting.de">Torben Jaeger</a>
 */
public class JarReport extends ArchiveReport
{
   /** DIRECTORY */
   private static final String DIRECTORY = "jar";
   private String filename;

   /**
    * Constructor
    * @param archive The archive
    */
   public JarReport(Archive archive)
   {
      super(DIRECTORY, ReportSeverity.INFO, archive);

      StringBuffer sb = new StringBuffer(archive.getName());
      setFilename(sb.append(".html").toString());
   }

   /**
    * Get the name of the directory
    * @return The directory
    */
   @Override
   public String getDirectory()
   {
      return DIRECTORY;
   }

   /**
    * returns a Jar report specific writer.
    * Jar reports don't use a index.html but a html per archive.
    * @return the BufferedWriter
    * @throws IOException if an error occurs
    */
   @Override
   BufferedWriter getBufferedWriter() throws IOException
   {
      return getBufferedWriter(getFilename());
   }

   /**
    * write out the report's content
    * @param bw the writer to use
    * @exception IOException if an error occurs
    */
   protected void writeHtmlBodyContent(BufferedWriter bw) throws IOException
   {
      bw.write("<table>" + Dump.newLine());

      bw.write("  <tr class=\"rowodd\">" + Dump.newLine());
      bw.write("     <td>Name</td>" + Dump.newLine());
      bw.write("     <td>" + archive.getName() + "</td>" + Dump.newLine());
      bw.write("  </tr>" + Dump.newLine());

      bw.write("  <tr class=\"roweven\">" + Dump.newLine());
      bw.write("     <td>Class Version</td>" + Dump.newLine());
      bw.write("     <td>");

      if (ClassFile.JAVA_6 == archive.getVersion())
      {
         bw.write("Java 6");
      }
      else if (ClassFile.JAVA_5 == archive.getVersion())
      {
         bw.write("Java 5");
      }
      else if (ClassFile.JAVA_4 == archive.getVersion())
      {
         bw.write("J2SE 1.4");
      }
      else if (ClassFile.JAVA_3 == archive.getVersion())
      {
         bw.write("J2SE 1.3");
      }
      else if (ClassFile.JAVA_2 == archive.getVersion())
      {
         bw.write("J2SE 1.2");
      }
      else if (ClassFile.JAVA_1 == archive.getVersion())
      {
         bw.write("JSE 1.0 / JSE 1.1");
      }

      bw.write("</td>" + Dump.newLine());
      bw.write("  </tr>" + Dump.newLine());

      bw.write("  <tr class=\"rowodd\">" + Dump.newLine());
      bw.write("     <td>Locations</td>" + Dump.newLine());
      bw.write("     <td>");

      bw.write("       <table>" + Dump.newLine());

      for (Location location : archive.getLocations())
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

      if (archive.getProfiles() != null)
      {
         Iterator<String> pit = archive.getProfiles().iterator();
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

      bw.write("</td>" + Dump.newLine());
      bw.write("  </tr>" + Dump.newLine());

      bw.write("  <tr class=\"roweven\">" + Dump.newLine());
      bw.write("     <td>Signing information</td>" + Dump.newLine());
      bw.write("     <td>");

      if (archive.getSign() != null)
      {
         Iterator<String> sit = archive.getSign().iterator();
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

      bw.write("</td>" + Dump.newLine());
      bw.write("  </tr>" + Dump.newLine());

      bw.write("  <tr class=\"roweven\">" + Dump.newLine());
      bw.write("     <td>Provides</td>" + Dump.newLine());
      bw.write("     <td>");

      bw.write("       <table>");

      for (Map.Entry<String, Long> entry : archive.getProvides().entrySet())
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
      bw.write("       </table>");

      bw.write("</td>" + Dump.newLine());
      bw.write("  </tr>" + Dump.newLine());

      bw.write("</table>" + Dump.newLine());
   }

   /**
    * write out the header of the report's content
    * @param bw the writer to use
    * @throws IOException if an errror occurs
    */
   protected void writeHtmlBodyHeader(BufferedWriter bw) throws IOException
   {
      bw.write("<body>" + Dump.newLine());
      bw.write(Dump.newLine());

      bw.write("<h1>" + archive.getName() + "</h1>" + Dump.newLine());

      bw.write("<a href=\"../index.html\">Main</a>" + Dump.newLine());
      bw.write("<p>" + Dump.newLine());
   }

   private String getFilename()
   {
      return filename;
   }

   private void setFilename(String filename)
   {
      this.filename = filename;
   }
}
