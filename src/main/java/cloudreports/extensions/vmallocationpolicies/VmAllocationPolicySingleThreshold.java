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

package cloudreports.extensions.vmallocationpolicies;

import cloudreports.enums.AllocationPolicy;
import cloudreports.models.Migration;
import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.power.PowerHost;

/**
 * A virtual machine allocation policy based on a single utilization threshold.
 * 
 * @see         VmAllocationPolicy
 * @see         AllocationPolicy#SINGLE_THRESHOLD
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class VmAllocationPolicySingleThreshold extends VmAllocationPolicy {

    /** 
     * Initializes a new instance of this class with the given list of hosts
     * and utilization threshold value.
     *
     * @param   list                    the list of hosts.
     * @param   utilizationThreshold    the utilization threshold value.
     * @since                           1.0
     */         
    public VmAllocationPolicySingleThreshold(List<? extends PowerHost> list,
                                                    double utilizationThreshold) {
        
        super(list, utilizationThreshold);
    }

    /** 
     * Optimizes the allocation of virtual machines.
     * It distributes virtual machines if there is any overused host and then 
     * consolidates the allocation.
     *
     * @param   vmList  the list of virtual machines.
     * @since           1.0
     */       
    public List<Migration> optimizeVmsAllocation(List<? extends Vm> vmList) {
        List<Migration> migrationList = new ArrayList<Migration>();
        if (vmList.isEmpty()) {
            return migrationList;
        }
        
        //Verify the existence of overused hosts
        List<PowerHost> overusedHosts = getOverusedHosts();
        //If there is any overused host, then distribute vms
        if(!overusedHosts.isEmpty()) {
            distributeVms(migrationList, overusedHosts);
        }
        
        consolidateVms(migrationList);

        return migrationList;
    }
    
    /** 
     * Distributes virtual machines among hosts.
     *
     * @param   migrationList   the list of migrations to be executed.
     * @param   overusedHosts   the list of overused hosts.
     * @since                   1.0
     */    
    @Override
    protected void distributeVms(List<Migration> migrationList, List<PowerHost> overusedHosts) {
        List<PowerHost> targetHosts = getNotOverusedHosts();
        
        if(!targetHosts.isEmpty()) {
            sortByPowerConsumption(overusedHosts);
            
            for(PowerHost host : overusedHosts) {
                getDistributingMigrationList(migrationList, host, targetHosts, getUpperUtilizationThreshold());
            }
            
            for(Migration migration : migrationList) {
                migration.setDescription("Distribution");
            }
        }
    }
    
    /** 
     * Consolidates the allocation of virtual machines.
     *
     * @param   migrationList   the list of migrations to be executed.
     * @since                   1.0
     */      
    @Override
    protected void consolidateVms(List<Migration> migrationList) {
        List<PowerHost> activeHosts = getActiveHosts();        
        List<PowerHost> targetHosts = activeHosts;
        
        List<PowerHost> sourceHosts = activeHosts;
        List<Integer> hostsToBeTurnedOffIds = new ArrayList<Integer>();
        
        for (PowerHost sourceHost : sourceHosts) {
            List<Migration> newMigrationList = new ArrayList<Migration>();
            getConsolidatingMigrationList(newMigrationList, sourceHost, targetHosts, hostsToBeTurnedOffIds, getUpperUtilizationThreshold());
            migrationList.addAll(newMigrationList);
            hostsToBeTurnedOffIds.add(sourceHost.getId());
        }
        
        for (Migration migration : migrationList) {
            if(migration.getDescription() == null) {
                migration.setDescription("Consolidation");
            }
        }
    }

    /** 
     * Gets a list of overused hosts.
     *
     * @return  the list of overused hosts.
     * @since   1.0
     */      
    public List<PowerHost> getOverusedHosts() {
        List<PowerHost> overusedHosts = new ArrayList<PowerHost>();
        for (PowerHost host : this.<PowerHost>getHostList()) {            
            double cpuUtilizationRate = getHostCpuUtilizationRate(host);
            double ramUtilizationRate = getHostRamUtilizationRate(host);
            
            if (cpuUtilizationRate >= getUpperUtilizationThreshold()
                    || ramUtilizationRate >= getUpperUtilizationThreshold()) {
                overusedHosts.add(host);
            }
        }
        return overusedHosts;
    }

    /** 
     * Gets a list of not overused hosts.
     *
     * @return  the list of not overused hosts.
     * @since   1.0
     */       
    public List<PowerHost> getNotOverusedHosts() {
        List<PowerHost> notOverusedHosts = new ArrayList<PowerHost>();
        for (PowerHost host : this.<PowerHost>getHostList()) {
            double cpuUtilizationRate = getHostCpuUtilizationRate(host);
            double ramUtilizationRate = getHostRamUtilizationRate(host);
            
            if ( cpuUtilizationRate < getUpperUtilizationThreshold()
                    && ramUtilizationRate  < getUpperUtilizationThreshold()) {
                notOverusedHosts.add(host);
            }
        }
        return notOverusedHosts;
    }
    
    /** 
     * Gets a list active hosts.
     *
     * @return  the list of active hosts.
     * @since   1.0
     */       
    public List<PowerHost> getActiveHosts() {
        List<PowerHost> activeHosts = new ArrayList<PowerHost>();
        for (PowerHost host : this.<PowerHost>getHostList()) {
            double cpuUtilization = host.getUtilizationOfCpuMips()/host.getTotalMips();
            double ramUtilization = host.getUtilizationOfRam()/host.getRam();
           
            if ( cpuUtilization > 0 && ramUtilization > 0) {
                activeHosts.add(host);
            }
        }
        return activeHosts;
    }

}
