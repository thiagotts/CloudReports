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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import cloudreports.database.Database;
import cloudreports.database.HibernateUtil;
import cloudreports.models.CustomerRegistry;
import cloudreports.models.UtilizationProfile;
import cloudreports.models.VirtualMachineRegistry;
import cloudreports.utils.LongOperations;

/**
 * CustomerRegistryDAO provides basic CRUD operations related to the 
 * {@link CustomerRegistry} class.
 * It also provides access to general information about virtual machines
 * deployed by a given customer.
 * 
 * @see         CustomerRegistry
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class CustomerRegistryDAO {
    
    /** 
     * Inserts a new customer registry into the database.
     * The customer's name must be unique.
     *
     * @param   cr      the customer registry to be inserted.
     * @return          <code>true</code> if the customer registry
     *                  has been successfully inserted; 
     *                  <code>false</code> otherwise.
     * @see             CustomerRegistry
     * @since           1.0
     */
    public boolean insertNewCustomerRegistry(CustomerRegistry cr) {
        Session session = HibernateUtil.getSession();
        try {            
            CustomerRegistry customer = (CustomerRegistry) session.createCriteria(CustomerRegistry.class).add(Restrictions.eq("name", cr.getName())).uniqueResult();

            //The customer's name must be unique
            if (customer != null) return false;
            
            session.beginTransaction();
            session.save(cr);
            session.getTransaction().commit();
        }
        catch (HibernateException ex) {
            session.getTransaction().rollback();
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
            
        //Insert new network entries
        NetworkMapEntryDAO neDAO = new NetworkMapEntryDAO();
        neDAO.insertNewEntityEntries(cr.getName());
        
        return true;
    }
    
    /** 
     * Gets an existing customer with the given name.
     *
     * @param   customerName    the name of the customer to be retrieved.
     * @return                  the customer, if it exists; <code>null</code>
     *                          otherwise.
     * @see                     CustomerRegistry
     * @since                   1.0
     */
    public CustomerRegistry getCustomerRegistry(String customerName) {
        Session session = HibernateUtil.getSession();        
        CustomerRegistry customer = null;
        try {
            customer = (CustomerRegistry) session.createCriteria(CustomerRegistry.class).add(Restrictions.eq("name", customerName)).uniqueResult();
        } 
        catch (HibernateException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        
        return customer;
    }
    
    /** 
     * Gets an existing customer with the given id.
     *
     * @param   customerId      the id of the customer to be retrieved.
     * @return                  the customer, if it exists; <code>null</code>
     *                          otherwise.
     * @see                     CustomerRegistry
     * @since                   1.0
     */
    public CustomerRegistry getCustomerRegistry(long customerId) {
        Session session = HibernateUtil.getSession();        
        CustomerRegistry customer = null;
        try {
            customer = (CustomerRegistry) session.createCriteria(CustomerRegistry.class).add(Restrictions.eq("id", customerId)).uniqueResult();
        }
        catch (HibernateException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            HibernateUtil.closeSession(session);
        }          
        
        return customer;
    }     
    
    /** 
     * Updates an existing customer registry.
     *
     * @param   customer        the customer to be updated.
     * @see                     CustomerRegistry
     * @since                   1.0
     */
    public void updateCustomerRegistry(CustomerRegistry customer) {
        Session session = HibernateUtil.getSession();
        try {
            session.beginTransaction();
            session.update(customer);
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
     * Removes an existing customer with the given name.
     *
     * @param   customerName    the id of the customer to be removed.
     * @return                  <code>true</code>, if the customer has been
     *                          successfully removed; <code>false</code>
     *                          otherwise.
     * @see                     CustomerRegistry
     * @since                   1.0
     */
    public boolean removeCustomerRegistry(String customerName) {
        Session session = HibernateUtil.getSession();        
        try {
            CustomerRegistry customer = (CustomerRegistry) session.createCriteria(CustomerRegistry.class).add(Restrictions.eq("name", customerName)).uniqueResult();
            
            if (customer == null) return false;
            
            session.beginTransaction();
            session.delete(customer);
            session.getTransaction().commit();
        } 
        catch (HibernateException ex) {
            session.getTransaction().rollback();
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        
        //Remove network entries
        NetworkMapEntryDAO neDAO = new NetworkMapEntryDAO();
        neDAO.removeEntries(customerName);

        return true;
    }
    
    /** 
     * Gets a list of all existing customers.
     *
     * @return                  a list containing all the customer registries
     *                          in the database; <code>null</code> if no
     *                          customers were found.
     * @see                     CustomerRegistry
     * @since                   1.0
     */
    public List<CustomerRegistry> getListOfCustomers() {
        Session session = HibernateUtil.getSession();
        List<CustomerRegistry> customerList = null;
        try {
            customerList = (List<CustomerRegistry>) session.createCriteria(CustomerRegistry.class).list();
        } 
        catch (HibernateException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            HibernateUtil.closeSession(session);
        }      
        
        return customerList;
    }
    
    /** 
     * Gets the number of customers in the database.
     *
     * @return                  the number of customers.
     * @see                     CustomerRegistry
     * @since                   1.0
     */
    public int getNumOfCustomers() {
        return getListOfCustomers().size();
    }   
    
    /** 
     * Gets all existing customers' names.
     *
     * @return                  an array containing the names of all customers.
     * @see                     CustomerRegistry
     * @since                   1.0
     */
    public String[] getCustomersNames() {
        List<CustomerRegistry> customerList = getListOfCustomers();
        String[] names = new String[customerList.size()];

        for(int i=0; i<customerList.size(); i++) {
            names[i] = customerList.get(i).getName();
        }

        return names;
    }    

    /** 
     * Gets a list of all virtual machines deployed by a given customer.
     *
     * @param   customerId      the id of the customer of which the virtual
     *                          machines will be listed.
     * @return                  a list containing all the virtual machines
     *                          deployed by the customer; <code>null</code> 
     *                          if no virtual machines were found.
     * @see                     CustomerRegistry
     * @see                     VirtualMachineRegistry
     * @since                   1.0
     */
    public List<VirtualMachineRegistry> getListOfVms(long customerId) {
        Session session = HibernateUtil.getSession();
        CustomerRegistry customer = null;
        try {
            customer = (CustomerRegistry) session.createCriteria(CustomerRegistry.class).add(Restrictions.eq("id", customerId)).uniqueResult();
        } 
        catch (HibernateException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            HibernateUtil.closeSession(session);
        }     
        
        return customer.getVmList();
    }
    
    /** 
     * Gets a virtual machine registry based on its id and the id of the
     * customer who owns it.
     * 
     * @param   vmId            the id of the virtual machine to be retrieved.
     * @param   customerId      the id of the customer who owns the virtual
     *                          machine to be retrieved.
     * @return                  the virtual machine registry, if it exists; 
     *                          <code>null</code> otherwise.
     * @see                     CustomerRegistry
     * @see                     VirtualMachineRegistry
     * @since                   1.0
     */
    public VirtualMachineRegistry getVirtualMachineRegistry(long vmId, long customerId) {
        Session session = HibernateUtil.getSession();
        CustomerRegistry customer = null;
        try {
            customer = (CustomerRegistry) session.createCriteria(CustomerRegistry.class).add(Restrictions.eq("id", customerId)).uniqueResult();
        } 
        catch (HibernateException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        
        VirtualMachineRegistry vm = null;
        for(VirtualMachineRegistry vr : customer.getVmList()) {
            if(vr.getId() == vmId) {
                vm = vr;
                break;
            }
        }
        
        return vm;
    }    
    
    /** 
     * Gets the number of virtual machines deployed by a given customer.
     * 
     * @param   customerId      the id of the customer who owns the virtual
     *                          machines to be counted.
     * @return                  the number of virtual machines deployed by
     *                          the customer.
     * @see                     CustomerRegistry
     * @see                     VirtualMachineRegistry
     * @since                   1.0
     */
    public int getNumOfVms(long customerId) {
        int numOfVms = 0;
        for(VirtualMachineRegistry vm : getListOfVms(customerId)) {
            numOfVms += vm.getAmount();
        }
        return numOfVms;
    }    
    
    /** 
     * Gets the names of all virtual machines deployed by a given customer.
     * 
     * @param   customerId      the id of the customer who owns the virtual
     *                          machines.
     * @return                  an array containing the names of all virtual
     *                          machines deployed by the customer.
     * @see                     CustomerRegistry
     * @see                     VirtualMachineRegistry
     * @since                   1.0
     */
    public String[] getNamesOfVms(long customerId) {
        int n = getNumOfVms(customerId);
        
        String[] names = new String[n];

        for(int i=0; i<n; i++) {
            names[i]="VM"+i;
        }

        return names;
    }    
    
    /** 
     * Gets the total demand of RAM from the virtual machines deployed by
     * a given customer.
     * 
     * @param   customerId      the id of the customer who owns the virtual
     *                          machines.
     * @return                  the total demand of RAM from all virtual
     *                          machines deployed by the customer.
     * @see                     CustomerRegistry
     * @see                     VirtualMachineRegistry
     * @since                   1.0
     */
    public long getRamDemand(long customerId) {
        long sum = 0;
        for(VirtualMachineRegistry vmr : getListOfVms(customerId)) {
            sum+=LongOperations.saturatedAdd(sum,(vmr.getRam()*vmr.getAmount()));
        }

        return sum;
    }
    
    /** 
     * Gets the total demand of bandwidth from the virtual machines deployed by
     * a given customer.
     * 
     * @param   customerId      the id of the customer who owns the virtual
     *                          machines.
     * @return                  the total demand of bandwidth from all virtual
     *                          machines deployed by the customer.
     * @see                     CustomerRegistry
     * @see                     VirtualMachineRegistry
     * @since                   1.0
     */
    public long getBwDemand(long customerId) {
        long sum = 0;

        for(VirtualMachineRegistry vmr : getListOfVms(customerId)) {
            sum+=LongOperations.saturatedAdd(sum,(vmr.getBw()*vmr.getAmount()));
        }

        return sum;
    }    
    
    /** 
     * Gets the total demand of MIPS from the virtual machines deployed by
     * a given customer.
     * 
     * @param   customerId      the id of the customer who owns the virtual
     *                          machines.
     * @return                  the total demand of MIPS from all virtual
     *                          machines deployed by the customer.
     * @see                     CustomerRegistry
     * @see                     VirtualMachineRegistry
     * @since                   1.0
     */    
    public long getMipsDemand(long customerId) {
        long sum = 0;

        for(VirtualMachineRegistry vmr : getListOfVms(customerId)) {
            String s = Double.toString(vmr.getMips()).split("\\.")[0];
            Long mips = Long.valueOf(s);
            sum+=LongOperations.saturatedAdd(sum,(mips*vmr.getAmount()));
        }

        return sum;
    }
    
    /** 
     * Gets the total demand of storage from the virtual machines deployed by
     * a given customer.
     * 
     * @param   customerId      the id of the customer who owns the virtual
     *                          machines.
     * @return                  the total demand of storage from all virtual
     *                          machines deployed by the customer.
     * @see                     CustomerRegistry
     * @see                     VirtualMachineRegistry
     * @since                   1.0
     */     
    public long getStorageDemand(long customerId) {
        long sum = 0;

        for(VirtualMachineRegistry vmr : getListOfVms(customerId)) {
            sum+=LongOperations.saturatedAdd(sum,vmr.getSize()*vmr.getAmount());
        }

        return sum;
    } 
    
    /** 
     * Gets the number of cloudlets created by all customers.
     * 
     * @return                  the total number of cloudlets created by
     *                          all customers.
     * @see                     CustomerRegistry
     * @see                     UtilizationProfile
     * @since                   1.0
     */      
    public long getTotalNumOfCloudlets() {
        long numOfCloudlets = 0;

        for(CustomerRegistry c : getListOfCustomers()) {
            numOfCloudlets+=c.getUtilizationProfile().getNumOfCloudlets();
        }
        
        return numOfCloudlets;
    }    
    
    /** 
     * Gets the total number of virtual machines deployed by all customers.
     * 
     * @return                  the total number of virtual machines deployed by
     *                          all customers.
     * @see                     CustomerRegistry
     * @see                     UtilizationProfile
     * @since                   1.0
     */ 
    public long getTotalNumOfVms() {
        long numOfVms = 0;

        for(CustomerRegistry c : getListOfCustomers()) {
            numOfVms=LongOperations.saturatedAdd(numOfVms,new CustomerRegistryDAO().getNumOfVms(c.getId()));
        }
                
        return numOfVms;
    }
    
    /** 
     * Gets the average length of cloudlets created by all customers.
     * 
     * @return                  the average length of cloudlets created by
     *                          all customers.
     * @see                     CustomerRegistry
     * @see                     UtilizationProfile
     * @since                   1.0
     */ 
    public long getAvgLength() {
        int numOfCostumers = getNumOfCustomers();
        long[] avgArray = new long[numOfCostumers];
        long longSum=0;

        int i = 0;
        for(CustomerRegistry c : getListOfCustomers()) {
            avgArray[i] = c.getUtilizationProfile().getLength();
            i++;
        }

        for(i = 0; i<avgArray.length; i++) {
            longSum=LongOperations.saturatedAdd(longSum, avgArray[i]);
        }

        return longSum/numOfCostumers;
    }    
    
    /** 
     * Gets the average file size of cloudlets created by all customers.
     * 
     * @return                  the average file size of cloudlets created by
     *                          all customers.
     * @see                     CustomerRegistry
     * @see                     UtilizationProfile
     * @since                   1.0
     */ 
    public long getAvgFileSize() {
        int numOfCostumers = getNumOfCustomers();
        long[] avgArray = new long[numOfCostumers];
        long longSum=0;

        int i = 0;
        for(CustomerRegistry c : getListOfCustomers()) {
            avgArray[i] = c.getUtilizationProfile().getFileSize();
            i++;
        }

        for(i = 0; i<avgArray.length; i++) {
            longSum=LongOperations.saturatedAdd(longSum, avgArray[i]);
        }

        return longSum/numOfCostumers;
    }    
    
    /** 
     * Gets the average output size of cloudlets created by all customers.
     * 
     * @return                  the average output size of cloudlets created by
     *                          all customers.
     * @see                     CustomerRegistry
     * @see                     UtilizationProfile
     * @since                   1.0
     */ 
    public long getAvgOutputSize() {
        int numOfCostumers = getNumOfCustomers();
        long[] avgArray = new long[numOfCostumers];
        long longSum=0;

        int i = 0;
        for(CustomerRegistry c : getListOfCustomers()) {
            avgArray[i]= c.getUtilizationProfile().getOutputSize();
            i++;
        }

        for(i = 0; i<avgArray.length; i++) {
            longSum=LongOperations.saturatedAdd(longSum, avgArray[i]);
        }

        return longSum/numOfCostumers;
    }    
    
    /** 
     * Gets the average image size of virtual machines deployed by all 
     * customers.
     * 
     * @return                  the average image size of virtual machines
     *                          deployed by all customers.
     * @see                     CustomerRegistry
     * @see                     VirtualMachineRegistry
     * @since                   1.0
     */ 
    public long getAvgImageSize() {
        long sum = 0;

        for(CustomerRegistry c : getListOfCustomers()) {
            for(VirtualMachineRegistry vmr : c.getVmList()) {
                sum=LongOperations.saturatedAdd(sum,vmr.getSize()*vmr.getAmount());
            }
        }

        return sum/getTotalNumOfVms();
    }    
    
    /** 
     * Gets the average amount of RAM requested by all virtual machines
     * deployed by all customers.
     * 
     * @return                  the average amount of RAM requested by all
     *                          virtual machines.
     * @see                     CustomerRegistry
     * @see                     UtilizationProfile
     * @since                   1.0
     */ 
    public long getAvgRAM() {
        long sum = 0;

        for(CustomerRegistry c : getListOfCustomers()) {
            for(VirtualMachineRegistry vmr : c.getVmList()) {
                sum=LongOperations.saturatedAdd(sum,vmr.getRam()*vmr.getAmount());
            }
        }

        return sum/getTotalNumOfVms();
    }    
    
    /** 
     * Gets the average amount of bandwidth requested by all virtual machines
     * deployed by all customers.
     * 
     * @return                  the average amount of bandwidth requested by all
     *                          virtual machines.
     * @see                     CustomerRegistry
     * @see                     UtilizationProfile
     * @since                   1.0
     */ 
    public long getAvgBw() {
        long sum = 0;

        for(CustomerRegistry c : getListOfCustomers()) {
            for(VirtualMachineRegistry vmr : c.getVmList()) {
                sum=LongOperations.saturatedAdd(sum,vmr.getBw()*vmr.getAmount());
            }
        }

        return sum/getTotalNumOfVms();
    }
    
}
