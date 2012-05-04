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
import cloudreports.models.SanStorageRegistry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 * SanStorageRegistryDAO provides basic CRUD operations related to the 
 * {@link SanStorageRegistry} class.
 * 
 * @see         SanStorageRegistry
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class SanStorageRegistryDAO {

    /** 
     * Inserts a new SAN registry into the database.
     * The name of the SAN must be unique.
     *
     * @param   sr      the SAN registry to be inserted.
     * @return          <code>true</code> if the SAN registry
     *                  has been successfully inserted; 
     *                  <code>false</code> otherwise.
     * @see             SanStorageRegistry
     * @since           1.0
     */    
    public boolean insertSanStorageRegistry(SanStorageRegistry sr) {
        boolean result = false;
        
        Session session = HibernateUtil.getSession();
        try {        
            SanStorageRegistry sanStorage = (SanStorageRegistry) session.createCriteria(SanStorageRegistry.class)
                                            .add(Restrictions.eq("name", sr.getName())).uniqueResult();

            if(sanStorage != null) result = false;
            else {
                session.beginTransaction();
                session.save(sr);
                session.getTransaction().commit();
                result = true;
            }
        }
        catch (HibernateException ex) {
            session.getTransaction().rollback();
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        
        return result;
    }
    
    /** 
     * Gets an existing SAN registry with the given id.
     *
     * @param   sanStorageId    the id of the SAN to be retrieved.
     * @return                  the SAN, if it exists; <code>null</code>
     *                          otherwise.
     * @see                     SanStorageRegistry
     * @since                   1.0
     */    
    public SanStorageRegistry getSanStorageRegistry(long sanStorageId) {
        SanStorageRegistry sanStorage = null;
        Session session = HibernateUtil.getSession();
        try {
            sanStorage = (SanStorageRegistry) session.createCriteria(SanStorageRegistry.class)
                                                     .add(Restrictions.eq("id", sanStorageId))
                                                     .uniqueResult();
        }
        catch (HibernateException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        
        return sanStorage;
    }
    
    /** 
     * Updates an existing SAN registry.
     *
     * @param   sr  the customer to be updated.
     * @see         SanStorageRegistry
     * @since       1.0
     */    
    public void updateSanStorageRegistry(SanStorageRegistry sr) {
        Session session = HibernateUtil.getSession();
        try {
            session.beginTransaction();
            session.update(sr);
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
