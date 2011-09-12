/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.tattletale.tools.as7;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * module.xml utilities
 * 
 * @author <a href="mailto:jesper.pedersen@jboss.org">Jesper Pedersen</a>
 */
public class ModuleXml
{
   /**
    * Get the module id from the specified file
    * @param f The file
    * @return The value
    */
   public static String getModuleId(File f)
   {
      FileReader fr = null;
      try
      {
         fr = new FileReader(f);

         XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
         XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(fr);

         while (xmlStreamReader.hasNext())
         {
            int eventCode = xmlStreamReader.next();

            switch (eventCode)
            {
               case XMLStreamReader.START_ELEMENT :

                  if ("module".equals(xmlStreamReader.getLocalName()))
                  {
                     for (int i = 0; i < xmlStreamReader.getAttributeCount(); i++)
                     {
                        String name = xmlStreamReader.getAttributeLocalName(i);
                        if ("name".equals(name))
                        {
                           return xmlStreamReader.getAttributeValue(i);
                        }
                     }
                  }

                  break;
               default :
            }
         }
      }
      catch (Throwable t)
      {
         // Nothing to do
      }
      finally
      {
         if (fr != null)
         {
            try
            {
               fr.close();
            }
            catch (IOException ioe)
            {
               // Ignore
            }
         }
      }

      return null;
   }

   /**
    * Read a string
    * @param xmlStreamReader The XML stream
    * @return The parameter
    * @exception XMLStreamException Thrown if an exception occurs
    */
   private static String readString(XMLStreamReader xmlStreamReader) throws XMLStreamException
   {
      String result = null;

      int eventCode = xmlStreamReader.next();

      while (eventCode != XMLStreamReader.END_ELEMENT)
      {
         switch (eventCode)
         {
            case XMLStreamReader.CHARACTERS :
               if (!xmlStreamReader.getText().trim().equals(""))
                  result = xmlStreamReader.getText().trim();

               break;

            default :
         }

         eventCode = xmlStreamReader.next();
      }

      return result;
   }
}
