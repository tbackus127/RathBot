
package com.rath.rathbot.disc;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class holds a user's entire infraction history.
 * 
 * @author Tim Backus tbackus127@gmail.com
 */
public class InfractionData implements Serializable {
  
  /** Default serial version UID. */
  private static final long serialVersionUID = 1L;
  
  /** How many times the user has been warned. */
  private int warnCount;
  
  /** How many times the user has been muted. */
  private int muteCount;
  
  /** How many times the user has been kicked. */
  private int kickCount;
  
  /** How many times the user has been banned. */
  private int banCount;
  
  /** If the user is currently muted. */
  private boolean isMuted;
  
  /** If the user is currently banned. */
  private boolean isBanned;
  
  /** The user's infraction history, with the most recent entry being index 0 of the list. */
  private final ArrayList<InfractionEntry> history;
  
  /**
   * Default constructor.
   */
  public InfractionData() {
    this.warnCount = 0;
    this.muteCount = 0;
    this.kickCount = 0;
    this.banCount = 0;
    this.history = new ArrayList<InfractionEntry>();
  }
  
  /**
   * Gets how many times the user has been warned.
   * 
   * @return an int.
   */
  public int getWarnCount() {
    return this.warnCount;
  }
  
  /**
   * Gets how many times the user has been muted.
   * 
   * @return an int.
   */
  public int getMuteCount() {
    return this.muteCount;
  }
  
  /**
   * Gets how many times the user has been kicked.
   * 
   * @return an int.
   */
  public int getKickCount() {
    return this.kickCount;
  }
  
  /**
   * Gets how many times the user has been banned.
   * 
   * @return an int.
   */
  public int getBanCount() {
    return this.banCount;
  }
  
  /**
   * Gets the user's infraction history.
   * 
   * @return an ArrayList of InfractionEntry's.
   */
  public ArrayList<InfractionEntry> getHistory() {
    return this.history;
  }
  
  /**
   * If the user is muted.
   * 
   * @return a boolean.
   */
  public boolean isMuted() {
    return isMuted;
  }
  
  /**
   * If the user is banned.
   * 
   * @return a boolean.
   */
  public boolean isBanned() {
    return isBanned;
  }
}
