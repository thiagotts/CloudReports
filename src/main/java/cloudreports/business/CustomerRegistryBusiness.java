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

package cloudreports.business;

import cloudreports.dao.CustomerRegistryDAO;
import cloudreports.models.CustomerRegistry;
import java.util.List;

/**
 * Provides a set of static high level methods related to the 
 * {@link CustomerRegistry} class.
 * 
 * @author      Thiago T. Sá
 * @since       1.1
 */
public class CustomerRegistryBusiness {
    
    private static CustomerRegistryDAO crDAO = new CustomerRegistryDAO();
    
    public static List<CustomerRegistry> getListOfCustomers() {
        return crDAO.getListOfCustomers();
    }
    
}
