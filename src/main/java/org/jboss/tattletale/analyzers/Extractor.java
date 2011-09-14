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

package org.jboss.tattletale.analyzers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Class that would be used in order to obtain .jar files stored within a zipped up .war file.
 *
 * @author Navin Surtani
 */

public class Extractor
{

   /** Extract a JAR type file
    *
    * @param war  The war/ear file
    * @return     The root of the extracted JAR file
    * @exception  IOException Thrown if an error occurs
    */
   public static File extract(JarFile war) throws IOException
   {
      String basedir = System.getProperty("java.io.tmpdir");
      String warName = war.getName();
      File target;

      if (warName.startsWith(basedir))
      {
         target = new File(warName);
      }
      else
      {
         target = new File(basedir, warName);
      }
      System.out.println("Target destination is: " + target.getCanonicalPath());

      if (target.exists())
      {
         recursiveDelete(target);
      }
      if (!target.mkdirs())
      {
         throw new IOException("Could not create " + target);
      }

      Enumeration<JarEntry> entries = war.entries();
      while (entries.hasMoreElements())
      {
         JarEntry je = entries.nextElement();
         File copy = new File(target, je.getName());

         if (!je.isDirectory())
         {
            InputStream in = null;
            OutputStream out = null;

            // Make sure that the directory is _really_ there
            if (copy.getParentFile() != null && !copy.getParentFile().exists())
            {
               if (!copy.getParentFile().mkdirs())
                  throw new IOException("Could not create " + copy.getParentFile());
            }

            try
            {
               in = new BufferedInputStream(war.getInputStream(je));
               out = new BufferedOutputStream(new FileOutputStream(copy));

               byte[] buffer = new byte[4096];
               for (;;)
               {
                  int nBytes = in.read(buffer);
                  if (nBytes <= 0)
                     break;

                  out.write(buffer, 0, nBytes);
               }
               out.flush();
            }
            finally
            {
               try
               {
                  if (out != null)
                     out.close();
               }
               catch (IOException ignore)
               {
                  // Ignore
               }

               try
               {
                  if (in != null)
                     in.close();
               }
               catch (IOException ignore)
               {
                  // Ignore
               }
            }
         }
         else
         {
            if (!copy.exists())
            {
               if (!copy.mkdirs())
                  throw new IOException("Could not create " + copy);
            }
            else
            {
               if (!copy.isDirectory())
                  throw new IOException(copy + " isn't a directory");
            }
         }
      }
      return target;
   }
   private static void recursiveDelete(File file) throws IOException
   {
      if (file != null && file.exists())
      {
         File[] files = file.listFiles();
         if (files != null)
         {
            for (int i = 0; i < files.length; i++)
            {
               if (files[i].isDirectory())
               {
                  recursiveDelete(files[i]);
               }
               else if (!files[i].delete())
               {
                  throw new IOException("Could not delete the file of: " + files[i]);
               }
            }
         }

         if (!file.delete())
         {
            throw new IOException("Could not delete the file of: " + file);
         }
      }
   }
}
