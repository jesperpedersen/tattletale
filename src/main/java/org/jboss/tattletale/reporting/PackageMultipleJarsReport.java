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
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Packages in multiple JAR files report
 * @author Jesper Pedersen <jesper.pedersen@jboss.org>
 * @author <a href="mailto:torben.jaeger@jit-consulting.de">Torben Jaeger</a>
 */
public class PackageMultipleJarsReport extends Report
{
   /** NAME */
   private static final String NAME = "Multiple Jar files (Packages)";

   /** DIRECTORY */
   private static final String DIRECTORY = "multiplejarspackage";

   /** Globally provides */
   private SortedMap<String, SortedSet<String>> gProvides;

   /**
    * Constructor
    * @param archives The archives
    * @param gProvides The global provides
    */
   public PackageMultipleJarsReport(SortedSet<Archive> archives,
                                    SortedMap<String, SortedSet<String>> gProvides)
   {
      super(DIRECTORY, ReportSeverity.WARNING, archives, NAME, DIRECTORY);
      this.gProvides = gProvides;
   }

   /**
    * write the report's content
    * @param bw the BufferedWriter to use
    * @throws IOException if an error occurs
    */
   @Override
   void writeHtmlBodyContent(BufferedWriter bw) throws IOException
   {
      bw.write("<table>" + Dump.NEW_LINE);

      bw.write("  <tr>" + Dump.NEW_LINE);
      bw.write("     <th>Package</th>" + Dump.NEW_LINE);
      bw.write("     <th>Jar files</th>" + Dump.NEW_LINE);
      bw.write("  </tr>" + Dump.NEW_LINE);

      SortedMap<String, SortedSet<String>> packageProvides = new TreeMap<String, SortedSet<String>>();

      for (Map.Entry<String, SortedSet<String>> entry : gProvides.entrySet())
      {
         String clz = (String)((Map.Entry)entry).getKey();
         SortedSet archives = (SortedSet)((Map.Entry)entry).getValue();

         String packageName = null;

         if (clz.indexOf('.') == -1)
         {
            packageName = "";
         }
         else
         {
            packageName = clz.substring(0, clz.lastIndexOf('.'));
         }

         SortedSet<String> packageJars = packageProvides.get(packageName);
         if (packageJars == null)
         {
            packageJars = new TreeSet<String>();
         }

         packageJars.addAll(archives);

         packageProvides.put(packageName, packageJars);
      }

      boolean odd = true;

      for (Map.Entry<String, SortedSet<String>> entry : packageProvides.entrySet())
      {
         String pkg = (String)((Map.Entry)entry).getKey();
         SortedSet archives = (SortedSet)((Map.Entry)entry).getValue();

         if (archives.size() > 1)
         {
            if (!isFiltered(pkg))
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
            bw.write("     <td>" + pkg + "</td>" + Dump.NEW_LINE);
            bw.write("     <td>");

            Iterator sit = archives.iterator();
            while (sit.hasNext())
            {
               String archive = (String)sit.next();
               bw.write("<a href=\"../jar/" + archive + ".html\">" + archive + "</a>" + Dump.NEW_LINE);

               if (sit.hasNext())
               {
                  bw.write(", ");
               }
            }

            bw.write("</td>" + Dump.NEW_LINE);
            bw.write("  </tr>" + Dump.NEW_LINE);

            odd = !odd;
         }
      }

      bw.write("</table>" + Dump.NEW_LINE);
   }

   @Override
   void writeHtmlBodyHeader(BufferedWriter bw) throws IOException
   {
      bw.write("<body>" + Dump.NEW_LINE);
      bw.write(Dump.NEW_LINE);

      bw.write("<h1>" + NAME + "</h1>" + Dump.NEW_LINE);

      bw.write("<a href=\"../index.html\">Main</a>" + Dump.NEW_LINE);
      bw.write("<p>" + Dump.NEW_LINE);
   }

   /**
    * Is key/value filters
    * @return True if key/value; false if key
    */
   @Override
   protected boolean isKeyValueFilter()
   {
      return false;
   }
}
