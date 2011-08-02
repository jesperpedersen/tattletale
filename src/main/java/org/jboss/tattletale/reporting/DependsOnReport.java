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

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Depends On report
 * @author Jesper Pedersen <jesper.pedersen@jboss.org>
 * @author <a href="mailto:torben.jaeger@jit-consulting.de">Torben Jaeger</a>
 */
public class DependsOnReport extends CLSReport
{
   /** NAME */
   private static final String NAME = "Depends On";

   /** DIRECTORY */
   private static final String DIRECTORY = "dependson";


   /**
    * Constructor
    * @param archives The archives
    * @param known The set of known archives
    * @param classloaderStructure The classloader structure
    */
   public DependsOnReport(SortedSet<Archive> archives,
                          List<Archive> known,
                          String classloaderStructure)
   {
      super(DIRECTORY, ReportSeverity.INFO, archives, NAME, DIRECTORY, classloaderStructure, known);
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
      bw.write("     <th>Depends On</th>" + Dump.NEW_LINE);
      bw.write("  </tr>" + Dump.NEW_LINE);

      boolean odd = true;

      for (Archive archive : archives)
      {

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
            bw.write("     <td><a href=\"../jar/" + archive.getName() + ".html\">" + archive.getName() + "</a></td>" +
                     Dump.NEW_LINE);
            bw.write("     <td>");

            SortedSet<String> result = new TreeSet<String>();

            for (String require : archive.getRequires())
            {

               boolean found = false;
               Iterator<Archive> ait = archives.iterator();
               while (!found && ait.hasNext())
               {
                  Archive a = ait.next();

                  if (a.doesProvide(require) && (getCLS() == null || getCLS().isVisible(archive, a)))
                  {
                     result.add(a.getName());
                     found = true;
                  }
               }

               if (!found)
               {
                  Iterator<Archive> kit = getKnown().iterator();
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
                     if (!isFiltered(archive.getName(), r))
                     {
                        bw.write("<i>" + r + "</i>");
                        status = ReportStatus.YELLOW;
                     }
                     else
                     {
                        bw.write("<i style=\"text-decoration: line-through;\">" + r + "</i>");
                     }
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
   }

   /**
    * write out the header of the report's content
    * @param bw the writer to use
    * @throws IOException if an error occurs
    */
   void writeHtmlBodyHeader(BufferedWriter bw) throws IOException
   {
      bw.write("<body>" + Dump.NEW_LINE);
      bw.write(Dump.NEW_LINE);

      bw.write("<h1>" + NAME + "</h1>" + Dump.NEW_LINE);

      bw.write("<a href=\"../index.html\">Main</a>" + Dump.NEW_LINE);
      bw.write("<p>" + Dump.NEW_LINE);

   }


}
