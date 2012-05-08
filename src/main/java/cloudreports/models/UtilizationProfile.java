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
 * Represents the resources utilization profile of a customer.
 * It stores the customer's broker policy, cloudlets configuration and
 * utilization models.
 * 
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class UtilizationProfile implements Serializable{

    /** The profile's id. */
    private long id;
    
    /** The broker policy alias. */
    private String brokerPolicyAlias;
    
    /** The time zone. */
    private double timeZone;
    
    /** The number of cloudlets. */
    private int numOfCloudlets;
    
    /** The maximum length of a cloudlet. */
    private long length;
    
    /** The cloudlets' file size. */
    private long fileSize;
    
    /** The cloudlets' output size. */
    private long outputSize;
    
    /** The number of processing elements required to process a cloudlet. */
    private int cloudletsPesNumber;
    
    /** The CPU utilization model alias. */
    private String utilizationModelCpuAlias;
    
    /** The RAM utilization model alias. */
    private String utilizationModelRamAlias;
    
    /** The bandwidth utilization model alias. */
    private String utilizationModelBwAlias;
    
    /** The time to send the next cloudlet. */
    private double timeToSend; //TODO: replace with list or array with distribution-specific values

    /** The default constructor. */
    public UtilizationProfile() {
        setBrokerPolicyAlias("Round robin");
        setTimeZone(-3.0);
        setNumOfCloudlets(50);
        setLength(50000);
        setFileSize(500);
        setOutputSize(500);
        setCloudletsPesNumber(1);
        setUtilizationModelCpuAlias("Full");
        setUtilizationModelRamAlias("Full");
        setUtilizationModelBwAlias("Full");
    }

    /**
     * Gets the profile's id.
     * 
     * @return the profile's id.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the profile's id.
     * 
     * @param   id  the profile's id.
     */
    public void setId(long id) {
        this.id = id;
    }
    
    /**
     * Gets the profile's time zone.
     * 
     * @return the profile's time zone.
     */
    public double getTimeZone() {
        return timeZone;
    }

    /**
     * Sets the profile's time zone.
     * 
     * @param   timeZone    the profile's time zone.
     */
    public void setTimeZone(double timeZone) {
        this.timeZone = timeZone;
    }

    /**
     * Gets the number of cloudlets.
     * 
     * @return the number of cloudlets.
     */
    public int getNumOfCloudlets() {
        return numOfCloudlets;
    }

    /**
     * Sets the number of cloudlets.
     * 
     * @param   numOfCloudlets  the number of cloudlets.
     */
    public void setNumOfCloudlets(int numOfCloudlets) {
        this.numOfCloudlets = numOfCloudlets;
    }

    /**
     * Gets the maximum length of a cloudlet.
     * 
     * @return the maximum length of a cloudlet.
     */
    public long getLength() {
        return length;
    }

    /**
     * Sets the maximum length of a cloudlet.
     * 
     * @param   length  the maximum length of a cloudlet.
     */
    public void setLength(long length) {
        this.length = length;
    }

    /**
     * Gets the cloudlets' file size.
     * 
     * @return the cloudlets' file size.
     */
    public long getFileSize() {
        return fileSize;
    }

    /**
     * Sets the cloudlets' file size.
     * 
     * @param fileSize  the cloudlets' file size.
     */
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * Gets the cloudlets' output size.
     * 
     * @return the cloudlets' output size.
     */
    public long getOutputSize() {
        return outputSize;
    }

    /**
     * Sets the cloudlets' output size.
     * 
     * @param   outputSize  the cloudlets' output size.
     */
    public void setOutputSize(long outputSize) {
        this.outputSize = outputSize;
    }

    /**
     * Gets the number of processing elements required to run a cloudlet.
     * 
     * @return the number of processing elements required to run a cloudlet.
     */
    public int getCloudletsPesNumber() {
        return cloudletsPesNumber;
    }

    /**
     * Sets the number of processing elements required to run a cloudlet.
     * 
     * @param   pesNumber   the number of processing elements required to run
     *                      a cloudlet.
     */
    public void setCloudletsPesNumber(int pesNumber) {
        this.cloudletsPesNumber = pesNumber;
    }

    /**
     * Gets the profile's CPU utilization model.
     * 
     * @return the profile's CPU utilization model.
     */
    public String getUtilizationModelCpuAlias() {
        return utilizationModelCpuAlias;
    }

    /**
     * Sets the profile's CPU utilization model.
     * 
     * @param   utilizationModelCpuAlias    the profile's CPU utilization model.
     */
    public void setUtilizationModelCpuAlias(String utilizationModelCpuAlias) {
        this.utilizationModelCpuAlias = utilizationModelCpuAlias;
    }

    /**
     * Gets the profile's RAM utilization model.
     * 
     * @return the profile's RAM utilization model.
     */
    public String getUtilizationModelRamAlias() {
        return utilizationModelRamAlias;
    }

    /**
     * Sets the profile's RAM utilization model.
     * 
     * @param   utilizationModelRamAlias    the profile's RAM utilization model.
     */
    public void setUtilizationModelRamAlias(String utilizationModelRamAlias) {
        this.utilizationModelRamAlias = utilizationModelRamAlias;
    }

    /**
     * Gets the profile's bandwidth utilization model.
     * 
     * @return the profile's bandwidth utilization model.
     */
    public String getUtilizationModelBwAlias() {
        return utilizationModelBwAlias;
    }

    /**
     * Sets the profile's bandwidth utilization model.
     * 
     * @param   utilizationModelBwAlias the profile's bandwidth utilization
     *                                  model.
     */
    public void setUtilizationModelBwAlias(String utilizationModelBwAlias) {
        this.utilizationModelBwAlias = utilizationModelBwAlias;
    }

    /**
     * Gets the profile's broker policy alias.
     * 
     * @return the profile's broker policy alias.
     */
    public String getBrokerPolicyAlias() {
        return brokerPolicyAlias;
    }

    /**
     * Sets the profile's broker policy alias.
     * 
     * @param   brokerPolicyAlias   the profile's broker policy alias.
     */
    public void setBrokerPolicyAlias(String brokerPolicyAlias) {
        this.brokerPolicyAlias = brokerPolicyAlias;
    }
    
    /**
     * Gets the time to send the next cloudlet.
     * 
     * @return the time to send the next cloudlet.
     */
    public double getTimeToSend() {
        return timeToSend;
    }

    /**
     * Sets the time to send the next cloudlet.
     * 
     * @param   timeToSend  the time to send the next cloudlet.
     */
    public void setTimeToSend(double timeToSend) {
        this.timeToSend = timeToSend;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("Broker Policy ID="+getBrokerPolicyAlias()+"\n");
        s.append("Time Zone (GMT)="+getTimeZone()+"\n");
        s.append("Cloudlets per minute="+getNumOfCloudlets()+"\n");
        s.append("Max length="+getLength()+"\n");
        s.append("Max File Size="+getFileSize()+"\n");
        s.append("Max Output Size="+getOutputSize()+"\n");
        s.append("Cloudlets PEs="+getCloudletsPesNumber()+"\n");
        s.append("CPU UM="+getUtilizationModelCpuAlias()+"\n");
        s.append("RAM UM ="+getUtilizationModelRamAlias()+"\n");
        s.append("Bandwidth UM ="+getUtilizationModelBwAlias()+"\n");

        return s.toString();
    }
}
