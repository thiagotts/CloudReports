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

package cloudreports.reports;

import cloudreports.dao.MigrationDAO;
import cloudreports.dao.ReportDataDAO;
import cloudreports.dao.SettingDAO;
import cloudreports.database.Database;
import cloudreports.extensions.PowerDatacenter;
import cloudreports.models.DatacenterRegistry;
import cloudreports.models.Migration;
import cloudreports.models.ReportData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerHost;

/**
 * Provides methods to collect simulation data.
 * 
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class DataCollector {
    
    /** The label used for the bandwidth resources. */
    private static final String BANDWIDTH = "BANDWIDTH";
    
    /** The label used for the CPU resources. */
    private static final String CPU = "CPU";
    
    /** The label used for the power resources. */
    private static final String POWER = "POWER";    
    
    /** The label used for the RAM resources. */
    private static final String RAM = "RAM";
    
    /** The number of samples to be considered when dealing with monitored
     *  resources.
     */
    private static final int SAMPLES = 2;

    /** A list of transient report data. */
    private List<ReportData> dataList;
    
    /** A map of datacenters. */
    private HashMap<String,PowerDatacenter> datacenters;
    
    /** A map of brokers. */
    private HashMap<String,DatacenterBroker> brokers;
    
    /** A list of monitored resources. */
    private List<ReportData> monitoredUsedResources;
    
    /** The last moment data was collected. */
    private double lastClock;

    /** 
     * Creates an instance of a data collector.
     * 
     * @param   datacenters a map of datacenters to keep track of.
     * @param   brokers     a map of brokers to keep track of.
     * @since               1.0
     */     
    public DataCollector(HashMap<String,PowerDatacenter> datacenters, HashMap<String,DatacenterBroker> brokers) {
        
        Database.cleanTempReport();
        this.datacenters = datacenters;
        this.brokers = brokers;
        this.monitoredUsedResources = new ArrayList<ReportData>();
        this.dataList = new ArrayList<ReportData>();
        insertClearedData(0);
    }
    
    /**
     * Gets the last moment data was collected.
     * 
     * @return  the last moment data was collected.
     */
    public double getLastClock() {
        return lastClock;
    }
    
    /**
     * Collects data from virtual machines and hosts and adds them to a
     * transient list.
     * If the transient list's size gets greater than 1000, the data is flushed
     * to the database.
     * 
     * @see     #flushData()
     * @since   1.0
     */    
    public void collectData() {
        collectVmsData();
        collectHostsData();
        if(dataList.size() > 10000) {
            flushData();            
        }
    }

    /**
     * Collects data from virtual machines and adds them to a transient list.
     * 
     * @since   1.0
     */     
    private void collectVmsData() {
        List<String> brokerNames = Arrays.asList(brokers.keySet().toArray(new String[0]));
        double currentTime = CloudSim.clock();
        this.lastClock = currentTime;
        for(String brokerName : brokerNames) {
            DatacenterBroker broker = brokers.get(brokerName);
            double overallRam = 0,
                   overallCpu = 0,
                   overallBandwidth = 0;
            int currentSimulation = Integer.valueOf(new SettingDAO().getSetting("CurrentSimulation").getValue());
            
            List<Vm> vmsList = broker.getVmList();
            for(Vm vm : vmsList) {
                int vmId = vm.getId();
                
                double ramUtilization = (vm.getCurrentAllocatedRam()/vm.getRam())*100;
                dataList.add(new ReportData(RAM, null, brokerName, null, vmId, currentTime, ramUtilization, currentSimulation));
                overallRam += ramUtilization;
                
                double cpuUtilization = (vm.getCurrentRequestedTotalMips()/vm.getMips())*100;
                dataList.add(new ReportData(CPU, null, brokerName, null, vmId, currentTime, cpuUtilization, currentSimulation));
                overallCpu += cpuUtilization;
                
                double bwUtilization = (vm.getCurrentAllocatedBw()/vm.getBw())*100;
                dataList.add(new ReportData(BANDWIDTH, null, brokerName, null, vmId, currentTime, bwUtilization, currentSimulation));
                overallBandwidth += bwUtilization;
            }
            
            int numOfVms = vmsList.size();
            dataList.add(new ReportData(RAM, null, brokerName, currentTime, overallRam/numOfVms, currentSimulation));            
            dataList.add(new ReportData(CPU, null, brokerName, currentTime, overallCpu/numOfVms, currentSimulation));            
            dataList.add(new ReportData(BANDWIDTH, null, brokerName, currentTime, overallBandwidth/numOfVms, currentSimulation));
        }
        
    }

    /**
     * Collects data from hosts and adds them to a transient list.
     * 
     * @since   1.0
     */         
    private void collectHostsData() {
        List<String> datacenterNames = Arrays.asList(datacenters.keySet().toArray(new String[0]));
        double currentTime = CloudSim.clock();
        this.lastClock = currentTime;
        for(String datacenterName : datacenterNames) {
            PowerDatacenter datacenter = datacenters.get(datacenterName);
            double overallRam = 0,
                   overallCpu = 0,
                   overallBandwidth = 0,
                   overallPower = 0;   
            int currentSimulation = Integer.valueOf(new SettingDAO().getSetting("CurrentSimulation").getValue());
            
            List<PowerHost> hostsList = datacenter.getHostList();
            for(PowerHost host : hostsList) {
                int hostId = host.getId();
                
                double ramUtilization = (host.getUtilizationOfRam()/host.getRam())*100;
                dataList.add(new ReportData(RAM, datacenterName, null, hostId, null, currentTime, ramUtilization, currentSimulation));
                overallRam += ramUtilization;
                
                double cpuUtilization = (host.getUtilizationOfCpuMips()/host.getTotalMips())*100;
                dataList.add(new ReportData(CPU, datacenterName, null, hostId, null, currentTime, cpuUtilization, currentSimulation));
                overallCpu += cpuUtilization;
                
                double bwUtilization = (host.getUtilizationOfBw()/host.getBw())*100;
                dataList.add(new ReportData(BANDWIDTH, datacenterName, null, hostId, null, currentTime, bwUtilization, currentSimulation));
                overallBandwidth += bwUtilization;
                
                double powerUtilization = (host.getPower()/host.getMaxPower())*100;
                dataList.add(new ReportData(POWER, datacenterName, null, hostId, null, currentTime, powerUtilization, currentSimulation));
                overallPower += powerUtilization;
            }
            
            int numOfHosts = hostsList.size();
            
            dataList.add(new ReportData(RAM, datacenterName, null, currentTime, overallRam/numOfHosts, currentSimulation));            
            dataList.add(new ReportData(CPU, datacenterName, null, currentTime, overallCpu/numOfHosts, currentSimulation));            
            dataList.add(new ReportData(BANDWIDTH, datacenterName, null, currentTime, overallBandwidth/numOfHosts, currentSimulation));            
            dataList.add(new ReportData(POWER, datacenterName, null, currentTime, overallPower/numOfHosts, currentSimulation));
        }
    }
    
    /**
     * Inserts cleared data into a report.
     * 
     * @param   time    the simulation time at which the cleared data must be
     *                  inserted.
     * @since   1.0
     */       
    public void insertClearedData(double time) {
        //Create cleared data for virtual machines
        List<String> brokerNames = Arrays.asList(brokers.keySet().toArray(new String[0]));
        ReportDataDAO rdDAO = new ReportDataDAO();
        double currentTime = time;
        int currentSimulation = Integer.valueOf(new SettingDAO().getSetting("CurrentSimulation").getValue());
        
        for (String brokerName : brokerNames) {
            DatacenterBroker broker = brokers.get(brokerName);

            List<Vm> vmsList = broker.getVmList();
            for (Vm vm : vmsList) {
                int vmId = vm.getId();

                rdDAO.insertVmData(RAM, brokerName, vmId, currentTime, 0, currentSimulation);
                rdDAO.insertVmData(CPU, brokerName, vmId, currentTime, 0, currentSimulation);
                rdDAO.insertVmData(BANDWIDTH, brokerName, vmId, currentTime, 0, currentSimulation);
            }

            rdDAO.insertCustomerOverallData(RAM, brokerName, currentTime, 0, currentSimulation);
            rdDAO.insertCustomerOverallData(CPU, brokerName, currentTime, 0, currentSimulation);
            rdDAO.insertCustomerOverallData(BANDWIDTH, brokerName, currentTime, 0, currentSimulation);
        }
        
        //Create cleared data for hosts
        List<String> datacenterNames = Arrays.asList(datacenters.keySet().toArray(new String[0]));
        for (String datacenterName : datacenterNames) {
            PowerDatacenter datacenter = datacenters.get(datacenterName);

            List<PowerHost> hostsList = datacenter.getHostList();
            for (PowerHost host : hostsList) {
                int hostId = host.getId();

                rdDAO.insertHostData(RAM, datacenterName, hostId, currentTime, 0, currentSimulation);
                rdDAO.insertHostData(CPU, datacenterName, hostId, currentTime, 0, currentSimulation);
                rdDAO.insertHostData(BANDWIDTH, datacenterName, hostId, currentTime, 0, currentSimulation);
                rdDAO.insertHostData(POWER, datacenterName, hostId, currentTime, 0, currentSimulation);
            }

            rdDAO.insertDatacenterOverallData(RAM, datacenterName, currentTime, 0, currentSimulation);
            rdDAO.insertDatacenterOverallData(CPU, datacenterName, currentTime, 0, currentSimulation);
            rdDAO.insertDatacenterOverallData(BANDWIDTH, datacenterName, currentTime, 0, currentSimulation);
            rdDAO.insertDatacenterOverallData(POWER, datacenterName, currentTime, 0, currentSimulation);
        }
    }

    /**
     * Inserts migration data into the database.
     * 
     * @param   migrationList   the list of migration data to be inserted.
     * @since   1.0
     */         
    public void flushMigrations(List<Migration> migrationList) {
        MigrationDAO mDAO = new MigrationDAO();
        mDAO.insertMigrations(migrationList);
    }
    
    /**
     * Inserts report data from {@link #dataList} into the database.
     * 
     * @since   1.0
     */       
    public void flushData() {
        if(!this.dataList.isEmpty()) {
            ReportDataDAO rDAO = new ReportDataDAO();
            rDAO.insertDataList(dataList);
            dataList.clear();
        }
    }
    
    /**
     * Collects monitored resources.
     * 
     * @see     DatacenterRegistry#monitoringInterval
     * @since   1.0
     */       
    public void collectMonitoredUsedResources() {
        List<String> datacenterNames = Arrays.asList(datacenters.keySet().toArray(new String[0]));
        double currentTime = CloudSim.clock();
        for (String datacenterName : datacenterNames) {
            PowerDatacenter datacenter = datacenters.get(datacenterName);
            List<PowerHost> hostsList = datacenter.getHostList();
            for (PowerHost host : hostsList) {
                int hostId = host.getId();

                double ramUtilization = (host.getUtilizationOfRam() / host.getRam()) * 100;
                insertMonitoredUsedResources(new ReportData(RAM, datacenterName, null, hostId, null, currentTime, ramUtilization, 0));

                double cpuUtilization = (host.getUtilizationOfCpuMips() / host.getTotalMips()) * 100;
                insertMonitoredUsedResources(new ReportData(CPU, datacenterName, null, hostId, null, currentTime, cpuUtilization, 0));
            }
        }
    }
    
    /**
     * Inserts monitored data into {@link #monitoredUsedResources}.
     * 
     * @param   data    the data to be inserted.
     * @since   1.0
     */         
    private void insertMonitoredUsedResources(ReportData data) {
        List<ReportData> sampleList = new ArrayList<ReportData>();
        for(ReportData sample : this.monitoredUsedResources) {
            if(sample.getDatacenterName().equals(data.getDatacenterName()) &&
               sample.getHostId().equals(data.getHostId()) &&
               sample.getType().equals(data.getType())) {
                sampleList.add(sample);
            }
        }
        
        if(sampleList.size() >= SAMPLES) {
            ReportData olderSample = sampleList.get(0);
            for(ReportData sample : sampleList) {
                if(sample.getTime() < olderSample.getTime()) {
                    olderSample = sample;
                }
            }
            
            this.monitoredUsedResources.remove(olderSample);
        }
        
        this.monitoredUsedResources.add(data);
    }
    
    /**
     * Gets a list of monitored resources.
     * 
     * @param   datacenterName  the name of the monitored datacenter.
     * @param   hostId          the id of the monitored host.
     * @param   type            the type of the monitored resource.
     * @since   1.0
     */       
    public List<Double> getMonitoredUsedResources(String datacenterName, int hostId, String type) {
        List<Double> valuesList = new ArrayList<Double>();
        for (ReportData sample : this.monitoredUsedResources) {
            if (sample.getDatacenterName().equals(datacenterName)
                    && sample.getHostId().equals(hostId)
                    && sample.getType().equals(type)) {
                valuesList.add(sample.getAmount());
            }
        }
        
        return valuesList;
    }    
}
