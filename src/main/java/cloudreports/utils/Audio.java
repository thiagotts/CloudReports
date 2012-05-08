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
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/**
 * Provides methods used to play audio files.
 * 
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class Audio {

    /** 
     * Plays audio from a file.
     * 
     * @param   filePath    the path to the audio file.
     * @since               1.0
     */        
    public static void playAudioFromFile(String filePath) {
        File file;
        InputStream in;
        AudioStream as;
        try {
            file = new File(File.class.getResource(filePath).toURI());
            in = new FileInputStream(file);
            as = new AudioStream(in);
            AudioPlayer.player.start(as);
        } catch (URISyntaxException ex) {
            Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /** 
     * Plays audio from a project resource.
     * 
     * @param   resourcePath    the path to the resource.
     * @since                   1.0
     */       
    public static void playAudioFromResource(String resourcePath) {
        ClassLoader classLoader = Audio.class.getClassLoader();
        InputStream resourceStream = classLoader.getResourceAsStream(resourcePath);

        AudioStream as;
        try {
            as = new AudioStream(resourceStream);
            AudioPlayer.player.start(as);
        } catch (IOException ex) {
            Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}