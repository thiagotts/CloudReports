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
import java.util.Arrays;
import java.util.List;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

/**
 * Defines native types of RAM provisioners and implements an extension type
 * to support user-implemented new types.
 * 
 * @see         cloudreports.extensions.brokers.Broker
 * @author      Thiago T. Sá
 * @since       1.0
 */
public enum RamProvisioner {

    /** The simple provisioner.
     *  Its {@link #getProvisioner(int, java.lang.String)} method 
     *  returns an instance of CloudSim's RamProvisionerSimple class.
     */     
    SIMPLE {
        @Override
        public org.cloudbus.cloudsim.provisioners.RamProvisioner getProvisioner(int avaiableRam, String provisionerAlias) {
            return new RamProvisionerSimple(avaiableRam);
        }
    },
    
    /** The extension type. 
     *  It is used for all user-implemented new types.
     */       
    EXTENSION {
        @Override
        public org.cloudbus.cloudsim.provisioners.RamProvisioner getProvisioner(int availableRam, String provisionerAlias) {
            try {
                Class<?>[] types = new Class<?>[]{int.class};
                Object[] arguments = new Object[]{availableRam};
                return (org.cloudbus.cloudsim.provisioners.RamProvisioner) ExtensionsLoader.getExtension("RamProvisioner", provisionerAlias, types, arguments);
            } catch (Exception e) {
                return null;
            }
        }
    };
    
    /** 
     * An abstract method to be implemented by every {@link RamProvisioner}.
     *
     * @param   availableRam        the amount of available RAM.
     * @param   provisionerAlias    the alias of the provisioner.
     * @return                      a CloudSim's RamProvisioner subtype.
     * @since                       1.0
     */      
    public abstract org.cloudbus.cloudsim.provisioners.RamProvisioner getProvisioner(int availableRam, String provisionerAlias);
    
    /** 
     * Gets an instance of RAM provisioner based on its alias.
     *
     * @param   provisionerAlias    the alias of the provisioner.
     * @return                      an instance of the type with the given alias.
     * @since                       1.0
     */       
    public static RamProvisioner getInstance(String provisionerAlias) {
        if(provisionerAlias.equals("Simple")) return RamProvisioner.SIMPLE;
        else return RamProvisioner.EXTENSION;
    }

    /** 
     * Gets all active RAM provisioners aliases.
     *
     * @return  an array of strings containing all active RAM provisioners
     *          aliases.
     * @since   1.0
     */     
    public static String[] getRamProvisionerNames() {
        String[] nativeProvisioners = new String[]{"Simple"};
        List<String> extensionProvisioners = ExtensionsLoader.getExtensionsAliasesByType("RamProvisioner");
        extensionProvisioners.addAll(Arrays.asList(nativeProvisioners));

        return extensionProvisioners.toArray(new String[0]);
    }
}
