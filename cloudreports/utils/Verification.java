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

import cloudreports.dao.CustomerRegistryDAO;
import cloudreports.dao.DatacenterRegistryDAO;
import cloudreports.models.CustomerRegistry;
import cloudreports.models.DatacenterRegistry;
import cloudreports.models.HostRegistry;
import cloudreports.models.VirtualMachineRegistry;
import java.util.List;

/**
 * Provides utility methods that verifies the viability of simulations.
 * 
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class Verification {

    /** 
     * Verifies the viability of a simulation.
     * 
     * @return  <code>true</code> if the simulation is practicable;
     *          <code>false</code> otherwise.
     * @since   1.0
     */       
    public static boolean verifyVMsDeploymentViability() {

        CustomerRegistryDAO crDAO = new CustomerRegistryDAO();
        for(CustomerRegistry cr : crDAO.getListOfCustomers()) {

            List<DatacenterRegistry> dcList = getDatacentersList();

            VM:
            for(VirtualMachineRegistry vmr : cr.getVmList()) {

                for(DatacenterRegistry dcr: dcList){
                    for(HostRegistry hr : dcr.getHostList()) {
                        if(hr.canRunVM(vmr)) continue VM;
                    }
                }

                return false;
            }
        }

        return true;
    }

    /** 
     * Gets a list of all simulated datacenters.
     * 
     * @return  a list of all simulated datacenters. 
     * @since   1.0
     */          
    private static List<DatacenterRegistry> getDatacentersList() {
        DatacenterRegistryDAO drDAO = new DatacenterRegistryDAO();
        return drDAO.getListOfDatacenters();
    }

}
