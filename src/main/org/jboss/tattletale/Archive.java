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

import java.util.List;
import java.util.SortedSet;

/**
 * Archive
 * @author Jesper Pedersen <jesper.pedersen@jboss.org>
 */
public class Archive implements Comparable
{
   /** The name */
   private String name;

   /** The filename */
   private String filename;

   /** Requires */
   private SortedSet<String> requires;

   /** Provides */
   private SortedSet<String> provides;

   /** Version */
   private String version;

   /** Locations */
   private List<String> locations;

   /**
    * Constructor
    * @param name The name
    * @param filename The filename
    * @param requires The requires
    * @param provides The provides
    * @param version The version
    */
   public Archive(String name, String filename, SortedSet<String> requires, SortedSet<String> provides, String version)
   {
      this.name = name;
      this.filename = filename;
      this.requires = requires;
      this.provides = provides;
      this.version = version;
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
    * Get the filename
    * @return The value
    */
   public String getFilename()
   {
      return filename;
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
    * Get the version
    * @return The value
    */
   public String getVersion()
   {
      return version;
   }

   /**
    * Get the locations
    * @return The value
    */
   public List<String> getLocations()
   {
      return locations;
   }

   /**
    * Set the locations
    * @param value The value
    */
   public void setLocations(List<String> value)
   {
      locations = value;
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

      return name.equals(a.getName()) && (version != null ? version.equals(a.getVersion()) : true);
   }

   /**
    * Hash code
    * @return The hash code
    */
   public int hashCode()
   {
      int hash = 7;

      hash += 31 * name.hashCode();

      if (version != null)
         hash += 31 * version.hashCode();

      return hash;
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

      sb = sb.append("filename=");
      sb = sb.append(filename);
      sb = sb.append("\n");

      sb = sb.append("requires=");
      sb = sb.append(requires);
      sb = sb.append("\n");

      sb = sb.append("provides=");
      sb = sb.append(provides);
      sb = sb.append("\n");

      sb = sb.append(")");

      return sb.toString();
   }
}
