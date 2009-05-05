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
 * @author Jay Balunas jbalunas@jboss.org
 *
 */
public abstract class AbstractReportTask extends Task
{
   
   /**
    * Directory to scan
    */
   private String scanDir;
   
   /**
    * Directory to output reports
    */
   private String outputDir;
   
   
   /**
    * Constructor
    */
   public AbstractReportTask()
   {
      scanDir = ".";
      outputDir = ".";
   }

   /**
    * @return the scanDir
    */
   public String getScanDir() 
   {
      return scanDir;
   }

   /**
    * @param scanDir the scanDir to set
    */
   public void setScanDir(String scanDir) 
   {
      this.scanDir = scanDir;
   }

   /**
    * @return the outputDir
    */
   public String getOutputDir() 
   {
      return outputDir;
   }

   /**
    * @param outputDir the outputDir to set
    */
   public void setOutputDir(String outputDir) 
   {
      this.outputDir = outputDir;
   }

   /**
    * Execute Ant task
    * @exception BuildException If an error occurs
    */
   public abstract void execute() throws BuildException;

}
