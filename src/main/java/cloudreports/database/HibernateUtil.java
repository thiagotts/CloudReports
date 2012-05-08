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

package cloudreports.database;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Provides utility operations related to the Hibernate framework.
 * 
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class HibernateUtil {

    /** A session factory. It provides database sessions to DAO methods. */
    private static SessionFactory sessionFactory;
    
    /** The current active database. */
    private static String activeDatabase;

    /** 
     * Provides an opened database session.
     *
     * @see     #sessionFactory
     * @return  a database session.
     * @since   1.0
     */        
    public static Session getSession() {
        return sessionFactory.openSession();
    }
    
    /** 
     * Closes a given database session.
     *
     * @param   session     the session to be closed.
     * @since               1.0
     */     
    public static void closeSession(Session session) {
        if(session != null) {
            session.clear();
            session.close();
        }
    }

    /** 
     * Closes the session factory.
     *
     * @see     #sessionFactory
     * @since   1.0
     */      
    public static void shutDown() {
        sessionFactory.close();
    }
    
    /** 
     * Gets the active database. 
     * It consists of the name of the CloudReports Environment (.cre) file
     * without its extension.
     *
     * @see     #activeDatabase
     * @since   1.0
     */         
    public static String getActiveDatabase() {
        return activeDatabase.replace(".cre", "");
    }

    /** 
     * Sets the active database.
     * It changes the active database and builds a new session factory for it.
     *
     * @param   aActiveDatabase the name of the file that contains the new database.
     * @see                     #activeDatabase
     * @since                   1.0
     */     
    public static void setActiveDatabase(String aActiveDatabase) {
        if(sessionFactory != null) sessionFactory.close();
        activeDatabase = aActiveDatabase;
        Configuration cfg = new Configuration();
        cfg.configure();
        System.setProperty("hibernate.connection.driver_class", "org.sqlite.JDBC");
        System.setProperty("hibernate.dialect", "cloudreports.database.SQLiteDialect");
        System.setProperty("hibernate.connection.url", "jdbc:sqlite:db/" + activeDatabase);
        cfg.setProperties(System.getProperties());
        sessionFactory = cfg.buildSessionFactory();
    }
}