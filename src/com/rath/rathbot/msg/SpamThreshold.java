
package com.rath.rathbot.msg;

import java.util.InputMismatchException;

/**
 * This class acts as a struct for anti-spam triggering metrics.
 * 
 * @author Tim Backus tbackus127@gmail.com
 *
 */
public class SpamThreshold {
  
  /** The number of messages to trigger. */
  private final int count;
  
  /** The amount of time in seconds that must not have passed to trigger. */
  private final int timeout;
  
  /**
   * Default constructor.
   * 
   * @param count the number of messages to trigger. Must be positive.
   * @param timeout the amount of time in seconds that must not have passed to trigger. Must be positive.
   */
  public SpamThreshold(final int count, final int timeout) {
    
    if (count < 0 || timeout < 0) {
      throw new InputMismatchException("Count/timeout must be positive!");
    }
    
    this.count = count;
    this.timeout = timeout;
  }
  
  /**
   * Gets the number of messages to trigger.
   * 
   * @return a positive int.
   */
  public int getCount() {
    return count;
  }
  
  /**
   * Gets the amount of time in seconds that must not have passed to trigger.
   * 
   * @return a positive int.
   */
  public int getTimeout() {
    return timeout;
  }
}
