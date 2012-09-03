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

import cloudreports.business.SettingBusiness;
import cloudreports.dao.ReportDataDAO;
import cloudreports.dao.SettingDAO;
import cloudreports.utils.FileIO;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Vm;

/**
 * Provides methods to generate simulation reports with information about 
 * usage of resources by customers.
 * 
 * @author      Thiago T. Sá
 * @since       1.0
 */
class CustomerReport {
    
    /** The name of the customer. */
    private String name;
    
	/** Number of cloudlets executed per virtual machine. */
    private TreeMap<String, Integer> cloudletsPerVm;
    
    /** Cloudlets' start and finish time of execution. */
    private TreeMap<Integer, List<Double>> cloudletsTimesOfExecution;
    
    /** RAM utilization per virtual machine. */
    private HashMap<Integer, TreeMap<Double, Double>> vmsUsedRam;
    
    /** CPU utilization per virtual machine. */
    private HashMap<Integer, TreeMap<Double, Double>> vmsUsedCpu;
    
    /** Bandwidth utilization per virtual machine. */
    private HashMap<Integer, TreeMap<Double, Double>> vmsUsedBandwidth;
    
    /** Overall usage of RAM by the customer. */
    private TreeMap<Double, Double> overallUsedRam;
    
    /** Overall usage of CPU by the customer. */
    private TreeMap<Double, Double> overallUsedCpu;
    
    /** Overall usage of bandwidth by the customer. */
    private TreeMap<Double, Double> overallUsedBandwidth;
    
    /** The HTML version of the report. */
    private String html;
    
    /** The raw data.
     *  This is useful for importing simulation data to other softwares, such
     *  as Octave or MATLAB.
     */
    private String rawData;

    /** 
     * Creates a customer report for a given broker.
     * 
     * @param   broker  the customer's broker.
     * @since           1.0
     */    
    public CustomerReport(DatacenterBroker broker, boolean htmlReportsEnabled, boolean rawDataReportsEnabled) throws IOException, URISyntaxException {
        this.name = broker.getName();
        
        //Get all virtual machines resource utilization data from the database
        this.vmsUsedRam = new HashMap<Integer, TreeMap<Double, Double>>();
        this.vmsUsedCpu = new HashMap<Integer, TreeMap<Double, Double>>();
        this.vmsUsedBandwidth = new HashMap<Integer, TreeMap<Double, Double>>();
        cloudletsPerVm = new TreeMap<String, Integer>();
        List<Vm> vmsList = broker.getVmList();
        ReportDataDAO rdDAO = new ReportDataDAO();
        for(Vm vm : vmsList) {
            int vmId = vm.getId();
            vmsUsedRam.put(vmId, rdDAO.getVmUsedResources("RAM", this.name, vmId));
            vmsUsedCpu.put(vmId, rdDAO.getVmUsedResources("CPU", this.name, vmId));
            vmsUsedBandwidth.put(vmId, rdDAO.getVmUsedResources("BANDWIDTH", this.name, vmId));
            cloudletsPerVm.put("VM"+vmId, 0);
        }
        
        //Get the overall resource utilization from this customer
        this.overallUsedRam = rdDAO.getCustomerOverallData("RAM", this.name);
        this.overallUsedCpu = rdDAO.getCustomerOverallData("CPU", this.name);
        this.overallUsedBandwidth = rdDAO.getCustomerOverallData("BANDWIDTH", this.name);
        
        cloudletsTimesOfExecution = new TreeMap<Integer, List<Double>>();
        List<Cloudlet> cloudletsList = broker.getCloudletSubmittedList();
        for(Cloudlet cloudlet : cloudletsList) {
            List<Double> timesOfExecution = new ArrayList<Double>();
            timesOfExecution.add(cloudlet.getExecStartTime());
            timesOfExecution.add(cloudlet.getFinishTime());
            cloudletsTimesOfExecution.put(cloudlet.getCloudletId(), timesOfExecution);
            cloudletsPerVm.put("VM"+cloudlet.getVmId(), cloudletsPerVm.get("VM"+cloudlet.getVmId()) + 1);
        }
        
        if(htmlReportsEnabled) generateHtml();
        if(rawDataReportsEnabled) generateRawData();
    }
    
    /**
     * Gets the name of the customer.
     * 
     * @return  a string that contains the name of the customer.
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
     * Generates the HTML version of the customer's report.
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
        this.html = FileIO.readStringFromResource("cloudreports/gui/reports/resources/customer");
        
        StringBuilder vmResUtilizationOptions = new StringBuilder();
        StringBuilder vmsResUtilization = new StringBuilder();
        List<Integer> vmIds = Arrays.asList(vmsUsedRam.keySet().toArray(new Integer[0]));
        for(Integer vmId : vmIds) {
            vmResUtilizationOptions.append("<option value=\"resource_utilization_")
                                   .append(this.name).append("_").append("VM")
                                   .append(vmId).append("\">VM").append(vmId)
                                   .append("</option>\n");
            
            //Create virtual machine resource utilization html
            String resHtml = FileIO.readStringFromResource("cloudreports/gui/reports/resources/vm_resource_utilization");
            resHtml = resHtml.replace("<!--INSERT_VM_NAME-->", "VM"+vmId);
            resHtml = resHtml.replace("<!--INSERT_RAM_DATA-->", getDataString(vmsUsedRam.get(vmId)));
            resHtml = resHtml.replace("<!--INSERT_CPU_DATA-->", getDataString(vmsUsedCpu.get(vmId)));
            resHtml = resHtml.replace("<!--INSERT_BANDWIDTH_DATA-->", getDataString(vmsUsedBandwidth.get(vmId)));
            vmsResUtilization.append(resHtml);
        }        
        //Insert virtual machine options
        html = html.replace("<!--INSERT_VM_RESOURCE_UTILIZATION_OPTIONS-->", vmResUtilizationOptions.toString());
        //Insert virtual machines resource utilization
        html = html.replace("<!--INSERT_VM_RESOURCE_UTILIZATION_LIST-->", vmsResUtilization.toString()) ;
        
        //Insert overall resource utilization data
        html = html.replace("<!--INSERT_OVERALL_RAM_DATA-->", getDataString(overallUsedRam));
        html = html.replace("<!--INSERT_OVERALL_CPU_DATA-->", getDataString(overallUsedCpu));
        html = html.replace("<!--INSERT_OVERALL_BANDWIDTH_DATA-->", getDataString(overallUsedBandwidth));
        
        //Insert cloudlets data
        html = html.replace("<!--INSERT_CLOUDLETS_DATA-->", getDataAndLabelString(cloudletsPerVm));
        
        //Insert execution times data
        String[] timeData = getCloudletsTimesDataString(cloudletsTimesOfExecution);
        if(timeData.length > 0) {
            html = html.replace("<!--INSERT_START_DATA-->", timeData[0]);
            html = html.replace("<!--INSERT_FINISH_DATA-->", timeData[1]);
            html = html.replace("<!--INSERT_AVERAGE_START_DATA-->", timeData[2]);
            html = html.replace("<!--INSERT_AVERAGE_FINISH_DATA-->", timeData[3]);
        }
        
        //Insert customer's name
        html = html.replace("<!--INSERT_CUSTOMER_NAME-->", this.name);    
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
            dataStringBuilder.append("[");
            dataStringBuilder.append(time);
            dataStringBuilder.append(",");
            dataStringBuilder.append(dataMap.get(time));
            dataStringBuilder.append("],");
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
    private String getDataAndLabelString(TreeMap<String,Integer> dataMap) {
        StringBuilder dataStringBuilder = new StringBuilder("[");
        Iterator<String> iterator = dataMap.keySet().iterator();
        int i = -1;
        while (iterator.hasNext()) {
            i += 2;
            String datacenter = iterator.next();
            dataStringBuilder.append("{ data: [[");
            dataStringBuilder.append(i); //The x label is not shown here
            dataStringBuilder.append(",");
            dataStringBuilder.append(dataMap.get(datacenter));
            dataStringBuilder.append("]], label: \"");
            dataStringBuilder.append(datacenter);
            dataStringBuilder.append("\"},");
        }
        dataStringBuilder.deleteCharAt(dataStringBuilder.lastIndexOf(","));
        dataStringBuilder.append("]");
        
        return dataStringBuilder.toString();        
    }    
    
    /**
     * Converts the map of cloudlets' execution times to an array of formatted
     * strings that can be parsed by the Flot library.
     * 
     * @param   dataMap the map of cloudlets' execution times.
     * @return          an array of formated strings containing the data of the map.
     * @see             <a href="http://code.google.com/p/flot/">The Flot library</a>
     * @since           1.0
     */        
    private String[] getCloudletsTimesDataString(TreeMap<Integer, List<Double>> dataMap) {
        double startSum = 0,
               finishSum = 0;
        StringBuilder startStringBuilder = new StringBuilder("[");
        StringBuilder finishStringBuilder = new StringBuilder("[");
        
        List<Integer> cloudletIdsList = Arrays.asList(dataMap.keySet().toArray(new Integer[0]));
        if(cloudletIdsList.isEmpty()) return new String[0];
        
        for(Integer cloudletId : cloudletIdsList) {
            List<Double> timesList = dataMap.get(cloudletId);
            
            startStringBuilder.append("[");
            startStringBuilder.append(cloudletId);
            startStringBuilder.append(",");
            startStringBuilder.append(timesList.get(0));
            startStringBuilder.append("],");
            startSum += timesList.get(0);
            
            finishStringBuilder.append("[");
            finishStringBuilder.append(cloudletId);
            finishStringBuilder.append(",");
            finishStringBuilder.append(timesList.get(1));
            finishStringBuilder.append("],");
            finishSum += timesList.get(1);
        }
        if(startStringBuilder.lastIndexOf(",") >= 0) startStringBuilder.deleteCharAt(startStringBuilder.lastIndexOf(","));
        startStringBuilder.append("]");
        if(finishStringBuilder.lastIndexOf(",") >= 0) finishStringBuilder.deleteCharAt(finishStringBuilder.lastIndexOf(","));
        finishStringBuilder.append("]");
        
        int numberOfCloudlets = cloudletIdsList.size();
        double averageStart = startSum/numberOfCloudlets;
        String averageStartData = "[[" + cloudletIdsList.get(0) + "," + averageStart
                                  + "],[" + cloudletIdsList.get(numberOfCloudlets-1) 
                                  + "," + averageStart + "]]";
        
        double averageFinish = finishSum/numberOfCloudlets;
        String averageFinishData = "[[" + cloudletIdsList.get(0) + "," + averageFinish
                                  + "],[" + cloudletIdsList.get(numberOfCloudlets-1) 
                                  + "," + averageFinish + "]]";
        
        return new String[] {startStringBuilder.toString(),
                             finishStringBuilder.toString(),
                             averageStartData,
                             averageFinishData};
    }    

    /**
     * Generates the report's raw data and assigns it to the {@link #rawData}
     * field.
     * 
     * @since           1.0
     */     
    private void generateRawData() {
        StringBuilder rawDataStringBuilder = new StringBuilder("");
        int simulationId = SettingBusiness.getCurrentSimulation();

        rawDataStringBuilder.append(getRawDataString(overallUsedRam, "Sim" + simulationId + "_" + this.name + "_overall_ram"));
        rawDataStringBuilder.append(getRawDataString(overallUsedCpu, "Sim" + simulationId + "_" + this.name + "_overall_cpu"));
        rawDataStringBuilder.append(getRawDataString(overallUsedBandwidth, "Sim" + simulationId + "_" + this.name + "_overall_bw"));

        List<Integer> vmIds = Arrays.asList(vmsUsedRam.keySet().toArray(new Integer[0]));
        for(Integer vmId : vmIds) {
            rawDataStringBuilder.append(getRawDataString(vmsUsedRam.get(vmId), "Sim" + simulationId + "_" + this.name + "_vm" + vmId + "_ram"));
            rawDataStringBuilder.append(getRawDataString(vmsUsedCpu.get(vmId), "Sim" + simulationId + "_" + this.name + "_vm" + vmId + "_cpu"));
            rawDataStringBuilder.append(getRawDataString(vmsUsedBandwidth.get(vmId), "Sim" + simulationId + "_" + this.name + "_vm" + vmId + "_bw"));
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

}
