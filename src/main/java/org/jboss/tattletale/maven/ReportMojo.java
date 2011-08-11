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
package org.jboss.tattletale.maven;

import org.jboss.tattletale.Main;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Implementation class for TattleTale Report Maven Mojo
 *
 * @author Jesper Pedersen <jesper.pedersen@jboss.org>
 */
public class ReportMojo extends TattletaleMojo
{
   /** Class loader structure */
   private String classloaderStructure;

   /** Reports */
   private String[] reports;

   /** Profiles */
   private String[] profiles;

   /** Excludes */
   private String[] excludes;

   /** Blacklisted */
   private String[] blacklisted;

   /** Fail on info */
   private boolean failOnInfo;

   /** Fail on warning */
   private boolean failOnWarn;

   /** Fail on error */
   private boolean failOnError;

   /** Scan */
   private String scan;

   /** Constructor */
   public ReportMojo()
   {
      this.classloaderStructure = null;
      this.profiles = null;
      this.excludes = null;
      this.blacklisted = null;
      this.failOnInfo = false;
      this.failOnWarn = false;
      this.failOnError = false;
      this.reports = null;
      this.scan = null;
   }

   /**
    * Get the class loader structure
    *
    * @return The value
    */
   public String getClassloader()
   {
      return classloaderStructure;
   }

   /**
    * Set the class loader structure
    *
    * @param cls The value
    */
   public void setClassloader(String cls)
   {
      this.classloaderStructure = cls;
   }

   /**
    * Get the reports
    *
    * @return The value
    */
   public String[] getReports()
   {
      return reports;
   }

   /**
    * Set the reports
    *
    * @param reports The value
    */
   public void setReports(String[] reports)
   {
      this.reports = reports;
   }

   /**
    * Get the profiles
    *
    * @return The value
    */
   public String[] getProfiles()
   {
      return profiles;
   }

   /**
    * Set the profiles
    *
    * @param profiles The value
    */
   public void setProfiles(String[] profiles)
   {
      this.profiles = profiles;
   }

   /**
    * Get the excludes
    *
    * @return The value
    */
   public String[] getExcludes()
   {
      return excludes;
   }

   /**
    * Set the excludes
    *
    * @param excludes The value
    */
   public void setExcludes(String[] excludes)
   {
      this.excludes = excludes;
   }

   /**
    * Get the blacklisted
    *
    * @return The value
    */
   public String[] getBlacklisted()
   {
      return blacklisted;
   }

   /**
    * Set the blacklisted
    *
    * @param blacklisted The value
    */
   public void setBlacklisted(String[] blacklisted)
   {
      this.blacklisted = blacklisted;
   }

   /**
    * Get fail on info
    *
    * @return The value
    */
   public boolean getFailOnInfo()
   {
      return failOnInfo;
   }

   /**
    * Set fail on info
    *
    * @param b The value
    */
   public void setFailOnInfo(boolean b)
   {
      this.failOnInfo = b;
   }

   /**
    * Get fail on warn
    *
    * @return The value
    */
   public boolean getFailOnWarn()
   {
      return failOnWarn;
   }

   /**
    * Set fail on warn
    *
    * @param b The value
    */
   public void setFailOnWarn(boolean b)
   {
      this.failOnWarn = b;
   }

   /**
    * Get fail on error
    *
    * @return The value
    */
   public boolean getFailOnError()
   {
      return failOnError;
   }

   /**
    * Set fail on error
    *
    * @param b The value
    */
   public void setFailOnError(boolean b)
   {
      this.failOnError = b;
   }

   /**
    * Get the scan
    *
    * @return The value
    */
   public String getScan()
   {
      return scan;
   }

   /**
    * Set the scan
    *
    * @param scan The value
    */
   public void setScan(String scan)
   {
      this.scan = scan;
   }

   /**
    * Execute
    *
    * @throws MojoExecutionException Thrown if the plugin cant be executed
    * @throws MojoFailureException   Thrown if there is an error
    */
   @Override
   public void execute() throws MojoExecutionException, MojoFailureException
   {
      try
      {
         Main main = new Main();

         main.setSource(getSource().getAbsolutePath());
         main.setDestination(getDestination().getAbsolutePath());

         if (getConfiguration() != null)
         {
            main.setConfiguration(getConfiguration().getAbsolutePath());
         }

         if (getFilter() != null)
         {
            main.setFilter(getFilter().getAbsolutePath());
         }

         main.setClassLoaderStructure(getClassloader());

         if (getReports() != null)
         {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < getReports().length; i++)
            {
               sb = sb.append(getReports()[i]);
               if (i < getReports().length - 1)
               {
                  sb = sb.append(",");
               }
            }
            main.setReports(sb.toString());
         }

         if (getProfiles() != null)
         {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < getProfiles().length; i++)
            {
               sb = sb.append(getProfiles()[i]);
               if (i < getProfiles().length - 1)
               {
                  sb = sb.append(",");
               }
            }
            main.setProfiles(sb.toString());
         }

         if (getExcludes() != null)
         {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < getExcludes().length; i++)
            {
               sb = sb.append(getExcludes()[i]);
               if (i < getExcludes().length - 1)
               {
                  sb = sb.append(",");
               }
            }
            main.setExcludes(sb.toString());
         }

         if (getBlacklisted() != null)
         {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < getBlacklisted().length; i++)
            {
               sb = sb.append(getBlacklisted()[i]);
               if (i < getBlacklisted().length - 1)
               {
                  sb = sb.append(",");
               }
            }
            main.setBlacklisted(sb.toString());
         }

         main.setFailOnInfo(getFailOnInfo());
         main.setFailOnWarn(getFailOnWarn());
         main.setFailOnError(getFailOnError());

         main.setScan(getScan());

         getLog().info("Scanning: " + getSource().getAbsolutePath());

         main.execute();
      }
      catch (Throwable t)
      {
         throw new MojoFailureException(t.getMessage());
      }
   }
}
