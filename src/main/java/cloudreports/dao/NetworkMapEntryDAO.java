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
import cloudreports.models.CustomerRegistry;
import cloudreports.models.DatacenterRegistry;
import cloudreports.models.NetworkMapEntry;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 * NetworkMapEntryDAO provides basic CRUD operations related to the 
 * {@link NetworkMapEntry} class.
 * 
 * @see         NetworkMapEntry
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class NetworkMapEntryDAO {
    
    /** 
     * Inserts a new customer registry into the database.
     *
     * @param   entry   the network entry to be inserted.
     * @see             NetworkMapEntry
     * @since           1.0
     */    
    public void insertNetworkMapEntry(NetworkMapEntry entry) {
        Session session = HibernateUtil.getSession();
        try {
        session.beginTransaction();
        session.save(entry);            
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
     * Creates initial network entries for new entities.
     *
     * @param   entityName  the name of the new entity.
     * @return              <code>true</code> if the initial entries have been
     *                      successfully created; <code>false</code> otherwise.
     * @see                 NetworkMapEntry
     * @since               1.0
     */     
    public boolean insertNewEntityEntries(String entityName) {
        Session session = HibernateUtil.getSession();

        try {        
            NetworkMapEntry ne = (NetworkMapEntry) session.createCriteria(NetworkMapEntry.class)
                                            .add(Restrictions.eq("source", entityName))
                                            .uniqueResult();

            if(ne != null) return false;
        }
        catch (HibernateException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            HibernateUtil.closeSession(session);
        }

        DatacenterRegistryDAO drDAO = new DatacenterRegistryDAO();
        List<DatacenterRegistry> datacenterList = drDAO.getListOfDatacenters();
        datacenterList.remove(drDAO.getDatacenterRegistry(entityName));
        for(DatacenterRegistry dr : datacenterList) {
            /*
            * Include all other datacenters as a destinations to
            * this new entity
            */            
            NetworkMapEntry newEntry = new NetworkMapEntry(entityName, dr.getName(), 1.0, 1.0);
            insertNetworkMapEntry(newEntry);

            /*
            * Include this new entity as a destination to all other
            * datacenters.
            */
            newEntry = new NetworkMapEntry(dr.getName(), entityName, 1.0, 1.0);
            insertNetworkMapEntry(newEntry);
        }

        CustomerRegistryDAO crDAO = new CustomerRegistryDAO();
        List<CustomerRegistry> customerList = crDAO.getListOfCustomers();
        customerList.remove(crDAO.getCustomerRegistry(entityName));
        for(CustomerRegistry cr : customerList) {
            /*
            * Include all other customers as a destination to
            * this new entity
            */
            NetworkMapEntry newEntry = new NetworkMapEntry(entityName, cr.getName(), 1.0, 1.0);
            insertNetworkMapEntry(newEntry);

            /*
            * Include this new entity as a destination to all other
            * customers.
            */
            newEntry = new NetworkMapEntry(cr.getName(), entityName, 1.0, 1.0);
            insertNetworkMapEntry(newEntry);
        }
        
        return true;
    }    
    
    /** 
     * Gets an existing network entry based on a source and a destination.
     *
     * @param   source          the name of a source entity.
     * @param   destination     the name of a destination entity.
     * @return                  the network entry, if it exists; 
     *                          <code>null</code> otherwise.
     * @see                     NetworkMapEntry
     * @since                   1.0
     */    
    public NetworkMapEntry getNetworkMapEntry(String source, String destination) {
        Session session = HibernateUtil.getSession();
        NetworkMapEntry entry = null;
        
        try {
            entry = (NetworkMapEntry) session.createCriteria(NetworkMapEntry.class)
                                             .add(Restrictions.and(
                                                Restrictions.eq("source", source),
                                                Restrictions.eq("destination", destination)))
                                             .uniqueResult();
        }
        catch (HibernateException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        
        return entry;
    }
    
    /** 
     * Updates an existing network entry.
     *
     * @param   entry   the network entry to be updated.
     * @see             NetworkMapEntry
     * @since           1.0
     */    
    public void updateNetworkMapEntry(NetworkMapEntry entry) {
        Session session = HibernateUtil.getSession();
        
        try {
            session.beginTransaction();
            session.update(entry);
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
     * Removes all network entries of a given entity.
     *
     * @param   entityName      the name of the entity of which all network
     *                          entries will be removed.
     * @see                     NetworkMapEntry
     * @since                   1.0
     */    
    public void removeEntries(String entityName) {
        List<NetworkMapEntry> networkList = getListOfEntries(entityName);        
        Session session = HibernateUtil.getSession();
        
        try {
            session.beginTransaction();

            for(NetworkMapEntry nm : networkList) {
                session.delete(nm);
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
    
    /** 
     * Gets a list of all existing network entries.
     *
     * @return  a list containing all existing network entries
     *          in the database; <code>null</code> if no
     *          entries were found.
     * @see     NetworkMapEntry
     * @since   1.0
     */    
    public List<NetworkMapEntry> getAllEntries() {
        Session session = HibernateUtil.getSession();
        List<NetworkMapEntry> networkList = null;

        try {
            networkList = (List<NetworkMapEntry>) session.createCriteria(NetworkMapEntry.class).list();
        }
        catch (HibernateException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        
        return networkList;        
    }    

    /** 
     * Gets a list of network entries of a given entity.
     * The resulting list contains entries on which the given entity is either
     * the source or the destination.
     * 
     * @param   entityName      the name of the entity of which all related 
     *                          network entries will be retrieved.
     * @return                  a list containing all existing network entries
     *                          in the database; <code>null</code> if no
     *                          entries were found.
     * @see                     NetworkMapEntry
     * @since                   1.0
     */       
    public List<NetworkMapEntry> getListOfEntries(String entityName) {
        Session session = HibernateUtil.getSession();
        List<NetworkMapEntry> networkList = null;

        try {
            networkList = (List<NetworkMapEntry>) session.createCriteria(NetworkMapEntry.class)
                                                         .add(Restrictions.or(
                                                            Restrictions.eq("source", entityName),
                                                            Restrictions.eq("destination", entityName)))
                                                         .list();
        }
        catch (HibernateException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        
        return networkList;
    }       
    
    /** 
     * Gets a list of network entries on which the given entity is the source.
     *
     * @param   entityName      the name of the entity of which all 
     *                          destinations will be retrieved.
     * @return                  a list containing all network entries on which
     *                          the given entity is the source; 
     *                          <code>null</code> if no entries were found.
     * @see                     NetworkMapEntry
     * @since                   1.0
     */         
    public List<NetworkMapEntry> getListOfDestinations(String entityName) {
        Session session = HibernateUtil.getSession();
        List<NetworkMapEntry> networkList = null;

        try {
            networkList = (List<NetworkMapEntry>) session.createCriteria(NetworkMapEntry.class)
                                                         .add(Restrictions.eq("source", entityName))
                                                         .list();
        }
        catch (HibernateException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            HibernateUtil.closeSession(session);
        }        
        
        return networkList;        
    }     

}
