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
import cloudreports.extensions.vmallocationpolicies.VmAllocationPolicySingleThreshold;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.power.PowerHost;

/**
 * Defines native types of allocation policies and implements an extension type
 * to support user-implemented new types.
 * 
 * @see         cloudreports.extensions.vmallocationpolicies.VmAllocationPolicy
 * @author      Thiago T. Sá
 * @since       1.0
 */
public enum AllocationPolicy implements Serializable {

    /** The single threshold allocation policy.
     *  Its {@link #getPolicy(java.util.List, double, java.lang.String)} method 
     *  returns an instance of {@link VmAllocationPolicySingleThreshold}.
     * 
     *  @see VmAllocationPolicySingleThreshold
     */
    SINGLE_THRESHOLD {
        @Override
        public VmAllocationPolicy getPolicy(List<PowerHost> hostList, double upperUtilizationThreshold, String policyAlias) {
            return new VmAllocationPolicySingleThreshold(hostList,upperUtilizationThreshold);
        }
    },
    
    /** The extension type. 
     *  It is used for all user-implemented new types.
     */
    EXTENSION {
        @Override
        public VmAllocationPolicy getPolicy(List<PowerHost> hostList, double upperUtilizationThreshold, String policyAlias) {
            try{
                Class<?>[] types = new Class<?>[]{ List.class, double.class };
                Object[] arguments = new Object[]{ hostList, upperUtilizationThreshold };
                return (VmAllocationPolicy) ExtensionsLoader.getExtension("cloudreports.extensions.vmallocationpolicies.VmAllocationPolicy", policyAlias, types, arguments);
            }
            catch(Exception e) {
                return null;
            }
        }
    };

    /** 
     * An abstract getter method to be implemented by every {@link AllocationPolicy}.
     *
     * @param   hostList                    the list of hosts that will be managed
     *                                      according to this policy.
     * @param   upperUtilizationThreshold   the upper utilization threshold. Its value
     *                                      must be greater than zero and less than
     *                                      or equal to 1.
     * @param   policyAlias                 the alias to be used by extension types.
     * @return                              a CloudSim's VmAllocationPolicy subtype.
     * @since                               1.0
     */    
    public abstract VmAllocationPolicy getPolicy(List<PowerHost> hostList, double upperUtilizationThreshold, String policyAlias);
    
    /** 
     * Gets an instance of an allocation policy based on its alias.
     *
     * @param   policyAlias     the alias of the allocation policy.
     * @return                  an instance of the type with the given alias.
     * @since                   1.0
     */     
    public static AllocationPolicy getInstance(String policyAlias) {
        if(policyAlias.equals("Single threshold")) return AllocationPolicy.SINGLE_THRESHOLD;
        else return AllocationPolicy.EXTENSION;
    }

    /** 
     * Gets all active allocation policies aliases.
     *
     * @return  an array of strings containing all active allocation
     *          policies aliases.
     * @since   1.0
     */       
    public static String[] getAllocationPoliciesNames() {
        String[] nativePolicies = new String[] {"Single threshold"};
        List<String> extensionPolicies = ExtensionsLoader.getExtensionsAliasesByBaseClassname("cloudreports.extensions.vmallocationpolicies.VmAllocationPolicy");
        extensionPolicies.addAll(Arrays.asList(nativePolicies));
        
        return extensionPolicies.toArray(new String[0]);
    }

}
