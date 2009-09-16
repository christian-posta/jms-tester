/**
 *  Copyright (C) 2009 Progress Software, Inc. All rights reserved.
 *  http://fusesource.com
 *
 *  The software in this package is published under the terms of the AGPL license
 *  a copy of which has been included with this distribution in the license.txt file.
 */
package com.fusesource.forge.jmstest.benchmark;

public class BenchmarkConfigurationException extends RuntimeException {

	private static final long serialVersionUID = 5077556607340469713L;

	public BenchmarkConfigurationException() {
        super();
    }

    public BenchmarkConfigurationException(String message) {
        super(message);
    }

    public BenchmarkConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public BenchmarkConfigurationException(Throwable cause) {
        super(cause);
    }
}
