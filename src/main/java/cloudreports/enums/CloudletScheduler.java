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

package cloudreports.enums;

import cloudreports.extensions.ExtensionsLoader;
import cloudreports.extensions.cloudletscheduler.CloudletSchedulerDynamicWorkload;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;

/**
 * Defines native types of cloudlet schedulers and implements an extension type
 * to support user-implemented new types.
 * 
 * @author      Thiago T. Sá
 * @since       1.0
 */
public enum CloudletScheduler implements Serializable{

    /** The space-shared cloudlet scheduler.
     *  Its {@link #getScheduler(double, int, java.lang.String)} method 
     *  returns an instance of CloudSim's CloudletSchedulerSpaceShared class.
     */        
    SPACE_SHARED {
        @Override
        public org.cloudbus.cloudsim.CloudletScheduler getScheduler(double mips, int pes, String schedulerAlias) {
            return new CloudletSchedulerSpaceShared();
        }
    },

    /** The time-shared cloudlet scheduler.
     *  Its {@link #getScheduler(double, int, java.lang.String)} method 
     *  returns an instance of CloudSim's CloudletSchedulerTimeShared class.
     */       
    TIME_SHARED {
        @Override
        public org.cloudbus.cloudsim.CloudletScheduler getScheduler(double mips, int pes, String schedulerAlias) {
            return new CloudletSchedulerTimeShared();
        }
    },

    /** The dynamic workload cloudlet scheduler.
     *  Its {@link #getScheduler(double, int, java.lang.String)} method 
     *  returns an instance of {@link CloudletSchedulerDynamicWorkload}.
     * 
     * @see CloudletSchedulerDynamicWorkload
     */          
    DYNAMIC_WORKLOAD {
        @Override
        public org.cloudbus.cloudsim.CloudletScheduler getScheduler(double mips, int pes, String schedulerAlias) {
            return new CloudletSchedulerDynamicWorkload(mips, pes);
        }
    },    
    
    /** The extension type. 
     *  It is used for all user-implemented new types.
     */     
    EXTENSION {
        @Override
        public org.cloudbus.cloudsim.CloudletScheduler getScheduler(double mips, int pes, String schedulerAlias) {
            try {
                Class<?>[] types = new Class<?>[]{double.class, int.class};
                Object[] arguments = new Object[]{mips, pes};
                return (org.cloudbus.cloudsim.CloudletScheduler) ExtensionsLoader.getExtension("CloudletScheduler", schedulerAlias, types, arguments);
            } catch (Exception e) {
                return null;
            }
        }
    };
    
    /** 
     * An abstract method to be implemented by every {@link CloudletScheduler}.
     *
     * @param   mips            the amount of mips per PE available to the scheduler.
     * @param   pes             the amount of processing unites available to the
     *                          scheduler.
     * @param   schedulerAlias  the alias of the cloudlet scheduler.
     * @return                  a CloudSim's CloudletScheduler subtype.
     * @since                   1.0
     */        
    public abstract org.cloudbus.cloudsim.CloudletScheduler getScheduler(double mips, int pes, String schedulerAlias);

    /** 
     * Gets an instance of cloudlet scheduler based on its alias.
     *
     * @param   schedulerAlias  the alias of the cloudlet scheduler.
     * @return                  an instance of the type with the given alias.
     * @since                   1.0
     */       
    public static CloudletScheduler getInstance(String schedulerAlias) {
        if(schedulerAlias.equals("Time shared")) return CloudletScheduler.TIME_SHARED;
        else if(schedulerAlias.equals("Space shared")) return CloudletScheduler.SPACE_SHARED;
        else if(schedulerAlias.equals("Dynamic workload")) return CloudletScheduler.DYNAMIC_WORKLOAD;
        else return CloudletScheduler.EXTENSION;
    }

    /** 
     * Gets all active cloudlet scheduler aliases.
     *
     * @return  an array of strings containing all active cloudlet scheduler
     *          aliases.
     * @since   1.0
     */     
    public static String[] getCloudletSchedulersNames() {
        String[] nativeSchedulers = new String[] {"Time shared", "Space shared", "Dynamic workload"};
        List<String> extensionSchedulers = ExtensionsLoader.getExtensionsAliasesByType("CloudletScheduler");
        extensionSchedulers.addAll(Arrays.asList(nativeSchedulers));
        
        return extensionSchedulers.toArray(new String[0]);
    }
}
