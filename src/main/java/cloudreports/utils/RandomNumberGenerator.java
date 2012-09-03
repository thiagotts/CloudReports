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

package cloudreports.utils;

import cloudreports.business.SettingBusiness;
import cloudreports.dao.SettingDAO;
import cloudreports.enums.RandomNumbersFactory;
import java.util.List;

/**
 * A helper class that provides utility methods related to random numbers
 * 
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class RandomNumberGenerator {
    
    /** 
     * Gets a specific amount of random numbers from the random numbers pool.
     * 
     * @param   amount  the amount of random numbers to be retrieved.
     * @since           1.0
     */       
    public static List<Double> getRandomNumbers(int amount) {
        int source = SettingBusiness.getRandomnessOption();
        return RandomNumbersFactory.getInstance(source).getRandomNumbers(amount);
    }
    
}
