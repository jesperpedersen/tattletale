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

import org.jboss.tattletale.Version;
import org.jboss.tattletale.core.Archive;
import org.jboss.tattletale.core.Location;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

/**
 * OSGi report
 * @author Jesper Pedersen <jesper.pedersen@jboss.org>
 * @author <a href="mailto:torben.jaeger@jit-consulting.de">Torben Jaeger</a>
 */
public class OSGiReport extends Report
{
   /** NAME */
   private static final String NAME = "OSGi";

   /** DIRECTORY */
   private static final String DIRECTORY = "osgi";

   /**
    * Constructor
    * @param archives The archives
    * @param known The known archives
    */
   public OSGiReport(SortedSet<Archive> archives,
                     List<Archive> known)
   {
      super(DIRECTORY, ReportSeverity.INFO, archives, NAME, DIRECTORY);
   }

   /**
    * Generate the report(s)
    * @param outputDirectory The top-level output directory
    */
   @Override
   public void generate(String outputDirectory)
   {
      super.generate(outputDirectory);

      try
      {
         for (Archive archive : archives)
         {
            List<String> osgiInformation = getOSGIInfo(archive);

            File archiveOutput = writeArchiveOSGIHtml(archive, osgiInformation);

            writeArchiveOSGIManifest(archive, osgiInformation, archiveOutput);
         }

      }
      catch (Exception e)
      {
         System.err.println("OSGiReport: " + e.getMessage());
         e.printStackTrace(System.err);
      }
   }

   private void writeArchiveOSGIManifest(Archive archive,
                                         List<String> osgiInformation,
                                         File archiveOutput) throws IOException
   {
      FileWriter mfw = new FileWriter(archiveOutput.getAbsolutePath() + File.separator + "MANIFEST.MF");
      BufferedWriter mbw = new BufferedWriter(mfw, 8192);

      if (archive.getManifest() != null)
      {
         for (String s : archive.getManifest())
         {
            mbw.write(s);
            mbw.write(Dump.NEW_LINE);
         }
      }

      if (!archive.isOSGi() && osgiInformation != null && osgiInformation.size() > 0)
      {
         mbw.write(Dump.NEW_LINE);
         mbw.write("### OSGi information" + Dump.NEW_LINE);
         for (String anOsgiInformation : osgiInformation)
         {
            if (anOsgiInformation.length() <= 69)
            {
               mbw.write(anOsgiInformation);
               mbw.write(Dump.NEW_LINE);
            }
            else
            {
               int count = 0;
               for (int i = 0; i < anOsgiInformation.length(); i++)
               {
                  char c = anOsgiInformation.charAt(i);
                  if (count <= 69)
                  {
                     mbw.write(c);
                     count++;
                  }
                  else
                  {
                     mbw.write(Dump.NEW_LINE);
                     mbw.write(' ');
                     mbw.write(c);
                     count = 2;
                  }
               }
               mbw.write(Dump.NEW_LINE);
            }
         }
      }

      mbw.flush();
      mbw.close();
   }

   private File writeArchiveOSGIHtml(Archive archive,
                                     List<String> osgiInformation) throws IOException
   {
      File archiveOutput = new File(getOutputDir(), archive.getName());
      archiveOutput.mkdirs();

      FileWriter rfw = new FileWriter(archiveOutput.getAbsolutePath() + File.separator + "index.html");
      BufferedWriter rbw = new BufferedWriter(rfw, 8192);
      rbw.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"" +
                "\"http://www.w3.org/TR/html4/loose.dtd\">" + Dump.NEW_LINE);
      rbw.write("<html>" + Dump.NEW_LINE);
      rbw.write("<head>" + Dump.NEW_LINE);
      rbw.write("  <title>" + Version.FULL_VERSION + ": " + NAME + " - " + archive.getName() + "</title>" +
                Dump.NEW_LINE);
      rbw.write("  <meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\">" + Dump.NEW_LINE);
      rbw.write("  <link rel=\"stylesheet\" type=\"text/css\" href=\"../../style.css\">" + Dump.NEW_LINE);
      rbw.write("</head>" + Dump.NEW_LINE);
      rbw.write("<body>" + Dump.NEW_LINE);
      rbw.write(Dump.NEW_LINE);

      rbw.write("<h1>" + NAME + " - " + archive.getName() + "</h1>" + Dump.NEW_LINE);

      rbw.write("<a href=\"../index.html\">Back</a>" + Dump.NEW_LINE);
      rbw.write("<p>" + Dump.NEW_LINE);

      rbw.write("<table>" + Dump.NEW_LINE);

      rbw.write("  <tr>" + Dump.NEW_LINE);
      rbw.write("     <th>Field</th>" + Dump.NEW_LINE);
      rbw.write("     <th>Value</th>" + Dump.NEW_LINE);
      rbw.write("  </tr>" + Dump.NEW_LINE);

      rbw.write("  <tr class=\"rowodd\">" + Dump.NEW_LINE);
      rbw.write("     <td>OSGi</td>" + Dump.NEW_LINE);
      if (archive.isOSGi())
      {
         rbw.write("     <td style=\"color: green;\">Yes</td>" + Dump.NEW_LINE);
      }
      else
      {
         rbw.write("     <td style=\"color: red;\">No</td>" + Dump.NEW_LINE);
      }
      rbw.write("  </tr>" + Dump.NEW_LINE);

      rbw.write("  <tr class=\"roweven\">" + Dump.NEW_LINE);
      rbw.write("     <td>Manifest</td>" + Dump.NEW_LINE);
      rbw.write("     <td><pre>");

      if (archive.getManifest() != null)
      {
         for (String s : archive.getManifest())
         {
            rbw.write(s);
            rbw.write("<br>");
         }
      }

      rbw.write("</pre></td>" + Dump.NEW_LINE);
      rbw.write("  </tr>" + Dump.NEW_LINE);

      if (!archive.isOSGi())
      {
         rbw.write("  <tr class=\"rowodd\">" + Dump.NEW_LINE);
         rbw.write("     <td>OSGi Manifest</td>" + Dump.NEW_LINE);
         rbw.write("     <td><pre>");

         if (osgiInformation != null && osgiInformation.size() > 0)
         {
            for (String anOsgiInformation : osgiInformation)
            {
               rbw.write(anOsgiInformation);
               rbw.write("<br>");
            }
         }

         rbw.write("</pre></td>" + Dump.NEW_LINE);
         rbw.write("  </tr>" + Dump.NEW_LINE);
      }

      rbw.write("</table>" + Dump.NEW_LINE);

      rbw.write(Dump.NEW_LINE);
      rbw.write("<p>" + Dump.NEW_LINE);
      rbw.write("<hr>" + Dump.NEW_LINE);
      rbw.write("Generated by: <a href=\"http://www.jboss.org/projects/tattletale\">" +
                Version.FULL_VERSION + "</a>" + Dump.NEW_LINE);
      rbw.write(Dump.NEW_LINE);
      rbw.write("</body>" + Dump.NEW_LINE);
      rbw.write("</html>" + Dump.NEW_LINE);

      rbw.flush();
      rbw.close();
      return archiveOutput;
   }

   private List<String> getOSGIInfo(Archive archive)
   {
      List<String> osgiInformation = null;

      if (!archive.isOSGi())
      {
         osgiInformation = new ArrayList<String>();

         SortedMap<String, Integer> exportPackages = new TreeMap<String, Integer>();

         for (String provide : archive.getProvides().keySet())
         {
            if (provide.lastIndexOf(".") != -1)
            {
               String packageName = provide.substring(0, provide.lastIndexOf("."));
               Integer number = exportPackages.get(packageName);
               if (number == null)
                  number = 0;

               number = number + 1;
               exportPackages.put(packageName, number);
            }
         }

         SortedMap<String, String> importPackages = new TreeMap<String, String>();

         for (String require : archive.getRequires())
         {

            if (require.lastIndexOf(".") != -1)
            {
               String packageName = require.substring(0, require.lastIndexOf("."));

               if (importPackages.get(packageName) == null)
               {
                  String version = null;
                  boolean found = false;

                  Iterator<Archive> ait = archives.iterator();
                  while (!found && ait.hasNext())
                  {
                     Archive a = ait.next();

                     if (a.doesProvide(require))
                     {
                        version = a.getLocations().first().getVersion();

                        if (version == null)
                           version = "0.0.0";

                        found = true;
                     }
                  }

                  importPackages.put(packageName, version);
               }
            }
         }

         osgiInformation.add("Bundle-ManifestVersion: 2");

         String bundleSymbolicName;
         if (exportPackages.size() > 0)
         {
            String bsn = null;
            Integer bsnv = null;

            for (Map.Entry<String, Integer> entry : exportPackages.entrySet())
            {
               String pkg = entry.getKey();
               Integer v = entry.getValue();

               if (bsn == null)
               {
                  bsn = pkg;
                  bsnv = v;
               }
               else
               {
                  if (v > bsnv)
                  {
                     bsn = pkg;
                     bsnv = v;
                  }
               }
            }

            bundleSymbolicName = bsn;
         }
         else
         {
            bundleSymbolicName = "UNKNOWN";
         }
         osgiInformation.add("Bundle-SymbolicName: " + bundleSymbolicName);

         String bundleDescription = archive.getName().substring(0, archive.getName().lastIndexOf("."));
         osgiInformation.add("Bundle-Description: " + bundleDescription);

         String bName = archive.getName().substring(0, archive.getName().lastIndexOf("."));
         StringBuffer bundleName = new StringBuffer();
         for (int i = 0; i < bName.length(); i++)
         {
            char c = bName.charAt(i);
            if (c != '\n' && c != '\r' && c != ' ')
            {
               bundleName = bundleName.append(c);
            }
         }
         osgiInformation.add("Bundle-Name: " + bundleName.toString());

         Location location = archive.getLocations().first();
         String bundleVersion = getOSGiVersion(location.getVersion());
         osgiInformation.add("Bundle-Version: " + bundleVersion);

         StringBuffer exportPackage = new StringBuffer();
         Iterator<String> eit = exportPackages.keySet().iterator();
         while (eit.hasNext())
         {
            String ep = eit.next();

            exportPackage = exportPackage.append(ep);

            SortedSet<String> epd = archive.getPackageDependencies().get(ep);
            if (epd != null && epd.size() > 0)
            {
               exportPackage = exportPackage.append(";uses:=\"");

               Iterator<String> epdi = epd.iterator();
               while (epdi.hasNext())
               {
                  exportPackage = exportPackage.append(epdi.next());

                  if (epdi.hasNext())
                     exportPackage = exportPackage.append(",");
               }

               exportPackage = exportPackage.append("\"");
            }

            if (eit.hasNext())
               exportPackage = exportPackage.append(",");
         }
         osgiInformation.add("Export-Package: " + exportPackage.toString());

         StringBuffer importPackage = new StringBuffer();
         Iterator iit = importPackages.entrySet().iterator();
         while (iit.hasNext())
         {
            Map.Entry entry = (Map.Entry)iit.next();

            String pkg = (String)entry.getKey();
            String v = (String)entry.getValue();

            importPackage = importPackage.append(pkg);

            if (v != null)
            {
               importPackage = importPackage.append(";version=\"");
               importPackage = importPackage.append(getOSGiVersion(v));
               importPackage = importPackage.append("\"");
            }

            if (iit.hasNext())
               importPackage = importPackage.append(",");
         }
         osgiInformation.add("Import-Package: " + importPackage.toString());
      }
      return osgiInformation;
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
      bw.write("     <th>OSGi</th>" + Dump.NEW_LINE);
      bw.write("     <th>Report</th>" + Dump.NEW_LINE);
      bw.write("     <th>Manifest</th>" + Dump.NEW_LINE);
      bw.write("  </tr>" + Dump.NEW_LINE);

      boolean odd = true;

      int osgiReady = 0;
      int osgiNotReady = 0;

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
         if (archive.isOSGi())
         {
            bw.write("     <td style=\"color: green;\">Yes</td>" + Dump.NEW_LINE);
            osgiReady++;
         }
         else
         {
            bw.write("     <td style=\"color: red;\">No</td>" + Dump.NEW_LINE);
            osgiNotReady++;
            status = ReportStatus.RED;
         }
         bw.write("     <td><a href=\"" + archive.getName() + "/index.html\">Report</a></td>" + Dump.NEW_LINE);
         bw.write("     <td><a href=\"" + archive.getName() + "/MANIFEST.MF\">Manifest</a></td>" + Dump.NEW_LINE);
         bw.write("  </tr>" + Dump.NEW_LINE);

         odd = !odd;
      }

      bw.write("</table>" + Dump.NEW_LINE);

      bw.write(Dump.NEW_LINE);
      bw.write("<p>" + Dump.NEW_LINE);

      bw.write("<table>" + Dump.NEW_LINE);

      bw.write("  <tr>" + Dump.NEW_LINE);
      bw.write("     <th>Status</th>" + Dump.NEW_LINE);
      bw.write("     <th>Archives</th>" + Dump.NEW_LINE);
      bw.write("  </tr>" + Dump.NEW_LINE);

      bw.write("  <tr class=\"rowodd\">" + Dump.NEW_LINE);
      bw.write("     <td>Ready</td>" + Dump.NEW_LINE);
      bw.write("     <td style=\"color: green;\">" + osgiReady + "</td>" + Dump.NEW_LINE);
      bw.write("  </tr>" + Dump.NEW_LINE);

      bw.write("  <tr class=\"roweven\">" + Dump.NEW_LINE);
      bw.write("     <td>Not ready</td>" + Dump.NEW_LINE);
      bw.write("     <td style=\"color: red;\">" + osgiNotReady + "</td>" + Dump.NEW_LINE);
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
    * Get Bundle-Version
    * @param version The archive version
    * @return OSGi version
    */
   private String getOSGiVersion(String version)
   {
      if (version == null)
         return "0.0.0";

      if (!version.matches("\\d+(\\.\\d+(\\.\\d+(\\.[0-9a-zA-Z\\_\\-]+)?)?)?"))
      {
         return "0.0.0";
      }

      return version;
   }
}
