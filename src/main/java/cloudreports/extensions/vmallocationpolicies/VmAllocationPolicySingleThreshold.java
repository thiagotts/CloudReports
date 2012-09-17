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

import cloudreports.business.SettingBusiness;
import cloudreports.enums.AllocationPolicy;
import cloudreports.models.Migration;
import cloudreports.simulation.Simulation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerHost;

/**
 * A virtual machine allocation policy based on a single utilization threshold.
 * 
 * @see         VmAllocationPolicy
 * @see         AllocationPolicy#SINGLE_THRESHOLD
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class VmAllocationPolicySingleThreshold extends VmAllocationPolicySimple implements VmAllocationPolicyExtensible {

    private double upperUtilizationThreshold;
    
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
        
        super(list);
        this.upperUtilizationThreshold = utilizationThreshold;
    }

    /** 
     * Optimizes the allocation of virtual machines.
     * It distributes virtual machines if there is any overused host and then 
     * consolidates the allocation.
     *
     * @param   vmList  the list of virtual machines.
     * @since           1.0
     */       
    @Override
    public List<Migration> getListOfMigrationsToBeExecuted(List<? extends Vm> vmList) {
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
    public void distributeVms(List<Migration> migrationList, List<PowerHost> overusedHosts) {
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
    public void consolidateVms(List<Migration> migrationList) {
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

    @Override
    public double getUpperUtilizationThreshold() {
        return this.upperUtilizationThreshold;
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
    
    /**
     * Allocates a host for a given VM.
     *
     * @param vm a virtual machine.
     *
     * @return      <code>true</code> if the host could be allocated;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean allocateHostForVm(Vm vm) {
        PowerHost allocatedHost = findHostForVm(vm);
        if (allocatedHost != null && allocatedHost.vmCreate(vm)) { //if vm has been succesfully created in the host
            getVmTable().put(vm.getUid(), allocatedHost);
            if (!Log.isDisabled()) {
                Log.print(String.format("%.2f: VM #" + vm.getId() + " has been allocated to the host #" + allocatedHost.getId() + "\n", CloudSim.clock()));
            }
            return true;
        }
        return false;
    }

    /**
     * Finds a host to allocate for the VM.
     *
     * @param vm the virtual machine to be allocated.
     * @return the chosen host.
     */
    public PowerHost findHostForVm(Vm vm) {
        double minPower = Double.MAX_VALUE;
        PowerHost allocatedHost = null;

        for (PowerHost host : this.<PowerHost>getHostList()) {
            if (host.isSuitableForVm(vm)) {
                double maxUtilization = getMaxUtilizationAfterAllocation(host, vm);
                if ((!vm.isBeingInstantiated() && maxUtilization > getUpperUtilizationThreshold()) || (vm.isBeingInstantiated() && maxUtilization > 1.0)) {
                    continue;
                }
                try {
                    double powerAfterAllocation = getPowerAfterAllocation(host, vm);
                    if (powerAfterAllocation != -1) {
                        double powerDiff = powerAfterAllocation - host.getPower();
                        if (powerDiff < minPower) {
                            minPower = powerDiff;
                            allocatedHost = host;
                        }
                    }
                } catch (Exception e) {
                }
            }
        }

        return allocatedHost;
    }

    /**
     * Gets the power after allocation.
     *
     * @param host the host.
     * @param vm the virtual machine.
     * @return the power usage after allocation of the virtual machine.
     */
    protected double getPowerAfterAllocation(PowerHost host, Vm vm) {
        List<Double> allocatedMipsForVm = null;
        PowerHost allocatedHost = (PowerHost) vm.getHost();

        if (allocatedHost != null) {
            allocatedMipsForVm = allocatedHost.getAllocatedMipsForVm(vm);
        }

        if (!host.allocatePesForVm(vm, vm.getCurrentRequestedMips())) {
            return -1;
        }

        double power = host.getPower();

        host.deallocatePesForVm(vm);

        if (allocatedHost != null && allocatedMipsForVm != null) {
            vm.getHost().allocatePesForVm(vm, allocatedMipsForVm);
        }

        return power;
    }

    /**
     * Gets the maximum utilization after allocation.
     *
     * @param host the host.
     * @param vm the virtual machine.
     * @return the maximum utilization after allocation of the virtual machine.
     */
    protected double getMaxUtilizationAfterAllocation(PowerHost host, Vm vm) {
        List<Double> allocatedMipsForVm = null;
        PowerHost allocatedHost = (PowerHost) vm.getHost();

        if (allocatedHost != null) {
            allocatedMipsForVm = vm.getHost().getAllocatedMipsForVm(vm);
        }

        if (!host.allocatePesForVm(vm, vm.getCurrentRequestedMips())) {
            return -1;
        }

        double maxUtilization = host.getMaxUtilizationAmongVmsPes(vm);

        host.deallocatePesForVm(vm);

        if (allocatedHost != null && allocatedMipsForVm != null) {
            vm.getHost().allocatePesForVm(vm, allocatedMipsForVm);
        }

        return maxUtilization;
    }

    /**
     * Deallocates a virtual machine in a host.
     *
     * @param vm the virtual machine to be deallocated.
     */
    @Override
    public void deallocateHostForVm(Vm vm) {
        if (getVmTable().containsKey(vm.getUid())) {
            PowerHost host = (PowerHost) getVmTable().remove(vm.getUid());
            if (host != null) {
                host.vmDestroy(vm);
            }
        }
    }

    /**
     * Sorts hosts by power consumption.
     *
     * @param hostList a list of hosts to be sorted.
     * @since 1.0
     */
    protected <T extends PowerHost> void sortByPowerConsumption(List<T> hostList) {
        Collections.sort(hostList, new Comparator<T>() {
            @Override
            public int compare(T a, T b) throws ClassCastException {
                Double aUtilization = a.getPower();
                Double bUtilization = b.getPower();
                return bUtilization.compareTo(aUtilization);
            }
        });
    }

    /**
     * Calculates a list of migrations needed to distribute virtual machines
     * among hosts.
     *
     * @param migrationList a list o migrations to be calculated.
     * @param sourceHost the host from which virtual machines will be taken to
     * be reallocated in other hosts.
     * @param targetHosts a list of target hosts.
     * @param upperUtilizationThreshold the upper utilization threshold.
     * @since 1.0
     */
    protected void getDistributingMigrationList(List<Migration> migrationList, PowerHost sourceHost, List<PowerHost> targetHosts, double upperUtilizationThreshold) {
        //Map of used resources on target hosts
        //Keys: the target hosts IDs
        //Values: a list with the amount of used CPU and RAM on the target host
        HashMap<Integer, List<Double>> resourcesMap = new HashMap<Integer, List<Double>>();
        for (PowerHost host : targetHosts) {
            List<Double> resourcesList = new ArrayList<Double>();
            resourcesList.add(getHostCpuUtilization(host));
            resourcesList.add(getHostRamUtilization(host));
            resourcesMap.put(host.getId(), resourcesList);
        }

        double cpuUtilization = getHostCpuUtilization(sourceHost);
        double ramUtilization = getHostRamUtilization(sourceHost);
        int currentSimulation = SettingBusiness.getCurrentSimulation();

        VMS_LOOP:
        for (Vm vm : sourceHost.getVmList()) {
            if (vm.isInMigration()) {
                continue;
            }

            HOSTS_LOOP:
            for (PowerHost targetHost : targetHosts) {
                List<Double> hostResources = resourcesMap.get(targetHost.getId());
                double vmTotalMips = vm.getNumberOfPes() * vm.getMips();
                if ((hostResources.get(0) + vmTotalMips) / targetHost.getTotalMips() < upperUtilizationThreshold
                        && (hostResources.get(1) + vm.getRam()) / targetHost.getRam() < upperUtilizationThreshold) {
                    //There's enough resources and no threshold violation, so add the migration to the list.
                    //Step 1: add the resources utilization of the target host
                    hostResources.set(0, hostResources.get(0) + vmTotalMips);
                    hostResources.set(1, hostResources.get(1) + vm.getRam());
                    //Step 2: subtract the utilization of the source host
                    ramUtilization -= vm.getRam();
                    cpuUtilization -= sourceHost.getTotalAllocatedMipsForVm(vm);
                    //Step 3: add the entry on the migration map
                    Migration migration = new Migration(this, targetHost, sourceHost, vm, currentSimulation);
                    migrationList.add(migration);

                    //If the source host is not overused anymore, so finish distribution.
                    if ((ramUtilization / sourceHost.getRam()) < upperUtilizationThreshold
                            && (cpuUtilization / sourceHost.getTotalMips()) < upperUtilizationThreshold) {
                        break VMS_LOOP;
                    } else {
                        break HOSTS_LOOP;
                    }
                }
            }
        }
    }

    /**
     * Calculates a list of migrations needed to consolidate the allocation of
     * virtual machines.
     *
     * @param migrationList a list o migrations to be calculated.
     * @param sourceHost the host from which virtual machines will be taken to
     * be reallocated in other hosts.
     * @param targetHosts a list of target hosts.
     * @param hostsToBeTurnedOffIds a list of host ids to be turned off.
     * @param upperUtilizationThreshold the upper utilization threshold.
     * @since 1.0
     */
    protected void getConsolidatingMigrationList(List<Migration> migrationList,
            PowerHost sourceHost,
            List<PowerHost> targetHosts,
            List<Integer> hostsToBeTurnedOffIds,
            double upperUtilizationThreshold) {
        //Map of resources
        //Keys: each one of the target hosts ID
        //Values: a list with the amount of available CPU and RAM of the host
        HashMap<Integer, List<Double>> resourcesMap = new HashMap<Integer, List<Double>>();
        for (PowerHost host : targetHosts) {
            List<Double> resourcesList = new ArrayList<Double>();
            resourcesList.add(getHostCpuUtilization(host));
            resourcesList.add(getHostRamUtilization(host));
            resourcesMap.put(host.getId(), resourcesList);
        }

        double cpuUtilization = getHostCpuUtilization(sourceHost);
        double ramUtilization = getHostRamUtilization(sourceHost);
        int currentSimulation = SettingBusiness.getCurrentSimulation();

        for (Vm vm : sourceHost.getVmList()) {
            if (vm.isInMigration()) {
                continue;
            }

            HOSTS_LOOP:
            for (PowerHost targetHost : targetHosts) {
                if (targetHost.getId() == sourceHost.getId()) {
                    continue HOSTS_LOOP;
                }

                for (int id : hostsToBeTurnedOffIds) {
                    if (targetHost.getId() == id) {
                        continue HOSTS_LOOP;
                    }
                }

                List<Double> hostResources = resourcesMap.get(targetHost.getId());
                double vmTotalMips = vm.getNumberOfPes() * vm.getMips();
                if ((hostResources.get(0) + vmTotalMips) / targetHost.getTotalMips() < upperUtilizationThreshold
                        && (hostResources.get(1) + vm.getRam()) / targetHost.getRam() < upperUtilizationThreshold) {
                    //There's enough resources and no threshold violation, so add the migration to the list.
                    //Step 1: subtract the resources of the target host
                    hostResources.set(0, hostResources.get(0) + vmTotalMips);
                    hostResources.set(1, hostResources.get(1) + vm.getRam());
                    //Step 2: subtract the utilization of the source host
                    ramUtilization -= vm.getRam();
                    cpuUtilization -= sourceHost.getTotalAllocatedMipsForVm(vm);
                    //Step 3: add the entry to the migration map
                    Migration migration = new Migration(this, targetHost, sourceHost, vm, currentSimulation);
                    migrationList.add(migration);

                    break HOSTS_LOOP;
                }
            }
        }

        //The consolidation will only occur if all vms have been migrated
        if (ramUtilization > 0 || cpuUtilization > 0) {
            migrationList.clear();
        }
    }

    /**
     * Gets the CPU utilization of a host.
     *
     * @param host the host to be analyzed.
     * @since 1.0
     */
    protected static double getHostCpuUtilization(PowerHost host) {
        double cpuUtilization = host.getUtilizationOfCpuMips();

        //Disconsider the resources of vms migrating out
        for (Vm vm : host.getVmList()) {
            if (vm.isInMigration()) {
                cpuUtilization -= host.getTotalAllocatedMipsForVm(vm);
            }
        }

        //Include resources of vms migrating in
        for (Vm vm : host.getVmsMigratingIn()) {
            if (vm.isInMigration()) {
                cpuUtilization += vm.getCurrentRequestedTotalMips();
            }
        }

        return cpuUtilization;
    }

    /**
     * Gets the RAM utilization of a host.
     *
     * @param host the host to be analyzed.
     * @since 1.0
     */
    protected static double getHostRamUtilization(PowerHost host) {
        double ramUtilization = host.getUtilizationOfRam();

        //Disconsider the resources of vms migrating out
        for (Vm vm : host.getVmList()) {
            if (vm.isInMigration()) {
                ramUtilization -= vm.getRam();
            }
        }

        //Include resources of vms migrating in
        for (Vm vm : host.getVmsMigratingIn()) {
            if (vm.isInMigration()) {
                ramUtilization += vm.getRam();
            }
        }

        return ramUtilization;
    }

    /**
     * Gets the CPU utilization rate of a host.
     *
     * @param host the host to be analyzed.
     * @since 1.0
     */
    public static double getHostCpuUtilizationRate(PowerHost host) {
        double cpuUtilizationRate = getHostCpuUtilization(host) / host.getTotalMips();

        List<Double> previousValuesCpu = Simulation.getDataCollector().getMonitoredUsedResources(host.getDatacenter().getName(), host.getId(), "CPU");
        for (Double previousValue : previousValuesCpu) {
            cpuUtilizationRate += (previousValue / 100);
        }
        cpuUtilizationRate /= (previousValuesCpu.size() + 1);

        return cpuUtilizationRate;
    }

    /**
     * Gets the RAM utilization rate of a host.
     *
     * @param host the host to be analyzed.
     * @since 1.0
     */
    public static double getHostRamUtilizationRate(PowerHost host) {
        double ramUtilizationRate = getHostRamUtilization(host) / host.getRam();

        List<Double> previousValuesRam = Simulation.getDataCollector().getMonitoredUsedResources(host.getDatacenter().getName(), host.getId(), "RAM");
        for (Double previousValue : previousValuesRam) {
            ramUtilizationRate += (previousValue / 100);
        }
        ramUtilizationRate /= (previousValuesRam.size() + 1);

        return ramUtilizationRate;
    }

}
