
package com.rath.rathbot.action;

import java.time.Instant;

import sx.blah.discord.handle.obj.IUser;

/**
 * This class contains information on mutes performed by RathBot.
 * 
 * @author Tim Backus tbackus127@gmail.com
 */
public class ActionMute extends RBDiscAction {
  
  /**
   * Default constructor.
   * 
   * @param timestamp the timestamp the user was muted as an Instant.
   * @param issuer the IUser that issued the command.
   * @param mutedUser the IUser that was muted.
   */
  public ActionMute(final Instant timestamp, final IUser issuer, final IUser mutedUser) {
    super(timestamp, issuer, mutedUser);
  }
  
  @Override
  public final String getActionMessage() {
    return this.timestamp + ": Muted " + this.infringingUser.getName() + ".";
  }
  
}
