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

/**
 * A host registry stores information about a specific host configuration.
 * It contains general information such as scheduling policy, power specifications
 * and amount of resources.
 * 
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class HostRegistry implements Serializable{

    /** The host's id. */
    private long id;
    
    /** The host's scheduling policy. */
    private String schedulingPolicyAlias;
    
    /** The host's number of processing elements. */
    private int numOfpes;
    
    /** The amount of mips per processing units. */
    private double mipsPerPe;
    
    /** The maximum power consumption. */
    private double maxPower;
    
    /** The static power consumption percent. */
    private double staticPowerPercent;
    
    /** The host's power model. */
    private String powerModelAlias;
    
    /** The amount of RAM. */
    private int ram;
    
    /** The host's RAM provisioner. */
    private String ramProvisionerAlias;
    
    /** The amount of bandwidth. */
    private int bw;
    
    /** The host's bandwidth provisioner. */
    private String bwProvisionerAlias;
    
    /** The amount of hosts with this registry's specification
     * owned by the datacenter. */
    private int amount;
    
    /** The storage capacity. */
    private long storage;
    
    /** The processing elements provisioner. */
    private String peProvisionerAlias;

    /** 
     * Creates a new host registry.
     * 
     * @since           1.0
     */    
    public HostRegistry() {
        setSchedulingPolicyAlias("Time shared");
        setNumOfPes(4);
        setMipsPerPe(2400);
        setMaxPower(250);
        setStaticPowerPercent(0.7);
        setPowerModelAlias("Linear");
        setRam(40000);
        setRamProvisionerAlias("Simple");
        setBw(10000000);
        setBwProvisionerAlias("Simple");
        setAmount(1);
        setStorage(1000000);
        setPeProvisionerAlias("Simple");
    }

    /**
     * Gets the host's id.
     * 
     * @return the host's id.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the host's id.
     * 
     * @param   id  the host's id.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the host's scheduling policy.
     * 
     * @return the host's scheduling policy.
     */
    public String getSchedulingPolicyAlias() {
        return schedulingPolicyAlias;
    }

    /**
     * Sets the host's scheduling policy.
     * 
     * @param   schedulingPolicyAlias   the host's scheduling policy.
     */
    public void setSchedulingPolicyAlias(String schedulingPolicyAlias) {
        this.schedulingPolicyAlias = schedulingPolicyAlias;
    }

    /**
     * Gets the host's number of processing elements.
     * 
     * @return the host's number of processing elements.
     */
    public int getNumOfPes() {
        return numOfpes;
    }

    /**
     * Sets the host's number of processing elements.
     * 
     * @param   numOfpes    the host's number of processing elements.
     */
    public void setNumOfPes(int numOfpes) {
        this.numOfpes = numOfpes;
    }

    /**
     * Gets the amount of mips per processing elements.
     * 
     * @return the amount of mips per processing elements.
     */
    public double getMipsPerPe() {
        return mipsPerPe;
    }

    /**
     * Sets the amount of mips per processing elements.
     * 
     * @param   mipsPerPe   the amount of mips per processing elements.
     */
    public void setMipsPerPe(double mipsPerPe) {
        this.mipsPerPe = mipsPerPe;
    }

    /**
     * Gets the host's maximum power consumption.
     * 
     * @return the host's maximum power consumption.
     */
    public double getMaxPower() {
        return maxPower;
    }

    /**
     * Sets the host's maximum power consumption.
     * 
     * @param   maxPower    the host's maximum power consumption.
     */
    public void setMaxPower(double maxPower) {
        this.maxPower = maxPower;
    }

    /**
     * Gets the host's static power consumption percent.
     * 
     * @return the host's static power consumption percent.
     */
    public double getStaticPowerPercent() {
        return staticPowerPercent;
    }
    /**
     * Sets the host's static power consumption percent.
     * 
     * @param   staticPowerPercent  the host's static power consumption percent.
     */
    public void setStaticPowerPercent(double staticPowerPercent) {
        this.staticPowerPercent = staticPowerPercent;
    }

    /**
     * Gets the host's power model alias.
     * 
     * @return the host's power model alias.
     */
    public String getPowerModelAlias() {
        return powerModelAlias;
    }

    /**
     * Sets the host's power model alias.
     * 
     * @param   powerModelAlias the host's power model alias.
     */
    public void setPowerModelAlias(String powerModelAlias) {
        this.powerModelAlias = powerModelAlias;
    }

    /**
     * Gets the host's amount of RAM.
     * 
     * @return the host's amount of RAM.
     */
    public int getRam() {
        return ram;
    }

    /**
     * Sets the host's amount of RAM.
     * 
     * @param   ram the host's amount of RAM.
     */
    public void setRam(int ram) {
        this.ram = ram;
    }

    /**
     * Gets the host's RAM provisioner.
     * 
     * @return the host's RAM provisioner.
     */
    public String getRamProvisionerAlias() {
        return ramProvisionerAlias;
    }

    /**
     * Sets the host's RAM provisioner.
     * 
     * @param   ramProvisionerAlias the host's RAM provisioner.
     */
    public void setRamProvisionerAlias(String ramProvisionerAlias) {
        this.ramProvisionerAlias = ramProvisionerAlias;
    }

    /**
     * Gets the host's bandwidth.
     * 
     * @return the host's bandwidth.
     */
    public int getBw() {
        return bw;
    }

    /**
     * Sets the host's bandwidth.
     * 
     * @param   bw  the host's bandwidth.
     */
    public void setBw(int bw) {
        this.bw = bw;
    }

    /**
     * Gets the host's bandwidth provisioner.
     * 
     * @return the host's bandwidth provisioner.
     */
    public String getBwProvisionerAlias() {
        return bwProvisionerAlias;
    }

    /**
     * Sets the host's bandwidth provisioner.
     * 
     * @param bwProvisionerAlias    the host's bandwidth provisioner.
     */
    public void setBwProvisionerAlias(String bwProvisionerAlias) {
        this.bwProvisionerAlias = bwProvisionerAlias;
    }

    /**
     * Gets the amount of hosts with this configuration.
     * 
     * @return the amount of hosts with this configuration.
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Sets the amount of hosts with this configuration.
     * 
     * @param   amount  the amount of hosts with this configuration.
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * Gets the host's storage capacity.
     * 
     * @return the host's storage capacity.
     */
    public long getStorage() {
        return storage;
    }

    /**
     * Sets the host's storage capacity.
     * 
     * @param   storage the host's storage capacity.
     */
    public void setStorage(long storage) {
        this.storage = storage;
    }

    /**
     * Gets the host's processing elements provisioner.
     * 
     * @return the host's processing elements provisioner.
     */
    public String getPeProvisionerAlias() {
        return peProvisionerAlias;
    }

    /**
     * Sets the host's processing elements provisioner.
     * 
     * @param   peProvisionerAlias  the host's processing elements provisioner.
     */
    public void setPeProvisionerAlias(String peProvisionerAlias) {
        this.peProvisionerAlias = peProvisionerAlias;
    }
    
    /**
     * Indicates whether the host can allocate a virtual machine or not.
     * 
     * @param   vmr     the virtual machine to be allocated.
     * @return          <code>true</code> if the host can allocate the virtual
     *                  machine; <code>false</code> otherwise.
     * @since           1.0
     */    
    public boolean canRunVM(VirtualMachineRegistry vmr) {
        if(this.getRam()<vmr.getRam()) return false;
        if((this.getNumOfPes()*this.getMipsPerPe()) < vmr.getMips()) return false;
        if(this.getBw()<vmr.getBw()) return false;
        if(this.getStorage()<vmr.getSize()) return false;

        return true;
    }
    
    @Override
    public boolean equals(Object host){
      if ( this == host ) return true;
      if ( !(host instanceof HostRegistry) ) return false;
      HostRegistry hr = (HostRegistry)host;
      return this.getId() == hr.getId();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }    

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("Host Id="+getId()+"\n");
        s.append("Amount="+getAmount()+"\n");
        s.append("VM Scheduling ="+getSchedulingPolicyAlias()+"\n");
        s.append("Processing Elements="+getNumOfPes()+"\n");
        s.append("MIPS/PE="+getMipsPerPe()+"\n");
        s.append("PE Provisioner ="+getPeProvisionerAlias()+"\n");
        s.append("Maximum Power="+getMaxPower()+"\n");
        s.append("Static Power Percent="+getStaticPowerPercent()+"\n");
        s.append("Power Model ="+getPowerModelAlias()+"\n");
        s.append("RAM="+getRam()+"\n");
        s.append("RAM Provisioner ="+getRamProvisionerAlias()+"\n");
        s.append("Bandwidth="+getBw()+"\n");
        s.append("Bandwidth Provisioner ="+getBwProvisionerAlias()+"\n");
        s.append("Storage="+getStorage()+"\n");

        return s.toString();
    }
}
