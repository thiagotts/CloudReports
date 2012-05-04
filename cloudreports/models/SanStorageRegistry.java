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
 * A SAN registry stores basic information about a storage area network owned
 * by a datacenter.
 * 
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class SanStorageRegistry implements Serializable{

    /** The SAN's id. */
    private long id;
    
    /** The SAN's name. */
    private String name;
    
    /** The SAN's capacity. */
    private double capacity;
    
    /** The SAN's bandwidth. */
    private double bandwidth;
    
    /** The SAN's latency. */
    private double networkLatency;

    /** The default constructor. */
    public SanStorageRegistry() {}
    
    /** 
     * Creates a new SAN registry with the given name. 
     * 
     * @param   name    the name of the SAN registry.
     * @since           1.0
     */    
    public SanStorageRegistry(String name) {
        setName(name);
        setCapacity(10000000);
        setBandwidth(10.0);
        setNetworkLatency(5);
    }

    /**
     * Gets the SAN's id.
     * 
     * @return the SAN's id.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the SAN's id.
     * 
     * @param   id  the SAN's id.
     */
    public void setId(long id) {
        this.id = id;
    }    
    
    /**
     * Gets the SAN's capacity.
     * 
     * @return the SAN's capacity.
     */
    public double getCapacity() {
        return capacity;
    }

    /**
     * Sets the SAN's capacity.
     * 
     * @param   capacity    the SAN's capacity.
     */
    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    /**
     * Gets the SAN's bandwidth.
     * 
     * @return the SAN's bandwidth.
     */
    public double getBandwidth() {
        return bandwidth;
    }

    /**
     * Sets the SAN's bandwidth.
     * 
     * @param   bandwidth   the SAN's bandwidth.
     */
    public void setBandwidth(double bandwidth) {
        this.bandwidth = bandwidth;
    }

    /**
     * Gets the SAN's latency.
     * 
     * @return the SAN's latency.
     */
    public double getNetworkLatency() {
        return networkLatency;
    }

    /**
     * Sets the SAN's latency.
     * 
     * @param   networkLatency  the SAN's latency.
     */
    public void setNetworkLatency(double networkLatency) {
        this.networkLatency = networkLatency;
    }

    /**
     * Gets the SAN's name.
     * 
     * @return the SAN's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the SAN's name.
     * 
     * @param   name    the SAN's name.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public boolean equals(Object sanStorage){
      if ( this == sanStorage ) return true;
      if ( !(sanStorage instanceof SanStorageRegistry) ) return false;
      SanStorageRegistry sr = (SanStorageRegistry)sanStorage;
      return this.getName().equals(sr.getName());
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
        s.append("Capacity="+getCapacity()+"\n");
        s.append("Bandwidth="+getBandwidth()+"\n");
        s.append("Latency="+getNetworkLatency()+"\n");

        return s.toString();
    }

}
