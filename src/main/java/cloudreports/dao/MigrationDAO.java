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
import cloudreports.models.Migration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 * MigrationDAO provides basic CRUD operations related to the 
 * {@link Migration} class.
 * 
 * @see         Migration
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class MigrationDAO {
    
    /** 
     * Registers a set of migration occurrences into the database.
     *
     * @param   migrationList   a list of migrations to be inserted.
     * @see                     Migration
     * @since                   1.0
     */    
    public void insertMigrations(List<Migration> migrationList) {
        Session session = HibernateUtil.getSession();
        try {
            session.beginTransaction();
            
            for (Migration migration : migrationList) {
                session.save(migration);
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
     * Lists all migration occurrences for a given datacenter.
     *
     * @return                  a list containing all migration occurrences;
     *                          <code>null</code> if no occurrences were found.
     * @see                     Migration
     * @since                   1.0
     */    
    public List<Migration> getMigrationList(String datacenterName) {
        Session session = HibernateUtil.getSession();
        List<Migration> migrationList = null;
        
        try {
            migrationList = (List<Migration>) session.createCriteria(Migration.class)
                                .add(Restrictions.eq("datacenterName", datacenterName))
                                .list();
        } catch (HibernateException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            HibernateUtil.closeSession(session);
        }

        return migrationList;
    }
}
