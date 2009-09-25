/*
 * Copyright (C) 2009, Progress Software Corporation and/or its
 * subsidiaries or affiliates.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fusesource.forge.jmstest.threading;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractSteppable implements Steppable {
  
  private String name = null;
  private SteppablePool pool = null;
  private SteppableState state = SteppableState.CREATED;
  
  private Log log = null;
  
  public AbstractSteppable(String name) {
    this.name = name;
  }
  
  public String getName() {
    return this.name;
  }

  public void setPool(SteppablePool p) {
    this.pool = p;
  }
  
  protected void doInitialize() {
  }
  
  final public void initialize() {
    if (state == SteppableState.CREATED) {
      log().debug("Initializing " + getClass().getSimpleName() + "(" + getName() + ")");
      doInitialize();
      state = SteppableState.INITIALIZED;
      pool.getExecutor().submit(new Runnable() {
        public void run() {
          start();
        }
      });
      pool.touch();
    } else {
      log().debug("Call to initialize ignored for " + getClass().getSimpleName() + "(" + getName() + ")");
    }
  }
  
  protected void doAbort() {
  }

  final public void abort() {
    if (state == SteppableState.RUNNING || state == SteppableState.INITIALIZED) {
      log().debug("Aborting " + getClass().getSimpleName() + "(" + getName() + ")");
      doAbort();
      state = SteppableState.ABORTED;
      pool.getExecutor().submit(new Runnable() {
        public void run() {
          terminate();
        }
      });
      pool.touch();
    } else {
      log().debug("Call to abort ignored for " + getClass().getSimpleName() + "(" + getName() + ")");
    }
  }

  protected void doTerminate() {
  }

  final public void terminate() {
    if (state == SteppableState.RUNNING || state == SteppableState.FAILED || state == SteppableState.ABORTED) {
      log().debug("Terminating " + getClass().getSimpleName() + "(" + getName() + ")");
      doTerminate();
      state = SteppableState.FINISHED;
      pool.getExecutor().submit(new Runnable() {
        public void run() {
          release();
        }
      });
      pool.touch();
    } else {
      log().debug("Call to terminate ignored for " + getClass().getSimpleName() + "(" + getName() + ")");
    }
  }

  protected void doPause() {
  }

  final public void pause() {
    if (state == SteppableState.RUNNING) {
      log().debug("Pausing " + getClass().getSimpleName() + "(" + getName() + ")");
      doPause();
      state = SteppableState.INITIALIZED;
      pool.touch();
    } else {
      log().debug("Call to pause ignored for " + getClass().getSimpleName() + "(" + getName() + ")");
    }
  }

  protected void doRelease() {
  }

  public void release() {
    if (state == SteppableState.FINISHED) {
      log().debug("Releasing " + getClass().getSimpleName() + "(" + getName() + ")");
      doRelease();
      state = SteppableState.RELEASED;
      pool.touch();
    } else {
      log().debug("Call to release ignored for " + getClass().getSimpleName() + "(" + getName() + ")");
    }
  }

  protected void doStart() {
  }

  public void start() {
    if (state == SteppableState.INITIALIZED) {
      log().debug("Starting " + getClass().getSimpleName() + "(" + getName() + ")");
      doStart();
      state = SteppableState.RUNNING;
      pool.getExecutor().submit(new Runnable() {
        public void run() {
          step();
        }
      });
      pool.touch();
    } else {
      log().debug("Call to start ignored for " + getClass().getSimpleName() + "(" + getName() + ")");
    }
  }
  
  protected void doStep() {
  }

  protected abstract boolean needsMoreSteps();
  
  final public void step() {
    if (state == SteppableState.RUNNING) {
      log().debug("Running " + getClass().getSimpleName() + "(" + getName() + ")");
      doStep();
      pool.getExecutor().submit(new Runnable() {
        public void run() {
          if (needsMoreSteps()) {
            step();
          } else {
            terminate();
          }
        }
      });
      state = SteppableState.RUNNING;
      pool.touch();
    } else {
      log().debug("Call to step ignored for " + getClass().getSimpleName() + "(" + getName() + ")");
    }
  }
  
  final public SteppableState getState() {
    return state;
  }

  private Log log() {
    if (log == null) {
      log = LogFactory.getLog(this.getClass());
    }
    return log;
  }
}
