package com.fusesource.forge.jmstest.config;

public enum DeliveryMode {
    
	NON_PERSISTENT(javax.jms.DeliveryMode.NON_PERSISTENT),
    PERSISTENT(javax.jms.DeliveryMode.PERSISTENT);

    private int code;

    DeliveryMode(int code) {
       this.code = code;
    }

    public int getCode() {
        return code;
    }
}
