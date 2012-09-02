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
import cloudreports.extensions.PowerDatacenter;
import cloudreports.models.Migration;
import cloudreports.utils.FileIO;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.power.PowerHost;

/**
 * Provides methods to generate simulation reports with information about 
 * usage of resources by datacenters.
 * 
 * @author      Thiago T. Sá
 * @since       1.0
 */
class DatacenterReport {

    /** The name of the datacenter. */
    private String name;
    
	/** Number of executed cloudlets per host. */
    private TreeMap<String,Double> executedCloudlets;
    
    /** Number of deployed virtual machines per host. */
    private TreeMap<String,Double> deployedVms;
    
    /** Costs generated per customer. */
    private TreeMap<String,Double> costs;
    
    /** RAM utilization per host. */
    private HashMap<Integer, TreeMap<Double, Double>> hostsUsedRam;
    
    /** CPU utilization per host. */
    private HashMap<Integer, TreeMap<Double, Double>> hostsUsedCpu;
    
    /** Bandwidth utilization per host. */
    private HashMap<Integer, TreeMap<Double, Double>> hostsUsedBandwidth;
    
    /** Power consumption per host. */
    private HashMap<Integer, TreeMap<Double, Double>> hostsUsedPower;
    
    /** Overall usage of RAM by the datacenter. */
    private TreeMap<Double, Double> overallUsedRam;
    
    /** Overall usage of CPU by the datacenter. */
    private TreeMap<Double, Double> overallUsedCpu;
    
    /** Overall usage of bandwidth by the datacenter. */
    private TreeMap<Double, Double> overallUsedBandwidth;
    
    /** Overall power consumption by the datacenter. */
    private TreeMap<Double, Double> overallUsedPower;
    
    /** The HTML version of the report. */
    private String html;
    
    /** The raw data.
     *  This is useful for importing simulation data to other softwares, such
     *  as Octave or MATLAB.
     */
    private String rawData;
    
    /** 
     * Creates a datacenter report.
     * 
     * @param   datacenter      the datacenter.
     * @param   brokersList     a list of all brokers.
     * @since                   1.0
     */           
    public DatacenterReport(PowerDatacenter datacenter, List<DatacenterBroker> brokersList, boolean htmlReportsEnabled,
    						boolean rawDataReportsEnabled) throws IOException, URISyntaxException {        
        this.name = datacenter.getName();
        
        // Get all hosts resource utilization data from the database
        this.hostsUsedRam =  new HashMap<Integer, TreeMap<Double, Double>>();
        this.hostsUsedCpu = new HashMap<Integer, TreeMap<Double, Double>>();
        this.hostsUsedBandwidth = new HashMap<Integer, TreeMap<Double, Double>>();
        this.hostsUsedPower = new HashMap<Integer, TreeMap<Double, Double>>();
        List<PowerHost> hostsList = datacenter.getHostList();
        ReportDataDAO rdDAO = new ReportDataDAO();
        for(PowerHost host : hostsList) {
            int hostId = host.getId();
            hostsUsedRam.put(hostId, rdDAO.getHostUsedResources("RAM", this.name, hostId));
            hostsUsedCpu.put(hostId, rdDAO.getHostUsedResources("CPU", this.name, hostId));
            hostsUsedBandwidth.put(hostId, rdDAO.getHostUsedResources("BANDWIDTH", this.name, hostId));
            hostsUsedPower.put(hostId, rdDAO.getHostUsedResources("POWER", this.name, hostId));
        }
        
        //Get the overall resource utilization data from the database
        this.overallUsedRam = rdDAO.getDatacenterOverallData("RAM", this.name);
        this.overallUsedCpu = rdDAO.getDatacenterOverallData("CPU", this.name);
        this.overallUsedBandwidth = rdDAO.getDatacenterOverallData("BANDWIDTH", this.name);
        this.overallUsedPower = rdDAO.getDatacenterOverallData("POWER", this.name);
        
        
        executedCloudlets = new TreeMap<String, Double>();
        deployedVms = new TreeMap<String, Double>();
        costs = new TreeMap<String, Double>();        
        for(DatacenterBroker broker : brokersList) {
            //Set of IDs from virtual machines deployed by this datacenter
            Set<Integer> vmIds = new HashSet<Integer>();
            
            // Compute all cloudlets executed in this datacenter
            executedCloudlets.put(broker.getName(), 0D);            
            List<Cloudlet> cloudletsList = broker.getCloudletSubmittedList();
            for(Cloudlet cloudlet : cloudletsList) {
                if(cloudlet.getResourceId() == datacenter.getId()) {
                    executedCloudlets.put(broker.getName(), executedCloudlets.get(broker.getName()) + 1);
                    vmIds.add(cloudlet.getVmId());
                } 
            }
            
            //Set the number of virtual machines from this customer deployed on this datacenter
            deployedVms.put(broker.getName(), Double.valueOf(String.valueOf(vmIds.size())));
            
            //Get the customer's debt on this datacenter
            costs.put(broker.getName(), datacenter.getDebts().get(broker.getId()));            
        }
        
        if(htmlReportsEnabled) generateHtml();
        if(rawDataReportsEnabled) generateRawData();        
    }

    /**
     * Gets the name of the datacenter.
     * 
     * @return  a string that contains the name of the datacenter.
     */
    public String getName() {
		return name;
	}
    
    /**
     * Gets the HTML version of the report.
     * 
     * @return  a string that contains the HTML version of the report.
     */
    public String getHtml() {
        return html;
    }

    /**
     * Gets the raw data.
     * 
     * @return  a string containing the report's raw data.
     */
    public String getRawData() {
        return rawData;
    }
    
    /**
     * Generates the HTML version of the datacenter's report.
     * It reads the template files and replaces the existing tags with real
     * simulation data. The resulting string is assigned to the {@link #html}
     * field.
     * 
     * @throws  IOException         If the template files could not be loaded
     *                              correctly.
     * @throws  URISyntaxException  If the path to the template files could not
     *                              be parsed successfully.
     * @since                       1.0
     */        
    private void generateHtml() throws IOException, URISyntaxException {
        this.html = FileIO.readStringFromResource("cloudreports/gui/reports/resources/datacenter");
        
        StringBuilder hostResUtilizationOptions = new StringBuilder();
        StringBuilder hostsResUtilization = new StringBuilder();
        StringBuilder hostPowerConsumptionOptions = new StringBuilder();
        StringBuilder hostsPowerConsumption = new StringBuilder();
        List<Integer> hostIds = Arrays.asList(hostsUsedRam.keySet().toArray(new Integer[0]));
        for(Integer hostId : hostIds) {
            hostResUtilizationOptions.append("<option value=\"resource_utilization_")
                                     .append(this.name).append("_").append("Host").append(hostId)
                                     .append("\">Host").append(hostId).append("</option>\n");
            
            hostPowerConsumptionOptions.append("<option value=\"power_consumption_")
                                       .append(this.name).append("_").append("Host").append(hostId)
                                       .append("\">Host").append(hostId).append("</option>\n");
            
            //Create host resource utilization html
            String resHtml = FileIO.readStringFromResource("cloudreports/gui/reports/resources/host_resource_utilization");
            resHtml = resHtml.replace("<!--INSERT_HOST_NAME-->", "Host"+hostId);
            resHtml = resHtml.replace("<!--INSERT_RAM_DATA-->", getDataString(hostsUsedRam.get(hostId)));
            resHtml = resHtml.replace("<!--INSERT_CPU_DATA-->", getDataString(hostsUsedCpu.get(hostId)));
            resHtml = resHtml.replace("<!--INSERT_BANDWIDTH_DATA-->", getDataString(hostsUsedBandwidth.get(hostId)));
            hostsResUtilization.append(resHtml);
            
            //Create host power consumption html
            String powerHtml = FileIO.readStringFromResource("cloudreports/gui/reports/resources/host_power_consumption");
            powerHtml = powerHtml.replace("<!--INSERT_HOST_NAME-->", "Host"+hostId);
            powerHtml = powerHtml.replace("<!--INSERT_POWER_CONSUMPTION_DATA-->", getDataString(hostsUsedPower.get(hostId)));
            hostsPowerConsumption.append(powerHtml);
        }        
        //Insert host resource utilization options
        html = html.replace("<!--INSERT_HOST_RESOURCE_UTILIZATION_OPTIONS-->", hostResUtilizationOptions.toString());
        //Insert hosts resource utilization
        html = html.replace("<!--INSERT_HOST_RESOURCE_UTILIZATION_LIST-->", hostsResUtilization.toString()) ;
        
        //Insert host power consumption options
        html = html.replace("<!--INSERT_HOST_POWER_CONSUMPTION_OPTIONS-->", hostPowerConsumptionOptions.toString());        
        //Insert hosts power consumption
        html = html.replace("<!--INSERT_HOST_POWER_CONSUMPTION_LIST-->", hostsPowerConsumption.toString());
        
        //Insert overall resource utilization data
        html = html.replace("<!--INSERT_OVERALL_RAM_DATA-->", getDataString(overallUsedRam));
        html = html.replace("<!--INSERT_OVERALL_CPU_DATA-->", getDataString(overallUsedCpu));
        html = html.replace("<!--INSERT_OVERALL_BANDWIDTH_DATA-->", getDataString(overallUsedBandwidth));
        
        //Insert overall power consumption data
        html = html.replace("<!--INSERT_OVERALL_POWER_CONSUMPTION_DATA-->", getDataString(overallUsedPower));
        
        //Insert virtual machines data
        html = html.replace("<!--INSERT_VIRTUAL_MACHINES_DATA-->", getDataAndLabelString(deployedVms));
        
        //Insert cloudlets data
        html = html.replace("<!--INSERT_CLOUDLETS_DATA-->", getDataAndLabelString(executedCloudlets));
        
        //Insert costs data
        html = html.replace("<!--INSERT_COSTS_DATA-->", getDataAndLabelString(costs));
        
        //Insert migrations data
        html = html.replace("<!--INSERT_MIGRATIONS_DATA-->", getMigrationsString());
        
        //Insert datacenter's name
        html = html.replace("<!--INSERT_DATACENTER_NAME-->", this.name);    
    }
    
    /**
     * Converts a time-value map of generic data to a string format that can be
     * parsed by the Flot library.
     * 
     * @param   dataMap a map of generic report data.
     * @return          a formated string containing the data of the map.
     * @see             <a href="http://code.google.com/p/flot/">The Flot library</a>
     * @since           1.0
     */      
    private String getDataString(TreeMap<Double, Double> dataMap) {
        StringBuilder dataStringBuilder = new StringBuilder("[");
        Iterator<Double> iterator = dataMap.keySet().iterator();
        while (iterator.hasNext()) {
            double time = iterator.next();
            dataStringBuilder.append("[").append(time).append(",")
                             .append(dataMap.get(time)).append("],");
        }
        dataStringBuilder.deleteCharAt(dataStringBuilder.lastIndexOf(","));
        dataStringBuilder.append("]");
        
        return dataStringBuilder.toString();
    }
    
    /**
     * Converts a label-value map of generic data to a string format that can 
     * be parsed by the Flot library.
     * 
     * @param   dataMap a map of generic report data.
     * @return          a formated string containing the data of the map.
     * @see             <a href="http://code.google.com/p/flot/">The Flot library</a>
     * @since           1.0
     */       
    private String getDataAndLabelString(TreeMap<String,Double> dataMap) {
        StringBuilder dataStringBuilder = new StringBuilder("[");
        Iterator<String> iterator = dataMap.keySet().iterator();
        int i = -1;
        while (iterator.hasNext()) {
            i += 2;
            String customer = iterator.next();
            dataStringBuilder.append("{ data: [[")
                             .append(i) //The x label is not shown here
                             .append(",").append(dataMap.get(customer))
                             .append("]], label: \"").append(customer)
                             .append("\"},");
        }
        dataStringBuilder.deleteCharAt(dataStringBuilder.lastIndexOf(","));
        dataStringBuilder.append("]");
        
        return dataStringBuilder.toString();        
    }
    
    /**
     * Generates the report's raw data and assigns it to the {@link #rawData}
     * field.
     * 
     * @since           1.0
     */           
    private void generateRawData() throws IOException, URISyntaxException {
        StringBuilder rawDataStringBuilder = new StringBuilder();
        int simulationId = Integer.valueOf(new SettingDAO().getSetting("CurrentSimulation").getValue());
        
        rawDataStringBuilder.append(getRawDataString(overallUsedRam, "Sim" + simulationId + "_" + this.name + "_overall_ram"));
        rawDataStringBuilder.append(getRawDataString(overallUsedCpu, "Sim" + simulationId + "_" + this.name + "_overall_cpu"));
        rawDataStringBuilder.append(getRawDataString(overallUsedBandwidth, "Sim" + simulationId + "_" + this.name + "_overall_bw"));
        rawDataStringBuilder.append(getRawDataString(overallUsedPower, "Sim" + simulationId + "_" + this.name + "_overall_power"));        
        
        List<Integer> hostIds = Arrays.asList(hostsUsedRam.keySet().toArray(new Integer[0]));
        for (Integer hostId : hostIds) {
            rawDataStringBuilder.append(getRawDataString(hostsUsedRam.get(hostId), "Sim" + simulationId + "_" + this.name + "_host" + hostId + "_ram"));
            rawDataStringBuilder.append(getRawDataString(hostsUsedCpu.get(hostId), "Sim" + simulationId + "_" + this.name + "_host" + hostId + "_cpu"));
            rawDataStringBuilder.append(getRawDataString(hostsUsedBandwidth.get(hostId), "Sim" + simulationId + "_" + this.name + "_host" + hostId + "_bw"));
            rawDataStringBuilder.append(getRawDataString(hostsUsedPower.get(hostId), "Sim" + simulationId + "_" + this.name + "_host" + hostId + "_power"));
        }
        
        this.rawData = rawDataStringBuilder.toString();        
    }
    
    /**
     * Converts a time-value map to a string that can be easily parsed by a
     * third party software.
     * 
     * @since           1.0
     */     
    private String getRawDataString(TreeMap<Double, Double> dataMap, String label) {
        StringBuilder timeStringBuilder = new StringBuilder("\n" + label + "_time\t");
        StringBuilder valuesStringBuilder = new StringBuilder("\n" + label + "_values\t");
        Iterator<Double> iterator = dataMap.keySet().iterator();
        while (iterator.hasNext()) {
            double time = iterator.next();
            timeStringBuilder.append(time).append("\t");
            valuesStringBuilder.append(dataMap.get(time)).append("\t");
        }
        
        return timeStringBuilder.toString() + valuesStringBuilder.toString();
    }
    
    /**
     * Gets a string that describes all migrations performed by this datacenter.
     * 
     * @return  a string containing a description of all performed migrations.
     * @since   1.0
     */        
    private String getMigrationsString() {        
        MigrationDAO mDAO = new MigrationDAO();
        List<Migration> migrationList = mDAO.getMigrationList(this.name);
        StringBuilder migrationStringBuilder = new StringBuilder("<br/>Number of migrations: ");
        migrationStringBuilder.append(migrationList.size()).append("<br/>");
        
        for(Migration migration : migrationList) {
            migrationStringBuilder.append("<br/><hr>")
                           .append("<strong>Migration ")
                           .append(migrationList.indexOf(migration))
                           .append(":</strong><br/>")
                           .append("Description: ").append(migration.getDescription())
                           .append("<br/><strong>").append(migration.getVmLabel())
                           .append("</strong> from <strong>").append(migration.getSourceHostLabel())
                           .append("</strong> to <strong>").append(migration.getTargetHostLabel())
                           .append("</strong> at <strong>").append(migration.getTime()/60)
                           .append("</strong> minutes.<br/>")
                           .append("Source host was consuming ").append(migration.getSourceHostCpuUtilization()*100)
                           .append("% of CPU, ").append(migration.getSourceHostRamUtilization()*100)
                           .append("% of RAM and ").append(migration.getSourceHostPowerConsumption()*100)
                           .append("% of power.<br/>")
                           .append("Target host was consuming ").append(migration.getTargetHostCpuUtilization()*100)
                           .append("% of CPU, ").append(migration.getTargetHostRamUtilization()*100)
                           .append("% of RAM and ").append(migration.getTargetHostPowerConsumption()*100)
                           .append("% of power.<br/>");
        }
        migrationStringBuilder.append("<br/><br/>");
        
        return migrationStringBuilder.toString();
    }
    
}
