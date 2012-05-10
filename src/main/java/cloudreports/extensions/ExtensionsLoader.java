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

package cloudreports.extensions;

import cloudreports.utils.FileIO;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Provides static methods that load user-implemented extensions using
 * the Java Reflection API.
 * It is entirely based on the content of the extensions/classnames.xml file.
 * 
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class ExtensionsLoader {
    
    /** The parameters array passed to the URLClassLoader. */
    private static final Class<?>[] parameters = new Class[]{URL.class};
    
    /** The list of the loaded extension files. */
    private static List<String> addedFiles = new ArrayList<String>();
    
    /** The path to the extensions folder. */
    private static String extensionsPath = FileIO.getPathOfExecutable() + "extensions";
    
    /** The path to the classnames.xml file. */
    private static String classnamesXmlPath = FileIO.getPathOfExecutable() + "extensions/classnames.xml";
    
    /** The classnames document. */
    private static Document classnamesXml = getClassnamesXml();

    /** 
     * Loads an extension file.
     *
     * @param   canonicalPath   the canonical path to the extension file.
     * @throws  IOException     if the system classloader could not load the
     *                          extension.
     * @since   1.0
     */     
    private static void addFile(String canonicalPath) throws IOException {
        if(addedFiles.contains(canonicalPath)) return;
        
        File f = new File(canonicalPath);
        addURL(f.toURI().toURL());
        addedFiles.add(canonicalPath);
    }

    /** 
     * Loads an extension file using its URL.
     *
     * @param   url             the URL of the extension file.
     * @throws  IOException     if the system classloader could not load the
     *                          extension.
     * @since   1.0
     */     
    private static void addURL(URL url) throws IOException {
        URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
        Class<?> sysclass = URLClassLoader.class;
        try {
            Method method = sysclass.getDeclaredMethod("addURL", parameters);
            method.setAccessible(true);
            method.invoke(sysloader,new Object[]{ url }); 
        } catch (Throwable t) {
            throw new IOException("Error: could not add URL to system classloader");
        }
    }
    
    /** 
     * Gets the aliases of all extension implementations of a given base class.
     * It reads the classnames.xml file to get the list of aliases.
     *
     * @param   type   			the type of the extension.
     * @return                  a list of aliases of all extension implementations
     *                          of the given base class.
     * @since   1.0
     */     
    public static List<String> getExtensionsAliasesByType(String type) {
        List<String> listAliases = new ArrayList<String>();
        if(classnamesXml == null) return listAliases;
        
        NodeList classNodes = classnamesXml.getElementsByTagName("class");            
        for(int index = 0; index < classNodes.getLength(); index++) {
        	try {
        		if(classNodes.item(index).getAttributes().getNamedItem("type").getNodeValue().equals(type))
        			listAliases.add(classNodes.item(index).getAttributes().getNamedItem("alias").getNodeValue());
        	}
        	catch (NullPointerException e) {
        		continue;
			}
        }
        
        return listAliases;
    }
    
    /** 
     * Gets a specific user-implemented extension object based on its base class
     * and its alias.
     *
     * @param   type		            the type of the extension.
     * @param   alias                   the alias of the extension.
     * @param   constructorTypes        the types used by the constructor of the
     *                                  extension class.
     * @param   constructorArguments    the arguments to be used in the 
     *                                  constructor of the extension class.
     * @return                          an instance of the extension class.
     * @since                           1.0
     */      
    public static Object getExtension(String type, String alias, Class<?>[] constructorTypes, Object[] constructorArguments) {
        String filename = getFileName(type, alias);
        String classname = getClassnameOfExtension(type, alias);
        if(filename == null || classname == null) return null;        
        
        File extensionFile = new File(extensionsPath + "/" + filename);
        if(!extensionFile.exists() || !extensionFile.isFile()) return null;
        
        try {
            addFile(extensionFile.getCanonicalPath());
            
            if(constructorTypes != null && constructorArguments != null) 
                return ClassLoader.getSystemClassLoader().loadClass(classname).getConstructor(constructorTypes).newInstance(constructorArguments);            
            
            else return ClassLoader.getSystemClassLoader().loadClass(classname).getConstructor().newInstance();
            
        } catch (Exception ex) {
            Logger.getLogger(ExtensionsLoader.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /** 
     * Gets the name of the file that contains the extension class based on
     * the base classname and the extension alias.
     * It reads the classnames.xml file to get name of the file.
     *
     * @param   type		            the type of the extension.
     * @param   alias                   the alias of the extension.
     * @return                          the name of the file that contains the
     *                                  extension class.
     * @since                           1.0
     */      
    private static String getFileName(String type, String alias) {
        if(classnamesXml == null) return null;
        
        NodeList classNodes = classnamesXml.getElementsByTagName("class");            
        for(int index = 0; index < classNodes.getLength(); index++) {
            if(classNodes.item(index).getAttributes().getNamedItem("type").getNodeValue().equals(type) &&
               classNodes.item(index).getAttributes().getNamedItem("alias").getNodeValue().equals(alias))
                return classNodes.item(index).getAttributes().getNamedItem("filename").getNodeValue();
        }
        
        return null;
    }
    
    /** 
     * Gets the classname of a extension class based on its base classname
     * and its alias.
     * It reads the classnames.xml file to get classname.
     *
     * @param   type		            the type of the extension.
     * @param   alias                   the alias of the extension.
     * @return                          the classname of the extension class.
     * @since                           1.0
     */     
    private static String getClassnameOfExtension(String type, String alias) {
        if(classnamesXml == null) return null;
        
        NodeList classNodes = classnamesXml.getElementsByTagName("class");            
        for(int index = 0; index < classNodes.getLength(); index++) {
            if(classNodes.item(index).getAttributes().getNamedItem("type").getNodeValue().equals(type) &&
               classNodes.item(index).getAttributes().getNamedItem("alias").getNodeValue().equals(alias))
                return classNodes.item(index).getAttributes().getNamedItem("name").getNodeValue();
        }
        
        return null;
    }
    
    /** 
     * Loads an instance of <code>Document</code> that contains the 
     * classnames.xml file.
     *
     * @return                          a <code>Document</code> object 
     *                                  containing the classnames.xml file.
     * @since                           1.0
     */      
    private static Document getClassnamesXml() {
        File classnamesXmlFile = new File(classnamesXmlPath);
        if(!classnamesXmlFile.exists() || !classnamesXmlFile.isFile()) return null;
        
        DocumentBuilder db;
        try {
            db = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            InputSource is = new InputSource(classnamesXmlFile.toURI().toURL().openStream());
            Document classnamesXmlDocument = db.parse(is);
            return classnamesXmlDocument;
            
        } catch (Exception ex) {
            Logger.getLogger(ExtensionsLoader.class.getName()).log(Level.INFO, null, ex);
            return null;
        }
    }
        
}
