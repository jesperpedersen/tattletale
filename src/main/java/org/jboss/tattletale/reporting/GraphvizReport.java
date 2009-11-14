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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Graphviz report
 * @author Jesper Pedersen <jesper.pedersen@jboss.org>
 * @author <a href="mailto:torben.jaeger@jit-consulting.de">Torben Jaeger</a>
 */
public class GraphvizReport extends CLSReport
{
   /** NAME */
   private static final String NAME = "Graphical dependencies";

   /** DIRECTORY */
   private static final String DIRECTORY = "graphviz";

   /**
    * Constructor
    * @param archives The archives
    * @param known The list of known archives
    * @param classloaderStructure The classloader structure
    */
   public GraphvizReport(SortedSet<Archive> archives,
                         List<Archive> known,
                         String classloaderStructure)
   {
      super(ReportSeverity.INFO, archives, NAME, DIRECTORY, classloaderStructure, known);
   }

   /**
    * write out the report's content
    * @param bw the writer to use
    * @throws IOException if an error occurs
    */
   void writeHtmlBodyContent(BufferedWriter bw) throws IOException
   {
      bw.write("<table>" + Dump.NEW_LINE);

      bw.write("  <tr>" + Dump.NEW_LINE);
      bw.write("     <th>Archive</th>" + Dump.NEW_LINE);
      bw.write("     <th>Archives</th>" + Dump.NEW_LINE);
      bw.write("     <th>Packages</th>" + Dump.NEW_LINE);
      bw.write("  </tr>" + Dump.NEW_LINE);

      boolean odd = true;

      FileWriter alldotfw = new FileWriter(getOutputDir().getAbsolutePath() + File.separator + "dependencies.dot");
      BufferedWriter alldotw = new BufferedWriter(alldotfw, 8192);

      alldotw.write("digraph dependencies {" + Dump.NEW_LINE);
      alldotw.write("  node [shape = box, fontsize=10.0];" + Dump.NEW_LINE);

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

            // Archive level dependencies
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
            }

            if (result.size() == 0)
            {
               bw.write("&nbsp;");
            }
            else
            {
               bw.write("<a href=\"" + archive.getName() + "/" + archive.getName() + ".dot\">.dot</a>");

               File doutput = new File(getOutputDir(), archive.getName());
               doutput.mkdirs();

               FileWriter dotfw = new FileWriter(
                     doutput.getAbsolutePath() + File.separator + archive.getName() + ".dot");
               BufferedWriter dotw = new BufferedWriter(dotfw, 8192);

               dotw.write("digraph " + dotName(archive.getName()) + "_dependencies {" + Dump.NEW_LINE);
               dotw.write("  node [shape = box, fontsize=10.0];" + Dump.NEW_LINE);

               for (String aResult : result)
               {

                  alldotw.write("  " + dotName(archive.getName()) + " -> " + dotName(aResult) + ";" + Dump.NEW_LINE);
                  dotw.write("  " + dotName(archive.getName()) + " -> " + dotName(aResult) + ";" + Dump.NEW_LINE);
               }

               dotw.write("}" + Dump.NEW_LINE);

               dotw.flush();
               dotw.close();
            }

            bw.write("</td>" + Dump.NEW_LINE);

            // Package level dependencies
            bw.write("     <td>");

            if (archive.getPackageDependencies().size() == 0)
            {
               bw.write("&nbsp;");
            }
            else
            {
               bw.write("<a href=\"" + archive.getName() + "/" + archive.getName() + "-package.dot\">.dot</a>");

               File doutput = new File(getOutputDir(), archive.getName());
               doutput.mkdirs();

               FileWriter dotfw = new FileWriter(
                     doutput.getAbsolutePath() + File.separator + archive.getName() + "-package.dot");
               BufferedWriter dotw = new BufferedWriter(dotfw, 8192);

               dotw.write("digraph " + dotName(archive.getName()) + "_package_dependencies {" + Dump.NEW_LINE);
               dotw.write("  node [shape = box, fontsize=10.0];" + Dump.NEW_LINE);

               for (Map.Entry<String, SortedSet<String>> entry : archive.getPackageDependencies()
                     .entrySet())
               {

                  String pkg = dotName(entry.getKey());
                  SortedSet<String> deps = entry.getValue();

                  for (String dep : deps)
                  {
                     dotw.write("  " + pkg + " -> " + dotName(dep) + ";" + Dump.NEW_LINE);
                  }
               }

               dotw.write("}" + Dump.NEW_LINE);

               dotw.flush();
               dotw.close();
            }

            bw.write("</td>" + Dump.NEW_LINE);

            bw.write("  </tr>" + Dump.NEW_LINE);

            odd = !odd;
         }
      }

      alldotw.write("}" + Dump.NEW_LINE);

      alldotw.flush();
      alldotw.close();

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

      bw.write("<a href=\"dependencies.dot\">All dependencies</a>");
      bw.write("<p>" + Dump.NEW_LINE);
   }

   /**
    * The dot name for an archive
    * @param name The name
    * @return The dot name
    */
   private String dotName(String name)
   {
      int idx = name.indexOf(".jar");
      if (idx != -1) name = name.substring(0, idx);

      return name.replace('-', '_').replace('.', '_');
   }
}
