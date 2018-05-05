
package com.rath.rathbot.action;

import java.time.Instant;

import sx.blah.discord.handle.obj.IUser;

/**
 * This class contains information on bans performed by RathBot.
 * 
 * @author Tim Backus tbackus127@gmail.com
 */
public class ActionBan extends RBDiscAction {
  
  /**
   * Default constructor.
   * 
   * @param timestamp the timestamp the user was banned as an Instant.
   * @param issuer the IUser that issued the command.
   * @param bannedUser the IUser that was banned.
   */
  public ActionBan(final Instant timestamp, final IUser issuer, final IUser bannedUser) {
    super(timestamp, issuer, bannedUser);
  }
  
  @Override
  public final String getActionMessage() {
    return this.timestamp + ": Banned " + this.infringingUser.getName() + ".";
  }
  
}
