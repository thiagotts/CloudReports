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

package cloudreports.gui;

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 * A helper class that generates customizable dialogs.
 * 
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class Dialog {

    /** 
     * Shows a warning message.
     *
     * @param   parent      the parent component of the dialog.
     * @param   message     the message to be shown.
     * @since               1.0
     */       
    public static void showWarning(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, 
                                     message,
                                     "Warning",
                                     JOptionPane.INFORMATION_MESSAGE,
                                     new javax.swing.ImageIcon(ImageIcon.class.getResource("/cloudreports/gui/resources/warning.png")));
    }

    /** 
     * Shows an information message.
     *
     * @param   parent      the parent component of the dialog.
     * @param   message     the message to be shown.
     * @since               1.0
     */       
    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent,
                                     message,
                                     "Information",
                                     JOptionPane.INFORMATION_MESSAGE,
                                     new javax.swing.ImageIcon(ImageIcon.class.getResource("/cloudreports/gui/resources/biginfo.png")));
    }

    /** 
     * Shows an error message.
     *
     * @param   parent      the parent component of the dialog.
     * @param   message     the message to be shown.
     * @since               1.0
     */      
    public static void showErrorMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(parent,
                                     message,
                                     "Error",
                                     JOptionPane.ERROR_MESSAGE,
                                     new javax.swing.ImageIcon(ImageIcon.class.getResource("/cloudreports/gui/resources/error.png")));
    }    

    /** 
     * Shows an success message.
     *
     * @param   parent      the parent component of the dialog.
     * @param   message     the message to be shown.
     * @since               1.0
     */     
    public static void showSuccessMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(parent,
                                     message,
                                     "Information",
                                     JOptionPane.INFORMATION_MESSAGE,
                                     new javax.swing.ImageIcon(ImageIcon.class.getResource("/cloudreports/gui/resources/success.png")));
    }

    /** 
     * Shows a dialog with a question.
     *
     * @param   parent      the parent component of the dialog.
     * @param   title       the title of the dialog.
     * @param   question    the question to be shown.
     * @since               1.0
     */      
    public static int showQuestionDialog(Component parent, String title, String question) {
        Object[] options = {"Cancel",
                            "Yes"};
        int n = JOptionPane.showOptionDialog(parent,
            question,
            title,
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            new javax.swing.ImageIcon(ImageIcon.class.getResource("/cloudreports/gui/resources/question.png")),
            options,
            options[0]);

        return n;
    }

}
