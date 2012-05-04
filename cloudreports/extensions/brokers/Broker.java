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

import cloudreports.dao.CustomerRegistryDAO;
import cloudreports.dao.SettingDAO;
import cloudreports.enums.BrokerPolicy;
import cloudreports.models.CustomerRegistry;
import cloudreports.utils.RandomNumberGenerator;
import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.lists.VmList;

/**
 * A subtype of CloudSim's DatacenterBroker class. 
 * All user-implemented new broker policies must be a subtype of this class.
 * 
 * @see         BrokerPolicy
 * @author      Thiago T. Sá
 * @since       1.0
 */
public abstract class Broker extends DatacenterBroker {
    
    /** The maximum length of cloudlets assigned to this broker. */
    private long maxLengthOfCloudlets;
    
    /** The cloudlet id. */
    private int cloudletId;
    
    /** 
     * Initializes a new instance of this class with the given name.
     *
     * @param   name    the name of the broker.
     * @since           1.0
     */     
    public Broker(String name) throws Exception {
        super(name);
        CustomerRegistryDAO crDAO = new CustomerRegistryDAO();
        CustomerRegistry cr = crDAO.getCustomerRegistry(name);
        this.cloudletId = cr.getUtilizationProfile().getNumOfCloudlets();
        this.maxLengthOfCloudlets = cr.getUtilizationProfile().getLength();
    }

    /** 
     * Processes the characteristics of datacenters assigned to this broker.
     *
     * @param   ev  a simulation event.
     * @since       1.0
     */        
    @Override
    protected void processResourceCharacteristics(SimEvent ev) {
        DatacenterCharacteristics characteristics = (DatacenterCharacteristics) ev.getData();
        getDatacenterCharacteristicsList().put(characteristics.getId(), characteristics);

        if (getDatacenterCharacteristicsList().size() == getDatacenterIdsList().size()) {
                setDatacenterRequestedIdsList(new ArrayList<Integer>());
                createVmsInDatacenter(getDatacenterIdList());
        }
    }

    /** 
     * Processes the creation of virtual machines and their allocation in
     * datacenters.
     *
     * @param   ev  a simulation event.
     * @since       1.0
     */      
    @Override
    protected void processVmCreate(SimEvent ev) {
            int[] data = (int[]) ev.getData();
            int datacenterId = data[0];
            int vmId = data[1];
            int result = data[2];

            if (result == CloudSimTags.TRUE) {
                    getVmsToDatacentersMap().put(vmId, datacenterId);
                    getVmsCreatedList().add(VmList.getById(getVmList(), vmId));
                    Log.printLine(CloudSim.clock()+": "+getName()+ ": VM #"+vmId+" has been created in " + getDatacenterCharacteristicsList().get(datacenterId).getResourceName() + ", Host #" + VmList.getById(getVmsCreatedList(), vmId).getHost().getId());
            } else {
                    Log.printLine(CloudSim.clock()+": "+getName()+ ": Creation of VM #"+vmId+" failed in "+ getDatacenterCharacteristicsList().get(datacenterId).getResourceName());
            }

            incrementVmsAcks();

            if (getVmsCreatedList().size() == getVmList().size() - getVmsDestroyed()) { // all the requested VMs have been created
                    submitCloudlets();
            } else {
                    if (getVmsRequested() == getVmsAcks()) { // all the acks received, but some VMs were not created
                            createVmsInDatacenter(getDatacenterIdList());

                            if (getVmsCreatedList().size() > 0) { //if some vm were created
                                    submitCloudlets();
                            }
                    }
            }
    }

    /** 
     * Processes the return of cloudlets.
     *
     * @param   ev  a simulation event.
     * @since       1.0
     */      
    @Override
    protected void processCloudletReturn(SimEvent ev) {
            Cloudlet cloudlet = (Cloudlet) ev.getData();
            getCloudletReceivedList().add(cloudlet);            
            Log.printLine(CloudSim.clock()+": "+getName()+ ": Cloudlet "+cloudlet.getCloudletId()+" received");
            cloudletsSubmitted -= 1;
            
            SettingDAO sDAO = new SettingDAO();
            if(CloudSim.clock() <= Integer.valueOf(sDAO.getSetting("TimeToSimulate").getValue())*60) {
                Cloudlet newCloudlet = new Cloudlet(this.cloudletId,
                                                    (long) ((long)this.maxLengthOfCloudlets *  RandomNumberGenerator.getRandomNumbers(1).get(0)),
                                                    cloudlet.getNumberOfPes(),
                                                    cloudlet.getCloudletLength(),
                                                    cloudlet.getCloudletOutputSize(),
                                                    cloudlet.getUtilizationModelCpu(),
                                                    cloudlet.getUtilizationModelRam(),
                                                    cloudlet.getUtilizationModelBw());
                newCloudlet.setUserId(getId());
                newCloudlet.setVmId(cloudlet.getVmId());
                getCloudletList().add(newCloudlet);
                this.cloudletId++;
                submitCloudlets();
            }
            else {
                CloudSim.abruptallyTerminate();
                CloudSim.stopSimulation();
            }
    }

    /** 
     * Submits cloudlets to be executed in virtual machines.
     *
     * @since       1.0
     */       
    @Override
    protected void submitCloudlets() {

            for (Cloudlet cloudlet : getCloudletList()) {
                    if (cloudlet.getVmId() == -1) { //If user didn't bind this cloudlet and it has not been executed yet
                        cloudlet.setVmId(getVmsCreatedList().get(0).getId());
                    }
                    
                    //Check if the cloudlet VM has been allocated
                    Vm cloudletVm = null;
                    for(Vm vm : getVmsCreatedList()) {
                        if(vm.getId() == cloudlet.getVmId()) {
                            cloudletVm = vm;
                        }
                    }
                    
                    //If the VM is allocated, send cloudlet
                    if(cloudletVm != null) {
                        Log.printLine(CloudSim.clock()+": "+getName()+ ": Sending cloudlet "+cloudlet.getCloudletId()+" to VM #"+cloudletVm.getId());
                        cloudlet.setVmId(cloudletVm.getId());

                        try {
                            sendNow(getVmsToDatacentersMap().get(cloudletVm.getId()), CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
                        }
                        catch(NullPointerException e) {
                            continue;
                        }

                        cloudletsSubmitted += 1;
                        getCloudletSubmittedList().add(cloudlet);
                    }
                    else { //The VM is not allocated yet, so postpone submission
                        Log.printLine(CloudSim.clock() + ": " + getName() + ": Postponing execution of cloudlet " + cloudlet.getCloudletId() + ": bount VM not available");
                    }
            }

            // remove submitted cloudlets from waiting list
            for (Cloudlet cloudlet : getCloudletSubmittedList()) {
                    getCloudletList().remove(cloudlet);
            }
    }

    /** 
     * Creates virtual machines in the datacenters managed by this broker.
     *
     * @param   datacenterIdList    the list of datacenters managed by this broker.
     * @since                       1.0
     */       
    protected void createVmsInDatacenter(List<Integer> datacenterIdList) {

        List<Vm> auxList = new ArrayList<Vm>();
        for(Vm vm : getVmList()) {
            auxList.add(vm);
        }

        int requestedVms = 0;
        for (Integer datacenterId : datacenterIdList) {
            String datacenterName = CloudSim.getEntityName(datacenterId);
            for (Vm vm : auxList) {
                if (!getVmsToDatacentersMap().containsKey(vm.getId())) {
                    Log.printLine(CloudSim.clock() + ": " + getName() + ": Trying to Create VM #" + vm.getId() + " in " + datacenterName);
                    sendNow(datacenterId, CloudSimTags.VM_CREATE_ACK, vm);
                    requestedVms++;
                    auxList.remove(vm);
                    break;
                }
            }
            getDatacenterRequestedIdsList().add(datacenterId);
            setVmsRequested(requestedVms);
        }

        setVmsAcks(0);
    }

    /** 
     * An abstract method whose implementations must return an id of
     * a datacenter.
     *
     * @return  the id of a datacenter.
     * @since   1.0
     */     
    public abstract int getDatacenterId();
    
    /** 
     * An abstract method whose implementations must return a list of ids
     * from datacenters managed by the broker.
     *
     * @return  a list of ids from datacenters managed by this broker.
     * @since   1.0
     */  
    public abstract List<Integer> getDatacenterIdList();

}
