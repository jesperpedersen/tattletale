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

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Base abstract class for TattleTale Maven Mojo
 *
 * @author Jesper Pedersen <jesper.pedersen@jboss.org>
 */
public abstract class TattletaleMojo extends AbstractMojo
{
   /** Source directory */
   private File source;

   /** Destination directory */
   private File destination;

   /** Configuration */
   private File configuration;

   /** Filter */
   private File filter;

   /** Constructor */
   public TattletaleMojo()
   {
      source = new File(".");
      destination = new File(".");
      configuration = null;
      filter = null;
   }

   /**
    * Get the source
    *
    * @return The value
    */
   public File getSource()
   {
      return source;
   }

   /**
    * Set the source
    *
    * @param source The value
    */
   public void setSource(File source)
   {
      this.source = source;
   }

   /**
    * Get the destination
    *
    * @return The value
    */
   public File getDestination()
   {
      return destination;
   }

   /**
    * Set the destination
    *
    * @param destination The value
    */
   public void setDestination(File destination)
   {
      this.destination = destination;
   }

   /**
    * Get the configuration
    *
    * @return The value
    */
   public File getConfiguration()
   {
      return configuration;
   }

   /**
    * Set the configuration
    *
    * @param configuration The value
    */
   public void setConfiguration(File configuration)
   {
      this.configuration = configuration;
   }

   /**
    * Get the filter
    *
    * @return The value
    */
   public File getFilter()
   {
      return filter;
   }

   /**
    * Set the filter
    *
    * @param filter The value
    */
   public void setFilter(File filter)
   {
      this.filter = filter;
   }

   /**
    * Execute
    *
    * @throws MojoExecutionException Thrown if the plugin cant be executed
    * @throws MojoFailureException   Thrown if there is an error
    */
   public abstract void execute() throws MojoExecutionException, MojoFailureException;
}
