/**
 *  Copyright (C) 2008 Progress Software, Inc. All rights reserved.
 *  http://fusesource.com
 *
 *  The software in this package is published under the terms of the AGPL license
 *  a copy of which has been included with this distribution in the license.txt file.
 */
package com.fusesource.forge.jmstest.config;

import java.math.BigDecimal;

public enum MeasureTimeUnit {

	NANOSECONDS(0),
	MICROSECONDS(1),
	MILLISECONDS(2),
	SECONDS(3),
    MINUTES(4),
    HOURS(5),
    DAYS(6);

    public static final BigDecimal [] DIVISORS = { 
    	new BigDecimal(1), new BigDecimal(1000), new BigDecimal(1000), new BigDecimal(1000), 
    	new BigDecimal(60), new BigDecimal(60), new BigDecimal(24)
    };

    public static BigDecimal getDivisor(MeasureTimeUnit unit) {
    	BigDecimal result = DIVISORS[0];
    	for(int i=1; i<=unit.getCode(); i++) {
    		result.multiply(DIVISORS[i]);
    	}
    	return result;
    }
    
    private int code;

    MeasureTimeUnit(int code) {
       this.code = code;
   }

    public int getCode() {
        return code;
    }

}
