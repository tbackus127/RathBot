
package com.rath.rathbot.action;

import java.time.Instant;

import sx.blah.discord.handle.obj.IUser;

/**
 * This class contains information on warns performed by RathBot.
 * 
 * @author Tim Backus tbackus127@gmail.com
 */
public class ActionWarn extends RBDiscAction {
  
  /**
   * Default constructor.
   * 
   * @param timestamp the timestamp the user was warned as an Instant.
   * @param issuer the IUser that issued the command.
   * @param warnedUser the IUser that was warned.
   */
  public ActionWarn(final Instant timestamp, final IUser issuer, final IUser warnedUser) {
    super(timestamp, issuer, warnedUser);
  }
  
  @Override
  public final String getActionMessage() {
    return this.timestamp + ": Warned " + this.infringingUser.getName() + ".";
  }
  
}
