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
package org.jboss.tattletale.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Base abstract class for TattleTale Report Ant Tasks
 *
 * @author Jesper Pedersen <jesper.pedersen@jboss.org>
 * @author Jay Balunas jbalunas@jboss.org
 */
public abstract class AbstractReportTask extends Task
{
   /** Source directory */
   private String source;

   /** Destination directory */
   private String destination;

   /** Configuration */
   private String configuration;

   /** Filter */
   private String filter;

   /** Constructor */
   public AbstractReportTask()
   {
      source = ".";
      destination = ".";
      configuration = null;
      filter = null;
   }

   /** @return the scanDir */
   @Deprecated
   public String getScanDir()
   {
      return source;
   }

   /** @param scanDir the scanDir to set */
   @Deprecated
   public void setScanDir(String scanDir)
   {
      this.source = scanDir;
   }

   /** @return the outputDir */
   @Deprecated
   public String getOutputDir()
   {
      return destination;
   }

   /** @param outputDir the outputDir to set */
   @Deprecated
   public void setOutputDir(String outputDir)
   {
      this.destination = outputDir;
   }

   /**
    * Get the source
    *
    * @return The value
    */
   public String getSource()
   {
      return source;
   }

   /**
    * Set the source
    *
    * @param source The value
    */
   public void setSource(String source)
   {
      this.source = source;
   }

   /**
    * Get the destination
    *
    * @return The value
    */
   public String getDestination()
   {
      return destination;
   }

   /**
    * Set the destination
    *
    * @param destination The value
    */
   public void setDestination(String destination)
   {
      this.destination = destination;
   }

   /**
    * Get the configuration
    *
    * @return The value
    */
   public String getConfiguration()
   {
      return configuration;
   }

   /**
    * Set the configuration
    *
    * @param configuration The value
    */
   public void setConfiguration(String configuration)
   {
      this.configuration = configuration;
   }

   /**
    * Get the filter
    *
    * @return The value
    */
   public String getFilter()
   {
      return filter;
   }

   /**
    * Set the filter
    *
    * @param filter The value
    */
   public void setFilter(String filter)
   {
      this.filter = filter;
   }

   /**
    * Execute Ant task
    *
    * @throws BuildException If an error occurs
    */
   public abstract void execute() throws BuildException;

}
