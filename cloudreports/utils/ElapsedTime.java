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

/**
 * Represents a time span.
 * It is used to generate a readable formatted string that represents 
 * the time a CloudReports simulation took to complete. The time components
 * (days, hours, minutes and seconds) are calculated with basic arithmetics 
 * that do not consider factors such as leap years, thus the results are not
 * always accurate.
 * 
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class ElapsedTime {
    
    /** The seconds component of the time span. */
    private int seconds;
    
    /** The minutes component of the time span. */
    private int minutes;
    
    /** The hours component of the time span. */
    private int hours;
    
    /** The days component of the time span. */
    private int days;

    /** 
     * Creates an elapsed time instance from a milliseconds value.
     * 
     * @param   elapsedTimeInMilliseconds the time span in milliseconds.
     * @since           1.0
     */       
    public ElapsedTime(double elapsedTimeInMilliseconds) {
        double time = elapsedTimeInMilliseconds / 1000;
        seconds = (int) (time % 60);
        minutes = (int) ((time % 3600) / 60);
        hours = (int) ((time % 86400) / 3600);
        days = (int) (time / 86400);        
    }
    
    /**
     * Gets the seconds component of the time span.
     * 
     * @return  the seconds component of the time span.
     */    
    private String getSeconds() {
        StringBuilder secondsSB = new StringBuilder();
        
        if (seconds > 0) {
            if (String.valueOf(seconds).length() < 2) {
                secondsSB.append("0");
            }
            secondsSB.append(seconds);
        }
        else {
            secondsSB.append("0");
        }
        
        return secondsSB.toString();
    }
    
    /**
     * Gets the minutes component of the time span.
     * 
     * @return  the minutes component of the time span.
     */     
    private String getMinutes() {
        StringBuilder minutesSB = new StringBuilder();

        if (minutes > 0) {
            if (String.valueOf(minutes).length() < 2) {
                minutesSB.append("0");
            }
            minutesSB.append(minutes);
        } else {
            minutesSB.append("0");
        }

        return minutesSB.toString();
    }
    
    /**
     * Gets the hours component of the time span.
     * 
     * @return  the hours component of the time span.
     */         
    private String getHours() {
        StringBuilder hoursSB = new StringBuilder();

        if (hours > 0) {
            hoursSB.append(hours);
        } else {
            hoursSB.append("0");
        }

        return hoursSB.toString();
    }     
    
    /**
     * Gets the days component of the time span.
     * 
     * @return  the days component of the time span.
     */        
    private String getDays() {
        StringBuilder daysSB = new StringBuilder();

        if (days > 0) {
            daysSB.append(days);
        } else {
            daysSB.append("0");
        }

        return daysSB.toString();
    }
    
    /**
     * Gets a formatted string that represents the time a simulation took to
     * complete.
     * 
     * @return  a formatted string that represents a time span.
     */        
    @Override
    public String toString() {
        StringBuilder elapsedTime = new StringBuilder();

        if(days > 0) {
            elapsedTime.append(getDays()).append(" day");
            if(days > 1) {
                elapsedTime.append("s");
            }
            
            int remainingElements = 0;
            if(hours> 0) remainingElements++;
            if(minutes> 0) remainingElements++;
            if(seconds> 0) remainingElements++;
            
            if(remainingElements > 0) {
                if(remainingElements == 1) {
                    elapsedTime.append(" and ");
                }
                else elapsedTime.append(", ");
            }
        }
        
        if (hours > 0) {
            elapsedTime.append(getHours()).append(" hour");
            if (hours > 1) {
                elapsedTime.append("s");
            }

            int remainingElements = 0;
            if (minutes > 0) remainingElements++;
            if (seconds > 0) remainingElements++;

            if (remainingElements > 0) {
                if (remainingElements == 1) {
                    elapsedTime.append(" and ");
                } else {
                    elapsedTime.append(", ");
                }
            }
            
        }
        
        if (minutes > 0) {
            elapsedTime.append(getMinutes()).append(" minute");
            if (minutes > 1) {
                elapsedTime.append("s");
            }
            if (seconds > 0) {
                elapsedTime.append(" and ");
            }
        }
        
        if (seconds > 0) {
            elapsedTime.append(getSeconds()).append(" second");
            if (seconds > 1) {
                elapsedTime.append("s");
            }
        }
        
        return elapsedTime.toString();
    }
    
}
