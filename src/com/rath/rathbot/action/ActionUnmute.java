
package com.rath.rathbot.action;

import java.time.Instant;

import sx.blah.discord.handle.obj.IUser;

/**
 * This class contains information on unmutes performed by RathBot.
 * 
 * @author Tim Backus tbackus127@gmail.com
 */
public class ActionUnmute extends RBDiscAction {
  
  /**
   * Default constructor.
   * 
   * @param timestamp the timestamp the user was unmuted as an Instant.
   * @param issuer the IUser that issued the command.
   * @param unmutedUser the IUser that was unmuted.
   */
  public ActionUnmute(final Instant timestamp, final IUser issuer, final IUser unmutedUser) {
    super(timestamp, issuer, unmutedUser);
  }
  
  @Override
  public final String getActionMessage() {
    return this.timestamp + ": Unmuted " + this.infringingUser.getName() + ".";
  }
  
}
