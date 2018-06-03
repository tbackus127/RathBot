
package com.rath.rathbot.task.util.remindme;

import sx.blah.discord.handle.obj.IUser;

/**
 * This class contains data for remindme command entries.
 * 
 * @author Tim Backus tbackus127@gmail.com
 */
public class ReminderEntry implements Comparable<ReminderEntry> {
  
  /** The member that will receive a direct message at the reminder time. */
  private final IUser author;
  
  /** The message the user will receive when the reminder time has been met. */
  private final String message;
  
  /** The time in seconds since 1/1/1970 that the user will receive the message. */
  private final long remEpochSecond;
  
  /**
   * Constructs a new ReminderEntry object with the given parameters.
   * 
   * @param author the IUser who issued the command.
   * @param message the message the author will receive as a String.
   * @param remEpochSecond the time the author will receive the direct message.
   */
  public ReminderEntry(final IUser author, final String message, final long remEpochSecond) {
    this.author = author;
    this.message = message;
    this.remEpochSecond = remEpochSecond;
  }
  
  /**
   * Gets the author of the remindme command.
   * 
   * @return the author as an IUser.
   */
  public final IUser getAuthor() {
    return this.author;
  }
  
  /**
   * Gets the message that will be sent.
   * 
   * @return the message as a String.
   */
  public final String getMessage() {
    return this.message;
  }
  
  /**
   * Gets the time the message will be sent in seconds as of 1/1/1970.
   * 
   * @return the epoch second as a long.
   */
  public final long getRemEpochSecond() {
    return this.remEpochSecond;
  }
  
  @Override
  public int compareTo(ReminderEntry o) {
    return (this.getRemEpochSecond() > o.getRemEpochSecond()) ? 1
        : (this.getRemEpochSecond() == o.getRemEpochSecond()) ? 0 : -1;
  }
  
}
