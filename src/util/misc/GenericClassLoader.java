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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class GenericClassLoader extends ClassLoader 
{

  private static Logger log = IseeLogger.getLogger(GenericClassLoader.class);

  private ArrayList _objects  = new ArrayList();
  private String folder_      = null;
  private String packageName_ = null;
  private String jarName_     = null;
  private boolean isJar       = false;

  public GenericClassLoader(String folder, String packageName)
  {
    this.folder_ = folder;
    this.packageName_ = packageName;
    this.jarName_ = getJarName(GenericClassLoader.class);
  }

  public ArrayList getObjects()
  {

    _objects = new ArrayList();

    try
    {
      File objectFolder       = new File(folder_);
      File[] currentObject    = null;
      String currentFileName  = null;
      ArrayList dummy         = new ArrayList();
      Class current           = null;
      currentObject           = objectFolder.listFiles();
      /*
       * Search Jar-File, if exists, for Objectalgorithms
       */
      /*
       * Search FileSystem Folder for Objectalgorithms
       */
      if (currentObject != null)
      {
        isJar = false;
//        log.debug("loading from file system");
        for (int i = 0; i < currentObject.length; i++)
        {
          currentFileName = currentObject[i].getName();
          if (currentFileName.endsWith(".class"))
          {
            currentFileName = currentFileName.substring(0,
                currentFileName.lastIndexOf("."));
            try
            {
//              log.debug("loading " + packageName_.replace(File.separatorChar,
//                    '.') + "." + currentFileName);

              current = Class.forName(packageName_.replace(File.separatorChar,
                    '.') + "." + currentFileName);

//              current = Class.forName(packageName_.replaceAll(File.separator,
//                    ".") + "." + currentFileName);
//
//              log.debug("loading " + packageName_.replaceAll(File.separator,
//                    ".") + "." + currentFileName);
            }
            catch (NoClassDefFoundError ncdfe)
            {
              current = loadClass("." + currentFileName);

              log.debug("loading " + currentFileName);
            }
            catch (ClassNotFoundException cnfe)
            {
              current = loadClass(packageName_ + File.separator + currentFileName);
            }
            _objects.add(current);
          }
        }
      }

      if (jarName_ != null)
      {
        isJar = true;
        log.debug("loading from jar-file");
        JarFile jf = new JarFile(jarName_);
        Enumeration jes = jf.entries();

        while (jes.hasMoreElements())
        {
          JarEntry je = (JarEntry)jes.nextElement();
          if (je.getName().startsWith(packageName_.replace('.', File.separatorChar)) &&
              je.getName().endsWith(".class"))
          {
            currentFileName = 
              je.getName().substring(je.getName().lastIndexOf(File.separatorChar)+1);
            currentFileName = currentFileName.substring(0, 
                currentFileName.lastIndexOf('.'));
            try
            {
              current = Class.forName(packageName_ + currentFileName);
            }
            catch (Exception ncdfe)
            {
              current = loadClass("." + currentFileName);
            }
            _objects.add(current);
          }
        }
      }



    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    return _objects;
  }


  private String getJarName(Class current)
  {
    String name         = null;
    /* Separator character in a Jar-File is '/' system independent */
    String myName       = current.getName().replace('.', '/');
    String resourceName = current.getResource(
                                      "/" + myName + ".class").toString();
    int    begin, end;
    
    if (resourceName.startsWith("jar:"))
    {
      end   = resourceName.indexOf('!');
      begin = resourceName.substring(0, end).lastIndexOf('/');
      name = resourceName.substring(begin + 1, end);
    }
    else
    {
      return null;
    }
    return name;
  }

   public Class findClass (String name) throws ClassNotFoundException 
   {
     Class returnvalue = null;
//     log.debug("findClass: isJar: " + isJar);

     try
     {
       if(name.indexOf(File.separatorChar) == 0)
       {
         returnvalue = Class.forName(packageName_ 
             + "."
             + name.substring(1,name.length()));
       }
       else
       {
         returnvalue = Class.forName(packageName_ 
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
         throw new ClassNotFoundException(name);
       }
       if(name.startsWith(packageName_))
       {
         returnvalue = defineClass(name.replace(File.separatorChar,'.'), b, 0, b.length);
       }
       else
       {
         returnvalue = defineClass(packageName_ + File.separator + name, b, 0, b.length);
       }
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
         throw new ClassNotFoundException(name);
       }
       //System.out.println("...ok: " + name);
       returnvalue = defineClass(packageName_ + File.separator + name, b, 0, b.length);
     }
     return returnvalue;
   }

   private byte[] loadClassData(String name)
   {
     if (isJar)
     {
//       log.debug("loadClassData: isJar");
       try 
       {
         name = folder_ + name.replace(
             '.',File.separatorChar)+".class";

         JarFile f   = new JarFile(jarName_);
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
     } 
     else
     {
//       log.debug("loadClassData: noJar");
       try 
       {
         if(!name.startsWith(packageName_.replace('.',File.separatorChar)))
         {
           name = folder_ + File.separator + name.replace(
               '.',File.separatorChar)+".class";
         }
         else
         {
           name += ".class";
         }
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
