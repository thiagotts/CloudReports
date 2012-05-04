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

/**
 * Represents a piece of data that composes a report.
 * 
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class ReportData {
    
    /** The data's id. */
    private long id;
    
    /** The simulation id of the report. */
    private Integer simulationId;
    
    /** The type of resource. */
    private String type;
    
    /** The datacenter's name. */
    private String datacenterName;
    
    /** The customer's name. */
    private String customerName;
    
    /** The id of the virtual machine. */
    private Integer vmId;
    
    /** The id of the host. */
    private Integer hostId;
    
    /** The reported moment. */
    private double time;
    
    /** The amount of a given resource. */
    private double amount;

    /** The default constructor. */
    public ReportData() {}
    
    /** 
     * Creates a new piece of data reporting reporting resource utilization of
     * a specific host or virtual machine.
     * 
     * @param   type            the type of resource.
     * @param   datacenterName  the datacenter's name.
     * @param   customerName    the customer's name.
     * @param   hostId          the id of the host.
     * @param   vmId            the id of the virtual machine.
     * @param   time            the reported moment.
     * @param   amount          the amount of used resources.
     * @param   simulationId    the id of the current simulation.
     * @since                   1.0
     */
    public ReportData(String type, String datacenterName, String customerName,
                      Integer hostId, Integer vmId, double time, double amount,
                      int simulationId) {
        this.type = type;
        this.datacenterName = datacenterName;
        this.customerName = customerName;
        if(hostId != null ) this.hostId = hostId;
        if(vmId != null) this.vmId = vmId;
        this.time = time;
        this.amount = amount;
        this.simulationId = simulationId;
    }

    /** 
     * Creates a new piece of data reporting reporting overall resource
     * utilization of a datacenter or a customer.
     * 
     * @param   type            the type of resource.
     * @param   datacenterName  the datacenter's name.
     * @param   customerName    the customer's name.
     * @param   time            the reported moment.
     * @param   amount          the amount of used resources.
     * @param   simulationId    the id of the current simulation.
     * @since                   1.0
     */    
    public ReportData(String type, String datacenterName, String customerName,
                      double time, double amount, int simulationId) {
        this.type = type;
        this.datacenterName = datacenterName;   
        this.customerName = customerName;
        this.time = time;
        this.amount = amount;
        this.simulationId = simulationId;
    }

    /**
     * Gets the data's id.
     * 
     * @return the data's id.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the data's id.
     * 
     * @param   id  the data's id.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the resource's type.
     * 
     * @return the resource's type.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the resource's type.
     * 
     * @param   type   the resource's type.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the datacenter's name.
     * 
     * @return the datacenter's name.
     */
    public String getDatacenterName() {
        return datacenterName;
    }

    /**
     * Sets the datacenter's name.
     * 
     * @param   datacenterName  the datacenter's name.
     */
    public void setDatacenterName(String datacenterName) {
        this.datacenterName = datacenterName;
    }

    /**
     * Gets the customer's name.
     * 
     * @return the customer's name.
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Sets the customer's name.
     * 
     * @param   customerName    the customer's name.
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Gets the id of the virtual machine.
     * 
     * @return the id of the virtual machine.
     */
    public Integer getVmId() {
        return vmId;
    }

    /**
     * Sets the id of the virtual machine.
     * 
     * @param   vmId    the id of the virtual machine.
     */
    public void setVmId(Integer vmId) {
        this.vmId = vmId;
    }

    /**
     * Gets the id of the host.
     * 
     * @return the id of the host.
     */
    public Integer getHostId() {
        return hostId;
    }

    /**
     * Sets the id of the host.
     * 
     * @param   hostId  the id of the host.
     */
    public void setHostId(Integer hostId) {
        this.hostId = hostId;
    }

    /**
     * Gets the reported moment.
     * 
     * @return the reported moment.
     */
    public double getTime() {
        return time;
    }

    /**
     * Sets the reported moment.
     * 
     * @param   time    the reported moment.
     */
    public void setTime(double time) {
        this.time = time;
    }

    /**
     * Gets the amount of resources.
     * 
     * @return the amount of resources.
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Sets the amount of resources.
     * 
     * @param   amount  the amount of resources.
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * Gets the simulation id.
     * 
     * @return the simulation id.
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
    
    
}
