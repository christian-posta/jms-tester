/**
 *  Copyright (C) 2008 Progress Software, Inc. All rights reserved.
 *  http://fusesource.com
 *
 *  The software in this package is published under the terms of the AGPL license
 *  a copy of which has been included with this distribution in the license.txt file.
 */
package com.fusesource.forge.jmstest.benchmark;


public class BenchmarkExecutionException extends RuntimeException {

	private static final long serialVersionUID = -2508448601770009818L;

	public BenchmarkExecutionException() {
        super();
    }

    public BenchmarkExecutionException(String message) {
        super(message);
    }

    public BenchmarkExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public BenchmarkExecutionException(Throwable cause) {
        super(cause);
    }
}
