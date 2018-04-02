
package com.rath.rathbot.msg;

/**
 * This enum contains the types of spam the bot checks for.
 * 
 * @author Tim Backus tbackus127@gmail.com
 *
 */
public enum SpamTrigger {
  
  /** If spam protection triggered from a high message rate. */
  MESSAGE_RATE(AntiSpam.REASON_MSG_RATE_ABUSE),
  
  /** If spam protection triggered from repeated messages. */
  REPEAT_MESSAGES(AntiSpam.REASON_REPEAT_MSG_ABUSE);
  
  /** The reason as a String. */
  private final String reason;
  
  /**
   * Internal constructor.
   * 
   * @param reason the reason as a String.
   */
  private SpamTrigger(final String reason) {
    this.reason = reason;
  }
  
  /**
   * Gets the String form of the reason.
   * 
   * @return a String.
   */
  public String getReason() {
    return this.reason;
  }
}
