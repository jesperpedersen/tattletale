/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.tattletale.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Helper class for handling configuration
 *
 * @author Jesper Pedersen <jesper.pedersen@jboss.org>
 */
public class Configuration
{
   /**
    * Constructor
    */
   private Configuration()
   {
   }

   /**
    * Load configuration from a file
    * @param fileName The file name
    * @return The properties
    */
   public static Properties loadFromFile(String fileName)
   {
      Properties properties = new Properties();

      FileInputStream fis = null;
      try
      {
         fis = new FileInputStream(fileName);
         properties.load(fis);
      }
      catch (IOException e)
      {
         System.err.println("Unable to open " + fileName);
      }
      finally
      {
         if (fis != null)
         {
            try
            {
               fis.close();
            }
            catch (IOException ioe)
            {
               // Nothing to do
            }
         }
      }

      return properties;
   }

   /**
    * Load configuration values specified from either a system property,
    * a file or the classloader
    * @param key The configuration key
    * @return The properties
    */
   public static Properties load(String key)
   {
      Properties properties = new Properties();
      String propertiesFile = System.getProperty(key);
      boolean loaded = false;

      if (propertiesFile != null)
      {
         FileInputStream fis = null;
         try
         {
            fis = new FileInputStream(propertiesFile);
            properties.load(fis);
            loaded = true;
         }
         catch (IOException e)
         {
            System.err.println("Unable to open " + propertiesFile);
         }
         finally
         {
            if (fis != null)
            {
               try
               {
                  fis.close();
               }
               catch (IOException ioe)
               {
                  //No op
               }
            }
         }
      }
      if (!loaded)
      {
         FileInputStream fis = null;
         try
         {
            fis = new FileInputStream(key);
            properties.load(fis);
            loaded = true;
         }
         catch (IOException ignore)
         {
            // Nothing to do
         }
         finally
         {
            if (fis != null)
            {
               try
               {
                  fis.close();
               }
               catch (IOException ioe)
               {
                  // Nothing to do
               }
            }
         }
      }
      if (!loaded)
      {
         InputStream is = null;
         try
         {
            ClassLoader cl = Configuration.class.getClassLoader();
            is = cl.getResourceAsStream(key);
            properties.load(is);
            loaded = true;
         }
         catch (Exception ie)
         {
            // Properties file not found
         }
         finally
         {
            if (is != null)
            {
               try
               {
                  is.close();
               }
               catch (IOException ioe)
               {
                  // Nothing to do
               }
            }
         }
      }

      return properties;
   }
}

