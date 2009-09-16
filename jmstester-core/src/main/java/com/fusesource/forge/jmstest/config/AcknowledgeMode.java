package com.fusesource.forge.jmstest.config;

import javax.jms.Session;

/**
 * @author   andreasgies
 */
public enum AcknowledgeMode {
    AUTO_ACKNOWLEDGE(Session.AUTO_ACKNOWLEDGE),
    CLIENT_ACKNOWLEDGE(Session.CLIENT_ACKNOWLEDGE),
    DUPS_OK_ACKNOWLEDGE(Session.DUPS_OK_ACKNOWLEDGE),
    SESSION_TRANSACTED(Session.SESSION_TRANSACTED);
    private int code;

    AcknowledgeMode(int code) {
       this.code = code;
   }

    public int getCode() {
        return code;
    }
}
