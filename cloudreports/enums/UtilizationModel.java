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
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.UtilizationModelStochastic;

/**
 * Defines native types of utilization models and implements an extension type
 * to support user-implemented new types.
 * 
 * @author      Thiago T. Sá
 * @since       1.0
 */
public enum UtilizationModel implements Serializable{

    /** The full utilization model.
     *  Its {@link #getModel()} method returns an instance of
     *  CloudSim's UtilizationModelFull class.
     */        
    FULL {
        @Override
        public org.cloudbus.cloudsim.UtilizationModel getModel() {
            return new UtilizationModelFull();
        }
    },

    /** The stochastic utilization model.
     *  Its {@link #getModel()} method returns an instance of
     *  CloudSim's UtilizationModelStochastic class.
     */     
    STOCHASTIC {
        @Override
        public org.cloudbus.cloudsim.UtilizationModel getModel() {
            return new UtilizationModelStochastic();
        }
    },
    
    /** The extension type. 
     *  It is used for all user-implemented new types.
     */      
    EXTENSION {
        @Override
        public org.cloudbus.cloudsim.UtilizationModel getModel() {
            return new UtilizationModelStochastic();
        }
    };
    
    /** 
     * An abstract method to be implemented by every {@link UtilizationModel}.
     *
     * @return                  a CloudSim's UtilizationModel subtype.
     * @since                   1.0
     */      
    public abstract org.cloudbus.cloudsim.UtilizationModel getModel();

    /** 
     * Gets an instance of utilization model based on its alias.
     *
     * @param   modelAlias  the alias of the utilization model.
     * @return              an instance of the type with the given alias.
     * @since               1.0
     */       
    public static UtilizationModel getInstance(String modelAlias) {
        if(modelAlias.equals("Full")) return UtilizationModel.FULL;
        else if(modelAlias.equals("Stochastic")) return UtilizationModel.STOCHASTIC;
        else return UtilizationModel.EXTENSION;
    }
    
    /** 
     * Gets all active utilization model aliases.
     *
     * @return  an array of strings containing all active utilization model
     *          aliases.
     * @since   1.0
     */      
    public static String[] getUtilizationModelNames() {
        String[] nativeModels = new String[]{"Full", "Stochastic"};
        List<String> extensionModels = ExtensionsLoader.getExtensionsAliasesByBaseClassname("org.cloudbus.cloudsim.UtilizationModel");
        extensionModels.addAll(Arrays.asList(nativeModels));

        return extensionModels.toArray(new String[0]);
    }

}
