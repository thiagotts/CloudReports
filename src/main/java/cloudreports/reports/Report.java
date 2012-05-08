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

import cloudreports.dao.CustomerRegistryDAO;
import cloudreports.dao.DatacenterRegistryDAO;
import cloudreports.dao.SettingDAO;
import cloudreports.database.HibernateUtil;
import cloudreports.extensions.PowerDatacenter;
import cloudreports.gui.reports.resources.images.Images;
import cloudreports.gui.reports.resources.js.JS;
import cloudreports.models.CustomerRegistry;
import cloudreports.models.DatacenterRegistry;
import cloudreports.utils.ElapsedTime;
import cloudreports.utils.FileIO;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import org.cloudbus.cloudsim.DatacenterBroker;

/**
 * Provides methods to generate a simulation report.
 * The reports are generated as a folder containing several HTML files and a 
 * raw data file.
 * 
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class Report {
    
    /** The report's base directory. */
    private static String baseDirectory;
    
    /** 
     * Generates a full report.
     * 
     * @param   datacentersList the list of simulated datacenters.
     * @param   brokersList     the list of simulated brokers.
     * @param   elapsedTime     the duration of the simulation.
     * @since           1.0
     */     
    public static void generateReport(List<PowerDatacenter> datacentersList, List<DatacenterBroker> brokersList, 
                                      ElapsedTime elapsedTime) throws IOException, URISyntaxException {
        
        baseDirectory = FileIO.getPathOfExecutable();        
        int simulationId = Integer.valueOf(new SettingDAO().getSetting("CurrentSimulation").getValue());
        baseDirectory += "reports/" + HibernateUtil.getActiveDatabase() + "/report" + simulationId;

        createDirectoryTree();        
        createOverallReport(datacentersList, brokersList, elapsedTime);
        createDatacentersReports(datacentersList, brokersList);
        createCustomersReports(brokersList);
        createLogReport(datacentersList, brokersList);
    }
    
    /** 
     * Creates the report's directory tree.
     * 
     * @throws  IOException         if any of the directories or files could 
     *                              not be created.
     * @throws  URISyntaxException  if any of the used paths could not be parsed
     *                              successfully.
     * @since                       1.0
     */     
    private static void createDirectoryTree() throws IOException, URISyntaxException {
        File tempDir = new File(baseDirectory + "/provider");
        tempDir.mkdirs();
        tempDir = new File(baseDirectory + "/customers");
        tempDir.mkdir();
        tempDir = new File(baseDirectory + "/log");
        tempDir.mkdir();
        tempDir = new File(baseDirectory + "/raw");
        tempDir.mkdir();
        File tempRawData = new File(baseDirectory + "/raw/rawData.crd");
        if(tempRawData.exists()) tempRawData.delete();
        tempRawData.createNewFile();
        
        //Create the CSS directory and file
        tempDir = new File(baseDirectory + "/css");
        tempDir.mkdir();
        String tempString = FileIO.readStringFromResource("cloudreports/gui/reports/resources/css/style.css");
        FileIO.writeStringToFile(baseDirectory + "/css/style.css", tempString);
        
        //Create the images directory and files
        tempDir = new File(baseDirectory + "/images");
        tempDir.mkdir();
        String[] tempFilesArray = FileIO.getResourceListing(Images.class, "cloudreports/gui/reports/resources/images/");
        for(String tempFile : tempFilesArray) {
            if (tempFile.isEmpty()) continue;
            byte[] tempBytes = FileIO.readBytesFromFile("cloudreports/gui/reports/resources/images/" + tempFile);
            FileIO.writeBytesToFile(baseDirectory + "/images/" + tempFile, tempBytes);
        }
        
        //Create the js directory and files
        tempDir = new File(baseDirectory + "/js");
        tempDir.mkdir();
        tempFilesArray = FileIO.getResourceListing(JS.class, "cloudreports/gui/reports/resources/js/");
        for(String tempFile : tempFilesArray) {
            if (tempFile.isEmpty()) continue;
            tempString = FileIO.readStringFromResource("cloudreports/gui/reports/resources/js/" + tempFile);
            FileIO.writeStringToFile(baseDirectory + "/js/" + tempFile, tempString);
        }        
        
    }    
    
    /** 
     * Creates the overall report.
     * This part of the report contains general information about the simulation
     * and includes overall data about datacenters and customers.
     * 
     * @param   datacentersList     the list of simulated datacenters.
     * @param   brokersList         the list of simulated brokers.
     * @param   elapsedTime         the duration of the simulation.
     * @throws  IOException         if any of the directories or files could 
     *                              not be created.
     * @throws  URISyntaxException  if any of the used paths could not be parsed
     *                              successfully.
     * @since                       1.0
     */     
    private static void createOverallReport(List<PowerDatacenter> datacentersList, List<DatacenterBroker> brokersList, 
                                            ElapsedTime elapsedTime) throws IOException, URISyntaxException {
        String html = FileIO.readStringFromResource("cloudreports/gui/reports/resources/index.html");

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        String timeAndDate = sdf.format(Calendar.getInstance().getTime());
        html = html.replace("<!--INSERT_TIME-->", timeAndDate.split(" ")[0]);
        html = html.replace("<!--INSERT_DATE-->", timeAndDate.split(" ")[1]);
        
        StringBuilder overallInformation = new StringBuilder();
        overallInformation.append("Simulation has finished in ")
                          .append(elapsedTime.toString())
                          .append(".");
        
        //Insert datacenters' overall information
        DatacenterRegistryDAO drDAO = new DatacenterRegistryDAO();
        for (PowerDatacenter datacenter : datacentersList) {
            DatacenterRegistry dcRegistry = drDAO.getDatacenterRegistry(datacenter.getName());            
            overallInformation.append("<br/><br/><strong>")
                              .append(datacenter.getName())
                              .append(":</strong><br/>Allocation policy: ")
                              .append(dcRegistry.getAllocationPolicyAlias())
                              .append("<br/>Number of hosts: ")
                              .append(drDAO.getNumOfHosts(dcRegistry.getId()))
                              .append("<br/>Number of migrations: ")
                              .append(datacenter.getMigrationCount());
        }
        
        //Insert customers' overall information
        CustomerRegistryDAO crDAO = new CustomerRegistryDAO();
        for(DatacenterBroker broker : brokersList) {
            CustomerRegistry custRegistry = crDAO.getCustomerRegistry(broker.getName());
            overallInformation.append("<br/><br/><strong>")
                    .append(broker.getName())
                    .append(":</strong><br/>Broker policy: ")
                    .append(custRegistry.getUtilizationProfile().getBrokerPolicyAlias())
                    .append("<br/>Number of virtual machines: ")
                    .append(crDAO.getNumOfVms(custRegistry.getId()));
        }
        
        overallInformation.append("<br/><br/>");
        html = html.replace("<!--INSERT_GENERAL_INFORMATION-->", overallInformation.toString());
        FileIO.writeStringToFile(baseDirectory + "/index.html", html);
    }

    /** 
     * Creates a report for each of the simulated datacenters.
     * 
     * @param   datacentersList     the list of simulated datacenters.
     * @param   brokersList         the list of simulated brokers.
     * @throws  IOException         if any of the directories or files could 
     *                              not be created.
     * @throws  URISyntaxException  if any of the used paths could not be parsed
     *                              successfully.
     * @since                       1.0
     */       
    private static void createDatacentersReports(List<PowerDatacenter> datacentersList,
                                                 List<DatacenterBroker> brokersList) throws IOException, URISyntaxException {
        String html = FileIO.readStringFromResource("cloudreports/gui/reports/resources/datacenters.html");
        
        StringBuilder datacenterOptions = new StringBuilder();
        StringBuilder datacentersHtmlList = new StringBuilder();
        for(PowerDatacenter datacenter : datacentersList) {
            //Insert option element
            datacenterOptions.append("<option value=\"datacenter_");
            datacenterOptions.append(datacenter.getName());
            datacenterOptions.append("\">");
            datacenterOptions.append(datacenter.getName());
            datacenterOptions.append("</option>\n");
            
            //Insert this datacenter's html report and raw data
            DatacenterReport datacenterReport = new DatacenterReport(datacenter, brokersList);
            if(datacentersList.indexOf(datacenter) == 0) datacentersHtmlList.append(datacenterReport.getHtml().replace("id=\"datacenter_" + datacenter.getName() + "\" style=\"display: none;\"", "id=\"datacenter_" + datacenter.getName() + "\""));
            else datacentersHtmlList.append(datacenterReport.getHtml());
            FileIO.appendStringToFile(baseDirectory + "/raw/rawData.crd", datacenterReport.getRawData());
        }
        
        html = html.replace("<!--INSERT_DATACENTER_OPTIONS-->", datacenterOptions.toString());
        html = html.replace("<!--INSERT_DATACENTERS_LIST-->", datacentersHtmlList.toString());
        
        FileIO.writeStringToFile(baseDirectory + "/provider/datacenters.html", html);
    }

    /** 
     * Creates a report for each of the simulated customers.
     * 
     * @param   brokersList         the list of simulated brokers.
     * @throws  IOException         if any of the directories or files could 
     *                              not be created.
     * @throws  URISyntaxException  if any of the used paths could not be parsed
     *                              successfully.
     * @since                       1.0
     */     
    private static void createCustomersReports(List<DatacenterBroker> brokersList) throws IOException, URISyntaxException {
        String html = FileIO.readStringFromResource("cloudreports/gui/reports/resources/customers.html");

        StringBuilder customerOptions = new StringBuilder();
        StringBuilder customersHtmlList = new StringBuilder();
        for (DatacenterBroker broker : brokersList) {
            //Insert option element
            customerOptions.append("<option value=\"customer_");
            customerOptions.append(broker.getName());
            customerOptions.append("\">");
            customerOptions.append(broker.getName());
            customerOptions.append("</option>\n");

            //Insert this customer's html report and raw data
            CustomerReport customerReport = new CustomerReport(broker);
            if(brokersList.indexOf(broker) == 0) customersHtmlList.append(customerReport.getHtml().replace("id=\"customer_" + broker.getName() + "\" style=\"display: none;\"", "id=\"customer_" + broker.getName() + "\""));
            else customersHtmlList.append(customerReport.getHtml());
            FileIO.appendStringToFile(baseDirectory + "/raw/rawData.crd", customerReport.getRawData());
        }

        html = html.replace("<!--INSERT_CUSTOMER_OPTIONS-->", customerOptions.toString());
        html = html.replace("<!--INSERT_CUSTOMERS_LIST-->", customersHtmlList.toString());

        FileIO.writeStringToFile(baseDirectory + "/customers/customers.html", html);
    }

    /** 
     * Creates the log report.
     * 
     * @param   datacentersList     the list of simulated datacenters.
     * @param   brokersList         the list of simulated brokers.
     * @throws  IOException         if any of the directories or files could 
     *                              not be created.
     * @throws  URISyntaxException  if any of the used paths could not be parsed
     *                              successfully.
     * @since                       1.0
     */         
    private static void createLogReport(List<PowerDatacenter> datacentersList,
                                        List<DatacenterBroker> brokersList) throws IOException, URISyntaxException {
        LogReport logReport = new LogReport(datacentersList, brokersList);
        FileIO.writeStringToFile(baseDirectory + "/log/log.html", logReport.getHtml());
    }
    
}
