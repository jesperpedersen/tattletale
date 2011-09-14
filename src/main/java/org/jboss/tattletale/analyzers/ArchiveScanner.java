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

import org.jboss.tattletale.core.Archive;
import org.jboss.tattletale.profiles.Profile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

/**
 * Interface that will be used on the top level in order to scan different archive types (.jar, .war, .ear etc).
 * @author Jesper Pedersen <jesper.pedersen@jboss.org>
 * @author Navin Surtani
 * */
public interface ArchiveScanner
{
   /**
    * Scan an archive
    *
    * @param file -  The file to be scanned.
    * @return The archive
    *
    * @throws IOException - If there is an error with the input streams.
    */
   public Archive scan(File file) throws IOException;

   /**
    * Scan an archive
    * @param file        The File to be scanned
    * @param gProvides   The global provides map
    * @param known       The set of known archives
    * @param blacklisted The set of black listed packages
    * @throws IOException - If there is an error with the input streams.
    * @return The archive
    */

   public Archive scan(File file, Map<String, SortedSet<String>> gProvides,
                              List<Profile> known, Set<String> blacklisted) throws IOException;

}
