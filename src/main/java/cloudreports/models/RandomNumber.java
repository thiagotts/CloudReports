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

package cloudreports.models;

/**
 * Represents a random number in the random number's pool.
 * 
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class RandomNumber {
    
    /** The id of the number. */
    private long id;
    
    /** The value. */
    private double value;

    /** The default constructor. */
    public RandomNumber() {
    }

    /** 
     * Creates a random number registry with the given value.
     * 
     * @param   value   the random number's value.
     * @since           1.0
     */    
    public RandomNumber(double value) {
        this.value = value;
    }    
    
    /**
     * Gets the random number's id.
     * 
     * @return the random number's id.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the random number's id.
     * 
     * @param   id  the random number's id.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the random number's value.
     * 
     * @return the random number's value.
     */
    public double getValue() {
        return value;
    }

    /**
     * Sets the random number's value.
     * 
     * @param   value   the random number's value.
     */
    public void setValue(double value) {
        this.value = value;
    }
    
}
