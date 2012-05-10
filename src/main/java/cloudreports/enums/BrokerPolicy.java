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
import cloudreports.extensions.brokers.RoundRobinDatacenterBroker;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import org.cloudbus.cloudsim.DatacenterBroker;

/**
 * Defines native types of broker policies and implements an extension type
 * to support user-implemented new types.
 * 
 * @see         cloudreports.extensions.brokers.Broker
 * @author      Thiago T. Sá
 * @since       1.0
 */
public enum BrokerPolicy implements Serializable {

    /** The round robin broker policy.
     *  Its {@link #createBroker(java.lang.String, java.lang.String) } method 
     *  returns an instance of {@link RoundRobinDatacenterBroker}.
     * 
     *  @see RoundRobinDatacenterBroker
     */    
    ROUND_ROBIN {
        @Override
        public DatacenterBroker createBroker(String customerName, String brokerAlias) {
            try{
                return new RoundRobinDatacenterBroker(customerName);
            }
            catch(Exception e) {
                return null;
            }

        }
    },
    
    /** The extension type. 
     *  It is used for all user-implemented new types.
     */    
    EXTENSION {
        @Override
        public DatacenterBroker createBroker(String customerName, String brokerAlias) {
            try{
                Class<?>[] types = new Class<?>[]{String.class};
                Object[] arguments = new Object[]{ customerName };
                return (DatacenterBroker) ExtensionsLoader.getExtension("Broker", brokerAlias, types, arguments);
            }
            catch(Exception e) {
                return null;
            }
        }
    };
    
    /** 
     * An abstract method to be implemented by every {@link BrokerPolicy}.
     *
     * @param   customerName    the name of the customer this broker policy relates to.
     * @param   brokerAlias     the alias of the broker policy.
     * @return                  a CloudSim's DatacenterBroker subtype.
     * @since                   1.0
     */      
    public abstract DatacenterBroker createBroker(String customerName, String brokerAlias);

    /** 
     * Gets an instance of broker policy based on its alias.
     *
     * @param   alias   the alias of the broker policy.
     * @return          an instance of the type with the given alias.
     * @since           1.0
     */      
    public static BrokerPolicy getInstance(String alias) {
        if(alias.equals("Round robin")) return BrokerPolicy.ROUND_ROBIN;
        else return BrokerPolicy.EXTENSION;
    }

    /** 
     * Gets all active broker policies aliases.
     *
     * @return  an array of strings containing all active broker
     *          policies aliases.
     * @since   1.0
     */      
    public static String[] getBrokerPoliciesNames() {
        String[] nativePolicies = new String[] {"Round robin"};
        List<String> extensionPolicies = ExtensionsLoader.getExtensionsAliasesByType("Broker");        
        extensionPolicies.addAll(Arrays.asList(nativePolicies));
        
        return extensionPolicies.toArray(new String[0]);
    }
}
