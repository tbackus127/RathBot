
package com.rath.rathbot;

public class DBG {
  
  /** Whether or not to print debug messages. */
  public static final boolean DEBUG_OUT = true;
  
  public static final void pl(final String msg) {
    if (DEBUG_OUT) {
      final long time = System.currentTimeMillis();
      System.out.println("@" + time + "ms:" + msg);
    }
  }
  
}
