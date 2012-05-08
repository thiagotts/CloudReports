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
import cloudreports.models.HostRegistry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 * HostRegistryDAO provides basic CRUD operations related to the 
 * {@link HostRegistry} class.
 * 
 * @see         HostRegistry
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class HostRegistryDAO {
    
    /** 
     * Gets an existing host with the given id.
     *
     * @param   hostId  the name of the customer to be retrieved.
     * @return          the host, if it exists; <code>null</code>
     *                  otherwise.
     * @see             HostRegistry
     * @since           1.0
     */    
    public HostRegistry getHostRegistry(long hostId) {
        Session session = HibernateUtil.getSession();
        HostRegistry host = null;

        try {
            host = (HostRegistry) session.createCriteria(HostRegistry.class)
                                         .add(Restrictions.eq("id", hostId))
                                         .uniqueResult();
        }
        catch (HibernateException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        
        return host;
    }
    
    /** 
     * Updates an existing host registry.
     *
     * @param   hr  the host to be updated.
     * @see         HostRegistry
     * @since       1.0
     */    
    public void updateHostRegistry(HostRegistry hr) {
        Session session = HibernateUtil.getSession();
        
        try {
            session.beginTransaction();
            session.update(hr);
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
