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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A helper class that provides utility methods related to log IO.
 * 
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class LogIO {

    /**
     * Gets the file the log will use as a output stream.
     * 
     * @return  the output stream, if the operation was successful;
     *          <code>null</code> otherwise.
     */    
    public static OutputStream getFileOutputStream() {        
        File f;
        FileOutputStream fos;
        try {
            f = new File("tempLog");
            f.deleteOnExit();
            fos = new FileOutputStream(f);
            return fos;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LogIO.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Removes the temporary log file.
     * 
     * @return  <code>true</code> if the operation was successful;
     *          <code>false</code> otherwise.
     */     
    public static boolean removeTempLogFile(){
        try {
            File f = new File("tempLog");
            f.delete();
            return true;
        }
        catch(SecurityException ex) {
            Logger.getLogger(LogIO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }        
    }

    /**
     * Gets a string containing the simulation log.
     * 
     * @return  a string containing the log, if the operation was successful;
     *          <code>null</code> otherwise.
     */         
    public static String getLogStringFromFile() {
        File f = new File("tempLog");
        if(!f.exists()) return null;

        FileReader in = null;
        char[] buffer = new char[(int)f.length()];

        try{
            in = new FileReader(f);
            in.read(buffer);
        }
        catch(IOException ex) {
            Logger.getLogger(LogIO.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        finally {
            try {
                if(in != null) in.close();
            } catch (IOException ex) {
                Logger.getLogger(LogIO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return new String(buffer);
    }

}
