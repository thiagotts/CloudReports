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

import cloudreports.extensions.PowerDatacenter;
import cloudreports.utils.FileIO;
import cloudreports.utils.LogIO;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Log;

/**
 * Provides methods to generate a log with general information
 * about the simulation process.
 * 
 * @author      Thiago T. Sá
 * @since       1.0
 */
class LogReport {

    /** The HTML version of the log. */
    private String html;   
    
    /** 
     * Generates a log.
     * 
     * @param   datacentersList     a list of all datacenters.
     * @param   brokersList         a list of all brokers.
     * @see                         LogIO
     * @since                       1.0
     */     
    LogReport(List<PowerDatacenter> datacentersList, List<DatacenterBroker> brokersList) throws IOException, URISyntaxException {
        html = FileIO.readStringFromResource("cloudreports/gui/reports/resources/log.html");

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        String timeAndDate = sdf.format(Calendar.getInstance().getTime());
        html = html.replace("<!--INSERT_TIME-->", timeAndDate.split(" ")[0]);
        html = html.replace("<!--INSERT_DATE-->", timeAndDate.split(" ")[1]);
        
        Log.printLine();
        for(DatacenterBroker broker : brokersList) {
            List<Cloudlet> list = broker.getCloudletSubmittedList();
            printCloudletList(list, broker.getName());
            Log.printLine("\n");
        }
        printDebts(datacentersList);
        
        String log = LogIO.getLogStringFromFile();
        log = log.replace("\n", "<br />");
        html = html.replace("<!--INSERT_LOG-->", log);
    }
    
    /**
     * Gets the HTML version of the log.
     * 
     * @return  a string that contains the HTML version of the log.
     */
    public String getHtml() {
        return html;
    }

    /**
     * Prints a HTML table containing information about cloudlets execution.
     * 
     * @param   list        the list of cloudlets whose information will be 
     *                      included in the table.
     * @param   brokerName  the name of the broker.
     */    
    private void printCloudletList(List<Cloudlet> list, String brokerName) {
        int size = list.size();
        Cloudlet cloudlet;

        String startColumn = "<td><center>";
        String endColumn = "</center></td>";
        Log.printLine("========== OUTPUT of " + brokerName + " ==========");
        Log.print("<table border=\"0\" style=\"color: #C2BEAD\"");
        Log.print("<tr>" + startColumn + "Cloudlet ID" + endColumn 
                    + startColumn + "STATUS" + endColumn 
                    + startColumn + "Resource ID" + endColumn 
                    + startColumn + "VM ID" + endColumn 
                    + startColumn + "Time" + endColumn 
                    + startColumn + "Start Time" + endColumn 
                    + startColumn + "Finish Time" + endColumn + "</tr>");

        DecimalFormat dft = new DecimalFormat("###.##");
        for (int i = 0; i < size; i++) {
            cloudlet = list.get(i);

            Log.print("<tr>" + startColumn + cloudlet.getCloudletId() + endColumn
                        + startColumn + cloudlet.getCloudletStatusString() + endColumn
                        + startColumn + cloudlet.getResourceId() + endColumn
                        + startColumn + cloudlet.getVmId() + endColumn
                        + startColumn + dft.format(cloudlet.getActualCPUTime()) + endColumn
                        + startColumn + dft.format(cloudlet.getExecStartTime()) + endColumn
                        + startColumn + dft.format(cloudlet.getFinishTime()) + endColumn + "</tr>");
        }
        Log.print("</table>");
    }

    /**
     * Prints a list of debts generated on each of the simulated datacenters.
     * 
     * @param   datacentersList     a list of datacenters.
     */      
    private void printDebts(List<PowerDatacenter> datacentersList) {
        Log.printLine();
        Log.print("========== Datacenters' Debts ==========\n");
        for(PowerDatacenter datacenter : datacentersList) {
            datacenter.printDebts();
            Log.print("\n");
        }
    }
    
}
