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
package org.jboss.tattletale;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Archive
 * @author Jesper Pedersen <jesper.pedersen@jboss.org>
 */
public class Archive implements Comparable
{
   /** Archve type */
   private int type;

   /** The name */
   private String name;

   /** Requires */
   private SortedSet<String> requires;

   /** Provides */
   private SortedSet<String> provides;

   /** Locations */
   private SortedSet<Location> locations;

   /** Sub-archives */
   private List<Archive> subArchives;

   /**
    * Constructor
    * @param type The type
    * @param name The name
    * @param requires The requires
    * @param provides The provides
    * @param location The location
    */
   public Archive(int type, String name, SortedSet<String> requires, SortedSet<String> provides, Location location)
   {
      this.type = type;
      this.name = name;
      this.requires = requires;
      this.provides = provides;
      this.locations = new TreeSet<Location>();
      this.subArchives = null;

      if (location != null)
         this.locations.add(location);
   }

   /**
    * Get the type
    * @return The value
    */
   public int getType()
   {
      return type;
   }

   /**
    * Get the name
    * @return The value
    */
   public String getName()
   {
      return name;
   }

   /**
    * Get the requires
    * @return The value
    */
   public SortedSet<String> getRequires()
   {
      return requires;
   }

   /**
    * Get the provides
    * @return The value
    */
   public SortedSet<String> getProvides()
   {
      return provides;
   }

   /**
    * Get the locations
    * @return The value
    */
   public SortedSet<Location> getLocations()
   {
      return locations;
   }

   /**
    * Add a location
    * @param value The value
    */
   public void addLocation(Location value)
   {
      locations.add(value);
   }

   /**
    * Get the sub-archives
    * @return The value
    */
   public List<Archive> getSubArchives()
   {
      return subArchives;
   }

   /**
    * Add a sub-archive
    * @param value The value
    */
   public void addSubArchive(Archive value)
   {
      if (subArchives == null)
         subArchives = new ArrayList<Archive>();

      subArchives.add(value);
   }

   /**
    * Does the archives provide this class
    * @param clz The class name
    * @return True if the class is provided; otherwise false
    */
   public boolean doesProvide(String clz)
   {
      return provides.contains(clz);
   }

   /**
    * Comparable
    * @param o The other object
    * @return The compareTo value
    */
   public int compareTo(Object o)
   {
      Archive a = (Archive)o;

      return name.compareTo(a.getName());
   }

   /**
    * Equals
    * @param obj The other object
    * @return True if equals; otherwise false
    */
   public boolean equals(Object obj)
   {
      if (obj == null || !(obj instanceof Archive))
      {
         return false;
      }

      Archive a = (Archive)obj;

      return name.equals(a.getName());
   }

   /**
    * Hash code
    * @return The hash code
    */
   public int hashCode()
   {
      return 7 + 31 * name.hashCode();
   }

   /**
    * String representation
    * @return The string
    */
   public String toString()
   {
      StringBuffer sb = new StringBuffer();

      sb = sb.append(getClass().getName());
      sb = sb.append("(\n");

      sb = sb.append("name=");
      sb = sb.append(name);
      sb = sb.append("\n");

      sb = sb.append("requires=");
      sb = sb.append(requires);
      sb = sb.append("\n");

      sb = sb.append("provides=");
      sb = sb.append(provides);
      sb = sb.append("\n");

      sb = sb.append("locations=");
      sb = sb.append(locations);
      sb = sb.append("\n");

      sb = sb.append(")");

      return sb.toString();
   }
}
