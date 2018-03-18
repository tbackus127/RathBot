
package com.rath.rathbot.msg;

/**
 * This class acts as a struct for a user's message history.
 * 
 * @author Tim Backus tbackus127@gmail.com
 *
 */
public class MessageTrackerEntry {
  
  /** The time this message was sent as Epoch time. */
  private final long time;
  
  /** The message content. */
  private final String message;
  
  /**
   * Default constructor.
   * 
   * @param time the time this message was sent.
   * @param message the message content.
   */
  public MessageTrackerEntry(final String message, final long time) {
    this.message = message;
    this.time = time;
  }
  
  /**
   * Gets the time this message was sent as Epoch time.
   * 
   * @return a positive long.
   */
  public long getTime() {
    return time;
  }
  
  /**
   * Gets the message contents.
   * 
   * @return a String.
   */
  public String getMessage() {
    return message;
  }
}
