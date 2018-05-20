
package com.rath.rathbot.action;

import java.time.Instant;

import sx.blah.discord.handle.obj.IUser;

/**
 * 
 * @author Nathan Lehenbauer lehenbnw@gmail.com
 *
 */
public class ActionReport extends RBDiscAction {
  
  /**
   * Default constructor.
   * 
   * @param timestamp the time this action was taken as an Instant.
   * @param issuedUser the user who made the report.
   * @param infringingUser the user who the report was made against.
   */
  public ActionReport(final Instant timestamp, final IUser issuedUser, final IUser infringingUser) {
    super(timestamp, issuedUser, infringingUser);
  }
  
  @Override
  public String getActionMessage() {
    return this.timestamp + ": " + this.getIssuer().getName() + " reported " + this.infringingUser.getName() + ".";
  }
}