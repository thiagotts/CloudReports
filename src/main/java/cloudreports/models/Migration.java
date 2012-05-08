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

package cloudreports.models;

import java.util.HashMap;
import java.util.Map;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.power.PowerHost;

/**
 * A migration represents the act of deallocating a virtual machine from a
 * host and allocating the same virtual machine to another host.
 * This class stores basic information about the execution of a virtual 
 * machine migration.
 * 
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class Migration {
    
    /** The migration's id. */
    private long id;
    
    /** The id of the simulation that performed the migration. */
    private Integer simulationId;
    
    /** The description of the migration. */
    private String description;
    
    /** The moment the migration occurred. */
    private double time;
    
    /** The target host's label. */
    private String targetHostLabel;
    
    /** The source host's label. */
    private String sourceHostLabel;
    
    /** The virtual machine's label. */
    private String vmLabel;
    
    /** The name of the datacenter on which the migration took place. */
    private String datacenterName;
    
    /** The CPU utilization of the source host on the moment the migration occurred. */
    private double sourceHostCpuUtilization;
    
    /** The RAM utilization of the source host on the moment the migration occurred. */
    private double sourceHostRamUtilization;
    
    /** The power consumption of the source host on the moment the migration occurred. */    
    private double sourceHostPowerConsumption;
    
    /** The CPU utilization of the target host on the moment the migration occurred. */    
    private double targetHostCpuUtilization;
    
    /** The RAM utilization of the target host on the moment the migration occurred. */
    private double targetHostRamUtilization;
    
    /** The power consumption of the target host on the moment the migration occurred. */   
    private double targetHostPowerConsumption;    
    
    /** The source host. */
    private PowerHost sourceHost;    
    
    /** The target host. */
    private PowerHost targetHost;
    
    /** The virtual machine allocation policy. */
    private VmAllocationPolicy vmAllocationPolicy;
    
    /** The virtual machine. */
    private Vm vm;

    /** The default constructor. */
    public Migration() {}
    
    /** 
     * Creates a new migration registry. 
     * 
     * @param   vmAllocationPolicy  the virtual machine allocation policy.
     * @param   targetHost          the target host.
     * @param   sourceHost          the source host.
     * @param   vm                  the virtual machine.
     * @param   currentSimulation   the id of the current simulation.
     * @since                       1.0
     */        
    public Migration(VmAllocationPolicy vmAllocationPolicy, PowerHost targetHost, PowerHost sourceHost, Vm vm, int currentSimulation) {
        this.vmAllocationPolicy = vmAllocationPolicy;
        this.targetHost = targetHost;
        this.sourceHost = sourceHost;
        this.vm = vm;
        this.simulationId = currentSimulation;
        
        this.targetHostLabel = "Host" + targetHost.getId();
        this.sourceHostLabel = "Host" + sourceHost.getId();
        this.vmLabel = "VM" + vm.getUid();
        this.datacenterName = targetHost.getDatacenter().getName();

        this.sourceHostCpuUtilization = sourceHost.getUtilizationOfCpuMips();
        this.sourceHostRamUtilization = sourceHost.getUtilizationOfRam();
        this.sourceHostPowerConsumption = sourceHost.getPower()/sourceHost.getMaxPower();

        this.targetHostCpuUtilization = targetHost.getUtilizationOfCpuMips();
        this.targetHostRamUtilization = targetHost.getUtilizationOfRam();
        this.targetHostPowerConsumption = targetHost.getPower()/targetHost.getMaxPower();
    }
    
    /**
     * Gets the migration's id.
     *
     * @return the migration's id.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the migration's id.
     *
     * @param id the migration's id.
     */
    public void setId(long id) {
        this.id = id;
    }    
    
    /**
     * Gets the migration's description.
     * 
     * @return  the migration's description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the migration's description.
     * 
     * @param   description the migration's description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the migration's moment.
     * 
     * @return  the migration's moment.
     */
    public double getTime() {
        return time;
    }

    /**
     * Sets the migration's moment.
     * 
     * @param   time    the migration's moment.
     */
    public void setTime(double time) {
        this.time = time;
    }

    /**
     * Gets the migration's target host.
     * 
     * @return  the migration's target host.
     */
    public PowerHost getTargetHost() {
        return targetHost;
    }

    /**
     * Sets the migration's target host.
     * 
     * @param   targetHost  the migration's target host.
     */
    public void setTargetHost(PowerHost targetHost) {
        this.targetHost = targetHost;
    }

    /**
     * Gets the migration's source host.
     * 
     * @return  the migration's source host.
     */
    public PowerHost getSourceHost() {
        return sourceHost;
    }

    /**
     * Sets the migration's source host.
     * 
     * @param   sourceHost  the migration's source host.
     */
    public void setSourceHost(PowerHost sourceHost) {
        this.sourceHost = sourceHost;
    }

    /**
     * Gets the migration's virtual machine.
     * 
     * @return  the migration's virtual machine.
     */
    public Vm getVm() {
        return vm;
    }

    /**
     * Sets the migration's virtual machine.
     * 
     * @param   vm  the migration's virtual machine.
     */
    public void setVm(Vm vm) {
        this.vm = vm;
    }

    /**
     * Gets the migration's target host label.
     * 
     * @return  the migration's target host label.
     */
    public String getTargetHostLabel() {
        return targetHostLabel;
    }

    /**
     * Sets the migration's target host label.
     * 
     * @param   targetHostLabel  the migration's target host label.
     */
    public void setTargetHostLabel(String targetHostLabel) {
        this.targetHostLabel = targetHostLabel;
    }

    /**
     * Gets the migration's source host label.
     * 
     * @return  the migration's source host label.
     */
    public String getSourceHostLabel() {
        return sourceHostLabel;
    }

    /**
     * Sets the migration's source host label.
     * 
     * @param   sourceHostLabel the migration's source host label.
     */
    public void setSourceHostLabel(String sourceHostLabel) {
        this.sourceHostLabel = sourceHostLabel;
    }

    /**
     * Gets the migration's virtual machine label.
     * 
     * @return  the migration's virtual machine label.
     */
    public String getVmLabel() {
        return vmLabel;
    }
    /**
     * Sets the migration's virtual machine label.
     * 
     * @param   vmLabel the migration's virtual machine label.
     */
    public void setVmLabel(String vmLabel) {
        this.vmLabel = vmLabel;
    }

    /**
     * Gets the migration's datacenter name.
     * 
     * @return  the migration's datacenter name.
     */
    public String getDatacenterName() {
        return datacenterName;
    }

    /**
     * Sets the migration's datacenter name.
     * 
     * @param   datacenterName  the migration's datacenter name.
     */
    public void setDatacenterName(String datacenterName) {
        this.datacenterName = datacenterName;
    }

    /**
     * Gets the CPU utilization of the source host.
     * 
     * @return  the CPU utilization of the source host.
     */
    public double getSourceHostCpuUtilization() {
        return sourceHostCpuUtilization;
    }

    /**
     * Sets the CPU utilization of the source host.
     * 
     * @param   sourceHostCpuUtilization    the CPU utilization of the source
     *                                      host.
     */
    public void setSourceHostCpuUtilization(double sourceHostCpuUtilization) {
        this.sourceHostCpuUtilization = sourceHostCpuUtilization;
    }

    /**
     * Gets the RAM utilization of the source host.
     * 
     * @return  the RAM utilization of the source host.
     */
    public double getSourceHostRamUtilization() {
        return sourceHostRamUtilization;
    }

    /**
     * Sets the RAM utilization of the source host.
     * 
     * @param sourceHostRamUtilization  the RAM utilization of the source host.
     */
    public void setSourceHostRamUtilization(double sourceHostRamUtilization) {
        this.sourceHostRamUtilization = sourceHostRamUtilization;
    }

    /**
     * Gets the power consumption of the source host.
     * 
     * @return  the power consumption of the source host.
     */
    public double getSourceHostPowerConsumption() {
        return sourceHostPowerConsumption;
    }

    /**
     * Sets the power consumption of the source host.
     * 
     * @param   sourceHostPowerConsumption  the power consumption of the source
     *                                      host.
     */
    public void setSourceHostPowerConsumption(double sourceHostPowerConsumption) {
        this.sourceHostPowerConsumption = sourceHostPowerConsumption;
    }

    /**
     * Gets the CPU utilization of the target host.
     * 
     * @return  the CPU utilization of the target host.
     */
    public double getTargetHostCpuUtilization() {
        return targetHostCpuUtilization;
    }

    /**
     * Sets the CPU utilization of the target host.
     * 
     * @param   targetHostCpuUtilization    the CPU utilization of the target
     *                                      host.
     */
    public void setTargetHostCpuUtilization(double targetHostCpuUtilization) {
        this.targetHostCpuUtilization = targetHostCpuUtilization;
    }

    /**
     * Gets the RAM utilization of the target host.
     * 
     * @return  the RAM utilization of the target host.
     */
    public double getTargetHostRamUtilization() {
        return targetHostRamUtilization;
    }

    /**
     * Sets the RAM utilization of the target host.
     * 
     * @param   targetHostRamUtilization    the RAM utilization of the target
     *                                      host.
     */
    public void setTargetHostRamUtilization(double targetHostRamUtilization) {
        this.targetHostRamUtilization = targetHostRamUtilization;
    }

    /**
     * Gets the power consumption of the target host.
     * 
     * @return  the power consumption of the target host.
     */
    public double getTargetHostPowerConsumption() {
        return targetHostPowerConsumption;
    }

    /**
     * Sets the power consumption of the target host.
     * 
     * @param   targetHostPowerConsumption  the power consumption of the target
     *                                      host.
     */
    public void setTargetHostPowerConsumption(double targetHostPowerConsumption) {
        this.targetHostPowerConsumption = targetHostPowerConsumption;
    }

    /**
     * Gets the simulation id.
     * 
     * @return  the simulation id.
     */
    public Integer getSimulationId() {
        return simulationId;
    }

    /**
     * Sets the simulation id.
     * 
     * @param   simulationId    the simulation id.
     */
    public void setSimulationId(Integer simulationId) {
        this.simulationId = simulationId;
    }    
    
    /**
     * Gets the migration map.
     * 
     * @return  the migration map.
     */    
    public Map<String, Object> getMigrationMap() {
        Map<String, Object> migrationMap = new HashMap<String, Object>();
        migrationMap.put("vm", getVm());
        migrationMap.put("host", getTargetHost());
        return migrationMap;
    }
}
