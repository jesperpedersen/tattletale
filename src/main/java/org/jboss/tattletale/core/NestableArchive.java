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
package org.jboss.tattletale.core;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;

/**
 * Nestable archive
 *
 * @author Jesper Pedersen <jesper.pedersen@jboss.org>
 */
public abstract class NestableArchive extends Archive
{
   /** SerialVersionUID */
   static final long serialVersionUID = -9197985968607581451L;

   /** Sub-archives */
   private List<Archive> subArchives;

   /**
    * Constructor
    *
    * @param type                    The type
    * @param name                    The name
    * @param version                 The version number
    * @param manifest                The manifest
    * @param sign                    The signing information
    * @param requires                The requires
    * @param provides                The provides
    * @param classDependencies       The class dependencies
    * @param packageDependencies     The package dependencies
    * @param blacklistedDependencies The blacklisted dependencies
    * @param location                The location
    */
   public NestableArchive(int type, String name, int version, List<String> manifest, List<String> sign,
                          SortedSet<String> requires, SortedMap<String, Long> provides,
                          SortedMap<String, SortedSet<String>> classDependencies,
                          SortedMap<String, SortedSet<String>> packageDependencies,
                          SortedMap<String, SortedSet<String>> blacklistedDependencies, Location location)
   {
      super(type, name, version, manifest, sign, requires, provides,
            classDependencies, packageDependencies, blacklistedDependencies, location);

      this.subArchives = null;
   }

   /**
    * Get the sub-archives
    *
    * @return The value
    */
   public List<Archive> getSubArchives()
   {
      return subArchives;
   }

   /**
    * Add a sub-archive
    *
    * @param value The value
    */
   public void addSubArchive(Archive value)
   {
      if (subArchives == null)
      {
         subArchives = new ArrayList<Archive>(1);
      }

      subArchives.add(value);
   }
}
