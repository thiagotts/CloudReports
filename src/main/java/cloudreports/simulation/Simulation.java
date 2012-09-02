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
import cloudreports.dao.SettingDAO;
import cloudreports.database.HibernateUtil;
import cloudreports.extensions.PowerDatacenter;
import cloudreports.gui.Dialog;
import cloudreports.gui.MainView;
import cloudreports.models.CustomerRegistry;
import cloudreports.models.Setting;
import cloudreports.reports.DataCollector;
import cloudreports.reports.Report;
import cloudreports.utils.*;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;

/**
 * The Simulation class is responsible for setting up the simulation
 * environments and running the all the simulations.
 * 
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class Simulation implements Runnable {

    /** Indicates whether the simulations are over or not. */
    private static boolean terminated;
    
    /** A data collector. */
    private static DataCollector dataCollector;
    
    /** Indicates whether the mail notification feature is enabled or not. */
    private boolean isMailNotificationEnabled;
    
    /**
     * Gets a value that indicates whether the simulations are over or not.
     * 
     * @return  a boolean that indicates whether the simulations are over or not.
     */    
    public static boolean hasTerminated() {
        return terminated;
    }

    /**
     * Gets the data collector.
     * 
     * @return  the data collector.
     */    
    public static DataCollector getDataCollector() {
        return dataCollector;
    }

    /** 
     * Loads each simulation environment and run its simulations.
     * 
     * @since           1.0
     */        
    @Override
    public void run() {
        terminated = false;
        MainView.setStartButtonEnabled(false);
        MainView.getSimulationView().setStateToInProgress();
        String[] dbNames = MainView.getEnvironmentsNames();
        double startTime = Calendar.getInstance().getTimeInMillis();

        CloudSim.init(getNumberOfCustomers(), Calendar.getInstance(), false);
        CloudSim.startSimulation();
        for (String dbName : dbNames) {
            HibernateUtil.setActiveDatabase(dbName + ".cre");
            runSimulations();
        }

        double finishTime = Calendar.getInstance().getTimeInMillis();
        ElapsedTime elapsedTime = new ElapsedTime(finishTime - startTime);
        MainView.getSimulationView().setStateToComplete(elapsedTime);
        Audio.playAudioFromResource("cloudreports/gui/resources/beep.wav");
        MainView.setStartButtonEnabled(true);
    }

    /** 
     * Runs all simulations of a specific environment.
     * It creates all CloudSim entities, runs the simulation and generates the
     * report. This cycle is repeated the number of times indicated by the
     * NumberOfSimulations setting.
     * 
     * @since           1.0
     */       
    private void runSimulations() {
        SettingDAO sDAO = new SettingDAO();
        if (sDAO.getSetting("EnableMailNotification").getValue().equals("1")) {
            isMailNotificationEnabled = true;
        } else {
            isMailNotificationEnabled = false;
        }

        int numberOfSimulations = Integer.valueOf(sDAO.getSetting("NumberOfSimulations").getValue());
        String baseDirectory = FileIO.getPathOfExecutable();
        File reportsDirectory = new File(baseDirectory + "reports/" + HibernateUtil.getActiveDatabase());
        if(reportsDirectory.exists()) 
        	FileIO.deleteDirectory(reportsDirectory);
        reportsDirectory.mkdirs();

        //Begin of simulations loop
        for (int simulationId = 1; simulationId <= numberOfSimulations; simulationId++) {
            double currentSimulationStartTime = Calendar.getInstance().getTimeInMillis();
            Setting currentSimulation = sDAO.getSetting("CurrentSimulation");
            currentSimulation.setValue(String.valueOf(simulationId));
            sDAO.updateSetting(currentSimulation);

            MainView.getSimulationView().setBarLabel("Simulation " + simulationId + " of " + HibernateUtil.getActiveDatabase() + " is in progress...");

            Log.setOutput(LogIO.getFileOutputStream());
            Log.printLine("CloudReports version 1.0");
            Log.print("Verifying available resources...");

            DatacenterRegistryDAO drDAO = new DatacenterRegistryDAO();
            CustomerRegistryDAO crDAO = new CustomerRegistryDAO();

            for (CustomerRegistry cr : crDAO.getListOfCustomers()) {
                cr.getUtilizationProfile().setTimeToSend(0);
                crDAO.updateCustomerRegistry(cr);
            }

            if (Verification.verifyVMsDeploymentViability()) {
                Log.print("OK\n");
                CloudSim.init(getNumberOfCustomers(), Calendar.getInstance(), false);
                HashMap<String, PowerDatacenter> datacenters = EntityFactory.createDatacenters(drDAO.getListOfDatacenters());
                HashMap<String, DatacenterBroker> brokers = EntityFactory.createBrokers(crDAO.getListOfCustomers());
                if (datacenters == null || brokers == null) {
                    continue;
                }
                EntityFactory.setUpNetworkLinks(datacenters, brokers);
                try {
                    //Initiate simulation
                    Simulation.dataCollector = new DataCollector(datacenters, brokers);
                    CloudSim.startSimulation();

                    //Generate report
                    Simulation.dataCollector.flushData();
                    MainView.getSimulationView().setBarLabel("Generating report " + simulationId + "...");
                    double currentSimulationFinishTime = Calendar.getInstance().getTimeInMillis();
                    ElapsedTime elapsedTime = new ElapsedTime(currentSimulationFinishTime - currentSimulationStartTime);

                    List<DatacenterBroker> brokersList = Arrays.asList(brokers.values().toArray(new DatacenterBroker[0]));
                    List<PowerDatacenter> datacentersList = Arrays.asList(datacenters.values().toArray(new PowerDatacenter[0]));
                    Report.generateReport(datacentersList, brokersList, elapsedTime);

                    if (hasTerminated()) {
                        Dialog.showWarning(MainView.getFrames()[0], "Simulation has been abrubtly terminated.");
                    } else {
                        //Send mail notification
                        if (isMailNotificationEnabled) {
                            Mail.sendMail("Simulation " + simulationId + " of " + HibernateUtil.getActiveDatabase() + " completed.",
                                    "Simulation " + simulationId + " of " + HibernateUtil.getActiveDatabase() + " has completed in " + elapsedTime.toString() + ".");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    CloudSim.init(getNumberOfCustomers(), Calendar.getInstance(), false);
                    CloudSim.startSimulation();
                    Dialog.showWarning(null, "Simulation has been interrupted.\nReports may have been generated with inconsistent data.");

                    //Send mail notification
                    if (isMailNotificationEnabled) {
                        double currentFinishTime = Calendar.getInstance().getTimeInMillis();
                        ElapsedTime elapsedTime = new ElapsedTime(currentFinishTime - currentSimulationStartTime);
                        Mail.sendMail("Simulation " + simulationId + " of " + HibernateUtil.getActiveDatabase() + " failed.",
                                "Simulation " + simulationId + " of " + HibernateUtil.getActiveDatabase() + " has failed after " + elapsedTime.toString() + ":\n\n" + e.getMessage());
                    }

                    MainView.getSimulationView().dispose();
                    LogIO.removeTempLogFile();
                }
            } else {
                Dialog.showErrorMessage(null, "Simulation aborted:\nSome of the virtual machines cannot be deployed by any available host.");
                MainView.getSimulationView().dispose();
                break;
            }

            //Remove current log file
            LogIO.removeTempLogFile();
        }
        //End of simulations loop        
    }

    /** 
     * Stops all simulations.
     * 
     * @since           1.0
     */      
    public static void stopSimulation() {
        CloudSim.init(1, Calendar.getInstance(), false);
        CloudSim.startSimulation();
        CloudSim.stopSimulation();
        terminated = true;
    }

    /** 
     * Gets the number of simulated customers.
     * 
     * @return  the number of simulated customers.
     * @since   1.0
     */      
    private int getNumberOfCustomers() {
        CustomerRegistryDAO crDAO = new CustomerRegistryDAO();
        return crDAO.getNumOfCustomers();
    }

}
