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
import org.cloudbus.cloudsim.power.models.PowerModelCubic;
import org.cloudbus.cloudsim.power.models.PowerModelLinear;
import org.cloudbus.cloudsim.power.models.PowerModelSqrt;
import org.cloudbus.cloudsim.power.models.PowerModelSquare;

/**
 * Defines native types of power models and implements an extension type
 * to support user-implemented new types.
 * 
 * @author      Thiago T. Sá
 * @since       1.0
 */
public enum PowerModel implements Serializable{

    /** The linear power model.
     *  Its {@link #getModel(double, double, java.lang.String)} method 
     *  returns an instance of CloudSim's PowerModelLinear class.
     */      
    LINEAR {
        @Override
        public org.cloudbus.cloudsim.power.models.PowerModel getModel(double maxPower, double staticPowerPercent, String modelAlias) {
            return new PowerModelLinear(maxPower, staticPowerPercent);
        }        
    },

    /** The cubic power model.
     *  Its {@link #getModel(double, double, java.lang.String)} method 
     *  returns an instance of CloudSim's PowerModelCubic class.
     */      
    CUBIC {
        @Override
        public org.cloudbus.cloudsim.power.models.PowerModel getModel(double maxPower, double staticPowerPercent, String modelAlias) {
            return new PowerModelCubic(maxPower, staticPowerPercent);
        }        
    },

    /** The square power model.
     *  Its {@link #getModel(double, double, java.lang.String)} method 
     *  returns an instance of CloudSim's PowerModelSquare class.
     */       
    SQUARE {
        @Override
        public org.cloudbus.cloudsim.power.models.PowerModel getModel(double maxPower, double staticPowerPercent, String modelAlias) {
            return new PowerModelSquare(maxPower, staticPowerPercent);
        }        
    },

    /** The square-root power model.
     *  Its {@link #getModel(double, double, java.lang.String)} method 
     *  returns an instance of CloudSim's PowerModelSqrt class.
     */        
    SQUARE_ROOT {
        @Override
        public org.cloudbus.cloudsim.power.models.PowerModel getModel(double maxPower, double staticPowerPercent, String modelAlias) {
            return new PowerModelSqrt(maxPower, staticPowerPercent);
        }        
    },
    
    /** The extension type. 
     *  It is used for all user-implemented new types.
     */        
    EXTENSION {
        @Override
        public org.cloudbus.cloudsim.power.models.PowerModel getModel(double maxPower, double staticPowerPercent, String modelAlias) {
            try {
                Class<?>[] types = new Class<?>[]{double.class, double.class};
                Object[] arguments = new Object[]{maxPower, staticPowerPercent};
                return (org.cloudbus.cloudsim.power.models.PowerModel) ExtensionsLoader.getExtension("PowerModel", modelAlias, types, arguments);
            } catch (Exception e) {
                return null;
            }
        }
    };
    
    /** 
     * An abstract method to be implemented by every {@link PowerModel}.
     *
     * @param   maxPower            the maximum power.
     * @param   staticPowerPercent  the static power percent usage.
     * @param   modelAlias          the alias of the power model.
     * @return                      a CloudSim's PowerModel subtype.
     * @since                       1.0
     */       
    public abstract org.cloudbus.cloudsim.power.models.PowerModel getModel(double maxPower, double staticPowerPercent, String modelAlias);

    /** 
     * Gets an instance of power model based on its alias.
     *
     * @param   modelAlias  the alias of the power model.
     * @return              an instance of the type with the given alias.
     * @since               1.0
     */      
    public static PowerModel getInstance(String modelAlias) {
        if(modelAlias.equals("Linear")) return PowerModel.LINEAR;
        else if(modelAlias.equals("Square root")) return PowerModel.SQUARE_ROOT;
        else if (modelAlias.equals("Square")) return PowerModel.SQUARE;
        else if(modelAlias.equals("Cubic")) return PowerModel.CUBIC;
        else return PowerModel.EXTENSION;
    }
    
    /** 
     * Gets all active power model aliases.
     *
     * @return  an array of strings containing all active power model
     *          aliases.
     * @since   1.0
     */     
    public static String[] getPowerModelNames() {
        String[] nativeModels = new String[]{"Linear", "Square root", "Square", "Cubic"};
        List<String> extensionModels = ExtensionsLoader.getExtensionsAliasesByType("PowerModel");
        extensionModels.addAll(Arrays.asList(nativeModels));

        return extensionModels.toArray(new String[0]);
    }

}
