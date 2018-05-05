
package com.rath.rathbot.action;

import java.time.Instant;

import sx.blah.discord.handle.obj.IUser;

/**
 * This class contains information on kicks performed by RathBot.
 * 
 * @author Tim Backus tbackus127@gmail.com
 */
public class ActionKick extends RBDiscAction {
  
  /**
   * Default constructor.
   * 
   * @param timestamp the timestamp the user was kicked as an Instant.
   * @param issuer the IUser that issued the command.
   * @param kickedUser the IUser that was kicked.
   */
  public ActionKick(final Instant timestamp, final IUser issuer, final IUser kickedUser) {
    super(timestamp, issuer, kickedUser);
  }
  
  @Override
  public final String getActionMessage() {
    return this.timestamp + ": Kicked " + this.infringingUser.getName() + ".";
  }
  
}
