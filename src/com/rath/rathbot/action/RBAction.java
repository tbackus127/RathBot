
package com.rath.rathbot.action;

import java.time.Instant;

/**
 * This class acts as the skeleton of log-supported actions the bot may take.
 * 
 * @author Tim Backus tbackus127@gmail.com
 */
public abstract class RBAction {
  
  /** The Instant the action was taken. */
  protected final Instant timestamp;
  
  /**
   * Default constructor.
   * 
   * @param timestamp the Instant the action was taken.
   */
  protected RBAction(final Instant timestamp) {
    this.timestamp = timestamp;
  }
  
  /**
   * Gets a String representation of all of the action's parameters.
   * 
   * @return a String that will be written to the action log.
   */
  public abstract String getActionMessage();
}
