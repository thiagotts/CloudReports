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

package cloudreports.extensions.brokers;

import cloudreports.enums.BrokerPolicy;
import java.util.List;

/**
 * A specific type of {@link Broker} that schedules virtual machines in a 
 * round-robin fashion.
 * 
 * @see         Broker
 * @see         BrokerPolicy#ROUND_ROBIN
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class RoundRobinDatacenterBroker extends Broker {

    /** The current datacenter id used to schedule virtual machines. */
    int currentId;
    
    /** 
     * Initializes a new instance of this class with the given name.
     *
     * @param   name    the name of the broker.
     * @since           1.0
     */      
    public RoundRobinDatacenterBroker(String name) throws Exception {
        super(name);
        this.currentId=0;
    }

    /** 
     * Gets an id of a datacenter managed by this broker.
     *
     * @return  an id of a datacenter.
     * @since   1.0
     */      
    @Override
    public int getDatacenterId() {
        currentId++;
        int index = currentId%getDatacenterIdsList().size();
        return getDatacenterIdsList().get(index);
    }

    /** 
     * Gets a list of ids from datacenters managed by this broker.
     *
     * @return  a list of datacenter ids.
     * @since   1.0
     */      
    @Override
    public List<Integer> getDatacenterIdList() {
        return getDatacenterIdsList();
    }
}
