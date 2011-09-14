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

import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;

/**
 * Class that would be used when Tattletale is being used to analyze .war files.
 *
 * @author Navin Surtani
 */
public class WarArchive extends NestableArchive
{

   /**
    * Constructor
    *
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
    * @param subArchives             The list of subsidiary archives as prescribed by {@link NestableArchive]}
    */
   public WarArchive(String name, int version, List<String> manifest, List<String> sign,
                     SortedSet<String> requires, SortedMap<String, Long> provides, SortedMap<String,
                     SortedSet<String>> classDependencies, SortedMap<String, SortedSet<String>> packageDependencies,
                     SortedMap<String, SortedSet<String>> blacklistedDependencies, Location location,
                     List<Archive> subArchives)
   {
      super(ArchiveTypes.WAR, name, version, manifest, sign, requires, provides, classDependencies,
            packageDependencies, blacklistedDependencies, location, subArchives);
   }


}
