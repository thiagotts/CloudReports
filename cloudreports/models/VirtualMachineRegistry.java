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
 * A virtual machine registry stores information about a specific virtual
 * machine configuration.
 * 
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class VirtualMachineRegistry implements Serializable{

    /** The id of the virtual machine. */
    private long id;
    
    /** The virtual machine's image image size. */
    private long size;
    
    /** The number of processing elements used by the virtual machine. */
    private int pesNumber;
    
    /** The amount of mips required by the virtual machine. */
    private double mips;
    
    /** The amount of RAM required by the virtual machine. */
    private int ram;
    
    /** The amount of bandwidth required by the virtual machine. */
    private long bw;
    
    /** The priority of the virtual machine. */
    private int priority;
    
    /** The virtual machine's type of hypervisor. */
    private String vmm;
    
    /** The virtual machine's scheduling policy. */
    private String schedulingPolicyAlias;
    
    /** The amount of virtual machines with this registry's specification
     * owned by the customer. */
    private int amount;

    /** The default constructor. */
    public VirtualMachineRegistry() {
        setSize(1000);
        setPesNumber(1);
        setMips(1000);
        setRam(512);
        setBw(100000);
        setPriority(1);
        setVmm("Xen");
        setSchedulingPolicyAlias("Dynamic workload");
        setAmount(1);
    }

    /**
     * Gets the virtual machine's id.
     * 
     * @return the virtual machine's id.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the virtual machine's id.
     * 
     * @param   id  the virtual machine's id.
     */
    public void setId(long id) {
        this.id = id;
    }
    
    /**
     * Gets the virtual machine's image size.
     * 
     * @return the virtual machine's image size.
     */
    public long getSize() {
        return size;
    }

    /**
     * Sets the virtual machine's image size.
     * 
     * @param   size    the virtual machine's image size.
     */
    public void setSize(long size) {
        this.size = size;
    }

    /**
     * Gets the virtual machine's number of processing elements.
     * 
     * @return the virtual machine's number of processing elements.
     */
    public int getPesNumber() {
        return pesNumber;
    }

    /**
     * Sets the virtual machine's number of processing elements.
     * 
     * @param   pesNumber   the virtual machine's number of processing elements.
     */
    public void setPesNumber(int pesNumber) {
        this.pesNumber = pesNumber;
    }

    /**
     * Gets the virtual machine's required mips.
     * 
     * @return the virtual machine's required mips.
     */
    public double getMips() {
        return mips;
    }

    /**
     * Sets the virtual machine's required mips.
     * 
     * @param   mips    the virtual machine's required mips.
     */
    public void setMips(double mips) {
        this.mips = mips;
    }

    /**
     * Gets the virtual machine's amount of RAM.
     * 
     * @return the virtual machine's amount of RAM.
     */
    public int getRam() {
        return ram;
    }

    /**
     * Sets the virtual machine's amount of RAM.
     * 
     * @param   ram the virtual machine's amount of RAM.
     */
    public void setRam(int ram) {
        this.ram = ram;
    }

    /**
     * Gets the virtual machine's amount of bandwidth.
     * 
     * @return the virtual machine's amount of bandwidth.
     */
    public long getBw() {
        return bw;
    }

    /**
     * Sets the virtual machine's amount of bandwidth.
     * 
     * @param   bw  the virtual machine's amount of bandwidth.
     */
    public void setBw(long bw) {
        this.bw = bw;
    }

    /**
     * Gets the virtual machine's priority.
     * 
     * @return the virtual machine's priority.
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Sets the virtual machine's priority.
     * 
     * @param   priority    the virtual machine's priority.
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Gets the virtual machine's hypervisor.
     * 
     * @return the virtual machine's hypervisor.
     */
    public String getVmm() {
        return vmm;
    }

    /**
     * Sets the virtual machine's hypervisor.
     * 
     * @param   vmm the virtual machine's hypervisor.
     */
    public void setVmm(String vmm) {
        this.vmm = vmm;
    }

    /**
     * Gets the amount of virtual machines with this configuration.
     * 
     * @return the amount of virtual machines with this configuration.
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Sets the amount of virtual machines with this configuration.
     * 
     * @param   amount  the amount of virtual machines with this configuration.
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * Gets the virtual machine's scheduling policy.
     * 
     * @return the virtual machine's scheduling policy.
     */
    public String getSchedulingPolicyAlias() {
        return schedulingPolicyAlias;
    }

    /**
     * Sets the virtual machine's scheduling policy.
     * 
     * @param   schedulingPolicyAlias   the virtual machine's scheduling policy.
     */
    public void setSchedulingPolicyAlias(String schedulingPolicyAlias) {
        this.schedulingPolicyAlias = schedulingPolicyAlias;
    }
    
    @Override
    public boolean equals(Object virtualMachine){
      if ( this == virtualMachine ) return true;
      if ( !(virtualMachine instanceof VirtualMachineRegistry) ) return false;
      VirtualMachineRegistry vr = (VirtualMachineRegistry)virtualMachine;
      return this.getId() == vr.getId();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("VM Id="+getId()+"\n");
        s.append("Number of VMs="+getAmount()+"\n");
        s.append("Image size="+getSize()+"\n");
        s.append("VM processors="+getPesNumber()+"\n");
        s.append("VM MIPS="+getMips()+"\n");
        s.append("VM RAM="+getRam()+"\n");
        s.append("VM Bandwidth="+getBw()+"\n");
        s.append("VM Priority="+getPriority()+"\n");
        if(getVmm().equalsIgnoreCase("xen")) s.append("Hypervisor=xen\n");
        else s.append("Hypervisor=kvm\n");

        return s.toString();
    }
}
