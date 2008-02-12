/* *********************************************************************** *
 *                                                                         *
 * This file is part of Integrated Structure Evolution Environment (ISEE). *
 * Copyright (C) 2002-2007 Keyan Zahedi and Martin Huelse                  *
 * All rights reserved.                                                    *
 * Email: {keyan,aberys}@users.sourceforge.net                             *
 * Web: http://sourceforge.net/projects/isee                               *
 *                                                                         *
 * For a list of contributors see the file AUTHORS.                        *
 *                                                                         *
 * ISEE is free software; you can redistribute it and/or modify it under   *
 * the terms of the GNU General Public License as published by the Free    *
 * Software Foundation; either version 2 of the License, or (at your       *
 * option) any later version.                                              *
 *                                                                         *
 * ISEE is distributed in the hope that it will be useful, but WITHOUT     *
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or   *
 * FITNESS FOR A PARTICULAR PURPOSE.                                       *
 *                                                                         *
 * You should have received a copy of the GNU General Public License       *
 * along with ISEE in the file COPYING; if not, write to the Free          *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor,                 *
 * Boston, MA 02110-1301, USA                                              *
 *                                                                         *
 * *********************************************************************** */


package util.misc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * This class represents a dynamic class loader which is able to (re)load
 * classes at runtime by their name and location (also in jar-files).
 *
 * Please note that a to be dynamically REloaded (!) class file must be
 * outside of the given classpath. This is why java in general does not allow
 * reloading classes and each classloader automatically askes the system
 * classloader to try loading before he tries it himself.
 *
 * That means: If you want to REload classes at runtime (more than one time)
 * their files MUST be unacessible to the system classloader. But this is not a
 * big problem, if you simply create somewhere a subfolder and put your dynamic
 * classes into it. If you only want to load them one time at startup, you
 * should better use the Class.forName()-method.
 *
 */
public class DynamicClassLoader extends ClassLoader 
{

   private String  rootPath;
   private String  jar;
   private boolean isJar;
   private String packageName = null;

   /**
    * Constructs an instance of the dynamic classloader wich is able to search
    * class-files in a specified path or jar-file.
    *
    * @param rootPath is the path where the classloader searches the classfiles
    *                 or where the given jar-file lies
    * @param jar is the name of the jarfile containing the wanted classfiles.
    *            can be null.
    * @param isJar should be true, the classloader should search in the
    *              jar-file.
    */
   public DynamicClassLoader(String rootPath, String jar, boolean isJar) 
   {
     this.rootPath = rootPath;
     this.isJar = isJar;
     this.jar  = jar;
   }

   public DynamicClassLoader(String rootPath, String packageName, String jar,
       boolean isJar)
   {
     this(rootPath, jar, isJar);
     this.packageName = packageName;
   }

   public Class findClass (String name) throws ClassNotFoundException 
   {
     Class returnvalue = null;
     if(packageName == null)
     {
       packageName = this.rootPath.replace(File.separatorChar, '.');
     }

     try
     {
       if(name.indexOf(File.separatorChar) == 0)
       {
         returnvalue = Class.forName(packageName 
             + "."
             + name.substring(1,name.length()));
       }
       else
       {
         returnvalue = Class.forName(packageName 
             + "."
             + name);
       }
     }
     catch (ClassNotFoundException cnfe)
     {

       //System.out.println("find class...");     
       byte[] b = loadClassData(name);
       if (isJar)
       {
         name = name.substring(1, name.length()); 
       }
       if (b == null)
       {
         //System.out.println("...failed: " + name);
         throw new ClassNotFoundException(name);
       }
       //System.out.println("...ok: " + name);
       returnvalue = defineClass(name, b, 0, b.length);
     }
     catch (NoClassDefFoundError ncdfe)
     {

       //System.out.println("find class...");     
       byte[] b = loadClassData(name);
       if (isJar)
       {
         name = name.substring(1, name.length()); 
       }
       if (b == null)
       {
         //System.out.println("...failed: " + name);
         throw new ClassNotFoundException(name);
       }
       //System.out.println("...ok: " + name);
       returnvalue = defineClass(name, b, 0, b.length);
     }
     return returnvalue;
   }

   private byte[] loadClassData(String name) 
   {
     if (isJar)
     {
       try 
       {
         name = rootPath + name.replace(
             '.',File.separatorChar)+".class";

         JarFile f   = new JarFile(jar);
         JarEntry je = f.getJarEntry(name);

         BufferedInputStream fi = new BufferedInputStream(f.getInputStream(je));
         
         byte cByte[] = new byte[(int)je.getSize()];
         fi.read(cByte);
         fi.close();
         
         return cByte;
       } catch (Exception ex) 
       {
         return null;
       }
     } else
     {
       try 
       {
         name = rootPath + File.separator + name.replace(
             '.',File.separatorChar)+".class";
         File f = new File(name);
         FileInputStream fi = new FileInputStream(name);
         byte cByte[] = new byte[(int) f.length()];
         fi.read(cByte);
         fi.close();
         return cByte;
       } catch (Exception ex) 
       {
         return null;
       }
     }
   }

}
