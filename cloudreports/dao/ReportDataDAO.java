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

package cloudreports.dao;

import cloudreports.database.Database;
import cloudreports.database.HibernateUtil;
import cloudreports.models.ReportData;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 * ReportDataDAO provides basic CRUD operations related to the 
 * {@link ReportData} class.
 * 
 * @see         ReportData
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class ReportDataDAO {
    
    /** 
     * Inserts report data related to resource usage of a given host.
     *
     * @param   type            the type of the used resource.
     * @param   datacenterName  the name of the datacenter that owns the host.
     * @param   hostId          the id of the host.
     * @param   time            the instant of time which the data being inserted refers to.
     * @param   amount          the amount of resources being used by the host at <code>time</code>.
     * @param   simulationId    the identification number of the simulation.
     * 
     * @see                     ReportData
     * @since                   1.0
     */    
    public void insertHostData(String type,String datacenterName, int hostId,
                               double time, double amount, int simulationId) {
        
        ReportData rd = new ReportData(type, datacenterName, null, hostId, null, time, amount, simulationId);
        
        Session session = HibernateUtil.getSession();        
        try {
            session.beginTransaction();
            session.save(rd);
            session.getTransaction().commit();
        }
        catch (HibernateException ex) {
            session.getTransaction().rollback();
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            HibernateUtil.closeSession(session);
        }        
    }
    
    /** 
     * Gets a report of resources usage of a given host.
     *
     * @param   type            the type of the used resource.
     * @param   datacenterName  the name of the datacenter that owns the host.
     * @param   hostId          the id of the host.
     * @return                  a map with values of time as keys and values of
     *                          used resources as values.
     * @see                     ReportData
     * @since                   1.0
     */    
    public TreeMap<Double, Double> getHostUsedResources(String type, String datacenterName, int hostId) {
        List<ReportData> dataList = null;
        Session session = HibernateUtil.getSession();
        try {
            dataList = (List<ReportData>) session.createCriteria(ReportData.class)
                                                 .add(Restrictions.conjunction()
                                                    .add(Restrictions.eq("type", type))
                                                    .add(Restrictions.eq("datacenterName", datacenterName))
                                                    .add(Restrictions.eq("hostId", hostId)))
                                                 .list();
        }
        catch (HibernateException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        
        TreeMap<Double, Double> hostUsedResources = new TreeMap<Double, Double>();
        if(dataList == null) return hostUsedResources;
        for(ReportData rd : dataList) {
            if(type.equals("BANDWIDTH")) {
                //If bandwidth, convert from kbps to Mbps
                hostUsedResources.put(rd.getTime()/60, rd.getAmount() / 1000);
            }
            else hostUsedResources.put(rd.getTime()/60, rd.getAmount());
        }
        
        return hostUsedResources;
    }  
    
    /**
     * Inserts report data related to resource usage of a given virtual machine.
     *
     * @param type          the type of the used resource.
     * @param customerName  the name of the customer that owns the virtual machine.
     * @param vmId          the id of the virtual machine.
     * @param time          the instant of time which the data being inserted refers to.
     * @param amount        the amount of resources being used by the virtual machine at
     *                      <code>time</code>.
     * @param simulationId  the identification number of the simulation.
     *
     * @see                 ReportData
     * @since               1.0
     */    
    public void insertVmData(String type,String customerName, int vmId, 
                             double time, double amount, int simulationId) {
        
        ReportData rd = new ReportData(type, null, customerName, null, vmId, time, amount, simulationId);
        
        Session session = HibernateUtil.getSession();
        try {
            session.beginTransaction();
            session.save(rd);
            session.getTransaction().commit();
        }
        catch (HibernateException ex) {
            session.getTransaction().rollback();
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
    }
    
    /** 
     * Gets a report of resources usage of a given virtual machine.
     *
     * @param   type            the type of the used resource.
     * @param   customerName    the name of the customer that owns the virtual machine.
     * @param   vmId            the id of the virtual machine.
     * @return                  a map with values of time as keys and values of
     *                          used resources as values.
     * @see                     ReportData
     * @since                   1.0
     */        
    public TreeMap<Double, Double> getVmUsedResources(String type, String customerName, int vmId) {
        List<ReportData> dataList = null;
        Session session = HibernateUtil.getSession();
        try {
            dataList = (List<ReportData>) session.createCriteria(ReportData.class)
                                                 .add(Restrictions.conjunction()
                                                    .add(Restrictions.eq("type", type))
                                                    .add(Restrictions.eq("customerName", customerName))
                                                    .add(Restrictions.eq("vmId", vmId)))
                                                 .list();
        }
        catch (HibernateException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        
        TreeMap<Double, Double> vmUsedResources = new TreeMap<Double, Double>();
        if(dataList == null) return vmUsedResources;
        for(ReportData rd : dataList) {
            if (type.equals("BANDWIDTH")) {
                //If bandwidth, convert from kbps to Mbps
                vmUsedResources.put(rd.getTime()/60, rd.getAmount() / 1000);
            } else {
                vmUsedResources.put(rd.getTime()/60, rd.getAmount());
            }
        }
        
        return vmUsedResources;
    }    
    
    /** 
     * Inserts report data related to overall resource usage of a given datacenter.
     *
     * @param   type            the type of the used resource.
     * @param   datacenterName  the name of the datacenter.
     * @param   time            the instant of time which the data being inserted refers to.
     * @param   amount          the overall amount of resources being used by 
     *                          the datacenter at <code>time</code>.
     * @param   simulationId    the identification number of the simulation.
     * 
     * @see                     ReportData
     * @since                   1.0
     */        
    public void insertDatacenterOverallData(String type, String datacenterName, double time,
                                            double amount, int simulationId) {
        
        ReportData rd = new ReportData(type, datacenterName, null, time, amount, simulationId);
        
        Session session = HibernateUtil.getSession();
        try {
            session.beginTransaction();
            session.save(rd);
            session.getTransaction().commit();
        }
        catch (HibernateException ex) {
            session.getTransaction().rollback();
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
    } 
    
    /** 
     * Gets a report of overall resources usage of a given datacenter.
     *
     * @param   type            the type of the used resource.
     * @param   datacenterName  the name of the datacenter.
     * @return                  a map with values of time as keys and values of
     *                          used resources as values.
     * @see                     ReportData
     * @since                   1.0
     */      
    public TreeMap<Double, Double> getDatacenterOverallData(String type, String datacenterName) {
        List<ReportData> dataList = null;
        Session session = HibernateUtil.getSession();
        try {
            dataList = (List<ReportData>) session.createCriteria(ReportData.class)
                                                 .add(Restrictions.conjunction()
                                                    .add(Restrictions.eq("type", type))
                                                    .add(Restrictions.eq("datacenterName", datacenterName))
                                                    .add(Restrictions.isNull("hostId")))
                                                 .list();
        }
        catch (HibernateException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        
        TreeMap<Double, Double> overallUsedResources = new TreeMap<Double, Double>();
        if(dataList == null) return overallUsedResources;
        for(ReportData rd : dataList) {
            if (type.equals("BANDWIDTH")) {
                //If bandwidth, convert from kbps to Mbps
                overallUsedResources.put(rd.getTime()/60, rd.getAmount() / 1000);
            } else {
                overallUsedResources.put(rd.getTime()/60, rd.getAmount());
            }
        }
        
        return overallUsedResources;
    }    
    
    /** 
     * Inserts report data related to overall resource usage of a given customer.
     *
     * @param   type            the type of the used resource.
     * @param   customerName    the name of the customer.
     * @param   time            the instant of time which the data being inserted refers to.
     * @param   amount          the overall amount of resources being used by 
     *                          the customer at <code>time</code>.
     * @param   simulationId    the identification number of the simulation.
     * 
     * @see                     ReportData
     * @since                   1.0
     */       
    public void insertCustomerOverallData(String type, String customerName, double time,
                                          double amount, int simulationId) {
        
        ReportData rd = new ReportData(type, null, customerName, time, amount, simulationId);
        
        Session session = HibernateUtil.getSession();
        try {
            session.beginTransaction();
            session.save(rd);
            session.getTransaction().commit();
        }
        catch (HibernateException ex) {
            session.getTransaction().rollback();
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
    }        
    
    /**
     * Gets a report of overall resources usage of a given customer.
     *
     * @param type          the type of the used resource.
     * @param customerName  the name of the customer.
     * @return              a map with values of time as keys and values of
     *                      used resources as values.
     * @see                 ReportData
     * @since               1.0
     */    
    public TreeMap<Double, Double> getCustomerOverallData(String type, String customerName) {
        List<ReportData> dataList = null;
        Session session = HibernateUtil.getSession();
        try {
            dataList = (List<ReportData>) session.createCriteria(ReportData.class)
                                                 .add(Restrictions.conjunction()
                                                    .add(Restrictions.eq("type", type))
                                                    .add(Restrictions.eq("customerName", customerName))
                                                    .add(Restrictions.isNull("vmId")))
                                                 .list();
        }
        catch (HibernateException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        
        TreeMap<Double, Double> overallUsedResources = new TreeMap<Double, Double>();
        if(dataList == null) return overallUsedResources;
        for(ReportData rd : dataList) {
            if (type.equals("BANDWIDTH")) {
                //If bandwidth, convert from kbps to Mbps
                overallUsedResources.put(rd.getTime()/60, rd.getAmount() / 1000);
            } else {
                overallUsedResources.put(rd.getTime()/60, rd.getAmount());
            }
        }
        
        return overallUsedResources;
    }

    /**
     * Inserts a list of report data into the database.
     *
     * @param   dataList    the list of report data to be inserted.
     * @see                 ReportData
     * @since               1.0
     */      
    public void insertDataList(List<ReportData> dataList) {
        Session session = HibernateUtil.getSession();
        try {
            session.beginTransaction();

            for(ReportData data : dataList) {
                session.save(data);            
            }       

            session.getTransaction().commit();
        }
        catch (HibernateException ex) {
            session.getTransaction().rollback();
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
    }

}
