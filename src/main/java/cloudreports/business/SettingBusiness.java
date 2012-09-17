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

package cloudreports.business;

import cloudreports.dao.SettingDAO;
import cloudreports.models.Setting;

/**
 * Provides a set of static high level methods related to the settings of 
 * the application.
 * 
 * @author      Thiago T. Sá
 * @since       1.1
 */
public class SettingBusiness {
    
    private static SettingDAO settingDAO = new SettingDAO();
    
    public static boolean isMailNotificationEnabled() {
        String settingValue = settingDAO.getSetting("EnableMailNotification").getValue();
        return Boolean.valueOf(settingValue);
    }
    
    public static int getNumberOfSimulations() {
        return Integer.valueOf(settingDAO.getSetting("NumberOfSimulations").getValue());
    }
    
    public static void setCurrentSimulation(int simulationId) {
        Setting currentSimulation = settingDAO.getSetting("CurrentSimulation");
        currentSimulation.setValue(String.valueOf(simulationId));
        settingDAO.updateSetting(currentSimulation);
    }
    
    public static int getCurrentSimulation() {
        return Integer.valueOf(settingDAO.getSetting("CurrentSimulation").getValue());
    }
    
    public static String getQRBGUsername() {
        return settingDAO.getSetting("QRBGusername").getValue();
    }
    
    public static String getQRBGPassword() {
        return settingDAO.getSetting("QRBGpassword").getValue();
    }
    
    public static int getTimeToSimulate() {
        return Integer.valueOf(settingDAO.getSetting("TimeToSimulate").getValue());
    }
    
    public static int getRandomnessOption() {
        return Integer.valueOf(settingDAO.getSetting("Randomness").getValue());
    }

    public static boolean isHtmlReportsEnabled() {
        Setting htmlReportsEnabled = settingDAO.getSetting("HtmlReports");
        if (htmlReportsEnabled == null) {
            htmlReportsEnabled = new Setting("HtmlReports", "true");
            settingDAO.insertSetting(htmlReportsEnabled);
            return true;
        }
        else return Boolean.valueOf(htmlReportsEnabled.getValue());

    }
    
    public static boolean isRawDataReportsEnabled() {
        Setting rawDataReportsEnabled = settingDAO.getSetting("RawDataReports");
        if (rawDataReportsEnabled == null) {
            rawDataReportsEnabled = new Setting("RawDataReports", "true");
            settingDAO.insertSetting(rawDataReportsEnabled);
            return true;
        }
        else return Boolean.valueOf(rawDataReportsEnabled.getValue());
    }
    
}
