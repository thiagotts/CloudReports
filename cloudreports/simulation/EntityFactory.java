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

package cloudreports.simulation;

import cloudreports.dao.CustomerRegistryDAO;
import cloudreports.dao.DatacenterRegistryDAO;
import cloudreports.dao.NetworkMapEntryDAO;
import cloudreports.enums.AllocationPolicy;
import cloudreports.enums.BrokerPolicy;
import cloudreports.extensions.PowerDatacenter;
import cloudreports.gui.Dialog;
import cloudreports.models.*;
import cloudreports.utils.RandomNumberGenerator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.models.PowerModel;
import org.cloudbus.cloudsim.provisioners.BwProvisioner;
import org.cloudbus.cloudsim.provisioners.PeProvisioner;
import org.cloudbus.cloudsim.provisioners.RamProvisioner;
import qrbg.ServiceDeniedException;

/**
 * Provides methods that generate CloudSim entities to be used in a simulation.
 * 
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class EntityFactory {
    
    /**
     * Creates instances of CloudSim's PowerDatacenter class from a list of 
     * datacenter registries.
     * 
     * @param   dcrList a list of datacenter registries.
     * @return          a map containing names of datacenters as keys and
     *                  PowerDatacenter instances as values.
     * @since           1.0
     */    
    static HashMap<String, PowerDatacenter> createDatacenters(List<DatacenterRegistry> dcrList) {
        HashMap<String, PowerDatacenter> map = new HashMap<String, PowerDatacenter>();

        for (DatacenterRegistry dcr : dcrList) {
            List<PowerHost> hostList = createHosts(dcr.getHostList());
            if (hostList == null) {
                return null;
            }

            DatacenterCharacteristics chars = new DatacenterCharacteristics(dcr.getArchitecture(),
                    dcr.getOs(),
                    dcr.getVmm(),
                    hostList,
                    dcr.getTimeZone(),
                    dcr.getCostPerSec(),
                    dcr.getCostPerMem(),
                    dcr.getCostPerStorage(),
                    dcr.getCostPerBw());

            LinkedList<Storage> storageList = createStorageList(dcr.getSanList());


            try {
                VmAllocationPolicy allocationPolicy = AllocationPolicy.getInstance(dcr.getAllocationPolicyAlias()).getPolicy(hostList, dcr.getUpperUtilizationThreshold(), dcr.getAllocationPolicyAlias());
                if (allocationPolicy == null) {
                    Dialog.showErrorMessage(null, "Error loading \"" + dcr.getAllocationPolicyAlias() + "\" allocation policy.");
                    return null;
                }

                PowerDatacenter newDC = new PowerDatacenter(dcr.getName(),
                        chars,
                        AllocationPolicy.getInstance(dcr.getAllocationPolicyAlias()).getPolicy(hostList, dcr.getUpperUtilizationThreshold(), dcr.getAllocationPolicyAlias()),
                        storageList,
                        dcr.getSchedulingInterval(),
                        dcr.getMonitoringInterval());

                if (dcr.isVmMigration()) {
                    newDC.setDisableMigrations(false);
                } else {
                    newDC.setDisableMigrations(true);
                }

                map.put(dcr.getName(), newDC);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return map;
    }

    /**
     * Creates instances of CloudSim's PowerHost class from a list of 
     * host registries.
     * 
     * @param   hostList    a list of datacenter registries.
     * @return              a list of PowerHost instances.
     * @since               1.0
     */        
    static List<PowerHost> createHosts(List<HostRegistry> hostList) {
        List<PowerHost> list = new ArrayList<PowerHost>();

        int i = 0;
        for (HostRegistry hr : hostList) {
            for (int n = 0; n < hr.getAmount(); n++) {
                List<Pe> peList = createPes(hr);
                if (peList == null) {
                    return null;
                }

                RamProvisioner rp = cloudreports.enums.RamProvisioner.getInstance(hr.getRamProvisionerAlias()).getProvisioner(hr.getRam(), hr.getRamProvisionerAlias());
                if (rp == null) {
                    Dialog.showErrorMessage(null, "Error loading \"" + hr.getRamProvisionerAlias() + "\" RAM provisioner.");
                    return null;
                }

                BwProvisioner bp = cloudreports.enums.BwProvisioner.getInstance(hr.getBwProvisionerAlias()).getProvisioner(hr.getBw(), hr.getBwProvisionerAlias());
                if (bp == null) {
                    Dialog.showErrorMessage(null, "Error loading \"" + hr.getBwProvisionerAlias() + "\" bandwidth provisioner.");
                    return null;
                }

                VmScheduler vs = cloudreports.enums.VmScheduler.getInstance(hr.getSchedulingPolicyAlias()).getScheduler(peList, hr.getSchedulingPolicyAlias());
                if (vs == null) {
                    Dialog.showErrorMessage(null, "Error loading \"" + hr.getSchedulingPolicyAlias() + "\" VM scheduler.");
                    return null;
                }
                
                PowerModel pm = cloudreports.enums.PowerModel.getInstance(hr.getPowerModelAlias()).getModel(hr.getMaxPower(), hr.getStaticPowerPercent(), hr.getPowerModelAlias());
                if (pm == null) {
                    Dialog.showErrorMessage(null, "Error loading \"" + hr.getPowerModelAlias() + "\" power model.");
                    return null;
                }

                list.add(new PowerHost(i, rp, bp, hr.getStorage(), peList, vs, pm));
                i++;
            }
        }


        return list;
    }

    /**
     * Creates instances of CloudSim's PowerPe class from a host registry.
     * 
     * @param   hr  a host registry.
     * @return      a list of PowerPe instances.
     * @since       1.0
     */     
    static List<Pe> createPes(HostRegistry hr) {
        List<Pe> list = new ArrayList<Pe>();

        for (int i = 0; i < hr.getNumOfPes(); i++) {
            PeProvisioner pp = cloudreports.enums.PeProvisioner.getInstance(hr.getPeProvisionerAlias()).getProvisioner(hr.getMipsPerPe(), hr.getPeProvisionerAlias());
            if (pp == null) {
                Dialog.showErrorMessage(null, "Error loading \"" + hr.getPeProvisionerAlias() + "\" PE provisioner.");
                return null;
            }

            list.add(new Pe(i, pp));
        }

        return list;
    }

    /**
     * Creates instances of CloudSim's SanStorage class from a list of
     * SAN registries.
     * 
     * @param   sanList a list of SAN registries.
     * @return          a list of SanStorage instances.
     * @since           1.0
     */         
    static LinkedList<Storage> createStorageList(List<SanStorageRegistry> sanList) {
        LinkedList<Storage> list = new LinkedList<Storage>();

        for (SanStorageRegistry sr : sanList) {
            try {
                list.add(new SanStorage(sr.getName(),
                        sr.getCapacity(),
                        sr.getBandwidth(),
                        sr.getNetworkLatency()));
            } catch (ParameterException e) {
                e.printStackTrace();
            }
        }

        return list;
    }

    /**
     * Creates instances of CloudSim's DatacenterBroker class from a list of
     * customer registries.
     * 
     * @param   customerList    a list of customer registries.
     * @return                  a map containing names of customers as keys and
     *                          DatacenterBroker instances as values.
     * @since                   1.0
     */     
    static HashMap<String, DatacenterBroker> createBrokers(List<CustomerRegistry> customerList) {
        HashMap<String, DatacenterBroker> map = new HashMap<String, DatacenterBroker>();

        try {
            for (CustomerRegistry cr : customerList) {
                UtilizationProfile up = cr.getUtilizationProfile();
                String name = cr.getName();

                DatacenterBroker broker = BrokerPolicy.getInstance(up.getBrokerPolicyAlias()).createBroker(name, up.getBrokerPolicyAlias());
                if (broker == null) {
                    Dialog.showErrorMessage(null, "Error loading \"" + up.getBrokerPolicyAlias() + "\" broker.");
                    return null;
                }

                int brokerId = broker.getId();
                List<Vm> vmList = createVms(cr.getVmList(), brokerId);
                if (vmList == null) {
                    return null;
                }

                broker.submitVmList(vmList);
                List<Cloudlet> cloudletList = createCloudlets(up, brokerId, new CustomerRegistryDAO().getNumOfVms(cr.getId()));
                if (cloudletList == null) {
                    return null;
                }

                broker.submitCloudletList(cloudletList);
                map.put(cr.getName(), broker);
            }
        } catch (IOException ex) {
            Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceDeniedException ex) {
            Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, null, ex);
        }

        return map;
    }

    /**
     * Creates instances of CloudSim's Vm class from a list of virtual machine
     * registries.
     * 
     * @param   vmList      a list of virtual machine registries.
     * @param   brokerId    the id of the broker that owns the virtual machines. 
     * @return              a list of Vm instances.
     * @since               1.0
     */         
    static List<Vm> createVms(List<VirtualMachineRegistry> vmList, int brokerId) {
        List<Vm> list = new ArrayList<Vm>();

        int vmId = 0;
        for (VirtualMachineRegistry vmr : vmList) {
            for (int n = 0; n < vmr.getAmount(); n++) {
                CloudletScheduler cs = cloudreports.enums.CloudletScheduler.getInstance(vmr.getSchedulingPolicyAlias()).getScheduler(vmr.getMips(), vmr.getPesNumber(), vmr.getSchedulingPolicyAlias());
                if (cs == null) {
                    Dialog.showErrorMessage(null, "Error loading \"" + vmr.getSchedulingPolicyAlias() + "\" cloudlet scheduler.");
                    return null;
                }


                list.add(new Vm(vmId,
                        brokerId,
                        vmr.getMips(),
                        vmr.getPesNumber(),
                        vmr.getRam(),
                        vmr.getBw(),
                        vmr.getSize(),
                        vmr.getVmm(),
                        cs));
                vmId++;
            }
        }
        return list;
    }

    /**
     * Creates instances of CloudSim's Cloudlet class from a customer's
     * utilization profile.
     * 
     * @param   ugr         the utilization profile.
     * @param   brokerId    the id of the broker that owns the cloudlets.
     * @param   numOfVms    the number of virtual machines.
     * @return              a list of Cloudlet instances.
     * @since               1.0
     */          
    static List<Cloudlet> createCloudlets(UtilizationProfile ugr, int brokerId, long numOfVms) throws IOException, ServiceDeniedException {
        List<Cloudlet> list = new ArrayList<Cloudlet>();

        for (int i = 0; i < numOfVms; i++) {
            UtilizationModel cpu = cloudreports.enums.UtilizationModel.getInstance(ugr.getUtilizationModelCpuAlias()).getModel();
            if (cpu == null) {
                Dialog.showErrorMessage(null, "Error loading \"" + ugr.getUtilizationModelCpuAlias() + "\" CPU utilization model.");
                return null;
            }

            UtilizationModel ram = cloudreports.enums.UtilizationModel.getInstance(ugr.getUtilizationModelRamAlias()).getModel();
            if (ram == null) {
                Dialog.showErrorMessage(null, "Error loading \"" + ugr.getUtilizationModelRamAlias() + "\" RAM utilization model.");
                return null;
            }

            UtilizationModel bw = cloudreports.enums.UtilizationModel.getInstance(ugr.getUtilizationModelBwAlias()).getModel();
            if (bw == null) {
                Dialog.showErrorMessage(null, "Error loading \"" + ugr.getUtilizationModelBwAlias() + "\" bandwidth utilization model.");
                return null;
            }

            Cloudlet cloudlet = new Cloudlet(i,
                    (long) ((long) ugr.getLength() * RandomNumberGenerator.getRandomNumbers(1).get(0)),
                    ugr.getCloudletsPesNumber(),
                    ugr.getFileSize(),
                    ugr.getOutputSize(),
                    cpu,
                    ram,
                    bw);

            cloudlet.setUserId(brokerId);
            cloudlet.setVmId(i);
            list.add(cloudlet);
        }

        return list;
    }

    /**
     * Sets up all the network links to be simulated,
     * 
     * @param   datacenters the datacenters being simulated.
     * @param   brokers     the brokers being simulated.
     * @since               1.0
     */      
    static void setUpNetworkLinks(HashMap<String, PowerDatacenter> datacenters,
            HashMap<String, DatacenterBroker> brokers) {

        NetworkMapEntryDAO neDAO = new NetworkMapEntryDAO();

        /*
         * Establish all links whose source is a datacenter
         */
        DatacenterRegistryDAO drDAO = new DatacenterRegistryDAO();
        String[] datacenterNames = drDAO.getAllDatacentersNames();

        for (String source : datacenterNames) {
            PowerDatacenter src = datacenters.get(source);

            List<NetworkMapEntry> destinationList = neDAO.getListOfDestinations(source);

            for (NetworkMapEntry entry : destinationList) {
                String destinationName = entry.getDestination();

                if (drDAO.getDatacenterRegistry(destinationName) != null) { //destination is a datacenter
                    PowerDatacenter dest = datacenters.get(destinationName);
                    NetworkTopology.addLink(src.getId(), dest.getId(), entry.getBandwidth(), entry.getLatency());
                } else { //destination is a customer
                    DatacenterBroker dest = brokers.get(destinationName);
                    NetworkTopology.addLink(src.getId(), dest.getId(), entry.getBandwidth(), entry.getLatency());
                }
            }
        }


        /*
         * Establish all links whose source is a customer
         */
        CustomerRegistryDAO crDAO = new CustomerRegistryDAO();
        String[] customerNames = crDAO.getCustomersNames();

        for (String source : customerNames) {
            DatacenterBroker src = brokers.get(source);

            List<NetworkMapEntry> destinationList = neDAO.getListOfDestinations(source);

            for (NetworkMapEntry entry : destinationList) {
                String destinationName = entry.getDestination();

                if (drDAO.getDatacenterRegistry(destinationName) != null) { //destination is a datacenter
                    PowerDatacenter dest = datacenters.get(destinationName);
                    NetworkTopology.addLink(src.getId(), dest.getId(), entry.getBandwidth(), entry.getLatency());
                } else { //destination is a customer
                    DatacenterBroker dest = brokers.get(destinationName);
                    NetworkTopology.addLink(src.getId(), dest.getId(), entry.getBandwidth(), entry.getLatency());
                }
            }
        }

    }
    
}
