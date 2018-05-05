
package com.rath.rathbot.disc;

/**
 * This class holds the data for individual infractions a user has made.
 * 
 * @author Tim Backus tbackus127@gmail.com
 *
 */
public class InfractionEntry {
  
  /** The type of punishment the user received. */
  private final PunishmentType type;
  
  /** The time at which the infraction was issued. */
  private final long timestamp;
  
  /** The reason, if any, that was given by the staff. */
  private final String reason;
  
  /**
   * Default Constructor.
   * 
   * @param t the type of punishment the user received.
   * @param time the time at which the infraction was issued.
   * @param reason the reason given by the staff, null if no reason.
   */
  public InfractionEntry(final PunishmentType t, final long time, final String reason) {
    this.type = t;
    this.timestamp = time;
    this.reason = (reason == null) ? "No reason given." : reason;
  }
  
  /**
   * Gets the type of punishment.
   * 
   * @return the enum value from PunishmentType.
   */
  public PunishmentType getType() {
    return type;
  }
  
  /**
   * Gets the time the infraction was made.
   * 
   * @return a long.
   */
  public final long getTimestamp() {
    return this.timestamp;
  }
  
  /**
   * Gets the reason the staff provided for the infraction.
   * 
   * @return a String.
   */
  public final String getReason() {
    return this.reason;
  }
  
}
