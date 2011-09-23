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
import org.jboss.tattletale.profiles.ExtendedProfile;
import org.jboss.tattletale.profiles.JBossAS7Profile;
import org.jboss.tattletale.profiles.Profile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Report type that makes use of the {@link org.jboss.tattletale.profiles.ExtendedProfile} to find which module
 * identifiers it needs for the scanned archives (eg: .war, .ear)
 *
 * @author Navin Surtani
 */
public class AS7Report extends CLSReport
{
   /** NAME **/
   private static final String NAME = "JBoss AS-7";

   /** DIRECTORY */
   private static final String DIRECTORY = "jboss-as7";

   /** Constructor */
   public AS7Report()
   {
      super(NAME, ReportSeverity.INFO, NAME, DIRECTORY);
   }


   /**
    * Build the header of the html file.
    *
    * @param bw the writer to use
    * @throws IOException - if there is an issue with the html writing
    */
   @Override
   protected void writeHtmlBodyHeader(BufferedWriter bw) throws IOException
   {
      bw.write("<body>" + Dump.newLine());
      bw.write(Dump.newLine());

      bw.write("<h1>" + NAME + "</h1>" + Dump.newLine());

      bw.write("<a href=\"../index.html\">Main</a>" + Dump.newLine());
      bw.write("<p />" + Dump.newLine());
   }

   /**
    * Write the main html content.
    *
    * @param bw the writer to use
    * @throws IOException - if there is an issue with the html writing
    */
   @Override
   protected void writeHtmlBodyContent(BufferedWriter bw) throws IOException
   {
      bw.write("<table>" + Dump.newLine());
      bw.write("  <tr>" + Dump.newLine());
      bw.write("     <th>Archive</th>" + Dump.newLine());
      bw.write("     <th>JBoss Deployment</th>" + Dump.newLine());
      bw.write("  </tr>" + Dump.newLine());

      boolean odd = true;
      for (Archive archive : archives)
      {
         Set<String> provides = getProvides(archive);
         Set<String> requires = getRequires(archive);
         requires.removeAll(provides);
         String archiveName = archive.getName();
         int finalDot = archiveName.lastIndexOf(".");
         String extension = archiveName.substring(finalDot + 1);
         File deploymentXml = buildDeploymentXml(requires, archiveName);
         String path = "./" + archiveName + "/" + deploymentXml.getName();

         if (odd)
         {
            bw.write("  <tr class=\"rowodd\">" + Dump.newLine());
         }
         else
         {
            bw.write("  <tr class=\"roweven\">" + Dump.newLine());
         }
         bw.write("     <td><a href=\"../" + extension + "/" + archiveName + ".html\">" +
               archiveName + "</a></td>" + Dump.newLine());
         bw.write("     <td><a href=\"" + path + "\">jboss-deployment-structure" +
               ".xml</a></td>" + Dump.newLine());
         bw.write("  </tr>" + Dump.newLine());

         odd = !odd;
      }
      bw.write("</table>" + Dump.newLine());
   }

   private Set<String> getProvides(Archive a)
   {
      Set<String> provides = new HashSet<String>();
      if (a instanceof NestableArchive)
      {
         NestableArchive na = (NestableArchive) a;
         List<Archive> subArchives = na.getSubArchives();
         provides.addAll(na.getProvides().keySet());

         for (Archive sa : subArchives)
         {
            provides.addAll(getProvides(sa));
         }
      }
      else
      {
         provides.addAll(a.getProvides().keySet());
      }
      return provides;
   }

   private Set<String> getRequires(Archive a)
   {
      Set<String> requires = new HashSet<String>();
      if (a instanceof NestableArchive)
      {
         NestableArchive na = (NestableArchive) a;
         List<Archive> subArchives = na.getSubArchives();
         requires.addAll(na.getRequires());

         for (Archive sa : subArchives)
         {
            requires.addAll(getRequires(sa));
         }
      }
      else
      {
         requires.addAll(a.getRequires());
      }
      return requires;
   }

   private File buildDeploymentXml(Set<String> requires, String archiveName) throws IOException
   {
      File deployedDir = new File(getOutputDirectory(), archiveName);
      deployedDir.mkdirs();
      File outputXml = new File(deployedDir.getAbsolutePath() + File.separator + "jboss-deployment-structure.xml");
      FileWriter fw = new FileWriter(outputXml);
      BufferedWriter bw = new BufferedWriter(fw, 8192);

      bw.write("<?xml version=\"1.0\"?>" + Dump.newLine());
      bw.write("<jboss-deployment-structure>" + Dump.newLine());
      bw.write("  <deployment>" + Dump.newLine());
      bw.write("     <dependencies>" + Dump.newLine());

      ExtendedProfile as7Profile = new JBossAS7Profile();
      SortedSet<String> moduleIdentifiers = new TreeSet<String>();

      for (String requiredClass : requires)
      {
         String moduleIdentifier = as7Profile.getModuleIdentifier(requiredClass);
         if (moduleIdentifier != null)
         {
            moduleIdentifiers.add(moduleIdentifier);
         }
         else
         {
            for (Profile p : getKnown())
            {
               if (p.doesProvide(requiredClass))
               {
                  moduleIdentifier = p.getModuleIdentifier();
                  if (moduleIdentifier != null)
                  {
                     moduleIdentifiers.add(moduleIdentifier);
                  }
               }
            }
         }
      }

      for (String identifier : moduleIdentifiers)
      {
         bw.write("        <module name=\"" + identifier + "\"/>" + Dump.newLine());
      }
      bw.write("     </dependencies>" + Dump.newLine());
      bw.write("  </deployment>" + Dump.newLine());
      bw.write("</jboss-deployment-structure>" + Dump.newLine());
      bw.flush();
      bw.close();

      return outputXml;
   }

}
