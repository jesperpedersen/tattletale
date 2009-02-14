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

/**
 * Java 5
 * @author Jesper Pedersen <jesper.pedersen@jboss.org>
 */
public class Java5 extends Archive
{
   /**
    * Constructor
    */
   public Java5()
   {
      super("Java 5", null, null, null);

      Location l = new Location("rt.jar", "JDK5");
      addLocation(l);
   }

   /**
    * Does the archives provide this class
    * @param clz The class name
    * @return True if the class is provided; otherwise false
    */
   @Override
   public boolean doesProvide(String clz)
   {
      /* TODO - provide a precise class list instead
                make specific versions for the various JREs */

      if (clz.startsWith("java.applet") ||
          clz.startsWith("java.awt") ||
          clz.startsWith("java.awt.color") ||
          clz.startsWith("java.awt.datatransfer") ||
          clz.startsWith("java.awt.dnd") ||
          clz.startsWith("java.awt.event") ||
          clz.startsWith("java.awt.font") ||
          clz.startsWith("java.awt.geom") ||
          clz.startsWith("java.awt.im") ||
          clz.startsWith("java.awt.im.spi") ||
          clz.startsWith("java.awt.image") ||
          clz.startsWith("java.awt.image.renderable") ||
          clz.startsWith("java.awt.print") ||
          clz.startsWith("java.beans") ||
          clz.startsWith("java.beans.beancontext") ||
          clz.startsWith("java.io") ||
          clz.startsWith("java.lang") ||
          clz.startsWith("java.lang.annotation") ||
          clz.startsWith("java.lang.instrument") ||
          clz.startsWith("java.lang.management") ||
          clz.startsWith("java.lang.ref") ||
          clz.startsWith("java.lang.reflect") ||
          clz.startsWith("java.math") ||
          clz.startsWith("java.net") ||
          clz.startsWith("java.nio") ||
          clz.startsWith("java.nio.channels") ||
          clz.startsWith("java.nio.channels.spi") ||
          clz.startsWith("java.nio.charset") ||
          clz.startsWith("java.nio.charset.spi") ||
          clz.startsWith("java.rmi") ||
          clz.startsWith("java.rmi.activation") ||
          clz.startsWith("java.rmi.dgc") ||
          clz.startsWith("java.rmi.registry") ||
          clz.startsWith("java.rmi.server") ||
          clz.startsWith("java.security") ||
          clz.startsWith("java.security.acl") ||
          clz.startsWith("java.security.cert") ||
          clz.startsWith("java.security.interfaces") ||
          clz.startsWith("java.security.spec") ||
          clz.startsWith("java.sql") ||
          clz.startsWith("java.text") ||
          clz.startsWith("java.util") ||
          clz.startsWith("java.util.concurrent") ||
          clz.startsWith("java.util.concurrent.atomic") ||
          clz.startsWith("java.util.concurrent.locks") ||
          clz.startsWith("java.util.jar") ||
          clz.startsWith("java.util.logging") ||
          clz.startsWith("java.util.prefs") ||
          clz.startsWith("java.util.regex") ||
          clz.startsWith("java.util.zip") ||
          clz.startsWith("javax.accessibility") ||
          clz.startsWith("javax.activity") ||
          clz.startsWith("javax.crypto") ||
          clz.startsWith("javax.crypto.interfaces") ||
          clz.startsWith("javax.crypto.spec") ||
          clz.startsWith("javax.imageio") ||
          clz.startsWith("javax.imageio.event") ||
          clz.startsWith("javax.imageio.metadata") ||
          clz.startsWith("javax.imageio.plugins.bmp") ||
          clz.startsWith("javax.imageio.plugins.jpeg") ||
          clz.startsWith("javax.imageio.spi") ||
          clz.startsWith("javax.imageio.stream") ||
          clz.startsWith("javax.management") ||
          clz.startsWith("javax.management.loading") ||
          clz.startsWith("javax.management.modelmbean") ||
          clz.startsWith("javax.management.monitor") ||
          clz.startsWith("javax.management.openmbean") ||
          clz.startsWith("javax.management.relation") ||
          clz.startsWith("javax.management.remote") ||
          clz.startsWith("javax.management.remote.rmi") ||
          clz.startsWith("javax.management.timer") ||
          clz.startsWith("javax.naming") ||
          clz.startsWith("javax.naming.directory") ||
          clz.startsWith("javax.naming.event") ||
          clz.startsWith("javax.naming.ldap") ||
          clz.startsWith("javax.naming.spi") ||
          clz.startsWith("javax.net") ||
          clz.startsWith("javax.net.ssl") ||
          clz.startsWith("javax.print") ||
          clz.startsWith("javax.print.attribute") ||
          clz.startsWith("javax.print.attribute.standard") ||
          clz.startsWith("javax.print.event") ||
          clz.startsWith("javax.rmi") ||
          clz.startsWith("javax.rmi.CORBA") ||
          clz.startsWith("javax.rmi.ssl") ||
          clz.startsWith("javax.security.auth") ||
          clz.startsWith("javax.security.auth.callback") ||
          clz.startsWith("javax.security.auth.kerberos") ||
          clz.startsWith("javax.security.auth.login") ||
          clz.startsWith("javax.security.auth.spi") ||
          clz.startsWith("javax.security.auth.x500") ||
          clz.startsWith("javax.security.cert") ||
          clz.startsWith("javax.security.sasl") ||
          clz.startsWith("javax.sound.midi") ||
          clz.startsWith("javax.sound.midi.spi") ||
          clz.startsWith("javax.sound.sampled") ||
          clz.startsWith("javax.sound.sampled.spi") ||
          clz.startsWith("javax.sql") ||
          clz.startsWith("javax.sql.rowset") ||
          clz.startsWith("javax.sql.rowset.serial") ||
          clz.startsWith("javax.sql.rowset.spi") ||
          clz.startsWith("javax.swing") ||
          clz.startsWith("javax.swing.border") ||
          clz.startsWith("javax.swing.colorchooser") ||
          clz.startsWith("javax.swing.event") ||
          clz.startsWith("javax.swing.filechooser") ||
          clz.startsWith("javax.swing.plaf") ||
          clz.startsWith("javax.swing.plaf.basic") ||
          clz.startsWith("javax.swing.plaf.metal") ||
          clz.startsWith("javax.swing.plaf.multi") ||
          clz.startsWith("javax.swing.plaf.synth") ||
          clz.startsWith("javax.swing.table") ||
          clz.startsWith("javax.swing.text") ||
          clz.startsWith("javax.swing.text.html") ||
          clz.startsWith("javax.swing.text.html.parser") ||
          clz.startsWith("javax.swing.text.rtf") ||
          clz.startsWith("javax.swing.tree") ||
          clz.startsWith("javax.swing.undo") ||
          clz.startsWith("javax.transaction") ||
          clz.startsWith("javax.transaction.xa") ||
          clz.startsWith("javax.xml") ||
          clz.startsWith("javax.xml.datatype") ||
          clz.startsWith("javax.xml.namespace") ||
          clz.startsWith("javax.xml.parsers") ||
          clz.startsWith("javax.xml.transform") ||
          clz.startsWith("javax.xml.transform.dom") ||
          clz.startsWith("javax.xml.transform.sax") ||
          clz.startsWith("javax.xml.transform.stream") ||
          clz.startsWith("javax.xml.validation") ||
          clz.startsWith("javax.xml.xpath") ||
          clz.startsWith("org.ietf.jgss") ||
          clz.startsWith("org.omg.CORBA") ||
          clz.startsWith("org.omg.CORBA_2_3") ||
          clz.startsWith("org.omg.CORBA_2_3.portable") ||
          clz.startsWith("org.omg.CORBA.DynAnyPackage") ||
          clz.startsWith("org.omg.CORBA.ORBPackage") ||
          clz.startsWith("org.omg.CORBA.portable") ||
          clz.startsWith("org.omg.CORBA.TypeCodePackage") ||
          clz.startsWith("org.omg.CosNaming") ||
          clz.startsWith("org.omg.CosNaming.NamingContextExtPackage") ||
          clz.startsWith("org.omg.CosNaming.NamingContextPackage") ||
          clz.startsWith("org.omg.Dynamic") ||
          clz.startsWith("org.omg.DynamicAny") ||
          clz.startsWith("org.omg.DynamicAny.DynAnyFactoryPackage") ||
          clz.startsWith("org.omg.DynamicAny.DynAnyPackage") ||
          clz.startsWith("org.omg.IOP") ||
          clz.startsWith("org.omg.IOP.CodecFactoryPackage") ||
          clz.startsWith("org.omg.IOP.CodecPackage") ||
          clz.startsWith("org.omg.Messaging") ||
          clz.startsWith("org.omg.PortableInterceptor") ||
          clz.startsWith("org.omg.PortableInterceptor.ORBInitInfoPackage") ||
          clz.startsWith("org.omg.PortableServer") ||
          clz.startsWith("org.omg.PortableServer.CurrentPackage") ||
          clz.startsWith("org.omg.PortableServer.POAManagerPackage") ||
          clz.startsWith("org.omg.PortableServer.POAPackage") ||
          clz.startsWith("org.omg.PortableServer.portable") ||
          clz.startsWith("org.omg.PortableServer.ServantLocatorPackage") ||
          clz.startsWith("org.omg.SendingContext") ||
          clz.startsWith("org.omg.stub.java.rmi") ||
          clz.startsWith("org.w3c.dom") ||
          clz.startsWith("org.w3c.dom.bootstrap") ||
          clz.startsWith("org.w3c.dom.events") ||
          clz.startsWith("org.w3c.dom.ls") ||
          clz.startsWith("org.xml.sax") ||
          clz.startsWith("org.xml.sax.ext") ||
          clz.startsWith("org.xml.sax.helpers") ||
          clz.startsWith("com.sun") ||
          clz.startsWith("sun") ||
          clz.startsWith("sunw"))
      {
         return true;
      }

      return false;
   }
}
