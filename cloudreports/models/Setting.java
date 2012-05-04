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
 * Represents a generic CloudReports setting.
 * 
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class Setting {
    
    /** The id of the setting. */
    private long id;
    
    /** The name of the setting. */
    private String name;
    
    /** The value of the setting. */
    private String value;

    /** The default constructor. */
    public Setting() {}

    /** 
     * Creates a new setting with the given name and value. 
     * 
     * @param   name    the name of the setting.
     * @param   value   the value of the setting.
     * @since           1.0
     */    
    public Setting(String name, String value) {
        this.name = name;
        this.value = value;
    }    
    
    /**
     * Gets the setting's id.
     * 
     * @return the setting's id.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the setting's id.
     * 
     * @param   id  the setting's id.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the setting's name.
     * 
     * @return the setting's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the setting's name.
     * 
     * @param   name    the setting's name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the setting's value.
     * 
     * @return the setting's value.
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the setting's value.
     * 
     * @param   value   the setting's value.
     */
    public void setValue(String value) {
        this.value = value;
    }    
}
