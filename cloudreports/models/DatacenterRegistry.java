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

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * A datacenter registry stores information about a specific datacenter.
 * It contains the list of hosts owned by the datacenter, cost values and 
 * other general specifications.
 * 
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class DatacenterRegistry implements Serializable{

    /** The datacenter's id. */
    private long id;
    
    /** The datacenter's name. */
    private String name;
    
    /** The datacenter's architecture. */
    private String architecture;
    
    /** The datacenter's operating system. */
    private String os;
    
    /** The datacenter's hypervisor. */
    private String vmm;
    
    /** The datacenter's time zone. */
    private double timeZone;
    
    /** The alias of the datacenter's allocation policy. */
    private String allocationPolicyAlias;
    
    /** Indicates whether virtual machines migrations are enabled or not. */
    private boolean vmMigration;
    
    /** The list of hosts owned by the datacenter. */
    private List<HostRegistry> hostList;
    
    /** The cost by second of processing. */
    private double costPerSec;
    
    /** The cost by RAM usage. */
    private double costPerMem;
    
    /** The cost by storage usage. */
    private double costPerStorage;
    
    /** The cost by bandwidth usage. */
    private double costPerBw;
    
    /** The list of SAN owned by the datacenter. */
    private List<SanStorageRegistry> sanList;
    
    /** The upper utilization threshold of the datacenter. */
    private double upperUtilizationThreshold;
    
    /** The lower utilization threshold of the datacenter. */
    private double lowerUtilizationThreshold;
    
    /** The datacenter's scheduling interval. */
    private double schedulingInterval;
    
    /** The datacenter's monitoring interval. */
    private double monitoringInterval;

    /** The default constructor. */
    public DatacenterRegistry() {}
    
    /** 
     * Creates a new datacenter registry with the given name. 
     * 
     * @param   name    the name of the datacenter registry.
     * @since           1.0
     */    
    public DatacenterRegistry(String name) {
        setName(name);
        setArchitecture("x86");
        setOs("Linux");
        setVmm("Xen");
        setTimeZone(-3.0);
        setAllocationPolicyAlias("Single threshold");
        setVmMigration(true);
        setCostPerSec(0.1);
        setCostPerMem(0.05);
        setCostPerStorage(0.001);
        setCostPerBw(0.1);
        setUpperUtilizationThreshold(0.8);
        setLowerUtilizationThreshold(0.2);
        setSchedulingInterval(30);
        setMonitoringInterval(180);
        
        //Create host list
        setHostList(new LinkedList<HostRegistry>());
        getHostList().add(new HostRegistry());
        
        //Create SAN Storage List
        setSanList(new LinkedList<SanStorageRegistry>());
        getSanList().add(new SanStorageRegistry("SAN1"));
    }    

    /**
     * Gets the datacenter's id.
     * 
     * @return the datacenter's id.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the datacenter's id.
     * 
     * @param   id  the datacenter's id.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the datacenter's name.
     * 
     * @return the datacenter's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the datacenter's name.
     * 
     * @param   name    the datacenter's name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the datacenter's architecture.
     * 
     * @return the datacenter's architecture.
     */
    public String getArchitecture() {
        return architecture;
    }

    /**
     * Sets the datacenter's architecture.
     * 
     * @param   architecture    the datacenter's architecture.
     */
    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    /**
     * Gets the datacenter's operating system.
     * 
     * @return the datacenter's operating system.
     */
    public String getOs() {
        return os;
    }

    /**
     * Sets the datacenter's operating system.
     * 
     * @param   os  the datacenter's operating system.
     */
    public void setOs(String os) {
        this.os = os;
    }

    /**
     * Gets the datacenter's hypervisor.
     * 
     * @return the datacenter's hypervisor.
     */
    public String getVmm() {
        return vmm;
    }

    /**
     * Sets the datacenter's hypervisor.
     * 
     * @param   vmm the datacenter's hypervisor.
     */
    public void setVmm(String vmm) {
        this.vmm = vmm;
    }

    /**
     * Gets the datacenter's time zone.
     * 
     * @return the datacenter's time zone.
     */
    public double getTimeZone() {
        return timeZone;
    }

    /**
     * Sets the datacenter's time zone.
     * 
     * @param   timeZone    the datacenter's time zone.
     */
    public void setTimeZone(double timeZone) {
        this.timeZone = timeZone;
    }

    /**
     * Gets the datacenter's allocation policy.
     * 
     * @return the datacenter's allocation policy.
     */
    public String getAllocationPolicyAlias() {
        return allocationPolicyAlias;
    }

    /**
     * Sets the datacenter's allocation policy.
     * 
     * @param   allocationPolicyAlias   the datacenter's allocation policy.
     */
    public void setAllocationPolicyAlias(String allocationPolicyAlias) {
        this.allocationPolicyAlias = allocationPolicyAlias;
    }

    /**
     * Checks if virtual machines migrations are enabled.
     * 
     * @return  <code>true</code> if virtual machines migrations are enabled;
     *          <code>false</code> otherwise.
     */
    public boolean isVmMigration() {
        return vmMigration;
    }

    /**
     * Enables/disables virtual machines migrations.
     * 
     * @param   vmMigration indicates if virtual machines migrations are enabled
     *                      or not
     */
    public void setVmMigration(boolean vmMigration) {
        this.vmMigration = vmMigration;
    }

    /**
     * Gets the datacenter's hosts list.
     * 
     * @return the datacenter's host list.
     */
    public List<HostRegistry> getHostList() {
        return hostList;
    }

    /**
     * Sets the datacenter's hosts list.
     * 
     * @param   hostList    the datacenter's host list.
     */
    public void setHostList(List<HostRegistry> hostList) {
        this.hostList = hostList;
    }

    /**
     * Gets the datacenter's cost by second of processing.
     * 
     * @return the datacenter's cost by second of processing.
     */
    public double getCostPerSec() {
        return costPerSec;
    }

    /**
     * Sets the datacenter's cost by second of processing.
     * 
     * @param   costPerSec  the datacenter's cost by second of processing.
     */
    public void setCostPerSec(double costPerSec) {
        this.costPerSec = costPerSec;
    }

    /**
     * Gets the datacenter's cost by RAM usage.
     * 
     * @return the datacenter's cost by RAM usage.
     */
    public double getCostPerMem() {
        return costPerMem;
    }

    /**
     * Sets the datacenter's cost by RAM usage.
     * 
     * @param   costPerMem  the datacenter's cost by RAM usage.
     */
    public void setCostPerMem(double costPerMem) {
        this.costPerMem = costPerMem;
    }

    /**
     * Gets the datacenter's cost by storage usage.
     * 
     * @return the datacenter's cost by storage usage.
     */
    public double getCostPerStorage() {
        return costPerStorage;
    }

    /**
     * Sets the datacenter's cost by storage usage.
     * 
     * @param   costPerStorage  the datacenter's cost by storage usage.
     */
    public void setCostPerStorage(double costPerStorage) {
        this.costPerStorage = costPerStorage;
    }

    /**
     * Gets the datacenter's cost by bandwidth usage.
     * 
     * @return the datacenter's cost by bandwidth usage.
     */
    public double getCostPerBw() {
        return costPerBw;
    }

    /**
     * Sets the datacenter's cost by bandwidth usage.
     * 
     * @param   costPerBw   the datacenter's cost by bandwidth usage.
     */
    public void setCostPerBw(double costPerBw) {
        this.costPerBw = costPerBw;
    }

    /**
     * Gets the datacenter's SAN list.
     * 
     * @return the datacenter's SAN list.
     */
    public List<SanStorageRegistry> getSanList() {
        return sanList;
    }

    /**
     * Sets the datacenter's SAN list.
     * 
     * @param   sanList the datacenter's SAN list.
     */
    public void setSanList(List<SanStorageRegistry> sanList) {
        this.sanList = sanList;
    }

    /**
     * Gets the datacenter's scheduling interval.
     * 
     * @return the datacenter's scheduling interval.
     */
    public double getSchedulingInterval() {
        return schedulingInterval;
    }

    /**
     * Sets the datacenter's scheduling interval.
     * 
     * @param   schedulingInterval  the datacenter's scheduling interval.
     */
    public void setSchedulingInterval(double schedulingInterval) {
        this.schedulingInterval = schedulingInterval;
    }
    
    /**
     * Gets the datacenter's upper utilization threshold.
     * 
     * @return the datacenter's upper utilization threshold.
     */
    public double getUpperUtilizationThreshold() {
        return upperUtilizationThreshold;
    }

    /**
     * Sets the datacenter's upper utilization threshold.
     * 
     * @param   upperUtilizationThreshold   the datacenter's upper utilization
     *                                      threshold.
     */
    public void setUpperUtilizationThreshold(double upperUtilizationThreshold) {
        this.upperUtilizationThreshold = upperUtilizationThreshold;
    }

    /**
     * Gets the datacenter's lower utilization threshold.
     * 
     * @return the datacenter's lower utilization threshold.
     */
    public double getLowerUtilizationThreshold() {
        return lowerUtilizationThreshold;
    }

    /**
     * Sets the datacenter's lower utilization threshold.
     * 
     * @param lowerUtilizationThreshold the datacenter's lower utilization
     *                                  threshold.
     */
    public void setLowerUtilizationThreshold(double lowerUtilizationThreshold) {
        this.lowerUtilizationThreshold = lowerUtilizationThreshold;
    }

    /**
     * Gets the datacenter's monitoring interval.
     * 
     * @return the datacenter's monitoring interval.
     */
    public double getMonitoringInterval() {
        return monitoringInterval;
    }

    /**
     * Sets the datacenter's monitoring interval.
     * 
     * @param   monitoringInterval  the datacenter's monitoring interval.
     */
    public void setMonitoringInterval(double monitoringInterval) {
        this.monitoringInterval = monitoringInterval;
    }
    
    @Override
    public boolean equals(Object datacenter){
      if ( this == datacenter ) return true;
      if ( !(datacenter instanceof DatacenterRegistry) ) return false;
      DatacenterRegistry dr = (DatacenterRegistry)datacenter;
      return this.getName().equals(dr.getName());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (this.getName() != null ? this.getName().hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("Name="+getName()+"\n");
        s.append("Architecture="+getArchitecture()+"\n");
        s.append("Operating System="+getOs()+"\n");
        s.append("Hypervisor="+getVmm()+"\n");
        s.append("Allocation Policy ID="+ getAllocationPolicyAlias()+"\n");
        s.append("Time Zone (GMT)="+getTimeZone()+"\n");
        s.append("VM Migrations="+isVmMigration()+"\n");
        s.append("Upper Utilization threshold="+getUpperUtilizationThreshold()+"\n");
        s.append("Lower Utilization threshold="+getLowerUtilizationThreshold()+"\n");
        s.append("Scheduling interval="+getSchedulingInterval()+"\n");
        s.append("Processing Cost="+getCostPerSec()+"\n");
        s.append("Memory Cost="+getCostPerMem()+"\n");
        s.append("Storage Cost="+getCostPerStorage()+"\n");
        s.append("Bandwidth Cost="+getCostPerBw()+"\n");

        s.append("\n++Beginning of hosts list++\n");
        for(HostRegistry hr : getHostList()) {
            s.append("\n"+hr.toString());
        }
        s.append("\n++End of hosts list++\n");

        s.append("\n++Beginning of SAN list++\n");
        for(SanStorageRegistry sr : getSanList()) {
            s.append("\n"+sr.toString());
        }
        s.append("\n++End of SAN list++\n");

        return s.toString();
    }

}
