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
import org.jboss.tattletale.core.ClassesArchive;
import org.jboss.tattletale.core.NestableArchive;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Graphviz report
 *
 * @author Jesper Pedersen <jesper.pedersen@jboss.org>
 * @author <a href="mailto:torben.jaeger@jit-consulting.de">Torben Jaeger</a>
 */
public class GraphvizReport extends CLSReport
{
   /** NAME */
   private static final String NAME = "Graphical dependencies";

   /** DIRECTORY */
   private static final String DIRECTORY = "graphviz";

   /** Enable dot */
   private boolean enableDot;

   /** Path to the dot application */
   private String graphvizDot;

   /** Constructor */
   public GraphvizReport()
   {
      super(DIRECTORY, ReportSeverity.INFO, NAME, DIRECTORY);

      this.enableDot = true;
      this.graphvizDot = "dot";
   }

   /**
    * Set the configuration properties to use in generating the report
    *
    * @param config The configuration properties
    */
   public void setConfig(Properties config)
   {
      enableDot = Boolean.valueOf(config.getProperty("enableDot", "true"));
      graphvizDot = config.getProperty("graphvizDot", "dot");
   }


   /**
    * write out the report's content
    *
    * @param bw the writer to use
    * @throws IOException if an error occurs
    */
   public void writeHtmlBodyContent(BufferedWriter bw) throws IOException
   {
      bw.write("<table>" + Dump.newLine());

      bw.write("  <tr>" + Dump.newLine());
      bw.write("     <th>Archive</th>" + Dump.newLine());
      bw.write("     <th>Archives</th>" + Dump.newLine());
      bw.write("     <th>Packages</th>" + Dump.newLine());
      bw.write("  </tr>" + Dump.newLine());


      FileWriter alldotfw = new FileWriter
      (getOutputDirectory().getAbsolutePath() + File.separator + "dependencies.dot");
      BufferedWriter alldotw = new BufferedWriter(alldotfw, 8192);

      alldotw.write("digraph dependencies {" + Dump.newLine());
      alldotw.write("  node [shape = box, fontsize=10.0];" + Dump.newLine());

      boolean odd = true;
      boolean hasDot = testDot();

      for (Archive archive : archives)
      {
         String archiveName = archive.getName();
         int finalDot = archiveName.lastIndexOf(".");
         String extension = archiveName.substring(finalDot + 1);

         if (odd)
         {
            bw.write("  <tr class=\"rowodd\">" + Dump.newLine());
         }
         else
         {
            bw.write("  <tr class=\"roweven\">" + Dump.newLine());
         }
         bw.write("     <td><a href=\"../" + extension + "/" + archiveName +
               ".html\">" + archiveName + "</a></td>" + Dump.newLine());

         // Archive level dependencies
         bw.write("     <td>");

         SortedSet<String> result = new TreeSet<String>();

         for (String require : getRequires(archive))
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
            bw.write("<a href=\"" + archiveName + "/" + archiveName + ".dot\">.dot</a>");
            if (hasDot)
            {
               bw.write("&nbsp;");
               bw.write("<a href=\"" + archiveName + "/" + archiveName + ".png\">.png</a>");
            }

            File doutput = new File(getOutputDirectory(), archiveName);
            doutput.mkdirs();

            String dotName = doutput.getAbsolutePath() + File.separator + archiveName + ".dot";
            String pngName = doutput.getAbsolutePath() + File.separator + archiveName + ".png";

            FileWriter dotfw = new FileWriter(dotName);
            BufferedWriter dotw = new BufferedWriter(dotfw, 8192);

            dotw.write("digraph " + dotName(archiveName) + "_dependencies {" + Dump.newLine());
            dotw.write("  node [shape = box, fontsize=10.0];" + Dump.newLine());

            for (String aResult : result)
            {

               alldotw.write("  " + dotName(archiveName) + " -> " + dotName(aResult) + ";" + Dump.newLine());
               dotw.write("  " + dotName(archiveName) + " -> " + dotName(aResult) + ";" + Dump.newLine());
            }

            dotw.write("}" + Dump.newLine());

            dotw.flush();
            dotw.close();

            if (enableDot && hasDot)
            {
               generatePicture(dotName, pngName, doutput);
            }
         }

         bw.write("</td>" + Dump.newLine());

         // Package level dependencies
         bw.write("     <td>");

         if (archive.getPackageDependencies().size() == 0)
         {
            bw.write("&nbsp;");
         }
         else
         {
            bw.write("<a href=\"" + archiveName + "/" + archiveName + "-package.dot\">.dot</a>");
            if (hasDot)
            {
               bw.write("&nbsp;");
               bw.write("<a href=\"" + archiveName + "/" + archiveName + "-package.png\">.png</a>");
            }

            File doutput = new File(getOutputDirectory(), archiveName);
            doutput.mkdirs();

            String dotName = doutput.getAbsolutePath() + File.separator + archiveName + "-package.dot";
            String pngName = doutput.getAbsolutePath() + File.separator + archiveName + "-package.png";

            FileWriter dotfw = new FileWriter(dotName);
            BufferedWriter dotw = new BufferedWriter(dotfw, 8192);

            dotw.write("digraph " + dotName(archiveName) + "_package_dependencies {" + Dump.newLine());
            dotw.write("  node [shape = box, fontsize=10.0];" + Dump.newLine());

            for (Map.Entry<String, SortedSet<String>> entry : archive.getPackageDependencies().entrySet())
            {

               String pkg = dotName(entry.getKey());
               SortedSet<String> deps = entry.getValue();

               for (String dep : deps)
               {
                  dotw.write("  " + pkg + " -> " + dotName(dep) + ";" + Dump.newLine());
               }
            }

            dotw.write("}" + Dump.newLine());

            dotw.flush();
            dotw.close();

            if (enableDot && hasDot)
            {
               generatePicture(dotName, pngName, doutput);
            }
         }

         bw.write("</td>" + Dump.newLine());

         bw.write("  </tr>" + Dump.newLine());

         odd = !odd;
      }


      alldotw.write("}" + Dump.newLine());

      alldotw.flush();
      alldotw.close();

      bw.write("</table>" + Dump.newLine());
   }

   private SortedSet<String> getRequires(Archive archive)
   {
      SortedSet<String> requires = new TreeSet<String>();

      if (archive instanceof NestableArchive)
      {
         NestableArchive nestableArchive = (NestableArchive) archive;
         List<Archive> subArchives = nestableArchive.getSubArchives();

         for (Archive sa : subArchives)
         {
            requires.addAll(getRequires(sa));
         }
         requires.addAll(nestableArchive.getRequires());
      }
      else if (archive instanceof ClassesArchive)
      {
         // No op
      }
      else
      {
         requires.addAll(archive.getRequires());
      }
      return requires;
   }

   /**
    * write out the header of the report's content
    *
    * @param bw the writer to use
    * @throws IOException if an errror occurs
    */
   public void writeHtmlBodyHeader(BufferedWriter bw) throws IOException
   {
      bw.write("<body>" + Dump.newLine());
      bw.write(Dump.newLine());

      bw.write("<h1>" + NAME + "</h1>" + Dump.newLine());

      bw.write("<a href=\"../index.html\">Main</a>" + Dump.newLine());
      bw.write("<p>" + Dump.newLine());

      bw.write("<a href=\"dependencies.dot\">All dependencies</a>");
      bw.write("<p>" + Dump.newLine());
   }

   /**
    * The dot name for an archive
    *
    * @param name The name
    * @return The dot name
    */
   private String dotName(String name)
   {
      int idx = name.indexOf(".jar");
      if (idx != -1)
      {
         name = name.substring(0, idx);
      }

      return name.replace('-', '_').replace('.', '_');
   }

   /** Test for the dot application */
   private boolean testDot()
   {
      try
      {
         ProcessBuilder pb = new ProcessBuilder();
         pb = pb.command(graphvizDot, "-V");

         Process proc = pb.redirectErrorStream(true).start();

         proc.waitFor();

         if (proc.exitValue() != 0)
         {
            return false;
         }

         return true;
      }
      catch (InterruptedException ie)
      {
         Thread.interrupted();
      }
      catch (IOException ioe)
      {
         // Ignore
      }

      return false;
   }

   /**
    * Generate picture
    *
    * @param dotName   The .dot file name
    * @param pngName   The .png file name
    * @param directory The directory
    */
   private boolean generatePicture(String dotName, String pngName, File directory)
   {
      try
      {
         ProcessBuilder pb = new ProcessBuilder();
         pb = pb.command(graphvizDot, "-Tpng", dotName, "-o", pngName);
         pb = pb.directory(directory);

         Process proc = pb.redirectErrorStream(true).start();

         BufferedReader out = new BufferedReader(new InputStreamReader(proc.getInputStream()));
         BufferedReader err = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
         String l;

         /*
         while ((l = out.readLine()) != null)
         {
            System.err.println(l);
         }

         while ((l = err.readLine()) != null)
         {
            System.err.println(l);
         }
         */

         proc.waitFor();

         if (proc.exitValue() != 0)
         {
            return false;
         }

         return true;
      }
      catch (InterruptedException ie)
      {
         Thread.interrupted();
      }
      catch (IOException ioe)
      {
         System.err.println(ioe.getMessage());
      }

      return false;
   }
}
