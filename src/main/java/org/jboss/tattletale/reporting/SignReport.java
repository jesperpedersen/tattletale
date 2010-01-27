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

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.SortedSet;

/**
 * Signing information report
 * @author Jesper Pedersen <jesper.pedersen@jboss.org>
 * @author <a href="mailto:torben.jaeger@jit-consulting.de">Torben Jaeger</a>
 */
public class SignReport extends Report
{
   /** NAME */
   private static final String NAME = "Signing information";

   /** DIRECTORY */
   private static final String DIRECTORY = "sign";

   /**
    * Constructor
    * @param archives The archives
    */
   public SignReport(SortedSet<Archive> archives)
   {
      super(DIRECTORY, ReportSeverity.INFO, archives, NAME, DIRECTORY);
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
      bw.write("     <th>Archive</th>" + Dump.NEW_LINE);
      bw.write("     <th>Status</th>" + Dump.NEW_LINE);
      bw.write("  </tr>" + Dump.NEW_LINE);

      boolean odd = true;

      int signed = 0;
      int unsigned = 0;

      for (Archive archive : archives)
      {

         if (odd)
         {
            bw.write("  <tr class=\"rowodd\">" + Dump.NEW_LINE);
         }
         else
         {
            bw.write("  <tr class=\"roweven\">" + Dump.NEW_LINE);
         }
         bw.write("     <td><a href=\"../jar/" + archive.getName() + ".html\">" + archive.getName() + "</a></td>" +
                  Dump.NEW_LINE);
         if (archive.getSign() != null)
         {
            bw.write("     <td style=\"color: red;\">Signed</td>" + Dump.NEW_LINE);
            signed++;
         }
         else
         {
            bw.write("     <td style=\"color: green;\">Unsigned</td>" + Dump.NEW_LINE);
            unsigned++;
         }
         bw.write("  </tr>" + Dump.NEW_LINE);

         odd = !odd;
      }

      bw.write("</table>" + Dump.NEW_LINE);

      boolean filtered = isFiltered();
      if (signed > 0 && unsigned > 0 && !filtered)
         status = ReportStatus.YELLOW;

      bw.write(Dump.NEW_LINE);
      bw.write("<p>" + Dump.NEW_LINE);

      bw.write("<table>" + Dump.NEW_LINE);

      bw.write("  <tr>" + Dump.NEW_LINE);
      bw.write("     <th>Status</th>" + Dump.NEW_LINE);
      bw.write("     <th>Archives</th>" + Dump.NEW_LINE);
      bw.write("  </tr>" + Dump.NEW_LINE);

      bw.write("  <tr class=\"rowodd\">" + Dump.NEW_LINE);
      bw.write("     <td>Signed</td>" + Dump.NEW_LINE);
      if (!filtered)
      {
         bw.write("     <td style=\"color: red;\">" + signed + "</td>" + Dump.NEW_LINE);
      }
      else
      {
         bw.write("     <td style=\"color: red; text-decoration: line-through;\">" + signed + "</td>" + 
                  Dump.NEW_LINE);
      }
      bw.write("  </tr>" + Dump.NEW_LINE);

      bw.write("  <tr class=\"roweven\">" + Dump.NEW_LINE);
      bw.write("     <td>Unsigned</td>" + Dump.NEW_LINE);
      if (!filtered)
      {
         bw.write("     <td style=\"color: green;\">" + unsigned + "</td>" + Dump.NEW_LINE);
      }
      else
      {
         bw.write("     <td style=\"color: green; text-decoration: line-through;\">" + unsigned + "</td>" +
                  Dump.NEW_LINE);
      }
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
      return new BooleanFilter();
   }
}
