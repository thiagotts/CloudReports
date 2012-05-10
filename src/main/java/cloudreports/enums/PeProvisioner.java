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
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;

/**
 * Defines native types of processing elements provisioners and implements an
 * extension type to support user-implemented new types.
 * 
 * @author      Thiago T. Sá
 * @since       1.0
 */
public enum PeProvisioner {
    
    /** The simple provisioner.
     *  Its {@link #getProvisioner(double, java.lang.String)} method 
     *  returns an instance of CloudSim's PeProvisionerSimple class.
     */      
    SIMPLE {
        @Override
        public org.cloudbus.cloudsim.provisioners.PeProvisioner getProvisioner(double mips, String provisionerAlias) {
            return new PeProvisionerSimple(mips);
        }
    },    
    
    /** The extension type. 
     *  It is used for all user-implemented new types.
     */      
    EXTENSION {
        @Override
        public org.cloudbus.cloudsim.provisioners.PeProvisioner getProvisioner(double mips, String provisionerAlias) {
            try {
                Class<?>[] types = new Class<?>[]{double.class};
                Object[] arguments = new Object[]{mips};
                return (org.cloudbus.cloudsim.provisioners.PeProvisioner) ExtensionsLoader.getExtension("PeProvisioner", provisionerAlias, types, arguments);
            } catch (Exception e) {
                return null;
            }
        }
    };

    /** 
     * An abstract method to be implemented by every {@link PeProvisioner}.
     *
     * @param   mips                the amount of mips per processing element.
     * @param   provisionerAlias    the alias of the PE provisioner.
     * @return                      a CloudSim's PeProvisioner subtype.
     * @since                       1.0
     */       
    public abstract org.cloudbus.cloudsim.provisioners.PeProvisioner getProvisioner(double mips, String provisionerAlias);

    /** 
     * Gets an instance of processing elements provisioner based on its alias.
     *
     * @param   provisionerAlias    the alias of the provisioner.
     * @return                      an instance of the type with the given alias.
     * @since                       1.0
     */      
    public static PeProvisioner getInstance(String provisionerAlias) {
        if (provisionerAlias.equals("Simple")) return PeProvisioner.SIMPLE;
        else return PeProvisioner.EXTENSION;
    }

    /** 
     * Gets all active processing elements provisioner aliases.
     *
     * @return  an array of strings containing all active processing elements
     *          provisioners aliases.
     * @since   1.0
     */         
    public static String[] getPeProvisionerNames() {
        String[] nativeProvisioners = new String[]{"Simple"};
        List<String> extensionProvisioners = ExtensionsLoader.getExtensionsAliasesByType("PeProvisioner");
        extensionProvisioners.addAll(Arrays.asList(nativeProvisioners));

        return extensionProvisioners.toArray(new String[0]);
    }    
}
