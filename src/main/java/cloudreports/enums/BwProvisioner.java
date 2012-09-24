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
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;

/**
 * Defines native types of bandwidth provisioners and implements an extension type
 * to support user-implemented new types.
 * 
 * @author      Thiago T. Sá
 * @since       1.0
 */
public enum BwProvisioner {
    
    /** The simple bandwidth provisioner.
     * Its {@link #getProvisioner(int, java.lang.String)} method returns an 
     * instance of CloudSim's BwProvisionerSimple class.
     */      
    SIMPLE {
        @Override
        public org.cloudbus.cloudsim.provisioners.BwProvisioner getProvisioner(long num, String provisionerAlias) {
            return new BwProvisionerSimple(num);
        }
    },
    
    /** The extension type. 
     *  It is used for all user-implemented new types.
     */     
    EXTENSION {
        @Override
        public org.cloudbus.cloudsim.provisioners.BwProvisioner getProvisioner(long num, String provisionerAlias) {
            try {
                Class<?>[] types = new Class<?>[]{long.class};
                Object[] arguments = new Object[]{num};
                return (org.cloudbus.cloudsim.provisioners.BwProvisioner) ExtensionsLoader.getExtension("BwProvisioner", provisionerAlias, types, arguments);
            } catch (Exception e) {
                return null;
            }
        }
    };

    /** 
     * An abstract method to be implemented by every {@link BwProvisioner}.
     *
     * @param   num                 the overall amount of bandwidth available
     *                              in the host.
     * @param   provisionerAlias    the alias of the bandwidth provisioner.
     * @return                      a CloudSim's BwProvisioner subtype.
     * @since                       1.0
     */     
    public abstract org.cloudbus.cloudsim.provisioners.BwProvisioner getProvisioner(long num, String provisionerAlias);

    /** 
     * Gets an instance of bandwidth provisioner based on its alias.
     *
     * @param   provisionerAlias    the alias of the bandwidth provisioner.
     * @return                      an instance of the type with the given alias.
     * @since                       1.0
     */         
    public static BwProvisioner getInstance(String provisionerAlias) {
        if (provisionerAlias.equals("Simple")) return BwProvisioner.SIMPLE;
        else return BwProvisioner.EXTENSION;
    }

    /** 
     * Gets all active bandwidth provisioners aliases.
     *
     * @return  an array of strings containing all active bandwidth
     *          provisioners aliases.
     * @since   1.0
     */        
    public static String[] getBwProvisionerNames() {
        String[] nativeProvisioners = new String[]{"Simple"};
        List<String> extensionProvisioners = ExtensionsLoader.getExtensionsAliasesByType("BwProvisioner");
        extensionProvisioners.addAll(Arrays.asList(nativeProvisioners));

        return extensionProvisioners.toArray(new String[0]);
    }
}
