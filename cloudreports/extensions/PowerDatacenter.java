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

package cloudreports.extensions;

import cloudreports.models.Migration;
import cloudreports.simulation.Simulation;
import java.util.List;
import java.util.Map;
import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.predicates.PredicateType;
import org.cloudbus.cloudsim.power.PowerHost;

/**
 * This is a subtype of CloudSim's PowerDatacenter class that implements some
 * functionalities needed to collect simulation data and generate reports.
 * 
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class PowerDatacenter extends org.cloudbus.cloudsim.power.PowerDatacenter {
    
    /** The period between data collections. */
    private double monitoringInterval;
    
    /** The last time data was collected. */
    private double lastMonitoringTime;

    /** 
     * A new constructor that sets the monitoring interval and the last monitoring
     * time and uses its base class constructor to initialize the power datacenter.
     *
     * @param   name                the name of the datacenter.
     * @param   characteristics     the characteristics of the datacenter.
     * @param   vmAllocationPolicy  the virtual machine allocation policy.
     * @param   storageList         a list of storage systems.
     * @param   schedulingInterval  the period used for scheduling.
     * @param   monitoringInterval  the period between data collection.
     * @since                       1.0
     */       
    public PowerDatacenter(String name, DatacenterCharacteristics characteristics,
                           VmAllocationPolicy vmAllocationPolicy, List<Storage> storageList,
                           double schedulingInterval, double monitoringInterval) throws Exception {

        super(name,characteristics,vmAllocationPolicy,storageList, schedulingInterval);
        this.monitoringInterval = monitoringInterval;
        this.lastMonitoringTime = 0;
    }

    /** 
     * An overridden version of the method that adds data collection 
     * functionalities.
     *
     * @since                   1.0
     */       
    @Override
    protected void updateCloudletProcessing() {
        if (getCloudletSubmitted() == -1 || getCloudletSubmitted() == CloudSim.clock()) {
            CloudSim.cancelAll(getId(), new PredicateType(CloudSimTags.VM_DATACENTER_EVENT));
            schedule(getId(), getSchedulingInterval(), CloudSimTags.VM_DATACENTER_EVENT);
            return;
        }
        double currentTime = CloudSim.clock();
        double timeframePower = 0.0;

        // if some time passed since last processing
        if (currentTime > getLastProcessTime()) {
            double timeDiff = currentTime - getLastProcessTime();
            double minTime = Double.MAX_VALUE;

            Log.printLine("\n");

            for (PowerHost host : this.<PowerHost>getHostList()) {
                double hostPower = 0.0;
                if (host.getUtilizationOfCpu() > 0) {
                    try {
                        hostPower = host.getPower() * timeDiff;
                        timeframePower += hostPower;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            for (PowerHost host : this.<PowerHost>getHostList()) {
                double time = host.updateVmsProcessing(currentTime); // inform VMs to update processing
                if (time < minTime) {
                    minTime = time;
                }
            }
            setPower(getPower() + timeframePower);
            
            if((currentTime - this.lastMonitoringTime) >= this.getMonitoringInterval()) {
                this.lastMonitoringTime = currentTime;

                if (!isDisableMigrations()) {

                    //Get VM migration list according to active policies
                    List<Migration> migrationList = ((cloudreports.extensions.vmallocationpolicies.VmAllocationPolicy) getVmAllocationPolicy()).optimizeVmsAllocation(getVmList());

                    for (Migration migration : migrationList) {
                        Vm vm = migration.getVm();
                        PowerHost targetHost = (PowerHost) migration.getTargetHost();
                        PowerHost oldHost = (PowerHost) migration.getSourceHost();
                        migration.setTime(CloudSim.clock());

                        targetHost.addMigratingInVm(vm);

                        if (oldHost == null) {
                            Log.formatLine("%.2f: Migration of VM #%d to Host #%d has started", CloudSim.clock(), vm.getId(), targetHost.getId());
                        } else {
                            Log.formatLine("%.2f: Migration of VM #%d from Host #%d to Host #%d has started", CloudSim.clock(), vm.getId(), oldHost.getId(), targetHost.getId());
                        }

                        incrementMigrationCount();

                        vm.setInMigration(true);

                        /** VM migration delay = RAM / bandwidth + C    (C = 10 sec) **/
                        send(getId(), vm.getRam() / ((double) vm.getBw() / 8000) + 10, CloudSimTags.VM_MIGRATE, migration.getMigrationMap());
                    }

                    if(!migrationList.isEmpty()) Simulation.getDataCollector().flushMigrations(migrationList);
                }
                
                //Collect monitored used resources
                Simulation.getDataCollector().collectMonitoredUsedResources();
            }

            // schedules an event to the next time
            if (minTime != Double.MAX_VALUE) {
                CloudSim.cancelAll(getId(), new PredicateType(CloudSimTags.VM_DATACENTER_EVENT));
                send(getId(), getSchedulingInterval(), CloudSimTags.VM_DATACENTER_EVENT);
            }

            setLastProcessTime(currentTime);
            Simulation.getDataCollector().collectData();
        }
    }
    
    /**
     * Gets the debts of this datacenter.
     *
     * @return the debts of this datacenter.
     */    
    @Override
    public Map<Integer, Double> getDebts() {
        return super.getDebts();
    }

    /**
     * Gets the monitoring interval of this datacenter.
     *
     * @return the period between data collections.
     */ 
    public double getMonitoringInterval() {
        return monitoringInterval;
    }
    
}
