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

package cloudreports.extensions.cloudletscheduler;

import cloudreports.enums.CloudletScheduler;
import org.cloudbus.cloudsim.ResCloudlet;

/**
 * A subtype of CloudSim's CloudletSchedulerDynamicWorkload class. 
 * 
 * @see         CloudletScheduler#DYNAMIC_WORKLOAD
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class CloudletSchedulerDynamicWorkload extends org.cloudbus.cloudsim.CloudletSchedulerDynamicWorkload {
    
    /** 
     * Initializes a new instance of this class with the given amount of mips
     * and processing elements.
     *
     * @param   mips        the amount of mips per processing element.
     * @param   pesNumber   the number o processing elements.
     * @since               1.0
     */       
    public CloudletSchedulerDynamicWorkload(double mips, int pesNumber) {
        super(mips, pesNumber);
    }
    
    /** 
     * Gets the estimated finish time of a cloudlet.
     *
     * @param   rcl     a cloudlet submitted to a cloud resource for processing.
     * @param   time    the current time.
     * @since           1.0
     */      
    @Override
    public double getEstimatedFinishTime(ResCloudlet rcl, double time) {
        return time + ((rcl.getRemainingCloudletLength()) / getTotalMips());
    }
    
}
