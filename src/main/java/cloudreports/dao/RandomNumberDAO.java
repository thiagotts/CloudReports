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

import cloudreports.business.SettingBusiness;
import cloudreports.database.Database;
import cloudreports.database.HibernateUtil;
import cloudreports.enums.RandomNumbersFactory;
import cloudreports.models.RandomNumber;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import qrbg.QRBG;
import qrbg.ServiceDeniedException;

/**
 * RandomNumberDAO provides basic CRUD operations related to random numbers
 * obtained from the QRBG service.
 * It consumes the random numbers pool and adds more entries on demand.
 * 
 * @see         <a href="http://random.irb.hr/">QRBG Service</a>
 * @see         RandomNumbersFactory
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class RandomNumberDAO {
    
    /** 
     * The amount of bytes to be obtained by request when using the QRBG service.
     * Divide by 4 to get the number of integers returned.
     */
    private final int NUMBER_OF_BYTES = 1440;
    
    /** 
     * The amount of requests to be executed each time the random numbers pool
     * needs more entries from the QRBG service.
     */    
    private final int NUMBER_OF_REQUESTS = 10;
    
    /** 
     * Inserts more entries in the random numbers pool.
     * Numbers are obtained from the QRBG service. The total amount of 
     * integers inserted can be calculated as ({@link #NUMBER_OF_BYTES}/4 * 
     * {@link #NUMBER_OF_REQUESTS}.
     *
     * @throws  IOException                If there is no Internet connectivity.
     * @throws  ServiceDeniedException     If the QRBG account is not valid.
     * @see                                <a href="http://random.irb.hr/">QRBG Service</a>
     * @since                              1.0
     */        
    private void insertMoreNumbersInPool() throws IOException, ServiceDeniedException {        
        QRBG source = new QRBG(SettingBusiness.getQRBGUsername(), SettingBusiness.getQRBGPassword());
        for(int request = 0; request < NUMBER_OF_REQUESTS; request++) {        
            byte[] buffer = new byte[NUMBER_OF_BYTES];
            source.getBytes(buffer, NUMBER_OF_BYTES);        

            Session session = HibernateUtil.getSession();
            try {
                session.beginTransaction();
                for (int i = 0; i < (NUMBER_OF_BYTES - 4); i+=4) {
                    double randomNumber = QRBG.readInt(buffer, i);
                    if (randomNumber < 0) {
                        randomNumber *= -1;
                    }
                    randomNumber /= Integer.MAX_VALUE;

                    if(randomNumber != 0) {
                        session.save(new RandomNumber(randomNumber));
                    }
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
    
    /** 
     * Gets the given amount of random numbers from the random numbers pool.
     * The effectiveness of this method depends on a proper configuration of
     * a valid account on the QRBG service and an active Internet connection.
     * Also, the QRBG service uses the port 1227. Make sure your firewall is
     * configured properly.
     *
     * @param   amount                     the amount of random numbers to get.
     * @throws  IOException                If there is no Internet connectivity.
     * @throws  ServiceDeniedException     If the QRBG account is not valid.
     * @see                                <a href="http://random.irb.hr/">QRBG Service</a>
     * @see                                RandomNumber
     * @since                              1.0
     */      
    public List<Double> getRandomNumbers(int amount) throws IOException, ServiceDeniedException {
        List<RandomNumber> numbersList = getRandomNumbersFromPool(amount);
        
        if(numbersList.size() < amount) {
            insertMoreNumbersInPool();            
            numbersList = getRandomNumbersFromPool(amount);
        }

        List<Double> returnList = new ArrayList<Double>();
        Session session = HibernateUtil.getSession();        
        try {
            session.beginTransaction();
            for(int i = 0; i < amount; i++) {
                RandomNumber randomNumber = numbersList.get(i);
                returnList.add(randomNumber.getValue());
                session.delete(randomNumber);
            }
            session.getTransaction().commit();
        }
        catch (HibernateException ex) {
            session.getTransaction().rollback();
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        
        return returnList;
    }
    
    /** 
     * Gets a list of {@link RandomNumber} registries from the random numbers
     * pool.
     *
     * @param   amount      the amount of random numbers to get from the pool.
     * @return              a list of random numbers from the pool. 
     *                      The resulting list has, at most, the number of
     *                      elements given by <code>amount</code>.
     * @see                 RandomNumber
     * @since               1.0
     */   
    private List<RandomNumber> getRandomNumbersFromPool(int amount) {
        Session session = HibernateUtil.getSession();
        List<RandomNumber> numbersList = new ArrayList<RandomNumber>();
        try {
            numbersList = (List<RandomNumber>) session.createCriteria(RandomNumber.class).setMaxResults(amount).list();
        } catch (HibernateException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        
        return numbersList;
    }

    
}
