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
 * Represents an entry on a network map. 
 * It stores information such as the link source and destination as well as the
 * link's bandwidth and latency.
 * 
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class NetworkMapEntry implements Serializable {
    
    /** The id of the entry. */
    private long id;
    
    /** The entry's source. */
    private String source;
    
    /** The entry's destination. */
    private String destination;
    
    /** The entry's bandwidth. */
    private double bandwidth;
    
    /** The entry's latency. */
    private double latency;

    /** The default constructor. */
    public NetworkMapEntry() {}    
    
    /** 
     * Creates a new network map entry.
     * 
     * @param   source          the entry's source.
     * @param   destination     the entry's destination.
     * @param   bandwidth       the entry's bandwidth.
     * @param   latency         the entry's latency.
     * @since                   1.0
     */        
    public NetworkMapEntry(String source, String destination, double bandwidth, double latency) {
        this.source = source;
        this.destination = destination;
        this.bandwidth = bandwidth;
        this.latency = latency;
    }    
    
    /**
     * Gets the entry's id.
     * 
     * @return the entry's id.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the entry's id.
     * 
     * @param   id  the entry's id.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the entry's destination.
     * 
     * @return the entry's destination.
     */
    public String getDestination() {
        return destination;
    }

    /**
     * Sets the entry's destination.
     * 
     * @param   destination the entry's destination.
     */
    public void setDestination(String destination) {
        this.destination = destination;
    }

    /**
     * Gets the entry's bandwidth.
     * 
     * @return the entry's bandwidth.
     */
    public double getBandwidth() {
        return bandwidth;
    }

    /**
     * Sets the entry's bandwidth.
     * 
     * @param   bandwidth   the entry's bandwidth.
     */
    public void setBandwidth(double bandwidth) {
        this.bandwidth = bandwidth;
    }

    /**
     * Gets the entry's latency.
     * 
     * @return the entry's latency.
     */
    public double getLatency() {
        return latency;
    }

    /**
     * Sets the entry's latency.
     * 
     * @param   latency the entry's latency.
     */
    public void setLatency(double latency) {
        this.latency = latency;
    }
    
    /**
     * Gets the entry's source.
     * 
     * @return the entry's source.
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the entry's source.
     * 
     * @param   source  the entry's source.
     */
    public void setSource(String source) {
        this.source = source;
    }    
}
