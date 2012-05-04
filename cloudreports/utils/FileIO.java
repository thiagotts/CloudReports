/* 
 * Copyright (c) 2010-2012 Thiago T. Sá
 * 
 * This file is part of CloudReports.
 *
 * CloudReports is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * CloudReports is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * For more information about your rights as a user of CloudReports,
 * refer to the LICENSE file or see <http://www.gnu.org/licenses/>.
 */

package cloudreports.utils;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * A helper class that provides utility methods related to files IO.
 * 
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class FileIO {
    
    /**
     * Gets the path to the application's executable.
     * 
     * @return  the path to the application's executable.
     */    
    public static String getPathOfExecutable() {
        String baseDirectory = FileIO.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String executableName = baseDirectory.substring(baseDirectory.lastIndexOf('/')+1);
        if(baseDirectory.indexOf(executableName) > 0) return baseDirectory.substring(0, baseDirectory.indexOf(executableName));
        else return baseDirectory;
    }
    
    /**
     * Gets a string from a project resource.
     * 
     * @param   filePath    the path to the resource.
     * @return              a string loaded from the resource.
     */      
    public static String readStringFromResource(String filePath) {
        ClassLoader classLoader = FileIO.class.getClassLoader();
        InputStream resourceStream = classLoader.getResourceAsStream(filePath);

        String strResult = null;
        try {
            strResult = convertStreamToString(resourceStream);
        } catch (Exception ex) {
            Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            try {
                if(resourceStream != null) resourceStream.close();
            } catch (IOException ex) {
                Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return strResult;
    }

    /**
     * Writes a string to a file.
     * 
     * @param   filePath    the path to the output file.
     * @param   content     the string to be written.
     * @return              <code>true</code> if the operation was successful;
     *                      <code>false</code> otherwise.
     */          
    public static boolean writeStringToFile(String filePath, String content) {
        FileOutputStream out = null;
        try {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }

            file.createNewFile();
            out = new FileOutputStream(file);
            out.write(content.getBytes());
            
            return true;
        } catch (IOException ex) {
            Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        finally {
            try {
                if(out != null) out.close();
            } catch (IOException ex) {
                Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Appends a string to a file.
     * 
     * @param   filePath    the path to the output file.
     * @param   content     the string to be appended.
     * @return              <code>true</code> if the operation was successful;
     *                      <code>false</code> otherwise.
     */          
    public static boolean appendStringToFile(String filePath, String content) {
        FileWriter fstream;
        BufferedWriter out = null;
        try {
            fstream = new FileWriter(filePath, true);
            out = new BufferedWriter(fstream);
            out.write(content);
            return true;
        } catch (IOException ex) {
            Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            try {
                if(out != null) out.close();
            } catch (IOException ex) {
                Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Reads a byte array from a file.
     * 
     * @param   filePath    the path to the file.
     * @return              the byte array if the operation was successful;
     *                      <code>null</code> otherwise.
     */     
    public static byte[] readBytesFromFile(String filePath) {
        InputStream resourceStream = null;
        try {
            ClassLoader classLoader = FileIO.class.getClassLoader();
            resourceStream = classLoader.getResourceAsStream(filePath);
            int bufferSize = resourceStream.read(new byte[200000]); //TODO: Set the buffer size dinamically.
            
            byte[] bytes = new byte[bufferSize];
            resourceStream = classLoader.getResourceAsStream(filePath);
            resourceStream.read(bytes);            
            return bytes;
        } catch (IOException ex) {
            Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        finally {
            try {
                if(resourceStream != null) resourceStream.close();
            } catch (IOException ex) {
                Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Writes a byte array into a file.
     * 
     * @param   filePath    the path to the output file.
     * @param   content     the byte array to be written.
     * @return              <code>true</code> if the operation was successful;
     *                      <code>false</code> otherwise.
     */       
    public static boolean writeBytesToFile(String filePath, byte[] content) {
        FileOutputStream out = null;
        try {
            File f = new File(filePath);
            if (f.exists()) f.delete();

            f.createNewFile();
            out = new FileOutputStream(f);
            out.write(content);

            return true;
        } catch (IOException ex) {
            Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        finally {
            try {
                if(out != null) out.close();
            } catch (IOException ex) {
                Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Converts a memory stream into a string.
     * 
     * @param   is  the input stream to be converted.
     * @return      the resulting string, if the operation was successful;
     *              <code>null</code> otherwise.
     */        
    private static String convertStreamToString(InputStream is) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            is.close();
            return sb.toString();
        } catch (IOException ex) {
            Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    
  /**
   * List directory contents for a resource folder. Not recursive.
   * This is basically a brute-force implementation.
   * Works for regular files and also JARs.
   * 
   * @author Greg Briggs
   * @param clazz Any java class that lives in the same place as the resources you want.
   * @param path Should end with "/", but not start with one.
   * @return Just the name of each member item, not the full paths.
   * @throws URISyntaxException 
   * @throws IOException 
   */
  public static String[] getResourceListing(Class clazz, String path) throws URISyntaxException, IOException {
      URL dirURL = clazz.getClassLoader().getResource(path);
      if (dirURL != null && dirURL.getProtocol().equals("file")) {
        /* A file path: easy enough */
        return new File(dirURL.toURI()).list();
      } 

      if (dirURL == null) {
        /* 
         * In case of a jar file, we can't actually find a directory.
         * Have to assume the same jar as clazz.
         */
        String me = clazz.getName().replace(".", "/")+".class";
        dirURL = clazz.getClassLoader().getResource(me);
      }
      
      if (dirURL.getProtocol().equals("jar")) {
        /* A JAR path */
        String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); //strip out only the JAR file
        JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
        Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
        Set<String> result = new HashSet<String>(); //avoid duplicates in case it is a subdirectory
        while(entries.hasMoreElements()) {
          String name = entries.nextElement().getName();
          if (name.startsWith(path)) { //filter according to the path
            String entry = name.substring(path.length());
            int checkSubdir = entry.indexOf("/");
            if (checkSubdir >= 0) {
              // if it is a subdirectory, we just return the directory name
              entry = entry.substring(0, checkSubdir);
            }
            result.add(entry);
          }
        }
        return result.toArray(new String[result.size()]);
      } 
      
      throw new UnsupportedOperationException("Cannot list files for URL "+dirURL);
  }    
    
}
