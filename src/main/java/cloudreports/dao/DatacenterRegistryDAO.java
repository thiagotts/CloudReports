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
import cloudreports.models.DatacenterRegistry;
import cloudreports.models.HostRegistry;
import cloudreports.models.SanStorageRegistry;
import cloudreports.utils.LongOperations;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 * DatacenterRegistryDAO provides basic CRUD operations related to the 
 * {@link DatacenterRegistry} class.
 * It also provides access to general information about datacenters.
 * 
 * @see         DatacenterRegistry
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class DatacenterRegistryDAO {
    
    /** 
     * Inserts a new datacenter registry into the database.
     * The datacenter's name must be unique.
     *
     * @param   dr      the datacenter registry to be inserted.
     * @return          <code>true</code> if the datacenter registry
     *                  has been successfully inserted; 
     *                  <code>false</code> otherwise.
     * @see             DatacenterRegistry
     * @since           1.0
     */    
    public boolean insertNewDatacenterRegistry(DatacenterRegistry dr) {
        Session session = HibernateUtil.getSession();

        try {
            DatacenterRegistry datacenter = (DatacenterRegistry) session.createCriteria(DatacenterRegistry.class)
                                            .add(Restrictions.eq("name", dr.getName())).uniqueResult();

            //The datacenter's name must be unique
            if(datacenter != null) return false;

            session.beginTransaction();
            session.save(dr);            
            session.getTransaction().commit();
        }
        catch (HibernateException ex) {
            session.getTransaction().rollback();
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            HibernateUtil.closeSession(session);
        }

        //Insert the new network entries
        NetworkMapEntryDAO neDAO = new NetworkMapEntryDAO();
        neDAO.insertNewEntityEntries(dr.getName());
        
        return true;
    }
    
    /** 
     * Gets an existing datacenter with the given id.
     *
     * @param   datacenterId    the id of the datacenter to be retrieved.
     * @return                  the datacenter, if it exists; <code>null</code>
     *                          otherwise.
     * @see                     DatacenterRegistry
     * @since                   1.0
     */    
    public DatacenterRegistry getDatacenterRegistry(long datacenterId) {
        Session session = HibernateUtil.getSession();
        DatacenterRegistry datacenter = null;
        try {
            datacenter = (DatacenterRegistry) session.createCriteria(DatacenterRegistry.class)
                                            .add(Restrictions.eq("id", datacenterId)).uniqueResult();
        }
        catch (HibernateException ex) {
            session.getTransaction().rollback();
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        
        return datacenter;
    }
    
    /** 
     * Gets an existing datacenter with the given name.
     *
     * @param   datacenterName    the name of the datacenter to be retrieved.
     * @return                  the datacenter, if it exists; <code>null</code>
     *                          otherwise.
     * @see                     DatacenterRegistry
     * @since                   1.0
     */       
    public DatacenterRegistry getDatacenterRegistry(String datacenterName) {
        Session session = HibernateUtil.getSession();
        DatacenterRegistry datacenter = null;
        
        try{
            datacenter = (DatacenterRegistry) session.createCriteria(DatacenterRegistry.class)
                                                     .add(Restrictions.eq("name", datacenterName)).uniqueResult();
        }
        catch (HibernateException ex) {
            session.getTransaction().rollback();
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        
        return datacenter;
    }    
    
    /** 
     * Updates an existing datacenter registry.
     *
     * @param   datacenter      the datacenter to be updated.
     * @see                     DatacenterRegistry
     * @since                   1.0
     */       
    public void updateDatacenterRegistry(DatacenterRegistry datacenter) {
        Session session = HibernateUtil.getSession();
        try {
            session.beginTransaction();
            session.update(datacenter);
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
     * Gets an existing datacenter with the given name.
     *
     * @param   datacenterName  the name of the datacenter to be retrieved.
     * @return                  the datacenter, if it exists; <code>null</code>
     *                          otherwise.
     * @see                     DatacenterRegistry
     * @since                   1.0
     */      
    public boolean removeDatacenterRegistry(String datacenterName) {
        Session session = HibernateUtil.getSession();
        
        DatacenterRegistry datacenter = null;
        try {
            datacenter = (DatacenterRegistry) session.createCriteria(DatacenterRegistry.class)
                                            .add(Restrictions.eq("name", datacenterName)).uniqueResult();

            if(datacenter == null) return false;

            session.beginTransaction();
            session.delete(datacenter);
            session.getTransaction().commit();
        }
        catch (HibernateException ex) {
            session.getTransaction().rollback();
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        
        //Remove the related network entries
        NetworkMapEntryDAO neDAO = new NetworkMapEntryDAO();
        neDAO.removeEntries(datacenter.getName());        
        
        return true;
    }    
    
    /**
     * Gets a list of all existing datacenters.
     *
     * @return          a list containing all the datacenter registries
     *                  in the database; <code>null</code> if no datacenters
     *                  were found.
     * @see             DatacenterRegistry
     * @since           1.0
     */
    public List<DatacenterRegistry> getListOfDatacenters() {
        Session session = HibernateUtil.getSession();
        List<DatacenterRegistry> datacenterList = null;
        
        try {
            datacenterList = (List<DatacenterRegistry>) session.createCriteria(DatacenterRegistry.class).list();
        }
        catch (HibernateException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        
        return datacenterList;        
    }     
    
    /** 
     * Gets the number of datacenters in the database.
     *
     * @return                  the number of datacenters.
     * @see                     DatacenterRegistry
     * @since                   1.0
     */
    public int getNumOfDatacenters() {
        return getListOfDatacenters().size();
    }
    
    /** 
     * Gets all existing datacenters' names.
     *
     * @return                  an array containing the names of all datacenters.
     * @see                     DatacenterRegistry
     * @since                   1.0
     */    
    public String[] getAllDatacentersNames() {
        List<DatacenterRegistry> datacenterList = getListOfDatacenters();
        String[] names =  new String[datacenterList.size()];

        for(int i=0; i<datacenterList.size(); i++) {
            names[i] = datacenterList.get(i).getName();
        }

        return names;
    }    
    
    /** 
     * Gets a list of all hosts of a given datacenter.
     *
     * @param   datacenterId    the id of the datacenter of which the hosts
     *                          will be listed.
     * @return                  a list containing all hosts of the
     *                          datacenter; <code>null</code> if the datacenter
     *                          does not exist.
     * @see                     DatacenterRegistry
     * @see                     HostRegistry
     * @since                   1.0
     */    
    public List<HostRegistry> getListOfHosts(long datacenterId) {
        Session session = HibernateUtil.getSession();
        DatacenterRegistry datacenter = null;

        try {
            datacenter = (DatacenterRegistry) session.createCriteria(DatacenterRegistry.class)
                                        .add(Restrictions.eq("id", datacenterId)).uniqueResult();
            
            if(datacenter ==  null) return null;
        }
        catch (HibernateException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        
        return datacenter.getHostList();        
    }    
    
    /** 
     * Gets a host registry based on its id and the id of the
     * datacenter that owns it.
     * 
     * @param   hostId          the id of the host to be retrieved.
     * @param   datacenterId    the id of the datacenter that owns the host
     *                          to be retrieved.
     * @return                  the host registry, if it exists; 
     *                          <code>null</code> otherwise.
     * @see                     DatacenterRegistry
     * @see                     HostRegistry
     * @since                   1.0
     */    
    public HostRegistry getHostRegistry(long hostId, long datacenterId) {
        HostRegistry host = null;
        for(HostRegistry hr : getListOfHosts(datacenterId)) {
            if(hr.getId() == hostId) {
                host = hr;
                break;
            }
        }
        
        return host;          
    }
    
    /** 
     * Gets the number of hosts of a given datacenter.
     * 
     * @param   datacenterId    the id of the datacenter that owns the hosts
     *                          to be counted.
     * @return                  the number of hosts of the datacenter.
     * @see                     DatacenterRegistry
     * @see                     HostRegistry
     * @since                   1.0
     */    
    public int getNumOfHosts(long datacenterId) {
        int numOfHosts = 0;
        for(HostRegistry host : getListOfHosts(datacenterId)) {
            numOfHosts += host.getAmount();
        }
        return numOfHosts;
    }    
    
    /** 
     * Gets the names of all hosts of a given datacenter.
     * 
     * @param   datacenterId    the id of the datacenter that owns the hosts.
     * @return                  an array containing the names of all hosts
     *                          of the datacenter.
     * @see                     DatacenterRegistry
     * @see                     HostRegistry
     * @since                   1.0
     */    
    public String[] getHostNames(long datacenterId) {
        int n = getNumOfHosts(datacenterId);
        String[] names = new String[n];

        for(int i=0; i<n; i++) {
            names[i]="Host"+i;
        }

        return names;
    }
    
    /** 
     * Gets a list of all storage area networks of a given datacenter.
     *
     * @param   datacenterId    the id of the datacenter of which the SAN
     *                          will be listed.
     * @return                  a list containing all SAN of the
     *                          datacenter; <code>null</code> if the datacenter
     *                          does not exist.
     * @see                     DatacenterRegistry
     * @see                     SanStorageRegistry
     * @since                   1.0
     */      
    public List<SanStorageRegistry> getListOfSans(long datacenterId) {
        Session session = HibernateUtil.getSession();
        DatacenterRegistry datacenter = null;

        try {
            datacenter = (DatacenterRegistry) session.createCriteria(DatacenterRegistry.class)
                                        .add(Restrictions.eq("id", datacenterId)).uniqueResult(); 
            
            if(datacenter == null) return null;
        }
        catch (HibernateException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        
        return datacenter.getSanList();        
    }    
    
    /** 
     * Gets a SAN registry based on its name and the id of the
     * datacenter that owns it.
     * 
     * @param   sanStorageName  the name of the SAN to be retrieved.
     * @param   datacenterId    the id of the datacenter that owns the SAN
     *                          to be retrieved.
     * @return                  the SAN registry, if it exists; 
     *                          <code>null</code> otherwise.
     * @see                     DatacenterRegistry
     * @see                     SanStorageRegistry
     * @since                   1.0
     */        
    public SanStorageRegistry getSanStorageRegistry(String sanStorageName, long datacenterId) {
        Session session = HibernateUtil.getSession();
        DatacenterRegistry datacenter = null;

        try {
            datacenter = (DatacenterRegistry) session.createCriteria(DatacenterRegistry.class)
                                        .add(Restrictions.eq("id", datacenterId)).uniqueResult();
            
            if(datacenter == null) return null;
        }
        catch (HibernateException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            HibernateUtil.closeSession(session);
        }        
        
        SanStorageRegistry sanStorage = null;
        for(SanStorageRegistry sr : datacenter.getSanList()) {
            if(sr.getName().equals(sanStorageName)) {
                sanStorage = sr;
                break;
            }
        }
        
        return sanStorage;
    }        
    
    /** 
     * Gets the number of processing elements of a given datacenter.
     * 
     * @param   datacenterId    the id of the datacenter that owns the
     *                          processing elements to be counted.
     * @return                  the number of processing elements of the
     *                          datacenter.
     * @see                     DatacenterRegistry
     * @see                     HostRegistry
     * @since                   1.0
     */    
    public int getNumOfPes(long datacenterId) {
        int numOfPes=0;

        for(HostRegistry h : getListOfHosts(datacenterId)) {
            numOfPes+=(h.getNumOfPes()*h.getAmount());
        }

        return numOfPes;
    }
    
    /** 
     * Gets the total MIPS capacity of the datacenter.
     * 
     * @param   datacenterId    the id of the datacenter.
     * @return                  the total MIPS capacity of the datacenter.
     * @see                     DatacenterRegistry
     * @see                     HostRegistry
     * @since                   1.0
     */      
    public double getMips(long datacenterId) {
        double mips=0;

        for(HostRegistry h : getListOfHosts(datacenterId)) {
            mips+=(h.getNumOfPes()*h.getMipsPerPe()*h.getAmount());
        }
        return mips;
    }
    
    /** 
     * Gets the total storage capacity of the datacenter.
     * 
     * @param   datacenterId    the id of the datacenter.
     * @return                  the total storage capacity of the datacenter.
     * @see                     DatacenterRegistry
     * @see                     HostRegistry
     * @see                     SanStorageRegistry
     * @since                   1.0
     */      
    public long getStorageCapacity(long datacenterId) {
        long storage=0;

        for(HostRegistry h : getListOfHosts(datacenterId)) {
            storage=LongOperations.saturatedAdd(LongOperations.saturatedMultiply(h.getStorage(),h.getAmount()),storage);
        }

        for(SanStorageRegistry s : getListOfSans(datacenterId)) {
            storage=LongOperations.saturatedAdd(storage, (long)s.getCapacity());
        }

        return storage;
    }
    
    /** 
     * Gets the total RAM capacity of the datacenter.
     * 
     * @param   datacenterId    the id of the datacenter.
     * @return                  the total RAM capacity of the datacenter.
     * @see                     DatacenterRegistry
     * @see                     HostRegistry
     * @since                   1.0
     */     
    public long getRam(long datacenterId) {
        long ram=0;

        for(HostRegistry h : getListOfHosts(datacenterId)) {
            ram=LongOperations.saturatedAdd(LongOperations.saturatedMultiply(h.getRam(),h.getAmount()),ram);
        }
        return ram;
    }
    
    /** 
     * Gets the total bandwidth capacity of the datacenter.
     * 
     * @param   datacenterId    the id of the datacenter.
     * @return                  the total bandwidth capacity of the datacenter.
     * @see                     DatacenterRegistry
     * @see                     HostRegistry
     * @see                     SanStorageRegistry
     * @since                   1.0
     */     
    public long getBandwidth(long datacenterId) {
        long bw=0;

        for(HostRegistry h : getListOfHosts(datacenterId)) {
            bw=LongOperations.saturatedAdd(LongOperations.saturatedMultiply(h.getBw(),h.getAmount()),bw);
        }
        return bw;
    }    
    
    /** 
     * Gets the total number of hosts regarding all datacenters.
     * 
     * @return                  the total number of hosts.
     * @see                     DatacenterRegistry
     * @since                   1.0
     */     
    public int getTotalNumOfHosts() {
        int numOfHosts=0;

        for(DatacenterRegistry d : getListOfDatacenters()) {
            numOfHosts += getNumOfHosts(d.getId());
        }

        return numOfHosts;
    }    
    
    /** 
     * Gets the total number of processing elements regarding all datacenters.
     * 
     * @return                  the total number of processing elements.
     * @see                     DatacenterRegistry
     * @since                   1.0
     */       
    public int getTotalNumOfPes() {
        int numOfPes=0;

        for(DatacenterRegistry d : getListOfDatacenters()) {
            numOfPes += getNumOfPes(d.getId());
        }
        return numOfPes;
    }    
    
    /** 
     * Gets the total MIPS capacity regarding all datacenters.
     * 
     * @return                  the total MIPS capacity.
     * @see                     DatacenterRegistry
     * @since                   1.0
     */       
    public double getTotalMips() {
        double mips=0;

        for(DatacenterRegistry d : getListOfDatacenters()) {
            mips += getMips(d.getId());
        }
        return mips;
    }    
    
    /** 
     * Gets the total RAM capacity regarding all datacenters.
     * 
     * @return                  the total RAM capacity.
     * @see                     DatacenterRegistry
     * @since                   1.0
     */      
    public long getTotalRam() {
        long ram=0;

        for(DatacenterRegistry d : getListOfDatacenters()) {
            ram += getRam(d.getId());
        }
        return ram;
    }    
   
    /** 
     * Gets the total storage capacity regarding all datacenters.
     * 
     * @return                  the total storage capacity.
     * @see                     DatacenterRegistry
     * @since                   1.0
     */      
    public long getTotalStorageCapacity() {
        long storage = 0;

        for(DatacenterRegistry d : getListOfDatacenters()) {
            storage += getStorageCapacity(d.getId());
        }
        return storage;
    }    
    
}
