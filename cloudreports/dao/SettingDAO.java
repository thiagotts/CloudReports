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
import cloudreports.models.Setting;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 * SettingDAO provides basic CRUD operations related to the 
 * {@link Setting} class.
 * 
 * @see         Setting
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class SettingDAO {
    
    /** 
     * Inserts a new setting into the database.
     *
     * @param   setting     the setting to be inserted.
     * @see                 Setting
     * @since               1.0
     */    
    public void insertSetting(Setting setting) {
        Session session = HibernateUtil.getSession();
        try {
            session.beginTransaction();
            session.save(setting);
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
     * Updates an existing setting.
     *
     * @param   setting     the setting to be updated.
     * @see                 Setting
     * @since               1.0
     */       
    public void updateSetting(Setting setting) {
        Session session = HibernateUtil.getSession();
        try {
            session.beginTransaction();
            session.update(setting);
            session.getTransaction().commit();
        } catch (HibernateException ex) {
            session.getTransaction().rollback();
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
    }

    /** 
     * Gets an existing setting.
     *
     * @param   name    the name of the setting to be retrieved.
     * @return          the setting with the given name, if it exists; 
     *                  <code>null</code> otherwise.
     * @see             Setting
     * @since           1.0
     */      
    public Setting getSetting(String name) {
        Setting setting = null;
        Session session = HibernateUtil.getSession();
        try {
            setting = (Setting) session.createCriteria(Setting.class)
                                       .add(Restrictions.eq("name", name))
                                       .uniqueResult();
        }
        catch (HibernateException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            HibernateUtil.closeSession(session);
        }

        return setting;
    }
    
}
