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
import org.jboss.tattletale.core.NestableArchive;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Reporting class that will generate package level
 * reports like how {@link ClassDependsOnReport}
 * does on class level reports.
 * <br />
 * @author Navin Surtani
 */

public class PackageDependsOnReport extends CLSReport
{
   /**NAME*/
   private static final String NAME = "Package Depends On";

   /**DIRECTORY*/
   private static final String DIRECTORY = "packagedependson";

   /**
    * Constructor
    */
   public PackageDependsOnReport()
   {
      super(DIRECTORY, ReportSeverity.INFO, NAME, DIRECTORY);
   }

   /**
    * write out the header of the report's content
    * @param bw the writer to use
    * @throws IOException if an errror occurs
    */
   @Override
   public void writeHtmlBodyHeader(BufferedWriter bw) throws IOException
   {
      bw.write("<body>" + Dump.newLine());
      bw.write(Dump.newLine());

      bw.write("<h1>" + NAME + "</h1>" + Dump.newLine());

      bw.write("<a href=\"../index.html\">Main</a>" + Dump.newLine());
      bw.write("<p>" + Dump.newLine());

   }

   /**
    * write out the report's content
    * @param bw the writer to use
    * @exception IOException if an error occurs
    */
   @Override
   public void writeHtmlBodyContent(BufferedWriter bw) throws IOException
   {
      bw.write("<table>" + Dump.newLine());

      bw.write("  <tr>" + Dump.newLine());
      bw.write("     <th>Package</th>" + Dump.newLine());
      bw.write("     <th>Depends On</th>" + Dump.newLine());
      bw.write("  </tr>" + Dump.newLine());

      SortedMap<String, SortedSet<String>> result = recursivelyBuildResultFromArchive(archives);
      boolean odd = true;

      Iterator<Map.Entry<String, SortedSet<String>>> rit = result.entrySet().iterator();

      while (rit.hasNext())
      {
         Map.Entry<String, SortedSet<String>> entry = rit.next();
         String pack = entry.getKey();
         SortedSet<String> packDeps = entry.getValue();

         if (odd)
         {
            bw.write("  <tr class=\"rowodd\">" + Dump.newLine());
         }
         else
         {
            bw.write("  <tr class=\"roweven\">" + Dump.newLine());
         }
         bw.write("     <td>" + pack + "</a></td>" + Dump.newLine());
         bw.write("     <td>");

         Iterator<String> sit = packDeps.iterator();
         while (sit.hasNext())
         {
            String dep = sit.next();
            bw.write(dep);

            if (sit.hasNext())
               bw.write(", ");
         }

         bw.write("</td>" + Dump.newLine());
         bw.write("  </tr>" + Dump.newLine());

         odd = !odd;
      }

      bw.write("</table>" + Dump.newLine());

   }

   private SortedMap<String, SortedSet<String>> recursivelyBuildResultFromArchive(Collection<Archive> archives)
   {
      SortedMap<String, SortedSet<String>> result = new TreeMap<String, SortedSet<String>>();
      for (Archive archive : archives)
      {
         if (archive instanceof NestableArchive)
         {
            NestableArchive nestableArchive = (NestableArchive) archive;
            SortedMap<String, SortedSet<String>> subResult = recursivelyBuildResultFromArchive(nestableArchive
                  .getSubArchives());
            result.putAll(subResult);
         }
         else
         {
            SortedMap<String, SortedSet<String>> packageDependencies = archive.getPackageDependencies();
            Iterator<Map.Entry<String, SortedSet<String>>> dit = packageDependencies.entrySet().iterator();
            while (dit.hasNext())
            {
               Map.Entry<String, SortedSet<String>> entry = dit.next();
               String pack = entry.getKey();
               SortedSet<String> packDeps = entry.getValue();

               SortedSet<String> newDeps = new TreeSet<String>();

               for (String dep : packDeps)
               {
                  if (!dep.equals(pack))
                     newDeps.add(dep);
               }

               result.put(pack, newDeps);
            }
         }
      }
      return result;
   }
}
