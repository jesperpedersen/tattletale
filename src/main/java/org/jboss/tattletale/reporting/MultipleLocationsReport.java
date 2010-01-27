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

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.SortedSet;

/**
 * Multiple locations report
 * @author Jesper Pedersen <jesper.pedersen@jboss.org>
 * @author <a href="mailto:torben.jaeger@jit-consulting.de">Torben Jaeger</a>
 */
public class MultipleLocationsReport extends Report
{
   /** NAME */
   private static final String NAME = "Multiple Locations";

   /** DIRECTORY */
   private static final String DIRECTORY = "multiplelocations";

   /**
    * Constructor
    * @param archives The archives
    */
   public MultipleLocationsReport(SortedSet<Archive> archives)
   {
      super(DIRECTORY, ReportSeverity.WARNING, archives, NAME, DIRECTORY);
   }

   /**
    * write out the report's content
    * @param bw the writer to use
    * @exception IOException if an error occurs
    */
   void writeHtmlBodyContent(BufferedWriter bw) throws IOException
   {
      bw.write("<table>" + Dump.NEW_LINE);

      bw.write("  <tr>" + Dump.NEW_LINE);
      bw.write("     <th>Name</th>" + Dump.NEW_LINE);
      bw.write("     <th>Location</th>" + Dump.NEW_LINE);
      bw.write("  </tr>" + Dump.NEW_LINE);

      boolean odd = true;

      for (Archive a : archives)
      {
         if (a.getType() == ArchiveTypes.JAR && a.getLocations().size() > 1)
         {
            boolean filtered = isFiltered(a.getName());

            if (!filtered)
            {
               status = ReportStatus.YELLOW;
            }

            if (odd)
            {
               bw.write("  <tr class=\"rowodd\">" + Dump.NEW_LINE);
            }
            else
            {
               bw.write("  <tr class=\"roweven\">" + Dump.NEW_LINE);
            }
            bw.write("     <td><a href=\"../jar/" + a.getName() + ".html\">" + a.getName() + "</a></td>" +
                     Dump.NEW_LINE);
            if (!filtered)
            {
               bw.write("     <td>");
            }
            else
            {
               bw.write("     <td style=\"text-decoration: line-through;\">");
            }

            Iterator<Location> lit = a.getLocations().iterator();
            while (lit.hasNext())
            {
               Location location = lit.next();
               bw.write(location.getFilename());

               if (lit.hasNext())
               {
                  bw.write("<br>");
               }
            }

            bw.write("</td>" + Dump.NEW_LINE);
            bw.write("  </tr>" + Dump.NEW_LINE);

            odd = !odd;
         }
      }

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

      bw.write("<h1>" + NAME + "</h1>" + Dump.NEW_LINE);

      bw.write("<a href=\"../index.html\">Main</a>" + Dump.NEW_LINE);
      bw.write("<p>" + Dump.NEW_LINE);
   }

   /**
    * Create filter
    * @return The filter
    */
   @Override
   protected Filter createFilter()
   {
      return new KeyFilter();
   }
}
