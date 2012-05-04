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

import cloudreports.dao.SettingDAO;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * A helper class that provides utility methods related to e-mail features.
 * 
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class Mail {
    
    /**
     * Sends an e-mail.
     * It uses the user settings stored in the database.
     * 
     * @param   subject     the subject of the e-mail.
     * @param   message     the body message of the e-mail.
     * @since               1.0
     */    
    public static void sendMail(String subject, String message) {
            SettingDAO sDAO = new SettingDAO();
            String host = sDAO.getSetting("SMTPServer").getValue();
            String from = sDAO.getSetting("SenderAddress").getValue();
            String password = sDAO.getSetting("EmailPassword").getValue();
            String to = sDAO.getSetting("ReceiverAddress").getValue();
        
            Properties props = new Properties();
            String h = host.split(":")[0],
                    port = host.split(":")[1];

            props.put("mail.host", h);
            props.put("mail.smtp.port", port);
            props.put("mail.smtp.user", from);
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.socketFactory.port", port);
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.fallback", "false");

            Authenticator auth = new SMTPAuthenticator(from, password);
            
            Session mailConnection = Session.getInstance(props, auth);
            final Message msg = new MimeMessage(mailConnection);

            Address toAddress;
            Address fromAddress;
            try {
                toAddress = new InternetAddress(to);
                fromAddress = new InternetAddress(from);
                msg.setContent(message, "text/plain");
                msg.setFrom(fromAddress);
                msg.setRecipient(Message.RecipientType.TO, toAddress);
                msg.setSubject("CloudReports Notification - " + subject);
            } catch (AddressException ex) {
                Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MessagingException ex) {
                Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, null, ex);
            }

            Runnable r = new Runnable() {
                @Override
                public void run() {
                    try {
                        Transport.send(msg);
                    } catch (MessagingException ex) {
                        Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };
            Thread t = new Thread(r);
            t.start();
    }

    /**
    * An inner class that implements what might be the most basic SMTP 
    * authenticator ever made.
    * 
    * @since       1.0
    */
    private static class SMTPAuthenticator extends javax.mail.Authenticator {

        /** The e-mail address. */
        String from;
        
        /** The password. */
        String pass;

        /** 
         * Creates a SMTP authenticator.
         * 
         * @param   from    the e-mail address.
         * @param   pass    the password.
         * @since           1.0
         */          
        public SMTPAuthenticator(String from, String pass) {
            this.from = from;
            this.pass = pass;
        }

        /** 
         * Gets a password authentication.
         * 
         * @since   1.0
         */
        @Override
        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(from, pass);
        }
    }
    
}
