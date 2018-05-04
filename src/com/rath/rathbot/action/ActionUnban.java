
package com.rath.rathbot.action;

import java.time.Instant;

import sx.blah.discord.handle.obj.IUser;

/**
 * This class contains information on unbans performed by RathBot.
 * 
 * @author Tim Backus tbackus127@gmail.com
 */
public class ActionUnban extends RBDiscAction {
  
  /**
   * Default constructor.
   * 
   * @param timestamp the timestamp the user was unbanned as an Instant.
   * @param issuer the IUser that issued the command.
   * @param unbannedUser the IUser that was unbanned.
   */
  public ActionUnban(final Instant timestamp, final IUser issuer, final IUser unbannedUser) {
    super(timestamp, issuer, unbannedUser);
  }
  
  @Override
  public final String getActionMessage() {
    return this.timestamp + ": Unbanned " + this.infringingUser.getName() + ".";
  }
  
}
