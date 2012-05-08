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

import java.math.BigInteger;

/**
 * A helper class that provides utility methods related to <code>long</code> 
 * operations.
 * 
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class LongOperations {
    
    /** The {@link Long} minimum value. */
    final static BigInteger bigMin = BigInteger.valueOf(Long.MIN_VALUE);
    
    /** The {@link Long} maximum value. */
    final static BigInteger bigMax = BigInteger.valueOf(Long.MAX_VALUE);

    /** 
     * Adds two long values.
     * 
     * @param   x   the first term.
     * @param   y   the second term.
     * @since       1.0
     */        
    static public long saturatedAdd(long x, long y) {
        BigInteger sum = BigInteger.valueOf(x).add(BigInteger.valueOf(y));
        return bigMin.max(sum).min(bigMax).longValue();
    }

    /** 
     * Multiplies two long values.
     * 
     * @param   x   the first factor.
     * @param   y   the second factor.
     * @since       1.0
     */       
    static public long saturatedMultiply(long x, long y) {
        BigInteger mult = BigInteger.valueOf(x).multiply(BigInteger.valueOf(y));
        return bigMin.max(mult).min(bigMax).longValue();
    }

}
