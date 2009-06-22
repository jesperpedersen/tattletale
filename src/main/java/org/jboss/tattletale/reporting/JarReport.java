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
      super(ReportSeverity.INFO, archive);

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
   void writeHtmlBodyContent(BufferedWriter bw) throws IOException
   {
      bw.write("<table>" + Dump.NEW_LINE);

      bw.write("  <tr class=\"rowodd\">" + Dump.NEW_LINE);
      bw.write("     <td>Name</td>" + Dump.NEW_LINE);
      bw.write("     <td>" + archive.getName() + "</td>" + Dump.NEW_LINE);
      bw.write("  </tr>" + Dump.NEW_LINE);

      bw.write("  <tr class=\"roweven\">" + Dump.NEW_LINE);
      bw.write("     <td>Locations</td>" + Dump.NEW_LINE);
      bw.write("     <td>");

      bw.write("       <table>" + Dump.NEW_LINE);

      for (Location location : archive.getLocations())
      {

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

      for (Map.Entry<String, Long> entry : archive.getProvides().entrySet())
      {

         String name = entry.getKey();
         Long serialVersionUID = entry.getValue();

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
   }

   /**
    * write out the header of the report's content
    * @param bw the writer to use
    * @throws IOException if an errror occurs
    */
   void writeHtmlBodyHeader(BufferedWriter bw) throws IOException
   {
      bw.write("<body>" + Dump.NEW_LINE);
      bw.write(Dump.NEW_LINE);

      bw.write("<h1>" + archive.getName() + "</h1>" + Dump.NEW_LINE);

      bw.write("<a href=\"../index.html\">Main</a>" + Dump.NEW_LINE);
      bw.write("<p>" + Dump.NEW_LINE);
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
